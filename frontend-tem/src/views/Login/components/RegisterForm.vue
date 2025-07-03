<script setup lang="tsx">
import { Form, FormSchema } from '@/components/Form'
// 移除未使用的 'reactive'
import { ref, computed } from 'vue'
import { useI18n } from '@/hooks/web/useI18n'
import { useForm } from '@/hooks/web/useForm'
import { ElButton, ElRadioGroup, ElRadioButton, FormRules, ElMessage } from 'element-plus'
import { useValidator } from '@/hooks/web/useValidator'
import { registerStudentApi, registerTeacherApi } from '@/api/login'

const emit = defineEmits(['to-login'])

const { formRegister, formMethods } = useForm()
const { getElFormExpose, getFormData } = formMethods

const { t } = useI18n()
const { required } = useValidator()

const loading = ref(false)
const registerRole = ref('STUDENT')

// --- 动态校验规则 ---
const validatePass = async (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error(t('login.checkPassword') + t('common.required')))
  } else {
    const formData = await getFormData()
    if (value !== formData.password) {
      callback(new Error('两次输入的密码不一致!'))
    } else {
      callback()
    }
  }
}

const rules = computed<FormRules>(() => {
  const commonRules: FormRules = {
    username: [required()],
    password: [required()],
    check_password: [{ validator: validatePass, trigger: 'blur' }],
    name: [required('姓名不能为空')]
  }
  if (registerRole.value === 'STUDENT') {
    return {
      ...commonRules,
      stuNo: [required('学号不能为空')]
    }
  } else {
    return {
      ...commonRules,
      teacherNo: [required('教师工号不能为空')]
    }
  }
})

// --- 动态表单 Schema ---
const schema = computed<FormSchema[]>(() => {
  const commonSchema: FormSchema[] = [
    {
      field: 'username',
      label: '用户名',
      component: 'Input',
      componentProps: { placeholder: '请输入用户名' }
    },
    {
      field: 'password',
      label: '密码',
      component: 'InputPassword',
      componentProps: { placeholder: '请输入密码', strength: true }
    },
    {
      field: 'check_password',
      label: '确认密码',
      component: 'InputPassword',
      componentProps: { placeholder: '请再次输入密码' }
    },
    {
      field: 'name',
      label: '姓名',
      component: 'Input',
      componentProps: { placeholder: '请输入您的真实姓名' }
    }
  ]

  const studentSchema: FormSchema[] = [
    {
      field: 'stuNo',
      label: '学号',
      component: 'Input',
      componentProps: { placeholder: '请输入学号' }
    },
    {
      field: 'gender',
      label: '性别',
      component: 'RadioGroup',
      value: 1, // 默认值
      componentProps: {
        options: [
          { label: '男', value: 1 },
          { label: '女', value: 0 }
        ]
      }
    },
    {
      field: 'major',
      label: '专业',
      component: 'Input',
      componentProps: { placeholder: '请输入专业' }
    },
    {
      field: 'grade',
      label: '年级',
      component: 'Input',
      componentProps: { placeholder: '例如：2021级' }
    }
  ]

  const teacherSchema: FormSchema[] = [
    {
      field: 'teacherNo',
      label: '教师工号',
      component: 'Input',
      componentProps: { placeholder: '请输入教师工号' }
    },
    {
      field: 'department',
      label: '部门',
      component: 'Input',
      componentProps: { placeholder: '请输入部门' }
    },
    {
      field: 'title',
      label: '职称',
      component: 'Input',
      componentProps: { placeholder: '例如：教授' }
    }
  ]

  const contactSchema: FormSchema[] = [
    {
      field: 'phone',
      label: '手机号',
      component: 'Input',
      componentProps: { placeholder: '（选填）' }
    },
    {
      field: 'email',
      label: '邮箱',
      component: 'Input',
      componentProps: { placeholder: '（选填）' }
    }
  ]

  const baseSchema: FormSchema[] = [
    {
      field: 'title',
      colProps: { span: 24 },
      formItemProps: {
        slots: {
          default: () => (
            <h2 class="text-2xl font-bold text-center w-[100%]">{t('login.register')}</h2>
          )
        }
      }
    },
    {
      field: 'role',
      label: '注册身份',
      colProps: { span: 24 },
      value: registerRole,
      formItemProps: {
        slots: {
          default: () => (
            <ElRadioGroup v-model={registerRole.value}>
              <ElRadioButton label="STUDENT">学生</ElRadioButton>
              <ElRadioButton label="TEACHER">教师</ElRadioButton>
            </ElRadioGroup>
          )
        }
      }
    },
    ...(registerRole.value === 'STUDENT'
      ? [...commonSchema, ...studentSchema, ...contactSchema]
      : [...commonSchema, ...teacherSchema, ...contactSchema]),
    {
      field: 'register',
      colProps: { span: 24 },
      formItemProps: {
        slots: {
          default: () => (
            <>
              <div class="w-[100%] mt-20px">
                <ElButton
                  type="primary"
                  class="w-[100%]"
                  loading={loading.value}
                  onClick={handleRegister}
                >
                  {t('login.register')}
                </ElButton>
              </div>
              <div class="w-[100%] mt-15px">
                <ElButton class="w-[100%]" onClick={toLogin}>
                  {t('login.hasUser')}
                </ElButton>
              </div>
            </>
          )
        }
      }
    }
  ]

  return baseSchema
})

const toLogin = () => {
  emit('to-login')
}

const handleRegister = async () => {
  const formRef = await getElFormExpose()
  await formRef?.validate(async (valid) => {
    if (valid) {
      loading.value = true
      const formData = await getFormData()

      try {
        let res
        if (registerRole.value === 'STUDENT') {
          const studentData = {
            username: formData.username,
            password: formData.password,
            stuNo: formData.stuNo,
            name: formData.name,
            gender: formData.gender,
            major: formData.major,
            grade: formData.grade,
            phone: formData.phone,
            email: formData.email
          }
          res = await registerStudentApi(studentData)
        } else {
          const teacherData = {
            username: formData.username,
            password: formData.password,
            teacherNo: formData.teacherNo,
            name: formData.name,
            department: formData.department,
            title: formData.title,
            phone: formData.phone,
            email: formData.email
          }
          res = await registerTeacherApi(teacherData)
        }

        if (res && res.code === 0) {
          ElMessage.success('注册成功，请登录！')
          toLogin()
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <Form
    :schema="schema"
    :rules="rules"
    label-position="top"
    hide-required-asterisk
    size="large"
    class="dark:(border-1 border-[var(--el-border-color)] border-solid)"
    @register="formRegister"
  />
</template>
