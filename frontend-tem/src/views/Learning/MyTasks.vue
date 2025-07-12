<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElProgress, ElMessage, ElSelect, ElOption } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import {
  getStudentTasksApi,
  getStudentTaskStatsApi,
  type StudentTaskVO,
  type StudentTaskSummaryStats
} from '@/api/task'
import { safeUserIdToString, autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'StudentTasks'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<StudentTaskVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const filterStatus = ref('')

const summaryData = ref<StudentTaskSummaryStats>({
  total: 0,
  pending: 0,
  submitted: 0,
  graded: 0,
  overdue: 0
})

const columns = [
  {
    field: 'title',
    label: '任务标题',
    width: 200
  },
  {
    field: 'courseName',
    label: '课程',
    width: 150
  },
  {
    field: 'type',
    label: '类型',
    width: 100,
    slots: {
      default: 'type'
    }
  },
  {
    field: 'dueDate',
    label: '截止时间',
    width: 150
  },
  {
    field: 'maxScore',
    label: '满分',
    width: 80
  },
  {
    field: 'score',
    label: '我的得分',
    width: 100
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
    field: 'submittedAt',
    label: '提交时间',
    width: 150
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

const fetchMyTasks = async () => {
  loading.value = true
  try {
    // 获取并修正用户ID
    const userInfo = userStore.getUserInfo
    const rawStudentId = userInfo?.userId
    const correctedIdStr = autoCorrectUserId(rawStudentId || '')

    if (!correctedIdStr || correctedIdStr === '0') {
      ElMessage.error('未获取到有效的学生信息，请重新登录')
      return
    }

    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      status: filterStatus.value || undefined
    }

    // 调用获取学生任务API
    const [tasksRes, statsRes] = await Promise.all([
      getStudentTasksApi(correctedIdStr, params),
      getStudentTaskStatsApi(correctedIdStr)
    ])

    if (tasksRes.code === 0 && tasksRes.data) {
      tableData.value = tasksRes.data.records
      pagination.value.total = tasksRes.data.total
      pagination.value.current = tasksRes.data.current
      pagination.value.size = tasksRes.data.size
    } else {
      ElMessage.warning('获取任务数据失败，显示默认数据')
      tableData.value = []
      pagination.value.total = 0
    }

    if (statsRes.code === 0 && statsRes.data) {
      summaryData.value = statsRes.data
    } else {
      console.warn('获取任务统计数据失败')
    }

    ElMessage.success(`成功加载 ${tableData.value.length} 条任务数据`)
  } catch (error) {
    console.error('获取任务失败:', error)
    ElMessage.error('获取我的任务失败')

    // 错误情况下使用空数据
    tableData.value = []
    pagination.value.total = 0
    summaryData.value = {
      total: 0,
      pending: 0,
      submitted: 0,
      graded: 0,
      overdue: 0
    }
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.value.current = 1
  fetchMyTasks()
}

const handleSubmit = (row: any) => {
  router.push(`/student-actions/task/submit/${row.id}`)
}

const handleView = (row: any) => {
  router.push(`/details/task/${row.id}`)
}

const handleViewSubmission = (row: any) => {
  router.push(`/learning/submission/${row.submissionId}`)
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    ASSIGNMENT: 'primary',
    QUIZ: 'success',
    PROJECT: 'warning',
    EXAM: 'danger'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    ASSIGNMENT: '作业',
    QUIZ: '测验',
    PROJECT: '项目',
    EXAM: '考试'
  }
  return textMap[type] || type
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    NOT_STARTED: 'info',
    IN_PROGRESS: 'primary',
    SUBMITTED: 'warning',
    GRADED: 'success',
    OVERDUE: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    SUBMITTED: '已提交',
    GRADED: '已批改',
    OVERDUE: '已逾期'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchMyTasks()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">我的任务</h2>
      <p class="text-sm text-gray-500 mt-1">查看和提交我的所有课程任务</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-5 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ summaryData.total }}</div>
          <div class="text-sm text-gray-600">总任务</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600">{{ summaryData.pending }}</div>
          <div class="text-sm text-gray-600">待完成</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-yellow-600">{{ summaryData.submitted }}</div>
          <div class="text-sm text-gray-600">已提交</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{ summaryData.graded }}</div>
          <div class="text-sm text-gray-600">已批改</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-red-600">{{ summaryData.overdue }}</div>
          <div class="text-sm text-gray-600">已逾期</div>
        </div>
      </ElCard>
    </div>

    <!-- 筛选条件 -->
    <div class="mb-20px">
      <div class="flex items-center space-x-4">
        <el-select
          v-model="filterStatus"
          placeholder="任务状态"
          style="width: 150px"
          clearable
          @change="handleFilter"
        >
          <el-option label="未开始" value="NOT_STARTED" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="已提交" value="SUBMITTED" />
          <el-option label="已批改" value="GRADED" />
          <el-option label="已逾期" value="OVERDUE" />
        </el-select>

        <el-button type="primary" @click="handleFilter"> 筛选 </el-button>
      </div>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
      <template #type="{ row }">
        <ElTag :type="getTypeColor(row.type)">
          {{ getTypeText(row.type) }}
        </ElTag>
      </template>

      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button
          v-if="row.status === 'NOT_STARTED' || row.status === 'IN_PROGRESS'"
          type="success"
          size="small"
          @click="handleSubmit(row)"
        >
          提交
        </el-button>
        <el-button
          v-if="row.status === 'SUBMITTED' || row.status === 'GRADED'"
          type="info"
          size="small"
          @click="handleViewSubmission(row)"
        >
          查看提交
        </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
