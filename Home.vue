<template>
  <div class="page">
    <el-card class="mb12" shadow="hover">
      <div style="display: flex; align-items: center; justify-content: space-between">
        <div>
          <h2 style="margin: 0">欢迎回来，{{ user ? user.realName : '' }}</h2>
          <p style="color: #909399; margin: 8px 0 0">{{ subtitle }}</p>
        </div>
        <el-button v-if="role === 'STUDENT'" type="primary" :icon="Tools" @click="$router.push('/student/repair')">
          一键报修
        </el-button>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>通知公告</template>
          <el-empty v-if="!notices.length" description="暂无公告" />
          <div v-for="n in notices" :key="n.id" class="notice-item" @click="openNotice(n)">
            <el-tag v-if="n.top" type="danger" size="small" effect="dark">置顶</el-tag>
            <span class="notice-title">{{ n.title }}</span>
            <span class="notice-time">{{ fmtDate(n.publishTime) }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never" class="mb12">
          <template #header>快捷入口</template>
          <div class="quick">
            <el-button text v-for="q in quickLinks" :key="q.path" @click="$router.push(q.path)">
              <el-icon><component :is="q.icon" /></el-icon>&nbsp;{{ q.label }}
            </el-button>
          </div>
        </el-card>

        <el-card shadow="never">
          <template #header>智能知识库 · 自助查询</template>
          <el-input v-model="kw" placeholder="输入故障关键词，如“网络 断网”" @keyup.enter="toKnowledge">
            <template #append><el-button :icon="Search" @click="toKnowledge" /></template>
          </el-input>
          <p style="color: #909399; font-size: 12px; margin-top: 10px">
            许多小问题可以按知识库指引自助解决，省去等待时间。
          </p>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="noticeVisible" :title="cur.title" width="600px">
      <div style="white-space: pre-wrap">{{ cur.content }}</div>
      <div style="color: #909399; margin-top: 12px">{{ cur.author }} · {{ fmtDate(cur.publishTime) }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Tools, Search } from '@element-plus/icons-vue'
import { api } from '../api'
import { store } from '../store'
import { fmtDate } from '../utils'

const router = useRouter()
const user = computed(() => store.state.user)
const role = computed(() => store.role())
const notices = ref([])
const cur = ref({})
const noticeVisible = ref(false)
const kw = ref('')

const subtitle = computed(() => ({
  ADMIN: '在这里进行系统管理、工单监控审核、备件库存与数据统计。',
  WORKER: '及时接收工单、反馈维修进度、申请维修材料。',
  STUDENT: '宿舍设施坏了？一键报修，进度实时可查，还能自助查询解决方案。'
}[role.value] || ''))

const quickLinks = computed(() => {
  if (role.value === 'ADMIN') return [
    { path: '/admin/dashboard', label: '数据统计', icon: 'DataLine' },
    { path: '/admin/orders', label: '工单审核', icon: 'Tickets' },
    { path: '/admin/parts', label: '备件库存', icon: 'Box' },
    { path: '/admin/notices', label: '发布公告', icon: 'Notification' }
  ]
  if (role.value === 'WORKER') return [
    { path: '/worker/pool', label: '接收工单', icon: 'Tickets' },
    { path: '/worker/orders', label: '我的工单', icon: 'List' },
    { path: '/worker/materials', label: '材料申请', icon: 'Document' },
    { path: '/worker/schedule', label: '工时排班', icon: 'Calendar' }
  ]
  return [
    { path: '/student/orders', label: '进度查询', icon: 'List' },
    { path: '/student/knowledge', label: '知识库', icon: 'Reading' },
    { path: '/student/feedback', label: '投诉意见', icon: 'ChatDotRound' },
    { path: '/messages', label: '消息通知', icon: 'Message' }
  ]
})

function openNotice(n) {
  cur.value = n
  noticeVisible.value = true
}

function toKnowledge() {
  router.push({ path: '/student/knowledge', query: { kw: kw.value } })
}

onMounted(async () => {
  const r = await api.get('/api/notices')
  notices.value = r.data
})
</script>

<style scoped>
.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 4px;
  border-bottom: 1px dashed #eee;
  cursor: pointer;
}
.notice-title { flex: 1; }
.notice-time { color: #909399; font-size: 12px; }
.quick { display: flex; flex-wrap: wrap; gap: 8px; }
</style>
