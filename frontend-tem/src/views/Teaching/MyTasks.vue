<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import {
  getTeacherTasksApi,
  getTeacherCourseOptionsApi,
  type TeacherTaskVO,
  type SimpleCourseVO
} from '@/api/teacher'
import { publishTaskApi, unpublishTaskApi } from '@/api/task'
import { safeUserIdToString, autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'MyTasks'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<TeacherTaskVO[]>([])
const courses = ref<SimpleCourseVO[]>([])
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
    // 临时使用模拟数据进行演示
    const mockCourses: SimpleCourseVO[] = [
      { id: 1001, name: 'Java程序设计' },
      { id: 1002, name: '数据结构与算法' },
      { id: 1003, name: '机器学习基础' },
      { id: 1004, name: '软件工程实践' }
    ]

    courses.value = mockCourses
    ElMessage.success('课程列表加载成功（演示数据）')

    // 正式版本的API调用（暂时注释）
    /*
    const userInfo = userStore.getUserInfo
    const rawTeacherId = userInfo?.userId
    const correctedIdStr = autoCorrectUserId(rawTeacherId || '')

    if (!correctedIdStr || correctedIdStr === '0') {
      ElMessage.error('未获取到有效的教师信息，请重新登录')
      return
    }

    const res = await getTeacherCourseOptionsApi(correctedIdStr)
    if (res.code === 0 && res.data) {
      courses.value = res.data
    } else {
      ElMessage.warning('获取课程列表失败')
    }
    */
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
  }
}

const fetchMyTasks = async () => {
  loading.value = true
  try {
    // 临时使用模拟数据进行演示
    const mockTasks: TeacherTaskVO[] = [
      {
        id: 4001,
        title: 'Java基础练习1',
        courseName: 'Java程序设计',
        type: 'ASSIGNMENT',
        dueDate: '2024-09-15 23:59:59',
        maxScore: 100,
        submissionCount: 15,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4002,
        title: 'Java面向对象编程',
        courseName: 'Java程序设计',
        type: 'PROJECT',
        dueDate: '2024-10-01 23:59:59',
        maxScore: 100,
        submissionCount: 8,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4003,
        title: 'Java期中测验',
        courseName: 'Java程序设计',
        type: 'QUIZ',
        dueDate: '2024-10-15 14:00:00',
        maxScore: 100,
        submissionCount: 12,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4004,
        title: '数据结构实验1',
        courseName: '数据结构与算法',
        type: 'ASSIGNMENT',
        dueDate: '2024-09-20 23:59:59',
        maxScore: 100,
        submissionCount: 20,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4005,
        title: '排序算法比较',
        courseName: '数据结构与算法',
        type: 'PROJECT',
        dueDate: '2024-10-10 23:59:59',
        maxScore: 100,
        submissionCount: 5,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4006,
        title: '算法设计期中考试',
        courseName: '数据结构与算法',
        type: 'EXAM',
        dueDate: '2024-10-20 10:00:00',
        maxScore: 100,
        submissionCount: 0,
        published: false,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4007,
        title: '机器学习作业1',
        courseName: '机器学习基础',
        type: 'ASSIGNMENT',
        dueDate: '2024-09-25 23:59:59',
        maxScore: 100,
        submissionCount: 18,
        published: true,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      },
      {
        id: 4008,
        title: '机器学习项目',
        courseName: '机器学习基础',
        type: 'PROJECT',
        dueDate: '2024-11-01 23:59:59',
        maxScore: 100,
        submissionCount: 0,
        published: false,
        createdAt: '2024-09-01 10:00:00',
        updatedAt: '2024-09-01 10:00:00'
      }
    ]

    // 根据选择的课程过滤任务
    let filteredTasks = mockTasks
    if (selectedCourse.value) {
      const selectedCourseId = parseInt(selectedCourse.value as string)
      const courseMap: Record<number, string> = {
        1001: 'Java程序设计',
        1002: '数据结构与算法',
        1003: '机器学习基础',
        1004: '软件工程实践'
      }
      const courseName = courseMap[selectedCourseId]
      if (courseName) {
        filteredTasks = mockTasks.filter((task) => task.courseName === courseName)
      }
    }

    // 分页处理
    const startIndex = (pagination.value.current - 1) * pagination.value.size
    const endIndex = startIndex + pagination.value.size
    const pagedTasks = filteredTasks.slice(startIndex, endIndex)

    tableData.value = pagedTasks
    pagination.value.total = filteredTasks.length
    ElMessage.success(`成功加载 ${pagedTasks.length} 条任务数据（演示数据）`)

    // 正式版本的API调用（暂时注释）
    /*
    const userInfo = userStore.getUserInfo
    const rawTeacherId = userInfo?.userId
    const correctedIdStr = autoCorrectUserId(rawTeacherId || '')

    if (!correctedIdStr || correctedIdStr === '0') {
      ElMessage.error('未获取到有效的教师信息，请重新登录')
      return
    }

    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      courseId: selectedCourse.value || undefined
    }

    const res = await getTeacherTasksApi(correctedIdStr, params)
    if (res.code === 0 && res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
      pagination.value.current = res.data.current
      pagination.value.size = res.data.size
      ElMessage.success(`成功加载 ${res.data.records.length} 条任务数据`)
    } else {
      ElMessage.warning('获取任务数据失败')
      tableData.value = []
      pagination.value.total = 0
    }
    */
  } catch (error) {
    console.error('获取任务失败:', error)
    ElMessage.error('获取我的任务失败')
    tableData.value = []
    pagination.value.total = 0
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

const handlePublish = async (row: TeacherTaskVO) => {
  try {
    await publishTaskApi(row.id)
    ElMessage.success('发布成功')
    row.published = true
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败')
  }
}

const handleUnpublish = async (row: TeacherTaskVO) => {
  try {
    await unpublishTaskApi(row.id)
    ElMessage.success('取消发布成功')
    row.published = false
  } catch (error) {
    console.error('取消发布失败:', error)
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
