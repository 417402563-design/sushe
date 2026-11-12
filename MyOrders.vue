<template>
  <div class="page">
    <div class="page-title">我的报修 · 进度与历史</div>
    <el-card shadow="never">
      <el-table :data="orders" v-loading="loading" empty-text="暂无报修记录">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="category" label="类别" width="90" />
        <el-table-column label="位置" width="140">
          <template #default="{ row }">{{ row.building }} {{ row.roomNo }}</template>
        </el-table-column>
        <el-table-column label="紧急" width="80">
          <template #default="{ row }">
            <el-tag :type="row.urgency === '紧急' ? 'danger' : 'info'" size="small">{{ row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusOf(row.status).type" size="small">{{ statusOf(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workerName" label="维修工" width="100">
          <template #default="{ row }">{{ row.workerName || '待派单' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="150">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDetail(row)">详情</el-button>
            <el-button size="small" v-if="row.status === 'PENDING'" type="warning" @click="cancel(row)">撤销</el-button>
            <el-button size="small" v-if="canEval(row)" type="success" @click="openEval(row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情 + 进度时间线 + 在线沟通 -->
    <el-drawer v-model="detailVisible" :title="cur.title" size="460px">
      <el-steps direction="vertical" :active="stepActive(cur.status)" finish-status="success">
        <el-step title="提交报修" :description="fmtDate(cur.createTime)" />
        <el-step title="管理员派单" :description="cur.workerName ? '维修工：' + cur.workerName : '待派单'" />
        <el-step title="维修处理" :description="cur.acceptTime ? '开始：' + fmtDate(cur.acceptTime) : ''" />
        <el-step title="完成审核" :description="cur.finishTime ? fmtDate(cur.finishTime) : ''" />
        <el-step title="服务闭环" :description="cur.status === 'CONFIRMED' ? '已完成' : ''" />
      </el-steps>
      <el-divider />
      <p><b>详细描述：</b>{{ cur.description }}</p>
      <p v-if="cur.finishRemark"><b>维修结果：</b>{{ cur.finishRemark }}</p>
      <p v-if="cur.auditRemark"><b>审核意见：</b>{{ cur.auditRemark }}</p>
      <img v-if="cur.imagePath" :src="cur.imagePath" style="max-width: 100%; margin-top: 8px; border-radius: 6px" />

      <template v-if="cur.workerId">
        <el-divider>与维修工在线沟通</el-divider>
        <div class="chat-box">
          <div v-for="m in chat" :key="m.id" :class="['chat-msg', m.fromUserId === uid ? 'mine' : '']">
            {{ m.content }}
          </div>
        </div>
        <div style="display: flex; gap: 8px; margin-top: 10px">
          <el-input v-model="chatText" placeholder="发送消息…" @keyup.enter="sendChat" />
          <el-button type="primary" @click="sendChat">发送</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 评价 -->
    <el-dialog v-model="evalVisible" title="服务评价" width="480px">
      <el-form label-width="90px">
        <el-form-item label="综合评分">
          <el-rate v-model="ev.score" />
        </el-form-item>
        <el-form-item label="响应速度"><el-rate v-model="ev.speedScore" /></el-form-item>
        <el-form-item label="维修质量"><el-rate v-model="ev.qualityScore" /></el-form-item>
        <el-form-item label="服务态度"><el-rate v-model="ev.attitudeScore" /></el-form-item>
        <el-form-item label="评价内容"><el-input v-model="ev.comment" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEval">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'
import { store } from '../../store'
import { statusOf, fmtDate } from '../../utils'

const orders = ref([])
const loading = ref(false)
const uid = store.state.user.id

const detailVisible = ref(false)
const cur = ref({})
const chat = ref([])
const chatText = ref('')

const evalVisible = ref(false)
const ev = reactive({ orderId: null, score: 5, speedScore: 5, qualityScore: 5, attitudeScore: 5, comment: '' })

const stepMap = { PENDING: 1, ASSIGNED: 2, PROCESSING: 3, DONE: 4, REWORK: 3, CONFIRMED: 5, CANCELED: 1 }
function stepActive(s) { return stepMap[s] || 0 }

function canEval(row) {
  return ['DONE', 'CONFIRMED'].includes(row.status)
}

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/orders/my')
    orders.value = r.data
  } finally { loading.value = false }
}

async function openDetail(row) {
  cur.value = row
  detailVisible.value = true
  if (row.workerId) {
    const r = await api.get('/api/messages/conversation/' + row.workerId)
    chat.value = r.data
  }
}

async function sendChat() {
  if (!chatText.value) return
  await api.post('/api/messages/send', { toUserId: cur.value.workerId, content: chatText.value, type: 'CHAT', orderId: cur.value.id })
  chat.value.push({ content: chatText.value, fromUserId: uid })
  chatText.value = ''
}

async function cancel(row) {
  await ElMessageBox.confirm('确定撤销该报修单？', '提示', { type: 'warning' })
  await api.put('/api/orders/' + row.id + '/cancel')
  ElMessage.success('已撤销')
  load()
}

function openEval(row) {
  Object.assign(ev, { orderId: row.id, score: 5, speedScore: 5, qualityScore: 5, attitudeScore: 5, comment: '' })
  evalVisible.value = true
}

async function submitEval() {
  await api.post('/api/evaluations', ev)
  ElMessage.success('感谢您的评价')
  evalVisible.value = false
  load()
}

onMounted(load)
</script>

<style scoped>
.chat-box { max-height: 260px; overflow-y: auto; background: #f7f8fa; padding: 10px; border-radius: 6px; }
.chat-msg { background: #fff; padding: 6px 10px; border-radius: 6px; margin-bottom: 8px; display: inline-block; }
.chat-msg.mine { background: #d9ecff; float: right; clear: both; }
</style>
