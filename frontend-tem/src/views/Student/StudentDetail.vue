<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Descriptions } from '@/components/Descriptions'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElRow, ElCol, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getStudentApi, type StudentVO } from '@/api/student'
import { getStudentSubmissionsApi, type TaskSubmissionVO } from '@/api/task'

defineOptions({
  name: 'StudentDetail'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submissionsLoading = ref(false)
const studentInfo = ref<StudentVO>()
const submissions = ref<TaskSubmissionVO[]>([])

const studentId = ref<number>(Number(route.params.id))

const studentDescriptions = ref([
  {
    field: 'name',
    label: '学生姓名'
  },
  {
    field: 'studentId',
    label: '学号'
  },
  {
    field: 'username',
    label: '用户名'
  },
  {
    field: 'email',
    label: '邮箱'
  },
  {
    field: 'phoneNumber',
    label: '手机号'
  },
  {
    field: 'enrollmentDate',
    label: '入学日期'
  },
  {
    field: 'status',
    label: '状态',
    slots: {
      default: 'status'
    }
  },
  {
    field: 'createdAt',
    label: '创建时间'
  },
  {
    field: 'updatedAt',
    label: '更新时间'
  }
])

const submissionColumns = [
  {
    field: 'taskTitle',
    label: '任务标题',
    width: 200
  },
  {
    field: 'submissionType',
    label: '提交类型',
    width: 120,
    slots: {
      default: 'submissionType'
    }
  },
  {
    field: 'score',
    label: '得分',
    width: 100
  },
  {
    field: 'status',
    label: '状态',
    width: 120,
    slots: {
      default: 'submissionStatus'
    }
  },
  {
    field: 'submittedAt',
    label: '提交时间',
    width: 150
  },
  {
    field: 'gradedAt',
    label: '批改时间',
    width: 150
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

const fetchStudent = async () => {
  loading.value = true
  try {
    const res = await getStudentApi(studentId.value)
    if (res.data) {
      studentInfo.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取学生信息失败')
  } finally {
    loading.value = false
  }
}

const fetchSubmissions = async () => {
  submissionsLoading.value = true
  try {
    // 这里需要课程ID，暂时使用一个默认值或者从路由参数获取
    const courseId = Number(route.query.courseId) || 1
    const res = await getStudentSubmissionsApi(studentId.value, courseId)
    if (res.data) {
      submissions.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取提交记录失败')
  } finally {
    submissionsLoading.value = false
  }
}

const handleEdit = () => {
  router.push(`/student/edit/${studentId.value}`)
}

const handleViewSubmission = (row: TaskSubmissionVO) => {
  router.push(`/task/submission/${row.id}`)
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    GRADUATED: 'warning',
    SUSPENDED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '在读',
    INACTIVE: '未激活',
    GRADUATED: '已毕业',
    SUSPENDED: '休学'
  }
  return textMap[status] || status
}

const getSubmissionTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    ONLINE: '在线提交',
    FILE: '文件上传',
    LINK: '链接提交'
  }
  return textMap[type] || type
}

const getSubmissionStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    SUBMITTED: 'success',
    GRADED: 'primary',
    RETURNED: 'warning',
    LATE: 'danger'
  }
  return colorMap[status] || 'info'
}

const getSubmissionStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    SUBMITTED: '已提交',
    GRADED: '已批改',
    RETURNED: '已退回',
    LATE: '迟交'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchStudent()
  fetchSubmissions()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <ElButton type="primary" @click="handleEdit"> 编辑学生 </ElButton>
    </div>

    <ElRow :gutter="20">
      <ElCol :span="24">
        <ElCard title="学生信息" class="mb-20px">
          <Descriptions :data="studentInfo" :schema="studentDescriptions" :loading="loading">
            <template #status="{ row }">
              <ElTag :type="getStatusColor(row.status)">
                {{ getStatusText(row.status) }}
              </ElTag>
            </template>
          </Descriptions>
        </ElCard>
      </ElCol>

      <ElCol :span="24">
        <ElCard title="提交记录">
          <Table
            :columns="submissionColumns"
            :data="submissions"
            :loading="submissionsLoading"
            :pagination="false"
          >
            <template #submissionType="{ row }">
              <ElTag type="info">
                {{ getSubmissionTypeText(row.submissionType) }}
              </ElTag>
            </template>

            <template #submissionStatus="{ row }">
              <ElTag :type="getSubmissionStatusColor(row.status)">
                {{ getSubmissionStatusText(row.status) }}
              </ElTag>
            </template>

            <template #action="{ row }">
              <ElButton type="primary" size="small" @click="handleViewSubmission(row)">
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
