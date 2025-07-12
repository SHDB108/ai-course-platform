<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElMessage, ElDialog, ElInput } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getTaskSubmissionsApi,
  gradeSubmissionApi,
  type TaskSubmissionVO,
  type TaskSubmissionGradeDTO,
  type PageVO
} from '@/api/task'

defineOptions({
  name: 'TaskSubmissions'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const aiGradeLoading = ref(false)
const tableData = ref<TaskSubmissionVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const taskId = ref<number>(Number(route.params.id))

const gradeDialogVisible = ref(false)
const currentSubmission = ref<TaskSubmissionVO>()
const gradeForm = ref({
  score: 0,
  feedback: ''
})

const aiGradeDialogVisible = ref(false)
const aiGradeForm = ref({
  gradingCriteria: '',
  rubric: ''
})
const aiGradeResult = ref()

const columns = [
  {
    field: 'studentName',
    label: '学生姓名',
    width: 120
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
    field: 'content',
    label: '提交内容',
    width: 200,
    slots: {
      default: 'content'
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
      default: 'status'
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
    width: 200,
    slots: {
      default: 'action'
    }
  }
]

const fetchSubmissions = async () => {
  loading.value = true
  try {
    const res = await getTaskSubmissionsApi(taskId.value, {
      current: pagination.value.current,
      size: pagination.value.size
    })
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取提交记录失败')
  } finally {
    loading.value = false
  }
}

const handleGrade = (row: TaskSubmissionVO) => {
  currentSubmission.value = row
  gradeForm.value = {
    score: row.score || 0,
    feedback: row.feedback || ''
  }
  gradeDialogVisible.value = true
}

const handleAiGrade = (row: TaskSubmissionVO) => {
  currentSubmission.value = row
  aiGradeForm.value = {
    gradingCriteria: '',
    rubric: ''
  }
  aiGradeResult.value = undefined
  aiGradeDialogVisible.value = true
}

const submitGrade = async () => {
  if (!currentSubmission.value) return

  try {
    await gradeSubmissionApi(currentSubmission.value.id, gradeForm.value)
    ElMessage.success('批改成功')
    gradeDialogVisible.value = false
    fetchSubmissions()
  } catch (error) {
    ElMessage.error('批改失败')
  }
}

const generateAiGrade = async () => {
  if (!currentSubmission.value) return

  aiGradeLoading.value = true
  try {
    // TODO: 实现AI批改功能
    ElMessage.info('AI批改功能开发中...')
    // const res = await intelligentGradeApi(currentSubmission.value.id, aiGradeForm.value)
    // if (res.data) {
    //   aiGradeResult.value = res.data
    // }
  } catch (error) {
    ElMessage.error('AI批改失败')
  } finally {
    aiGradeLoading.value = false
  }
}

const applyAiGrade = async () => {
  if (!currentSubmission.value || !aiGradeResult.value) return

  try {
    await gradeSubmissionApi(currentSubmission.value.id, {
      score: aiGradeResult.value.score,
      feedback: aiGradeResult.value.feedback
    })
    ElMessage.success('应用AI批改结果成功')
    aiGradeDialogVisible.value = false
    fetchSubmissions()
  } catch (error) {
    ElMessage.error('应用AI批改结果失败')
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchSubmissions()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchSubmissions()
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
  fetchSubmissions()
})
</script>

<template>
  <ContentWrap>
    <ElCard title="任务提交记录">
      <Table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      >
        <template #submissionType="{ row }">
          <ElTag type="info">
            {{ getSubmissionTypeText(row.submissionType) }}
          </ElTag>
        </template>

        <template #content="{ row }">
          <div class="max-w-200px truncate">
            {{ row.content }}
          </div>
        </template>

        <template #status="{ row }">
          <ElTag :type="getSubmissionStatusColor(row.status)">
            {{ getSubmissionStatusText(row.status) }}
          </ElTag>
        </template>

        <template #action="{ row }">
          <el-button type="primary" size="small" @click="handleGrade(row)"> 批改 </el-button>
          <el-button type="success" size="small" @click="handleAiGrade(row)"> AI批改 </el-button>
        </template>
      </Table>
    </ElCard>

    <!-- 手动批改对话框 -->
    <ElDialog v-model="gradeDialogVisible" title="批改作业" width="500px">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-2">得分</label>
          <el-input-number
            v-model="gradeForm.score"
            :min="0"
            :max="100"
            placeholder="请输入得分"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-2">反馈</label>
          <el-input
            v-model="gradeForm.feedback"
            type="textarea"
            :rows="4"
            placeholder="请输入反馈内容"
          />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="gradeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitGrade">提交</el-button>
        </div>
      </template>
    </ElDialog>

    <!-- AI批改对话框 -->
    <ElDialog v-model="aiGradeDialogVisible" title="AI智能批改" width="600px">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-2">批改标准</label>
          <el-input
            v-model="aiGradeForm.gradingCriteria"
            type="textarea"
            :rows="3"
            placeholder="请输入批改标准，例如：语法正确性、内容完整性、逻辑清晰度等"
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-2">评分细则（可选）</label>
          <el-input
            v-model="aiGradeForm.rubric"
            type="textarea"
            :rows="3"
            placeholder="请输入详细的评分细则"
          />
        </div>
        <div class="flex justify-center">
          <el-button type="primary" :loading="aiGradeLoading" @click="generateAiGrade">
            生成AI批改
          </el-button>
        </div>

        <div v-if="aiGradeResult" class="mt-4 p-4 bg-gray-50 rounded">
          <h4 class="font-semibold mb-2">AI批改结果</h4>
          <div class="space-y-2">
            <div><strong>得分：</strong>{{ aiGradeResult.score }}</div>
            <div><strong>反馈：</strong>{{ aiGradeResult.feedback }}</div>
            <div><strong>详细分析：</strong>{{ aiGradeResult.detailedAnalysis }}</div>
            <div v-if="aiGradeResult.suggestions.length > 0">
              <strong>改进建议：</strong>
              <ul class="list-disc pl-5">
                <li v-for="suggestion in aiGradeResult.suggestions" :key="suggestion">
                  {{ suggestion }}
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="aiGradeDialogVisible = false">取消</el-button>
          <el-button v-if="aiGradeResult" type="primary" @click="applyAiGrade">
            应用批改结果
          </el-button>
        </div>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
