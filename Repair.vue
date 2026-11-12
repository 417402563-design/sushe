<template>
  <div class="page">
    <div class="page-title">一键报修</div>
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never">
          <el-form :model="form" label-width="90px">
            <el-form-item label="报修类别">
              <el-select v-model="form.category" style="width: 100%" placeholder="请选择">
                <el-option v-for="c in cats" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
            <el-form-item label="故障标题">
              <el-input v-model="form.title" placeholder="一句话描述，如：卫生间漏水" />
            </el-form-item>
            <el-form-item label="宿舍">
              <el-select v-model="form.roomId" style="width: 100%" placeholder="选择宿舍（可先“全部”浏览）" clearable
                         @change="onRoom">
                <el-option v-for="r in rooms" :key="r.id" :label="r.building + ' ' + r.roomNo + '（' + r.dormType + '）'" :value="r.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="详细描述">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="补充故障现象、出现时间等" />
            </el-form-item>
            <el-form-item label="紧急程度">
              <el-radio-group v-model="form.urgency">
                <el-radio label="普通">普通</el-radio>
                <el-radio label="紧急">紧急</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="现场图片">
              <el-upload
                class="uploader"
                action="/api/upload/image"
                name="file"
                :headers="headers"
                :show-file-list="false"
                :on-success="onUpload"
                :before-upload="beforeUpload">
                <img v-if="form.imagePath" :src="form.imagePath" class="preview" />
                <el-icon v-else class="plus"><Plus /></el-icon>
              </el-upload>
              <div v-if="form.imageRecognize" class="recognize">
                <el-icon><MagicStick /></el-icon> {{ form.imageRecognize }}
              </div>
            </el-form-item>

            <el-form-item label="自助尝试">
              <el-button link type="primary" @click="selfHelpVisible = true">遇到问题先自查？点此查询解决方案</el-button>
            </el-form-item>

            <el-button type="primary" style="width: 100%" @click="submit" :loading="loading">提交报修</el-button>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never">
          <template #header><el-icon><MagicStick /></el-icon> 紧急报修 · 智能识别</template>
          <p style="color: #606266; line-height: 1.8">
            上传现场照片后，系统会依据图像特征自动预判<b>故障类别</b>与<b>紧急程度</b>，
            帮助你更快报修。识别结果仅供参考，可手动修正。
          </p>
          <el-alert type="info" :closable="false" show-icon
                    title="漏水、断电等存在安全隐患的问题，请选择“紧急”，系统将优先派单。" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="selfHelpVisible" title="自助解决 · 知识库" width="640px">
      <el-input v-model="skw" placeholder="搜索故障关键词" @keyup.enter="doSearch">
        <template #append><el-button :icon="Search" @click="doSearch" /></template>
      </el-input>
      <div class="mt12">
        <el-empty v-if="!solved.length && searched" description="没有自助方案，建议直接报修" />
        <el-collapse v-else>
          <el-collapse-item v-for="a in solved" :key="a.id" :title="a.title" :name="a.id">
            <div style="white-space: pre-wrap">{{ a.content }}</div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, MagicStick, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { api } from '../api'
import { store } from '../store'
import { CATEGORY_LIST } from '../utils'

const router = useRouter()
const cats = CATEGORY_LIST
const rooms = ref([])
const loading = ref(false)
const headers = computed(() => ({ 'X-Token': store.state.token }))

const form = reactive({
  category: '', title: '', roomId: null, building: '', roomNo: '',
  description: '', urgency: '普通', imagePath: '', imageRecognize: ''
})

// 自助查询
const selfHelpVisible = ref(false)
const skw = ref('')
const solved = ref([])
const searched = ref(false)

function onRoom(id) {
  const r = rooms.value.find(x => x.id === id)
  if (r) { form.building = r.building; form.roomNo = r.roomNo }
}

function beforeUpload(file) {
  const isImg = file.type.startsWith('image/')
  if (!isImg) ElMessage.error('只能上传图片文件')
  const lt20 = file.size / 1024 / 1024 < 10
  if (!lt20) ElMessage.error('图片大小不能超过 10MB')
  return isImg && lt20
}

function onUpload(res) {
  if (res.code === 200) {
    form.imagePath = res.data.url
    form.imageRecognize = res.data.recognize
    // 依据识别结果自动预填类别与紧急程度
    const m = res.data.recognize && res.data.recognize.match(/故障类别：(.+?)，建议紧急程度：(.+?)（/)
    if (m) {
      if (cats.includes(m[1])) form.category = m[1]
      form.urgency = m[2]
    }
    ElMessage.success('图片上传并完成智能识别')
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

async function doSearch() {
  searched.value = true
  if (!skw.value) { solved.value = []; return }
  const r = await api.get('/api/knowledge/search', { kw: skw.value })
  solved.value = r.data
}

async function submit() {
  if (!form.category || !form.title) return ElMessage.warning('请填写类别与标题')
  loading.value = true
  try {
    await api.post('/api/orders', form)
    ElMessage.success('报修提交成功，可在“进度查询”中跟踪')
    router.push('/student/orders')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const r = await api.get('/api/rooms')
  rooms.value = r.data
})
</script>

<style scoped>
.uploader :deep(.el-upload) { border: 1px dashed #d9d9d9; border-radius: 6px; cursor: pointer; }
.uploader .plus { font-size: 28px; color: #8c939d; width: 148px; height: 148px; text-align: center; line-height: 148px; }
.preview { width: 148px; height: 148px; display: block; object-fit: cover; }
.recognize { margin-top: 10px; color: #e6a23c; background: #fdf6ec; padding: 8px; border-radius: 6px; font-size: 13px; }
</style>
