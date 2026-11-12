<template>
  <div class="page">
    <div class="page-title">维修工 / 学生管理</div>
    <el-card shadow="never">
      <div class="mb12" style="display: flex; justify-content: space-between">
        <el-radio-group v-model="role" @change="load">
          <el-radio-button label="WORKER">维修工</el-radio-button>
          <el-radio-button label="STUDENT">学生</el-radio-button>
          <el-radio-button label="">全部</el-radio-button>
        </el-radio-group>
        <el-button type="primary" :icon="Plus" @click="openAdd">新增{{ role === 'STUDENT' ? '学生' : '维修工' }}</el-button>
      </div>

      <el-table :data="list" v-loading="loading">
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.role === 'ADMIN' ? 'danger' : row.role === 'WORKER' ? 'warning' : 'success'">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="skills" label="技能" min-width="120" />
        <el-table-column prop="college" label="学院班级" min-width="120" />
        <el-table-column label="评分" width="80">
          <template #default="{ row }">{{ row.avgRating || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="toggle(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-button size="small" type="danger" @click="del(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="form.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="账号"><el-input v-model="form.username" :disabled="!!form.id" /></el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" :placeholder="form.id ? '留空则不修改' : ''" />
        </el-form-item>
        <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option label="维修工" value="WORKER" />
            <el-option label="学生" value="STUDENT" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender"><el-radio label="男">男</el-radio><el-radio label="女">女</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.role === 'WORKER'" label="技能"><el-input v-model="form.skills" placeholder="如：水电,家具" /></el-form-item>
        <el-form-item v-if="form.role === 'STUDENT'" label="学院班级"><el-input v-model="form.college" /></el-form-item>
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
const role = ref('WORKER')
const loading = ref(false)
const visible = ref(false)
const empty = { id: null, username: '', password: '', realName: '', role: 'WORKER', phone: '', gender: '男', skills: '', college: '' }
const form = reactive({ ...empty })

function roleLabel(r) { return { ADMIN: '管理员', WORKER: '维修工', STUDENT: '学生' }[r] || r }

async function load() {
  loading.value = true
  try {
    const r = await api.get('/api/admin/users', role.value ? { role: role.value } : {})
    list.value = r.data
  } finally { loading.value = false }
}

function openAdd() {
  Object.assign(form, empty)
  form.role = role.value || 'WORKER'
  visible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  form.password = ''
  visible.value = true
}

async function save() {
  if (!form.username || !form.realName) return ElMessage.warning('请填写账号和姓名')
  if (form.id) await api.put('/api/admin/users/' + form.id, form)
  else await api.post('/api/admin/users', form)
  ElMessage.success('保存成功')
  visible.value = false
  load()
}

async function toggle(row) {
  await api.put('/api/admin/users/' + row.id + '/toggle')
  load()
}

async function del(row) {
  await ElMessageBox.confirm('确定删除用户 ' + row.realName + '？', '提示', { type: 'warning' })
  await api.del('/api/admin/users/' + row.id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
