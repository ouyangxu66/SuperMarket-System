<template>
  <div class="purchase-plan-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>📋 采购计划</span>
          <el-button type="primary" @click="handleCreatePlan">
            <el-icon><Plus /></el-icon>
            新建采购计划
          </el-button>
        </div>
      </template>

      <el-table :data="plans" stripe border v-loading="loading">
        <el-table-column prop="billNo" label="计划编号" width="180" />
        <el-table-column prop="supplierName" label="供应商" min-width="150" />
        <el-table-column label="总金额" width="120" align="right">
          <template #default="{ row }">
            ¥{{ formatMoney(row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button
                v-if="row.status === 0"
                size="small"
                type="success"
                @click="handleAudit(row)"
            >
              审核入库
            </el-button>
            <el-button
                v-if="row.status === 0"
                size="small"
                type="danger"
                @click="handleVoid(row)"
            >
              作废
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchPlans"
          @current-change="fetchPlans"
          style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新建采购计划对话框 -->
    <el-dialog
        v-model="dialogVisible"
        title="新建采购计划"
        width="900px"
        :close-on-click-modal="false"
    >
      <el-form :model="planForm" label-width="100px">
        <el-form-item label="供应商">
          <el-select
              v-model="planForm.supplierId"
              placeholder="请选择供应商"
              style="width: 100%"
              filterable
          >
            <el-option
                v-for="supplier in supplierList"
                :key="supplier.id"
                :label="supplier.name"
                :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
              v-model="planForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
          />
        </el-form-item>

        <el-divider>采购明细</el-divider>

        <el-table :data="planForm.items" border>
          <el-table-column label="商品" min-width="200">
            <template #default="{ row, $index }">
              <el-select
                  v-model="row.productId"
                  placeholder="选择商品"
                  filterable
                  @change="handleProductChange($index)"
              >
                <el-option
                    v-for="item in productList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="规格" width="100">
            <template #default="{ row }">
              {{ row.spec || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="当前库存" width="100" align="center">
            <template #default="{ row }">
              {{ row.currentStock || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="采购数量" width="120">
            <template #default="{ row }">
              <el-input-number
                  v-model="row.quantity"
                  :min="1"
                  :max="9999"
                  size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="采购单价" width="120">
            <template #default="{ row }">
              <el-input-number
                  v-model="row.price"
                  :min="0.01"
                  :precision="2"
                  size="small"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template #default="{ row }">
              ¥{{ formatMoney((row.price || 0) * (row.quantity || 0)) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ $index }">
              <el-button
                  type="danger"
                  link
                  @click="handleRemoveItem($index)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-button
            type="primary"
            plain
            @click="handleAddItem"
            style="margin-top: 10px"
        >
          <el-icon><Plus /></el-icon>
          添加商品
        </el-button>

        <div style="margin-top: 15px; text-align: right; font-weight: bold">
          总计：¥{{ formatMoney(calculateTotal) }}
        </div>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getPurchasePage,
  createPurchase,
  auditPurchase,
  voidPurchase,
  getSupplierList,
  getProductList
} from '@/api/purchase'

const loading = ref(false)
const submitting = ref(false)
const plans = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const supplierList = ref([])
const productList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null
})

const planForm = reactive({
  supplierId: null,
  remark: '',
  items: []
})

const calculateTotal = computed(() => {
  return planForm.items.reduce((sum, item) => {
    return sum + (item.price || 0) * (item.quantity || 0)
  }, 0)
})

const fetchPlans = async () => {
  loading.value = true
  try {
    const res = await getPurchasePage(queryParams)
    if (res.code === 200) {
      plans.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取采购计划失败:', error)
    ElMessage.error('获取采购计划失败')
  } finally {
    loading.value = false
  }
}

const fetchSuppliers = async () => {
  try {
    const res = await getSupplierList()
    if (res.code === 200) {
      supplierList.value = res.data || []
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error)
  }
}

const fetchProductList = async () => {
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      productList.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  }
}

const handleCreatePlan = () => {
  planForm.supplierId = null
  planForm.remark = ''
  planForm.items = []
  dialogVisible.value = true
  fetchSuppliers()
  fetchProductList()
}

const handleAddItem = () => {
  planForm.items.push({
    productId: null,
    productName: '',
    spec: '',
    currentStock: 0,
    quantity: 1,
    price: 0
  })
}

const handleRemoveItem = (index) => {
  planForm.items.splice(index, 1)
}

const handleProductChange = (index) => {
  const item = planForm.items[index]
  const product = productList.value.find(p => p.id === item.productId)
  if (product) {
    item.productName = product.name
    item.spec = product.spec
    item.currentStock = product.stock
    item.price = product.costPrice || 0
  }
}

const handleSubmit = async () => {
  if (!planForm.supplierId) {
    ElMessage.warning('请选择供应商')
    return
  }
  if (planForm.items.length === 0) {
    ElMessage.warning('请至少添加一个商品')
    return
  }

  const hasEmptyItem = planForm.items.some(item => !item.productId || item.quantity <= 0)
  if (hasEmptyItem) {
    ElMessage.warning('请完善商品信息')
    return
  }

  submitting.value = true
  try {
    const data = {
      supplierId: planForm.supplierId,
      remark: planForm.remark,
      items: planForm.items.map(item => ({
        productId: item.productId,
        quantity: item.quantity,
        price: item.price
      }))
    }

    const res = await createPurchase(data)
    if (res.code === 200) {
      ElMessage.success('采购计划创建成功')
      dialogVisible.value = false
      fetchPlans()
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (error) {
    console.error('创建采购计划失败:', error)
    ElMessage.error('创建采购计划失败')
  } finally {
    submitting.value = false
  }
}

const handleViewDetail = (row) => {
  ElMessage.info(`查看采购单详情: ${row.billNo}`)
}

const handleAudit = async (row) => {
  try {
    await ElMessageBox.confirm('确认审核通过并入库吗？', '提示', {
      type: 'warning'
    })
    const res = await auditPurchase(row.id)
    if (res.code === 200) {
      ElMessage.success('审核成功')
      fetchPlans()
    } else {
      ElMessage.error(res.message || '审核失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

const handleVoid = async (row) => {
  try {
    await ElMessageBox.confirm('确认作废该采购单吗？', '提示', {
      type: 'warning'
    })
    const res = await voidPurchase(row.id)
    if (res.code === 200) {
      ElMessage.success('作废成功')
      fetchPlans()
    } else {
      ElMessage.error(res.message || '作废失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('作废失败:', error)
      ElMessage.error('作废失败')
    }
  }
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', '-1': 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已入库', '-1': '已作废' }
  return texts[status] || '未知'
}

const formatMoney = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toFixed(2)
}

onMounted(() => {
  fetchPlans()
})
</script>

<style scoped>
.purchase-plan-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
