<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'AITools'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">AI教学工具</h2>
      <p class="text-sm text-gray-500 mt-1">使用AI技术辅助教学工作</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">🤖</div>
        <div class="text-xl font-semibold text-gray-600">AI教学工具开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供强大的AI教学辅助功能</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
