<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import {
  ElButton,
  ElTag,
  ElSelect,
  ElOption,
  ElMessageBox,
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElCard,
  ElRow,
  ElCol,
  ElStatistic
} from 'element-plus'
import { ref, onMounted, computed } from 'vue'
import {
  getCoursesApi,
  getCourseStudentsApi,
  enrollStudentApi,
  withdrawStudentApi,
  type CourseVO,
  type StudentVO,
  type PageVO
} from '@/api/course'
import { getUsersByAdminApi } from '@/api/admin'

defineOptions({
  name: 'EnrollmentManagement'
})

const loading = ref(false)
const studentLoading = ref(false)
const dialogVisible = ref(false)
const enrollDialogVisible = ref(false)

// 课程列表
const courses = ref<CourseVO[]>([])
const selectedCourse = ref<CourseVO | null>(null)

// 学生列表
const students = ref<StudentVO[]>([])
const allStudents = ref<any[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

// 选课统计
const enrollmentStats = ref({
  totalEnrollments: 0,
  avgEnrollmentPerCourse: 0,
  mostPopularCourse: '',
  enrollmentRate: 0
})

// 表格列定义
const columns = [
  {
    field: 'name',
    label: '学生姓名',
    width: 120
  },
  {
    field: 'stuNo',
    label: '学号',
    width: 120
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'phoneNumber',
    label: '联系电话',
    width: 130
  },
  {
    field: 'enrollmentDate',
    label: '选课时间',
    width: 150
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
    width: 120,
    slots: {
      default: 'action'
    }
  }
]

// 获取课程列表
const fetchCourses = async () => {
  loading.value = true
  try {
    const response = await getCoursesApi({ status: 'PUBLISHED' })
    if (response?.data) {
      courses.value = response.data.records

      // 计算统计数据
      const totalEnrollments = courses.value.reduce(
        (sum, course) => sum + course.enrolledStudents,
        0
      )
      const avgEnrollment = courses.value.length > 0 ? totalEnrollments / courses.value.length : 0
      const mostPopular =
        courses.value.length > 0
          ? courses.value.reduce((prev, current) =>
              prev.enrolledStudents > current.enrolledStudents ? prev : current
            )
          : null

      enrollmentStats.value = {
        totalEnrollments,
        avgEnrollmentPerCourse: Math.round(avgEnrollment * 100) / 100,
        mostPopularCourse: mostPopular?.name || '',
        enrollmentRate: 0 // 需要根据实际业务计算
      }
    }
  } catch (error: any) {
    console.error('获取课程列表失败:', error)
    ElMessage.error(error?.message || '获取课程列表失败')
  } finally {
    loading.value = false
  }
}

// 获取课程学生列表
const fetchCourseStudents = async (courseId: number) => {
  studentLoading.value = true
  try {
    const response = await getCourseStudentsApi(courseId, {
      page: pagination.value.current,
      size: pagination.value.size
    })
    if (response?.data) {
      students.value = response.data.records
      pagination.value.total = response.data.total
      pagination.value.current = response.data.current
      pagination.value.size = response.data.size
    }
  } catch (error: any) {
    console.error('获取课程学生列表失败:', error)
    ElMessage.error(error?.message || '获取课程学生列表失败')
  } finally {
    studentLoading.value = false
  }
}

// 获取所有学生（用于选课）
const fetchAllStudents = async () => {
  try {
    const response = await getUsersByAdminApi({ role: 'STUDENT' })
    if (response?.data) {
      allStudents.value = response.data.records
    }
  } catch (error: any) {
    console.error('获取学生列表失败:', error)
    ElMessage.error(error?.message || '获取学生列表失败')
  }
}

// 选择课程
const handleCourseChange = (courseId: number) => {
  const course = courses.value.find((c) => c.id === courseId)
  if (course) {
    selectedCourse.value = course
    pagination.value.current = 1
    fetchCourseStudents(courseId)
  }
}

// 查看课程详情
const handleViewCourse = (course: CourseVO) => {
  selectedCourse.value = course
  dialogVisible.value = true
  fetchCourseStudents(course.id)
}

// 添加学生到课程
const handleEnrollStudent = () => {
  if (!selectedCourse.value) {
    ElMessage.warning('请先选择课程')
    return
  }

  fetchAllStudents()
  enrollDialogVisible.value = true
}

// 确认选课
const confirmEnrollment = async (studentId: number) => {
  if (!selectedCourse.value) return

  try {
    await enrollStudentApi(selectedCourse.value.id, studentId)
    ElMessage.success('选课成功')
    enrollDialogVisible.value = false
    fetchCourseStudents(selectedCourse.value.id)
    fetchCourses() // 刷新课程列表以更新选课人数
  } catch (error: any) {
    console.error('选课失败:', error)
    ElMessage.error(error?.message || '选课失败')
  }
}

// 退课
const handleWithdraw = async (student: StudentVO) => {
  if (!selectedCourse.value) return

  try {
    await ElMessageBox.confirm(
      `确定要将学生"${student.name}"从课程"${selectedCourse.value.name}"中移除吗？`,
      '确认退课',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await withdrawStudentApi(selectedCourse.value.id, student.id)
    ElMessage.success('退课成功')
    fetchCourseStudents(selectedCourse.value.id)
    fetchCourses() // 刷新课程列表以更新选课人数
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('退课失败:', error)
      ElMessage.error(error?.message || '退课失败')
    }
  }
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ENROLLED: 'success',
    COMPLETED: 'info',
    DROPPED: 'warning'
  }
  return colorMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ENROLLED: '已选课',
    COMPLETED: '已完成',
    DROPPED: '已退课'
  }
  return textMap[status] || status
}

// 分页处理
const handlePageChange = (current: number) => {
  pagination.value.current = current
  if (selectedCourse.value) {
    fetchCourseStudents(selectedCourse.value.id)
  }
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  if (selectedCourse.value) {
    fetchCourseStudents(selectedCourse.value.id)
  }
}

// 获取未选课学生
const unenrolledStudents = computed(() => {
  const enrolledStudentIds = students.value.map((s) => s.id)
  return allStudents.value.filter((s) => !enrolledStudentIds.includes(s.id))
})

onMounted(() => {
  fetchCourses()
})
</script>

<template>
  <ContentWrap>
    <!-- 选课统计 -->
    <ElRow :gutter="16" class="mb-6">
      <ElCol :span="6">
        <ElCard>
          <ElStatistic title="总选课数" :value="enrollmentStats.totalEnrollments">
            <template #suffix>
              <span class="text-sm text-gray-500">人次</span>
            </template>
          </ElStatistic>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <ElStatistic
            title="平均选课人数"
            :value="enrollmentStats.avgEnrollmentPerCourse"
            :precision="1"
          >
            <template #suffix>
              <span class="text-sm text-gray-500">人/课程</span>
            </template>
          </ElStatistic>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <div class="text-center">
            <div class="text-lg font-medium mb-2">最受欢迎课程</div>
            <div class="text-sm text-gray-600">{{
              enrollmentStats.mostPopularCourse || '暂无数据'
            }}</div>
          </div>
        </ElCard>
      </ElCol>
      <ElCol :span="6">
        <ElCard>
          <div class="text-center">
            <div class="text-lg font-medium mb-2">课程总数</div>
            <div class="text-2xl font-bold text-blue-600">{{ courses.length }}</div>
          </div>
        </ElCard>
      </ElCol>
    </ElRow>

    <!-- 课程列表 -->
    <ElCard class="mb-6">
      <template #header>
        <span class="card-title">课程列表</span>
      </template>

      <div class="course-grid">
        <div
          v-for="course in courses"
          :key="course.id"
          class="course-card"
          @click="handleViewCourse(course)"
        >
          <div class="course-header">
            <h3 class="course-name">{{ course.name }}</h3>
            <ElTag :type="course.status === 'PUBLISHED' ? 'success' : 'warning'">
              {{ course.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </ElTag>
          </div>
          <div class="course-info">
            <div class="info-item">
              <span class="label">教师:</span>
              <span class="value">{{ course.teacherName }}</span>
            </div>
            <div class="info-item">
              <span class="label">学分:</span>
              <span class="value">{{ course.credits }}</span>
            </div>
            <div class="info-item">
              <span class="label">选课人数:</span>
              <span class="value">{{ course.enrolledStudents }}/{{ course.maxStudents }}</span>
            </div>
          </div>
        </div>
      </div>
    </ElCard>

    <!-- 学生选课管理对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="`课程${selectedCourse?.name}选课管理`"
      width="800px"
      @close="dialogVisible = false"
    >
      <div class="mb-4">
        <ElButton type="primary" @click="handleEnrollStudent"> 添加学生 </ElButton>
      </div>

      <Table
        :columns="columns"
        :data="students"
        :loading="studentLoading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
      >
        <template #status="{ row }">
          <ElTag :type="getStatusColor(row.status)">
            {{ getStatusText(row.status) }}
          </ElTag>
        </template>

        <template #action="{ row }">
          <ElButton type="danger" size="small" @click="handleWithdraw(row)"> 退课 </ElButton>
        </template>
      </Table>
    </ElDialog>

    <!-- 选课对话框 -->
    <ElDialog
      v-model="enrollDialogVisible"
      title="添加学生"
      width="600px"
      @close="enrollDialogVisible = false"
    >
      <div class="student-list">
        <div v-for="student in unenrolledStudents" :key="student.id" class="student-item">
          <div class="student-info">
            <div class="student-name">{{ student.name }}</div>
            <div class="student-details">
              <span>学号: {{ student.stuNo }}</span>
              <span>邮箱: {{ student.email }}</span>
            </div>
          </div>
          <ElButton type="primary" size="small" @click="confirmEnrollment(student.id)">
            选课
          </ElButton>
        </div>
      </div>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less">
.card-title {
  font-size: 1.125rem;
  font-weight: 600;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.course-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.course-name {
  font-size: 1.125rem;
  font-weight: 600;
  margin: 0;
}

.course-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;

  .label {
    color: #6b7280;
  }

  .value {
    font-weight: 500;
  }
}

.student-list {
  max-height: 400px;
  overflow-y: auto;
}

.student-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 8px;

  &:hover {
    background-color: #f9fafb;
  }
}

.student-info {
  flex: 1;
}

.student-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.student-details {
  font-size: 0.875rem;
  color: #6b7280;

  span {
    margin-right: 16px;
  }
}

.text-sm {
  font-size: 0.875rem;
}

.text-lg {
  font-size: 1.125rem;
}

.text-2xl {
  font-size: 1.5rem;
}

.font-medium {
  font-weight: 500;
}

.font-bold {
  font-weight: 700;
}

.text-gray-500 {
  color: #6b7280;
}

.text-gray-600 {
  color: #4b5563;
}

.text-blue-600 {
  color: #2563eb;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.text-center {
  text-align: center;
}
</style>
