<template>
  <div class="page">
    <div class="page-title">宿舍信息管理</div>
    <el-card shadow="never">
      <div class="mb12"><el-button type="primary" :icon="Plus" @click="openAdd">新增宿舍</el-button></div>
      <el-table :data="list" v-loading="loading">
        <el-table-column prop="building" label="楼栋" width="120" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="120" />
        <el-table-column prop="dormType" label="类型" width="120" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="genderLimit" label="性别" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="{ '空闲': 'success', '住满': 'info', '维修中': 'warning' }[row.status]">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑宿舍' : '新增宿舍'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="楼栋"><el-input v-model="form.building" /></el-form-item>
        <el-form-item label="楼层"><el-input-number v-model="form.floor" :min="1" /></el-form-item>
        <el-form-item label="房间号"><el-input v-model="form.roomNo" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.dormType" style="width: 100%">
            <el-option label="四人间" value="四人间" /><el-option label="六人间" value="六人间" /><el-option label="八人间" value="八人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量"><el-input-number v-model="form.capacity" :min="1" /></el-form-item>
        <el-form-item label="住宿性别">
          <el-radio-group v-model="form.genderLimit"><el-radio label="男">男</el-radio><el-radio label="女">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="空闲" value="空闲" /><el-option label="住满" value="住满" /><el-option label="维修中" value="维修中" />
          </el-select>
        </el-form-item>
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

const list = ref([])
const loading = ref(false)
const visible = ref(false)
const empty = { id: null, building: '', floor: 1, roomNo: '', dormType: '四人间', capacity: 4, genderLimit: '男', status: '空闲' }
const form = reactive({ ...empty })

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/rooms')
    list.value = r.data
  } finally { loading.value = false }
}

function openAdd() { Object.assign(form, empty); visible.value = true }
function openEdit(row) { Object.assign(form, row); visible.value = true }

async function save() {
  if (!form.building || !form.roomNo) return ElMessage.warning('请填写楼栋和房间号')
  if (form.id) await api.put('/api/rooms/' + form.id, form)
  else await api.post('/api/rooms', form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除该宿舍？', '提示', { type: 'warning' })
  await api.del('/api/rooms/' + row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
