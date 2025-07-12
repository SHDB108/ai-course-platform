<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElCard, ElProgress, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getStudentCoursesApi, type PageVO } from '@/api/student'
import type { CourseVO } from '@/api/course'
import { safeUserIdToString, safeUserIdToNumber, autoCorrectUserId } from '@/utils/userIdUtils'

defineOptions({
  name: 'StudentCourses'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<CourseVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = [
  {
    field: 'name',
    label: '课程名称',
    width: 200
  },
  {
    field: 'teacherName',
    label: '授课教师',
    width: 120
  },
  {
    field: 'credits',
    label: '学分',
    width: 80
  },
  {
    field: 'status',
    label: '状态',
    width: 100,
    slots: {
      default: 'status'
    }
  },
  {
    field: 'action',
    label: '操作',
    width: 200,
    slots: {
      default: 'action'
    }
  }
]

const summaryData = ref({
  totalCourses: 0,
  inProgress: 0,
  completed: 0,
  avgProgress: 0
})

const fetchMyCourses = async () => {
  loading.value = true
  try {
    // 强制刷新用户信息以确保ID正确
    try {
      await userStore.fetchUserInfo()
    } catch (fetchError) {
      console.warn('刷新用户信息失败，使用缓存信息:', fetchError)
    }

    // 获取真实的用户信息和认证信息
    const userInfo = userStore.getUserInfo
    const token = userStore.getToken
    const tokenKey = userStore.getTokenKey

    console.log('=== API调用调试信息 ===')
    console.log('用户信息:', userInfo)
    console.log('Token存在:', !!token)
    console.log('Token值:', token ? `${token.substring(0, 20)}...` : 'null')
    console.log('Token键:', tokenKey)

    const rawStudentId = userInfo?.userId

    // 使用安全的ID处理工具替代临时修复
    console.log('开始处理用户ID...')
    const correctedIdStr = autoCorrectUserId(rawStudentId || '')
    const studentId = correctedIdStr // 直接使用字符串，不转换为数字

    // 如果ID被修正了，更新store中的用户信息
    if (correctedIdStr !== String(rawStudentId) && userInfo) {
      console.log('用户ID已被修正，更新store...')
      userInfo.userId = correctedIdStr
      userStore.setUserInfo({
        token: token || '',
        userId: correctedIdStr,
        username: userInfo.username || 'student1',
        role: userInfo.role || 'STUDENT'
      })
    }

    console.log('原始学生ID:', rawStudentId)
    console.log('修正后学生ID字符串:', correctedIdStr)
    console.log('API调用用学生ID:', studentId)
    console.log('学生ID类型:', typeof studentId)

    if (!studentId || studentId === '0') {
      ElMessage.error('未获取到有效的学生信息，请重新登录')
      return
    }

    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size
    }
    console.log('请求参数:', params)
    console.log('完整API路径:', `/api/v1/students/${studentId}/courses`)

    // 调用真实API
    console.log('开始调用真实API...')
    const res = await getStudentCoursesApi(studentId, params)
    console.log('API响应类型:', typeof res)
    console.log('API响应内容:', res)

    // 检查响应结构
    if (res === undefined || res === null) {
      console.error('API返回undefined或null')
      ElMessage.error('API调用失败，返回空数据')
      return
    }

    console.log('响应状态码:', res.code)
    console.log('响应消息:', res.msg)
    console.log('响应数据:', res.data)

    if (res.code === 0 && res.data) {
      if (res.data.records && res.data.records.length > 0) {
        tableData.value = res.data.records
        pagination.value.total = res.data.total || 0

        // 更新统计数据
        const totalCourses = res.data.total || 0
        summaryData.value = {
          totalCourses: totalCourses,
          inProgress: Math.floor(totalCourses * 0.6),
          completed: Math.floor(totalCourses * 0.3),
          avgProgress: totalCourses > 0 ? 65 : 0
        }

        ElMessage.success(`成功加载 ${res.data.records.length} 门课程`)
      } else {
        // 成功响应但没有课程数据
        tableData.value = []
        pagination.value.total = 0
        summaryData.value = {
          totalCourses: 0,
          inProgress: 0,
          completed: 0,
          avgProgress: 0
        }
        ElMessage.info('暂无选修课程，请先选课')
      }
    } else {
      console.warn('API返回错误:', res)
      if (res.code !== 0) {
        ElMessage.error(`获取课程失败: ${res.msg || '未知错误'}`)

        // 如果是学生不存在错误，提示用户可能需要重新登录
        if (res.msg && res.msg.includes('学生不存在')) {
          ElMessage.warning('学生信息可能已过期，建议重新登录或联系管理员')
        }
      } else {
        ElMessage.warning('暂无课程数据')
      }

      tableData.value = []
      pagination.value.total = 0
      summaryData.value = {
        totalCourses: 0,
        inProgress: 0,
        completed: 0,
        avgProgress: 0
      }
    }
  } catch (error) {
    console.error('API调用异常详情:', error)
    console.error('错误类型:', error.constructor.name)
    console.error('错误消息:', error.message)
    console.error('错误堆栈:', error.stack)

    if (error.response) {
      console.error('HTTP响应状态:', error.response.status)
      console.error('HTTP响应数据:', error.response.data)
      ElMessage.error(
        `HTTP错误 ${error.response.status}: ${error.response.data?.msg || error.message}`
      )
    } else if (error.request) {
      console.error('网络请求失败:', error.request)
      ElMessage.error('网络请求失败，请检查网络连接')
    } else {
      ElMessage.error(`获取课程失败: ${error.message}`)
    }
  } finally {
    loading.value = false
  }
}

const handleStudy = (row: any) => {
  router.push(`/student-actions/course/study/${row.id}`)
}

const handleViewTasks = (row: any) => {
  router.push(`/learning/course/${row.id}/tasks`)
}

const handleViewGrades = (row: any) => {
  router.push(`/learning/course/${row.id}/grades`)
}

const handleViewDetail = (row: any) => {
  router.push(`/details/course/${row.id}`)
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ENROLLED: 'success',
    IN_PROGRESS: 'primary',
    COMPLETED: 'warning',
    DROPPED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ENROLLED: '已选课',
    IN_PROGRESS: '学习中',
    COMPLETED: '已完成',
    DROPPED: '已退课'
  }
  return textMap[status] || status
}

// 清除缓存并重新加载页面
const clearCacheAndReload = () => {
  try {
    // 清除localStorage
    localStorage.clear()
    // 清除sessionStorage
    sessionStorage.clear()

    ElMessage.success('缓存已清除，页面即将重新加载')

    // 延迟重新加载页面
    setTimeout(() => {
      window.location.reload()
    }, 1000)
  } catch (error) {
    console.error('清除缓存失败:', error)
    ElMessage.error('清除缓存失败，请手动刷新页面')
  }
}

onMounted(() => {
  fetchMyCourses()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex justify-between items-center">
        <div>
          <h2 class="text-xl font-bold">我的课程</h2>
          <p class="text-sm text-gray-500 mt-1">查看我选修的所有课程和学习进度</p>
        </div>
        <!-- 调试工具 -->
        <div class="flex gap-2">
          <el-button size="small" type="warning" @click="clearCacheAndReload">
            清除缓存重新加载
          </el-button>
          <el-button size="small" type="primary" @click="fetchMyCourses"> 刷新课程 </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4 mb-20px">
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-blue-600">{{ summaryData.totalCourses }}</div>
          <div class="text-sm text-gray-600">总课程数</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{ summaryData.inProgress }}</div>
          <div class="text-sm text-gray-600">进行中</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-orange-600">{{ summaryData.completed }}</div>
          <div class="text-sm text-gray-600">已完成</div>
        </div>
      </ElCard>
      <ElCard shadow="hover">
        <div class="text-center">
          <div class="text-2xl font-bold text-purple-600">{{ summaryData.avgProgress }}%</div>
          <div class="text-sm text-gray-600">平均进度</div>
        </div>
      </ElCard>
    </div>

    <div class="table-container">
      <Table
        :columns="columns"
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        style="width: 100%"
      >
        <template #status="{ row }">
          <ElTag :type="getStatusColor(row.status)">
            {{ getStatusText(row.status) }}
          </ElTag>
        </template>

        <template #action="{ row }">
          <el-button type="primary" size="small" @click="handleStudy(row)">开始学习</el-button>
          <el-button type="success" size="small" @click="handleViewTasks(row)">任务</el-button>
          <el-button type="info" size="small" @click="handleViewGrades(row)">成绩</el-button>
          <el-button type="warning" size="small" @click="handleViewDetail(row)">详情</el-button>
        </template>
      </Table>
    </div>
  </ContentWrap>
</template>

<style scoped lang="less">
.table-container {
  width: 100%;
  min-height: 400px;

  :deep(.el-table) {
    width: 100% !important;
  }

  :deep(.el-table__body-wrapper) {
    min-height: 300px;
  }
}

// 确保ContentWrap组件也填满容器
:deep(.content-wrap) {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
