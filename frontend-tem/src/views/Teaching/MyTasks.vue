<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

defineOptions({
  name: 'MyTasks'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const courses = ref([])
const selectedCourse = ref('')
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
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
    field: 'submissionCount',
    label: '提交数',
    width: 100
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
    field: 'action',
    label: '操作',
    width: 250,
    slots: {
      default: 'action'
    }
  }
]

const fetchMyCourses = async () => {
  try {
    const teacherId = userStore.getUserInfo?.id
    // TODO: 调用获取教师课程API
    // const res = await getTeacherCoursesApi(teacherId)
    // if (res.data) {
    //   courses.value = res.data
    // }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const fetchMyTasks = async () => {
  loading.value = true
  try {
    const teacherId = userStore.getUserInfo?.id
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      courseId: selectedCourse.value || undefined
    }

    // TODO: 调用获取教师任务API
    // const res = await getTeacherTasksApi(teacherId, params)
    // if (res.data) {
    //   tableData.value = res.data.records
    //   pagination.value.total = res.data.total
    // }
    ElMessage.info('任务数据加载中...')
  } catch (error) {
    ElMessage.error('获取我的任务失败')
  } finally {
    loading.value = false
  }
}

const handleCourseChange = () => {
  pagination.value.current = 1
  fetchMyTasks()
}

const handleCreate = () => {
  router.push('/forms/task/create')
}

const handleEdit = (row: any) => {
  router.push(`/forms/task/edit/${row.id}`)
}

const handleView = (row: any) => {
  router.push(`/details/task/${row.id}`)
}

const handleViewSubmissions = (row: any) => {
  router.push(`/details/submissions/${row.id}`)
}

const handlePublish = async (row: any) => {
  try {
    // await publishTaskApi(row.id)
    ElMessage.success('发布成功')
    fetchMyTasks()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const handleUnpublish = async (row: any) => {
  try {
    // await unpublishTaskApi(row.id)
    ElMessage.success('取消发布成功')
    fetchMyTasks()
  } catch (error) {
    ElMessage.error('取消发布失败')
  }
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
  fetchMyCourses()
  fetchMyTasks()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold">我的任务</h2>
          <p class="text-sm text-gray-500 mt-1">管理我创建的所有任务</p>
        </div>
        <div class="flex items-center space-x-4">
          <el-select
            v-model="selectedCourse"
            placeholder="选择课程"
            style="width: 200px"
            clearable
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
          <el-button type="primary" @click="handleCreate"> 新建任务 </el-button>
        </div>
      </div>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
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
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
