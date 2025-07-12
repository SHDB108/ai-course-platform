<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getTeacherCoursesApi } from '@/api/teacher'

defineOptions({
  name: 'MyCourses'
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
    field: 'description',
    label: '课程描述',
    width: 300
  },
  {
    field: 'enrolledStudents',
    label: '学生人数',
    width: 100
  },
  {
    field: 'maxStudents',
    label: '最大人数',
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
    field: 'startDate',
    label: '开始时间',
    width: 120
  },
  {
    field: 'endDate',
    label: '结束时间',
    width: 120
  },
  {
    field: 'action',
    label: '操作',
    width: 250,
    slots: {
      default: 'action'
    }
  }
]

const fetchMyCourses = async () => {
  loading.value = true
  try {
    // 临时使用模拟数据进行演示
    const mockCourses = [
      {
        id: 1001,
        name: 'Java程序设计',
        description: 'Java编程语言基础与高级特性，包括面向对象编程、集合框架、多线程等',
        enrolledStudents: 45,
        maxStudents: 50,
        status: 'ACTIVE',
        startDate: '2024-09-01',
        endDate: '2024-12-31'
      },
      {
        id: 1002,
        name: '数据结构与算法',
        description: '计算机科学核心课程，包含各种数据结构和算法的设计与分析',
        enrolledStudents: 38,
        maxStudents: 45,
        status: 'ACTIVE',
        startDate: '2024-09-01',
        endDate: '2024-12-31'
      },
      {
        id: 1003,
        name: '机器学习基础',
        description: '人工智能入门课程，介绍机器学习基本概念、算法和应用',
        enrolledStudents: 32,
        maxStudents: 40,
        status: 'ACTIVE',
        startDate: '2024-09-01',
        endDate: '2024-12-31'
      },
      {
        id: 1004,
        name: '软件工程实践',
        description: '软件开发生命周期与项目管理，包括需求分析、设计模式、测试等',
        enrolledStudents: 0,
        maxStudents: 35,
        status: 'DRAFT',
        startDate: '2024-10-01',
        endDate: '2025-01-31'
      }
    ]

    tableData.value = mockCourses
    pagination.value.total = mockCourses.length
    pagination.value.current = 1
    pagination.value.size = 10
    ElMessage.success(`成功加载 ${mockCourses.length} 门课程（演示数据）`)

    // 正式版本的API调用（暂时注释）
    /*
    const teacherId = userStore.getUserInfo?.userId
    if (teacherId) {
      const params = {
        pageNum: pagination.value.current,
        pageSize: pagination.value.size
      }
      const res = await getTeacherCoursesApi(teacherId, params)
      if (res?.data) {
        tableData.value = res.data.records
        pagination.value.total = res.data.total
        pagination.value.current = res.data.current
        pagination.value.size = res.data.size
      }
    } else {
      ElMessage.error('获取用户信息失败')
    }
    */
  } catch (error: any) {
    console.error('获取我的课程失败:', error)
    ElMessage.error(error?.message || '获取我的课程失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  router.push('/forms/course/create')
}

const handleEdit = (row: any) => {
  router.push(`/forms/course/edit/${row.id}`)
}

const handleView = (row: any) => {
  router.push(`/details/course/${row.id}`)
}

const handleManageStudents = (row: any) => {
  router.push(`/teaching/course/${row.id}/students`)
}

const handleManageTasks = (row: any) => {
  router.push(`/teaching/course/${row.id}/tasks`)
}

const handleAnalytics = (row: any) => {
  router.push(`/details/analytics/course/${row.id}`)
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    COMPLETED: 'warning',
    CANCELLED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '进行中',
    INACTIVE: '未开始',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold">我的课程</h2>
          <p class="text-sm text-gray-500 mt-1">管理我教授的所有课程</p>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建课程 </el-button>
      </div>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button type="success" size="small" @click="handleManageStudents(row)"> 学生 </el-button>
        <el-button type="warning" size="small" @click="handleManageTasks(row)"> 任务 </el-button>
        <el-button type="info" size="small" @click="handleAnalytics(row)"> 分析 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less">
.table-container {
  width: 100%;

  :deep(.el-table) {
    width: 100% !important;
  }
}
</style>
