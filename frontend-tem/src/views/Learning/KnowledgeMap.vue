<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'

defineOptions({
  name: 'KnowledgeMap'
})

onMounted(() => {
  ElMessage.info('学生知识图谱功能开发中...')
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">知识图谱</h2>
      <p class="text-sm text-gray-500 mt-1">可视化展示我的知识掌握情况</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">🕸️</div>
        <div class="text-xl font-semibold text-gray-600">知识图谱功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供个性化的知识图谱可视化</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>
