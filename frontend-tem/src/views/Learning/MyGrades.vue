<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'

defineOptions({
  name: 'MyGrades'
})

const loading = ref(false)

onMounted(() => {
  ElMessage.info('成绩模块开发中...')
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">我的成绩</h2>
      <p class="text-sm text-gray-500 mt-1">查看我所有课程的成绩和表现</p>
    </div>

    <ElCard>
      <div class="text-center py-20">
        <div class="text-6xl mb-4">📊</div>
        <div class="text-xl font-semibold text-gray-600">成绩功能开发中</div>
        <div class="text-sm text-gray-500 mt-2">即将为您提供详细的成绩分析和统计</div>
      </div>
    </ElCard>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
