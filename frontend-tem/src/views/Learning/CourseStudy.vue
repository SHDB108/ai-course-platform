<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'CourseStudy'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">课程学习</h2>
      <p class="text-sm text-gray-500 mt-1">进入课程学习模式</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📚</div>
        <div class="text-xl font-semibold text-gray-600">课程学习功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供沉浸式的学习体验</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
