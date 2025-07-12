<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Form } from '@/components/Form'
import { ElButton, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  createCourseApi,
  updateCourseApi,
  getCourseApi,
  getCourseCategoriesApi,
  type CourseCreateDTO,
  type CourseUpdateDTO,
  type CourseVO,
  type CourseCategoryVO
} from '@/api/course'
import { getTeacherOptionsApi } from '@/api/teacher'
import type { FormSchema } from '@/components/Form/src/types'

defineOptions({
  name: 'CourseForm'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const courseId = ref<string>()
const teacherOptions = ref<{ id: number; name: string; teacherNo: string; department: string }[]>(
  []
)
const categories = ref<CourseCategoryVO[]>([])

const formSchema: FormSchema[] = [
  {
    field: 'name',
    label: '课程名称',
    component: 'Input',
    componentProps: {
      placeholder: '请输入课程名称',
      maxlength: 100
    },
    rules: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
  },
  {
    field: 'description',
    label: '课程描述',
    component: 'Input',
    componentProps: {
      type: 'textarea',
      placeholder: '请输入课程描述',
      rows: 4,
      maxlength: 500
    },
    rules: [{ required: true, message: '请输入课程描述', trigger: 'blur' }]
  },
  {
    field: 'categoryId',
    label: '课程分类',
    component: 'Select',
    componentProps: {
      placeholder: '请选择课程分类',
      options: categories.value.map((category) => ({
        label: category.name,
        value: category.id
      }))
    },
    rules: [{ required: true, message: '请选择课程分类', trigger: 'change' }]
  },
  {
    field: 'credits',
    label: '学分',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入学分',
      min: 0.5,
      max: 10,
      step: 0.5,
      precision: 1
    },
    rules: [{ required: true, message: '请输入学分', trigger: 'blur' }]
  },
  {
    field: 'duration',
    label: '学时',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入学时',
      min: 1,
      max: 200
    },
    rules: [{ required: true, message: '请输入学时', trigger: 'blur' }]
  },
  {
    field: 'dateRange',
    label: '课程时间',
    component: 'DatePicker',
    componentProps: {
      type: 'daterange',
      placeholder: '请选择课程时间',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
      rangeSeparator: '至',
      startPlaceholder: '开始日期',
      endPlaceholder: '结束日期'
    },
    rules: [{ required: true, message: '请选择课程时间', trigger: 'change' }]
  },
  {
    field: 'teacherId',
    label: '授课教师',
    component: 'Select',
    componentProps: {
      placeholder: '请选择授课教师',
      filterable: true,
      options: teacherOptions.value.map((teacher) => ({
        label: `${teacher.name} (${teacher.teacherNo}) - ${teacher.department}`,
        value: teacher.id
      }))
    },
    rules: [{ required: true, message: '请选择授课教师', trigger: 'change' }]
  },
  {
    field: 'maxStudents',
    label: '最大选课人数',
    component: 'InputNumber',
    componentProps: {
      placeholder: '请输入最大选课人数',
      min: 1,
      max: 1000
    },
    rules: [{ required: true, message: '请输入最大选课人数', trigger: 'blur' }]
  },
  {
    field: 'status',
    label: '课程状态',
    component: 'Select',
    componentProps: {
      placeholder: '请选择课程状态',
      options: [
        { label: '已发布', value: 'PUBLISHED' },
        { label: '草稿', value: 'DRAFT' },
        { label: '已暂停', value: 'SUSPENDED' },
        { label: '已完成', value: 'COMPLETED' }
      ]
    },
    rules: [{ required: true, message: '请选择课程状态', trigger: 'change' }]
  }
]

const fetchCategories = async () => {
  try {
    const response = await getCourseCategoriesApi()
    if (response?.data) {
      categories.value = response.data
      // 更新表单schema中的分类选项
      const categoryField = formSchema.find((item) => item.field === 'categoryId')
      if (categoryField && categoryField.componentProps) {
        categoryField.componentProps.options = categories.value.map((category) => ({
          label: category.name,
          value: category.id
        }))
      }
    }
  } catch (error) {
    console.error('获取课程分类失败:', error)
    ElMessage.error('获取课程分类失败')
  }
}

const fetchTeachers = async () => {
  try {
    const response = await getTeacherOptionsApi()
    if (response?.data) {
      teacherOptions.value = response.data
      // 更新表单schema中的教师选项
      const teacherField = formSchema.find((item) => item.field === 'teacherId')
      if (teacherField && teacherField.componentProps) {
        teacherField.componentProps.options = teacherOptions.value.map((teacher) => ({
          label: `${teacher.name} (${teacher.teacherNo}) - ${teacher.department}`,
          value: teacher.id
        }))
      }
    }
  } catch (error) {
    console.error('获取教师列表失败:', error)
    ElMessage.error('获取教师列表失败')
  }
}

const initForm = () => {
  if (route.params.id) {
    isEdit.value = true
    courseId.value = String(route.params.id)
    fetchCourse()
  } else {
    // 设置默认值
    formRef.value?.setValues({
      status: 'DRAFT',
      credits: 1,
      duration: 16,
      maxStudents: 50
    })
  }
}

const fetchCourse = async () => {
  if (!courseId.value) return

  loading.value = true
  try {
    const res = await getCourseApi(courseId.value)
    if (res.data) {
      const course = res.data
      formRef.value?.setValues({
        ...course,
        dateRange: [course.startDate, course.endDate]
      })
    }
  } catch (error) {
    ElMessage.error('获取课程信息失败')
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
    startDate: formData.dateRange[0],
    endDate: formData.dateRange[1]
  }
  delete submitData.dateRange

  loading.value = true
  try {
    if (isEdit.value && courseId.value) {
      await updateCourseApi(courseId.value, submitData as CourseUpdateDTO)
      ElMessage.success('更新课程成功')
    } else {
      await createCourseApi(submitData as CourseCreateDTO)
      ElMessage.success('创建课程成功')
    }
    router.push('/course-admin/courses')
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新课程失败' : '创建课程失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.push('/course-admin/courses')
}

onMounted(() => {
  fetchCategories()
  fetchTeachers()
  initForm()
})
</script>

<template>
  <ContentWrap :title="isEdit ? '编辑课程' : '新建课程'">
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
