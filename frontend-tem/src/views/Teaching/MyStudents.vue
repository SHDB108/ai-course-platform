<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

defineOptions({
  name: 'MyStudents'
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const courses = ref([])
const selectedCourse = ref('')
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const columns = [
  {
    field: 'name',
    label: '学生姓名',
    width: 120
  },
  {
    field: 'studentId',
    label: '学号',
    width: 150
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'courseName',
    label: '课程',
    width: 150
  },
  {
    field: 'enrollmentDate',
    label: '选课时间',
    width: 120
  },
  {
    field: 'progress',
    label: '学习进度',
    width: 120,
    slots: {
      default: 'progress'
    }
  },
  {
    field: 'lastActiveDate',
    label: '最后活跃',
    width: 120
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

const fetchMyCourses = async () => {
  try {
    // 临时使用模拟数据进行演示
    const mockCourses = [
      { id: '', name: '全部课程' },
      { id: 1001, name: 'Java程序设计' },
      { id: 1002, name: '数据结构与算法' },
      { id: 1003, name: '机器学习基础' },
      { id: 1004, name: '软件工程实践' }
    ]

    courses.value = mockCourses
    ElMessage.success('课程列表加载成功（演示数据）')

    // 正式版本的API调用（暂时注释）
    /*
    const teacherId = userStore.getUserInfo?.userId
    const res = await getTeacherCoursesApi(teacherId)
    if (res.data) {
      courses.value = res.data
    }
    */
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const fetchMyStudents = async () => {
  loading.value = true
  try {
    // 临时使用模拟数据进行演示
    const mockStudents = [
      {
        id: 2001,
        name: '张三',
        studentId: 'S2024001',
        email: 'zhangsan@example.com',
        courseName: 'Java程序设计',
        enrollmentDate: '2024-09-01',
        progress: 85,
        lastActiveDate: '2024-07-10',
        major: '计算机科学与技术',
        grade: '2024'
      },
      {
        id: 2002,
        name: '李四',
        studentId: 'S2024002',
        email: 'lisi@example.com',
        courseName: 'Java程序设计',
        enrollmentDate: '2024-09-01',
        progress: 72,
        lastActiveDate: '2024-07-09',
        major: '软件工程',
        grade: '2024'
      },
      {
        id: 2003,
        name: '王五',
        studentId: 'S2024003',
        email: 'wangwu@example.com',
        courseName: 'Java程序设计',
        enrollmentDate: '2024-09-02',
        progress: 91,
        lastActiveDate: '2024-07-11',
        major: '人工智能',
        grade: '2023'
      },
      {
        id: 2001,
        name: '张三',
        studentId: 'S2024001',
        email: 'zhangsan@example.com',
        courseName: '数据结构与算法',
        enrollmentDate: '2024-09-01',
        progress: 78,
        lastActiveDate: '2024-07-10',
        major: '计算机科学与技术',
        grade: '2024'
      },
      {
        id: 2004,
        name: '赵六',
        studentId: 'S2024004',
        email: 'zhaoliu@example.com',
        courseName: '数据结构与算法',
        enrollmentDate: '2024-09-01',
        progress: 68,
        lastActiveDate: '2024-07-08',
        major: '计算机科学与技术',
        grade: '2023'
      },
      {
        id: 2005,
        name: '钱七',
        studentId: 'S2024005',
        email: 'qianqi@example.com',
        courseName: '数据结构与算法',
        enrollmentDate: '2024-09-02',
        progress: 83,
        lastActiveDate: '2024-07-11',
        major: '软件工程',
        grade: '2024'
      },
      {
        id: 2003,
        name: '王五',
        studentId: 'S2024003',
        email: 'wangwu@example.com',
        courseName: '机器学习基础',
        enrollmentDate: '2024-09-01',
        progress: 94,
        lastActiveDate: '2024-07-11',
        major: '人工智能',
        grade: '2023'
      },
      {
        id: 2004,
        name: '赵六',
        studentId: 'S2024004',
        email: 'zhaoliu@example.com',
        courseName: '机器学习基础',
        enrollmentDate: '2024-09-01',
        progress: 87,
        lastActiveDate: '2024-07-08',
        major: '计算机科学与技术',
        grade: '2023'
      },
      {
        id: 2005,
        name: '钱七',
        studentId: 'S2024005',
        email: 'qianqi@example.com',
        courseName: '机器学习基础',
        enrollmentDate: '2024-09-02',
        progress: 79,
        lastActiveDate: '2024-07-11',
        major: '软件工程',
        grade: '2024'
      }
    ]

    // 根据选择的课程过滤学生
    let filteredStudents = mockStudents
    if (selectedCourse.value) {
      const courseMap: Record<number, string> = {
        1001: 'Java程序设计',
        1002: '数据结构与算法',
        1003: '机器学习基础',
        1004: '软件工程实践'
      }
      const courseName = courseMap[selectedCourse.value as number]
      if (courseName) {
        filteredStudents = mockStudents.filter((student) => student.courseName === courseName)
      }
    }

    // 分页处理
    const startIndex = (pagination.value.current - 1) * pagination.value.size
    const endIndex = startIndex + pagination.value.size
    const pagedStudents = filteredStudents.slice(startIndex, endIndex)

    tableData.value = pagedStudents
    pagination.value.total = filteredStudents.length
    ElMessage.success(`成功加载 ${pagedStudents.length} 名学生数据（演示数据）`)

    // 正式版本的API调用（暂时注释）
    /*
    const teacherId = userStore.getUserInfo?.userId
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      courseId: selectedCourse.value || undefined
    }

    const res = await getTeacherStudentsApi(teacherId, params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
    */
  } catch (error) {
    ElMessage.error('获取我的学生失败')
  } finally {
    loading.value = false
  }
}

const handleCourseChange = () => {
  pagination.value.current = 1
  fetchMyStudents()
}

const handleViewStudent = (row: any) => {
  router.push(`/details/student/${row.id}`)
}

const handleViewProgress = (row: any) => {
  router.push(`/teaching/student/${row.id}/progress`)
}

const handleSendMessage = (row: any) => {
  ElMessage.info('发送消息功能开发中...')
}

const getProgressColor = (progress: number) => {
  if (progress >= 80) return 'success'
  if (progress >= 60) return 'warning'
  return 'danger'
}

onMounted(() => {
  fetchMyCourses()
  fetchMyStudents()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div>
          <h2 class="text-xl font-bold">我的学生</h2>
          <p class="text-sm text-gray-500 mt-1">管理我课程中的所有学生</p>
        </div>
        <div class="flex items-center space-x-4">
          <el-select
            v-model="selectedCourse"
            placeholder="选择课程"
            style="width: 200px"
            clearable
            @change="handleCourseChange"
          >
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </div>
      </div>
    </div>

    <Table :columns="columns" :data="tableData" :loading="loading" :pagination="pagination">
      <template #progress="{ row }">
        <div class="flex items-center">
          <div class="w-20 bg-gray-200 rounded-full h-2 mr-2">
            <div
              :class="`h-2 rounded-full bg-${getProgressColor(row.progress)}-500`"
              :style="`width: ${row.progress || 0}%`"
            ></div>
          </div>
          <span class="text-sm">{{ row.progress || 0 }}%</span>
        </div>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleViewStudent(row)"> 详情 </el-button>
        <el-button type="success" size="small" @click="handleViewProgress(row)"> 进度 </el-button>
        <el-button type="info" size="small" @click="handleSendMessage(row)"> 消息 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
