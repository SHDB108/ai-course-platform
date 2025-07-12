<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage, ElInput } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getTeachersApi,
  updateTeacherStatusApi,
  deleteTeacherApi,
  type TeacherVO
} from '@/api/admin'

defineOptions({
  name: 'TeacherManagement'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref<TeacherVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  name: '',
  department: '',
  status: ''
})

const columns = [
  {
    field: 'username',
    label: '用户名',
    width: 120
  },
  {
    field: 'name',
    label: '姓名',
    width: 120
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'teacherNo',
    label: '教师工号',
    width: 120
  },
  {
    field: 'department',
    label: '部门',
    width: 150
  },
  {
    field: 'title',
    label: '职称',
    width: 120
  },
  {
    field: 'phone',
    label: '手机号',
    width: 130
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

const fetchTeachers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      keyword: searchForm.value.name || undefined,
      department: searchForm.value.department || undefined,
      status: searchForm.value.status || undefined
    }

    const response = await getTeachersApi(params)
    if (response?.data) {
      tableData.value = response.data.records
      pagination.value.total = response.data.total
      pagination.value.current = response.data.current
      pagination.value.size = response.data.size
    }
  } catch (error: any) {
    console.error('获取教师列表失败:', error)
    ElMessage.error(error?.message || '获取教师列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchTeachers()
}

const handleReset = () => {
  searchForm.value = {
    name: '',
    department: '',
    status: ''
  }
  pagination.value.current = 1
  fetchTeachers()
}

const handleCreate = () => {
  // 跳转到通用新建用户页面，并指定角色为教师
  router.push('/admin/user-create?role=TEACHER')
}

const handleEdit = (row: TeacherVO) => {
  // router.push(`/forms/teacher/edit/${row.id}`)
  ElMessage.info(`编辑教师"${row.name}"功能开发中...`)
}

const handleView = (row: TeacherVO) => {
  // router.push(`/details/teacher/${row.id}`)
  ElMessage.info(`查看教师"${row.name}"详情功能开发中...`)
}

const handleDelete = async (row: TeacherVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除教师"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteTeacherApi(row.id)
    ElMessage.success('删除成功')
    fetchTeachers()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

const handleStatusChange = async (row: TeacherVO, status: string) => {
  try {
    await updateTeacherStatusApi(row.id, { status })
    const statusText = getStatusText(status)
    ElMessage.success(`教师状态已更新为"${statusText}"`)
    row.status = status
  } catch (error: any) {
    console.error('状态更新失败:', error)
    ElMessage.error(error?.message || '状态更新失败')
  }
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    SUSPENDED: 'danger',
    INACTIVE: 'info'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '在职',
    SUSPENDED: '停职',
    INACTIVE: '离职'
  }
  return textMap[status] || status
}

// 分页处理
const handlePageChange = (current: number) => {
  pagination.value.current = current
  fetchTeachers()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchTeachers()
}

onMounted(() => {
  fetchTeachers()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchForm.name"
            placeholder="姓名/工号"
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-input
            v-model="searchForm.department"
            placeholder="部门"
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
            <el-option label="在职" value="ACTIVE" />
            <el-option label="停职" value="SUSPENDED" />
            <el-option label="离职" value="INACTIVE" />
          </el-select>
          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建教师 </el-button>
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
          停职
        </el-button>
        <el-button
          v-else-if="row.status === 'SUSPENDED'"
          type="success"
          size="small"
          @click="handleStatusChange(row, 'ACTIVE')"
        >
          复职
        </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
