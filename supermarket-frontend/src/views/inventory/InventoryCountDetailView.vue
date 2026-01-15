<template>
  <div class="count-detail-container">
    <el-page-header @back="goBack" title="盘点详情" class="page-header">
      <template #content>
        <span class="text-large font-600 mr-3"> {{ countInfo.title || '任务详情' }} </span>
        <el-tag :type="getStatusType(countInfo.status)" style="margin-left: 10px">{{ getStatusText(countInfo.status) }}</el-tag>
      </template>
    </el-page-header>

    <el-card class="operation-card" v-if="countInfo.status === 'IN_PROGRESS'">
      <el-form :inline="true" class="demo-form-inline">
        <el-form-item>
          <el-button type="primary" icon="Plus" @click="handleAdd">录入盘点数据</el-button>
        </el-form-item>
        <el-form-item>
           <el-text type="info">请扫描商品条码或搜索商品进行录入</el-text>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="detailList" border stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="productBarcode" label="条码" width="140" />
        <el-table-column prop="productName" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="productSpec" label="规格" width="100" />
        <el-table-column prop="systemStock" label="系统库存" width="100" align="center" />
        <el-table-column prop="actualStock" label="实盘库存" width="100" align="center" />
        <el-table-column label="差异" width="100" align="center">
          <template #default="scope">
            <span :class="getDiffClass(scope.row.difference)">
              {{ scope.row.difference > 0 ? '+' : '' }}{{ scope.row.difference }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="discrepancyReason" label="差异原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>

    <!-- 录入对话框 -->
    <el-dialog
      title="录入盘点数据"
      v-model="dialog.visible"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="选择商品" prop="productId">
          <el-select
            v-model="form.productId"
            filterable
            remote
            reserve-keyword
            placeholder="输入商品名称或条码搜索"
            :remote-method="searchProduct"
            :loading="searchLoading"
            style="width: 100%"
            @change="handleProductSelect"
          >
            <el-option
              v-for="item in productOptions"
              :key="item.id"
              :label="`${item.name} (${item.barcode})`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <div v-if="selectedProduct" class="product-info-box">
          <p>当前系统库存: <strong>{{ selectedProduct.stock }}</strong> {{ selectedProduct.unit }}</p>
          <p>规格: {{ selectedProduct.spec }}</p>
        </div>

        <el-form-item label="实盘数量" prop="actualStock">
          <el-input-number v-model="form.actualStock" :min="0" style="width: 100%" />
        </el-form-item>

        <el-form-item label="差异原因">
          <el-input v-model="form.discrepancyReason" placeholder="如有差异请填写原因" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getInventoryCountDetails, addInventoryCountDetail } from '@/api/inventory'
import { getProductPage } from '@/api/product'

const route = useRoute()
const router = useRouter()
const countId = route.params.id

// 从 Query 参数获取 basic info，或者后续如果有 API 获取详情也可以
const countInfo = reactive({
  title: route.query.title || '',
  status: route.query.status || 'UNKNOWN'
})

const loading = ref(false)
const detailList = ref([])

// 新增相关
const dialog = reactive({
  visible: false
})
const searchLoading = ref(false)
const productOptions = ref([])
const selectedProduct = ref(null)

const form = reactive({
  productId: null,
  productName: '',
  productBarcode: '',
  productSpec: '',
  productUnit: '',
  systemStock: 0,
  actualStock: 0,
  discrepancyReason: '',
  remark: ''
})

const formRef = ref(null)

const rules = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  actualStock: [{ required: true, message: '请输入实盘数量', trigger: 'blur' }]
}

onMounted(() => {
  if (countId) {
    loadDetails()
  }
})

const loadDetails = async () => {
  loading.value = true
  try {
    const res = await getInventoryCountDetails(countId)
    detailList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const getStatusType = (status) => {
  const map = {
    'DRAFT': 'info',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'DRAFT': '未开始',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成'
  }
  return map[status] || status
}

const getDiffClass = (diff) => {
  if (diff > 0) return 'text-success'
  if (diff < 0) return 'text-danger'
  return ''
}

const handleAdd = () => {
  dialog.visible = true
  // 预加载一些商品或者清空
  productOptions.value = []
  resetFormState()
}

const searchProduct = async (query) => {
  if (query) {
    searchLoading.value = true
    try {
      // 复用商品分页接口进行搜索
      const res = await getProductPage({ pageNum: 1, pageSize: 20, name: query }) // 简单按名字搜，实际后端可能支持 barcode
      productOptions.value = res.data.records
    } catch (error) {
      console.error(error)
    } finally {
      searchLoading.value = false
    }
  } else {
    productOptions.value = []
  }
}

const handleProductSelect = (val) => {
  const product = productOptions.value.find(item => item.id === val)
  if (product) {
    selectedProduct.value = product
    form.productId = product.id
    form.productName = product.name
    form.productBarcode = product.barcode
    form.productSpec = product.spec
    form.productUnit = product.unit
    form.systemStock = product.stock
    form.actualStock = product.stock // 默认实盘等于系统库存，方便修改
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await addInventoryCountDetail(countId, form)
        ElMessage.success('录入成功')
        dialog.visible = false
        loadDetails()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const resetFormState = () => {
  form.productId = null
  form.productName = ''
  form.productBarcode = ''
  form.productSpec = ''
  form.productUnit = ''
  form.systemStock = 0
  form.actualStock = 0
  form.discrepancyReason = ''
  form.remark = ''
  selectedProduct.value = null
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}
.operation-card {
  margin-bottom: 20px;
}
.table-card {
  /* margin-bottom: 20px; */
}
.text-success {
  color: #67c23a;
  font-weight: bold;
}
.text-danger {
  color: #f56c6c;
  font-weight: bold;
}
.product-info-box {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 18px;
  font-size: 13px;
  color: #606266;
}
.product-info-box p {
  margin: 4px 0;
}
</style>

