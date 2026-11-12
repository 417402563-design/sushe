<template>
  <div class="page">
    <div class="page-title">消息中心</div>
    <el-tabs v-model="tab">
      <el-tab-pane label="通知消息" name="notify">
        <div class="mb12">
          <el-button size="small" @click="readAll">全部标记已读</el-button>
        </div>
        <el-card shadow="never">
          <el-empty v-if="!notifyList.length" description="暂无通知" />
          <div v-for="m in notifyList" :key="m.id" class="msg-row" :class="{ unread: !m.readFlag }" @click="read(m)">
            <el-tag size="small" :type="m.type === 'ORDER' ? 'warning' : 'info'">
              {{ m.type === 'ORDER' ? '工单' : '系统' }}
            </el-tag>
            <span class="msg-content">{{ m.content }}</span>
            <span class="msg-time">{{ fmtDate(m.createTime) }}</span>
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="在线沟通" name="chat">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-card shadow="never" body-style="padding: 0">
              <div class="contact-title">联系人</div>
              <el-empty v-if="!contacts.length" description="暂无会话对象" :image-size="60" />
              <div v-for="c in contacts" :key="c.id" class="contact" :class="{ active: cur && cur.id === c.id }"
                   @click="openChat(c)">
                <el-avatar :size="34">{{ c.realName ? c.realName[0] : '?' }}</el-avatar>
                <span style="margin-left: 10px">{{ c.realName }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="16">
            <el-card shadow="never">
              <template #header>{{ cur ? '与 ' + cur.realName + ' 的对话' : '请选择联系人' }}</template>
              <div class="chat-box" v-if="cur">
                <div v-for="m in chat" :key="m.id" :class="['chat-msg', m.fromUserId === uid ? 'mine' : '']">
                  <div class="who">{{ m.fromUserName }}</div>
                  <div>{{ m.content }}</div>
                </div>
              </div>
              <el-empty v-else description="选择左侧联系人开始沟通" :image-size="70" />
              <div v-if="cur" style="display: flex; gap: 8px; margin-top: 12px">
                <el-input v-model="text" placeholder="输入消息，回车发送" @keyup.enter="send" />
                <el-button type="primary" @click="send">发送</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import { store } from '../store'
import { fmtDate } from '../utils'

const uid = store.state.user.id
const tab = ref('notify')
const inbox = ref([])
const chat = ref([])
const cur = ref(null)
const text = ref('')

const notifyList = computed(() => inbox.value.filter(m => m.type === 'SYSTEM' || m.type === 'ORDER'))
const contacts = computed(() => {
  const map = {}
  inbox.value.forEach(m => {
    if (m.fromUserId && m.fromUserId !== uid) map[m.fromUserId] = { id: m.fromUserId, realName: m.fromUserName }
  })
  return Object.values(map)
})

async function loadInbox() {
  const r = await api.get('/api/messages/inbox')
  inbox.value = r.data
}

async function read(m) {
  if (!m.readFlag) {
    await api.put('/api/messages/' + m.id + '/read')
    m.readFlag = true
  }
}

async function readAll() {
  await api.put('/api/messages/read-all')
  inbox.value.forEach(m => m.readFlag = true)
  ElMessage.success('已全部标记为已读')
}

async function openChat(c) {
  cur.value = c
  const r = await api.get('/api/messages/conversation/' + c.id)
  chat.value = r.data
}

async function send() {
  if (!text.value) return
  await api.post('/api/messages/send', { toUserId: cur.value.id, content: text.value, type: 'CHAT' })
  chat.value.push({ content: text.value, fromUserId: uid, fromUserName: store.state.user.realName })
  text.value = ''
}

onMounted(loadInbox)
</script>

<style scoped>
.msg-row { display: flex; align-items: center; gap: 10px; padding: 12px 6px; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.msg-row.unread .msg-content { font-weight: 600; }
.msg-content { flex: 1; }
.msg-time { color: #909399; font-size: 12px; }
.contact-title { padding: 12px; font-weight: 600; border-bottom: 1px solid #eee; }
.contact { display: flex; align-items: center; padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #f5f5f5; }
.contact.active { background: #ecf5ff; }
.chat-box { max-height: 420px; overflow-y: auto; }
.chat-msg { background: #f2f3f5; padding: 8px 12px; border-radius: 8px; margin-bottom: 10px; max-width: 70%; }
.chat-msg.mine { background: #d9ecff; margin-left: auto; }
.who { font-size: 12px; color: #909399; margin-bottom: 2px; }
</style>
