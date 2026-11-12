<template>
  <div class="page">
    <div class="page-title">我的工时排班</div>
    <el-card shadow="never" class="mb12">
      <div style="display: flex; gap: 10px; align-items: flex-end; flex-wrap: wrap">
        <div>
          <div class="lbl">日期</div>
          <el-date-picker v-model="form.workDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </div>
        <div>
          <div class="lbl">班次</div>
          <el-select v-model="form.shift" style="width: 120px">
            <el-option v-for="s in shifts" :key="s" :label="s" :value="s" />
          </el-select>
        </div>
        <div>
          <div class="lbl">工时</div>
          <el-input-number v-model="form.hours" :min="0" :max="16" :step="0.5" />
        </div>
        <el-button type="primary" @click="add">新增排班</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" empty-text="暂无排班">
        <el-table-column prop="workDate" label="日期" width="140" />
        <el-table-column prop="shift" label="班次" width="120" />
        <el-table-column prop="hours" label="工时" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="setStatus(row, '值班中')" v-if="row.status === '待值班'">开始值班</el-button>
            <el-button size="small" type="success" @click="setStatus(row, '已完成')" v-if="row.status !== '已完成'">结束值班</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="mt12" style="color: #909399">
        本周累计工时：<b style="color: #409eff">{{ total }} 小时</b>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const shifts = ['早班', '中班', '晚班', '休息']
const list = ref([])
const form = reactive({ workDate: '', shift: '早班', hours: 8 })

const total = computed(() => list.value.reduce((s, x) => s + (x.hours || 0), 0))

async function load() {
  const r = await api.get('/api/schedules/my')
  list.value = r.data
}

async function add() {
  if (!form.workDate) return ElMessage.warning('请选择日期')
  await api.post('/api/schedules', { ...form, status: '待值班' })
  ElMessage.success('排班成功')
  load()
}

async function setStatus(row, status) {
  await api.put('/api/schedules/' + row.id, { ...row, status })
  load()
}

onMounted(load)
</script>

<style scoped>
.lbl { font-size: 13px; color: #606266; margin-bottom: 4px; }
</style>
