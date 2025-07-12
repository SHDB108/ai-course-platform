<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'StudyProgress'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">学习进度</h2>
      <p class="text-sm text-gray-500 mt-1">跟踪我的学习进度和成就</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📈</div>
        <div class="text-xl font-semibold text-gray-600">学习进度功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供详细的学习进度跟踪</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
