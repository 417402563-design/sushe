<template>
  <el-container style="height: 100vh">
    <el-aside width="230px" style="background: #2b3648">
      <div class="logo">宿舍维修管理</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#2b3648"
        text-color="#c9d3e0"
        active-text-color="#ffffff">
        <template v-for="m in menus" :key="m.path">
          <el-menu-item :index="m.path">
            <el-icon><component :is="m.icon" /></el-icon>
            <span>{{ m.label }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background: #fff; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #eee">
        <span style="color: #909399">{{ roleLabel }}</span>
        <div style="display: flex; align-items: center; gap: 18px">
          <el-badge :value="unread" :hidden="unread === 0" class="msg-badge">
            <el-icon size="20" @click="$router.push('/messages')" style="cursor: pointer"><Bell /></el-icon>
          </el-badge>
          <span>{{ user ? user.realName : '' }}</span>
          <el-button size="small" @click="$router.push('/profile')">个人中心</el-button>
          <el-button size="small" type="danger" plain @click="doLogout">退出</el-button>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { store } from '../store'
import { api } from '../api'

const router = useRouter()
const user = computed(() => store.state.user)
const role = computed(() => store.role())
const unread = ref(0)

const roleLabel = computed(() =>
  ({ ADMIN: '管理员', WORKER: '维修工', STUDENT: '学生' }[role.value] || ''))

const menus = computed(() => {
  if (role.value === 'ADMIN') {
    return [
      { path: '/admin/dashboard', label: '数据统计', icon: 'DataLine' },
      { path: '/admin/orders', label: '工单监控审核', icon: 'Tickets' },
      { path: '/admin/users', label: '维修工/学生管理', icon: 'User' },
      { path: '/admin/rooms', label: '宿舍信息管理', icon: 'House' },
      { path: '/admin/parts', label: '备件库存', icon: 'Box' },
      { path: '/admin/materials', label: '材料申请审批', icon: 'Document' },
      { path: '/admin/notices', label: '通知公告', icon: 'Notification' },
      { path: '/admin/feedback', label: '意见反馈投诉', icon: 'ChatDotRound' },
      { path: '/student/knowledge', label: '知识库维护', icon: 'Reading' },
      { path: '/messages', label: '消息中心', icon: 'Message' }
    ]
  }
  if (role.value === 'WORKER') {
    return [
      { path: '/home', label: '工作台', icon: 'HomeFilled' },
      { path: '/worker/pool', label: '工单接收', icon: 'Tickets' },
      { path: '/worker/orders', label: '我的工单/进度', icon: 'List' },
      { path: '/worker/schedule', label: '工时排班', icon: 'Calendar' },
      { path: '/worker/materials', label: '材料申请', icon: 'Document' },
      { path: '/worker/performance', label: '绩效与评价', icon: 'Medal' },
      { path: '/messages', label: '消息通讯', icon: 'Message' }
    ]
  }
  // STUDENT
  return [
    { path: '/home', label: '首页', icon: 'HomeFilled' },
    { path: '/student/repair', label: '一键报修', icon: 'Tools' },
    { path: '/student/orders', label: '进度/历史查询', icon: 'List' },
    { path: '/student/knowledge', label: '智能知识库', icon: 'Reading' },
    { path: '/student/feedback', label: '投诉意见', icon: 'ChatDotRound' },
    { path: '/messages', label: '消息通知', icon: 'Message' }
  ]
})

async function loadUnread() {
  try {
    const r = await api.get('/api/messages/unread')
    unread.value = r.data || 0
  } catch (e) {}
}

function doLogout() {
  api.logout().finally(() => {
    store.logout()
    router.push('/login')
  })
}

onMounted(loadUnread)
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 2px;
}
:deep(.el-menu) { border-right: none; }
</style>
