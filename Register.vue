<template>
  <div class="login-bg">
    <el-card class="reg-card">
      <h2 style="text-align: center; margin-bottom: 20px">用户注册</h2>
      <el-form :model="form" label-width="80px">
        <el-form-item label="注册身份">
          <el-radio-group v-model="form.role">
            <el-radio-button label="STUDENT">学生</el-radio-button>
            <el-radio-button label="WORKER">维修工</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户名"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.role === 'WORKER'" label="技能方向">
          <el-input v-model="form.skills" placeholder="如：水电,家具,网络" />
        </el-form-item>
        <el-form-item v-if="form.role === 'STUDENT'" label="学院班级">
          <el-input v-model="form.college" placeholder="如：计算机学院 2022-1" />
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="submit" :loading="loading">立即注册</el-button>
        <div style="margin-top: 14px; text-align: right">
          <span class="link" @click="$router.push('/login')">已有账号？去登录</span>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../api'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  role: 'STUDENT', username: '', password: '', realName: '',
  phone: '', gender: '男', skills: '', college: ''
})

async function submit() {
  if (!form.username || !form.password || !form.realName) return ElMessage.warning('请填写完整信息')
  loading.value = true
  try {
    await api.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
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
.reg-card { width: 520px; padding: 10px 20px 20px; border-radius: 12px; }
</style>
