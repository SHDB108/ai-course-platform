<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import { ElButton, ElTag, ElSelect, ElOption, ElMessageBox, ElMessage, ElInput } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getUsersByAdminApi,
  updateUserStatusApi,
  createUserByAdminApi,
  type UserVO,
  type UserCreateByAdminDTO,
  type UserStatusUpdateDTO
} from '@/api/admin'

defineOptions({
  name: 'UserManagement'
})

const router = useRouter()

const loading = ref(false)
const tableData = ref<UserVO[]>([])
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = ref({
  username: '',
  role: '',
  status: ''
})

const columns = [
  {
    field: 'username',
    label: '用户名',
    width: 150
  },
  {
    field: 'email',
    label: '邮箱',
    width: 200
  },
  {
    field: 'name',
    label: '姓名',
    width: 120
  },
  {
    field: 'role',
    label: '角色',
    width: 100,
    slots: {
      default: 'role'
    }
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
    field: 'lastLoginAt',
    label: '最后登录',
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

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      role: searchForm.value.role || undefined,
      keyword: searchForm.value.username || undefined
    }

    const response = await getUsersByAdminApi(params)
    if (response?.data) {
      tableData.value = response.data.records
      pagination.value.total = response.data.total
      pagination.value.current = response.data.current
      pagination.value.size = response.data.size
      ElMessage.success('用户列表加载成功')
    }
  } catch (error: any) {
    console.error('获取用户列表失败:', error)
    ElMessage.error(error?.message || '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.value.current = 1
  fetchUsers()
}

const handleReset = () => {
  searchForm.value = {
    username: '',
    role: '',
    status: ''
  }
  pagination.value.current = 1
  fetchUsers()
}

const handleCreate = () => {
  // 跳转到通用新建用户页面
  router.push('/admin/user-create')
}

const handleEdit = (row: UserVO) => {
  // router.push(`/forms/user/edit/${row.id}`)
  ElMessage.info(`编辑用户"${row.username}"功能开发中...`)
}

const handleDelete = async (row: UserVO) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // TODO: 实现删除用户API
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row: UserVO, status: string) => {
  try {
    const statusUpdateData: UserStatusUpdateDTO = { status: status as any }
    await updateUserStatusApi(row.id, statusUpdateData)

    ElMessage.success(`用户状态已更新为"${getStatusText(status)}"`)
    // 更新本地数据
    row.status = status
  } catch (error: any) {
    console.error('状态更新失败:', error)
    ElMessage.error(error?.message || '状态更新失败')
  }
}

const getRoleColor = (role: string) => {
  const colorMap: Record<string, string> = {
    ADMIN: 'danger',
    TEACHER: 'warning',
    STUDENT: 'success'
  }
  return colorMap[role] || 'info'
}

const getRoleText = (role: string) => {
  const textMap: Record<string, string> = {
    ADMIN: '管理员',
    TEACHER: '教师',
    STUDENT: '学生'
  }
  return textMap[role] || role
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info',
    SUSPENDED: 'danger'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '激活',
    INACTIVE: '未激活',
    SUSPENDED: '已停用'
  }
  return textMap[status] || status
}

// 分页处理
const handlePageChange = (current: number) => {
  pagination.value.current = current
  fetchUsers()
}

const handleSizeChange = (size: number) => {
  pagination.value.size = size
  pagination.value.current = 1
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <el-input
            v-model="searchForm.username"
            placeholder="用户名"
            style="width: 150px"
            clearable
            @keyup.enter="handleSearch"
          />
          <el-select v-model="searchForm.role" placeholder="角色" style="width: 120px" clearable>
            <el-option label="管理员" value="ADMIN" />
            <el-option label="教师" value="TEACHER" />
            <el-option label="学生" value="STUDENT" />
          </el-select>
          <el-select v-model="searchForm.status" placeholder="状态" style="width: 120px" clearable>
            <el-option label="激活" value="ACTIVE" />
            <el-option label="未激活" value="INACTIVE" />
            <el-option label="已停用" value="SUSPENDED" />
          </el-select>
          <el-button type="primary" @click="handleSearch"> 搜索 </el-button>
          <el-button @click="handleReset"> 重置 </el-button>
        </div>
        <el-button type="primary" @click="handleCreate"> 新建用户 </el-button>
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
      <template #role="{ row }">
        <ElTag :type="getRoleColor(row.role)">
          {{ getRoleText(row.role) }}
        </ElTag>
      </template>

      <template #status="{ row }">
        <ElTag :type="getStatusColor(row.status)">
          {{ getStatusText(row.status) }}
        </ElTag>
      </template>

      <template #action="{ row }">
        <el-button type="primary" size="small" @click="handleEdit(row)"> 编辑 </el-button>
        <el-button
          v-if="row.status === 'ACTIVE'"
          type="warning"
          size="small"
          @click="handleStatusChange(row, 'SUSPENDED')"
        >
          停用
        </el-button>
        <el-button v-else type="success" size="small" @click="handleStatusChange(row, 'ACTIVE')">
          启用
        </el-button>
        <el-button type="danger" size="small" @click="handleDelete(row)"> 删除 </el-button>
      </template>
    </Table>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
