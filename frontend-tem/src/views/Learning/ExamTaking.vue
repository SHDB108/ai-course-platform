<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'ExamTaking'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">在线考试</h2>
      <p class="text-sm text-gray-500 mt-1">参加在线考试</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">✍️</div>
        <div class="text-xl font-semibold text-gray-600">在线考试功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供完整的在线考试体验</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
