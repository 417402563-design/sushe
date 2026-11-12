import { reactive } from 'vue'

const state = reactive({
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user') || 'null')
})

export const store = {
  state,
  isLogin() {
    return !!state.token
  },
  role() {
    return state.user ? state.user.role : ''
  },
  login(token, user) {
    state.token = token
    state.user = user
    localStorage.setItem('token', token)
    localStorage.setItem('user', JSON.stringify(user))
  },
  logout() {
    state.token = ''
    state.user = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },
  setUser(user) {
    state.user = user
    localStorage.setItem('user', JSON.stringify(user))
  }
}
