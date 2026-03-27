<template>
  <div class="sale-order-list-container">
    <el-row :gutter="20" class="summary-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">订单总数</div>
          <div class="stat-value">{{ total }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">当前页销售额</div>
          <div class="stat-value">¥ {{ formatMoney(currentPageAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">会员订单数</div>
          <div class="stat-value">{{ memberOrderCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-title">当前页发放积分</div>
          <div class="stat-value">{{ currentPagePoints }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="会员姓名">
          <el-input v-model="queryParams.memberName" placeholder="请输入会员姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryParams.paymentType" placeholder="全部方式" clearable style="width: 120px">
            <el-option label="现金" :value="1" />
            <el-option label="微信" :value="2" />
            <el-option label="支付宝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="销售时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="x"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="warning" icon="Download" @click="handleExport">导出销售流水Excel</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-header">
        <span>销售记录</span>
      </div>
      <el-table :data="saleList" v-loading="loading" border>
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="totalAmount" label="应收金额" width="110" align="right">
          <template #default="scope">¥ {{ formatMoney(scope.row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="realAmount" label="实收金额" width="110" align="right">
          <template #default="scope">¥ {{ formatMoney(scope.row.realAmount) }}</template>
        </el-table-column>
        <el-table-column prop="paymentType" label="支付方式" width="100" align="center">
          <template #default="scope">{{ paymentTypeText(scope.row.paymentType) }}</template>
        </el-table-column>
        <el-table-column prop="memberName" label="会员姓名" width="120">
          <template #default="scope">{{ scope.row.memberName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="memberPhone" label="会员手机号" width="130">
          <template #default="scope">{{ scope.row.memberPhone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="pointEarned" label="本单积分" width="100" align="center">
          <template #default="scope">{{ scope.row.pointEarned ?? 0 }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="销售时间" width="180" align="center">
          <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :background="true"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchSaleList"
          @current-change="fetchSaleList"
        />
      </div>
    </el-card>

    <el-dialog title="订单详情" v-model="detailDialog.visible" width="860px">
      <el-descriptions :column="3" border class="detail-section">
        <el-descriptions-item label="订单号">{{ detail.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ paymentTypeText(detail.paymentType) }}</el-descriptions-item>
        <el-descriptions-item label="销售时间">{{ formatDateTime(detail.createTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="应收金额">¥ {{ formatMoney(detail.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="实收金额">¥ {{ formatMoney(detail.realAmount) }}</el-descriptions-item>
        <el-descriptions-item label="本单积分">{{ detail.pointEarned ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="会员姓名">{{ detail.memberName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会员手机号">{{ detail.memberPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="订单备注">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-table :data="detail.items || []" border class="detail-section">
        <el-table-column prop="productName" label="商品名称" min-width="180" />
        <el-table-column prop="price" label="单价" width="100" align="right">
          <template #default="scope">¥ {{ formatMoney(scope.row.price) }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="90" align="center" />
        <el-table-column prop="amount" label="小计" width="110" align="right">
          <template #default="scope">¥ {{ formatMoney(scope.row.amount) }}</template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button type="primary" @click="detailDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { getSaleDetail, getSalePage, exportSale } from '@/api/sale'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const total = ref(0)
const saleList = ref([])
const detail = ref({})
const dateRange = ref([])
const detailDialog = reactive({
  visible: false
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  memberName: '',
  paymentType: undefined,
  startTime: undefined,
  endTime: undefined
})

const currentPageAmount = computed(() => saleList.value.reduce((sum, item) => sum + Number(item.realAmount || 0), 0))
const memberOrderCount = computed(() => saleList.value.filter(item => item.memberId || item.memberName).length)
const currentPagePoints = computed(() => saleList.value.reduce((sum, item) => sum + Number(item.pointEarned || 0), 0))

onMounted(() => {
  fetchSaleList()
})

const fetchSaleList = async () => {
  loading.value = true
  try {
    queryParams.startTime = dateRange.value?.[0] ? Number(dateRange.value[0]) : undefined
    queryParams.endTime = dateRange.value?.[1] ? Number(dateRange.value[1]) : undefined
    const res = await getSalePage(queryParams)
    saleList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取销售记录失败', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  if (dateRange.value && dateRange.value.length === 2) {
    queryParams.startTime = dateRange.value[0]
    queryParams.endTime = dateRange.value[1]
  } else {
    queryParams.startTime = undefined
    queryParams.endTime = undefined
  }
  fetchSaleList()
}

const handleExport = async () => {
  try {
    const params = { ...queryParams }
    // 移除分页参数
    delete params.pageNum
    delete params.pageSize

    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }

    const res = await exportSale(params)
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = `销售流水_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
    console.error(error)
  }
}

const resetQuery = () => {
  queryParams.orderNo = ''
  queryParams.memberName = ''
  queryParams.paymentType = undefined
  queryParams.startTime = undefined
  queryParams.endTime = undefined
  queryParams.pageNum = 1
  dateRange.value = []
  fetchSaleList()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getSaleDetail(row.id)
    detail.value = res.data || {}
    detailDialog.visible = true
  } catch (error) {
    console.error('获取订单详情失败', error)
  }
}

const formatMoney = (value) => Number(value || 0).toFixed(2)
const formatDateTime = (timeStr) => timeStr ? String(timeStr).replace('T', ' ') : ''

const paymentTypeText = (paymentType) => {
  if (paymentType === 1) return '现金'
  if (paymentType === 2) return '微信'
  if (paymentType === 3) return '支付宝'
  return '-'
}
</script>

<style scoped>
.summary-row {
  margin-bottom: 20px;
}

.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.stat-value {
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.search-card {
  margin-bottom: 20px;
}

.table-header {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-section {
  margin-bottom: 20px;
}
</style>
