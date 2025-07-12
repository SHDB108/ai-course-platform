<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Descriptions } from '@/components/Descriptions'
import { ElCard, ElButton, ElTag, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getTaskApi, type TaskVO } from '@/api/task'

defineOptions({
  name: 'TaskDetail'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const taskInfo = ref<TaskVO>()

const taskId = ref<number>(Number(route.params.id))

const taskDescriptions = ref([
  {
    field: 'title',
    label: '任务标题'
  },
  {
    field: 'description',
    label: '任务描述'
  },
  {
    field: 'courseName',
    label: '所属课程'
  },
  {
    field: 'type',
    label: '任务类型',
    slots: {
      default: 'type'
    }
  },
  {
    field: 'dueDate',
    label: '截止时间'
  },
  {
    field: 'maxScore',
    label: '满分'
  },
  {
    field: 'published',
    label: '发布状态',
    slots: {
      default: 'published'
    }
  },
  {
    field: 'instructions',
    label: '任务说明'
  },
  {
    field: 'createdAt',
    label: '创建时间'
  },
  {
    field: 'updatedAt',
    label: '更新时间'
  }
])

const fetchTask = async () => {
  loading.value = true
  try {
    const res = await getTaskApi(taskId.value)
    if (res.data) {
      taskInfo.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取任务信息失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  router.push(`/task/edit/${taskId.value}`)
}

const handleViewSubmissions = () => {
  router.push(`/task/submissions/${taskId.value}`)
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    ASSIGNMENT: 'primary',
    QUIZ: 'success',
    PROJECT: 'warning',
    EXAM: 'danger'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    ASSIGNMENT: '作业',
    QUIZ: '测验',
    PROJECT: '项目',
    EXAM: '考试'
  }
  return textMap[type] || type
}

onMounted(() => {
  fetchTask()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <ElButton type="primary" @click="handleEdit"> 编辑任务 </ElButton>
      <ElButton type="success" @click="handleViewSubmissions"> 查看提交 </ElButton>
      <ElButton @click="router.push('/task/list')"> 返回列表 </ElButton>
    </div>

    <ElCard title="任务详情">
      <Descriptions :data="taskInfo" :schema="taskDescriptions" :loading="loading">
        <template #type="{ row }">
          <ElTag :type="getTypeColor(row.type)">
            {{ getTypeText(row.type) }}
          </ElTag>
        </template>

        <template #published="{ row }">
          <ElTag :type="row.published ? 'success' : 'info'">
            {{ row.published ? '已发布' : '未发布' }}
          </ElTag>
        </template>
      </Descriptions>

      <div v-if="taskInfo?.attachments && taskInfo.attachments.length > 0" class="mt-20px">
        <h4 class="text-lg font-semibold mb-10px">附件</h4>
        <div class="space-y-2">
          <div v-for="(attachment, index) in taskInfo.attachments" :key="index">
            <a :href="attachment" target="_blank" class="text-blue-600 hover:text-blue-800">
              {{ attachment }}
            </a>
          </div>
        </div>
      </div>
    </ElCard>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
