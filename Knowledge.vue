<template>
  <div class="page">
    <div class="page-title">智能知识库</div>

    <el-card shadow="never" class="mb12">
      <div style="display: flex; gap: 10px">
        <el-input v-model="kw" placeholder="输入你的故障问题，如：宿舍没有网络 / 灯不亮" @keyup.enter="doSearch">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="doSearch">智能查询</el-button>
        <el-button @click="loadAll">全部</el-button>
        <el-button v-if="isAdmin" type="success" :icon="Plus" @click="openAdd">新增文章</el-button>
      </div>
      <div class="tags mt12">
        <el-tag v-for="c in cats" :key="c" class="mb12" style="margin-right: 8px; cursor: pointer"
                @click="byCategory(c)">{{ c }}</el-tag>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col v-for="a in list" :key="a.id" :span="8" style="margin-bottom: 16px">
        <el-card shadow="hover" class="kcard" @click="openDetail(a)">
          <div style="display: flex; justify-content: space-between">
            <el-tag size="small">{{ a.category }}</el-tag>
            <span style="color: #909399; font-size: 12px">{{ a.views }} 次浏览</span>
          </div>
          <h4 style="margin: 10px 0">{{ a.title }}</h4>
          <p class="ellipsis">{{ a.content }}</p>
          <div v-if="isAdmin" style="margin-top: 10px; text-align: right">
            <el-button size="small" @click.stop="openEdit(a)">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="del(a)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="detailVisible" :title="cur.title" width="640px">
      <p style="white-space: pre-wrap">{{ cur.content }}</p>
      <p style="color: #909399">关键词：{{ cur.keywords }}</p>
    </el-dialog>

    <el-dialog v-model="editVisible" :title="form.id ? '编辑文章' : '新增文章'" width="640px">
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" allow-create filterable style="width: 100%">
            <el-option v-for="c in cats" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词"><el-input v-model="form.keywords" placeholder="空格分隔" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../api'
import { store } from '../store'

const route = useRoute()
const isAdmin = computed(() => store.role() === 'ADMIN')
const kw = ref(route.query.kw || '')
const list = ref([])
const cats = ['网络', '水电', '电料', '家具', '门窗', '家电', '五金']
const detailVisible = ref(false)
const editVisible = ref(false)
const cur = ref({})
const form = reactive({ id: null, title: '', category: '', keywords: '', content: '' })

async function loadAll() {
  const r = await api.get('/api/knowledge')
  list.value = r.data
}

async function doSearch() {
  if (!kw.value) return loadAll()
  const r = await api.get('/api/knowledge/search', { kw: kw.value })
  list.value = r.data
}

async function byCategory(c) {
  const r = await api.get('/api/knowledge/category/' + c)
  list.value = r.data
}

function openDetail(a) {
  cur.value = a
  detailVisible.value = true
  api.get('/api/knowledge/' + a.id).then(r => { a.views = r.data.views })
}

function openAdd() {
  Object.assign(form, { id: null, title: '', category: '', keywords: '', content: '' })
  editVisible.value = true
}

function openEdit(a) {
  Object.assign(form, a)
  editVisible.value = true
}

async function save() {
  if (form.id) await api.put('/api/knowledge/' + form.id, form)
  else await api.post('/api/knowledge', form)
  ElMessage.success('保存成功')
  editVisible.value = false
  loadAll()
}

async function del(a) {
  await ElMessageBox.confirm('确定删除该文章？', '提示', { type: 'warning' })
  await api.del('/api/knowledge/' + a.id)
  ElMessage.success('已删除')
  loadAll()
}

onMounted(kw.value ? doSearch : loadAll)
</script>

<style scoped>
.kcard { cursor: pointer; height: 100%; }
.ellipsis {
  color: #606266;
  font-size: 13px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
