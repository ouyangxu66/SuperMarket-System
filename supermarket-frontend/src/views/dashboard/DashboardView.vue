<template>
  <div class="dashboard-page" v-loading="loading">
    <div class="dashboard-top">
      <div>
        <div class="dashboard-title">经营数据看板</div>
        <div class="dashboard-subtitle">聚焦销售、会员与库存关键指标，适配后台首页驾驶舱展示。</div>
      </div>
      <div class="dashboard-actions">
        <el-radio-group v-model="query.rangeType" size="default" @change="fetchOverview">
          <el-radio-button label="7d">近7天</el-radio-button>
          <el-radio-button label="15d">近15天</el-radio-button>
          <el-radio-button label="30d">近30天</el-radio-button>
        </el-radio-group>
        <el-button @click="fetchOverview">刷新数据</el-button>
        <el-button type="primary" @click="handleExport">导出销售概览Excel</el-button>
      </div>
    </div>

    <el-row :gutter="20" class="section-gap">
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card shadow="hover" class="summary-card sales-card">
          <template #header>
            <div class="card-header"><span>销售概览</span><span>{{ formatDateTime(overview.meta.generatedAt) }}</span></div>
          </template>
          <div class="metric-grid">
            <div class="metric-item">
              <div class="metric-label">今日销售额</div>
              <div class="metric-value">{{ formatCurrency(overview.sales.summary.todaySalesAmount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">今日订单数</div>
              <div class="metric-value">{{ formatNumber(overview.sales.summary.todayOrderCount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">今日客单价</div>
              <div class="metric-value">{{ formatCurrency(overview.sales.summary.todayAvgOrderAmount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">较昨日环比</div>
              <div :class="['metric-value', growthClass]">{{ formatRate(overview.sales.summary.salesGrowthRate) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :lg="8">
        <el-card shadow="hover" class="summary-card member-card">
          <template #header>
            <div class="card-header"><span>会员概览</span><span>支持真实聚合</span></div>
          </template>
          <div class="metric-grid">
            <div class="metric-item">
              <div class="metric-label">会员总数</div>
              <div class="metric-value">{{ formatNumber(overview.members.summary.memberTotal) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">今日新增</div>
              <div class="metric-value">{{ formatNumber(overview.members.summary.todayNewMemberCount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">本周新增</div>
              <div class="metric-value">{{ formatNumber(overview.members.summary.weekNewMemberCount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">储值总额</div>
              <div class="metric-value">{{ formatCurrency(overview.members.summary.totalBalance) }}</div>
            </div>
          </div>
          <div class="member-extra">
            <span>总积分 {{ formatNumber(overview.members.summary.totalPoints) }}</span>
            <span>近30天活跃 {{ formatNumber(overview.members.summary.activeMemberCount) }}</span>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :lg="8">
        <el-card shadow="hover" class="summary-card inventory-card">
          <template #header>
            <div class="card-header"><span>库存概览</span><span>预警优先</span></div>
          </template>
          <div class="metric-grid">
            <div class="metric-item">
              <div class="metric-label">商品总数</div>
              <div class="metric-value">{{ formatNumber(overview.inventory.summary.productTotal) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">库存总量</div>
              <div class="metric-value">{{ formatNumber(overview.inventory.summary.stockTotalQuantity) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">库存预警</div>
              <div class="metric-value warning-text">{{ formatNumber(overview.inventory.summary.lowStockProductCount) }}</div>
            </div>
            <div class="metric-item">
              <div class="metric-label">临期 / 过期</div>
              <div class="metric-value">{{ formatNumber(overview.inventory.summary.nearExpiryProductCount) }} / {{ formatNumber(overview.inventory.summary.expiredProductCount) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="section-gap">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header"><span>销售趋势</span><span>{{ rangeLabel }}</span></div>
          </template>
          <div ref="salesChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <div class="card-header"><span>热销商品排行</span><span>Top {{ hotProducts.length }}</span></div>
          </template>
          <div v-if="hotProducts.length" class="rank-list">
            <div v-for="(item, index) in hotProducts" :key="item.productId || index" class="rank-item">
              <div class="rank-index">{{ index + 1 }}</div>
              <div class="rank-main">
                <div class="rank-name">{{ item.productName }}</div>
                <div class="rank-meta">销量 {{ formatNumber(item.salesQuantity) }} · 销售额 {{ formatCurrency(item.salesAmount) }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无销售排行数据" :image-size="100" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header"><span>库存预警商品</span><span>Top {{ lowStockList.length }}</span></div>
          </template>
          <el-table :data="lowStockList" stripe>
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="productName" label="商品" min-width="140" />
            <el-table-column prop="stock" label="当前库存" width="100" />
            <el-table-column prop="lowStockThreshold" label="预警阈值" width="100" />
            <el-table-column prop="gap" label="缺口" width="80" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header"><span>临期商品提醒</span><span>{{ overview.meta.nearExpiryDays || 7 }} 天内</span></div>
          </template>
          <el-table :data="nearExpiryList" stripe>
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="productName" label="商品" min-width="140" />
            <el-table-column prop="stock" label="库存" width="90" />
            <el-table-column label="到期日期" min-width="120">
              <template #default="scope">{{ formatDate(scope.row.earliestExpirationDate) }}</template>
            </el-table-column>
            <el-table-column prop="remainingDays" label="剩余天数" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="section-gap bottom-gap">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="status-card">
          <template #header>
            <div class="card-header"><span>支付方式分布</span><span>{{ paymentDistribution.length }} 种</span></div>
          </template>
          <div class="payment-list" v-if="paymentDistribution.length">
            <div class="payment-item" v-for="item in paymentDistribution" :key="item.paymentType">
              <div>
                <div class="payment-name">{{ item.paymentTypeName }}</div>
                <div class="payment-meta">订单 {{ formatNumber(item.orderCount) }}</div>
              </div>
              <div class="payment-amount">{{ formatCurrency(item.amount) }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无支付方式数据" :image-size="90" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="status-card">
          <template #header>
            <div class="card-header"><span>会员扩展统计</span><span>{{ overview.members.levelDistribution.ready ? '已开放' : '预留中' }}</span></div>
          </template>
          <div class="placeholder-panel">
            <el-tag type="warning" effect="dark">待扩展</el-tag>
            <p>{{ overview.members.levelDistribution.pendingReason || '会员等级与精细化画像能力将在后续迭代中接入。' }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { getDashboardOverview, exportDashboardSales } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const salesChartRef = ref(null)
let salesChart = null

const query = reactive({
  rangeType: '7d',
  topN: 10,
  nearExpiryDays: 7
})

const createDefaultOverview = () => ({
  sales: { summary: {}, trend: [], hotProducts: [], paymentDistribution: [] },
  members: { summary: {}, newMemberTrend: [], levelDistribution: { ready: false, pendingReason: '' } },
  inventory: { summary: {}, lowStockList: [], nearExpiryList: [] },
  meta: { generatedAt: null, nearExpiryDays: 7 }
})

const overview = reactive(createDefaultOverview())

const hotProducts = computed(() => overview.sales.hotProducts || [])
const lowStockList = computed(() => overview.inventory.lowStockList || [])
const nearExpiryList = computed(() => overview.inventory.nearExpiryList || [])
const paymentDistribution = computed(() => overview.sales.paymentDistribution || [])
const rangeLabel = computed(() => {
  const map = { '7d': '近7天', '15d': '近15天', '30d': '近30天' }
  return map[query.rangeType] || '近7天'
})
const growthClass = computed(() => Number(overview.sales.summary.salesGrowthRate || 0) >= 0 ? 'up' : 'down')

const patchOverview = (payload = createDefaultOverview()) => {
  overview.sales = payload.sales || createDefaultOverview().sales
  overview.members = payload.members || createDefaultOverview().members
  overview.inventory = payload.inventory || createDefaultOverview().inventory
  overview.meta = payload.meta || createDefaultOverview().meta
}

const fetchOverview = async () => {
  loading.value = true
  try {
    const res = await getDashboardOverview({
      rangeType: query.rangeType,
      topN: 10,
      nearExpiryDays: 7
    })
    overview.sales = res.data.sales
    overview.members = res.data.members
    overview.inventory = res.data.inventory
    overview.meta = res.data.meta
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleExport = async () => {
  try {
    const res = await exportDashboardSales({
      rangeType: query.rangeType
    })
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = `销售概览_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(link.href)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
    console.error(error)
  }
}

const renderSalesChart = () => {
  if (!salesChartRef.value) return
  if (!salesChart) {
    salesChart = echarts.init(salesChartRef.value)
  }
  const trend = overview.sales.trend || []
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单数'] },
    grid: { left: 24, right: 24, top: 40, bottom: 24, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trend.map(item => item.date?.slice(5) || '')
    },
    yAxis: [
      { type: 'value', name: '销售额' },
      { type: 'value', name: '订单数' }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        data: trend.map(item => Number(item.salesAmount || 0))
      },
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: trend.map(item => Number(item.orderCount || 0))
      }
    ]
  })
}

const handleResize = () => {
  salesChart?.resize()
}

onMounted(() => {
  fetchOverview()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  salesChart = null
})

watch(() => overview.sales.trend, () => nextTick(() => renderSalesChart()), { deep: true })

const currencyFormatter = new Intl.NumberFormat('zh-CN', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
})
const numberFormatter = new Intl.NumberFormat('zh-CN')

const formatCurrency = (value) => `￥${currencyFormatter.format(Number(value || 0))}`
const formatNumber = (value) => numberFormatter.format(Number(value || 0))
const formatRate = (value) => `${Number(value || 0) >= 0 ? '+' : ''}${Number(value || 0).toFixed(2)}%`
const formatDate = (value) => value ? String(value).slice(0, 10) : '--'
const formatDateTime = (value) => value ? String(value).replace('T', ' ').slice(0, 16) : '刚刚更新'
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dashboard-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 22px 24px;
  border-radius: 18px;
  background: linear-gradient(135deg, #0f4cdb 0%, #3f8cff 55%, #85b5ff 100%);
  color: #fff;
  box-shadow: 0 16px 40px rgba(26, 115, 232, 0.22);
}

.dashboard-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}

.dashboard-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.dashboard-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.section-gap {
  margin-top: 0;
}

.summary-card,
.chart-card,
.table-card,
.status-card {
  border: none;
  border-radius: 18px;
}

.sales-card,
.member-card,
.inventory-card {
  min-height: 250px;
}

:deep(.el-card__body) {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-weight: 600;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.metric-item {
  padding: 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.62);
}

.metric-label {
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 10px;
}

.metric-value {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
}

.sales-card {
  background: linear-gradient(180deg, #eef5ff 0%, #ffffff 100%);
}

.member-card {
  background: linear-gradient(180deg, #eefcf6 0%, #ffffff 100%);
}

.inventory-card {
  background: linear-gradient(180deg, #fff7eb 0%, #ffffff 100%);
}

.member-extra {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  color: #4b5563;
  font-size: 13px;
}

.warning-text {
  color: #d97706;
}

.up {
  color: #16a34a;
}

.down {
  color: #dc2626;
}

.chart-box {
  height: 360px;
}

.rank-list,
.payment-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rank-item,
.payment-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8fafc;
}

.rank-index {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1d4ed8;
  color: #fff;
  font-weight: 700;
}

.rank-main {
  flex: 1;
}

.rank-name,
.payment-name {
  font-weight: 600;
  color: #111827;
}

.rank-meta,
.payment-meta {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.payment-amount {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.placeholder-panel {
  min-height: 180px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 12px;
  color: #4b5563;
}

.bottom-gap {
  margin-bottom: 8px;
}

@media (max-width: 992px) {
  .dashboard-top {
    flex-direction: column;
    align-items: flex-start;
  }

  .metric-grid {
    grid-template-columns: 1fr;
  }
}
</style>
