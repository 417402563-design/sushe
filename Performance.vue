<template>
  <div class="page">
    <div class="page-title">绩效数据与服务评价</div>

    <div class="card-row mb12">
      <el-card class="stat-card" shadow="hover">
        <el-statistic title="累计完成工单" :value="user.finishedCount || 0" />
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <el-statistic title="累计工时(小时)" :value="user.totalWorkHours || 0" />
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div style="color: #909399; font-size: 14px">平均评分</div>
        <div style="font-size: 28px; font-weight: 600; color: #e6a23c">{{ user.avgRating || 0 }}</div>
        <el-rate :model-value="user.avgRating || 0" disabled allow-half />
      </el-card>
    </div>

    <el-card shadow="never" class="mb12">
      <template #header>技能信息维护</template>
      <div style="display: flex; gap: 10px; align-items: center">
        <el-input v-model="skills" placeholder="如：水电,家具,网络" style="width: 320px" />
        <el-button type="primary" @click="saveSkills">保存技能</el-button>
      </div>
    </el-card>

    <el-card shadow="never">
      <template #header>收到的服务评价</template>
      <el-empty v-if="!evals.length" description="暂无评价" />
      <el-table v-else :data="evals">
        <el-table-column prop="studentName" label="评价人" width="120" />
        <el-table-column label="综合" width="160">
          <template #default="{ row }"><el-rate v-model="row.score" disabled /></template>
        </el-table-column>
        <el-table-column prop="speedScore" label="速度" width="70" />
        <el-table-column prop="qualityScore" label="质量" width="70" />
        <el-table-column prop="attitudeScore" label="态度" width="70" />
        <el-table-column prop="comment" label="评价内容" min-width="200" />
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ fmtDate(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { store } from '../../store'
import { fmtDate } from '../../utils'

const user = store.state.user
const evals = ref([])
const skills = ref(user.skills || '')

async function load() {
  const r = await api.get('/api/evaluations/worker/' + user.id)
  evals.value = r.data
}

async function saveSkills() {
  await api.put('/api/admin/users/' + user.id + '/skills', null, { skills: skills.value })
  store.setUser({ ...user, skills: skills.value })
  ElMessage.success('技能已更新')
}

onMounted(load)
</script>
