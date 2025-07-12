<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElRow, ElCol, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'

defineOptions({
  name: 'SystemAnalytics'
})

const loading = ref(false)
const systemStats = ref({
  totalUsers: 0,
  totalCourses: 0,
  totalTasks: 0,
  totalSubmissions: 0,
  activeUsers: 0,
  activeCourses: 0
})

const fetchSystemStats = async () => {
  loading.value = true
  try {
    // TODO: 调用系统统计API
    // const res = await getSystemStatsApi()
    // if (res.data) {
    //   systemStats.value = res.data
    // }

    // 模拟数据
    systemStats.value = {
      totalUsers: 1234,
      totalCourses: 89,
      totalTasks: 456,
      totalSubmissions: 3210,
      activeUsers: 987,
      activeCourses: 67
    }
  } catch (error) {
    ElMessage.error('获取系统统计失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchSystemStats()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <h2 class="text-xl font-bold">系统总览</h2>
      <p class="text-sm text-gray-500 mt-1">AI智慧课程平台运营数据统计</p>
    </div>

    <ElRow :gutter="20">
      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-blue-600">{{ systemStats.totalUsers }}</div>
              <div class="text-sm text-gray-600 mt-1">总用户数</div>
            </div>
            <div class="text-4xl text-blue-500"> 👥 </div>
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-green-600">{{ systemStats.totalCourses }}</div>
              <div class="text-sm text-gray-600 mt-1">总课程数</div>
            </div>
            <div class="text-4xl text-green-500"> 📚 </div>
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-orange-600">{{ systemStats.totalTasks }}</div>
              <div class="text-sm text-gray-600 mt-1">总任务数</div>
            </div>
            <div class="text-4xl text-orange-500"> 📝 </div>
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-purple-600">{{
                systemStats.totalSubmissions
              }}</div>
              <div class="text-sm text-gray-600 mt-1">总提交数</div>
            </div>
            <div class="text-4xl text-purple-500"> 📤 </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="20" class="mt-20px">
      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-red-600">{{ systemStats.activeUsers }}</div>
              <div class="text-sm text-gray-600 mt-1">活跃用户</div>
            </div>
            <div class="text-4xl text-red-500"> 🔥 </div>
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="6">
        <ElCard shadow="hover" class="stat-card">
          <div class="flex items-center justify-between">
            <div>
              <div class="text-2xl font-bold text-cyan-600">{{ systemStats.activeCourses }}</div>
              <div class="text-sm text-gray-600 mt-1">活跃课程</div>
            </div>
            <div class="text-4xl text-cyan-500"> ⭐ </div>
          </div>
        </ElCard>
      </ElCol>

      <ElCol :span="12">
        <ElCard shadow="hover" title="系统健康状态">
          <div class="text-center py-20">
            <div class="text-6xl mb-4">✅</div>
            <div class="text-xl font-semibold text-green-600">系统运行正常</div>
            <div class="text-sm text-gray-500 mt-2">所有服务正常运行中</div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>

<style scoped lang="less">
.stat-card {
  transition: transform 0.2s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
  }
}
</style>
