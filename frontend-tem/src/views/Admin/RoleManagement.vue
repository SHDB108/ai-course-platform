<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import {
  ElCard,
  ElButton,
  ElTable,
  ElTableColumn,
  ElTag,
  ElSwitch,
  ElMessage,
  ElMessageBox
} from 'element-plus'
import { ref, onMounted } from 'vue'
import {
  getRolesApi,
  getPermissionsApi,
  updateRoleStatusApi,
  deleteRoleApi,
  type RoleVO,
  type PermissionVO
} from '@/api/admin'

defineOptions({
  name: 'RoleManagement'
})

const loading = ref(false)

// 角色数据
const roles = ref<RoleVO[]>([])

// 权限数据
const permissions = ref<PermissionVO[]>([])

const fetchRoles = async () => {
  loading.value = true
  try {
    const response = await getRolesApi()
    if (response?.data) {
      roles.value = response.data
    }
  } catch (error: any) {
    console.error('获取角色数据失败:', error)
    // 如果API失败，使用Mock数据
    roles.value = [
      {
        id: 1,
        name: 'ADMIN',
        displayName: '系统管理员',
        description: '拥有系统的所有权限',
        userCount: 8,
        status: 1,
        permissions: [
          'user_manage',
          'course_manage',
          'system_config',
          'data_analytics',
          'role_manage'
        ]
      },
      {
        id: 2,
        name: 'TEACHER',
        displayName: '教师',
        description: '可以创建和管理课程，查看学生信息',
        userCount: 156,
        status: 1,
        permissions: [
          'course_create',
          'course_manage',
          'student_view',
          'assignment_manage',
          'grade_manage'
        ]
      },
      {
        id: 3,
        name: 'STUDENT',
        displayName: '学生',
        description: '可以选课学习，提交作业',
        userCount: 1089,
        status: 1,
        permissions: [
          'course_view',
          'course_enroll',
          'assignment_submit',
          'grade_view',
          'profile_edit'
        ]
      }
    ]
  } finally {
    loading.value = false
  }
}

const fetchPermissions = async () => {
  try {
    const response = await getPermissionsApi()
    if (response?.data) {
      permissions.value = response.data
    }
  } catch (error: any) {
    console.error('获取权限数据失败:', error)
    // 如果API失败，使用Mock数据
    permissions.value = [
      {
        id: 1,
        name: 'user_manage',
        displayName: '用户管理',
        category: '系统管理',
        description: '管理系统用户信息'
      },
      {
        id: 2,
        name: 'course_manage',
        displayName: '课程管理',
        category: '教学管理',
        description: '管理所有课程信息'
      },
      {
        id: 3,
        name: 'course_create',
        displayName: '创建课程',
        category: '教学管理',
        description: '创建新的课程'
      },
      {
        id: 4,
        name: 'course_view',
        displayName: '查看课程',
        category: '教学管理',
        description: '查看课程信息'
      },
      {
        id: 5,
        name: 'course_enroll',
        displayName: '选课',
        category: '学习管理',
        description: '选择课程学习'
      },
      {
        id: 6,
        name: 'student_view',
        displayName: '查看学生',
        category: '教学管理',
        description: '查看学生信息'
      },
      {
        id: 7,
        name: 'assignment_manage',
        displayName: '作业管理',
        category: '教学管理',
        description: '管理课程作业'
      },
      {
        id: 8,
        name: 'assignment_submit',
        displayName: '提交作业',
        category: '学习管理',
        description: '提交课程作业'
      },
      {
        id: 9,
        name: 'grade_manage',
        displayName: '成绩管理',
        category: '教学管理',
        description: '管理学生成绩'
      },
      {
        id: 10,
        name: 'grade_view',
        displayName: '查看成绩',
        category: '学习管理',
        description: '查看个人成绩'
      },
      {
        id: 11,
        name: 'system_config',
        displayName: '系统配置',
        category: '系统管理',
        description: '配置系统参数'
      },
      {
        id: 12,
        name: 'data_analytics',
        displayName: '数据分析',
        category: '系统管理',
        description: '查看数据分析报告'
      },
      {
        id: 13,
        name: 'role_manage',
        displayName: '角色管理',
        category: '系统管理',
        description: '管理用户角色和权限'
      },
      {
        id: 14,
        name: 'profile_edit',
        displayName: '编辑个人信息',
        category: '个人管理',
        description: '编辑个人资料'
      }
    ]
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    await Promise.all([fetchRoles(), fetchPermissions()])
  } finally {
    loading.value = false
  }
}

const handleRoleStatusChange = async (role: RoleVO) => {
  try {
    await updateRoleStatusApi(role.id, { status: role.status })
    const statusText = role.status ? '启用' : '禁用'
    ElMessage.success(`角色"${role.displayName}"已${statusText}`)
  } catch (error: any) {
    console.error('角色状态更新失败:', error)
    ElMessage.error(error?.message || '状态更新失败')
    // 回滚状态
    role.status = role.status ? 0 : 1
  }
}

const handleEditRole = (role: RoleVO) => {
  ElMessage.info(`编辑角色"${role.displayName}"功能开发中...`)
}

const handleViewPermissions = (role: RoleVO) => {
  ElMessage.info(`查看角色"${role.displayName}"权限详情功能开发中...`)
}

const handleCreateRole = () => {
  ElMessage.info('创建新角色功能开发中...')
}

const handleDeleteRole = async (role: RoleVO) => {
  if (role.userCount > 0) {
    ElMessage.warning('该角色下还有用户，无法删除')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除角色"${role.displayName}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteRoleApi(role.id)
    ElMessage.success('删除成功')
    fetchRoles()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

const getPermissionsByCategory = (category: string) => {
  return permissions.value.filter((p) => p.category === category)
}

const getPermissionCategories = () => {
  const categories = [...new Set(permissions.value.map((p) => p.category))]
  return categories
}

const hasPermission = (role: RoleVO, permissionName: string) => {
  return role.permissions.includes(permissionName)
}

const getPermissionColor = (hasPermission: boolean) => {
  return hasPermission ? 'success' : 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <ContentWrap title="角色权限管理" :loading="loading">
    <!-- 角色管理 -->
    <ElCard header="角色管理" class="mb-6">
      <div class="mb-4">
        <ElButton type="primary" @click="handleCreateRole"> 新建角色 </ElButton>
      </div>

      <ElTable :data="roles" style="width: 100%">
        <ElTableColumn prop="displayName" label="角色名称" width="150" />
        <ElTableColumn prop="name" label="角色标识" width="120" />
        <ElTableColumn prop="description" label="角色描述" min-width="200" />
        <ElTableColumn prop="userCount" label="用户数量" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.userCount }}人</span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <ElSwitch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleRoleStatusChange(row)"
            />
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="250" align="center">
          <template #default="{ row }">
            <ElButton type="primary" size="small" @click="handleViewPermissions(row)">
              查看权限
            </ElButton>
            <ElButton type="success" size="small" @click="handleEditRole(row)"> 编辑 </ElButton>
            <ElButton
              type="danger"
              size="small"
              :disabled="row.userCount > 0"
              @click="handleDeleteRole(row)"
            >
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </ElCard>

    <!-- 权限矩阵 -->
    <ElCard header="权限矩阵">
      <div class="permission-matrix">
        <div class="grid grid-cols-1 gap-6">
          <div v-for="category in getPermissionCategories()" :key="category">
            <h3 class="text-lg font-medium mb-3">{{ category }}</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div
                v-for="permission in getPermissionsByCategory(category)"
                :key="permission.id"
                class="permission-item border rounded-lg p-4"
              >
                <div class="font-medium mb-2">{{ permission.displayName }}</div>
                <div class="text-sm text-gray-500 mb-3">{{ permission.description }}</div>
                <div class="flex gap-2">
                  <ElTag
                    v-for="role in roles"
                    :key="role.id"
                    :type="getPermissionColor(hasPermission(role, permission.name))"
                    size="small"
                  >
                    {{ role.displayName }}
                  </ElTag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ElCard>
  </ContentWrap>
</template>

<style scoped lang="less">
.permission-matrix {
  .grid {
    display: grid;
  }
  .grid-cols-1 {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
  .gap-6 {
    gap: 1.5rem;
  }
  .gap-4 {
    gap: 1rem;
  }
  .gap-2 {
    gap: 0.5rem;
  }

  @media (min-width: 768px) {
    .md\:grid-cols-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }

  @media (min-width: 1024px) {
    .lg\:grid-cols-3 {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
  }
}

.permission-item {
  border: 1px solid #e5e7eb;
  border-radius: 0.5rem;
  padding: 1rem;
}

.text-lg {
  font-size: 1.125rem;
}

.font-medium {
  font-weight: 500;
}

.font-bold {
  font-weight: 700;
}

.text-sm {
  font-size: 0.875rem;
}

.text-gray-500 {
  color: #6b7280;
}

.mb-2 {
  margin-bottom: 0.5rem;
}

.mb-3 {
  margin-bottom: 0.75rem;
}

.mb-4 {
  margin-bottom: 1rem;
}

.mb-6 {
  margin-bottom: 1.5rem;
}

.flex {
  display: flex;
}
</style>
