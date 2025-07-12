<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElButton, ElSelect, ElOption, ElDialog, ElMessage, ElLoading } from 'element-plus'
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import {
  getCourseKnowledgeGraphApi,
  generateKnowledgeGraphApi,
  type KnowledgeGraphVO,
  type KnowledgeGraphGenerationDTO
} from '@/api/knowledge'
import { getCoursesApi, type CourseVO } from '@/api/course'
import * as echarts from 'echarts'

defineOptions({
  name: 'KnowledgeGraph'
})

const route = useRoute()

const loading = ref(false)
const generateLoading = ref(false)
const courseLoading = ref(false)
const knowledgeGraph = ref<KnowledgeGraphVO>()
const courses = ref<CourseVO[]>([])
const selectedCourseId = ref<number>(Number(route.params.courseId) || 0)

const graphContainer = ref<HTMLDivElement>()
const generateDialogVisible = ref(false)

const generateForm = ref<KnowledgeGraphGenerationDTO>({
  courseId: 0,
  analysisDepth: 'detailed',
  includeResources: true,
  includeAssignments: true,
  includeQuizzes: true,
  customPrompt: ''
})

const fetchCourses = async () => {
  courseLoading.value = true
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
      if (!selectedCourseId.value && courses.value.length > 0) {
        selectedCourseId.value = courses.value[0].id
      }
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    courseLoading.value = false
  }
}

const fetchKnowledgeGraph = async () => {
  if (!selectedCourseId.value) return

  loading.value = true
  try {
    const res = await getCourseKnowledgeGraphApi(selectedCourseId.value)
    if (res.data) {
      knowledgeGraph.value = res.data
      await nextTick()
      renderKnowledgeGraph()
    }
  } catch (error) {
    ElMessage.error('获取知识图谱失败')
  } finally {
    loading.value = false
  }
}

const handleCourseChange = () => {
  if (selectedCourseId.value) {
    fetchKnowledgeGraph()
  }
}

const handleGenerate = () => {
  generateForm.value = {
    courseId: selectedCourseId.value,
    analysisDepth: 'detailed',
    includeResources: true,
    includeAssignments: true,
    includeQuizzes: true,
    customPrompt: ''
  }
  generateDialogVisible.value = true
}

const submitGenerate = async () => {
  if (!generateForm.value.courseId) {
    ElMessage.error('请选择课程')
    return
  }

  generateLoading.value = true
  try {
    const res = await generateKnowledgeGraphApi(generateForm.value.courseId, generateForm.value)
    if (res.data) {
      knowledgeGraph.value = res.data
      generateDialogVisible.value = false
      ElMessage.success('生成知识图谱成功')
      await nextTick()
      renderKnowledgeGraph()
    }
  } catch (error) {
    ElMessage.error('生成知识图谱失败')
  } finally {
    generateLoading.value = false
  }
}

const renderKnowledgeGraph = () => {
  if (!graphContainer.value || !knowledgeGraph.value) return

  const chart = echarts.init(graphContainer.value)

  // 准备节点数据
  const nodes = knowledgeGraph.value.nodes.map((node) => ({
    id: node.id,
    name: node.name,
    value: node.importance,
    category: node.category,
    symbolSize: Math.max(20, node.importance * 50),
    itemStyle: {
      color: getNodeColor(node.category)
    },
    label: {
      show: true,
      position: 'right',
      fontSize: 12
    }
  }))

  // 准备边数据
  const edges = knowledgeGraph.value.edges.map((edge) => ({
    source: edge.source,
    target: edge.target,
    value: edge.strength,
    lineStyle: {
      width: Math.max(1, edge.strength * 5),
      color: getEdgeColor(edge.relationship)
    },
    label: {
      show: false,
      formatter: edge.relationship
    }
  }))

  // 准备分类数据
  const categories = Array.from(
    new Set(knowledgeGraph.value.nodes.map((node) => node.category))
  ).map((category) => ({
    name: category,
    itemStyle: {
      color: getNodeColor(category)
    }
  }))

  const option = {
    title: {
      text: `${knowledgeGraph.value.courseName} - 知识图谱`,
      subtext: `节点: ${knowledgeGraph.value.metadata.totalNodes} | 边: ${knowledgeGraph.value.metadata.totalEdges}`,
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params: any) => {
        if (params.dataType === 'node') {
          const node = knowledgeGraph.value?.nodes.find((n) => n.id === params.data.id)
          return `<div>
            <strong>${node?.name}</strong><br/>
            类别: ${node?.category}<br/>
            重要度: ${node?.importance}<br/>
            难度: ${node?.difficulty}<br/>
            描述: ${node?.description}
          </div>`
        } else if (params.dataType === 'edge') {
          const edge = knowledgeGraph.value?.edges.find(
            (e) => e.source === params.data.source && e.target === params.data.target
          )
          return `<div>
            <strong>${edge?.relationship}</strong><br/>
            强度: ${edge?.strength}<br/>
            描述: ${edge?.description}
          </div>`
        }
        return ''
      }
    },
    legend: {
      data: categories.map((c) => c.name),
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '知识图谱',
        type: 'graph',
        layout: 'force',
        data: nodes,
        links: edges,
        categories: categories,
        roam: true,
        focusNodeAdjacency: true,
        force: {
          repulsion: 1000,
          edgeLength: [50, 200]
        },
        lineStyle: {
          color: 'source',
          curveness: 0.3
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 10
          }
        }
      }
    ]
  }

  chart.setOption(option)

  // 添加点击事件
  chart.on('click', (params) => {
    if (params.dataType === 'node') {
      const node = knowledgeGraph.value?.nodes.find((n) => n.id === params.data.id)
      if (node) {
        ElMessage.info(`知识点: ${node.name}`)
      }
    }
  })
}

const getNodeColor = (category: string) => {
  const colorMap: Record<string, string> = {
    basic: '#409EFF',
    intermediate: '#67C23A',
    advanced: '#E6A23C',
    expert: '#F56C6C',
    concept: '#909399',
    skill: '#13C2C2',
    application: '#722ED1'
  }
  return colorMap[category] || '#409EFF'
}

const getEdgeColor = (relationship: string) => {
  const colorMap: Record<string, string> = {
    prerequisite: '#F56C6C',
    related: '#67C23A',
    similar: '#409EFF',
    extends: '#E6A23C',
    depends: '#909399'
  }
  return colorMap[relationship] || '#409EFF'
}

const getAnalysisDepthText = (depth: string) => {
  const textMap: Record<string, string> = {
    basic: '基础分析',
    detailed: '详细分析',
    comprehensive: '全面分析'
  }
  return textMap[depth] || depth
}

onMounted(() => {
  fetchCourses()
  if (selectedCourseId.value) {
    fetchKnowledgeGraph()
  }
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-select
            v-model="selectedCourseId"
            placeholder="选择课程"
            style="width: 200px"
            :loading="courseLoading"
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>

        <div class="flex items-center space-x-2">
          <el-button type="primary" @click="handleGenerate"> AI生成知识图谱 </el-button>
          <el-button type="success" @click="fetchKnowledgeGraph"> 刷新 </el-button>
        </div>
      </div>
    </div>

    <ElCard>
      <template #header>
        <div class="flex items-center justify-between">
          <span>知识图谱可视化</span>
          <div v-if="knowledgeGraph" class="text-sm text-gray-500">
            最后更新: {{ knowledgeGraph.metadata.lastUpdated }}
          </div>
        </div>
      </template>

      <div ref="graphContainer" style="height: 600px; width: 100%" v-loading="loading"></div>

      <div v-if="knowledgeGraph" class="mt-20px">
        <div class="grid grid-cols-4 gap-4 text-center">
          <div class="bg-blue-50 p-4 rounded">
            <div class="text-2xl font-bold text-blue-600">{{
              knowledgeGraph.metadata.totalNodes
            }}</div>
            <div class="text-sm text-gray-600">知识点</div>
          </div>
          <div class="bg-green-50 p-4 rounded">
            <div class="text-2xl font-bold text-green-600">{{
              knowledgeGraph.metadata.totalEdges
            }}</div>
            <div class="text-sm text-gray-600">关联关系</div>
          </div>
          <div class="bg-orange-50 p-4 rounded">
            <div class="text-2xl font-bold text-orange-600">{{
              knowledgeGraph.metadata.maxLevel
            }}</div>
            <div class="text-sm text-gray-600">最大层级</div>
          </div>
          <div class="bg-purple-50 p-4 rounded">
            <div class="text-2xl font-bold text-purple-600">{{ knowledgeGraph.nodes.length }}</div>
            <div class="text-sm text-gray-600">可视化节点</div>
          </div>
        </div>
      </div>
    </ElCard>

    <!-- AI生成知识图谱对话框 -->
    <ElDialog v-model="generateDialogVisible" title="AI生成知识图谱" width="600px">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-2">课程</label>
          <el-select v-model="generateForm.courseId" placeholder="请选择课程" class="w-full">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">分析深度</label>
          <el-select v-model="generateForm.analysisDepth" class="w-full">
            <el-option label="基础分析" value="basic" />
            <el-option label="详细分析" value="detailed" />
            <el-option label="全面分析" value="comprehensive" />
          </el-select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">包含内容</label>
          <div class="space-y-2">
            <el-checkbox v-model="generateForm.includeResources"> 包含课程资源 </el-checkbox>
            <el-checkbox v-model="generateForm.includeAssignments"> 包含作业任务 </el-checkbox>
            <el-checkbox v-model="generateForm.includeQuizzes"> 包含测验题目 </el-checkbox>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">自定义提示（可选）</label>
          <el-input
            v-model="generateForm.customPrompt"
            type="textarea"
            :rows="3"
            placeholder="输入自定义分析要求，例如：重点关注某个知识领域，或特定的关联关系类型"
          />
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="generateDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="generateLoading" @click="submitGenerate">
            生成知识图谱
          </el-button>
        </div>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less">
.knowledge-graph-container {
  .graph-stats {
    .stat-item {
      text-align: center;
      padding: 16px;
      border-radius: 8px;

      .stat-value {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
      }
    }
  }
}
</style>
