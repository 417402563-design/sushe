<template>
  <div class="page">
    <div class="page-title">投诉与意见反馈</div>
    <el-row :gutter="16">
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>我要反馈</template>
          <el-form :model="form" label-width="80px">
            <el-form-item label="类型">
              <el-radio-group v-model="form.type">
                <el-radio-button label="OPINION">意见建议</el-radio-button>
                <el-radio-button label="COMPLAINT">投诉</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="关联工单">
              <el-select v-model="form.orderId" clearable placeholder="可选" style="width: 100%">
                <el-option v-for="o in orders" :key="o.id" :label="o.title" :value="o.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请详细描述您的意见或投诉内容" />
            </el-form-item>
            <el-button type="primary" style="width: 100%" @click="submit">提交</el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="never">
          <template #header>我的反馈记录</template>
          <el-empty v-if="!list.length" description="暂无反馈" />
          <el-timeline v-else>
            <el-timeline-item v-for="f in list" :key="f.id"
                              :type="f.type === 'COMPLAINT' ? 'danger' : 'primary'"
                              :timestamp="fmtDate(f.createTime)" placement="top">
              <el-card>
                <div style="margin-bottom: 6px">
                  <el-tag size="small" :type="f.type === 'COMPLAINT' ? 'danger' : 'info'">
                    {{ f.type === 'COMPLAINT' ? '投诉' : '意见' }}
                  </el-tag>
                  <el-tag size="small" :type="f.status === 'REPLIED' ? 'success' : 'warning'" style="margin-left: 8px">
                    {{ f.status === 'REPLIED' ? '已回复' : '待处理' }}
                  </el-tag>
                </div>
                <p>{{ f.content }}</p>
                <el-alert v-if="f.reply" type="success" :closable="false" show-icon
                          :title="'管理员回复：' + f.reply" />
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { fmtDate } from '../../utils'

const form = reactive({ type: 'OPINION', content: '', orderId: null })
const list = ref([])
const orders = ref([])

async function load() {
  const [f, o] = await Promise.all([api.get('/api/feedback/my'), api.get('/api/orders/my')])
  list.value = f.data
  orders.value = o.data
}

async function submit() {
  if (!form.content) return ElMessage.warning('请填写内容')
  await api.post('/api/feedback', form)
  ElMessage.success('提交成功，感谢反馈')
  form.content = ''
  form.orderId = null
  load()
}

onMounted(load)
</script>
