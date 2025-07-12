<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'StudyPlan'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">学习计划</h2>
      <p class="text-sm text-gray-500 mt-1">制定和管理我的个人学习计划</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📅</div>
        <div class="text-xl font-semibold text-gray-600">学习计划功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供智能的学习计划制定工具</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
