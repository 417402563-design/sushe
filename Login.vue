<template>
  <div class="login-bg">
    <el-card class="login-card">
      <h2 style="text-align: center; margin-bottom: 4px">智慧校园宿舍维修管理系统</h2>
      <p style="text-align: center; color: #909399; margin-bottom: 24px">用户登录</p>
      <el-form :model="form" @keyup.enter="submit" label-width="0">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit" :loading="loading">登录</el-button>
        <div style="margin-top: 14px; text-align: right">
          <span class="link" @click="$router.push('/register')">还没有账号？去注册</span>
        </div>
      </el-form>
      <el-divider>演示账号（一键填充）</el-divider>
      <div style="display: flex; gap: 8px; justify-content: center">
        <el-button size="small" @click="fill('admin')">管理员</el-button>
        <el-button size="small" @click="fill('worker1')">维修工</el-button>
        <el-button size="small" @click="fill('stu1')">学生</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { api } from '../api'
import { store } from '../store'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

function fill(name) {
  form.username = name
  form.password = '123456'
}

async function submit() {
  if (!form.username || !form.password) return ElMessage.warning('请输入用户名和密码')
  loading.value = true
  try {
    const r = await api.login(form)
    store.login(r.data.token, r.data.user)
    ElMessage.success('登录成功')
    router.push('/home')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4b6cb7, #182848);
}
.login-card {
  width: 420px;
  padding: 10px 20px 20px;
  border-radius: 12px;
}
</style>
