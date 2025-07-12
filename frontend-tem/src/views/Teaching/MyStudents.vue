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
    const teacherId = userStore.getUserInfo?.id
    // TODO: 调用获取教师课程API
    // const res = await getTeacherCoursesApi(teacherId)
    // if (res.data) {
    //   courses.value = res.data
    // }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  }
}

const fetchMyStudents = async () => {
  loading.value = true
  try {
    const teacherId = userStore.getUserInfo?.id
    const params = {
      current: pagination.value.current,
      size: pagination.value.size,
      courseId: selectedCourse.value || undefined
    }

    // TODO: 调用获取教师学生API
    // const res = await getTeacherStudentsApi(teacherId, params)
    // if (res.data) {
    //   tableData.value = res.data.records
    //   pagination.value.total = res.data.total
    // }
    ElMessage.info('学生数据加载中...')
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
