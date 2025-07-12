<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage, ElInput } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getCoursesApi,
  updateCourseStatusApi,
  createCourseApi,
  deleteCourseApi,
  getCourseCategoriesApi,
  type CourseVO,
  type CourseCreateDTO,
  type CourseCategoryVO
} from '@/api/course'

defineOptions({
  name: 'CourseManagement'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref<CourseVO[]>([])
const categories = ref<CourseCategoryVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  name: '',
  categoryId: '',
  status: '',
  teacherId: ''
})

const columns = [
  {
    field: 'id',
    label: '课程ID',
    width: 80
  },
  {
    field: 'name',
    label: '课程名称',
    width: 200
  },
  {
    field: 'categoryName',
    label: '课程分类',
    width: 120
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
    field: 'enrolledStudents',
    label: '选课人数',
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
    field: 'createdAt',
    label: '创建时间',
    width: 150
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

const fetchCourses = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      keyword: searchForm.value.name || undefined,
      status: searchForm.value.status || undefined,
      teacherId: searchForm.value.teacherId || undefined,
      categoryId: searchForm.value.categoryId || undefined
    }

    const response = await getCoursesApi(params)
    if (response?.data) {
      tableData.value = response.data.records
      pagination.value.total = response.data.total
      pagination.value.current = response.data.current
      pagination.value.size = response.data.size
      ElMessage.success('课程列表加载成功')
    }
  } catch (error: any) {
    console.error('获取课程列表失败:', error)
    ElMessage.error(error?.message || '获取课程列表失败')
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
    categoryId: '',
    status: '',
    teacherId: ''
  }
  pagination.value.current = 1
  fetchCourses()
}

const handleCreate = () => {
  router.push('/forms/course/create')
}

const handleEdit = (row: CourseVO) => {
  router.push(`/forms/course/edit/${row.id}`)
}

const handleView = (row: CourseVO) => {
  router.push(`/details/course/${row.id}`)
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
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

const handleStatusChange = async (row: CourseVO, status: string) => {
  try {
    await updateCourseStatusApi(row.id, { status })
    const statusText = getStatusText(status)
    ElMessage.success(`课程状态已更新为"${statusText}"`)
    row.status = status
  } catch (error: any) {
    console.error('状态更新失败:', error)
    ElMessage.error(error?.message || '状态更新失败')
  }
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    PUBLISHED: 'success',
    DRAFT: 'info',
    SUSPENDED: 'danger',
    COMPLETED: 'warning'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PUBLISHED: '已发布',
    DRAFT: '草稿',
    SUSPENDED: '已暂停',
    COMPLETED: '已完成'
  }
  return textMap[status] || status
}

const fetchCategories = async () => {
  try {
    const response = await getCourseCategoriesApi()
    if (response?.data) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取课程分类失败:', error)
  }
}

// 分页处理
const handlePageChange = (current: number) => {
  pagination.value.current = current
  fetchCourses()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchCourses()
}

onMounted(() => {
  fetchCategories()
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
            placeholder="课程名称/教师姓名"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select
            v-model="searchForm.categoryId"
            placeholder="课程分类"
            style="width: 150px"
            clearable
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-input
            v-model="searchForm.teacherId"
            placeholder="教师ID"
            style="width: 120px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已暂停" value="SUSPENDED" />
            <el-option label="已完成" value="COMPLETED" />
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
      @size-change="handleSizeChange"
    >
      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="success" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button
          v-if="row.status === 'PUBLISHED'"
          type="warning"
          size="small"
          @click="handleStatusChange(row, 'SUSPENDED')"
        >
          暂停
        </el-button>
        <el-button
          v-else-if="row.status === 'SUSPENDED'"
          type="success"
          size="small"
          @click="handleStatusChange(row, 'PUBLISHED')"
        >
          恢复
        </el-button>
        <el-button
          v-else-if="row.status === 'DRAFT'"
          type="success"
          size="small"
          @click="handleStatusChange(row, 'PUBLISHED')"
        >
          发布
        </el-button>
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
