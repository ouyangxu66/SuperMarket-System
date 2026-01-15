<template>
  <div class="inventory-count-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="queryParams.title" placeholder="请输入盘点任务名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
           <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 150px">
              <el-option label="未开始" value="DRAFT" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" />
           </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button type="primary" icon="Plus" @click="handleAdd">新建盘点</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="countList" style="width: 100%" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="countNumber" label="盘点编号" width="160" />
        <el-table-column prop="title" label="盘点名称" min-width="150" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
             <el-tag v-if="scope.row.status === 'DRAFT'" type="info">未开始</el-tag>
             <el-tag v-else-if="scope.row.status === 'IN_PROGRESS'" type="primary">进行中</el-tag>
             <el-tag v-else-if="scope.row.status === 'COMPLETED'" type="success">已完成</el-tag>
             <el-tag v-else>{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="经办人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160">
           <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'DRAFT'"
              type="success"
              link
              @click="handleStart(scope.row)"
            >开始</el-button>
            <el-button
              v-if="scope.row.status === 'IN_PROGRESS'"
              type="warning"
              link
              @click="handleComplete(scope.row)"
            >完成</el-button>
            <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

       <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

     <!-- 新增盘点对话框 -->
    <el-dialog
      title="新建盘点任务"
      v-model="dialog.visible"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="盘点名称" prop="title">
          <el-input v-model="form.title" placeholder="如：2026年第一季度全盘" />
        </el-form-item>
        <el-form-item label="盘点描述" prop="description">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="经办人" prop="operatorName">
          <el-input v-model="form.operatorName" placeholder="默认当前用户" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
           <el-input v-model="form.remark" />
        </el-form-item>
      </el-form>
       <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router' // Added import
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getInventoryCountPage,
  addInventoryCount,
  startInventoryCount,
  completeInventoryCount
} from '@/api/inventory'

const router = useRouter() // Added router instance
const loading = ref(false)
const countList = ref([])
const total = ref(0)
const dialog = reactive({
  visible: false
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  status: ''
})

const form = reactive({
  title: '',
  description: '',
  operatorName: '', // 实际应从当前登录用户取
  remark: ''
})

const formRef = ref(null)

const rules = {
  title: [{ required: true, message: '请输入盘点名称', trigger: 'blur' }]
}

onMounted(() => {
  getList()
})

const formatDateTime = (timeStr) => {
    return timeStr ? timeStr.replace('T', ' ') : ''
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getInventoryCountPage(queryParams)
    countList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
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

const handleAdd = () => {
    dialog.visible = true
}

const submitForm = async () => {
    if(!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if(valid) {
            try {
                // 模拟 operatorId
                const data = { ...form, operatorId: 1 }
                await addInventoryCount(data)
                ElMessage.success('创建成功')
                dialog.visible = false
                getList()
            } catch(e) { console.error(e) }
        }
    })
}

const resetForm = () => {
    if(formRef.value) formRef.value.resetFields()
}

const handleStart = async (row) => {
    try {
        await startInventoryCount(row.id)
        ElMessage.success('盘点任务已开始')
        getList()
    } catch(e) { console.error(e) }
}

const handleComplete = (row) => {
     ElMessageBox.confirm('确认结束本次盘点吗？结束将无法再修改数据。', '提示', {
         type: 'warning'
     }).then(async () => {
         try {
             await completeInventoryCount(row.id)
             ElMessage.success('盘点已完成')
             getList()
         } catch(e) { console.error(e) }
     })
}

const handleDetail = (row) => {
    router.push({
        name: 'inventory-count-detail',
        params: { id: row.id },
        query: { title: row.title, status: row.status }
    })
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
</style>
