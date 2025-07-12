<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import {
  ElCard,
  ElRow,
  ElCol,
  ElButton,
  ElSelect,
  ElOption,
  ElDatePicker,
  ElMessage
} from 'element-plus'
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import {
  getCourseScoreTrendApi,
  getCourseStudentScoresApi,
  getTaskCompletionSummaryApi,
  getKnowledgePointPerformanceApi,
  exportCourseScoresApi,
  type TrendPointVO,
  type CourseStudentScoreVO,
  type TaskCompletionSummaryVO,
  type KnowledgePointPerformanceVO,
  type PageVO
} from '@/api/analytics'
import * as echarts from 'echarts'

defineOptions({
  name: 'CourseAnalytics'
})

const route = useRoute()

const loading = ref(false)
const trendLoading = ref(false)
const studentScoresLoading = ref(false)
const taskCompletionLoading = ref(false)
const knowledgePointLoading = ref(false)

const courseId = ref<number>(Number(route.params.courseId))
const trendData = ref<TrendPointVO[]>([])
const studentScores = ref<CourseStudentScoreVO[]>([])
const taskCompletion = ref<TaskCompletionSummaryVO[]>([])
const knowledgePointPerformance = ref<KnowledgePointPerformanceVO[]>([])

const scoreChart = ref<HTMLDivElement>()
const completionChart = ref<HTMLDivElement>()
const knowledgeChart = ref<HTMLDivElement>()

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const filters = ref({
  granularity: 'weekly' as 'daily' | 'weekly' | 'monthly',
  dateRange: [] as string[],
  sortBy: 'score' as 'score' | 'completion' | 'activity'
})

const studentColumns = [
  {
    field: 'studentName',
    label: '学生姓名',
    width: 120
  },
  {
    field: 'studentNo',
    label: '学号',
    width: 150
  },
  {
    field: 'totalScore',
    label: '总分',
    width: 100
  },
  {
    field: 'averageScore',
    label: '平均分',
    width: 100
  },
  {
    field: 'taskCount',
    label: '任务数',
    width: 100
  },
  {
    field: 'completionRate',
    label: '完成率',
    width: 100,
    slots: {
      default: 'completionRate'
    }
  },
  {
    field: 'lastActivityDate',
    label: '最后活动',
    width: 150
  }
]

const taskColumns = [
  {
    field: 'taskTitle',
    label: '任务标题',
    width: 200
  },
  {
    field: 'totalStudents',
    label: '总学生数',
    width: 100
  },
  {
    field: 'submittedCount',
    label: '提交数',
    width: 100
  },
  {
    field: 'completionRate',
    label: '完成率',
    width: 100,
    slots: {
      default: 'completionRate'
    }
  },
  {
    field: 'averageScore',
    label: '平均分',
    width: 100
  },
  {
    field: 'dueDate',
    label: '截止时间',
    width: 150
  }
]

const knowledgeColumns = [
  {
    field: 'knowledgePoint',
    label: '知识点',
    width: 200
  },
  {
    field: 'totalQuestions',
    label: '题目数',
    width: 100
  },
  {
    field: 'correctAnswers',
    label: '正确数',
    width: 100
  },
  {
    field: 'accuracyRate',
    label: '正确率',
    width: 100,
    slots: {
      default: 'accuracyRate'
    }
  },
  {
    field: 'averageScore',
    label: '平均分',
    width: 100
  },
  {
    field: 'difficultyLevel',
    label: '难度',
    width: 100
  }
]

const fetchTrendData = async () => {
  trendLoading.value = true
  try {
    const params: any = {
      granularity: filters.value.granularity
    }

    if (filters.value.dateRange.length === 2) {
      params.startDate = filters.value.dateRange[0]
      params.endDate = filters.value.dateRange[1]
    }

    const res = await getCourseScoreTrendApi(courseId.value, params)
    if (res.data) {
      trendData.value = res.data
      await nextTick()
      renderScoreTrendChart()
    }
  } catch (error) {
    ElMessage.error('获取趋势数据失败')
  } finally {
    trendLoading.value = false
  }
}

const fetchStudentScores = async () => {
  studentScoresLoading.value = true
  try {
    const res = await getCourseStudentScoresApi(courseId.value, {
      current: pagination.value.current,
      size: pagination.value.size,
      sortBy: filters.value.sortBy
    })
    if (res.data) {
      studentScores.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取学生成绩失败')
  } finally {
    studentScoresLoading.value = false
  }
}

const fetchTaskCompletion = async () => {
  taskCompletionLoading.value = true
  try {
    const res = await getTaskCompletionSummaryApi(courseId.value)
    if (res.data) {
      taskCompletion.value = res.data
      await nextTick()
      renderCompletionChart()
    }
  } catch (error) {
    ElMessage.error('获取任务完成情况失败')
  } finally {
    taskCompletionLoading.value = false
  }
}

const fetchKnowledgePointPerformance = async () => {
  knowledgePointLoading.value = true
  try {
    const res = await getKnowledgePointPerformanceApi(courseId.value)
    if (res.data) {
      knowledgePointPerformance.value = res.data
      await nextTick()
      renderKnowledgeChart()
    }
  } catch (error) {
    ElMessage.error('获取知识点表现失败')
  } finally {
    knowledgePointLoading.value = false
  }
}

const renderScoreTrendChart = () => {
  if (!scoreChart.value) return

  const chart = echarts.init(scoreChart.value)

  const option = {
    title: {
      text: '分数趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: trendData.value.map((item) => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '平均分',
        type: 'line',
        data: trendData.value.map((item) => item.value),
        smooth: true,
        lineStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.1)'
        }
      }
    ]
  }

  chart.setOption(option)
}

const renderCompletionChart = () => {
  if (!completionChart.value) return

  const chart = echarts.init(completionChart.value)

  const option = {
    title: {
      text: '任务完成情况'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: taskCompletion.value.map((item) => item.taskTitle)
    },
    yAxis: {
      type: 'value',
      max: 100
    },
    series: [
      {
        name: '完成率',
        type: 'bar',
        data: taskCompletion.value.map((item) => item.completionRate),
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }

  chart.setOption(option)
}

const renderKnowledgeChart = () => {
  if (!knowledgeChart.value) return

  const chart = echarts.init(knowledgeChart.value)

  const option = {
    title: {
      text: '知识点掌握情况'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: knowledgePointPerformance.value.map((item) => item.knowledgePoint),
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      max: 100
    },
    series: [
      {
        name: '正确率',
        type: 'bar',
        data: knowledgePointPerformance.value.map((item) => item.accuracyRate),
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }

  chart.setOption(option)
}

const handleExport = async () => {
  try {
    const params: any = {}

    if (filters.value.dateRange.length === 2) {
      params.startDate = filters.value.dateRange[0]
      params.endDate = filters.value.dateRange[1]
    }

    const blob = await exportCourseScoresApi(courseId.value, params)
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `course_analytics_${courseId.value}_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchStudentScores()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchStudentScores()
}

const formatPercentage = (value: number) => {
  return `${(value * 100).toFixed(1)}%`
}

onMounted(() => {
  fetchTrendData()
  fetchStudentScores()
  fetchTaskCompletion()
  fetchKnowledgePointPerformance()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-select v-model="filters.granularity" style="width: 120px" @change="fetchTrendData">
            <el-option label="按天" value="daily" />
            <el-option label="按周" value="weekly" />
            <el-option label="按月" value="monthly" />
          </el-select>

          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="fetchTrendData"
          />
        </div>

        <el-button type="primary" @click="handleExport"> 导出报告 </el-button>
      </div>
    </div>

    <ElRow :gutter="20">
      <!-- 分数趋势图 -->
      <ElCol :span="24" class="mb-20px">
        <ElCard title="分数趋势分析">
          <div ref="scoreChart" style="height: 300px"></div>
        </ElCard>
      </ElCol>

      <!-- 任务完成情况 -->
      <ElCol :span="12" class="mb-20px">
        <ElCard title="任务完成情况">
          <div ref="completionChart" style="height: 300px"></div>
        </ElCard>
      </ElCol>

      <!-- 知识点掌握情况 -->
      <ElCol :span="12" class="mb-20px">
        <ElCard title="知识点掌握情况">
          <div ref="knowledgeChart" style="height: 300px"></div>
        </ElCard>
      </ElCol>

      <!-- 学生成绩详情 -->
      <ElCol :span="24" class="mb-20px">
        <ElCard title="学生成绩详情">
          <div class="mb-10px">
            <el-select v-model="filters.sortBy" style="width: 150px" @change="fetchStudentScores">
              <el-option label="按分数排序" value="score" />
              <el-option label="按完成率排序" value="completion" />
              <el-option label="按活跃度排序" value="activity" />
            </el-select>
          </div>

          <Table
            :columns="studentColumns"
            :data="studentScores"
            :loading="studentScoresLoading"
            :pagination="pagination"
            @page-change="handlePageChange"
            @page-size-change="handlePageSizeChange"
          >
            <template #completionRate="{ row }">
              <span
                :class="{
                  'text-red-500': row.completionRate < 0.6,
                  'text-green-500': row.completionRate >= 0.8
                }"
              >
                {{ formatPercentage(row.completionRate) }}
              </span>
            </template>
          </Table>
        </ElCard>
      </ElCol>

      <!-- 任务完成详情 -->
      <ElCol :span="24" class="mb-20px">
        <ElCard title="任务完成详情">
          <Table
            :columns="taskColumns"
            :data="taskCompletion"
            :loading="taskCompletionLoading"
            :pagination="false"
          >
            <template #completionRate="{ row }">
              <span
                :class="{
                  'text-red-500': row.completionRate < 60,
                  'text-green-500': row.completionRate >= 80
                }"
              >
                {{ row.completionRate.toFixed(1) }}%
              </span>
            </template>
          </Table>
        </ElCard>
      </ElCol>

      <!-- 知识点表现详情 -->
      <ElCol :span="24">
        <ElCard title="知识点表现详情">
          <Table
            :columns="knowledgeColumns"
            :data="knowledgePointPerformance"
            :loading="knowledgePointLoading"
            :pagination="false"
          >
            <template #accuracyRate="{ row }">
              <span
                :class="{
                  'text-red-500': row.accuracyRate < 60,
                  'text-green-500': row.accuracyRate >= 80
                }"
              >
                {{ row.accuracyRate.toFixed(1) }}%
              </span>
            </template>
          </Table>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
