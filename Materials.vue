<template>
  <div class="page">
    <div class="page-title">材料申请审批</div>
    <el-card shadow="never">
      <div class="mb12">
        <el-radio-group v-model="status" @change="load">
          <el-radio-button label="PENDING">待审批</el-radio-button>
          <el-radio-button label="">全部</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="list" v-loading="loading" empty-text="暂无申请">
        <el-table-column prop="workerName" label="申请人" width="110" />
        <el-table-column prop="partName" label="备件" min-width="140" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="purpose" label="用途" min-width="140" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="MATERIAL_STATUS[row.status].type" size="small">{{ MATERIAL_STATUS[row.status].label }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="150">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button size="small" type="success" @click="audit(row, true)">批准</el-button>
              <el-button size="small" type="danger" @click="audit(row, false)">驳回</el-button>
            </template>
            <span v-else style="color: #909399">已处理</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'
import { MATERIAL_STATUS, fmtDate } from '../../utils'

const list = ref([])
const status = ref('PENDING')
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/materials', status.value ? { status: status.value } : {})
    list.value = r.data
  } finally { loading.value = false }
}

async function audit(row, approved) {
  const { value } = await ElMessageBox.prompt('请输入审批意见', approved ? '批准' : '驳回', {
    inputValue: approved ? '同意领用' : '库存不足'
  })
  await api.put('/api/materials/' + row.id + '/audit', { approved, remark: value })
  ElMessage.success('已处理')
  load()
}

onMounted(load)
</script>
