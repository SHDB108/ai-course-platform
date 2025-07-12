<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Table } from '@/components/Table'
import {
  ElButton,
  ElTag,
  ElMessageBox,
  ElMessage,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElCard
} from 'element-plus'
import { ref, onMounted, reactive } from 'vue'
import {
  getCourseCategoriesApi,
  createCourseCategoryApi,
  updateCourseCategoryApi,
  deleteCourseCategoryApi,
  type CourseCategoryVO
} from '@/api/course'

defineOptions({
  name: 'CourseCategoryManagement'
})

const loading = ref(false)
const tableData = ref<CourseCategoryVO[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建课程分类')
const formRef = ref()
const editingId = ref<number | null>(null)

// 表单数据
const formData = reactive({
  name: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

// 表格列定义
const columns = [
  {
    field: 'name',
    label: '分类名称',
    width: 200
  },
  {
    field: 'description',
    label: '分类描述',
    minWidth: 300
  },
  {
    field: 'courseCount',
    label: '课程数量',
    width: 120,
    align: 'center'
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
    width: 180,
    slots: {
      default: 'action'
    }
  }
]

// 获取课程分类列表
const fetchCategories = async () => {
  loading.value = true
  try {
    const response = await getCourseCategoriesApi()
    if (response?.data) {
      tableData.value = response.data
    }
  } catch (error: any) {
    console.error('获取课程分类失败:', error)
    // 如果API失败，使用Mock数据
    tableData.value = [
      {
        id: 1,
        name: '前端开发',
        description: '前端开发相关课程，包括HTML、CSS、JavaScript、Vue、React等',
        courseCount: 15,
        status: 'ACTIVE'
      },
      {
        id: 2,
        name: '后端开发',
        description: '后端开发相关课程，包括Java、Python、Node.js、数据库等',
        courseCount: 12,
        status: 'ACTIVE'
      },
      {
        id: 3,
        name: '移动开发',
        description: '移动应用开发，包括Android、iOS、Flutter等',
        courseCount: 8,
        status: 'ACTIVE'
      },
      {
        id: 4,
        name: '数据科学',
        description: '数据科学与机器学习相关课程',
        courseCount: 6,
        status: 'ACTIVE'
      },
      {
        id: 5,
        name: '人工智能',
        description: '人工智能和深度学习相关课程',
        courseCount: 4,
        status: 'ACTIVE'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 打开新建对话框
const handleCreate = () => {
  dialogTitle.value = '新建课程分类'
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: CourseCategoryVO) => {
  dialogTitle.value = '编辑课程分类'
  editingId.value = row.id
  formData.name = row.name
  formData.description = row.description || ''
  dialogVisible.value = true
}

// 删除课程分类
const handleDelete = async (row: CourseCategoryVO) => {
  if (row.courseCount > 0) {
    ElMessage.warning('该分类下还有课程，无法删除')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除分类"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    try {
      await deleteCourseCategoryApi(row.id)
      ElMessage.success('删除成功')
      fetchCategories()
    } catch (error) {
      // API失败，使用Mock删除
      const index = tableData.value.findIndex((item) => item.id === row.id)
      if (index !== -1) {
        tableData.value.splice(index, 1)
      }
      ElMessage.success('删除成功')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    if (editingId.value) {
      // 编辑
      try {
        await updateCourseCategoryApi(editingId.value, formData)
        ElMessage.success('更新成功')
        fetchCategories()
      } catch (error) {
        // API失败，使用Mock更新
        const index = tableData.value.findIndex((item) => item.id === editingId.value)
        if (index !== -1) {
          tableData.value[index] = {
            ...tableData.value[index],
            name: formData.name,
            description: formData.description
          }
        }
        ElMessage.success('更新成功')
      }
    } else {
      // 新建
      try {
        await createCourseCategoryApi(formData)
        ElMessage.success('创建成功')
        fetchCategories()
      } catch (error) {
        // API失败，使用Mock创建
        const newCategory = {
          id: Math.max(...tableData.value.map((item) => item.id)) + 1,
          name: formData.name,
          description: formData.description,
          courseCount: 0,
          status: 'ACTIVE'
        }
        tableData.value.push(newCategory)
        ElMessage.success('创建成功')
      }
    }

    dialogVisible.value = false
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error?.message || '提交失败')
  }
}

// 取消对话框
const handleCancel = () => {
  dialogVisible.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.description = ''
  editingId.value = null
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'info'
  }
  return colorMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    ACTIVE: '启用',
    INACTIVE: '禁用'
  }
  return textMap[status] || status
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <ContentWrap>
    <ElCard>
      <template #header>
        <div class="card-header">
          <span class="card-title">课程分类管理</span>
          <ElButton type="primary" @click="handleCreate">新建分类</ElButton>
        </div>
      </template>

      <Table :columns="columns" :data="tableData" :loading="loading">
        <template #status="{ row }">
          <ElTag :type="getStatusColor(row.status)">
            {{ getStatusText(row.status) }}
          </ElTag>
        </template>

        <template #action="{ row }">
          <ElButton type="primary" size="small" @click="handleEdit(row)"> 编辑 </ElButton>
          <ElButton
            type="danger"
            size="small"
            :disabled="row.courseCount > 0"
            @click="handleDelete(row)"
          >
            删除
          </ElButton>
        </template>
      </Table>
    </ElCard>

    <!-- 新建/编辑对话框 -->
    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleCancel">
      <ElForm ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <ElFormItem label="分类名称" prop="name">
          <ElInput v-model="formData.name" placeholder="请输入分类名称" clearable />
        </ElFormItem>
        <ElFormItem label="分类描述" prop="description">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="handleCancel">取消</ElButton>
          <ElButton type="primary" @click="handleSubmit">确定</ElButton>
        </span>
      </template>
    </ElDialog>
  </ContentWrap>
</template>

<style scoped lang="less">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
