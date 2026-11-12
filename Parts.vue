<template>
  <div class="page">
    <div class="page-title">备件库存管理</div>
    <el-card shadow="never">
      <div class="mb12" style="display: flex; justify-content: space-between">
        <el-button type="primary" :icon="Plus" @click="openAdd">新增备件</el-button>
        <el-button type="warning" plain @click="showLow = !showLow">
          {{ showLow ? '显示全部' : '仅看预警' }}
        </el-button>
      </div>
      <el-table :data="shown" v-loading="loading">
        <el-table-column prop="partName" label="备件名称" min-width="140" />
        <el-table-column prop="category" label="类别" width="100" />
        <el-table-column label="库存" width="120">
          <template #default="{ row }">
            <span :style="{ color: isLow(row) ? '#f56c6c' : '', fontWeight: isLow(row) ? 700 : 400 }">
              {{ row.stock }} {{ row.unit }}
            </span>
            <el-tag v-if="isLow(row)" type="danger" size="small" style="margin-left: 6px">低</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="price" label="单价" width="90" />
        <el-table-column prop="location" label="存放位置" min-width="120" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑备件' : '新增备件'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.partName" /></el-form-item>
        <el-form-item label="类别"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="单位"><el-input v-model="form.unit" style="width: 100px" /></el-form-item>
        <el-form-item label="安全库存"><el-input-number v-model="form.safetyStock" :min="0" /></el-form-item>
        <el-form-item label="单价"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="存放位置"><el-input v-model="form.location" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const showLow = ref(false)
const empty = { id: null, partName: '', category: '', stock: 0, unit: '个', safetyStock: 10, price: 0, location: '' }
const form = reactive({ ...empty })

function isLow(row) { return row.stock != null && row.safetyStock != null && row.stock <= row.safetyStock }
const shown = computed(() => showLow.value ? list.value.filter(isLow) : list.value)

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/parts')
    list.value = r.data
  } finally { loading.value = false }
}

function openAdd() { Object.assign(form, empty); visible.value = true }
function openEdit(row) { Object.assign(form, row); visible.value = true }

async function save() {
  if (!form.partName) return ElMessage.warning('请填写名称')
  if (form.id) await api.put('/api/parts/' + form.id, form)
  else await api.post('/api/parts', form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该备件？', '提示', { type: 'warning' })
  await api.del('/api/parts/' + row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
