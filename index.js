import axios from 'axios'
import { ElMessage } from 'element-plus'
import { store } from '../store'

const http = axios.create({ baseURL: '/', timeout: 15000 })

http.interceptors.request.use(cfg => {
  if (store.state.token) cfg.headers['X-Token'] = store.state.token
  return cfg
})

http.interceptors.response.use(
  res => {
    const data = res.data
    if (data && data.code && data.code !== 200) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(data)
    }
    return data
  },
  err => {
    if (err.response && err.response.status === 401) {
      ElMessage.warning('登录已过期，请重新登录')
      store.logout()
      window.location.href = '/#/login'
    } else {
      ElMessage.error('网络错误：' + (err.message || ''))
    }
    return Promise.reject(err)
  }
)

// 通用 CRUD 封装
export const api = {
  // 认证
  login: (d) => http.post('/api/auth/login', d),
  register: (d) => http.post('/api/auth/register', d),
  logout: () => http.post('/api/auth/logout'),

  get: (url, params) => http.get(url, { params }),
  post: (url, data) => http.post(url, data),
  put: (url, data, params) => http.put(url, data, { params }),
  del: (url) => http.delete(url)
}

export default http
