<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getQuestionsApi,
  deleteQuestionApi,
  importQuestionsApi,
  exportQuestionsApi,
  type QuestionVO,
  type PageVO
} from '@/api/quiz'
import { getCoursesApi, type CourseVO } from '@/api/course'

defineOptions({
  name: 'QuestionBank'
})

const router = useRouter()

const loading = ref(false)
const courseLoading = ref(false)
const tableData = ref<QuestionVO[]>([])
const courses = ref<CourseVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  courseId: undefined as number | undefined,
  type: '',
  difficulty: '',
  keyword: ''
})

const columns = [
  {
    field: 'content',
    label: '题目内容',
    width: 300,
    slots: {
      default: 'content'
    }
  },
  {
    field: 'type',
    label: '题型',
    width: 120,
    slots: {
      default: 'type'
    }
  },
  {
    field: 'difficulty',
    label: '难度',
    width: 100,
    slots: {
      default: 'difficulty'
    }
  },
  {
    field: 'courseName',
    label: '所属课程',
    width: 150
  },
  {
    field: 'tags',
    label: '标签',
    width: 200,
    slots: {
      default: 'tags'
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

const fetchQuestions = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    }

    const res = await getQuestionsApi(params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取题目列表失败')
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
  fetchQuestions()
}

const handleReset = () => {
  searchForm.value = {
    courseId: undefined,
    type: '',
    difficulty: '',
    keyword: ''
  }
  pagination.value.current = 1
  fetchQuestions()
}

const handleCreate = () => {
  router.push('/quiz/question/create')
}

const handleEdit = (row: QuestionVO) => {
  router.push(`/quiz/question/edit/${row.id}`)
}

const handleDelete = async (row: QuestionVO) => {
  try {
    await ElMessageBox.confirm('确定要删除这道题目吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteQuestionApi(row.id)
    ElMessage.success('删除成功')
    fetchQuestions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImport = async (file: File) => {
  try {
    await importQuestionsApi(file)
    ElMessage.success('导入成功')
    fetchQuestions()
  } catch (error) {
    ElMessage.error('导入失败')
  }
}

const handleExport = async () => {
  try {
    const blob = await exportQuestionsApi()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `questions_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchQuestions()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchQuestions()
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    SINGLE_CHOICE: 'primary',
    MULTIPLE_CHOICE: 'success',
    TRUE_FALSE: 'info',
    SHORT_ANSWER: 'warning',
    ESSAY: 'danger'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    SINGLE_CHOICE: '单选题',
    MULTIPLE_CHOICE: '多选题',
    TRUE_FALSE: '判断题',
    SHORT_ANSWER: '简答题',
    ESSAY: '论述题'
  }
  return textMap[type] || type
}

const getDifficultyColor = (difficulty: string) => {
  const colorMap: Record<string, string> = {
    EASY: 'success',
    MEDIUM: 'warning',
    HARD: 'danger'
  }
  return colorMap[difficulty] || 'info'
}

const getDifficultyText = (difficulty: string) => {
  const textMap: Record<string, string> = {
    EASY: '简单',
    MEDIUM: '中等',
    HARD: '困难'
  }
  return textMap[difficulty] || difficulty
}

onMounted(() => {
  fetchCourses()
  fetchQuestions()
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

          <el-select v-model="searchForm.type" placeholder="题型" style="width: 120px" clearable>
            <el-option label="单选题" value="SINGLE_CHOICE" />
            <el-option label="多选题" value="MULTIPLE_CHOICE" />
            <el-option label="判断题" value="TRUE_FALSE" />
            <el-option label="简答题" value="SHORT_ANSWER" />
            <el-option label="论述题" value="ESSAY" />
          </el-select>

          <el-select
            v-model="searchForm.difficulty"
            placeholder="难度"
            style="width: 100px"
            clearable
          >
            <el-option label="简单" value="EASY" />
            <el-option label="中等" value="MEDIUM" />
            <el-option label="困难" value="HARD" />
          </el-select>

          <el-input
            v-model="searchForm.keyword"
            placeholder="关键词搜索"
            style="width: 200px"
            clearable
            @keyup.enter="handleSearch"
          />

          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>

        <div class="flex items-center space-x-2">
          <el-upload
            :show-file-list="false"
            :auto-upload="true"
            accept=".xlsx,.xls,.csv"
            :on-change="(file) => handleImport(file.raw)"
          >
            <el-button type="info">导入题目</el-button>
          </el-upload>
          <el-button type="warning" @click="handleExport"> 导出题目 </el-button>
          <el-button type="primary" @click="handleCreate"> 新建题目 </el-button>
        </div>
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
      <template #content="{ row }">
        <div class="max-w-300px">
          <div class="truncate" :title="row.content">
            {{ row.content }}
          </div>
        </div>
      </template>

      <template #type="{ row }">
        <ElTag :type="getTypeColor(row.type)">
          {{ getTypeText(row.type) }}
        </ElTag>
      </template>

      <template #difficulty="{ row }">
        <ElTag :type="getDifficultyColor(row.difficulty)">
          {{ getDifficultyText(row.difficulty) }}
        </ElTag>
      </template>

      <template #tags="{ row }">
        <div class="flex flex-wrap gap-1">
          <ElTag v-for="tag in row.tags" :key="tag" size="small" type="info">
            {{ tag }}
          </ElTag>
        </div>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
