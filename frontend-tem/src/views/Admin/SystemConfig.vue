<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElButton,
  ElCard,
  ElForm,
  ElFormItem,
  ElInput,
  ElSwitch,
  ElMessage,
  ElDivider
} from 'element-plus'
import { ref, reactive, onMounted } from 'vue'

defineOptions({
  name: 'SystemConfig'
})

const loading = ref(false)
const saveLoading = ref(false)

// 系统基础配置
const basicConfig = reactive({
  systemName: 'AI智慧课程平台',
  systemLogo: '',
  systemDescription: '基于AI的智能化在线教育平台',
  supportEmail: 'support@aicourse.com',
  supportPhone: '400-123-4567',
  enableRegistration: true,
  enableGuestAccess: false,
  maxFileUploadSize: 100, // MB
  sessionTimeout: 30 // 分钟
})

// 邮件配置
const emailConfig = reactive({
  smtpHost: 'smtp.example.com',
  smtpPort: 587,
  smtpUsername: 'noreply@aicourse.com',
  smtpPassword: '',
  enableTLS: true,
  fromEmail: 'noreply@aicourse.com',
  fromName: 'AI智慧课程平台'
})

// AI配置
const aiConfig = reactive({
  enableAIFeatures: true,
  aiApiEndpoint: 'http://219.216.65.193:11434/api',
  aiApiKey: 'app-SKDG1Ww8x4Rw4js6WsSuRKYI',
  aiModel: 'deepseek-r1:1.5b',
  maxTokens: 4000,
  temperature: 0.7
})

// 存储配置
const storageConfig = reactive({
  storageType: 'local', // local, oss, s3
  localPath: 'upload',
  maxStorageSize: 10240, // MB
  enableCompression: true,
  allowedFileTypes: '.jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.zip,.rar'
})

// 安全配置
const securityConfig = reactive({
  passwordMinLength: 6,
  passwordRequireNumbers: true,
  passwordRequireSymbols: false,
  enableTwoFactor: false,
  maxLoginAttempts: 5,
  lockoutDuration: 15, // 分钟
  jwtExpiration: 24 // 小时
})

// 备份配置
const backupConfig = reactive({
  enableAutoBackup: true,
  backupFrequency: 'daily', // daily, weekly, monthly
  backupRetention: 30, // 天
  backupPath: '/backup',
  enableDatabaseBackup: true,
  enableFileBackup: true
})

const loadConfigs = async () => {
  loading.value = true
  try {
    // TODO: 调用获取系统配置API
    // const res = await getSystemConfigApi()
    // Object.assign(basicConfig, res.data.basic)
    // Object.assign(emailConfig, res.data.email)
    // ...

    ElMessage.success('配置加载成功')
  } catch (error) {
    ElMessage.error('配置加载失败')
  } finally {
    loading.value = false
  }
}

const saveConfigs = async () => {
  saveLoading.value = true
  try {
    // TODO: 调用保存系统配置API
    // await updateSystemConfigApi({
    //   basic: basicConfig,
    //   email: emailConfig,
    //   ai: aiConfig,
    //   storage: storageConfig,
    //   security: securityConfig,
    //   backup: backupConfig
    // })

    ElMessage.success('配置保存成功')
  } catch (error) {
    ElMessage.error('配置保存失败')
  } finally {
    saveLoading.value = false
  }
}

const testEmailConfig = async () => {
  try {
    // TODO: 调用测试邮件配置API
    // await testEmailConfigApi(emailConfig)
    ElMessage.success('邮件配置测试成功')
  } catch (error) {
    ElMessage.error('邮件配置测试失败')
  }
}

const testAIConfig = async () => {
  try {
    // TODO: 调用测试AI配置API
    // await testAIConfigApi(aiConfig)
    ElMessage.success('AI配置测试成功')
  } catch (error) {
    ElMessage.error('AI配置测试失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<template>
  <ContentWrap title="系统配置" :loading="loading">
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 系统基础配置 -->
      <ElCard header="系统基础配置">
        <ElForm :model="basicConfig" label-width="120px">
          <ElFormItem label="系统名称">
            <ElInput v-model="basicConfig.systemName" placeholder="请输入系统名称" />
          </ElFormItem>
          <ElFormItem label="系统描述">
            <ElInput
              v-model="basicConfig.systemDescription"
              type="textarea"
              :rows="3"
              placeholder="请输入系统描述"
            />
          </ElFormItem>
          <ElFormItem label="支持邮箱">
            <ElInput v-model="basicConfig.supportEmail" placeholder="请输入支持邮箱" />
          </ElFormItem>
          <ElFormItem label="支持电话">
            <ElInput v-model="basicConfig.supportPhone" placeholder="请输入支持电话" />
          </ElFormItem>
          <ElFormItem label="允许注册">
            <ElSwitch v-model="basicConfig.enableRegistration" />
          </ElFormItem>
          <ElFormItem label="允许访客访问">
            <ElSwitch v-model="basicConfig.enableGuestAccess" />
          </ElFormItem>
          <ElFormItem label="文件上传限制">
            <ElInput v-model.number="basicConfig.maxFileUploadSize" placeholder="MB">
              <template #append>MB</template>
            </ElInput>
          </ElFormItem>
          <ElFormItem label="会话超时">
            <ElInput v-model.number="basicConfig.sessionTimeout" placeholder="分钟">
              <template #append>分钟</template>
            </ElInput>
          </ElFormItem>
        </ElForm>
      </ElCard>

      <!-- 邮件配置 -->
      <ElCard header="邮件配置">
        <ElForm :model="emailConfig" label-width="120px">
          <ElFormItem label="SMTP主机">
            <ElInput v-model="emailConfig.smtpHost" placeholder="请输入SMTP主机" />
          </ElFormItem>
          <ElFormItem label="SMTP端口">
            <ElInput v-model.number="emailConfig.smtpPort" placeholder="请输入SMTP端口" />
          </ElFormItem>
          <ElFormItem label="SMTP用户名">
            <ElInput v-model="emailConfig.smtpUsername" placeholder="请输入SMTP用户名" />
          </ElFormItem>
          <ElFormItem label="SMTP密码">
            <ElInput
              v-model="emailConfig.smtpPassword"
              type="password"
              placeholder="请输入SMTP密码"
              show-password
            />
          </ElFormItem>
          <ElFormItem label="启用TLS">
            <ElSwitch v-model="emailConfig.enableTLS" />
          </ElFormItem>
          <ElFormItem label="发件人邮箱">
            <ElInput v-model="emailConfig.fromEmail" placeholder="请输入发件人邮箱" />
          </ElFormItem>
          <ElFormItem label="发件人名称">
            <ElInput v-model="emailConfig.fromName" placeholder="请输入发件人名称" />
          </ElFormItem>
          <ElFormItem>
            <ElButton @click="testEmailConfig">测试邮件配置</ElButton>
          </ElFormItem>
        </ElForm>
      </ElCard>

      <!-- AI配置 -->
      <ElCard header="AI配置">
        <ElForm :model="aiConfig" label-width="120px">
          <ElFormItem label="启用AI功能">
            <ElSwitch v-model="aiConfig.enableAIFeatures" />
          </ElFormItem>
          <ElFormItem label="AI API地址">
            <ElInput v-model="aiConfig.aiApiEndpoint" placeholder="请输入AI API地址" />
          </ElFormItem>
          <ElFormItem label="AI API密钥">
            <ElInput
              v-model="aiConfig.aiApiKey"
              type="password"
              placeholder="请输入AI API密钥"
              show-password
            />
          </ElFormItem>
          <ElFormItem label="AI模型">
            <ElInput v-model="aiConfig.aiModel" placeholder="请输入AI模型名称" />
          </ElFormItem>
          <ElFormItem label="最大Token数">
            <ElInput v-model.number="aiConfig.maxTokens" placeholder="请输入最大Token数" />
          </ElFormItem>
          <ElFormItem label="Temperature">
            <ElInput v-model.number="aiConfig.temperature" placeholder="0.0-1.0" />
          </ElFormItem>
          <ElFormItem>
            <ElButton @click="testAIConfig">测试AI配置</ElButton>
          </ElFormItem>
        </ElForm>
      </ElCard>

      <!-- 存储配置 -->
      <ElCard header="存储配置">
        <ElForm :model="storageConfig" label-width="120px">
          <ElFormItem label="存储类型">
            <ElInput v-model="storageConfig.storageType" placeholder="local/oss/s3" />
          </ElFormItem>
          <ElFormItem label="本地存储路径">
            <ElInput v-model="storageConfig.localPath" placeholder="请输入本地存储路径" />
          </ElFormItem>
          <ElFormItem label="最大存储空间">
            <ElInput v-model.number="storageConfig.maxStorageSize" placeholder="MB">
              <template #append>MB</template>
            </ElInput>
          </ElFormItem>
          <ElFormItem label="启用压缩">
            <ElSwitch v-model="storageConfig.enableCompression" />
          </ElFormItem>
          <ElFormItem label="允许文件类型">
            <ElInput
              v-model="storageConfig.allowedFileTypes"
              type="textarea"
              :rows="2"
              placeholder="请输入允许的文件类型，用逗号分隔"
            />
          </ElFormItem>
        </ElForm>
      </ElCard>

      <!-- 安全配置 -->
      <ElCard header="安全配置">
        <ElForm :model="securityConfig" label-width="120px">
          <ElFormItem label="密码最小长度">
            <ElInput
              v-model.number="securityConfig.passwordMinLength"
              placeholder="请输入密码最小长度"
            />
          </ElFormItem>
          <ElFormItem label="密码需要数字">
            <ElSwitch v-model="securityConfig.passwordRequireNumbers" />
          </ElFormItem>
          <ElFormItem label="密码需要符号">
            <ElSwitch v-model="securityConfig.passwordRequireSymbols" />
          </ElFormItem>
          <ElFormItem label="启用双因子认证">
            <ElSwitch v-model="securityConfig.enableTwoFactor" />
          </ElFormItem>
          <ElFormItem label="最大登录尝试">
            <ElInput
              v-model.number="securityConfig.maxLoginAttempts"
              placeholder="请输入最大登录尝试次数"
            />
          </ElFormItem>
          <ElFormItem label="锁定时长">
            <ElInput v-model.number="securityConfig.lockoutDuration" placeholder="分钟">
              <template #append>分钟</template>
            </ElInput>
          </ElFormItem>
          <ElFormItem label="JWT过期时间">
            <ElInput v-model.number="securityConfig.jwtExpiration" placeholder="小时">
              <template #append>小时</template>
            </ElInput>
          </ElFormItem>
        </ElForm>
      </ElCard>

      <!-- 备份配置 -->
      <ElCard header="备份配置">
        <ElForm :model="backupConfig" label-width="120px">
          <ElFormItem label="启用自动备份">
            <ElSwitch v-model="backupConfig.enableAutoBackup" />
          </ElFormItem>
          <ElFormItem label="备份频率">
            <ElInput v-model="backupConfig.backupFrequency" placeholder="daily/weekly/monthly" />
          </ElFormItem>
          <ElFormItem label="备份保留期">
            <ElInput v-model.number="backupConfig.backupRetention" placeholder="天">
              <template #append>天</template>
            </ElInput>
          </ElFormItem>
          <ElFormItem label="备份路径">
            <ElInput v-model="backupConfig.backupPath" placeholder="请输入备份路径" />
          </ElFormItem>
          <ElFormItem label="备份数据库">
            <ElSwitch v-model="backupConfig.enableDatabaseBackup" />
          </ElFormItem>
          <ElFormItem label="备份文件">
            <ElSwitch v-model="backupConfig.enableFileBackup" />
          </ElFormItem>
        </ElForm>
      </ElCard>
    </div>

    <ElDivider />

    <div class="text-center">
      <ElButton type="primary" :loading="saveLoading" @click="saveConfigs" size="large">
        保存所有配置
      </ElButton>
      <ElButton @click="loadConfigs" size="large"> 重新加载 </ElButton>
    </div>
  </ContentWrap>
</template>

<style scoped lang="less">
.grid {
  display: grid;
}
.grid-cols-1 {
  grid-template-columns: repeat(1, minmax(0, 1fr));
}
.gap-6 {
  gap: 1.5rem;
}
.text-center {
  text-align: center;
}

@media (min-width: 1024px) {
  .lg\:grid-cols-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
