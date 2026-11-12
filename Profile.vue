<template>
  <div class="page">
    <div class="page-title">个人中心</div>
    <el-card shadow="never">
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="avatar-box">
            <el-avatar :size="90">{{ form.realName ? form.realName[0] : '?' }}</el-avatar>
            <h3>{{ form.realName }}</h3>
            <el-tag>{{ roleLabel }}</el-tag>
          </div>
          <el-descriptions :column="1" border class="mt12">
            <el-descriptions-item label="账号">{{ form.username }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ fmtDate(form.createTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="isWorker" label="完成工单">{{ form.finishedCount || 0 }}</el-descriptions-item>
            <el-descriptions-item v-if="isWorker" label="累计工时">{{ form.totalWorkHours || 0 }}</el-descriptions-item>
            <el-descriptions-item v-if="isWorker" label="平均评分">{{ form.avgRating || 0 }}</el-descriptions-item>
          </el-descriptions>
        </el-col>

        <el-col :span="16">
          <el-form :model="form" label-width="90px">
            <el-form-item label="姓名"><el-input v-model="form.realName" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
            <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender"><el-radio label="男">男</el-radio><el-radio label="女">女</el-radio></el-radio-group>
            </el-form-item>
            <el-form-item v-if="isWorker" label="技能方向">
              <el-input v-model="form.skills" placeholder="如：水电,家具,网络" />
            </el-form-item>
            <el-form-item v-if="isStudent" label="学院班级">
              <el-input v-model="form.college" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="form.password" type="password" placeholder="不修改请留空" show-password />
            </el-form-item>
            <el-button type="primary" @click="save">保存修改</el-button>
          </el-form>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import { store } from '../store'
import { fmtDate } from '../utils'

const raw = store.state.user
const form = reactive({ ...raw, password: '' })

const roleLabel = computed(() => ({ ADMIN: '管理员', WORKER: '维修工', STUDENT: '学生' }[raw.role]))
const isWorker = computed(() => raw.role === 'WORKER')
const isStudent = computed(() => raw.role === 'STUDENT')

async function save() {
  await api.put('/api/admin/users/' + raw.id, form)
  store.setUser({ ...form, password: undefined })
  ElMessage.success('个人信息已更新')
}
</script>

<style scoped>
.avatar-box { text-align: center; }
.avatar-box h3 { margin: 12px 0 6px; }
</style>
