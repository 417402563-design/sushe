<template>
  <div class="page">
    <div class="page-title">工单接收池（待抢单）</div>
    <el-alert class="mb12" type="info" :closable="false" show-icon
              title="这里是尚未派单的报修工单，点击“接单”即可领取并开始处理。" />
    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" empty-text="暂无待抢工单">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="category" label="类别" width="90" />
        <el-table-column label="位置" width="140">
          <template #default="{ row }">{{ row.building }} {{ row.roomNo }}</template>
        </el-table-column>
        <el-table-column prop="studentName" label="报修人" width="100" />
        <el-table-column label="紧急" width="80">
          <template #default="{ row }">
            <el-tag :type="row.urgency === '紧急' ? 'danger' : 'info'" size="small">{{ row.urgency }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="150">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="open(row)">查看</el-button>
            <el-button size="small" type="primary" @click="grab(row)">立即接单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="cur.title" width="520px">
      <p><b>类别：</b>{{ cur.category }} ｜ <b>紧急：</b>{{ cur.urgency }}</p>
      <p><b>位置：</b>{{ cur.building }} {{ cur.roomNo }}</p>
      <p><b>报修人：</b>{{ cur.studentName }}</p>
      <p><b>描述：</b>{{ cur.description }}</p>
      <img v-if="cur.imagePath" :src="cur.imagePath" style="max-width: 100%; border-radius: 6px; margin-top: 8px" />
      <template #footer>
        <el-button @click="visible = false">关闭</el-button>
        <el-button type="primary" @click="grab(cur)">接单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { fmtDate } from '../../utils'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const cur = ref({})

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/orders/pool')
    list.value = r.data
  } finally { loading.value = false }
}

function open(row) {
  cur.value = row
  visible.value = true
}

async function grab(row) {
  await api.put('/api/orders/' + row.id + '/grab')
  ElMessage.success('接单成功，已加入我的工单')
  visible.value = false
  load()
}

onMounted(load)
</script>
