<template>
  <div class="page">
    <div class="page-title">数据统计总览</div>

    <div class="card-row mb12">
      <el-card class="stat-card" shadow="hover"><el-statistic title="工单总量" :value="data.orderTotal || 0" /></el-card>
      <el-card class="stat-card" shadow="hover"><el-statistic title="维修工人数" :value="data.workerCount || 0" /></el-card>
      <el-card class="stat-card" shadow="hover"><el-statistic title="学生人数" :value="data.studentCount || 0" /></el-card>
      <el-card class="stat-card" shadow="hover">
        <el-statistic title="库存预警备件" :value="data.partLowStock || 0" />
      </el-card>
    </div>

    <el-row :gutter="16" class="mb12">
      <el-col :span="12">
        <el-card shadow="never"><template #header>工单状态分布</template><div ref="statusChart" style="height: 300px"></div></el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never"><template #header>报修类别分布</template><div ref="catChart" style="height: 300px"></div></el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>维修工绩效排行</template>
          <el-table :data="data.workerRanking || []">
            <el-table-column type="index" label="排名" width="70" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="finished" label="完成工单" />
            <el-table-column prop="workHours" label="累计工时" />
            <el-table-column label="评分">
              <template #default="{ row }">{{ row.avgRating || 0 }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>系统维护提示</template>
          <el-alert type="warning" :closable="false" show-icon class="mb12"
                    :title="'当前有 ' + (data.partLowStock || 0) + ' 项备件低于安全库存，请及时补货'" />
          <el-alert type="info" :closable="false" show-icon class="mb12"
                    :title="'待派单工单 ' + (data.statusDist && data.statusDist.PENDING || 0) + ' 条，待审核 ' + (data.statusDist && data.statusDist.DONE || 0) + ' 条'" />
          <el-button type="primary" @click="$router.push('/admin/orders')">前往处理工单</el-button>
          <el-button @click="load">刷新数据</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { api } from '../../api'

const data = ref({})
const statusChart = ref()
const catChart = ref()

async function load() {
  const r = await api.get('/api/stats/dashboard')
  data.value = r.data
  await nextTick()
  renderCharts()
}

function renderCharts() {
  const sc = echarts.init(statusChart.value)
  sc.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '65%'],
      data: Object.entries(data.value.statusDist || {}).map(([k, v]) => ({ name: k, value: v }))
    }]
  })
  const cc = echarts.init(catChart.value)
  const entries = Object.entries(data.value.categoryDist || {})
  cc.setOption({
    tooltip: {},
    xAxis: { type: 'category', data: entries.map(e => e[0]) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: entries.map(e => e[1]), itemStyle: { color: '#409eff' } }]
  })
}

onMounted(load)
</script>

<style scoped>
.card-row { margin-bottom: 16px; }
</style>
