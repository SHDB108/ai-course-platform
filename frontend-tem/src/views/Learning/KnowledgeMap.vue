<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage, ElSelect, ElOption, ElButton, ElSkeleton } from 'element-plus'
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getCourseKnowledgeGraphApi, type KnowledgeGraphVO } from '@/api/knowledge'
import { getCoursesApi, type CourseVO } from '@/api/course'

defineOptions({
  name: 'KnowledgeMap'
})

const loading = ref(false)
const chartContainer = ref<HTMLElement>()
const knowledgeGraph = ref<KnowledgeGraphVO | null>(null)
const courses = ref<CourseVO[]>([])
const selectedCourseId = ref<number>()
let chartInstance: echarts.ECharts | null = null

const fetchCourses = async () => {
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
      if (courses.value.length > 0) {
        selectedCourseId.value = courses.value[0].id
        await fetchKnowledgeGraph()
      }
    }
  } catch (error) {
    console.error('获取课程失败:', error)
  }
}

const fetchKnowledgeGraph = async () => {
  if (!selectedCourseId.value) return

  loading.value = true
  try {
    console.log('Fetching knowledge graph for course:', selectedCourseId.value)
    const res = await getCourseKnowledgeGraphApi(selectedCourseId.value)
    console.log('API response:', res)
    if (res.data) {
      knowledgeGraph.value = res.data
    }
  } catch (error) {
    console.error('获取知识图谱失败:', error)
    ElMessage.warning('知识图谱暂时不可用，显示示例数据')
    // 显示示例数据
    knowledgeGraph.value = {
      nodes: [
        { id: 1, name: 'Vue基础', type: 'CONCEPT', description: 'Vue框架基础概念' },
        { id: 2, name: '组件系统', type: 'CONCEPT', description: 'Vue组件化开发' },
        { id: 3, name: '响应式原理', type: 'PRINCIPLE', description: 'Vue响应式系统' },
        { id: 4, name: 'Vue Router', type: 'TOOL', description: 'Vue路由管理' }
      ],
      edges: [
        { source: 1, target: 2, relation: 'prerequisite' },
        { source: 1, target: 3, relation: 'prerequisite' },
        { source: 2, target: 4, relation: 'prerequisite' }
      ]
    }
  } finally {
    loading.value = false
    // 在loading完成后延迟渲染，确保ElSkeleton已经切换到实际内容
    await nextTick()
    setTimeout(() => {
      renderGraph()
    }, 200)
  }
}

const renderGraph = async () => {
  console.log('renderGraph called')

  // 等待DOM更新和loading状态变化
  await nextTick()

  console.log('chartContainer.value:', chartContainer.value)
  console.log('knowledgeGraph.value:', knowledgeGraph.value)
  console.log('loading.value:', loading.value)

  if (!chartContainer.value || !knowledgeGraph.value) {
    console.log('Early return: missing container or data')
    // 如果容器还没准备好，稍后重试
    if (knowledgeGraph.value && !loading.value) {
      setTimeout(() => renderGraph(), 100)
    }
    return
  }

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartContainer.value)
  console.log('Chart instance created:', chartInstance)

  const option = {
    title: {
      text: '知识图谱',
      subtext: '展示知识点之间的关联关系',
      top: 20,
      left: 20
    },
    tooltip: {
      formatter: (params: any) => {
        if (params.dataType === 'node') {
          return `<strong>${params.data.name}</strong><br/>
                  类型: ${params.data.type}<br/>
                  描述: ${params.data.description}`
        } else {
          return `关系: ${params.data.relation}`
        }
      }
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: knowledgeGraph.value.nodes.map((node) => ({
          id: node.id.toString(),
          name: node.name,
          type: node.type,
          description: node.description,
          symbolSize: 50,
          itemStyle: {
            color: getNodeColor(node.type)
          }
        })),
        links: knowledgeGraph.value.edges.map((edge) => ({
          source: edge.source.toString(),
          target: edge.target.toString(),
          relation: edge.relation,
          lineStyle: {
            color: '#999',
            width: 2
          }
        })),
        emphasis: {
          focus: 'adjacency'
        },
        roam: true,
        force: {
          repulsion: 200,
          edgeLength: 100
        },
        label: {
          show: true,
          position: 'inside',
          fontSize: 10
        }
      }
    ]
  }

  console.log('Setting chart option:', option)
  chartInstance.setOption(option)
  console.log('Chart option set successfully')
}

const getNodeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    CONCEPT: '#3b82f6',
    PRINCIPLE: '#10b981',
    TOOL: '#f59e0b',
    SKILL: '#ef4444',
    knowledgePoint: '#3b82f6' // 默认知识点颜色
  }
  return colorMap[type] || '#6b7280'
}

const handleCourseChange = () => {
  fetchKnowledgeGraph()
}

onMounted(() => {
  fetchCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">知识图谱</h2>
      <p class="text-sm text-gray-500 mt-1">可视化展示我的知识掌握情况</p>
    </div>

    <ElCard>
      <div class="mb-4 flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <span class="font-medium">选择课程:</span>
          <ElSelect
            v-model="selectedCourseId"
            placeholder="请选择课程"
            @change="handleCourseChange"
            style="width: 200px"
          >
            <ElOption
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </ElSelect>
        </div>
        <ElButton type="primary" @click="fetchKnowledgeGraph" :loading="loading">
          刷新图谱
        </ElButton>
      </div>

      <ElSkeleton :loading="loading" animated>
        <template #template>
          <div class="h-96 bg-gray-100 rounded animate-pulse"></div>
        </template>
        <template #default>
          <div
            ref="chartContainer"
            class="knowledge-graph-chart"
            style="width: 100%; height: 500px"
          ></div>
        </template>
      </ElSkeleton>
    </ElCard>
  </ContentWrap>
</template>
