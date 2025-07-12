<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElProgress, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

defineOptions({
  name: 'StudentCourses'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = [
  {
    field: 'name',
    label: '课程名称',
    width: 200
  },
  {
    field: 'teacherName',
    label: '授课教师',
    width: 120
  },
  {
    field: 'credits',
    label: '学分',
    width: 80
  },
  {
    field: 'progress',
    label: '学习进度',
    width: 150,
    slots: {
      default: 'progress'
    }
  },
  {
    field: 'grade',
    label: '当前成绩',
    width: 100
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
    field: 'enrollmentDate',
    label: '选课时间',
    width: 120
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

const fetchMyCourses = async () => {
  loading.value = true
  try {
    const studentId = userStore.getUserInfo?.userId
    const params = {
      current: pagination.value.current,
      size: pagination.value.size
    }

    // TODO: 调用获取学生课程API
    // const res = await getStudentCoursesApi(studentId, params)
    // if (res.data) {
    //   tableData.value = res.data.records
    //   pagination.value.total = res.data.total
    // }

    console.log('加载模拟课程数据')
    // 使用模拟数据
    tableData.value = [
      {
        id: 1,
        name: 'Vue 3 实战教程',
        teacherName: '张老师',
        credits: 3,
        progress: 75,
        grade: 85,
        status: 'IN_PROGRESS',
        enrollmentDate: '2024-01-15'
      },
      {
        id: 2,
        name: 'TypeScript 基础',
        teacherName: '李老师',
        credits: 2,
        progress: 45,
        grade: null,
        status: 'ENROLLED',
        enrollmentDate: '2024-02-01'
      }
    ]
    pagination.value.total = 2

    // 更新统计数据
    summaryData.value = {
      totalCourses: 2,
      inProgress: 1,
      completed: 0,
      avgProgress: 60
    }

    ElMessage.success('课程数据加载成功(模拟数据)')
  } catch (error) {
    console.error('获取课程失败:', error)
    ElMessage.error('获取我的课程失败')
  } finally {
    loading.value = false
  }
}

const handleStudy = (row: any) => {
  router.push(`/student-actions/course/study/${row.id}`)
}

const handleViewTasks = (row: any) => {
  router.push(`/learning/course/${row.id}/tasks`)
}

const handleViewGrades = (row: any) => {
  router.push(`/learning/course/${row.id}/grades`)
}

const handleViewDetail = (row: any) => {
  router.push(`/details/course/${row.id}`)
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ENROLLED: 'success',
    IN_PROGRESS: 'primary',
    COMPLETED: 'warning',
    DROPPED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ENROLLED: '已选课',
    IN_PROGRESS: '学习中',
    COMPLETED: '已完成',
    DROPPED: '已退课'
  }
  return textMap[status] || status
}

const getProgressColor = (progress: number) => {
  if (progress >= 80) return 'success'
  if (progress >= 60) return 'primary'
  if (progress >= 40) return 'warning'
  return 'exception'
}

const summaryData = ref({
  totalCourses: 0,
  inProgress: 0,
  completed: 0,
  avgProgress: 0
})

onMounted(() => {
  fetchMyCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">我的课程</h2>
      <p class="text-sm text-gray-500 mt-1">查看我选修的所有课程和学习进度</p>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ summaryData.totalCourses }}</div>
          <div class="text-sm text-gray-600">总课程数</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{ summaryData.inProgress }}</div>
          <div class="text-sm text-gray-600">学习中</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600">{{ summaryData.completed }}</div>
          <div class="text-sm text-gray-600">已完成</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">{{ summaryData.avgProgress }}%</div>
          <div class="text-sm text-gray-600">平均进度</div>
        </div>
      </ElCard>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
      <template #progress="{ row }">
        <ElProgress
          :percentage="row.progress || 0"
          :status="getProgressColor(row.progress)"
          :stroke-width="6"
        />
      </template>

      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleStudy(row)"> 开始学习 </el-button>
        <el-button type="success" size="small" @click="handleViewTasks(row)"> 任务 </el-button>
        <el-button type="info" size="small" @click="handleViewGrades(row)"> 成绩 </el-button>
        <el-button type="warning" size="small" @click="handleViewDetail(row)"> 详情 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
