<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElMessageBox } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCoursesApi, deleteCourseApi, type CourseVO, type PageVO } from '@/api/course'
import { ElMessage } from 'element-plus'

defineOptions({
  name: 'CourseList'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref<CourseVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  name: '',
  status: '',
  teacherId: undefined as number | undefined
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
    field: 'duration',
    label: '学时',
    width: 80
  },
  {
    field: 'enrolledStudents',
    label: '已选人数',
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
    width: 200,
    slots: {
      default: 'action'
    }
  }
]

const fetchCourses = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    }

    const res = await getCoursesApi(params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchCourses()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    status: '',
    teacherId: undefined
  }
  pagination.value.current = 1
  fetchCourses()
}

const handleCreate = () => {
  router.push('/course/create')
}

const handleEdit = (row: CourseVO) => {
  router.push(`/course/edit/${row.id}`)
}

const handleView = (row: CourseVO) => {
  router.push(`/course/detail/${row.id}`)
}

const handleDelete = async (row: CourseVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除课程"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteCourseApi(row.id)
    ElMessage.success('删除成功')
    fetchCourses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchCourses()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchCourses()
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
  fetchCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchForm.name"
            placeholder="课程名称"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="未开始" value="INACTIVE" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建课程 </el-button>
      </div>
    </div>

    <Table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    >
      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
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
