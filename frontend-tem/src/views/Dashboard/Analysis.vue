<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { useUserStore } from '@/store/modules/user'
import { computed, ref, onMounted } from 'vue'
import { ElRow, ElCol, ElCard, ElSkeleton, ElMessage } from 'element-plus'
import { Icon } from '@/components/Icon'
import { getStudentDashboardStatsApi, type StudentDashboardStats } from '@/api/student'
import { safeUserIdToString, autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'Analysis'
})

const { t } = useI18n()
const userStore = useUserStore()

const loading = ref(true)
const dashboardStats = ref<StudentDashboardStats>({
  myCourses: 0,
  pendingTasks: 0,
  weeklySubmissions: 0,
  unreadMessages: 0,
  todoItems: { pending: 0, total: 0 },
  projects: 0
})

const username = computed(() => userStore.getUserInfo?.username || '用户')

const statCards = computed(() => [
  {
    title: '我的课程',
    value: dashboardStats.value.myCourses,
    unit: '门',
    icon: 'ant-design:read-filled',
    color: '#409eff'
  },
  {
    title: '待办任务',
    value: dashboardStats.value.pendingTasks,
    unit: '个',
    icon: 'ant-design:bell-filled',
    color: '#67c23a'
  },
  {
    title: '本周提交',
    value: dashboardStats.value.weeklySubmissions,
    unit: '次',
    icon: 'ant-design:check-square-filled',
    color: '#e6a23c'
  },
  {
    title: '未读消息',
    value: dashboardStats.value.unreadMessages,
    unit: '条',
    icon: 'ant-design:message-filled',
    color: '#f56c6c'
  }
])

const fetchDashboardStats = async () => {
  loading.value = true
  try {
    // 获取并修正用户ID
    const userInfo = userStore.getUserInfo
    const rawStudentId = userInfo?.userId
    const correctedIdStr = autoCorrectUserId(rawStudentId || '')

    if (!correctedIdStr || correctedIdStr === '0') {
      ElMessage.error('未获取到有效的学生信息，请重新登录')
      return
    }

    const res = await getStudentDashboardStatsApi(correctedIdStr)
    if (res.code === 0 && res.data) {
      dashboardStats.value = res.data
    } else {
      ElMessage.warning('获取仪表板数据失败，显示默认数据')
    }
  } catch (error) {
    console.error('获取仪表板统计数据失败:', error)
    ElMessage.warning('获取仪表板数据失败，显示默认数据')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardStats()
})
</script>

<template>
  <div>
    <el-card shadow="never" class="mb-20px">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-20px font-bold">你好，{{ username }}！</h2>
          <p class="text-14px text-gray-500 mt-5px">{{ t('workplace.happyDay') }}</p>
        </div>
        <div class="flex items-center">
          <div class="text-right mr-20px">
            <p class="text-14px text-gray-500">待办事项</p>
            <p class="text-24px font-bold"
              >{{ dashboardStats.todoItems.pending }} / {{ dashboardStats.todoItems.total }}</p
            >
          </div>
          <div class="text-right">
            <p class="text-14px text-gray-500">项目</p>
            <p class="text-24px font-bold">{{ dashboardStats.projects }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <ContentWrap>
      <ElRow :gutter="20">
        <ElCol
          v-for="(card, index) in statCards"
          :key="index"
          :xs="24"
          :sm="12"
          :md="12"
          :lg="6"
          class="mb-20px"
        >
          <ElCard shadow="hover">
            <ElSkeleton :loading="loading" animated>
              <template #template>
                <div class="flex items-center">
                  <ElSkeleton :rows="1" style="width: 50%" />
                  <ElSkeleton :rows="1" style="width: 25%; margin-left: auto" />
                </div>
              </template>
              <template #default>
                <div class="flex justify-between items-center">
                  <div class="text-16px text-gray-600">{{ card.title }}</div>
                  <Icon :icon="card.icon" :size="28" :color="card.color" />
                </div>
                <div class="text-32px font-bold mt-10px">
                  <span>{{ card.value }}</span>
                  <span class="text-14px ml-5px">{{ card.unit }}</span>
                </div>
              </template>
            </ElSkeleton>
          </ElCard>
        </ElCol>
      </ElRow>
    </ContentWrap>
  </div>
</template>

<style scoped lang="less"></style>
