<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Form } from '@/components/Form'
import { ElButton, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  createTaskApi,
  updateTaskApi,
  getTaskApi,
  type TaskCreateDTO,
  type TaskUpdateDTO,
  type TaskVO
} from '@/api/task'
import { getCoursesApi, type CourseVO } from '@/api/course'
import type { FormSchema } from '@/components/Form/src/types'

defineOptions({
  name: 'TaskForm'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const courseLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const taskId = ref<number>()
const courses = ref<CourseVO[]>([])

const formSchema: FormSchema[] = [
  {
    field: 'title',
    label: '任务标题',
    component: 'Input',
    componentProps: {
      placeholder: '请输入任务标题',
      maxlength: 200
    },
    rules: [{ required: true, message: '请输入任务标题', trigger: 'blur' }]
  },
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
    label: '任务类型',
    component: 'Select',
    componentProps: {
      placeholder: '请选择任务类型',
      options: [
        { label: '作业', value: 'ASSIGNMENT' },
        { label: '测验', value: 'QUIZ' },
        { label: '项目', value: 'PROJECT' },
        { label: '考试', value: 'EXAM' }
      ]
    },
    rules: [{ required: true, message: '请选择任务类型', trigger: 'change' }]
  },
  {
    field: 'description',
    label: '任务描述',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入任务描述',
      rows: 3,
      maxlength: 1000
    },
    rules: [{ required: true, message: '请输入任务描述', trigger: 'blur' }]
  },
  {
    field: 'instructions',
    label: '任务说明',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入详细的任务说明和要求',
      rows: 5,
      maxlength: 2000
    },
    rules: [{ required: true, message: '请输入任务说明', trigger: 'blur' }]
  },
  {
    field: 'dueDate',
    label: '截止时间',
    component: 'DatePicker',
    componentProps: {
      type: 'datetime',
      placeholder: '请选择截止时间',
      format: 'YYYY-MM-DD HH:mm:ss',
      valueFormat: 'YYYY-MM-DD HH:mm:ss'
    },
    rules: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
  },
  {
    field: 'maxScore',
    label: '满分',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入满分',
      min: 1,
      max: 1000,
      precision: 0
    },
    rules: [{ required: true, message: '请输入满分', trigger: 'blur' }]
  },
  {
    field: 'attachments',
    label: '附件',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入附件URL，每行一个',
      rows: 3
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
    taskId.value = Number(route.params.id)
    await fetchTask()
  } else {
    // 设置默认值
    formRef.value?.setValues({
      type: 'ASSIGNMENT',
      maxScore: 100
    })
  }
}

const fetchTask = async () => {
  if (!taskId.value) return

  loading.value = true
  try {
    const res = await getTaskApi(taskId.value)
    if (res.data) {
      const task = res.data
      formRef.value?.setValues({
        ...task,
        attachments: task.attachments?.join('\n') || ''
      })
    }
  } catch (error) {
    ElMessage.error('获取任务信息失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate()
  if (!valid) return

  const formData = formRef.value?.getValues()

  const submitData = {
    ...formData,
    attachments: formData.attachments
      ? formData.attachments.split('\n').filter((url: string) => url.trim())
      : []
  }

  loading.value = true
  try {
    if (isEdit.value && taskId.value) {
      await updateTaskApi(taskId.value, submitData as TaskUpdateDTO)
      ElMessage.success('更新任务成功')
    } else {
      await createTaskApi(submitData as TaskCreateDTO)
      ElMessage.success('创建任务成功')
    }
    router.push('/task/list')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新任务失败' : '创建任务失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/task/list')
}

onMounted(() => {
  initForm()
})
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑任务' : '新建任务'">
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
