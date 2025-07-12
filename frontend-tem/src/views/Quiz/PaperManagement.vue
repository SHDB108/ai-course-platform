<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import {
  ElButton,
  ElTag,
  ElSelect,
  ElOption,
  ElMessageBox,
  ElMessage,
  ElDialog
} from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getPapersApi,
  deletePaperApi,
  generatePaperApi,
  type QuizPaperVO,
  type PaperGenDTO,
  type PageVO
} from '@/api/quiz'
import { getCoursesApi, type CourseVO } from '@/api/course'

defineOptions({
  name: 'PaperManagement'
})

const router = useRouter()

const loading = ref(false)
const courseLoading = ref(false)
const generateLoading = ref(false)
const tableData = ref<QuizPaperVO[]>([])
const courses = ref<CourseVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  courseId: undefined as number | undefined,
  title: ''
})

const generateDialogVisible = ref(false)
const generateForm = ref<PaperGenDTO>({
  title: '',
  description: '',
  courseId: 0,
  totalScore: 100,
  timeLimit: 60,
  questionCount: 10,
  difficultyDistribution: {
    easy: 30,
    medium: 50,
    hard: 20
  },
  knowledgePoints: [],
  questionTypes: []
})

const columns = [
  {
    field: 'title',
    label: '试卷标题',
    width: 200
  },
  {
    field: 'courseName',
    label: '所属课程',
    width: 150
  },
  {
    field: 'questionCount',
    label: '题目数量',
    width: 100
  },
  {
    field: 'totalScore',
    label: '总分',
    width: 80
  },
  {
    field: 'timeLimit',
    label: '时长(分钟)',
    width: 120
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
    width: 200,
    slots: {
      default: 'action'
    }
  }
]

const fetchPapers = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    }

    const res = await getPapersApi(params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取试卷列表失败')
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
  fetchPapers()
}

const handleReset = () => {
  searchForm.value = {
    courseId: undefined,
    title: ''
  }
  pagination.value.current = 1
  fetchPapers()
}

const handleGenerate = () => {
  generateForm.value = {
    title: '',
    description: '',
    courseId: 0,
    totalScore: 100,
    timeLimit: 60,
    questionCount: 10,
    difficultyDistribution: {
      easy: 30,
      medium: 50,
      hard: 20
    },
    knowledgePoints: [],
    questionTypes: []
  }
  generateDialogVisible.value = true
}

const handleView = (row: QuizPaperVO) => {
  router.push(`/quiz/paper/detail/${row.id}`)
}

const handleDelete = async (row: QuizPaperVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除试卷"${row.title}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deletePaperApi(row.id)
    ElMessage.success('删除成功')
    fetchPapers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitGenerate = async () => {
  generateLoading.value = true
  try {
    const res = await generatePaperApi(generateForm.value)
    if (res.data) {
      ElMessage.success('生成试卷成功')
      generateDialogVisible.value = false
      fetchPapers()
    }
  } catch (error) {
    ElMessage.error('生成试卷失败')
  } finally {
    generateLoading.value = false
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchPapers()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchPapers()
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已归档'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchCourses()
  fetchPapers()
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
            style="width: 150px"
            clearable
            :loading="courseLoading"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>

          <el-input
            v-model="searchForm.title"
            placeholder="试卷标题"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          />

          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>

        <el-button type="primary" @click="handleGenerate"> AI生成试卷 </el-button>
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
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>

    <!-- AI生成试卷对话框 -->
    <ElDialog v-model="generateDialogVisible" title="AI生成试卷" width="600px">
      <div class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-2">试卷标题</label>
            <el-input v-model="generateForm.title" placeholder="请输入试卷标题" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-2">所属课程</label>
            <el-select v-model="generateForm.courseId" placeholder="请选择课程" class="w-full">
              <el-option
                v-for="course in courses"
                :key="course.id"
                :label="course.name"
                :value="course.id"
              />
            </el-select>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">试卷描述</label>
          <el-input
            v-model="generateForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入试卷描述"
          />
        </div>

        <div class="grid grid-cols-3 gap-4">
          <div>
            <label class="block text-sm font-medium mb-2">总分</label>
            <el-input-number
              v-model="generateForm.totalScore"
              :min="1"
              :max="1000"
              class="w-full"
            />
          </div>
          <div>
            <label class="block text-sm font-medium mb-2">时长(分钟)</label>
            <el-input-number v-model="generateForm.timeLimit" :min="1" :max="300" class="w-full" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-2">题目数量</label>
            <el-input-number
              v-model="generateForm.questionCount"
              :min="1"
              :max="100"
              class="w-full"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">难度分布(%)</label>
          <div class="grid grid-cols-3 gap-4">
            <div>
              <span class="text-sm">简单</span>
              <el-input-number
                v-model="generateForm.difficultyDistribution.easy"
                :min="0"
                :max="100"
                class="w-full"
              />
            </div>
            <div>
              <span class="text-sm">中等</span>
              <el-input-number
                v-model="generateForm.difficultyDistribution.medium"
                :min="0"
                :max="100"
                class="w-full"
              />
            </div>
            <div>
              <span class="text-sm">困难</span>
              <el-input-number
                v-model="generateForm.difficultyDistribution.hard"
                :min="0"
                :max="100"
                class="w-full"
              />
            </div>
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">题型选择</label>
          <el-select
            v-model="generateForm.questionTypes"
            multiple
            placeholder="请选择题型"
            class="w-full"
          >
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="简答题" value="SHORT_ANSWER" />
            <el-option label="论述题" value="ESSAY" />
          </el-select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">知识点</label>
          <el-input v-model="generateForm.knowledgePoints" placeholder="请输入知识点，用逗号分隔" />
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="generateDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="generateLoading" @click="submitGenerate">
            生成试卷
          </el-button>
        </div>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
