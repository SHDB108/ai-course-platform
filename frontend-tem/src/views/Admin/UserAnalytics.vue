<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'

defineOptions({
  name: 'UserAnalytics'
})

const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    // TODO: 调用用户分析API
    ElMessage.success('用户分析功能开发中...')
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <ContentWrap title="用户分析" :loading="loading">
    <div class="p-6 text-center">
      <h3 class="text-xl mb-4">用户分析</h3>
      <p class="text-gray-500">此功能正在开发中...</p>
    </div>
  </ContentWrap>
</template>

<style scoped lang="less">
.text-xl {
  font-size: 1.25rem;
}
.text-gray-500 {
  color: #6b7280;
}
.mb-4 {
  margin-bottom: 1rem;
}
.p-6 {
  padding: 1.5rem;
}
.text-center {
  text-align: center;
}
</style>
