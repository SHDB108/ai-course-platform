<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElCard, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

defineOptions({
  name: 'GradingCenter'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const courses = ref([])
const selectedCourse = ref('')
const filterStatus = ref('')
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = [
  {
    field: 'taskTitle',
    label: '任务名称',
    width: 180
  },
  {
    field: 'studentName',
    label: '学生姓名',
    width: 120
  },
  {
    field: 'courseName',
    label: '课程',
    width: 120
  },
  {
    field: 'submissionType',
    label: '提交类型',
    width: 100,
    slots: {
      default: 'submissionType'
    }
  },
  {
    field: 'submittedAt',
    label: '提交时间',
    width: 150
  },
  {
    field: 'score',
    label: '得分',
    width: 80
  },
  {
    field: 'status',
    label: '状态',
    width: 100,
    slots: {
      default: 'status'
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

const fetchCourses = async () => {
  try {
    // TODO: 调用获取教师课程API
    ElMessage.info('课程数据加载中...')
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const fetchSubmissions = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      courseId: selectedCourse.value || undefined,
      status: filterStatus.value || undefined
    }

    // TODO: 调用获取待批改提交API
    // const res = await getPendingSubmissionsApi(params)
    // if (res.data) {
    //   tableData.value = res.data.records
    //   pagination.value.total = res.data.total
    // }
    ElMessage.info('提交数据加载中...')
  } catch (error) {
    ElMessage.error('获取提交记录失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.value.current = 1
  fetchSubmissions()
}

const handleGrade = (row: any) => {
  router.push(`/teaching/grade/${row.id}`)
}

const handleQuickGrade = (row: any) => {
  ElMessage.info('快速批改功能开发中...')
}

const handleAiGrade = (row: any) => {
  ElMessage.info('AI批改功能开发中...')
}

const getSubmissionTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    ONLINE: '在线提交',
    FILE: '文件上传',
    LINK: '链接提交'
  }
  return textMap[type] || type
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    SUBMITTED: 'warning',
    GRADED: 'success',
    RETURNED: 'info',
    LATE: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    SUBMITTED: '待批改',
    GRADED: '已批改',
    RETURNED: '已退回',
    LATE: '迟交'
  }
  return textMap[status] || status
}

const summaryData = ref({
  pending: 0,
  graded: 0,
  avgScore: 0,
  totalSubmissions: 0
})

onMounted(() => {
  fetchCourses()
  fetchSubmissions()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">批改中心</h2>
      <p class="text-sm text-gray-500 mt-1">集中批改学生提交的作业和任务</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600">{{ summaryData.pending }}</div>
          <div class="text-sm text-gray-600">待批改</div>
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
          <div class="text-2xl font-bold text-blue-600">{{ summaryData.avgScore }}</div>
          <div class="text-sm text-gray-600">平均分</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">{{ summaryData.totalSubmissions }}</div>
          <div class="text-sm text-gray-600">总提交数</div>
        </div>
      </ElCard>
    </div>

    <!-- 筛选条件 -->
    <div class="mb-20px">
      <div class="flex items-center space-x-4">
        <el-select
          v-model="selectedCourse"
          placeholder="选择课程"
          style="width: 200px"
          clearable
          @change="handleFilter"
        >
          <el-option
            v-for="course in courses"
            :key="course.id"
            :label="course.name"
            :value="course.id"
          />
        </el-select>

        <el-select
          v-model="filterStatus"
          placeholder="状态"
          style="width: 120px"
          clearable
          @change="handleFilter"
        >
          <el-option label="待批改" value="SUBMITTED" />
          <el-option label="已批改" value="GRADED" />
          <el-option label="已退回" value="RETURNED" />
          <el-option label="迟交" value="LATE" />
        </el-select>

        <el-button type="primary" @click="handleFilter"> 筛选 </el-button>
      </div>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
      <template #submissionType="{ row }">
        <ElTag type="info">
          {{ getSubmissionTypeText(row.submissionType) }}
        </ElTag>
      </template>

      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleGrade(row)"> 批改 </el-button>
        <el-button type="success" size="small" @click="handleQuickGrade(row)"> 快速批改 </el-button>
        <el-button type="warning" size="small" @click="handleAiGrade(row)"> AI批改 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
