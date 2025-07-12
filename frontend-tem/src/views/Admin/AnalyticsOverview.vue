<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { ElCard, ElRow, ElCol, ElStatistic, ElProgress, ElTag } from 'element-plus'
import { ref, onMounted } from 'vue'
import { getAnalyticsOverviewApi } from '@/api/analytics'
import { getSystemHealthApi } from '@/api/admin'

defineOptions({
  name: 'AnalyticsOverview'
})

const loading = ref(false)

// 统计数据
const stats = ref({
  totalUsers: 0,
  totalCourses: 0,
  totalEnrollments: 0,
  activeUsers: 0,
  newUsersThisMonth: 0,
  newCoursesThisMonth: 0,
  completionRate: 0,
  avgRating: 0
})

// 用户分布数据
const userDistribution = ref({
  students: 0,
  teachers: 0,
  admins: 0
})

// 课程统计数据
const courseStats = ref({
  published: 0,
  draft: 0,
  suspended: 0,
  totalLessons: 0,
  totalAssignments: 0,
  totalQuizzes: 0
})

// 最近活动数据
const recentActivities = ref([])

// 热门课程数据
const popularCourses = ref([])

// 系统健康状态
const systemHealth = ref({
  cpu: 0,
  memory: 0,
  disk: 0,
  database: 'healthy',
  api: 'healthy',
  storage: 'healthy'
})

const loadAnalyticsData = async () => {
  loading.value = true
  try {
    // 获取分析数据
    const analyticsResponse = await getAnalyticsOverviewApi()
    if (analyticsResponse?.data) {
      const data = analyticsResponse.data

      // 更新基础统计数据
      stats.value = {
        totalUsers: data.totalUsers || 0,
        totalCourses: data.totalCourses || 0,
        totalEnrollments: data.totalEnrollments || 0,
        activeUsers: data.activeUsers || 0,
        newUsersThisMonth: data.newUsersThisMonth || 0,
        newCoursesThisMonth: data.newCoursesThisMonth || 0,
        completionRate: data.completionRate || 0,
        avgRating: data.avgRating || 0
      }

      // 更新用户分布数据
      userDistribution.value = {
        students: data.userDistribution?.students || 0,
        teachers: data.userDistribution?.teachers || 0,
        admins: data.userDistribution?.admins || 0
      }

      // 更新课程统计数据
      courseStats.value = {
        published: data.courseStats?.published || 0,
        draft: data.courseStats?.draft || 0,
        suspended: data.courseStats?.suspended || 0,
        totalLessons: data.courseStats?.totalLessons || 0,
        totalAssignments: data.courseStats?.totalAssignments || 0,
        totalQuizzes: data.courseStats?.totalQuizzes || 0
      }

      // 更新最近活动数据
      recentActivities.value = data.recentActivities || []

      // 更新热门课程数据
      popularCourses.value = data.popularCourses || []
    }

    // 获取系统健康状态
    const healthResponse = await getSystemHealthApi()
    if (healthResponse?.data) {
      systemHealth.value = {
        cpu: healthResponse.data.cpuUsage || 0,
        memory: healthResponse.data.memoryUsage || 0,
        disk: healthResponse.data.diskUsage || 0,
        database: healthResponse.data.databaseStatus || 'unknown',
        api: 'healthy', // API正常运行才能获取到数据
        storage: 'healthy' // 默认存储状态
      }
    }
  } catch (error: any) {
    console.error('加载分析数据失败:', error)
    // 如果API调用失败，可以选择显示错误信息或使用默认数据
    // 这里我们保持空数据状态，让用户知道数据加载失败
  } finally {
    loading.value = false
  }
}

const getActivityIcon = (type: string) => {
  const iconMap = {
    user_register: '👤',
    course_publish: '📚',
    assignment_submit: '📝',
    user_login: '🔐'
  }
  return iconMap[type] || '📊'
}

const getHealthColor = (status: string) => {
  const colorMap = {
    healthy: 'success',
    warning: 'warning',
    error: 'danger'
  }
  return colorMap[status] || 'info'
}

const getHealthText = (status: string) => {
  const textMap = {
    healthy: '正常',
    warning: '警告',
    error: '异常'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadAnalyticsData()
})
</script>

<template>
  <ContentWrap title="数据概览" :loading="loading">
    <!-- 核心统计指标 -->
    <ElRow :gutter="16" class="mb-6">
      <ElCol :span="6">
        <ElCard>
          <ElStatistic title="总用户数" :value="stats.totalUsers" :precision="0">
            <template #suffix>
              <span class="text-sm text-gray-500">人</span>
            </template>
          </ElStatistic>
          <div class="mt-2 text-sm text-green-600"> +{{ stats.newUsersThisMonth }} 本月新增 </div>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <ElStatistic title="总课程数" :value="stats.totalCourses" :precision="0">
            <template #suffix>
              <span class="text-sm text-gray-500">门</span>
            </template>
          </ElStatistic>
          <div class="mt-2 text-sm text-green-600"> +{{ stats.newCoursesThisMonth }} 本月新增 </div>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <ElStatistic title="总选课数" :value="stats.totalEnrollments" :precision="0">
            <template #suffix>
              <span class="text-sm text-gray-500">次</span>
            </template>
          </ElStatistic>
          <div class="mt-2 text-sm text-blue-600"> 活跃用户: {{ stats.activeUsers }} </div>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <ElStatistic title="完成率" :value="stats.completionRate" :precision="1">
            <template #suffix>
              <span class="text-sm text-gray-500">%</span>
            </template>
          </ElStatistic>
          <div class="mt-2 text-sm text-orange-600"> 平均评分: {{ stats.avgRating }}/5.0 </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="16" class="mb-6">
      <!-- 用户分布 -->
      <ElCol :span="8">
        <ElCard header="用户分布">
          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <span>学生</span>
              <div class="flex items-center">
                <ElProgress
                  :percentage="Math.round((userDistribution.students / stats.totalUsers) * 100)"
                  :show-text="false"
                  style="width: 100px"
                />
                <span class="ml-2 text-sm">{{ userDistribution.students }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center">
              <span>教师</span>
              <div class="flex items-center">
                <ElProgress
                  :percentage="Math.round((userDistribution.teachers / stats.totalUsers) * 100)"
                  :show-text="false"
                  style="width: 100px"
                />
                <span class="ml-2 text-sm">{{ userDistribution.teachers }}</span>
              </div>
            </div>
            <div class="flex justify-between items-center">
              <span>管理员</span>
              <div class="flex items-center">
                <ElProgress
                  :percentage="Math.round((userDistribution.admins / stats.totalUsers) * 100)"
                  :show-text="false"
                  style="width: 100px"
                />
                <span class="ml-2 text-sm">{{ userDistribution.admins }}</span>
              </div>
            </div>
          </div>
        </ElCard>
      </ElCol>

      <!-- 课程统计 -->
      <ElCol :span="8">
        <ElCard header="课程统计">
          <div class="grid grid-cols-2 gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">{{ courseStats.published }}</div>
              <div class="text-sm text-gray-500">已发布</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-yellow-600">{{ courseStats.draft }}</div>
              <div class="text-sm text-gray-500">草稿</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-red-600">{{ courseStats.suspended }}</div>
              <div class="text-sm text-gray-500">已暂停</div>
            </div>
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ courseStats.totalLessons }}</div>
              <div class="text-sm text-gray-500">总课时</div>
            </div>
          </div>
        </ElCard>
      </ElCol>

      <!-- 系统健康状态 -->
      <ElCol :span="8">
        <ElCard header="系统健康">
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span>CPU使用率</span>
              <ElProgress :percentage="systemHealth.cpu" :width="80" type="circle" />
            </div>
            <div class="flex justify-between items-center">
              <span>内存使用率</span>
              <ElProgress :percentage="systemHealth.memory" :width="80" type="circle" />
            </div>
            <div class="flex justify-between items-center">
              <span>磁盘使用率</span>
              <ElProgress :percentage="systemHealth.disk" :width="80" type="circle" />
            </div>
            <div class="flex justify-between items-center">
              <span>服务状态</span>
              <div class="space-x-1">
                <ElTag :type="getHealthColor(systemHealth.database)" size="small">
                  DB: {{ getHealthText(systemHealth.database) }}
                </ElTag>
                <ElTag :type="getHealthColor(systemHealth.api)" size="small">
                  API: {{ getHealthText(systemHealth.api) }}
                </ElTag>
              </div>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <ElRow :gutter="16">
      <!-- 最近活动 -->
      <ElCol :span="12">
        <ElCard header="最近活动">
          <div class="space-y-3">
            <div
              v-for="activity in recentActivities"
              :key="activity.id"
              class="flex items-center justify-between p-2 rounded hover:bg-gray-50"
            >
              <div class="flex items-center">
                <span class="mr-3 text-lg">{{ getActivityIcon(activity.type) }}</span>
                <span class="text-sm">{{ activity.description }}</span>
              </div>
              <span class="text-xs text-gray-400">{{ activity.time }}</span>
            </div>
          </div>
        </ElCard>
      </ElCol>

      <!-- 热门课程 -->
      <ElCol :span="12">
        <ElCard header="热门课程">
          <div class="space-y-3">
            <div
              v-for="course in popularCourses"
              :key="course.id"
              class="flex items-center justify-between p-2 rounded hover:bg-gray-50"
            >
              <div>
                <div class="font-medium">{{ course.name }}</div>
                <div class="text-sm text-gray-500">
                  {{ course.enrollments }}人选课 · 评分{{ course.rating }}
                </div>
              </div>
              <div class="text-right">
                <div class="text-sm font-medium">{{ course.completion }}%</div>
                <div class="text-xs text-gray-400">完成率</div>
              </div>
            </div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>

<style scoped lang="less">
.space-y-3 > * + * {
  margin-top: 0.75rem;
}
.space-y-4 > * + * {
  margin-top: 1rem;
}
.space-x-1 > * + * {
  margin-left: 0.25rem;
}
.grid {
  display: grid;
}
.grid-cols-2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}
.gap-4 {
  gap: 1rem;
}
.text-center {
  text-align: center;
}
.text-sm {
  font-size: 0.875rem;
}
.text-xs {
  font-size: 0.75rem;
}
.text-2xl {
  font-size: 1.5rem;
}
.font-bold {
  font-weight: 700;
}
.font-medium {
  font-weight: 500;
}
.text-green-600 {
  color: #059669;
}
.text-blue-600 {
  color: #2563eb;
}
.text-orange-600 {
  color: #ea580c;
}
.text-yellow-600 {
  color: #d97706;
}
.text-red-600 {
  color: #dc2626;
}
.text-gray-400 {
  color: #9ca3af;
}
.text-gray-500 {
  color: #6b7280;
}
.hover\:bg-gray-50:hover {
  background-color: #f9fafb;
}
.rounded {
  border-radius: 0.25rem;
}
.mb-6 {
  margin-bottom: 1.5rem;
}
.mt-2 {
  margin-top: 0.5rem;
}
.ml-2 {
  margin-left: 0.5rem;
}
.mr-3 {
  margin-right: 0.75rem;
}
.p-2 {
  padding: 0.5rem;
}
.text-right {
  text-align: right;
}
</style>
