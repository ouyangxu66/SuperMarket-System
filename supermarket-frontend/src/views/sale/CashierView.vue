<template>
  <div class="cashier-container">
    <el-row :gutter="20">
      <el-col :span="14">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>商品搜索</span>
            </div>
          </template>

          <el-form :inline="true" :model="productQuery" class="search-form">
            <el-form-item label="商品名称">
              <el-input v-model="productQuery.name" placeholder="请输入商品名称" clearable @keyup.enter="fetchProductList" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="fetchProductList">搜索</el-button>
              <el-button icon="Refresh" @click="resetProductQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table :data="productList" v-loading="productLoading" border max-height="320">
            <el-table-column prop="barcode" label="条码" width="140" />
            <el-table-column prop="name" label="商品名称" min-width="160" />
            <el-table-column prop="price" label="售价" width="100" align="right">
              <template #default="scope">¥ {{ Number(scope.row.price || 0).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="90" align="center" />
            <el-table-column label="操作" width="140" align="center">
              <template #default="scope">
                <el-button type="primary" link @click="addToCart(scope.row)">加入购物车</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>购物车</span>
              <span>共 {{ cartList.length }} 件商品</span>
            </div>
          </template>

          <el-table :data="cartList" border empty-text="请先添加商品">
            <el-table-column prop="name" label="商品名称" min-width="160" />
            <el-table-column prop="price" label="单价" width="100" align="right">
              <template #default="scope">¥ {{ Number(scope.row.price || 0).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="数量" width="160" align="center">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" :max="scope.row.stock || 9999" @change="refreshCart" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120" align="right">
              <template #default="scope">¥ {{ calcItemAmount(scope.row).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="90" align="center">
              <template #default="scope">
                <el-button type="danger" link @click="removeCartItem(scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>会员绑定</span>
            </div>
          </template>

          <el-form :inline="true" class="member-search-form">
            <el-form-item>
              <el-input v-model="memberKeyword" placeholder="请输入手机号/姓名" clearable @keyup.enter="searchMember" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchMember">查询会员</el-button>
              <el-button @click="clearMember">清空</el-button>
            </el-form-item>
          </el-form>

          <el-descriptions v-if="selectedMember" :column="1" border>
            <el-descriptions-item label="会员姓名">{{ selectedMember.name }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ selectedMember.phone }}</el-descriptions-item>
            <el-descriptions-item label="储值余额">¥ {{ Number(selectedMember.balance || 0).toFixed(2) }}</el-descriptions-item>
            <el-descriptions-item label="当前积分">{{ selectedMember.points || 0 }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="未绑定会员" :image-size="80" />
        </el-card>

        <el-card class="section-card">
          <template #header>
            <div class="card-header">
              <span>订单结算</span>
            </div>
          </template>

          <el-form :model="checkoutForm" label-width="100px">
            <el-form-item label="应收金额">
              <span class="amount-text">¥ {{ totalAmount.toFixed(2) }}</span>
            </el-form-item>

            <el-form-item label="积分抵扣" v-if="selectedMember">
              <div style="display: flex; flex-direction: column; width: 100%;">
                <el-checkbox v-model="checkoutForm.usePoints" @change="handlePointsChange" :disabled="maxDeductiblePoints < 100">
                  使用积分抵扣 (当前积分: {{ selectedMember.points }})
                </el-checkbox>
                <div v-if="checkoutForm.usePoints" style="margin-top: 10px;">
                  <el-input-number
                    v-model="checkoutForm.usedPoints"
                    :min="maxDeductiblePoints >= 100 ? 100 : 0"
                    :max="maxDeductiblePoints"
                    :step="100"
                    style="width: 150px"
                    @change="handlePointsChange"
                  />
                  <span style="margin-left: 10px; color: #f56c6c;">抵扣金额: ¥{{ pointDeductAmount.toFixed(2) }}</span>
                </div>
              </div>
            </el-form-item>

            <el-form-item label="实收金额">
              <el-input-number v-model="checkoutForm.realPayAmount" :min="0" :precision="2" :step="0.1" style="width: 150px;" />
            </el-form-item>
            <el-form-item label="支付方式">
              <el-radio-group v-model="checkoutForm.paymentType">
                <el-radio :value="1">现金</el-radio>
                <el-radio :value="2">微信</el-radio>
                <el-radio :value="3">支付宝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="订单备注">
              <el-input v-model="checkoutForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-form>

          <div class="checkout-summary">
            <div class="summary-row"><span>商品种数</span><span>{{ cartList.length }}</span></div>
            <div class="summary-row"><span>商品总件数</span><span>{{ totalQuantity }}</span></div>
            <div class="summary-row total"><span>合计</span><span>¥ {{ totalAmount.toFixed(2) }}</span></div>
          </div>

          <div class="checkout-actions">
            <el-button @click="clearCart">清空购物车</el-button>
            <el-button type="primary" :loading="checkoutLoading" @click="submitCheckout">立即结算</el-button>
          </div>

          <el-alert
            v-if="lastOrderNo"
            title="结算成功"
            type="success"
            :closable="false"
            show-icon
            class="checkout-result"
          >
            <template #default>
              订单号：{{ lastOrderNo }}
            </template>
          </el-alert>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductPage } from '@/api/product'
import { getSimpleMember } from '@/api/member'
import { checkout } from '@/api/sale'

const productLoading = ref(false)
const checkoutLoading = ref(false)
const productList = ref([])
const cartList = ref([])
const selectedMember = ref(null)
const memberKeyword = ref('')
const lastOrderNo = ref('')

const productQuery = reactive({
  pageNum: 1,
  pageSize: 20,
  name: '',
  status: 1
})

const checkoutForm = reactive({
  paymentType: 1,
  realPayAmount: 0,
  remark: '',
  usePoints: false,
  usedPoints: 0
})

const totalAmount = computed(() => {
  return cartList.value.reduce((sum, item) => sum + calcItemAmount(item), 0)
})

const totalQuantity = computed(() => {
  return cartList.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
})

const maxDeductiblePoints = computed(() => {
  if (!selectedMember.value) return 0
  const maxPointsByAmount = Math.floor(totalAmount.value) * 100
  return Math.min(selectedMember.value.points || 0, maxPointsByAmount)
})

const pointDeductAmount = computed(() => {
  return Math.floor(checkoutForm.usedPoints / 100)
})

const handlePointsChange = () => {
  if (checkoutForm.usePoints) {
    let newPoints = checkoutForm.usedPoints
    if (!newPoints || newPoints < 100) {
       newPoints = 100
    }
    if (newPoints > maxDeductiblePoints.value) {
       newPoints = maxDeductiblePoints.value
    }
    newPoints -= newPoints % 100

    if (newPoints < 0) newPoints = 0

    checkoutForm.usedPoints = newPoints
    checkoutForm.realPayAmount = Math.max(0, Number((totalAmount.value - (newPoints / 100)).toFixed(2)))
  } else {
    checkoutForm.usedPoints = 0
    checkoutForm.realPayAmount = Number(totalAmount.value.toFixed(2))
  }
}

onMounted(() => {
  fetchProductList()
})

const fetchProductList = async () => {
  productLoading.value = true
  try {
    const res = await getProductPage(productQuery)
    productList.value = (res.data.records || []).filter(item => Number(item.stock || 0) > 0)
  } catch (error) {
    console.error('获取商品列表失败', error)
  } finally {
    productLoading.value = false
  }
}

const resetProductQuery = () => {
  productQuery.name = ''
  fetchProductList()
}

const addToCart = (product) => {
  const existing = cartList.value.find(item => item.id === product.id)
  if (existing) {
    if (existing.quantity >= Number(product.stock || 0)) {
      ElMessage.warning('库存不足，无法继续添加')
      return
    }
    existing.quantity += 1
  } else {
    cartList.value.push({
      ...product,
      quantity: 1
    })
  }
  syncRealPayAmount()
}

const removeCartItem = (index) => {
  cartList.value.splice(index, 1)
}

const refreshCart = () => {
  syncRealPayAmount()
}

const clearCart = (showAlert = true) => {
  cartList.value = []
  syncRealPayAmount()
  if (showAlert !== false) {
    ElMessage.success('已清空购物车')
  }
}

const calcItemAmount = (item) => Number(item.price || 0) * Number(item.quantity || 0)

const syncRealPayAmount = () => {
  if (checkoutForm.usePoints) {
    if (checkoutForm.usedPoints > maxDeductiblePoints.value) {
       checkoutForm.usedPoints = maxDeductiblePoints.value - (maxDeductiblePoints.value % 100)
    }
    checkoutForm.realPayAmount = Math.max(0, Number((totalAmount.value - checkoutForm.usedPoints / 100).toFixed(2)))
  } else {
    checkoutForm.realPayAmount = Number(totalAmount.value.toFixed(2))
  }
}

watch(totalAmount, (newVal) => {
  syncRealPayAmount()
})

const searchMember = async () => {
  const keyword = memberKeyword.value.trim()
  if (!keyword) {
    ElMessage.warning('请输入手机号或姓名')
    return
  }
  try {
    const params = /^1\d{10}$/.test(keyword)
      ? { phone: keyword }
      : { name: keyword }
    const res = await getSimpleMember(params)
    selectedMember.value = res.data
    ElMessage.success('会员绑定成功')
  } catch (error) {
    selectedMember.value = null
    console.error('查询会员失败', error)
  }
}

const clearMember = () => {
  memberKeyword.value = ''
  selectedMember.value = null
  checkoutForm.usePoints = false
  checkoutForm.usedPoints = 0
  syncRealPayAmount()
}

const submitCheckout = async () => {
  if (cartList.value.length === 0) {
    ElMessage.warning('请先添加商品到购物车')
    return
  }
  if (Number(checkoutForm.realPayAmount || 0) <= 0) {
    ElMessage.warning('实收金额必须大于 0')
    return
  }

  checkoutLoading.value = true
  try {
    const payload = {
      ...checkoutForm,
      memberId: selectedMember.value?.id || null,
      remark: checkoutForm.remark?.trim() || '',
      items: cartList.value.map(item => ({
        productId: item.id,
        quantity: item.quantity
      }))
    }
    const res = await checkout(payload)
    lastOrderNo.value = res.data
    ElMessage.success('结算成功')
    clearCart(false)
    clearMember()
    checkoutForm.remark = ''
    await fetchProductList()
  } catch (error) {
    console.error('结算失败', error)
  } finally {
    checkoutLoading.value = false
  }
}
</script>

<style scoped>
.cashier-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkout-summary {
  margin-top: 16px;
  padding: 12px 16px;
  background: #fafafa;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #606266;
}

.summary-row.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.checkout-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.checkout-result {
  margin-top: 20px;
}

.member-search-form {
  margin-bottom: 12px;
}

.amount-text {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}
</style>
