<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElCard,
  ElSelect,
  ElOption,
  ElButton,
  ElProgress,
  ElTag,
  ElTimeline,
  ElTimelineItem,
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElDatePicker,
  ElInputNumber,
  ElSwitch,
  ElEmpty,
  ElSkeleton,
  ElRow,
  ElCol,
  ElDivider
} from 'element-plus'
import { ref, onMounted, computed, reactive, nextTick } from 'vue'
import { getCoursesApi, type CourseVO } from '@/api/course'
import {
  getStudyPlansApi,
  createStudyPlanApi,
  generateAiStudyPlanApi,
  updateStudyPlanProgressApi,
  type StudyPlanVO,
  type StudyPlanCreateDTO
} from '@/api/study'
import { useUserStore } from '@/store/modules/user'
import { Plus, Refresh, Tools } from '@element-plus/icons-vue'

defineOptions({
  name: 'StudyPlan'
})

const userStore = useUserStore()
const loading = ref(false)
const createLoading = ref(false)
const courses = ref<CourseVO[]>([])
const selectedCourseId = ref<number>()
const studyPlans = ref<StudyPlanVO[]>([])
const createDialogVisible = ref(false)

// 创建学习计划表单
const createForm = reactive<StudyPlanCreateDTO>({
  courseId: undefined,
  planName: '',
  description: '',
  planType: 'WEEKLY',
  startDate: '',
  endDate: '',
  targetDate: '',
  estimatedHours: 20,
  goals: [],
  milestones: [],
  priority: 'MEDIUM',
  reminderEnabled: true,
  reminderFrequency: 'DAILY',
  reminderTime: '09:00'
})

const goalInput = ref('')
const milestoneForm = reactive({
  title: '',
  description: '',
  targetDate: ''
})

const fetchCourses = async () => {
  try {
    const res = await getCoursesApi({ size: 100 })
    if (res.data) {
      courses.value = res.data.records
      if (courses.value.length > 0) {
        selectedCourseId.value = courses.value[0].id
        await loadStudyPlans()
      }
    }
  } catch (error) {
    console.error('获取课程失败:', error)
  }
}

const loadStudyPlans = async () => {
  if (!selectedCourseId.value) return

  const currentUser = userStore.getUserInfo
  if (!currentUser?.userId) {
    ElMessage.error('用户信息获取失败')
    return
  }

  loading.value = true
  try {
    const res = await getStudyPlansApi(currentUser.userId, selectedCourseId.value)
    if (res.data) {
      studyPlans.value = res.data
    }
    ElMessage.success('学习计划加载成功')
  } catch (error) {
    console.error('加载学习计划失败:', error)
    ElMessage.error('加载学习计划失败')
    // 显示示例数据
    loadDemoPlans()
  } finally {
    loading.value = false
  }
}

const loadDemoPlans = () => {
  studyPlans.value = [
    {
      id: 1,
      studentId: 1,
      courseId: selectedCourseId.value || 1,
      courseName: courses.value.find((c) => c.id === selectedCourseId.value)?.name || '示例课程',
      planName: 'React进阶学习计划',
      description: '深入学习React高级特性和生态系统',
      planType: 'MONTHLY',
      startDate: new Date().toISOString(),
      endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
      targetDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
      estimatedHours: 40,
      status: 'ACTIVE',
      progress: 65,
      priority: 'HIGH',
      completedTasks: 8,
      totalTasks: 12,
      goals: ['掌握React Hook高级用法', '理解React性能优化', '学会状态管理'],
      milestones: [
        {
          title: 'React Hook掌握',
          description: '熟练使用各种React Hook',
          targetDate: new Date(Date.now() + 10 * 24 * 60 * 60 * 1000).toISOString(),
          completed: true,
          completedDate: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString()
        },
        {
          title: '性能优化实践',
          description: '完成性能优化项目',
          targetDate: new Date(Date.now() + 20 * 24 * 60 * 60 * 1000).toISOString(),
          completed: false,
          completedDate: ''
        }
      ],
      reminderEnabled: true,
      reminderFrequency: 'DAILY',
      reminderTime: '09:00',
      isAiGenerated: false,
      aiRecommendReason: '',
      daysRemaining: 25,
      progressRate: 2.6,
      progressStatus: 'ON_TRACK'
    }
  ]
}

const handleCourseChange = () => {
  loadStudyPlans()
}

const showCreateDialog = () => {
  createForm.courseId = selectedCourseId.value
  createForm.planName = ''
  createForm.description = ''
  createForm.goals = []
  createForm.milestones = []
  createDialogVisible.value = true
}

const addGoal = () => {
  if (goalInput.value.trim()) {
    createForm.goals.push(goalInput.value.trim())
    goalInput.value = ''
  }
}

const removeGoal = (index: number) => {
  createForm.goals.splice(index, 1)
}

const addMilestone = () => {
  if (milestoneForm.title && milestoneForm.targetDate) {
    createForm.milestones.push({
      title: milestoneForm.title,
      description: milestoneForm.description,
      targetDate: milestoneForm.targetDate
    })
    milestoneForm.title = ''
    milestoneForm.description = ''
    milestoneForm.targetDate = ''
  }
}

const removeMilestone = (index: number) => {
  createForm.milestones.splice(index, 1)
}

const submitCreatePlan = async () => {
  const currentUser = userStore.getUserInfo
  if (!currentUser?.userId) {
    ElMessage.error('用户信息获取失败')
    return
  }

  if (!createForm.planName || !createForm.startDate || !createForm.targetDate) {
    ElMessage.error('请填写必要信息')
    return
  }

  createLoading.value = true
  try {
    await createStudyPlanApi(currentUser.userId, createForm)
    ElMessage.success('学习计划创建成功')
    createDialogVisible.value = false
    await loadStudyPlans()
  } catch (error) {
    console.error('创建学习计划失败:', error)
    ElMessage.error('创建学习计划失败')
  } finally {
    createLoading.value = false
  }
}

const generateAiPlan = async () => {
  const currentUser = userStore.getUserInfo
  if (!currentUser?.userId || !selectedCourseId.value) {
    ElMessage.error('用户信息或课程信息获取失败')
    return
  }

  createLoading.value = true
  try {
    const res = await generateAiStudyPlanApi(currentUser.userId, selectedCourseId.value, 'WEEKLY')
    if (res.data) {
      ElMessage.success('AI学习计划生成成功')
      await loadStudyPlans()
    }
  } catch (error) {
    console.error('生成AI学习计划失败:', error)
    ElMessage.error('生成AI学习计划失败')
  } finally {
    createLoading.value = false
  }
}

const updatePlanProgress = async (planId: number, progress: number) => {
  try {
    await updateStudyPlanProgressApi(planId, progress)
    ElMessage.success('进度更新成功')
    await loadStudyPlans()
  } catch (error) {
    console.error('更新进度失败:', error)
    ElMessage.error('更新进度失败')
  }
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'primary',
    COMPLETED: 'success',
    PAUSED: 'warning',
    CANCELLED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '进行中',
    COMPLETED: '已完成',
    PAUSED: '已暂停',
    CANCELLED: '已取消'
  }
  return textMap[status] || status
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

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
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
          <h2 class="text-xl font-bold">学习计划</h2>
          <p class="text-sm text-gray-500 mt-1">制定和管理我的学习计划</p>
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
          <ElButton type="success" :icon="Tools" @click="generateAiPlan" :loading="createLoading">
            AI生成计划
          </ElButton>
          <ElButton type="primary" :icon="Plus" @click="showCreateDialog"> 创建计划 </ElButton>
          <ElButton :icon="Refresh" @click="loadStudyPlans" :loading="loading"> 刷新 </ElButton>
        </div>
      </div>
    </div>

    <ElSkeleton :loading="loading" animated>
      <template #template>
        <div class="space-y-4">
          <ElSkeleton-item variant="rect" style="height: 200px" />
          <ElSkeleton-item variant="rect" style="height: 300px" />
        </div>
      </template>

      <template #default>
        <div v-if="studyPlans.length > 0" class="space-y-4">
          <ElCard v-for="plan in studyPlans" :key="plan.id" class="study-plan-card">
            <div class="flex justify-between items-start mb-4">
              <div class="flex-1">
                <div class="flex items-center space-x-2 mb-2">
                  <h3 class="text-lg font-semibold">{{ plan.planName }}</h3>
                  <ElTag :type="getStatusColor(plan.status)" size="small">
                    {{ getStatusText(plan.status) }}
                  </ElTag>
                  <ElTag :type="getPriorityColor(plan.priority)" size="small">
                    {{ getPriorityText(plan.priority) }}优先级
                  </ElTag>
                  <ElTag v-if="plan.isAiGenerated" type="info" size="small">
                    <template #default>
                      <Tools class="w-3 h-3 mr-1" />
                      AI生成
                    </template>
                  </ElTag>
                </div>
                <p class="text-gray-600 mb-3">{{ plan.description }}</p>

                <div class="grid grid-cols-2 gap-4 text-sm text-gray-500 mb-3">
                  <div>开始日期: {{ formatDate(plan.startDate) }}</div>
                  <div>目标日期: {{ formatDate(plan.targetDate) }}</div>
                  <div>预计时长: {{ plan.estimatedHours }}小时</div>
                  <div>剩余天数: {{ plan.daysRemaining }}天</div>
                </div>
              </div>

              <div class="text-right ml-4">
                <div class="text-2xl font-bold text-blue-600 mb-1">{{ plan.progress }}%</div>
                <div class="text-sm text-gray-500">完成进度</div>
              </div>
            </div>

            <ElProgress :percentage="plan.progress" :stroke-width="8" class="mb-4" />

            <ElRow :gutter="16" class="mb-4">
              <ElCol :span="12">
                <div class="bg-gray-50 p-3 rounded">
                  <div class="font-medium mb-2">学习目标</div>
                  <div class="space-y-1">
                    <div
                      v-for="goal in plan.goals"
                      :key="goal"
                      class="text-sm text-gray-700 flex items-center"
                    >
                      <span class="w-2 h-2 bg-blue-500 rounded-full mr-2"></span>
                      {{ goal }}
                    </div>
                  </div>
                </div>
              </ElCol>

              <ElCol :span="12">
                <div class="bg-gray-50 p-3 rounded">
                  <div class="font-medium mb-2">里程碑</div>
                  <ElTimeline>
                    <ElTimelineItem
                      v-for="milestone in plan.milestones"
                      :key="milestone.title"
                      :type="milestone.completed ? 'success' : 'primary'"
                      :icon="milestone.completed ? 'Check' : 'Clock'"
                    >
                      <div class="text-sm">
                        <div class="font-medium">{{ milestone.title }}</div>
                        <div class="text-gray-500">{{ milestone.description }}</div>
                        <div class="text-xs text-gray-400 mt-1">
                          目标: {{ formatDate(milestone.targetDate) }}
                          <span v-if="milestone.completed" class="text-green-600 ml-2">
                            (已完成: {{ formatDate(milestone.completedDate) }})
                          </span>
                        </div>
                      </div>
                    </ElTimelineItem>
                  </ElTimeline>
                </div>
              </ElCol>
            </ElRow>

            <div class="flex justify-between items-center text-sm text-gray-500">
              <div> 任务进度: {{ plan.completedTasks }} / {{ plan.totalTasks }} 完成 </div>
              <div class="flex space-x-2">
                <ElButton
                  size="small"
                  @click="updatePlanProgress(plan.id, Math.min(plan.progress + 10, 100))"
                >
                  +10% 进度
                </ElButton>
                <ElButton
                  v-if="plan.progress < 100"
                  size="small"
                  type="success"
                  @click="updatePlanProgress(plan.id, 100)"
                >
                  标记完成
                </ElButton>
              </div>
            </div>
          </ElCard>
        </div>

        <div v-else class="text-center py-20">
          <ElEmpty description="暂无学习计划">
            <div class="space-x-2">
              <ElButton type="primary" @click="showCreateDialog">创建新计划</ElButton>
              <ElButton type="success" @click="generateAiPlan">AI生成计划</ElButton>
            </div>
          </ElEmpty>
        </div>
      </template>
    </ElSkeleton>

    <!-- 创建学习计划对话框 -->
    <ElDialog
      v-model="createDialogVisible"
      title="创建学习计划"
      width="600px"
      :close-on-click-modal="false"
    >
      <ElForm :model="createForm" label-width="100px">
        <ElFormItem label="计划名称" required>
          <ElInput v-model="createForm.planName" placeholder="请输入计划名称" />
        </ElFormItem>

        <ElFormItem label="计划描述">
          <ElInput
            v-model="createForm.description"
            type="textarea"
            rows="3"
            placeholder="计划描述"
          />
        </ElFormItem>

        <ElFormItem label="计划类型">
          <ElSelect v-model="createForm.planType" style="width: 100%">
            <ElOption label="每日计划" value="DAILY" />
            <ElOption label="每周计划" value="WEEKLY" />
            <ElOption label="每月计划" value="MONTHLY" />
            <ElOption label="自定义计划" value="CUSTOM" />
          </ElSelect>
        </ElFormItem>

        <ElRow :gutter="16">
          <ElCol :span="12">
            <ElFormItem label="开始日期" required>
              <ElDatePicker v-model="createForm.startDate" type="date" style="width: 100%" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="目标日期" required>
              <ElDatePicker v-model="createForm.targetDate" type="date" style="width: 100%" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElFormItem label="预计时长">
          <ElInputNumber v-model="createForm.estimatedHours" :min="1" :max="1000" />
          <span class="ml-2 text-gray-500">小时</span>
        </ElFormItem>

        <ElFormItem label="优先级">
          <ElSelect v-model="createForm.priority" style="width: 100%">
            <ElOption label="高优先级" value="HIGH" />
            <ElOption label="中优先级" value="MEDIUM" />
            <ElOption label="低优先级" value="LOW" />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="学习目标">
          <div class="space-y-2">
            <div class="flex space-x-2">
              <ElInput v-model="goalInput" placeholder="输入学习目标" />
              <ElButton @click="addGoal">添加</ElButton>
            </div>
            <div class="space-y-1">
              <ElTag
                v-for="(goal, index) in createForm.goals"
                :key="index"
                closable
                @close="removeGoal(index)"
              >
                {{ goal }}
              </ElTag>
            </div>
          </div>
        </ElFormItem>

        <ElFormItem label="里程碑">
          <div class="space-y-3">
            <div class="border p-3 rounded">
              <ElRow :gutter="16">
                <ElCol :span="8">
                  <ElInput v-model="milestoneForm.title" placeholder="里程碑标题" />
                </ElCol>
                <ElCol :span="8">
                  <ElInput v-model="milestoneForm.description" placeholder="描述" />
                </ElCol>
                <ElCol :span="6">
                  <ElDatePicker
                    v-model="milestoneForm.targetDate"
                    type="date"
                    style="width: 100%"
                  />
                </ElCol>
                <ElCol :span="2">
                  <ElButton @click="addMilestone">添加</ElButton>
                </ElCol>
              </ElRow>
            </div>
            <div class="space-y-2">
              <div
                v-for="(milestone, index) in createForm.milestones"
                :key="index"
                class="flex justify-between items-center p-2 bg-gray-50 rounded"
              >
                <div>
                  <div class="font-medium">{{ milestone.title }}</div>
                  <div class="text-sm text-gray-500">{{ milestone.description }}</div>
                  <div class="text-xs text-gray-400">{{ formatDate(milestone.targetDate) }}</div>
                </div>
                <ElButton size="small" type="danger" @click="removeMilestone(index)">删除</ElButton>
              </div>
            </div>
          </div>
        </ElFormItem>

        <ElDivider content-position="left">提醒设置</ElDivider>

        <ElFormItem label="启用提醒">
          <ElSwitch v-model="createForm.reminderEnabled" />
        </ElFormItem>

        <ElRow :gutter="16" v-if="createForm.reminderEnabled">
          <ElCol :span="12">
            <ElFormItem label="提醒频率">
              <ElSelect v-model="createForm.reminderFrequency" style="width: 100%">
                <ElOption label="每日提醒" value="DAILY" />
                <ElOption label="每周提醒" value="WEEKLY" />
                <ElOption label="自定义" value="CUSTOM" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="提醒时间">
              <ElInput v-model="createForm.reminderTime" placeholder="如: 09:00" />
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>

      <template #footer>
        <div class="flex justify-end space-x-2">
          <ElButton @click="createDialogVisible = false">取消</ElButton>
          <ElButton type="primary" :loading="createLoading" @click="submitCreatePlan">
            创建计划
          </ElButton>
        </div>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less">
.study-plan-card {
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

:deep(.el-timeline-item__content) {
  padding-bottom: 10px;
}
</style>
