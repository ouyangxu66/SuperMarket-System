<template>
  <div class="inventory-list-container">
    <el-tabs v-model="activeTab" class="inventory-tabs" type="card" @tab-change="handleTabChange">
      <el-tab-pane label="库存列表" name="all">
        <!-- 搜索栏 -->
        <el-card class="search-card">
          <el-form :inline="true" :model="queryParams" class="search-form">
            <el-form-item label="商品名称">
              <el-input v-model="queryParams.name" placeholder="请输入商品名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="商品分类">
               <el-cascader
                v-model="queryParams.categoryId"
                :options="categoryOptions"
                :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children' }"
                placeholder="请选择分类"
                clearable
                @change="handleQuery"
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="table-card" shadow="never">
          <el-table
            v-loading="loading"
            :data="inventoryList"
            style="width: 100%"
            border
          >
            <el-table-column prop="id" label="ID" width="80" align="center" />
            <el-table-column prop="barcode" label="条码" width="140" />
            <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="spec" label="规格" width="100" />
            <el-table-column prop="stock" label="当前库存" width="120" align="center" sortable>
              <template #default="scope">
                 <el-tag :type="scope.row.stock < scope.row.lowStockThreshold ? 'danger' : 'success'">
                  {{ scope.row.stock }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lowStockThreshold" label="预警阈值" width="120" align="center" />
            <el-table-column label="库存详情" width="120" align="center">
               <template #default="scope">
                 <el-popover placement="top" width="300" trigger="hover">
                    <template #reference>
                      <el-button link type="primary">查看详情</el-button>
                    </template>
                    <div class="inventory-detail">
                      <p><strong>商品名称:</strong> {{ scope.row.name }}</p>
                      <p><strong>进货价:</strong> ¥{{ scope.row.costPrice }}</p>
                      <p><strong>最新生产日期:</strong> {{ scope.row.latestProductionDate || '未知' }}</p>
                      <p><strong>最早过期日期:</strong> {{ scope.row.earliestExpirationDate || '未知' }}</p>
                      <p><strong>保质期:</strong> {{ scope.row.shelfLifeDays }} 天</p>
                    </div>
                 </el-popover>
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
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="库存预警" name="low_stock">
        <template #label>
          <span>库存预警 <el-tag v-if="lowStockList.length" type="danger" size="small" effect="dark" round>{{ lowStockList.length }}</el-tag></span>
        </template>
        <el-card shadow="never">
          <el-table :data="lowStockList" border stripe>
             <el-table-column prop="barcode" label="条码" width="140" />
             <el-table-column prop="name" label="商品名称" min-width="150" />
             <el-table-column prop="spec" label="规格" width="100" />
             <el-table-column prop="stock" label="当前库存" width="120" align="center">
                <template #default="scope">
                   <span style="color: red; font-weight: bold;">{{ scope.row.stock }}</span>
                </template>
             </el-table-column>
             <el-table-column prop="lowStockThreshold" label="预警阈值" width="120" align="center" />
             <el-table-column label="操作" width="120" align="center">
               <el-button type="primary" link @click="ElMessage.info('请前往采购系统补货')">补货</el-button>
             </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="临期商品" name="expiring">
         <template #label>
          <span>临期商品 <el-tag v-if="expiringList.length" type="warning" size="small" effect="dark" round>{{ expiringList.length }}</el-tag></span>
        </template>
         <el-card shadow="never">
           <div style="margin-bottom: 15px;">
              <span>临期天数设置：</span>
              <el-input-number v-model="expiringDays" :min="1" :max="365" size="small" @change="loadExpiring" />
              <span style="margin-left: 10px; font-size: 12px; color: #666;">(查询 {{ expiringDays }} 天内过期的商品)</span>
           </div>
           <el-table :data="expiringList" border stripe>
             <el-table-column prop="barcode" label="条码" width="140" />
             <el-table-column prop="name" label="商品名称" min-width="150" />
             <el-table-column prop="stock" label="库存" width="100" align="center" />
             <el-table-column prop="earliestExpirationDate" label="过期日期" width="150" align="center" sortable>
                <template #default="scope">
                   <span style="color: #e6a23c; font-weight: bold;">{{ scope.row.earliestExpirationDate }}</span>
                </template>
             </el-table-column>
             <el-table-column label="剩余天数" width="120" align="center">
                <template #default="scope">
                  {{ calculateDaysLeft(scope.row.earliestExpirationDate) }} 天
                </template>
             </el-table-column>
          </el-table>
         </el-card>
      </el-tab-pane>

      <el-tab-pane label="过期商品" name="expired">
         <template #label>
          <span>过期商品 <el-tag v-if="expiredList.length" type="danger" size="small" effect="dark" round>{{ expiredList.length }}</el-tag></span>
        </template>
        <el-card shadow="never">
          <el-alert title="以下商品已过期，请及时下架处理" type="error" :closable="false" style="margin-bottom: 15px;" />
          <el-table :data="expiredList" border stripe>
             <el-table-column prop="barcode" label="条码" width="140" />
             <el-table-column prop="name" label="商品名称" min-width="150" />
             <el-table-column prop="stock" label="库存" width="100" align="center" />
             <el-table-column prop="earliestExpirationDate" label="过期日期" width="150" align="center">
                 <template #default="scope">
                   <span style="color: red;">{{ scope.row.earliestExpirationDate }}</span>
                </template>
             </el-table-column>
              <el-table-column label="操作" width="120" align="center">
               <el-button type="danger" link @click="ElMessage.warning('请进行销毁处理')">销毁登记</el-button>
             </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {  ElMessage } from 'element-plus'
import { getProductPage, getCategoryTree, getExpiringProducts, getExpiredProducts } from '@/api/product'
import { getLowStockList } from '@/api/inventory'

const activeTab = ref('all')
const loading = ref(false)
const inventoryList = ref([])
const lowStockList = ref([])
const expiringList = ref([])
const expiredList = ref([])
const categoryOptions = ref([])
const total = ref(0)
const expiringDays = ref(30) // 默认查询 30 天临期

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  categoryId: null,
  status: 1 // 只查上架商品
})

onMounted(() => {
  loadCategoryTree()
  // 初始全查，以便显示 Badge
  getList()
  getLowStock()
  loadExpiring()
  loadExpired()
})

const handleTabChange = (tabName) => {
  if (tabName === 'all') getList()
  else if (tabName === 'low_stock') getLowStock()
  else if (tabName === 'expiring') loadExpiring()
  else if (tabName === 'expired') loadExpired()
}

const loadCategoryTree = async () => {
  try {
    const res = await getCategoryTree()
    categoryOptions.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (Array.isArray(params.categoryId)) {
      params.categoryId = params.categoryId[params.categoryId.length - 1]
    }
    const res = await getProductPage(params)
    inventoryList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getLowStock = async () => {
  try {
    const res = await getLowStockList()
    lowStockList.value = res.data
  } catch (error) {
    console.log(error)
  }
}

const loadExpiring = async () => {
  try {
    const res = await getExpiringProducts(expiringDays.value)
    expiringList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadExpired = async () => {
  try {
    const res = await getExpiredProducts()
    expiredList.value = res.data
  } catch(error) {
    console.error(error)
  }
}

const calculateDaysLeft = (dateStr) => {
  if (!dateStr) return '-'
  const today = new Date()
  const target = new Date(dateStr)
  const diffTime = target - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 ? diffDays : 0
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.name = ''
  queryParams.categoryId = null
  queryParams.pageNum = 1
  getList()
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.inventory-detail p {
  margin: 5px 0;
  font-size: 14px;
}
.inventory-tabs > .el-tabs__content {
  padding: 15px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}
</style>

