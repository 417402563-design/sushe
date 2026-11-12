<template>
  <div class="page">
    <div class="page-title">工单监控与审核</div>
    <el-card shadow="never">
      <div class="mb12">
        <el-radio-group v-model="status" @change="load">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="PENDING">待派单</el-radio-button>
          <el-radio-button label="PROCESSING">维修中</el-radio-button>
          <el-radio-button label="DONE">待审核</el-radio-button>
          <el-radio-button label="CONFIRMED">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="list" v-loading="loading" empty-text="暂无工单">
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="category" label="类别" width="80" />
        <el-table-column label="位置" width="130">
          <template #default="{ row }">{{ row.building }} {{ row.roomNo }}</template>
        </el-table-column>
        <el-table-column prop="studentName" label="报修人" width="90" />
        <el-table-column label="紧急" width="70">
          <template #default="{ row }">
            <el-tag :type="row.urgency === '紧急' ? 'danger' : 'info'" size="small">{{ row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusOf(row.status).type" size="small">{{ statusOf(row.status).label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="workerName" label="维修工" width="90">
          <template #default="{ row }">{{ row.workerName || '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" width="140">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="open(row)">详情</el-button>
            <el-button size="small" type="primary" v-if="row.status === 'PENDING'" @click="openAssign(row)">派单</el-button>
            <el-button size="small" type="success" v-if="row.status === 'DONE'" @click="openAudit(row)">审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="assignVisible" title="派单给维修工" width="440px">
      <el-select v-model="assignWorkerId" placeholder="选择维修工" style="width: 100%">
        <el-option v-for="w in workers" :key="w.id"
                   :label="w.realName + '（' + (w.skills || '无技能标注') + ' 评分' + (w.avgRating || 0) + '）'" :value="w.id" />
      </el-select>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确定派单</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="auditVisible" title="工单审核" width="520px">
      <p><b>{{ cur.title }}</b></p>
      <p>维修结果：{{ cur.finishRemark }}</p>
      <p>耗费工时：{{ cur.workHours }} 小时</p>
      <el-form-item label="审核意见">
        <el-input v-model="auditRemark" type="textarea" :rows="3" placeholder="可填写审核意见" />
      </el-form-item>
      <template #footer>
        <el-button type="danger" @click="doAudit(false)">不通过（返修）</el-button>
        <el-button type="primary" @click="doAudit(true)">审核通过</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" :title="cur.title" size="420px">
      <p><b>报修人：</b>{{ cur.studentName }}</p>
      <p><b>位置：</b>{{ cur.building }} {{ cur.roomNo }}</p>
      <p><b>描述：</b>{{ cur.description }}</p>
      <p v-if="cur.imageRecognize"><b>识别：</b>{{ cur.imageRecognize }}</p>
      <img v-if="cur.imagePath" :src="cur.imagePath" style="max-width: 100%; border-radius: 6px" />
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { statusOf, fmtDate } from '../../utils'

const list = ref([])
const workers = ref([])
const status = ref('')
const loading = ref(false)

const assignVisible = ref(false)
const assignWorkerId = ref(null)
const auditVisible = ref(false)
const detailVisible = ref(false)
const auditRemark = ref('')
const cur = ref({})

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/orders', status.value ? { status: status.value } : {})
    list.value = r.data
  } finally { loading.value = false }
}

async function openAssign(row) {
  cur.value = row
  assignWorkerId.value = null
  const w = await api.get('/api/admin/users', { role: 'WORKER' })
  workers.value = w.data
  assignVisible.value = true
}

async function doAssign() {
  if (!assignWorkerId.value) return ElMessage.warning('请选择维修工')
  await api.put('/api/orders/' + cur.value.id + '/assign', null, { workerId: assignWorkerId.value })
  ElMessage.success('派单成功')
  assignVisible.value = false
  load()
}

function openAudit(row) {
  cur.value = row
  auditRemark.value = ''
  auditVisible.value = true
}

async function doAudit(approved) {
  await api.put('/api/orders/' + cur.value.id + '/audit', { approved, remark: auditRemark.value })
  ElMessage.success(approved ? '审核已通过' : '已退回返修')
  auditVisible.value = false
  load()
}

function open(row) {
  cur.value = row
  detailVisible.value = true
}

onMounted(load)
</script>
