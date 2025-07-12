<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElMessageBox, ElUpload, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getStudentsApi,
  deleteStudentApi,
  importStudentsApi,
  exportStudentsApi,
  type StudentVO,
  type PageVO
} from '@/api/student'

defineOptions({
  name: 'StudentList'
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
  studentId: '',
  status: ''
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
    field: 'username',
    label: '用户名',
    width: 120
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'phoneNumber',
    label: '手机号',
    width: 150
  },
  {
    field: 'enrollmentDate',
    label: '入学日期',
    width: 120
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
    field: 'createdAt',
    label: '创建时间',
    width: 150
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
      current: pagination.value.current,
      size: pagination.value.size,
      ...searchForm.value
    }

    const res = await getStudentsApi(params)
    if (res.data) {
      tableData.value = res.data.records
      pagination.value.total = res.data.total
    }
  } catch (error) {
    ElMessage.error('获取学生列表失败')
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
    studentId: '',
    status: ''
  }
  pagination.value.current = 1
  fetchStudents()
}

const handleCreate = () => {
  router.push('/student/create')
}

const handleEdit = (row: StudentVO) => {
  router.push(`/student/edit/${row.id}`)
}

const handleView = (row: StudentVO) => {
  router.push(`/student/detail/${row.id}`)
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
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImport = async (file: File) => {
  try {
    const res = await importStudentsApi(file)
    if (res.data) {
      ElMessage.success(`导入成功：${res.data.successCount}条，失败：${res.data.failCount}条`)
      if (res.data.errorMessages.length > 0) {
        console.warn('导入错误信息：', res.data.errorMessages)
      }
      fetchStudents()
    }
  } catch (error) {
    ElMessage.error('导入失败')
  }
}

const handleExport = async () => {
  try {
    const blob = await exportStudentsApi()
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `students_${new Date().toISOString().split('T')[0]}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handlePageChange = (page: number) => {
  pagination.value.current = page
  fetchStudents()
}

const handlePageSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchStudents()
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    GRADUATED: 'warning',
    SUSPENDED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '在读',
    INACTIVE: '未激活',
    GRADUATED: '已毕业',
    SUSPENDED: '休学'
  }
  return textMap[status] || status
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
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.studentId"
            placeholder="学号"
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
            <el-option label="在读" value="ACTIVE" />
            <el-option label="未激活" value="INACTIVE" />
            <el-option label="已毕业" value="GRADUATED" />
            <el-option label="休学" value="SUSPENDED" />
          </el-select>
          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <div class="flex items-center space-x-2">
          <el-upload
            :show-file-list="false"
            :auto-upload="true"
            accept=".xlsx,.xls,.csv"
            :on-change="(file) => handleImport(file.raw)"
          >
            <el-button type="info">导入学生</el-button>
          </el-upload>
          <el-button type="warning" @click="handleExport"> 导出学生 </el-button>
          <el-button type="primary" @click="handleCreate"> 新建学生 </el-button>
        </div>
      </div>
    </div>

    <Table
      :columns="columns"
      :data="tableData"
      :loading="loading"
      :pagination="pagination"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
    >
      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleView(row)"> 查看 </el-button>
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
