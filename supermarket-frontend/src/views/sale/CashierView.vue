<template>
  <div class="cashier-container">
    <!-- 顶部信息栏 -->
    <div class="header-bar">
      <div class="header-item">
        <span class="label">订单号:</span>
        <span class="value">{{ currentOrderNo }}</span>
      </div>
      <div class="header-item">
        <span class="label">时间:</span>
        <span class="value">{{ currentTime }}</span>
      </div>
      <div class="header-item">
        <span class="label">收银员:</span>
        <span class="value">{{ currentUser }}</span>
      </div>
    </div>

    <div class="main-content">
      <!-- 左侧区域 - 购物车列表 -->
      <div class="left-panel">
        <div class="panel-section cart-section">
          <div class="section-title">购物车列表</div>
          <div class="cart-list" v-loading="productLoading">
            <div v-if="cartList.length === 0" class="empty-cart">
              <el-empty description="购物车为空，请扫码或搜索添加商品" />
            </div>
            <div v-else class="cart-items">
              <div class="cart-item" v-for="(item, index) in cartList" :key="item.id || index">
                <div class="item-info">
                  <div class="item-name">{{ item.name }}</div>
                  <div class="item-price">¥{{ Number(item.price || 0).toFixed(2) }}</div>
                </div>
                <div class="item-quantity">
                  <el-input-number
                      v-model="item.quantity"
                      :min="1"
                      :max="item.stock || 9999"
                      size="small"
                      @change="refreshCart"
                  />
                </div>
                <div class="item-subtotal">¥{{ calcItemAmount(item).toFixed(2) }}</div>
                <div class="item-actions">
                  <el-button type="danger" link @click="removeCartItem(index)">删除</el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 商品搜索 -->
          <div class="product-search">
            <el-input
                v-model="productQuery.name"
                placeholder="输入商品名称或条码搜索"
                clearable
                @keyup.enter="fetchProductList"
                @clear="handleClearSearch"
            >
              <template #append>
                <el-button icon="Search" @click="fetchProductList">搜索</el-button>
              </template>
            </el-input>
          </div>

          <!-- 商品列表 -->
          <div class="product-grid-section">
            <div class="section-subtitle">点击商品添加到购物车</div>
            <div class="product-grid" v-loading="productLoading">
              <div
                  v-for="product in displayedProducts"
                  :key="product.id"
                  class="product-card"
                  @click="addToCartFromGrid(product)"
              >
                <div class="product-image">
                  <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
                  <el-icon v-else :size="40" color="#909399"><Goods /></el-icon>
                </div>
                <div class="product-info">
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-spec">{{ product.spec || '标准' }}</div>
                  <div class="product-bottom">
                    <span class="product-price">¥{{ Number(product.price || 0).toFixed(2) }}</span>
                    <span class="product-stock">库存:{{ product.stock }}</span>
                  </div>
                </div>
                <div class="add-badge">
                  <el-icon><Plus /></el-icon>
                </div>
              </div>
              <el-empty v-if="displayedProducts.length === 0" description="暂无商品" :image-size="80" />
            </div>
            <div class="product-pagination" v-if="productList.length > pageSize">
              <el-pagination
                  v-model:current-page="currentPage"
                  :page-size="pageSize"
                  :total="productList.length"
                  layout="prev, pager, next"
                  @current-change="handlePageChange"
                  small
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧区域 - 扫码和结算 -->
      <div class="right-panel">
        <!-- 扫码/输入区域 -->
        <div class="panel-section input-section">
          <div class="section-title">扫码/输入区域</div>
          <div class="input-area">
            <el-input
                v-model="barcodeInput"
                placeholder="扫码枪输入框"
                @keyup.enter="handleBarcodeInput"
                ref="barcodeInputRef"
                size="large"
                @input="handleBarcodeInputChange"
            />
            <div class="barcode-hint">{{ barcodeInput || '[扫码商品条码]' }}</div>
          </div>
        </div>

        <!-- 数字键盘 -->
        <div class="panel-section keypad-section">
          <div class="section-title">数字键盘</div>
          <div class="numeric-keyboard">
            <div class="key-row">
              <el-button class="num-key" @click="handleNumericKey('1')" :disabled="checkoutForm.paymentType !== 1">1</el-button>
              <el-button class="num-key" @click="handleNumericKey('2')" :disabled="checkoutForm.paymentType !== 1">2</el-button>
              <el-button class="num-key" @click="handleNumericKey('3')" :disabled="checkoutForm.paymentType !== 1">3</el-button>
            </div>
            <div class="key-row">
              <el-button class="num-key" @click="handleNumericKey('4')" :disabled="checkoutForm.paymentType !== 1">4</el-button>
              <el-button class="num-key" @click="handleNumericKey('5')" :disabled="checkoutForm.paymentType !== 1">5</el-button>
              <el-button class="num-key" @click="handleNumericKey('6')" :disabled="checkoutForm.paymentType !== 1">6</el-button>
            </div>
            <div class="key-row">
              <el-button class="num-key" @click="handleNumericKey('7')" :disabled="checkoutForm.paymentType !== 1">7</el-button>
              <el-button class="num-key" @click="handleNumericKey('8')" :disabled="checkoutForm.paymentType !== 1">8</el-button>
              <el-button class="num-key" @click="handleNumericKey('9')" :disabled="checkoutForm.paymentType !== 1">9</el-button>
            </div>
            <div class="key-row">
              <el-button class="num-key" @click="handleNumericKey('0')" :disabled="checkoutForm.paymentType !== 1">0</el-button>
              <el-button class="num-key" @click="handleNumericKey('00')" :disabled="checkoutForm.paymentType !== 1">00</el-button>
              <el-button class="num-key" @click="handleNumericKey('.')" :disabled="checkoutForm.paymentType !== 1">·</el-button>
            </div>
            <div class="key-row">
              <el-button class="action-key delete-key" @click="handleDeleteKey" :disabled="checkoutForm.paymentType !== 1">删除</el-button>
              <el-button class="action-key confirm-key" @click="handleConfirmKey" :disabled="checkoutForm.paymentType !== 1 || cartList.length === 0">确认</el-button>
            </div>
          </div>
        </div>

        <!-- 快捷功能区 -->
        <div class="panel-section quick-actions">
          <div class="section-title">快捷功能区</div>
          <div class="quick-buttons">
            <el-button class="quick-btn" @click="holdOrder">挂单</el-button>
            <el-button class="quick-btn" @click="cancelOrder">取消</el-button>
            <el-button class="quick-btn" @click="returnGoods">退货</el-button>
            <el-button class="quick-btn" @click="openDrawer">开钱箱</el-button>
          </div>
        </div>

        <!-- 结算面板 -->
        <div class="panel-section checkout-section">
          <div class="section-title">结算面板</div>
          <div class="checkout-info">
            <div class="info-row">
              <span class="info-label">商品数量:</span>
              <span class="info-value">{{ totalQuantity }}件</span>
            </div>
            <div class="info-row">
              <span class="info-label">应收金额:</span>
              <span class="info-value">¥ {{ totalAmount.toFixed(2) }}</span>
            </div>
            <div class="info-row discount-row" v-if="selectedMember && checkoutForm.usePoints">
              <span class="info-label">会员折扣:</span>
              <span class="info-value discount">- ¥ {{ pointDeductAmount.toFixed(2) }}</span>
            </div>
            <div class="info-row total-row">
              <span class="info-label">实收金额:</span>
              <span class="info-value highlight">¥ {{ checkoutForm.realPayAmount.toFixed(2) }}</span>
            </div>
            <div class="info-row change-row" v-if="checkoutForm.paymentType === 1 && checkoutForm.realPayAmount >= totalAmount">
              <span class="info-label">找零金额:</span>
              <span class="info-value change">¥ {{ (checkoutForm.realPayAmount - totalAmount).toFixed(2) }}</span>
            </div>
          </div>

          <!-- 会员绑定 -->
          <div class="member-section">
            <div class="member-title">会员</div>
            <template v-if="!selectedMember">
              <el-input
                  v-model="memberKeyword"
                  placeholder="手机号/姓名"
                  @keyup.enter="searchMember"
                  size="default"
                  clearable
              >
                <template #append>
                  <el-button @click="searchMember" type="primary">查询</el-button>
                </template>
              </el-input>
            </template>
            <template v-else>
              <div class="member-info">
                <div class="member-header">
                  <span class="member-name">{{ selectedMember.name }}</span>
                  <el-button type="primary" link @click="clearMember" size="small">清除</el-button>
                </div>
                <div class="member-details">
                  <span>余额: ¥{{ Number(selectedMember.balance || 0).toFixed(2) }}</span>
                  <span>积分: {{ selectedMember.points }}</span>
                </div>
                <el-checkbox v-model="checkoutForm.usePoints" @change="handlePointsChange" :disabled="maxDeductiblePoints < 100" style="margin-top: 8px;">
                  使用积分抵扣
                </el-checkbox>
                <div v-if="checkoutForm.usePoints" class="points-input">
                  <el-input-number
                      v-model="checkoutForm.usedPoints"
                      :min="100"
                      :max="maxDeductiblePoints"
                      :step="100"
                      size="small"
                      @change="handlePointsChange"
                  />
                  <span class="points-hint">可抵 ¥{{ pointDeductAmount.toFixed(2) }}</span>
                </div>
              </div>
            </template>
          </div>

          <!-- 支付方式选择 -->
          <div class="payment-methods">
            <div class="payment-title">支付方式</div>
            <el-radio-group v-model="checkoutForm.paymentType" class="payment-group">
              <el-radio-button :value="1" class="payment-btn">现金</el-radio-button>
              <el-radio-button :value="2" class="payment-btn">微信</el-radio-button>
              <el-radio-button :value="3" class="payment-btn">支付宝</el-radio-button>
            </el-radio-group>
          </div>


          <!-- 结算按钮 -->
          <div class="checkout-actions">
            <el-button
                type="primary"
                size="large"
                :loading="checkoutLoading"
                @click="submitCheckout"
                :disabled="cartList.length === 0"
                class="checkout-btn"
            >
              结算
            </el-button>
          </div>

          <!-- 结算成功提示 -->
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
                <span>订单号：{{ lastOrderNo }}</span>
                <el-button type="primary" size="small" @click="printReceipt">
                  打印小票
                </el-button>
              </div>
            </template>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductPage } from '@/api/product'
import { getSimpleMember } from '@/api/member'
import { checkout } from '@/api/sale'
import { Goods, Plus } from '@element-plus/icons-vue'
// 状态定义
const productLoading = ref(false)
const checkoutLoading = ref(false)
const productList = ref([])
const cartList = ref([])
const selectedMember = ref(null)
const memberKeyword = ref('')
const lastOrderNo = ref('')
const lastOrderItems = ref([])
const lastOrderTotal = ref(0)
const barcodeInput = ref('')
const barcodeInputRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(15)


// 当前用户信息
const currentOrderNo = ref('XS20240101001')
const currentTime = ref(new Date().toLocaleTimeString('zh-CN', { hour12: false }))
const currentUser = ref('张三')

// 更新时间的定时器
let timeTimer = null

const productQuery = reactive({
  pageNum: 1,
  pageSize: 100,
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

// 计算属性
const displayedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return productList.value.slice(start, end)
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

// 生命周期
onMounted(() => {
  fetchProductList()
  timeTimer = setInterval(() => {
    currentTime.value = new Date().toLocaleTimeString('zh-CN', { hour12: false })
  }, 1000)

  // 自动聚焦扫码输入框
  setTimeout(() => {
    barcodeInputRef.value?.focus()
  }, 500)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
})

// 方法定义
const handleBarcodeInputChange = (value) => {
  // 扫码枪输入完成后自动触发
  if (value && value.length > 5) {
    setTimeout(() => {
      handleBarcodeInput()
    }, 100)
  }
}
const handleClearSearch = () => {
  productQuery.name = ''
  currentPage.value = 1
  fetchProductList()
}

const handlePageChange = (page) => {
  currentPage.value = page
}


const addToCartFromGrid = (product) => {
  if (Number(product.stock || 0) <= 0) {
    ElMessage.warning('该商品库存不足')
    return
  }
  addToCart(product)
  ElMessage.success(`已添加: ${product.name}`)
}

const handleBarcodeInput = async () => {
  if (!barcodeInput.value.trim()) return

  const code = barcodeInput.value.trim()

  // 根据条码查找商品
  let product = productList.value.find(p => p.barcode === code)

  if (!product) {
    // 如果当前列表没有，尝试搜索
    productQuery.name = ''
    await fetchProductList()
    product = productList.value.find(p => p.barcode === code)
  }

  if (product) {
    addToCart(product)
    barcodeInput.value = ''
    ElMessage.success(`已添加: ${product.name}`)
  } else {
    ElMessage.warning(`未找到条码 [${code}] 对应的商品`)
    barcodeInput.value = ''
  }

  // 重新聚焦输入框
  setTimeout(() => {
    barcodeInputRef.value?.focus()
  }, 100)
}

const handleNumericKey = (key) => {
  if (checkoutForm.paymentType !== 1) return

  const currentValue = checkoutForm.realPayAmount.toString()
  let newValue = currentValue

  if (key === '.') {
    if (!currentValue.includes('.')) {
      newValue = currentValue === '' ? '0.' : currentValue + '.'
    }
  } else if (key === '00') {
    newValue = currentValue + '00'
  } else {
    newValue = currentValue === '0' ? key : currentValue + key
  }

  const numValue = parseFloat(newValue)
  if (!isNaN(numValue) && numValue >= 0) {
    checkoutForm.realPayAmount = Math.min(numValue, 999999.99)
  }
}

const handleDeleteKey = () => {
  if (checkoutForm.paymentType !== 1) return

  const currentValue = checkoutForm.realPayAmount.toString()
  if (currentValue.length > 0) {
    const newValue = currentValue.slice(0, -1)
    checkoutForm.realPayAmount = newValue ? parseFloat(newValue) || 0 : 0
  }
}

const handleConfirmKey = () => {
  if (checkoutForm.paymentType !== 1) return
  if (cartList.value.length > 0 && checkoutForm.realPayAmount >= totalAmount.value) {
    submitCheckout()
  } else if (checkoutForm.realPayAmount < totalAmount.value) {
    ElMessage.warning('收款金额不足')
  }
}

const fetchProductList = async () => {
  productLoading.value = true
  try {
    const res = await getProductPage(productQuery)
    productList.value = (res.data.records || []).filter(item => Number(item.stock || 0) > 0)
    currentPage.value = 1
  } catch (error) {
    console.error('获取商品列表失败', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    productLoading.value = false
  }
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
  syncRealPayAmount()
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
  if (checkoutForm.usePoints && selectedMember.value) {
    if (checkoutForm.usedPoints > maxDeductiblePoints.value) {
      checkoutForm.usedPoints = maxDeductiblePoints.value - (maxDeductiblePoints.value % 100)
    }
    checkoutForm.realPayAmount = Math.max(0, Number((totalAmount.value - checkoutForm.usedPoints / 100).toFixed(2)))
  } else {
    checkoutForm.realPayAmount = Number(totalAmount.value.toFixed(2))
  }
}

watch(totalAmount, () => {
  syncRealPayAmount()
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
    checkoutForm.usePoints = false
    checkoutForm.usedPoints = 0
    syncRealPayAmount()
    ElMessage.success('会员绑定成功')
  } catch (error) {
    selectedMember.value = null
    ElMessage.error('未找到该会员')
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

  if (checkoutForm.paymentType === 1 && checkoutForm.realPayAmount < totalAmount.value) {
    ElMessage.warning('收款金额不足')
    return
  }

  if (Number(checkoutForm.realPayAmount || 0) <= 0) {
    ElMessage.warning('实收金额必须大于 0')
    return
  }

  try {
    await ElMessageBox.confirm(
        `确认结算？\n应收: ¥${totalAmount.value.toFixed(2)}\n实收: ¥${checkoutForm.realPayAmount.toFixed(2)}`,
        '确认结算',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'info'
        }
    )
  } catch {
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
        quantity: Number(item.quantity)
      }))
    }

    const res = await checkout(payload)
    lastOrderNo.value = res.data
    ElMessage.success('结算成功')
    lastOrderTotal.value = totalAmount.value
    lastOrderItems.value = [...cartList.value]

    // 清空数据
    clearCart(false)
    clearMember()
    checkoutForm.remark = ''
    checkoutForm.realPayAmount = 0

    // 刷新商品列表
    await fetchProductList()

    // 更新订单号
    const date = new Date()
    const dateStr = date.toISOString().slice(0, 10).replace(/-/g, '')
    const randomNum = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
    currentOrderNo.value = `XS${dateStr}${randomNum}`

  } catch (error) {
    console.error('结算失败', error)
    ElMessage.error('结算失败，请重试')
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
  const changeAmount = checkoutForm.paymentType === 1 ? (checkoutForm.realPayAmount - lastOrderTotal.value) : 0

  const receiptHTML = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <title>收银小票</title>
      <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'SimSun', monospace; font-size: 12px; line-height: 1.6; padding: 10px; width: 80mm; }
        .header { text-align: center; border-bottom: 2px solid #000; padding-bottom: 10px; margin-bottom: 10px; }
        .header h2 { font-size: 18px; margin-bottom: 5px; }
        .info-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .divider { border-bottom: 1px dashed #000; margin: 10px 0; }
        .items { margin-bottom: 10px; }
        .item-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .item-name { flex: 2; }
        .item-qty { flex: 1; text-align: center; }
        .item-price { flex: 1; text-align: right; }
        .total { border-top: 2px solid #000; border-bottom: 2px solid #000; padding: 10px 0; margin: 10px 0; }
        .total-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .total-row.bold { font-weight: bold; font-size: 14px; }
        .footer { text-align: center; margin-top: 15px; }
        .footer p { margin: 3px 0; }
      </style>
    </head>
    <body>
      <div class="header">
        <h2>超市管理系统</h2>
        <div>收 银 小 票</div>
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
        <div class="total-row bold">
          <span>应收金额：</span>
          <span>¥ ${lastOrderTotal.value.toFixed(2)}</span>
        </div>
        ${checkoutForm.usePoints ? `
          <div class="total-row">
            <span>积分抵扣：</span>
            <span>- ¥ ${pointDeductAmount.value.toFixed(2)}</span>
          </div>
        ` : ''}
        <div class="total-row bold">
          <span>实收金额：</span>
          <span>¥ ${checkoutForm.realPayAmount.toFixed(2)}</span>
        </div>
        <div class="total-row">
          <span>支付方式：</span>
          <span>${paymentTypeMap[checkoutForm.paymentType] || '未知'}</span>
        </div>
        ${checkoutForm.paymentType === 1 && changeAmount > 0 ? `
          <div class="total-row">
            <span>找零：</span>
            <span>¥ ${changeAmount.toFixed(2)}</span>
          </div>
        ` : ''}
      </div>
      <div class="footer">
        <p>谢谢惠顾，欢迎下次光临！</p>
        <p style="font-size: 10px; margin-top: 8px;">${new Date().toLocaleString('zh-CN')}</p>
      </div>
    </body>
    </html>
  `

  const printWindow = window.open('', '_blank', 'width=320,height=600')
  printWindow.document.write(receiptHTML)
  printWindow.document.close()

  setTimeout(() => {
    printWindow.print()
    setTimeout(() => printWindow.close(), 1000)
  }, 500)
}

// 快捷功能
const holdOrder = () => {
  ElMessage.info('挂单功能开发中')
}

const cancelOrder = () => {
  if (cartList.value.length > 0) {
    ElMessageBox.confirm('确认清空购物车？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      clearCart()
    }).catch(() => {})
  }
}

const returnGoods = () => {
  ElMessage.info('退货功能开发中')
}

const openDrawer = () => {
  ElMessage.info('钱箱已打开')
}
</script>

<style scoped>
.cashier-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f0f2f5;
  padding: 12px;
  box-sizing: border-box;
  overflow: hidden;
}

/* 顶部信息栏 */
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 10px 20px;
  border-radius: 8px;
  margin-bottom: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  flex-shrink: 0;
}

/* 主内容区域 */
/* 主内容区域 */
.main-content {
  display: flex;
  flex: 1;
  gap: 12px;
  min-height: 0;
  overflow: hidden;
}

/* 左侧面板 - 购物车列表 */
.left-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 右侧面板 - 扫码和结算 */
.right-panel {
  width: 45%;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow-y: auto;
  overflow-x: hidden;
}

.checkout-section {
  display: flex;
  flex-direction: column;
}


/* 面板区域 */
.panel-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}


.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e4e7ed;
}

/* 扫码输入区域 */
/* 扫码输入区域 */
/* 扫码输入区域 */
.input-section {
  flex-shrink: 0;
  padding: 16px;
}

.input-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.barcode-hint {
  padding: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border: 2px dashed #909399;
  border-radius: 6px;
  text-align: center;
  color: #606266;
  min-height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 500;
}


/* 数字键盘 */
.keypad-section {
  flex-shrink: 0;
  padding: 16px;
}

.numeric-keyboard {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.key-row {
  display: flex;
  gap: 8px;
}

.num-key {
  flex: 1;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 6px;
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  transition: all 0.2s;
}

.num-key:hover:not(:disabled) {
  background-color: #e6e8eb;
  transform: translateY(-1px);
}

.num-key:active:not(:disabled) {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
  transform: scale(0.98);
}

.num-key:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.action-key {
  flex: 1;
  height: 44px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 6px;
}

.delete-key {
  background-color: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.delete-key:hover:not(:disabled) {
  background-color: #f78989;
}

.confirm-key {
  background-color: #67c23a;
  color: white;
  border-color: #67c23a;
}

.confirm-key:hover:not(:disabled) {
  background-color: #85ce61;
}

/* 快捷功能按钮 */
.quick-actions {
  flex-shrink: 0;
  padding: 16px;
}

.quick-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.quick-btn {
  height: 44px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
  background-color: #ecf5ff;
  color: #409eff;
  border-color: #d9ecff;
  transition: all 0.3s;
}

.quick-btn:hover {
  background-color: #409eff;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
}
/* 购物车列表 */
.cart-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.cart-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 12px;
  min-height: 150px;
}

.empty-cart {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 200px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(to right, #f8f9fa, #ffffff);
  border-radius: 8px;
  gap: 16px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
  margin-bottom: 8px;
}

.cart-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transform: translateY(-1px);
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #303133;
}

.item-price {
  color: #909399;
  font-size: 13px;
}

.item-quantity {
  width: 120px;
}

.item-subtotal {
  width: 90px;
  text-align: right;
  font-weight: 700;
  color: #f56c6c;
  font-size: 15px;
}

.item-actions {
  width: 60px;
}

.product-search {
  flex-shrink: 0;
  margin-bottom: 12px;
}

.product-grid-section {
  flex-shrink: 0;
  border-top: 2px solid #e4e7ed;
  padding-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-subtitle {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
  font-weight: 500;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(145px, 1fr));
  gap: 10px;
  max-height: 480px;
  overflow-y: auto;
  padding: 8px;
}

.product-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 10px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 6px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.product-image {
  width: 100%;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #ebeef5;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 4px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.product-spec {
  font-size: 13px;
  color: #909399;
  line-height: 1.3;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 6px;
  padding-top: 8px;
  border-top: 1px dashed #e4e7ed;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}

.product-stock {
  font-size: 12px;
  color: #67c23a;
  background-color: #f0f9ff;
  padding: 3px 8px;
  border-radius: 6px;
  font-weight: 500;
}

.add-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.3s;
}

.product-card:hover .add-badge {
  opacity: 1;
}

.product-grid::-webkit-scrollbar {
  width: 8px;
}

.product-grid::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.product-grid::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.product-grid::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
.product-pagination {
  display: flex;
  justify-content: center;
  padding: 8px 0;
  border-top: 1px solid #e4e7ed;
}
/* 结算面板 */
.checkout-section {
  display: flex;
  flex-direction: column;
}

.checkout-info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 15px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  opacity: 0.9;
}

.info-value {
  font-weight: 600;
  font-size: 16px;
}

.info-value.highlight {
  font-size: 24px;
  color: #ffd700;
  font-weight: 700;
}

.info-value.discount {
  color: #ffd700;
}

.info-value.change {
  color: #67c23a;
  font-size: 18px;
}

.total-row {
  border-top: 1px solid rgba(255, 255, 255, 0.3);
  padding-top: 10px;
  margin-top: 10px;
}

/* 支付方式 */
.payment-methods {
  margin-bottom: 16px;
}

.payment-title {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
}

.payment-group {
  display: flex;
  flex-direction: row;
  gap: 8px;
  width: 100%;
}

.payment-btn {
  flex: 1;
  height: 45px;
  font-size: 15px;
}

/* 会员区域 */
.member-section {
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.member-title {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 10px;
}

.member-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.member-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.member-name {
  font-weight: 600;
  color: #409eff;
  font-size: 16px;
}

.member-details {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
}

.points-input {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}

.points-hint {
  font-size: 13px;
  color: #f56c6c;
  font-weight: 600;
}

/* 结算按钮 */
.checkout-actions {
  margin-top: auto;
}

.checkout-btn {
  width: 100%;
  height: 55px;
  font-size: 20px;
  font-weight: 700;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.checkout-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.checkout-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 结算成功提示 */
.checkout-result {
  margin-top: 12px;
}

.success-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

/* 滚动条样式 */
.cart-list::-webkit-scrollbar {
  width: 8px;
}

.cart-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.cart-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.cart-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
