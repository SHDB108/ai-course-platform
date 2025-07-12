<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElButton,
  ElMessage,
  ElRadioGroup,
  ElRadio,
  ElCard,
  ElRow,
  ElCol
} from 'element-plus'
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createUserByAdminApi, type UserCreateByAdminDTO } from '@/api/admin'

defineOptions({
  name: 'UserCreate'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref()

// 表单数据
const formData = reactive<UserCreateByAdminDTO>({
  username: '',
  password: '',
  email: '',
  name: '',
  role: 'STUDENT',
  // 学生字段
  stuNo: '',
  major: '',
  grade: '',
  gender: 1,
  // 教师字段
  teacherNo: '',
  department: '',
  title: '',
  phone: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  // 学生字段验证
  stuNo: [
    {
      required: true,
      message: '请输入学号',
      trigger: 'blur',
      when: () => formData.role === 'STUDENT'
    }
  ],
  major: [
    {
      required: true,
      message: '请输入专业',
      trigger: 'blur',
      when: () => formData.role === 'STUDENT'
    }
  ],
  grade: [
    {
      required: true,
      message: '请选择年级',
      trigger: 'change',
      when: () => formData.role === 'STUDENT'
    }
  ],
  // 教师字段验证
  teacherNo: [
    {
      required: true,
      message: '请输入教师工号',
      trigger: 'blur',
      when: () => formData.role === 'TEACHER'
    }
  ],
  department: [
    {
      required: true,
      message: '请输入部门',
      trigger: 'blur',
      when: () => formData.role === 'TEACHER'
    }
  ]
}

// 动态验证规则
const getActiveRules = () => {
  const activeRules: any = {}
  Object.keys(rules).forEach((key) => {
    const rule = rules[key as keyof typeof rules]
    if (Array.isArray(rule)) {
      const validRules = rule.filter((r) => !r.when || r.when())
      if (validRules.length > 0) {
        activeRules[key] = validRules
      }
    }
  })
  return activeRules
}

// 年级选项
const gradeOptions = [
  { label: '2020级', value: '2020级' },
  { label: '2021级', value: '2021级' },
  { label: '2022级', value: '2022级' },
  { label: '2023级', value: '2023级' },
  { label: '2024级', value: '2024级' }
]

// 职称选项
const titleOptions = [
  { label: '助教', value: '助教' },
  { label: '讲师', value: '讲师' },
  { label: '副教授', value: '副教授' },
  { label: '教授', value: '教授' }
]

// 专业选项
const majorOptions = [
  { label: '计算机科学与技术', value: '计算机科学与技术' },
  { label: '软件工程', value: '软件工程' },
  { label: '数据科学与大数据技术', value: '数据科学与大数据技术' },
  { label: '人工智能', value: '人工智能' },
  { label: '网络工程', value: '网络工程' },
  { label: '信息安全', value: '信息安全' }
]

// 部门选项
const departmentOptions = [
  { label: '计算机科学与技术系', value: '计算机科学与技术系' },
  { label: '软件工程系', value: '软件工程系' },
  { label: '数据科学系', value: '数据科学系' },
  { label: '人工智能系', value: '人工智能系' },
  { label: '网络工程系', value: '网络工程系' },
  { label: '信息安全系', value: '信息安全系' }
]

// 初始化表单
const initForm = () => {
  const roleFromQuery = route.query.role as string
  if (roleFromQuery && ['ADMIN', 'TEACHER', 'STUDENT'].includes(roleFromQuery)) {
    formData.role = roleFromQuery as any
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // 根据角色清理不需要的字段
    const submitData: UserCreateByAdminDTO = { ...formData }
    if (submitData.role !== 'STUDENT') {
      delete submitData.stuNo
      delete submitData.major
      delete submitData.grade
      delete submitData.gender
    }
    if (submitData.role !== 'TEACHER') {
      delete submitData.teacherNo
      delete submitData.department
      delete submitData.title
    }

    await createUserByAdminApi(submitData)
    ElMessage.success('用户创建成功')

    // 根据角色跳转到对应的管理页面
    if (submitData.role === 'STUDENT') {
      router.push('/admin/students')
    } else if (submitData.role === 'TEACHER') {
      router.push('/admin/teachers')
    } else {
      router.push('/admin/users')
    }
  } catch (error: any) {
    console.error('创建用户失败:', error)
    ElMessage.error(error?.message || '创建用户失败')
  } finally {
    loading.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  // 保持角色选择
  const roleFromQuery = route.query.role as string
  if (roleFromQuery && ['ADMIN', 'TEACHER', 'STUDENT'].includes(roleFromQuery)) {
    formData.role = roleFromQuery as any
  }
}

// 取消
const handleCancel = () => {
  router.back()
}

// 角色改变时清空相关字段
const handleRoleChange = () => {
  // 清空学生相关字段
  formData.stuNo = ''
  formData.major = ''
  formData.grade = ''
  formData.gender = 1

  // 清空教师相关字段
  formData.teacherNo = ''
  formData.department = ''
  formData.title = ''
}

onMounted(() => {
  initForm()
})
</script>

<template>
  <ContentWrap>
    <ElCard>
      <template #header>
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold">新建用户</h2>
          <ElButton @click="handleCancel">返回</ElButton>
        </div>
      </template>

      <ElForm
        ref="formRef"
        :model="formData"
        :rules="getActiveRules()"
        label-width="120px"
        style="max-width: 800px"
      >
        <!-- 基础信息 -->
        <ElCard class="mb-4">
          <template #header>
            <span class="font-medium">基础信息</span>
          </template>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="用户名" prop="username">
                <ElInput v-model="formData.username" placeholder="请输入用户名" clearable />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="密码" prop="password">
                <ElInput
                  v-model="formData.password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  clearable
                />
              </ElFormItem>
            </ElCol>
          </ElRow>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="姓名" prop="name">
                <ElInput v-model="formData.name" placeholder="请输入真实姓名" clearable />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="邮箱" prop="email">
                <ElInput v-model="formData.email" placeholder="请输入邮箱地址" clearable />
              </ElFormItem>
            </ElCol>
          </ElRow>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="角色" prop="role">
                <ElSelect
                  v-model="formData.role"
                  placeholder="请选择角色"
                  style="width: 100%"
                  @change="handleRoleChange"
                >
                  <ElOption label="管理员" value="ADMIN" />
                  <ElOption label="教师" value="TEACHER" />
                  <ElOption label="学生" value="STUDENT" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="手机号" prop="phone">
                <ElInput v-model="formData.phone" placeholder="请输入手机号" clearable />
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElCard>

        <!-- 学生专用信息 -->
        <ElCard v-if="formData.role === 'STUDENT'" class="mb-4">
          <template #header>
            <span class="font-medium">学生信息</span>
          </template>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="学号" prop="stuNo">
                <ElInput v-model="formData.stuNo" placeholder="请输入学号" clearable />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="性别" prop="gender">
                <ElRadioGroup v-model="formData.gender">
                  <ElRadio :value="1">男</ElRadio>
                  <ElRadio :value="0">女</ElRadio>
                </ElRadioGroup>
              </ElFormItem>
            </ElCol>
          </ElRow>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="专业" prop="major">
                <ElSelect
                  v-model="formData.major"
                  placeholder="请选择专业"
                  style="width: 100%"
                  filterable
                >
                  <ElOption
                    v-for="option in majorOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="年级" prop="grade">
                <ElSelect v-model="formData.grade" placeholder="请选择年级" style="width: 100%">
                  <ElOption
                    v-for="option in gradeOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElCard>

        <!-- 教师专用信息 -->
        <ElCard v-if="formData.role === 'TEACHER'" class="mb-4">
          <template #header>
            <span class="font-medium">教师信息</span>
          </template>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="教师工号" prop="teacherNo">
                <ElInput v-model="formData.teacherNo" placeholder="请输入教师工号" clearable />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="职称" prop="title">
                <ElSelect v-model="formData.title" placeholder="请选择职称" style="width: 100%">
                  <ElOption
                    v-for="option in titleOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>

          <ElRow :gutter="20">
            <ElCol :span="12">
              <ElFormItem label="部门" prop="department">
                <ElSelect
                  v-model="formData.department"
                  placeholder="请选择部门"
                  style="width: 100%"
                  filterable
                >
                  <ElOption
                    v-for="option in departmentOptions"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElCard>

        <!-- 提交按钮 -->
        <ElFormItem>
          <ElButton type="primary" :loading="loading" @click="handleSubmit"> 创建用户 </ElButton>
          <ElButton @click="handleReset">重置</ElButton>
          <ElButton @click="handleCancel">取消</ElButton>
        </ElFormItem>
      </ElForm>
    </ElCard>
  </ContentWrap>
</template>

<style scoped lang="less">
.text-xl {
  font-size: 1.25rem;
}

.font-bold {
  font-weight: 700;
}

.font-medium {
  font-weight: 500;
}

.mb-4 {
  margin-bottom: 1rem;
}

.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.justify-between {
  justify-content: space-between;
}
</style>
