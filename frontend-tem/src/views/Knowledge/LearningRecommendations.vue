<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElCard,
  ElButton,
  ElTag,
  ElSelect,
  ElOption,
  ElDialog,
  ElMessage,
  ElEmpty,
  ElInputNumber,
  ElCheckbox
} from 'element-plus'
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import {
  getLearningRecommendationsApi,
  generateRecommendationsApi,
  updateRecommendationStatusApi,
  type LearningRecommendationVO,
  type RecommendationGenerationRequestDTO,
  type RecommendationStatusUpdateDTO
} from '@/api/knowledge'
import { getCoursesApi, type CourseVO } from '@/api/course'

defineOptions({
  name: 'LearningRecommendations'
})

const userStore = useUserStore()

const loading = ref(false)
const generateLoading = ref(false)
const courseLoading = ref(false)
const recommendations = ref<LearningRecommendationVO[]>([])
const courses = ref<CourseVO[]>([])

const generateDialogVisible = ref(false)
const generateForm = ref<RecommendationGenerationRequestDTO>({
  studentId: 0,
  courseId: 0,
  analysisType: 'COMPREHENSIVE',
  includeWeakAreas: true,
  includeAdvancedTopics: false,
  maxRecommendations: 10,
  timeConstraint: undefined
})

const fetchRecommendations = async () => {
  loading.value = true
  try {
    const res = await getLearningRecommendationsApi()
    if (res.data) {
      recommendations.value = res.data
    }
  } catch (error) {
    console.error('获取学习推荐失败:', error)
    ElMessage.warning('后端服务暂时不可用，使用模拟数据')
    // 使用模拟数据
    recommendations.value = [
      {
        id: 1,
        title: 'Vue 3 组合式 API 学习',
        description: '建议学习Vue 3的Composition API，提升前端开发技能',
        type: 'COURSE',
        priority: 'HIGH',
        status: 'PENDING',
        reason: '基于您的学习进度和表现，推荐该课程',
        estimatedDuration: 120,
        difficultyLevel: 'INTERMEDIATE',
        courseId: 1,
        taskId: null,
        topicId: null,
        createdAt: new Date().toISOString()
      },
      {
        id: 2,
        title: 'TypeScript 基础学习',
        description: '学习TypeScript的基础语法和类型系统',
        type: 'TASK',
        priority: 'MEDIUM',
        status: 'PENDING',
        reason: '为提升代码质量和开发效率',
        estimatedDuration: 90,
        difficultyLevel: 'BEGINNER',
        courseId: null,
        taskId: 2,
        topicId: null,
        createdAt: new Date().toISOString()
      }
    ]
  } finally {
    loading.value = false
  }
}

const fetchCourses = async () => {
  courseLoading.value = true
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    courseLoading.value = false
  }
}

const handleGenerate = () => {
  const currentUser = userStore.getUserInfo
  generateForm.value = {
    studentId: currentUser?.id || 0,
    courseId: 0,
    analysisType: 'COMPREHENSIVE',
    includeWeakAreas: true,
    includeAdvancedTopics: false,
    maxRecommendations: 10,
    timeConstraint: undefined
  }
  generateDialogVisible.value = true
}

const submitGenerate = async () => {
  if (!generateForm.value.studentId || !generateForm.value.courseId) {
    ElMessage.error('请选择学生和课程')
    return
  }

  generateLoading.value = true
  try {
    const res = await generateRecommendationsApi(generateForm.value)
    if (res.data) {
      recommendations.value = res.data
      generateDialogVisible.value = false
      ElMessage.success('生成学习推荐成功')
    }
  } catch (error) {
    ElMessage.error('生成学习推荐失败')
  } finally {
    generateLoading.value = false
  }
}

const handleStatusChange = async (recommendation: LearningRecommendationVO, status: string) => {
  try {
    const updateData: RecommendationStatusUpdateDTO = {
      status: status as any
    }

    await updateRecommendationStatusApi(recommendation.id, updateData)

    // 更新本地数据
    const index = recommendations.value.findIndex((r) => r.id === recommendation.id)
    if (index !== -1) {
      recommendations.value[index].status = status as any
    }

    ElMessage.success('更新状态成功')
  } catch (error) {
    ElMessage.error('更新状态失败')
  }
}

const getPriorityColor = (priority: string) => {
  const colorMap: Record<string, string> = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return colorMap[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const textMap: Record<string, string> = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return textMap[priority] || priority
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    RESOURCE: 'primary',
    PRACTICE: 'success',
    REVIEW: 'warning',
    ASSIGNMENT: 'info'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    RESOURCE: '资源学习',
    PRACTICE: '练习巩固',
    REVIEW: '复习回顾',
    ASSIGNMENT: '作业任务'
  }
  return textMap[type] || type
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    PENDING: 'info',
    IN_PROGRESS: 'warning',
    COMPLETED: 'success',
    DISMISSED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PENDING: '待开始',
    IN_PROGRESS: '进行中',
    COMPLETED: '已完成',
    DISMISSED: '已忽略'
  }
  return textMap[status] || status
}

const getAnalysisTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    PERFORMANCE_BASED: '基于表现分析',
    KNOWLEDGE_GAP: '知识薄弱点分析',
    LEARNING_PATH: '学习路径规划',
    COMPREHENSIVE: '综合分析'
  }
  return textMap[type] || type
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
  fetchRecommendations()
  fetchCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold">个性化学习推荐</h2>
          <p class="text-sm text-gray-500 mt-1">基于AI分析为您量身定制的学习建议</p>
        </div>
        <el-button type="primary" @click="handleGenerate"> 生成新推荐 </el-button>
      </div>
    </div>

    <div v-if="recommendations.length === 0" class="text-center py-20">
      <ElEmpty description="暂无学习推荐">
        <el-button type="primary" @click="handleGenerate"> 立即生成推荐 </el-button>
      </ElEmpty>
    </div>

    <div v-else class="space-y-4">
      <ElCard
        v-for="recommendation in recommendations"
        :key="recommendation.id"
        class="recommendation-card"
      >
        <div class="flex justify-between items-start">
          <div class="flex-1">
            <div class="flex items-center space-x-2 mb-2">
              <h3 class="text-lg font-semibold">{{ recommendation.title }}</h3>
              <ElTag :type="getPriorityColor(recommendation.priority)" size="small">
                {{ getPriorityText(recommendation.priority) }}优先级
              </ElTag>
              <ElTag :type="getTypeColor(recommendation.type)" size="small">
                {{ getTypeText(recommendation.type) }}
              </ElTag>
            </div>

            <p class="text-gray-600 mb-3">{{ recommendation.description }}</p>

            <div class="flex items-center space-x-4 text-sm text-gray-500 mb-3">
              <span>预计用时: {{ formatTime(recommendation.estimatedTime) }}</span>
              <span>创建时间: {{ recommendation.createdAt }}</span>
            </div>

            <div class="mb-3">
              <div class="text-sm font-medium mb-1">推荐理由:</div>
              <p class="text-sm text-gray-600 bg-gray-50 p-2 rounded">{{
                recommendation.reason
              }}</p>
            </div>

            <div v-if="recommendation.targetKnowledgePoints.length > 0" class="mb-3">
              <div class="text-sm font-medium mb-1">目标知识点:</div>
              <div class="flex flex-wrap gap-1">
                <ElTag
                  v-for="point in recommendation.targetKnowledgePoints"
                  :key="point"
                  size="small"
                  type="info"
                >
                  {{ point }}
                </ElTag>
              </div>
            </div>

            <div v-if="recommendation.relatedResources.length > 0" class="mb-3">
              <div class="text-sm font-medium mb-1">相关资源:</div>
              <div class="space-y-1">
                <div
                  v-for="resource in recommendation.relatedResources"
                  :key="resource.id"
                  class="flex items-center space-x-2"
                >
                  <ElTag size="small" type="success">{{ resource.type }}</ElTag>
                  <a
                    :href="resource.url"
                    target="_blank"
                    class="text-blue-600 hover:text-blue-800 text-sm"
                  >
                    {{ resource.title }}
                  </a>
                </div>
              </div>
            </div>
          </div>

          <div class="flex flex-col items-end space-y-2">
            <ElTag :type="getStatusColor(recommendation.status)">
              {{ getStatusText(recommendation.status) }}
            </ElTag>

            <div class="flex space-x-1">
              <el-button
                v-if="recommendation.status === 'PENDING'"
                type="primary"
                size="small"
                @click="handleStatusChange(recommendation, 'IN_PROGRESS')"
              >
                开始学习
              </el-button>

              <el-button
                v-if="recommendation.status === 'IN_PROGRESS'"
                type="success"
                size="small"
                @click="handleStatusChange(recommendation, 'COMPLETED')"
              >
                标记完成
              </el-button>

              <el-button
                v-if="recommendation.status !== 'DISMISSED'"
                type="info"
                size="small"
                @click="handleStatusChange(recommendation, 'DISMISSED')"
              >
                忽略
              </el-button>
            </div>
          </div>
        </div>
      </ElCard>
    </div>

    <!-- 生成推荐对话框 -->
    <ElDialog v-model="generateDialogVisible" title="生成学习推荐" width="600px">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium mb-2">课程</label>
          <el-select
            v-model="generateForm.courseId"
            placeholder="请选择课程"
            class="w-full"
            :loading="courseLoading"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">分析类型</label>
          <el-select v-model="generateForm.analysisType" class="w-full">
            <el-option label="基于表现分析" value="PERFORMANCE_BASED" />
            <el-option label="知识薄弱点分析" value="KNOWLEDGE_GAP" />
            <el-option label="学习路径规划" value="LEARNING_PATH" />
            <el-option label="综合分析" value="COMPREHENSIVE" />
          </el-select>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium mb-2">最大推荐数</label>
            <el-input-number
              v-model="generateForm.maxRecommendations"
              :min="1"
              :max="20"
              class="w-full"
            />
          </div>
          <div>
            <label class="block text-sm font-medium mb-2">时间限制(分钟)</label>
            <el-input-number
              v-model="generateForm.timeConstraint"
              :min="10"
              :max="1440"
              class="w-full"
              placeholder="可选"
            />
          </div>
        </div>

        <div>
          <label class="block text-sm font-medium mb-2">推荐选项</label>
          <div class="space-y-2">
            <el-checkbox v-model="generateForm.includeWeakAreas"> 包含薄弱知识点 </el-checkbox>
            <el-checkbox v-model="generateForm.includeAdvancedTopics"> 包含进阶主题 </el-checkbox>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end space-x-2">
          <el-button @click="generateDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="generateLoading" @click="submitGenerate">
            生成推荐
          </el-button>
        </div>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less">
.recommendation-card {
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
