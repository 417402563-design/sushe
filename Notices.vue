<template>
  <div class="page">
    <div class="page-title">通知公告管理</div>
    <el-card shadow="never">
      <div class="mb12"><el-button type="primary" :icon="Plus" @click="openAdd">发布公告</el-button></div>
      <el-table :data="list" v-loading="loading">
        <el-table-column label="标题" min-width="220">
          <template #default="{ row }">
            <el-tag v-if="row.top" type="danger" size="small" effect="dark" style="margin-right: 6px">置顶</el-tag>
            {{ row.title }}
          </template>
        </el-table-column>
        <el-table-column prop="author" label="发布人" width="120" />
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">{{ fmtDate(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑公告' : '发布公告'" width="600px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.top" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'
import { fmtDate } from '../../utils'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const empty = { id: null, title: '', content: '', top: false }
const form = reactive({ ...empty })

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/notices')
    list.value = r.data
  } finally { loading.value = false }
}

function openAdd() { Object.assign(form, empty); visible.value = true }
function openEdit(row) { Object.assign(form, row); visible.value = true }

async function save() {
  if (!form.title || !form.content) return ElMessage.warning('请填写标题和内容')
  if (form.id) await api.put('/api/notices/' + form.id, form)
  else await api.post('/api/notices', form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该公告？', '提示', { type: 'warning' })
  await api.del('/api/notices/' + row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
