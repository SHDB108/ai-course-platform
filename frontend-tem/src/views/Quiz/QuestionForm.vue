<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Form } from '@/components/Form'
import { ElButton, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  createQuestionApi,
  updateQuestionApi,
  getQuestionApi,
  type QuestionDTO,
  type QuestionVO
} from '@/api/quiz'
import { getCoursesApi, type CourseVO } from '@/api/course'
import type { FormSchema } from '@/components/Form/src/types'

defineOptions({
  name: 'QuestionForm'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const courseLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const questionId = ref<number>()
const courses = ref<CourseVO[]>([])

const formSchema: FormSchema[] = [
  {
    field: 'courseId',
    label: '所属课程',
    component: 'Select',
    componentProps: {
      placeholder: '请选择课程',
      filterable: true,
      loading: courseLoading.value,
      options: courses.value.map((course) => ({
        label: course.name,
        value: course.id
      }))
    },
    rules: [{ required: true, message: '请选择课程', trigger: 'change' }]
  },
  {
    field: 'type',
    label: '题型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择题型',
      options: [
        { label: '单选题', value: 'SINGLE_CHOICE' },
        { label: '多选题', value: 'MULTIPLE_CHOICE' },
        { label: '判断题', value: 'TRUE_FALSE' },
        { label: '简答题', value: 'SHORT_ANSWER' },
        { label: '论述题', value: 'ESSAY' }
      ]
    },
    rules: [{ required: true, message: '请选择题型', trigger: 'change' }]
  },
  {
    field: 'content',
    label: '题目内容',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入题目内容',
      rows: 4,
      maxlength: 1000
    },
    rules: [{ required: true, message: '请输入题目内容', trigger: 'blur' }]
  },
  {
    field: 'difficulty',
    label: '难度',
    component: 'Select',
    componentProps: {
      placeholder: '请选择难度',
      options: [
        { label: '简单', value: 'EASY' },
        { label: '中等', value: 'MEDIUM' },
        { label: '困难', value: 'HARD' }
      ]
    },
    rules: [{ required: true, message: '请选择难度', trigger: 'change' }]
  },
  {
    field: 'correctAnswer',
    label: '正确答案',
    component: 'Input',
    componentProps: {
      placeholder: '请输入正确答案',
      maxlength: 500
    },
    rules: [{ required: true, message: '请输入正确答案', trigger: 'blur' }]
  },
  {
    field: 'explanation',
    label: '答案解析',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入答案解析',
      rows: 3,
      maxlength: 1000
    }
  },
  {
    field: 'tags',
    label: '标签',
    component: 'Input',
    componentProps: {
      placeholder: '请输入标签，用逗号分隔',
      maxlength: 200
    }
  },
  {
    field: 'knowledgePoints',
    label: '知识点',
    component: 'Input',
    componentProps: {
      placeholder: '请输入知识点，用逗号分隔',
      maxlength: 200
    }
  }
]

const fetchCourses = async () => {
  courseLoading.value = true
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
      // 更新表单选项
      const courseField = formSchema.find((field) => field.field === 'courseId')
      if (courseField?.componentProps) {
        courseField.componentProps.options = courses.value.map((course) => ({
          label: course.name,
          value: course.id
        }))
      }
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    courseLoading.value = false
  }
}

const initForm = async () => {
  await fetchCourses()

  if (route.params.id) {
    isEdit.value = true
    questionId.value = Number(route.params.id)
    await fetchQuestion()
  } else {
    // 设置默认值
    formRef.value?.setValues({
      type: 'SINGLE_CHOICE',
      difficulty: 'MEDIUM'
    })
  }
}

const fetchQuestion = async () => {
  if (!questionId.value) return

  loading.value = true
  try {
    const res = await getQuestionApi(questionId.value)
    if (res.data) {
      const question = res.data
      formRef.value?.setValues({
        ...question,
        tags: question.tags?.join(', ') || '',
        knowledgePoints: question.knowledgePoints?.join(', ') || ''
      })
    }
  } catch (error) {
    ElMessage.error('获取题目信息失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  const formData = formRef.value?.getValues()

  const submitData: QuestionDTO = {
    ...formData,
    tags: formData.tags
      ? formData.tags
          .split(',')
          .map((tag: string) => tag.trim())
          .filter(Boolean)
      : [],
    knowledgePoints: formData.knowledgePoints
      ? formData.knowledgePoints
          .split(',')
          .map((point: string) => point.trim())
          .filter(Boolean)
      : [],
    options: [] // 暂时为空数组，可以根据需要扩展
  }

  loading.value = true
  try {
    if (isEdit.value && questionId.value) {
      await updateQuestionApi(questionId.value, submitData)
      ElMessage.success('更新题目成功')
    } else {
      await createQuestionApi(submitData)
      ElMessage.success('创建题目成功')
    }
    router.push('/quiz/questions')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新题目失败' : '创建题目失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/quiz/questions')
}

onMounted(() => {
  initForm()
})
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑题目' : '新建题目'">
    <Form
      ref="formRef"
      :schema="formSchema"
      :loading="loading"
      label-width="120px"
      class="max-w-800px"
    />

    <div class="mt-40px">
      <ElButton type="primary" :loading="loading" @click="handleSubmit">
        {{ isEdit ? '更新' : '创建' }}
      </ElButton>
      <ElButton @click="handleCancel"> 取消 </ElButton>
    </div>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
