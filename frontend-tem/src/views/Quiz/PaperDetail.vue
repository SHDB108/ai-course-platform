<script setup lang="ts">
import { ContentWrap } from '@/components/ContentWrap'
import { Descriptions } from '@/components/Descriptions'
import { Table } from '@/components/Table'
import { ElCard, ElButton, ElTag, ElRow, ElCol, ElMessage } from 'element-plus'
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getPaperApi, type QuizPaperDetailsVO } from '@/api/quiz'

defineOptions({
  name: 'PaperDetail'
})

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const paperInfo = ref<QuizPaperDetailsVO>()

const paperId = ref<number>(Number(route.params.id))

const paperDescriptions = ref([
  {
    field: 'title',
    label: '试卷标题'
  },
  {
    field: 'description',
    label: '试卷描述'
  },
  {
    field: 'courseName',
    label: '所属课程'
  },
  {
    field: 'totalScore',
    label: '总分'
  },
  {
    field: 'timeLimit',
    label: '时长(分钟)'
  },
  {
    field: 'questionCount',
    label: '题目数量'
  },
  {
    field: 'status',
    label: '状态',
    slots: {
      default: 'status'
    }
  },
  {
    field: 'createdAt',
    label: '创建时间'
  }
])

const questionColumns = [
  {
    field: 'content',
    label: '题目内容',
    width: 300,
    slots: {
      default: 'content'
    }
  },
  {
    field: 'type',
    label: '题型',
    width: 120,
    slots: {
      default: 'type'
    }
  },
  {
    field: 'difficulty',
    label: '难度',
    width: 100,
    slots: {
      default: 'difficulty'
    }
  },
  {
    field: 'correctAnswer',
    label: '正确答案',
    width: 150
  },
  {
    field: 'explanation',
    label: '答案解析',
    width: 200,
    slots: {
      default: 'explanation'
    }
  }
]

const fetchPaper = async () => {
  loading.value = true
  try {
    const res = await getPaperApi(paperId.value)
    if (res.data) {
      paperInfo.value = res.data
    }
  } catch (error) {
    ElMessage.error('获取试卷信息失败')
  } finally {
    loading.value = false
  }
}

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return colorMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已归档'
  }
  return textMap[status] || status
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    SINGLE_CHOICE: 'primary',
    MULTIPLE_CHOICE: 'success',
    TRUE_FALSE: 'info',
    SHORT_ANSWER: 'warning',
    ESSAY: 'danger'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    SINGLE_CHOICE: '单选题',
    MULTIPLE_CHOICE: '多选题',
    TRUE_FALSE: '判断题',
    SHORT_ANSWER: '简答题',
    ESSAY: '论述题'
  }
  return textMap[type] || type
}

const getDifficultyColor = (difficulty: string) => {
  const colorMap: Record<string, string> = {
    EASY: 'success',
    MEDIUM: 'warning',
    HARD: 'danger'
  }
  return colorMap[difficulty] || 'info'
}

const getDifficultyText = (difficulty: string) => {
  const textMap: Record<string, string> = {
    EASY: '简单',
    MEDIUM: '中等',
    HARD: '困难'
  }
  return textMap[difficulty] || difficulty
}

onMounted(() => {
  fetchPaper()
})
</script>

<template>
  <ContentWrap>
    <div class="mb-20px">
      <ElButton @click="router.push('/quiz/papers')"> 返回试卷列表 </ElButton>
    </div>

    <ElRow :gutter="20">
      <ElCol :span="24">
        <ElCard title="试卷信息" class="mb-20px">
          <Descriptions :data="paperInfo" :schema="paperDescriptions" :loading="loading">
            <template #status="{ row }">
              <ElTag :type="getStatusColor(row.status)">
                {{ getStatusText(row.status) }}
              </ElTag>
            </template>
          </Descriptions>
        </ElCard>
      </ElCol>

      <ElCol :span="24">
        <ElCard title="题目列表">
          <Table
            :columns="questionColumns"
            :data="paperInfo?.questions || []"
            :loading="loading"
            :pagination="false"
          >
            <template #content="{ row }">
              <div class="max-w-300px">
                <div class="truncate" :title="row.content">
                  {{ row.content }}
                </div>
              </div>
            </template>

            <template #type="{ row }">
              <ElTag :type="getTypeColor(row.type)">
                {{ getTypeText(row.type) }}
              </ElTag>
            </template>

            <template #difficulty="{ row }">
              <ElTag :type="getDifficultyColor(row.difficulty)">
                {{ getDifficultyText(row.difficulty) }}
              </ElTag>
            </template>

            <template #explanation="{ row }">
              <div class="max-w-200px">
                <div class="truncate" :title="row.explanation">
                  {{ row.explanation }}
                </div>
              </div>
            </template>
          </Table>
        </ElCard>
      </ElCol>
    </ElRow>
  </ContentWrap>
</template>

<style scoped lang="less"></style>
