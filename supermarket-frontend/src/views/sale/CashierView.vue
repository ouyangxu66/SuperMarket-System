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

        <el-card class="section-card checkout-card">
          <template #header>
            <div class="card-header">
              <span>订单结算</span>
            </div>
          </template>

          <el-row :gutter="16">
            <el-col :span="13">
              <div class="amount-info-card">
                <div class="amount-row">
                  <span class="amount-label">应收金额：</span>
                  <span class="amount-value">¥ {{ totalAmount.toFixed(2) }}</span>
                </div>
                <div class="amount-row" v-if="selectedMember && checkoutForm.usePoints">
                  <span class="amount-label">积分抵扣：</span>
                  <span class="amount-value discount">- ¥ {{ pointDeductAmount.toFixed(2) }}</span>
                </div>
                <div class="amount-row total">
                  <span class="amount-label">实收金额：</span>
                  <span class="amount-value highlight">¥ {{ checkoutForm.realPayAmount.toFixed(2) }}</span>
                </div>
              </div>

              <div class="cash-input-section" v-if="checkoutForm.paymentType === 1">
                <el-form :model="checkoutForm" label-width="100px">
                  <el-form-item label="现金收取">
                    <el-input-number
                        v-model="checkoutForm.realPayAmount"
                        :min="0"
                        :precision="2"
                        :step="0.01"
                        style="width: 100%;"
                        placeholder="请输入现金金额"
                    />
                  </el-form-item>
                  <el-form-item label="应找零钱">
                    <span class="change-amount">¥ {{ (checkoutForm.realPayAmount - totalAmount).toFixed(2) }}</span>
                  </el-form-item>
                </el-form>
              </div>

              <div class="numeric-keyboard">
                <el-row :gutter="8">
                  <el-col :span="8" v-for="key in numericKeys" :key="key.value">
                    <el-button
                        :class="['num-key', key.type || '']"
                        @click="handleNumericKey(key)"
                        :disabled="checkoutForm.paymentType !== 1"
                    >
                      {{ key.label }}
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </el-col>

            <el-col :span="11">
              <div class="payment-method-section">
                <div class="section-title">支付方式</div>
                <el-radio-group v-model="checkoutForm.paymentType" class="payment-radio-group">
                  <el-radio-button :value="1" class="payment-btn">
                    <el-icon><Wallet /></el-icon>
                    <span>现金</span>
                  </el-radio-button>
                  <el-radio-button :value="2" class="payment-btn">
                    <el-icon><ChatDotRound /></el-icon>
                    <span>微信</span>
                  </el-radio-button>
                  <el-radio-button :value="3" class="payment-btn">
                    <el-icon><Shop /></el-icon>
                    <span>支付宝</span>
                  </el-radio-button>
                </el-radio-group>
              </div>

              <div class="points-section" v-if="selectedMember">
                <el-checkbox v-model="checkoutForm.usePoints" @change="handlePointsChange" :disabled="maxDeductiblePoints < 100">
                  使用积分抵扣
                </el-checkbox>
                <div v-if="checkoutForm.usePoints" class="points-input-area">
                  <div class="points-info">
                    <span>可用积分：{{ selectedMember.points }}</span>
                    <span>最多抵扣：{{ maxDeductiblePoints }} 积分 (¥{{ (Math.floor(maxDeductiblePoints / 100)).toFixed(2) }})</span>
                  </div>
                  <el-input-number
                      v-model="checkoutForm.usedPoints"
                      :min="100"
                      :max="maxDeductiblePoints"
                      :step="100"
                      style="width: 100%; margin-top: 8px;"
                      @change="handlePointsChange"
                      placeholder="输入积分数量"
                  />
                  <div class="points-deduct-amount">
                    抵扣金额：<span class="highlight">¥{{ pointDeductAmount.toFixed(2) }}</span>
                  </div>
                </div>
              </div>

              <div class="remark-section">
                <el-input
                    v-model="checkoutForm.remark"
                    type="textarea"
                    :rows="2"
                    placeholder="订单备注（选填）"
                    maxlength="200"
                    show-word-limit
                />
              </div>

              <div class="action-buttons">
                <el-button @click="clearCart" :disabled="cartList.length === 0">
                  <el-icon><Delete /></el-icon>
                  清空购物车
                </el-button>
                <el-button type="primary" :loading="checkoutLoading" @click="submitCheckout" :disabled="cartList.length === 0">
                  <el-icon><Check /></el-icon>
                  立即结算
                </el-button>
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
                  <div class="success-content">
                    <span>订单号：<strong>{{ lastOrderNo }}</strong></span>
                    <el-button type="primary" size="small" @click="printReceipt" style="margin-left: 12px;">
                      <el-icon><Printer /></el-icon>
                      打印小票
                    </el-button>
                  </div>
                </template>
              </el-alert>

            </el-col>
          </el-row>

          <div class="order-summary-footer">
            <div class="summary-item">
              <el-tag type="info" size="small">商品种数</el-tag>
              <span>{{ cartList.length }}</span>
            </div>
            <div class="summary-item">
              <el-tag type="info" size="small">总件数</el-tag>
              <span>{{ totalQuantity }}</span>
            </div>
            <div class="summary-item total">
              <span>合计金额：</span>
              <span class="total-amount">¥ {{ totalAmount.toFixed(2) }}</span>
            </div>
          </div>
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
import { Wallet, ChatDotRound, Shop, Delete, Check, Printer } from '@element-plus/icons-vue'
const productLoading = ref(false)
const checkoutLoading = ref(false)
const productList = ref([])
const cartList = ref([])
const selectedMember = ref(null)
const memberKeyword = ref('')
const lastOrderNo = ref('')
const lastOrderItems = ref([])
const lastOrderTotal = ref(0)
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
const numericKeys = ref([
  { value: '1', label: '1' },
  { value: '2', label: '2' },
  { value: '3', label: '3' },
  { value: '4', label: '4' },
  { value: '5', label: '5' },
  { value: '6', label: '6' },
  { value: '7', label: '7' },
  { value: '8', label: '8' },
  { value: '9', label: '9' },
  { value: '0', label: '0' },
  { value: '00', label: '00' },
  { value: '.', label: '.' }
])

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
const handleNumericKey = (key) => {
  if (checkoutForm.paymentType !== 1) return

  const currentValue = checkoutForm.realPayAmount.toString()
  let newValue = currentValue

  if (key.value === '.') {
    if (!currentValue.includes('.')) {
      newValue = currentValue + '.'
    }
  } else if (key.value === '00') {
    newValue = currentValue + '00'
  } else {
    newValue = currentValue + key.value
  }

  const numValue = parseFloat(newValue)
  if (!isNaN(numValue) && numValue >= 0) {
    checkoutForm.realPayAmount = Math.min(numValue, 999999.99)
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
    lastOrderTotal.value = totalAmount.value
    lastOrderItems.value = [...cartList.value]
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
const printReceipt = () => {
  if (!lastOrderNo.value || lastOrderItems.value.length === 0) {
    ElMessage.warning('没有可打印的订单')
    return
  }

  const paymentTypeMap = { 1: '现金', 2: '微信', 3: '支付宝' }

  const receiptHTML = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <title>收银小票</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: '宋体', monospace; font-size: 12px; line-height: 1.4; padding: 10px; width: 80mm; }
        .header { text-align: center; border-bottom: 1px dashed #000; padding-bottom: 8px; margin-bottom: 8px; }
        .header h2 { font-size: 16px; margin-bottom: 4px; }
        .info-row { display: flex; justify-content: space-between; margin-bottom: 4px; }
        .items { border-bottom: 1px dashed #000; padding-bottom: 8px; margin-bottom: 8px; }
        .item-row { display: flex; justify-content: space-between; margin-bottom: 4px; }
        .item-name { flex: 2; }
        .item-qty { flex: 1; text-align: center; }
        .item-price { flex: 1; text-align: right; }
        .total { border-bottom: 1px dashed #000; padding-bottom: 8px; margin-bottom: 8px; }
        .total-row { display: flex; justify-content: space-between; font-weight: bold; font-size: 14px; }
        .footer { text-align: center; margin-top: 8px; }
        .divider { border-bottom: 1px dashed #000; margin: 8px 0; }
      </style>
    </head>
    <body>
      <div class="header">
        <h2>超市管理系统</h2>
        <div>收银小票</div>
      </div>
      <div class="info-row">
        <span>订单号：${lastOrderNo.value}</span>
      </div>
      <div class="info-row">
        <span>时间：${new Date().toLocaleString('zh-CN')}</span>
      </div>
      ${selectedMember.value ? `<div class="info-row"><span>会员：${selectedMember.value.name}</span></div>` : ''}
      <div class="divider"></div>
      <div class="items">
    ${lastOrderItems.value.map(item => `
          <div class="item-row">
            <span class="item-name">${item.name}</span>
            <span class="item-qty">×${item.quantity}</span>
            <span class="item-price">¥${calcItemAmount(item).toFixed(2)}</span>
          </div>
        `).join('')}
      </div>
      <div class="total">
        <div class="total-row">
          <span>商品数量：</span>
          <span>${lastOrderItems.value.reduce((s, i) => s + Number(i.quantity || 0), 0)} 件</span>
        </div>
        <div class="total-row">
          <span>应收金额：</span>
          <span>¥ ${lastOrderTotal.value.toFixed(2)}</span>
        </div>
        ${checkoutForm.usePoints ? `
          <div class="info-row">
            <span>积分抵扣：</span>
            <span>- ¥ ${pointDeductAmount.value.toFixed(2)}</span>
          </div>
        ` : ''}
        <div class="total-row">
          <span>实收金额：</span>
          <span>¥ ${checkoutForm.realPayAmount.toFixed(2)}</span>
        </div>
        <div class="info-row">
          <span>支付方式：</span>
          <span>${paymentTypeMap[checkoutForm.paymentType] || '未知'}</span>
        </div>
        ${checkoutForm.paymentType === 1 ? `
          <div class="info-row">
            <span>找零：</span>
            <span>¥ ${(checkoutForm.realPayAmount - lastOrderTotal.value).toFixed(2)}</span>
          </div>
        ` : ''}
      </div>
      <div class="footer">
        <p>谢谢惠顾，欢迎下次光临！</p>
        <p style="margin-top: 4px; font-size: 10px;">${new Date().toLocaleString('zh-CN')}</p>
      </div>
    </body>
    </html>
  `

  const printWindow = window.open('', '_blank', 'width=300,height=600')
  printWindow.document.write(receiptHTML)
  printWindow.document.close()

  setTimeout(() => {
    printWindow.print()
    printWindow.close()
  }, 500)
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

.checkout-card {
  min-height: 600px;
}

.amount-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 16px;
}

.amount-row:last-child {
  margin-bottom: 0;
}

.amount-label {
  opacity: 0.9;
}

.amount-value {
  font-weight: 600;
  font-size: 20px;
}

.amount-value.discount {
  color: #ffd700;
}

.amount-value.highlight {
  font-size: 28px;
  color: #ffd700;
}

.amount-row.total {
  border-top: 1px solid rgba(255, 255, 255, 0.3);
  padding-top: 12px;
  margin-top: 12px;
}

.cash-input-section {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.change-amount {
  font-size: 18px;
  font-weight: 600;
  color: #67c23a;
}

.numeric-keyboard {
  background: #fff;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.num-key {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: 500;
  margin-bottom: 8px;
  border-radius: 6px;
}

.num-key:hover {
  background: #f5f7fa;
}

.num-key:active:not(:disabled) {
  background: #409eff;
  color: white;
}

.num-key:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.payment-method-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.payment-radio-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.payment-btn {
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 15px;
}

.payment-btn :deep(.el-radio-button__inner) {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.points-section {
  background: #fdf6ec;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #e6a23c;
}

.points-section .el-checkbox {
  font-weight: 600;
  margin-bottom: 12px;
}

.points-input-area {
  margin-top: 12px;
}

.points-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.points-deduct-amount {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}

.points-deduct-amount .highlight {
  font-weight: 600;
  color: #f56c6c;
  font-size: 16px;
}

.remark-section {
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.action-buttons .el-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.checkout-result {
  margin-top: 12px;
}

.order-summary-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  margin-top: 20px;
  border-top: 2px solid #ebeef5;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.summary-item.total {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.total-amount {
  font-size: 22px;
  color: #f56c6c;
  font-weight: 700;
}

.summary-item.total {
  border-left: 2px solid #e4e7ed;
  padding-left: 16px;
}

.member-search-form {
  margin-bottom: 12px;
}

.amount-text {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}
.success-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap; /* 允许换行，防止按钮被遮挡 */
  gap: 8px;        /* 增加文字和按钮之间的间距 */
}
</style>
