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
  ElBadge,
  ElProgress,
  ElCountdown
} from 'element-plus'
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getStudentExamsApi, getStudentExamStatisticsApi, startExamApi } from '@/api/exam'
import type { ExamVO, ExamStatisticsVO } from '@/api/exam/types'
import type { PageVO } from '@/api/types'
import { autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'MyExams'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<ExamVO[]>([])
const statistics = ref<ExamStatisticsVO>({
  totalExams: 0,
  completedExams: 0,
  pendingExams: 0,
  expiredExams: 0,
  averageScore: 0,
  totalScore: 0,
  passedExams: 0,
  failedExams: 0
})

const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const filters = ref({
  status: '',
  examType: '',
  courseId: null as number | null
})

const columns = [
  {
    field: 'examTitle',
    label: '考试名称',
    width: 200
  },
  {
    field: 'courseName',
    label: '课程',
    width: 150
  },
  {
    field: 'examType',
    label: '类型',
    width: 100,
    slots: {
      default: 'examType'
    }
  },
  {
    field: 'status',
    label: '状态',
    width: 120,
    slots: {
      default: 'status'
    }
  },
  {
    field: 'startTime',
    label: '开始时间',
    width: 150,
    slots: {
      default: 'startTime'
    }
  },
  {
    field: 'endTime',
    label: '结束时间',
    width: 150,
    slots: {
      default: 'endTime'
    }
  },
  {
    field: 'duration',
    label: '时长',
    width: 80,
    slots: {
      default: 'duration'
    }
  },
  {
    field: 'score',
    label: '成绩',
    width: 100,
    slots: {
      default: 'score'
    }
  },
  {
    field: 'action',
    label: '操作',
    width: 200,
    slots: {
      default: 'action'
    }
  }
]

const fetchExams = async () => {
  loading.value = true
  try {
    // 暂时使用模拟数据，因为后端API尚未实现
    const mockExams: ExamVO[] = [
      {
        id: 1,
        examTitle: 'Java基础期中考试',
        courseId: 1,
        courseName: 'Java程序设计',
        teacherName: '李老师',
        examType: 'MIDTERM',
        status: 'COMPLETED',
        startTime: '2024-03-15T09:00:00',
        endTime: '2024-03-15T11:00:00',
        duration: 120,
        totalScore: 100,
        passingScore: 60,
        description: 'Java基础知识期中测试',
        allowRetake: false,
        maxAttempts: 1,
        currentAttempts: 1,
        lastAttemptScore: 85,
        bestScore: 85,
        createdAt: '2024-03-01T00:00:00',
        updatedAt: '2024-03-15T11:00:00'
      },
      {
        id: 2,
        examTitle: '数据结构小测验',
        courseId: 2,
        courseName: '数据结构与算法',
        teacherName: '王老师',
        examType: 'QUIZ',
        status: 'NOT_STARTED',
        startTime: '2024-07-20T14:00:00',
        endTime: '2024-07-20T15:30:00',
        duration: 90,
        totalScore: 50,
        passingScore: 30,
        description: '树和图的基础知识测试',
        allowRetake: true,
        maxAttempts: 2,
        currentAttempts: 0,
        createdAt: '2024-07-01T00:00:00',
        updatedAt: '2024-07-01T00:00:00'
      },
      {
        id: 3,
        examTitle: '操作系统期末考试',
        courseId: 3,
        courseName: '操作系统原理',
        teacherName: '张老师',
        examType: 'FINAL',
        status: 'IN_PROGRESS',
        startTime: '2024-07-12T08:00:00',
        endTime: '2024-07-12T10:30:00',
        duration: 150,
        totalScore: 100,
        passingScore: 60,
        description: '操作系统综合知识考试',
        allowRetake: false,
        maxAttempts: 1,
        currentAttempts: 1,
        remainingTime: 3600,
        createdAt: '2024-06-15T00:00:00',
        updatedAt: '2024-07-12T08:00:00'
      }
    ]

    // 应用筛选
    let filteredExams = mockExams
    if (filters.value.status) {
      filteredExams = filteredExams.filter((exam) => exam.status === filters.value.status)
    }
    if (filters.value.examType) {
      filteredExams = filteredExams.filter((exam) => exam.examType === filters.value.examType)
    }

    tableData.value = filteredExams
    pagination.value.total = filteredExams.length

    if (tableData.value.length === 0) {
      ElMessage.info('暂无考试安排')
    } else {
      ElMessage.success(`加载了 ${tableData.value.length} 个考试`)
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
      ...(filters.value.status && { status: filters.value.status }),
      ...(filters.value.examType && { examType: filters.value.examType }),
      ...(filters.value.courseId && { courseId: filters.value.courseId })
    }

    const res = await getStudentExamsApi(correctedUserId, params)
    
    if (res.code === 0 && res.data) {
      tableData.value = res.data.records || []
      pagination.value.total = res.data.total || 0
      
      if (tableData.value.length === 0) {
        ElMessage.info('暂无考试安排')
      }
    } else {
      ElMessage.error(`获取考试列表失败: ${res.msg || '未知错误'}`)
    }
    */
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败，请重试')
  } finally {
    loading.value = false
  }
}

const fetchStatistics = async () => {
  try {
    // 暂时使用模拟数据，因为后端API尚未实现
    const mockStats: ExamStatisticsVO = {
      totalExams: 3,
      completedExams: 1,
      pendingExams: 1,
      expiredExams: 0,
      averageScore: 85,
      totalScore: 85,
      passedExams: 1,
      failedExams: 0
    }

    statistics.value = mockStats

    // 注释掉真实API调用，等后端实现后再启用
    /*
    const userInfo = userStore.getUserInfo
    const correctedUserId = autoCorrectUserId(userInfo?.userId || '')
    
    if (!correctedUserId) return

    const res = await getStudentExamStatisticsApi(correctedUserId)
    
    if (res.code === 0 && res.data) {
      statistics.value = res.data
    }
    */
  } catch (error) {
    console.error('获取考试统计失败:', error)
  }
}

const handleStartExam = async (exam: ExamVO) => {
  if (exam.status !== 'NOT_STARTED' && exam.status !== 'IN_PROGRESS') {
    ElMessage.warning('当前考试状态不允许开始考试')
    return
  }

  try {
    const res = await startExamApi(exam.id)
    if (res.code === 0) {
      ElMessage.success('开始考试')
      router.push(`/learning/exam/${exam.id}/take`)
    } else {
      ElMessage.error(`开始考试失败: ${res.msg}`)
    }
  } catch (error) {
    console.error('开始考试失败:', error)
    ElMessage.error('开始考试失败，请重试')
  }
}

const handleViewResult = (exam: ExamVO) => {
  router.push(`/learning/exam/${exam.id}/result`)
}

const handleViewDetail = (exam: ExamVO) => {
  router.push(`/learning/exam/${exam.id}/detail`)
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    NOT_STARTED: 'info',
    IN_PROGRESS: 'warning',
    COMPLETED: 'success',
    EXPIRED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
    EXPIRED: '已过期'
  }
  return textMap[status] || status
}

const getExamTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    QUIZ: '测验',
    MIDTERM: '期中考试',
    FINAL: '期末考试',
    ASSIGNMENT: '作业'
  }
  return typeMap[type] || type
}

const getExamTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    QUIZ: '',
    MIDTERM: 'warning',
    FINAL: 'danger',
    ASSIGNMENT: 'success'
  }
  return colorMap[type] || ''
}

const formatDateTime = (dateTime: string) => {
  return new Date(dateTime).toLocaleString('zh-CN')
}

const formatDuration = (minutes: number) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}

const isExamAvailable = (exam: ExamVO) => {
  const now = new Date()
  const startTime = new Date(exam.startTime)
  const endTime = new Date(exam.endTime)
  return now >= startTime && now <= endTime
}

const getRemainingTime = (exam: ExamVO) => {
  if (exam.remainingTime) {
    return new Date(Date.now() + exam.remainingTime * 1000)
  }
  return new Date(exam.endTime)
}

const statusOptions = [
  { label: '全部', value: '' },
  { label: '未开始', value: 'NOT_STARTED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已过期', value: 'EXPIRED' }
]

const examTypeOptions = [
  { label: '全部', value: '' },
  { label: '测验', value: 'QUIZ' },
  { label: '期中考试', value: 'MIDTERM' },
  { label: '期末考试', value: 'FINAL' },
  { label: '作业', value: 'ASSIGNMENT' }
]

const onFilterChange = () => {
  pagination.value.current = 1
  fetchExams()
}

onMounted(() => {
  fetchExams()
  fetchStatistics()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-xl font-bold">我的考试</h2>
          <p class="text-sm text-gray-500 mt-1">查看和参加我的所有考试</p>
        </div>
        <div class="flex gap-2">
          <el-button type="primary" @click="fetchExams">刷新</el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ statistics.totalExams }}</div>
          <div class="text-sm text-gray-600">总考试数</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{ statistics.completedExams }}</div>
          <div class="text-sm text-gray-600">已完成</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600">{{ statistics.pendingExams }}</div>
          <div class="text-sm text-gray-600">待参加</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">{{
            statistics.averageScore.toFixed(1)
          }}</div>
          <div class="text-sm text-gray-600">平均分</div>
        </div>
      </ElCard>
    </div>

    <!-- 筛选器 -->
    <ElCard class="mb-20px">
      <div class="flex gap-4 items-center">
        <div class="flex items-center gap-2">
          <span class="text-sm">状态:</span>
          <ElSelect
            v-model="filters.status"
            placeholder="选择状态"
            style="width: 120px"
            @change="onFilterChange"
          >
            <ElOption
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </ElSelect>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-sm">类型:</span>
          <ElSelect
            v-model="filters.examType"
            placeholder="选择类型"
            style="width: 120px"
            @change="onFilterChange"
          >
            <ElOption
              v-for="option in examTypeOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </ElSelect>
        </div>
      </div>
    </ElCard>

    <!-- 考试列表 -->
    <div class="table-container">
      <Table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        style="width: 100%"
      >
        <template #examType="{ row }">
          <ElTag :type="getExamTypeColor(row.examType)" size="small">
            {{ getExamTypeText(row.examType) }}
          </ElTag>
        </template>

        <template #status="{ row }">
          <ElBadge v-if="row.status === 'IN_PROGRESS'" is-dot>
            <ElTag :type="getStatusColor(row.status)">
              {{ getStatusText(row.status) }}
            </ElTag>
          </ElBadge>
          <ElTag v-else :type="getStatusColor(row.status)">
            {{ getStatusText(row.status) }}
          </ElTag>
        </template>

        <template #startTime="{ row }">
          <span class="text-sm">{{ formatDateTime(row.startTime) }}</span>
        </template>

        <template #endTime="{ row }">
          <span class="text-sm">{{ formatDateTime(row.endTime) }}</span>
          <ElCountdown
            v-if="row.status === 'IN_PROGRESS'"
            :value="getRemainingTime(row)"
            format="HH:mm:ss"
            class="text-xs text-red-500 mt-1"
          />
        </template>

        <template #duration="{ row }">
          <span class="text-sm">{{ formatDuration(row.duration) }}</span>
        </template>

        <template #score="{ row }">
          <div v-if="row.status === 'COMPLETED'">
            <div class="font-semibold">{{ row.lastAttemptScore || 0 }}/{{ row.totalScore }}</div>
            <div class="text-xs text-gray-500">最佳: {{ row.bestScore || 0 }}</div>
          </div>
          <span v-else class="text-gray-400">-</span>
        </template>

        <template #action="{ row }">
          <div class="flex gap-1">
            <el-button
              v-if="row.status === 'NOT_STARTED' || row.status === 'IN_PROGRESS'"
              type="primary"
              size="small"
              :disabled="!isExamAvailable(row)"
              @click="handleStartExam(row)"
            >
              {{ row.status === 'IN_PROGRESS' ? '继续考试' : '开始考试' }}
            </el-button>
            <el-button
              v-if="row.status === 'COMPLETED'"
              type="success"
              size="small"
              @click="handleViewResult(row)"
            >
              查看结果
            </el-button>
            <el-button type="info" size="small" @click="handleViewDetail(row)"> 详情 </el-button>
          </div>
        </template>
      </Table>
    </div>
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
</style>
