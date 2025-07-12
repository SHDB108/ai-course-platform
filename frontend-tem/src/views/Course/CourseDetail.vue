<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Descriptions } from '@/components/Descriptions'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElRow, ElCol, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getCourseApi,
  getCourseStudentsApi,
  type CourseVO,
  type StudentVO,
  type PageVO
} from '@/api/course'

defineOptions({
  name: 'CourseDetail'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const studentsLoading = ref(false)
const courseInfo = ref<CourseVO>()
const students = ref<StudentVO[]>([])
const studentsPagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const courseId = ref<number>(Number(route.params.id))

const courseDescriptions = ref([
  {
    field: 'name',
    label: '课程名称'
  },
  {
    field: 'description',
    label: '课程描述'
  },
  {
    field: 'teacherName',
    label: '授课教师'
  },
  {
    field: 'credits',
    label: '学分'
  },
  {
    field: 'duration',
    label: '学时'
  },
  {
    field: 'startDate',
    label: '开始时间'
  },
  {
    field: 'endDate',
    label: '结束时间'
  },
  {
    field: 'maxStudents',
    label: '最大选课人数'
  },
  {
    field: 'enrolledStudents',
    label: '已选课人数'
  },
  {
    field: 'status',
    label: '课程状态',
    slots: {
      default: 'status'
    }
  },
  {
    field: 'createdAt',
    label: '创建时间'
  }
])

const studentColumns = [
  {
    field: 'name',
    label: '学生姓名',
    width: 120
  },
  {
    field: 'studentId',
    label: '学号',
    width: 150
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'phoneNumber',
    label: '手机号',
    width: 150
  },
  {
    field: 'enrollmentDate',
    label: '入学日期',
    width: 120
  },
  {
    field: 'status',
    label: '状态',
    width: 100,
    slots: {
      default: 'studentStatus'
    }
  },
  {
    field: 'action',
    label: '操作',
    width: 150,
    slots: {
      default: 'action'
    }
  }
]

const fetchCourse = async () => {
  loading.value = true
  try {
    const res = await getCourseApi(courseId.value)
    if (res.data) {
      courseInfo.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取课程信息失败')
  } finally {
    loading.value = false
  }
}

const fetchCourseStudents = async () => {
  studentsLoading.value = true
  try {
    const res = await getCourseStudentsApi(courseId.value, {
      pageNum: studentsPagination.value.current,
      pageSize: studentsPagination.value.size
    })
    if (res.data) {
      students.value = res.data.records
      studentsPagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取选课学生列表失败')
  } finally {
    studentsLoading.value = false
  }
}

const handleEdit = () => {
  router.push(`/forms/course/edit/${courseId.value}`)
}

const handleViewTasks = () => {
  router.push(`/task/list?courseId=${courseId.value}`)
}

const handleViewAnalytics = () => {
  router.push(`/details/analytics/course/${courseId.value}`)
}

const handleStudentPageChange = (page: number) => {
  studentsPagination.value.current = page
  fetchCourseStudents()
}

const handleStudentPageSizeChange = (size: number) => {
  studentsPagination.value.size = size
  studentsPagination.value.current = 1
  fetchCourseStudents()
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

const getStudentStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    GRADUATED: 'warning',
    SUSPENDED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStudentStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '在读',
    INACTIVE: '未激活',
    GRADUATED: '已毕业',
    SUSPENDED: '休学'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchCourse()
  fetchCourseStudents()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <ElButton type="primary" @click="handleEdit"> 编辑课程 </ElButton>
      <ElButton type="success" @click="handleViewTasks"> 查看任务 </ElButton>
      <ElButton type="warning" @click="handleViewAnalytics"> 数据分析 </ElButton>
    </div>

    <ElRow :gutter="20">
      <ElCol :span="24">
        <ElCard title="课程信息" class="mb-20px">
          <Descriptions :data="courseInfo" :schema="courseDescriptions" :loading="loading">
            <template #status="{ row }">
              <ElTag :type="getStatusColor(row.status)">
                {{ getStatusText(row.status) }}
              </ElTag>
            </template>
          </Descriptions>
        </ElCard>
      </ElCol>

      <ElCol :span="24">
        <ElCard title="选课学生">
          <Table
            :columns="studentColumns"
            :data="students"
            :loading="studentsLoading"
            :pagination="studentsPagination"
            @page-change="handleStudentPageChange"
            @page-size-change="handleStudentPageSizeChange"
          >
            <template #studentStatus="{ row }">
              <ElTag :type="getStudentStatusColor(row.status)">
                {{ getStudentStatusText(row.status) }}
              </ElTag>
            </template>

            <template #action="{ row }">
              <ElButton
                type="primary"
                size="small"
                @click="router.push(`/details/student/${row.id}`)"
              >
                查看详情
              </ElButton>
            </template>
          </Table>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
