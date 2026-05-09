<template>
  <div class="restock-suggestion">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>📦 智能补货建议</span>
          <el-select v-model="restockDays" size="small" style="width: 150px">
            <el-option label="覆盖3天" :value="3" />
            <el-option label="覆盖7天" :value="7" />
            <el-option label="覆盖15天" :value="15" />
            <el-option label="覆盖30天" :value="30" />
          </el-select>
        </div>
      </template>

      <el-table :data="suggestions" stripe border v-loading="loading">
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="barcode" label="条码" width="120" />
        <el-table-column prop="spec" label="规格" width="100" />
        <el-table-column label="当前库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStockTagType(row)">
              {{ row.currentStock }} {{ row.unit }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预警阈值" width="100" align="center">
          <template #default="{ row }">
            {{ row.lowStockThreshold || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="日均销量" width="100" align="center">
          <template #default="{ row }">
            {{ row.avgDailySales }}
          </template>
        </el-table-column>
        <el-table-column label="建议补货量" width="120" align="center">
          <template #default="{ row }">
            <el-button
                type="success"
                size="small"
                @click="handleRestock(row)"
            >
              补货 {{ row.suggestedQuantity }} {{ row.unit }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgencyLevel)">
              {{ getUrgencyText(row.urgencyLevel) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && suggestions.length === 0" description="暂无补货建议" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getRestockSuggestions } from '@/api/inventory'

const loading = ref(false)
const suggestions = ref([])
const restockDays = ref(7)
const router = useRouter()

const fetchSuggestions = async () => {
  loading.value = true
  try {
    const res = await getRestockSuggestions(restockDays.value)
    if (res.code === 200) {
      suggestions.value = res.data || []
    } else {
      ElMessage.error(res.message || '获取补货建议失败')
    }
  } catch (error) {
    console.error('获取补货建议失败:', error)
    ElMessage.error('获取补货建议失败')
  } finally {
    loading.value = false
  }
}

const getStockTagType = (row) => {
  if (row.currentStock <= row.lowStockThreshold) return 'danger'
  if (row.currentStock <= row.lowStockThreshold * 1.5) return 'warning'
  return 'success'
}

const getUrgencyType = (level) => {
  const types = { URGENT: 'danger', WARNING: 'warning', NORMAL: 'info' }
  return types[level] || 'info'
}

const getUrgencyText = (level) => {
  const texts = { URGENT: '紧急', WARNING: '警告', NORMAL: '正常' }
  return texts[level] || '正常'
}
const handleRestock = (row) => {
  router.push({
    path: '/product/list',
    query: {
      keyword: row.productName,
      restockId: row.productId,
      quantity: row.suggestedQuantity
    }
  })
}
watch(restockDays, () => fetchSuggestions())

onMounted(() => fetchSuggestions())
</script>

<style scoped>
.restock-suggestion { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
