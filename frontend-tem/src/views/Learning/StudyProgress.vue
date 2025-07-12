<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElCard,
  ElSelect,
  ElOption,
  ElButton,
  ElProgress,
  ElTag,
  ElRow,
  ElCol,
  ElStatistic,
  ElMessage,
  ElSkeleton,
  ElSkeletonItem,
  ElEmpty
} from 'element-plus'
import { ref, onMounted, computed, nextTick } from 'vue'
import { getCoursesApi, type CourseVO } from '@/api/course'
import {
  getStudyProgressApi,
  getStudyAnalysisApi,
  getKnowledgePointProgressApi,
  type StudyProgressVO,
  type StudyAnalysisVO
} from '@/api/study'
import { useUserStore } from '@/store/modules/user'
import * as echarts from 'echarts'

defineOptions({
  name: 'StudyProgress'
})

const userStore = useUserStore()
const loading = ref(false)
const courses = ref<CourseVO[]>([])
const selectedCourseId = ref<number>()
const chartContainer = ref<HTMLElement>()
const radarChartContainer = ref<HTMLElement>()
const barChartContainer = ref<HTMLElement>()
let pieChart: echarts.ECharts | null = null
let radarChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

// 学习进度数据
const studyProgress = ref<StudyProgressVO | null>(null)
const studyAnalysis = ref<StudyAnalysisVO | null>(null)
const knowledgeProgress = ref<any[]>([])

const overallProgress = computed(() => {
  return studyProgress.value?.totalProgress || 0
})

const fetchCourses = async () => {
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
      if (courses.value.length > 0) {
        selectedCourseId.value = courses.value[0].id
        await loadStudyProgress()
      }
    }
  } catch (error) {
    console.error('获取课程失败:', error)
  }
}

const loadStudyProgress = async () => {
  if (!selectedCourseId.value) return

  const currentUser = userStore.getUserInfo
  if (!currentUser?.userId) {
    ElMessage.error('用户信息获取失败')
    return
  }

  loading.value = true
  try {
    // 获取学习进度
    const progressRes = await getStudyProgressApi(currentUser.userId, selectedCourseId.value)
    if (progressRes.data) {
      studyProgress.value = progressRes.data
    }

    // 获取学习分析
    try {
      const analysisRes = await getStudyAnalysisApi(currentUser.userId, selectedCourseId.value)
      if (analysisRes.data) {
        studyAnalysis.value = analysisRes.data
      }
    } catch (error) {
      console.warn('获取学习分析失败:', error)
    }

    // 获取知识点进度
    try {
      const knowledgeRes = await getKnowledgePointProgressApi(
        currentUser.userId,
        selectedCourseId.value
      )
      if (knowledgeRes.data) {
        knowledgeProgress.value = knowledgeRes.data
      }
    } catch (error) {
      console.warn('获取知识点进度失败:', error)
    }

    // 渲染图表
    await nextTick()
    await renderCharts()

    ElMessage.success('学习进度加载成功')
  } catch (error) {
    console.error('加载学习进度失败:', error)
    ElMessage.error('加载学习进度失败')

    // 显示示例数据作为fallback
    loadDemoData()
  } finally {
    loading.value = false
  }
}

const loadDemoData = () => {
  // 创建示例数据
  studyProgress.value = {
    id: 1,
    studentId: 1,
    courseId: selectedCourseId.value || 1,
    courseName: courses.value.find((c) => c.id === selectedCourseId.value)?.name || '示例课程',
    totalProgress: 75,
    status: 'ACTIVE',
    lastStudyTime: new Date().toISOString(),
    startDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString(),
    expectedEndDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
    videoProgress: { progress: 80, completed: 8, total: 10, status: 'ACTIVE' },
    taskProgress: { progress: 70, completed: 7, total: 10, status: 'ACTIVE' },
    examProgress: { progress: 60, completed: 3, total: 5, status: 'ACTIVE' },
    knowledgeProgress: { progress: 85, completed: 17, total: 20, status: 'ACTIVE' },
    studyTimeStats: {
      totalStudyTime: 1200,
      todayStudyTime: 45,
      weekStudyTime: 180,
      monthStudyTime: 720,
      averageDaily: 25.7
    },
    completionStats: {
      completedVideos: 8,
      totalVideos: 10,
      completedTasks: 7,
      totalTasks: 10,
      completedExams: 3,
      totalExams: 5,
      masteredKnowledge: 17,
      totalKnowledge: 20
    }
  }

  // 创建知识点示例数据
  knowledgeProgress.value = [
    { knowledgePointName: 'React基础', masteryLevel: 'MASTERED', masteryScore: 90, accuracy: 0.9 },
    { knowledgePointName: 'JSX语法', masteryLevel: 'MASTERED', masteryScore: 85, accuracy: 0.85 },
    { knowledgePointName: 'Hook系统', masteryLevel: 'LEARNING', masteryScore: 70, accuracy: 0.7 },
    {
      knowledgePointName: 'React Router',
      masteryLevel: 'PARTIAL',
      masteryScore: 60,
      accuracy: 0.6
    },
    {
      knowledgePointName: 'Redux状态管理',
      masteryLevel: 'LEARNING',
      masteryScore: 50,
      accuracy: 0.5
    }
  ]

  renderCharts()
}

const renderCharts = async () => {
  if (!studyProgress.value) return

  await new Promise((resolve) => setTimeout(resolve, 100))

  // 渲染模块进度饼图
  if (chartContainer.value && studyProgress.value) {
    if (pieChart) {
      pieChart.dispose()
    }
    pieChart = echarts.init(chartContainer.value)

    const pieOption = {
      title: {
        text: '学习模块进度',
        left: 'center',
        top: 20
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c}% ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'middle'
      },
      series: [
        {
          name: '模块进度',
          type: 'pie',
          radius: '50%',
          data: [
            { value: studyProgress.value.videoProgress.progress, name: '视频学习' },
            { value: studyProgress.value.taskProgress.progress, name: '任务完成' },
            { value: studyProgress.value.examProgress.progress, name: '考试测验' },
            { value: studyProgress.value.knowledgeProgress.progress, name: '知识掌握' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }

    pieChart.setOption(pieOption)
  }

  // 渲染知识点掌握雷达图
  if (radarChartContainer.value && knowledgeProgress.value.length > 0) {
    if (radarChart) {
      radarChart.dispose()
    }
    radarChart = echarts.init(radarChartContainer.value)

    const radarOption = {
      title: {
        text: '知识点掌握情况',
        left: 'center',
        top: 20
      },
      tooltip: {},
      radar: {
        indicator: knowledgeProgress.value.map((kp) => ({
          name: kp.knowledgePointName,
          max: 100
        }))
      },
      series: [
        {
          name: '掌握程度',
          type: 'radar',
          data: [
            {
              value: knowledgeProgress.value.map((kp) => kp.masteryScore),
              name: '当前水平',
              itemStyle: {
                color: '#1890ff'
              },
              areaStyle: {
                opacity: 0.3
              }
            }
          ]
        }
      ]
    }

    radarChart.setOption(radarOption)
  }

  // 渲染学习时间柱状图
  if (barChartContainer.value && studyProgress.value) {
    if (barChart) {
      barChart.dispose()
    }
    barChart = echarts.init(barChartContainer.value)

    const barOption = {
      title: {
        text: '学习时间统计',
        left: 'center',
        top: 20
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: ['今日', '本周', '本月', '总计']
      },
      yAxis: {
        type: 'value',
        name: '时间(分钟)'
      },
      series: [
        {
          data: [
            studyProgress.value.studyTimeStats.todayStudyTime,
            studyProgress.value.studyTimeStats.weekStudyTime,
            studyProgress.value.studyTimeStats.monthStudyTime,
            studyProgress.value.studyTimeStats.totalStudyTime
          ],
          type: 'bar',
          itemStyle: {
            color: '#1890ff'
          }
        }
      ]
    }

    barChart.setOption(barOption)
  }
}

const handleCourseChange = () => {
  loadStudyProgress()
}

const getCategoryColor = (progress: number) => {
  if (progress >= 90) return 'success'
  if (progress >= 70) return 'warning'
  return 'danger'
}

const formatTime = (minutes: number) => {
  if (minutes < 60) {
    return `${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    return `${hours}小时${mins > 0 ? `${mins}分钟` : ''}`
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold">学习进度</h2>
          <p class="text-sm text-gray-500 mt-1">跟踪我的学习进度和成就</p>
        </div>
        <div class="flex items-center space-x-3">
          <ElSelect
            v-model="selectedCourseId"
            placeholder="选择课程"
            @change="handleCourseChange"
            style="width: 200px"
          >
            <ElOption
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </ElSelect>
          <ElButton type="primary" @click="loadStudyProgress" :loading="loading">
            刷新数据
          </ElButton>
        </div>
      </div>
    </div>

    <ElSkeleton :loading="loading" animated>
      <template #template>
        <div class="space-y-4">
          <ElSkeletonItem variant="rect" style="height: 120px" />
          <ElSkeletonItem variant="rect" style="height: 300px" />
          <ElSkeletonItem variant="rect" style="height: 400px" />
        </div>
      </template>

      <template #default>
        <div v-if="studyProgress">
          <!-- 总体进度概览 -->
          <ElRow :gutter="16" class="mb-4">
            <ElCol :span="6">
              <ElCard>
                <ElStatistic
                  title="总体进度"
                  :value="overallProgress"
                  suffix="%"
                  :value-style="{ color: overallProgress >= 80 ? '#52c41a' : '#faad14' }"
                />
                <ElProgress
                  :percentage="overallProgress"
                  :show-text="false"
                  :stroke-width="8"
                  class="mt-2"
                />
                <div class="text-xs text-gray-500 mt-1">
                  {{ studyProgress.studyTimeStats.totalStudyTime }}分钟总时长
                </div>
              </ElCard>
            </ElCol>

            <ElCol :span="6">
              <ElCard>
                <ElStatistic
                  title="视频学习"
                  :value="studyProgress.videoProgress.progress"
                  suffix="%"
                  :value-style="{
                    color: studyProgress.videoProgress.progress >= 80 ? '#52c41a' : '#faad14'
                  }"
                />
                <ElProgress
                  :percentage="studyProgress.videoProgress.progress"
                  :show-text="false"
                  :stroke-width="8"
                  class="mt-2"
                />
                <div class="text-xs text-gray-500 mt-1">
                  {{ studyProgress.videoProgress.completed }} /
                  {{ studyProgress.videoProgress.total }} 个视频
                </div>
              </ElCard>
            </ElCol>

            <ElCol :span="6">
              <ElCard>
                <ElStatistic
                  title="任务完成"
                  :value="studyProgress.taskProgress.progress"
                  suffix="%"
                  :value-style="{
                    color: studyProgress.taskProgress.progress >= 80 ? '#52c41a' : '#faad14'
                  }"
                />
                <ElProgress
                  :percentage="studyProgress.taskProgress.progress"
                  :show-text="false"
                  :stroke-width="8"
                  class="mt-2"
                />
                <div class="text-xs text-gray-500 mt-1">
                  {{ studyProgress.taskProgress.completed }} /
                  {{ studyProgress.taskProgress.total }} 个任务
                </div>
              </ElCard>
            </ElCol>

            <ElCol :span="6">
              <ElCard>
                <ElStatistic
                  title="知识掌握"
                  :value="studyProgress.knowledgeProgress.progress"
                  suffix="%"
                  :value-style="{
                    color: studyProgress.knowledgeProgress.progress >= 80 ? '#52c41a' : '#faad14'
                  }"
                />
                <ElProgress
                  :percentage="studyProgress.knowledgeProgress.progress"
                  :show-text="false"
                  :stroke-width="8"
                  class="mt-2"
                />
                <div class="text-xs text-gray-500 mt-1">
                  {{ studyProgress.knowledgeProgress.completed }} /
                  {{ studyProgress.knowledgeProgress.total }} 个知识点
                </div>
              </ElCard>
            </ElCol>
          </ElRow>

          <!-- 学习时间统计 -->
          <ElCard class="mb-4">
            <h3 class="text-lg font-semibold mb-4">学习时间统计</h3>
            <ElRow :gutter="24">
              <ElCol :span="6">
                <div class="text-center p-4">
                  <div class="text-2xl font-bold text-blue-600">
                    {{ formatTime(studyProgress.studyTimeStats.todayStudyTime) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-2">今日学习</div>
                </div>
              </ElCol>
              <ElCol :span="6">
                <div class="text-center p-4">
                  <div class="text-2xl font-bold text-green-600">
                    {{ formatTime(studyProgress.studyTimeStats.weekStudyTime) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-2">本周学习</div>
                </div>
              </ElCol>
              <ElCol :span="6">
                <div class="text-center p-4">
                  <div class="text-2xl font-bold text-orange-600">
                    {{ formatTime(studyProgress.studyTimeStats.monthStudyTime) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-2">本月学习</div>
                </div>
              </ElCol>
              <ElCol :span="6">
                <div class="text-center p-4">
                  <div class="text-2xl font-bold text-purple-600">
                    {{ formatTime(studyProgress.studyTimeStats.totalStudyTime) }}
                  </div>
                  <div class="text-sm text-gray-500 mt-2">总计学习</div>
                </div>
              </ElCol>
            </ElRow>
            <ElRow :gutter="16" class="mt-4">
              <ElCol :span="24">
                <div ref="barChartContainer" style="width: 100%; height: 300px"></div>
              </ElCol>
            </ElRow>
          </ElCard>

          <!-- 学习模块进度 -->
          <ElCard class="mb-4">
            <h3 class="text-lg font-semibold mb-4">学习模块进度</h3>
            <ElRow :gutter="16">
              <ElCol :span="12">
                <div class="space-y-4">
                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <span class="font-medium">视频学习</span>
                      <ElTag
                        :type="getCategoryColor(studyProgress.videoProgress.progress)"
                        size="small"
                      >
                        {{ studyProgress.videoProgress.progress }}%
                      </ElTag>
                    </div>
                    <ElProgress
                      :percentage="studyProgress.videoProgress.progress"
                      :stroke-width="6"
                      :show-text="false"
                    />
                    <div class="text-xs text-gray-500 mt-1">
                      {{ studyProgress.videoProgress.completed }} /
                      {{ studyProgress.videoProgress.total }} 完成
                    </div>
                  </div>

                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <span class="font-medium">任务作业</span>
                      <ElTag
                        :type="getCategoryColor(studyProgress.taskProgress.progress)"
                        size="small"
                      >
                        {{ studyProgress.taskProgress.progress }}%
                      </ElTag>
                    </div>
                    <ElProgress
                      :percentage="studyProgress.taskProgress.progress"
                      :stroke-width="6"
                      :show-text="false"
                    />
                    <div class="text-xs text-gray-500 mt-1">
                      {{ studyProgress.taskProgress.completed }} /
                      {{ studyProgress.taskProgress.total }} 完成
                    </div>
                  </div>

                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <span class="font-medium">考试测验</span>
                      <ElTag
                        :type="getCategoryColor(studyProgress.examProgress.progress)"
                        size="small"
                      >
                        {{ studyProgress.examProgress.progress }}%
                      </ElTag>
                    </div>
                    <ElProgress
                      :percentage="studyProgress.examProgress.progress"
                      :stroke-width="6"
                      :show-text="false"
                    />
                    <div class="text-xs text-gray-500 mt-1">
                      {{ studyProgress.examProgress.completed }} /
                      {{ studyProgress.examProgress.total }} 完成
                    </div>
                  </div>

                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <span class="font-medium">知识掌握</span>
                      <ElTag
                        :type="getCategoryColor(studyProgress.knowledgeProgress.progress)"
                        size="small"
                      >
                        {{ studyProgress.knowledgeProgress.progress }}%
                      </ElTag>
                    </div>
                    <ElProgress
                      :percentage="studyProgress.knowledgeProgress.progress"
                      :stroke-width="6"
                      :show-text="false"
                    />
                    <div class="text-xs text-gray-500 mt-1">
                      {{ studyProgress.knowledgeProgress.completed }} /
                      {{ studyProgress.knowledgeProgress.total }} 掌握
                    </div>
                  </div>
                </div>
              </ElCol>
              <ElCol :span="12">
                <div ref="chartContainer" style="width: 100%; height: 300px"></div>
              </ElCol>
            </ElRow>
          </ElCard>

          <!-- 知识点掌握雷达图 -->
          <ElCard v-if="knowledgeProgress.length > 0">
            <h3 class="text-lg font-semibold mb-4">知识点掌握情况</h3>
            <ElRow :gutter="16">
              <ElCol :span="12">
                <div ref="radarChartContainer" style="width: 100%; height: 400px"></div>
              </ElCol>
              <ElCol :span="12">
                <div class="space-y-3">
                  <div
                    v-for="kp in knowledgeProgress"
                    :key="kp.knowledgePointName"
                    class="flex items-center justify-between p-3 bg-gray-50 rounded"
                  >
                    <div>
                      <div class="font-medium">{{ kp.knowledgePointName }}</div>
                      <div class="text-sm text-gray-500">
                        掌握等级:
                        <ElTag
                          :type="
                            kp.masteryLevel === 'MASTERED'
                              ? 'success'
                              : kp.masteryLevel === 'LEARNING'
                                ? 'warning'
                                : 'info'
                          "
                          size="small"
                        >
                          {{
                            kp.masteryLevel === 'MASTERED'
                              ? '已掌握'
                              : kp.masteryLevel === 'LEARNING'
                                ? '学习中'
                                : '部分掌握'
                          }}
                        </ElTag>
                      </div>
                    </div>
                    <div class="text-right">
                      <div class="text-lg font-bold">{{ kp.masteryScore }}分</div>
                      <div class="text-sm text-gray-500"
                        >正确率: {{ Math.round(kp.accuracy * 100) }}%</div
                      >
                    </div>
                  </div>
                </div>
              </ElCol>
            </ElRow>
          </ElCard>
        </div>

        <div v-else class="text-center py-20">
          <ElEmpty description="暂无学习进度数据">
            <ElButton type="primary" @click="loadStudyProgress">加载数据</ElButton>
          </ElEmpty>
        </div>
      </template>
    </ElSkeleton>
  </ContentWrap>
</template>

<style scoped lang="less">
:deep(.el-statistic__content) {
  font-size: 24px;
}

:deep(.el-statistic__title) {
  font-size: 14px;
  color: #666;
}
</style>
