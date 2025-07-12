<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { useI18n } from '@/hooks/web/useI18n'
import { useUserStore } from '@/store/modules/user'
import { computed, ref } from 'vue'
import { ElRow, ElCol, ElCard, ElSkeleton } from 'element-plus'
import { Icon } from '@/components/Icon'

defineOptions({
  name: 'Analysis'
})

const { t } = useI18n()
const userStore = useUserStore()

const loading = ref(true)

const username = computed(() => userStore.getUserInfo?.username || '用户')

const statCards = ref([
  { title: '我的课程', value: 12, unit: '门', icon: 'ant-design:read-filled', color: '#409eff' },
  { title: '待办任务', value: 3, unit: '个', icon: 'ant-design:bell-filled', color: '#67c23a' },
  {
    title: '本周提交',
    value: 27,
    unit: '次',
    icon: 'ant-design:check-square-filled',
    color: '#e6a23c'
  },
  { title: '未读消息', value: 8, unit: '条', icon: 'ant-design:message-filled', color: '#f56c6c' }
])

// 模拟数据加载效果
setTimeout(() => {
  loading.value = false
}, 800)
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
            <p class="text-24px font-bold">3 / 10</p>
          </div>
          <div class="text-right">
            <p class="text-14px text-gray-500">项目</p>
            <p class="text-24px font-bold">8</p>
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
