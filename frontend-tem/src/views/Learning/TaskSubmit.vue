<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'TaskSubmit'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">提交任务</h2>
      <p class="text-sm text-gray-500 mt-1">完成并提交我的课程任务</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📤</div>
        <div class="text-xl font-semibold text-gray-600">任务提交功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供便捷的任务提交界面</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
