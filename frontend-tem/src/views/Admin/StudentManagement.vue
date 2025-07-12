<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage, ElInput } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getStudentsApi,
  updateStudentStatusApi,
  createStudentApi,
  deleteStudentApi,
  type StudentVO,
  type StudentCreateDTO
} from '@/api/student'

defineOptions({
  name: 'StudentManagement'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref<StudentVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  name: '',
  stuNo: '',
  major: '',
  grade: '',
  status: ''
})

const columns = [
  {
    field: 'stuNo',
    label: '学号',
    width: 120
  },
  {
    field: 'name',
    label: '姓名',
    width: 120
  },
  {
    field: 'gender',
    label: '性别',
    width: 80,
    slots: {
      default: 'gender'
    }
  },
  {
    field: 'major',
    label: '专业',
    width: 150
  },
  {
    field: 'grade',
    label: '年级',
    width: 100
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'phoneNumber',
    label: '手机号',
    width: 130
  },
  {
    field: 'courseCount',
    label: '选课数',
    width: 80
  },
  {
    field: 'avgScore',
    label: '平均分',
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

const fetchStudents = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.value.current,
      size: pagination.value.size,
      keyword: searchForm.value.name || undefined,
      major: searchForm.value.major || undefined,
      grade: searchForm.value.grade || undefined,
      status: searchForm.value.status || undefined
    }

    const response = await getStudentsApi(params)
    if (response?.data) {
      tableData.value = response.data.records
      pagination.value.total = response.data.total
      pagination.value.current = response.data.current
      pagination.value.size = response.data.size
      ElMessage.success('学生列表加载成功')
    }
  } catch (error: any) {
    console.error('获取学生列表失败:', error)
    ElMessage.error(error?.message || '获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchStudents()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    stuNo: '',
    major: '',
    grade: '',
    status: ''
  }
  pagination.value.current = 1
  fetchStudents()
}

const handleCreate = () => {
  // 跳转到通用新建用户页面，并指定角色为学生
  router.push('/admin/user-create?role=STUDENT')
}

const handleEdit = (row: StudentVO) => {
  // router.push(`/forms/student/edit/${row.id}`)
  ElMessage.info(`编辑学生"${row.name}"功能开发中...`)
}

const handleView = (row: StudentVO) => {
  // router.push(`/details/student/${row.id}`)
  ElMessage.info(`查看学生"${row.name}"详情功能开发中...`)
}

const handleDelete = async (row: StudentVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除学生"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteStudentApi(row.id)
    ElMessage.success('删除成功')
    fetchStudents()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

const handleStatusChange = async (row: StudentVO, status: string) => {
  try {
    await updateStudentStatusApi(row.id, { status })
    const statusText = getStatusText(status)
    ElMessage.success(`学生状态已更新为"${statusText}"`)
    row.status = status
  } catch (error: any) {
    console.error('状态更新失败:', error)
    ElMessage.error(error?.message || '状态更新失败')
  }
}

const getGenderText = (gender: number) => {
  return gender === 1 ? '男' : '女'
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    SUSPENDED: 'danger',
    GRADUATED: 'info',
    EXPELLED: 'warning'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '在校',
    SUSPENDED: '休学',
    GRADUATED: '已毕业',
    EXPELLED: '退学'
  }
  return textMap[status] || status
}

// 分页处理
const handlePageChange = (current: number) => {
  pagination.value.current = current
  fetchStudents()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchStudents()
}

onMounted(() => {
  fetchStudents()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchForm.name"
            placeholder="学生姓名"
            style="width: 120px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.stuNo"
            placeholder="学号"
            style="width: 120px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.major"
            placeholder="专业"
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.grade" placeholder="年级" style="width: 100px" clearable>
            <el-option label="2020级" value="2020级" />
            <el-option label="2021级" value="2021级" />
            <el-option label="2022级" value="2022级" />
            <el-option label="2023级" value="2023级" />
          </el-select>
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 100px" clearable>
            <el-option label="在校" value="ACTIVE" />
            <el-option label="休学" value="SUSPENDED" />
            <el-option label="已毕业" value="GRADUATED" />
            <el-option label="退学" value="EXPELLED" />
          </el-select>
          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建学生 </el-button>
      </div>
    </div>

    <Table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
    >
      <template #gender="{ row }">
        <span>{{ getGenderText(row.gender) }}</span>
      </template>

      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="success" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button
          v-if="row.status === 'ACTIVE'"
          type="warning"
          size="small"
          @click="handleStatusChange(row, 'SUSPENDED')"
        >
          休学
        </el-button>
        <el-button
          v-else-if="row.status === 'SUSPENDED'"
          type="success"
          size="small"
          @click="handleStatusChange(row, 'ACTIVE')"
        >
          复学
        </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
