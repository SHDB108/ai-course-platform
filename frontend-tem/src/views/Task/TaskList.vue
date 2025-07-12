<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getCourseTasksApi,
  deleteTaskApi,
  publishTaskApi,
  unpublishTaskApi,
  type TaskVO,
  type PageVO
} from '@/api/task'
import { getCoursesApi, type CourseVO } from '@/api/course'

defineOptions({
  name: 'TaskList'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const courseLoading = ref(false)
const tableData = ref<TaskVO[]>([])
const courses = ref<CourseVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  courseId: Number(route.query.courseId) || undefined,
  type: '',
  published: undefined as boolean | undefined
})

const columns = [
  {
    field: 'title',
    label: '任务标题',
    width: 200
  },
  {
    field: 'courseName',
    label: '所属课程',
    width: 150
  },
  {
    field: 'type',
    label: '任务类型',
    width: 120,
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
    field: 'published',
    label: '发布状态',
    width: 100,
    slots: {
      default: 'published'
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

const fetchTasks = async () => {
  if (!searchForm.value.courseId) return

  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      type: searchForm.value.type || undefined,
      published: searchForm.value.published
    }

    const res = await getCourseTasksApi(searchForm.value.courseId, params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取任务列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  courseLoading.value = true
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    courseLoading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchTasks()
}

const handleReset = () => {
  searchForm.value = {
    courseId: undefined,
    type: '',
    published: undefined
  }
  pagination.value.current = 1
  fetchTasks()
}

const handleCreate = () => {
  router.push('/task/create')
}

const handleEdit = (row: TaskVO) => {
  router.push(`/task/edit/${row.id}`)
}

const handleView = (row: TaskVO) => {
  router.push(`/task/detail/${row.id}`)
}

const handleViewSubmissions = (row: TaskVO) => {
  router.push(`/task/submissions/${row.id}`)
}

const handlePublish = async (row: TaskVO) => {
  try {
    await publishTaskApi(row.id)
    ElMessage.success('发布成功')
    fetchTasks()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const handleUnpublish = async (row: TaskVO) => {
  try {
    await unpublishTaskApi(row.id)
    ElMessage.success('取消发布成功')
    fetchTasks()
  } catch (error) {
    ElMessage.error('取消发布失败')
  }
}

const handleDelete = async (row: TaskVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除任务"${row.title}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteTaskApi(row.id)
    ElMessage.success('删除成功')
    fetchTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchTasks()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchTasks()
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

onMounted(() => {
  fetchCourses()
  if (searchForm.value.courseId) {
    fetchTasks()
  }
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-select
            v-model="searchForm.courseId"
            placeholder="选择课程"
            style="width: 200px"
            clearable
            filterable
            :loading="courseLoading"
            @change="handleSearch"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>

          <el-select
            v-model="searchForm.type"
            placeholder="任务类型"
            style="width: 120px"
            clearable
          >
            <el-option label="作业" value="ASSIGNMENT" />
            <el-option label="测验" value="QUIZ" />
            <el-option label="项目" value="PROJECT" />
            <el-option label="考试" value="EXAM" />
          </el-select>

          <el-select
            v-model="searchForm.published"
            placeholder="发布状态"
            style="width: 120px"
            clearable
          >
            <el-option label="已发布" :value="true" />
            <el-option label="未发布" :value="false" />
          </el-select>

          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建任务 </el-button>
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
      <template #type="{ row }">
        <ElTag :type="getTypeColor(row.type)">
          {{ getTypeText(row.type) }}
        </ElTag>
      </template>

      <template #published="{ row }">
        <ElTag :type="row.published ? 'success' : 'info'">
          {{ row.published ? '已发布' : '未发布' }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button type="info" size="small" @click="handleViewSubmissions(row)"> 提交 </el-button>
        <el-button v-if="!row.published" type="success" size="small" @click="handlePublish(row)">
          发布
        </el-button>
        <el-button v-else type="warning" size="small" @click="handleUnpublish(row)">
          取消发布
        </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
