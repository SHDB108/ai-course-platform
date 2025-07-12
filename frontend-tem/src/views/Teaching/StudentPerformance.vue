<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'StudentPerformance'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">学生表现</h2>
      <p class="text-sm text-gray-500 mt-1">分析我的学生的学习表现和成绩</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">🎯</div>
        <div class="text-xl font-semibold text-gray-600">学生表现分析功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供深入的学生表现分析</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
