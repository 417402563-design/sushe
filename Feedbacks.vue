<template>
  <div class="page">
    <div class="page-title">意见反馈与投诉处理</div>
    <el-card shadow="never">
      <div class="mb12">
        <el-radio-group v-model="status" @change="load">
          <el-radio-button label="PENDING">待处理</el-radio-button>
          <el-radio-button label="">全部</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="list" v-loading="loading" empty-text="暂无反馈">
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.type === 'COMPLAINT' ? 'danger' : 'info'">
              {{ row.type === 'COMPLAINT' ? '投诉' : '意见' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="提交人" width="110" />
        <el-table-column prop="content" label="内容" min-width="220" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 'REPLIED' ? 'success' : 'warning'">
              {{ row.status === 'REPLIED' ? '已回复' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" min-width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="open(row)">回复</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" title="回复反馈" width="520px">
      <p style="color: #606266">{{ cur.content }}</p>
      <el-input v-model="reply" type="textarea" :rows="4" placeholder="输入回复内容" class="mt12" />
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="doReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const list = ref([])
const status = ref('PENDING')
const loading = ref(false)
const visible = ref(false)
const cur = ref({})
const reply = ref('')

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/feedback', status.value ? { status: status.value } : {})
    list.value = r.data
  } finally { loading.value = false }
}

function open(row) {
  cur.value = row
  reply.value = row.reply || ''
  visible.value = true
}

async function doReply() {
  if (!reply.value) return ElMessage.warning('请输入回复内容')
  await api.put('/api/feedback/' + cur.value.id + '/reply', { reply: reply.value })
  ElMessage.success('已回复')
  visible.value = false
  load()
}

onMounted(load)
</script>
