<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'

defineOptions({
  name: 'CourseStatistics'
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">课程统计</h2>
      <p class="text-sm text-gray-500 mt-1">查看我的课程数据统计和分析</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📊</div>
        <div class="text-xl font-semibold text-gray-600">课程统计功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供详细的课程数据分析</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
