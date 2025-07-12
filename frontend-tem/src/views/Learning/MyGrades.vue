<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import {
  ElCard,
  ElMessage,
  ElButton,
  ElTag,
  ElSelect,
  ElOption,
  ElProgress,
  ElTabs,
  ElTabPane,
  ElStatistic,
  ElDivider,
  ElDescriptions,
  ElDescriptionsItem,
  ElEmpty
} from 'element-plus'
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import {
  getStudentGradesApi,
  getStudentGradeStatisticsApi,
  getStudentGPAHistoryApi,
  getStudentGradeTrendApi,
  exportGradeReportApi
} from '@/api/grade'
import type { GradeVO, GradeStatisticsVO, GPAHistoryVO, GradeTrendVO } from '@/api/grade/types'
import type { PageVO } from '@/api/types'
import { autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'MyGrades'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<GradeVO[]>([])
const statistics = ref<GradeStatisticsVO>({
  totalCourses: 0,
  completedCourses: 0,
  inProgressCourses: 0,
  failedCourses: 0,
  overallGPA: 0,
  semesterGPA: 0,
  totalCredits: 0,
  earnedCredits: 0,
  averageScore: 0,
  highestScore: 0,
  lowestScore: 0,
  gradesDistribution: {
    A: 0,
    B: 0,
    C: 0,
    D: 0,
    F: 0
  }
})
const gpaHistory = ref<GPAHistoryVO[]>([])
const gradeTrend = ref<GradeTrendVO[]>([])

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const filters = ref({
  gradeType: '',
  semester: '',
  courseId: null as number | null
})

const activeTab = ref('grades')

const columns = [
  {
    field: 'courseName',
    label: '课程名称',
    width: 150
  },
  {
    field: 'gradeType',
    label: '成绩类型',
    width: 100,
    slots: {
      default: 'gradeType'
    }
  },
  {
    field: 'examTitle',
    label: '考试/作业',
    width: 150
  },
  {
    field: 'score',
    label: '得分',
    width: 80,
    slots: {
      default: 'score'
    }
  },
  {
    field: 'totalScore',
    label: '总分',
    width: 80
  },
  {
    field: 'percentage',
    label: '百分比',
    width: 80,
    slots: {
      default: 'percentage'
    }
  },
  {
    field: 'letterGrade',
    label: '等级',
    width: 80,
    slots: {
      default: 'letterGrade'
    }
  },
  {
    field: 'weight',
    label: '权重',
    width: 80,
    slots: {
      default: 'weight'
    }
  },
  {
    field: 'submissionDate',
    label: '提交时间',
    width: 150,
    slots: {
      default: 'submissionDate'
    }
  },
  {
    field: 'status',
    label: '状态',
    width: 100,
    slots: {
      default: 'status'
    }
  }
]

const fetchGrades = async () => {
  loading.value = true
  try {
    // 暂时使用模拟数据，因为后端API尚未实现
    const mockGrades: GradeVO[] = [
      {
        id: 1,
        studentId: '1943886359143919618',
        courseId: 1,
        courseName: 'Java程序设计',
        teacherName: '李老师',
        examId: 1,
        examTitle: 'Java基础期中考试',
        gradeType: 'EXAM',
        score: 85,
        totalScore: 100,
        percentage: 85,
        letterGrade: 'B',
        gradePoint: 3.0,
        weight: 0.4,
        submissionDate: '2024-03-15T11:00:00',
        gradedDate: '2024-03-20T10:00:00',
        feedback: '基础知识掌握较好，需要加强编程实践',
        isLate: false,
        latePenalty: 0,
        status: 'GRADED',
        createdAt: '2024-03-15T11:00:00',
        updatedAt: '2024-03-20T10:00:00'
      },
      {
        id: 2,
        studentId: '1943886359143919618',
        courseId: 2,
        courseName: '数据结构与算法',
        teacherName: '王老师',
        taskId: 1,
        taskTitle: '二叉树编程作业',
        gradeType: 'ASSIGNMENT',
        score: 92,
        totalScore: 100,
        percentage: 92,
        letterGrade: 'A',
        gradePoint: 4.0,
        weight: 0.3,
        submissionDate: '2024-04-10T23:30:00',
        gradedDate: '2024-04-15T14:00:00',
        feedback: '代码质量高，逻辑清晰',
        isLate: true,
        latePenalty: 5,
        status: 'GRADED',
        createdAt: '2024-04-10T23:30:00',
        updatedAt: '2024-04-15T14:00:00'
      },
      {
        id: 3,
        studentId: '1943886359143919618',
        courseId: 3,
        courseName: '操作系统原理',
        teacherName: '张老师',
        examId: 2,
        examTitle: '进程管理测验',
        gradeType: 'QUIZ',
        score: 78,
        totalScore: 100,
        percentage: 78,
        letterGrade: 'C',
        gradePoint: 2.0,
        weight: 0.2,
        submissionDate: '2024-05-20T16:00:00',
        gradedDate: '2024-05-22T09:00:00',
        feedback: '理论理解尚可，需要加强实践',
        isLate: false,
        latePenalty: 0,
        status: 'GRADED',
        createdAt: '2024-05-20T16:00:00',
        updatedAt: '2024-05-22T09:00:00'
      }
    ]

    // 应用筛选
    let filteredGrades = mockGrades
    if (filters.value.gradeType) {
      filteredGrades = filteredGrades.filter((grade) => grade.gradeType === filters.value.gradeType)
    }
    if (filters.value.courseId) {
      filteredGrades = filteredGrades.filter((grade) => grade.courseId === filters.value.courseId)
    }

    tableData.value = filteredGrades
    pagination.value.total = filteredGrades.length

    if (tableData.value.length === 0) {
      ElMessage.info('暂无成绩记录')
    } else {
      ElMessage.success(`加载了 ${tableData.value.length} 条成绩记录`)
    }

    // 注释掉真实API调用，等后端实现后再启用
    /*
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')
    
    if (!correctedUserId) {
      ElMessage.error('未获取到有效的学生信息，请重新登录')
      return
    }

    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      ...(filters.value.gradeType && { gradeType: filters.value.gradeType }),
      ...(filters.value.semester && { semester: filters.value.semester }),
      ...(filters.value.courseId && { courseId: filters.value.courseId })
    }

    const res = await getStudentGradesApi(correctedUserId, params)
    
    if (res.code === 0 && res.data) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
      
      if (tableData.value.length === 0) {
        ElMessage.info('暂无成绩记录')
      }
    } else {
      ElMessage.error(`获取成绩列表失败: ${res.msg || '未知错误'}`)
    }
    */
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败，请重试')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    // 暂时使用模拟数据，因为后端API尚未实现
    const mockStats: GradeStatisticsVO = {
      totalCourses: 3,
      completedCourses: 2,
      inProgressCourses: 1,
      failedCourses: 0,
      overallGPA: 3.0,
      semesterGPA: 3.2,
      totalCredits: 12,
      earnedCredits: 8,
      averageScore: 85,
      highestScore: 92,
      lowestScore: 78,
      gradesDistribution: {
        A: 1,
        B: 1,
        C: 1,
        D: 0,
        F: 0
      }
    }

    statistics.value = mockStats

    // 注释掉真实API调用，等后端实现后再启用
    /*
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')
    
    if (!correctedUserId) return

    const res = await getStudentGradeStatisticsApi(correctedUserId)
    
    if (res.code === 0 && res.data) {
      statistics.value = res.data
    }
    */
  } catch (error) {
    console.error('获取成绩统计失败:', error)
  }
}

const fetchGPAHistory = async () => {
  try {
    // 暂时使用模拟数据，因为后端API尚未实现
    const mockHistory: GPAHistoryVO[] = [
      {
        semester: '2024春季学期',
        gpa: 3.2,
        credits: 6,
        courses: 2,
        createdAt: '2024-06-30T00:00:00'
      },
      {
        semester: '2023秋季学期',
        gpa: 2.8,
        credits: 6,
        courses: 2,
        createdAt: '2024-01-30T00:00:00'
      }
    ]

    gpaHistory.value = mockHistory

    // 注释掉真实API调用，等后端实现后再启用
    /*
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')
    
    if (!correctedUserId) return

    const res = await getStudentGPAHistoryApi(correctedUserId)
    
    if (res.code === 0 && res.data) {
      gpaHistory.value = res.data
    }
    */
  } catch (error) {
    console.error('获取GPA历史失败:', error)
  }
}

const fetchGradeTrend = async () => {
  try {
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')

    if (!correctedUserId) return

    const res = await getStudentGradeTrendApi(correctedUserId, 12)

    if (res.code === 0 && res.data) {
      gradeTrend.value = res.data
    }
  } catch (error) {
    console.error('获取成绩趋势失败:', error)
  }
}

const handleExportGrades = async (format: 'PDF' | 'EXCEL') => {
  try {
    // 暂时显示模拟导出成功，因为后端API尚未实现
    ElMessage.success(`正在生成${format}格式的成绩单...`)

    // 模拟导出延迟
    setTimeout(() => {
      ElMessage.success(`${format}成绩单导出成功（模拟）`)
    }, 1500)

    // 注释掉真实API调用，等后端实现后再启用
    /*
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')
    
    if (!correctedUserId) {
      ElMessage.error('未获取到有效的学生信息')
      return
    }

    const blob = await exportGradeReportApi(correctedUserId, format)
    
    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `成绩单_${new Date().toISOString().split('T')[0]}.${format.toLowerCase()}`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('成绩单导出成功')
    */
  } catch (error) {
    console.error('导出成绩单失败:', error)
    ElMessage.error('导出成绩单失败，请重试')
  }
}

const getGradeTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    EXAM: 'danger',
    ASSIGNMENT: 'warning',
    QUIZ: 'primary',
    PROJECT: 'success',
    PARTICIPATION: 'info'
  }
  return colorMap[type] || ''
}

const getGradeTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    EXAM: '考试',
    ASSIGNMENT: '作业',
    QUIZ: '测验',
    PROJECT: '项目',
    PARTICIPATION: '参与度'
  }
  return textMap[type] || type
}

const getLetterGradeColor = (grade: string) => {
  const colorMap: Record<string, string> = {
    A: 'success',
    B: 'primary',
    C: 'warning',
    D: 'info',
    F: 'danger'
  }
  return colorMap[grade] || ''
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    GRADED: 'success',
    PENDING: 'warning',
    RESUBMIT_REQUIRED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    GRADED: '已评分',
    PENDING: '待评分',
    RESUBMIT_REQUIRED: '需重提交'
  }
  return textMap[status] || status
}

const formatDateTime = (dateTime: string) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

const gradeTypeOptions = [
  { label: '全部', value: '' },
  { label: '考试', value: 'EXAM' },
  { label: '作业', value: 'ASSIGNMENT' },
  { label: '测验', value: 'QUIZ' },
  { label: '项目', value: 'PROJECT' },
  { label: '参与度', value: 'PARTICIPATION' }
]

const onFilterChange = () => {
  pagination.value.current = 1
  fetchGrades()
}

const onTabChange = (tab: string) => {
  activeTab.value = tab
  if (tab === 'statistics' && gpaHistory.value.length === 0) {
    fetchGPAHistory()
    fetchGradeTrend()
  }
}

onMounted(() => {
  fetchGrades()
  fetchStatistics()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-xl font-bold">我的成绩</h2>
          <p class="text-sm text-gray-500 mt-1">查看我所有课程的成绩和表现</p>
        </div>
        <div class="flex gap-2">
          <el-button type="success" @click="handleExportGrades('PDF')">导出PDF</el-button>
          <el-button type="primary" @click="handleExportGrades('EXCEL')">导出Excel</el-button>
          <el-button type="primary" @click="fetchGrades">刷新</el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ statistics.overallGPA.toFixed(2) }}</div>
          <div class="text-sm text-gray-600">总GPA</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{
            statistics.averageScore.toFixed(1)
          }}</div>
          <div class="text-sm text-gray-600">平均分</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600"
            >{{ statistics.earnedCredits }}/{{ statistics.totalCredits }}</div
          >
          <div class="text-sm text-gray-600">获得学分</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">{{ statistics.completedCourses }}</div>
          <div class="text-sm text-gray-600">完成课程</div>
        </div>
      </ElCard>
    </div>

    <!-- 标签页 -->
    <ElTabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 成绩列表 -->
      <ElTabPane label="成绩列表" name="grades">
        <!-- 筛选器 -->
        <ElCard class="mb-20px">
          <div class="flex gap-4 items-center">
            <div class="flex items-center gap-2">
              <span class="text-sm">成绩类型:</span>
              <ElSelect
                v-model="filters.gradeType"
                placeholder="选择类型"
                style="width: 120px"
                @change="onFilterChange"
              >
                <ElOption
                  v-for="option in gradeTypeOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </ElSelect>
            </div>
          </div>
        </ElCard>

        <!-- 成绩表格 -->
        <div class="table-container">
          <Table
            :columns="columns"
            :data="tableData"
            :loading="loading"
            :pagination="pagination"
            style="width: 100%"
          >
            <template #gradeType="{ row }">
              <ElTag :type="getGradeTypeColor(row.gradeType)" size="small">
                {{ getGradeTypeText(row.gradeType) }}
              </ElTag>
            </template>

            <template #score="{ row }">
              <span
                class="font-semibold"
                :class="{
                  'text-red-500': row.score < row.passingScore,
                  'text-green-600': row.score >= row.passingScore
                }"
              >
                {{ row.score }}
              </span>
            </template>

            <template #percentage="{ row }">
              <div class="flex items-center gap-2">
                <ElProgress
                  :percentage="row.percentage"
                  :stroke-width="8"
                  :show-text="false"
                  :color="row.percentage >= 60 ? '#67c23a' : '#f56c6c'"
                />
                <span class="text-sm font-medium">{{ row.percentage.toFixed(1) }}%</span>
              </div>
            </template>

            <template #letterGrade="{ row }">
              <ElTag :type="getLetterGradeColor(row.letterGrade)" size="large">
                {{ row.letterGrade }}
              </ElTag>
            </template>

            <template #weight="{ row }">
              <span class="text-sm">{{ (row.weight * 100).toFixed(0) }}%</span>
            </template>

            <template #submissionDate="{ row }">
              <span class="text-sm">{{ formatDateTime(row.submissionDate) }}</span>
              <div v-if="row.isLate" class="text-xs text-red-500"
                >迟交(-{{ row.latePenalty }}分)</div
              >
            </template>

            <template #status="{ row }">
              <ElTag :type="getStatusColor(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </ElTag>
            </template>
          </Table>
        </div>
      </ElTabPane>

      <!-- 成绩统计 -->
      <ElTabPane label="成绩统计" name="statistics">
        <div class="grid grid-cols-2 gap-6">
          <!-- 成绩分布 -->
          <ElCard>
            <template #header>
              <div class="card-header">
                <span>成绩分布</span>
              </div>
            </template>
            <div class="space-y-4">
              <div
                v-for="(count, grade) in statistics.gradesDistribution"
                :key="grade"
                class="flex justify-between items-center"
              >
                <span class="font-medium">等级 {{ grade }}:</span>
                <div class="flex items-center gap-2">
                  <ElProgress
                    :percentage="
                      statistics.totalCourses > 0 ? (count / statistics.totalCourses) * 100 : 0
                    "
                    :stroke-width="12"
                    :show-text="false"
                    :color="getLetterGradeColor(grade)"
                    style="width: 100px"
                  />
                  <span class="text-sm">{{ count }} 门</span>
                </div>
              </div>
            </div>
          </ElCard>

          <!-- 学习情况 -->
          <ElCard>
            <template #header>
              <div class="card-header">
                <span>学习情况</span>
              </div>
            </template>
            <ElDescriptions :column="1" border>
              <ElDescriptionsItem label="总课程数">{{
                statistics.totalCourses
              }}</ElDescriptionsItem>
              <ElDescriptionsItem label="已完成">{{
                statistics.completedCourses
              }}</ElDescriptionsItem>
              <ElDescriptionsItem label="进行中">{{
                statistics.inProgressCourses
              }}</ElDescriptionsItem>
              <ElDescriptionsItem label="失败课程">{{
                statistics.failedCourses
              }}</ElDescriptionsItem>
              <ElDescriptionsItem label="当前GPA">
                <ElTag :type="statistics.semesterGPA >= 3.0 ? 'success' : 'warning'">
                  {{ statistics.semesterGPA.toFixed(2) }}
                </ElTag>
              </ElDescriptionsItem>
              <ElDescriptionsItem label="最高分">{{ statistics.highestScore }}</ElDescriptionsItem>
              <ElDescriptionsItem label="最低分">{{ statistics.lowestScore }}</ElDescriptionsItem>
            </ElDescriptions>
          </ElCard>
        </div>

        <!-- GPA历史 -->
        <ElCard class="mt-6" v-if="gpaHistory.length > 0">
          <template #header>
            <div class="card-header">
              <span>GPA历史趋势</span>
            </div>
          </template>
          <div class="space-y-3">
            <div
              v-for="item in gpaHistory"
              :key="item.semester"
              class="flex justify-between items-center p-3 bg-gray-50 rounded"
            >
              <div>
                <div class="font-medium">{{ item.semester }}</div>
                <div class="text-sm text-gray-500"
                  >{{ item.courses }} 门课程 · {{ item.credits }} 学分</div
                >
              </div>
              <div class="text-right">
                <div
                  class="text-xl font-bold"
                  :class="{
                    'text-green-600': item.gpa >= 3.5,
                    'text-blue-600': item.gpa >= 3.0 && item.gpa < 3.5,
                    'text-orange-600': item.gpa >= 2.5 && item.gpa < 3.0,
                    'text-red-600': item.gpa < 2.5
                  }"
                  >{{ item.gpa.toFixed(2) }}</div
                >
                <div class="text-sm text-gray-500">GPA</div>
              </div>
            </div>
          </div>
        </ElCard>
      </ElTabPane>
    </ElTabs>
  </ContentWrap>
</template>

<style scoped lang="less">
.table-container {
  width: 100%;
  min-height: 400px;

  :deep(.el-table) {
    width: 100% !important;
  }

  :deep(.el-table__body-wrapper) {
    min-height: 300px;
  }
}

:deep(.content-wrap) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>
