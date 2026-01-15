<template>
  <div class="product-list-container">
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
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px" @change="handleQuery">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏与表格 -->
    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增商品</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="productList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="barcode" label="条码" width="140" />
        <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" show-overflow-tooltip />
        <el-table-column prop="spec" label="规格" width="100" />
        <el-table-column prop="price" label="销售价" width="100" align="right">
          <template #default="scope">
            ¥ {{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.stock < scope.row.lowStockThreshold ? 'danger' : 'success'">
              {{ scope.row.stock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品条码" prop="barcode">
          <el-input v-model="form.barcode" placeholder="请输入条码" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
           <el-cascader
            v-model="form.categoryId"
            :options="categoryOptions"
            :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children', emitPath: false }"
            placeholder="请选择分类"
            style="width: 100%"
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="规格" prop="spec">
              <el-input v-model="form.spec" placeholder="如: 500ml" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="如: 瓶" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
             <el-form-item label="进货价" prop="costPrice">
              <el-input-number v-model="form.costPrice" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售价" prop="price">
              <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
             <el-form-item label="初始库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" :disabled="dialog.type === 'edit'" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存预警" prop="lowStockThreshold">
              <el-input-number v-model="form.lowStockThreshold" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
         <el-row>
          <el-col :span="12">
            <el-form-item label="保质期(天)" prop="shelfLifeDays">
              <el-input-number v-model="form.shelfLifeDays" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
             <!-- 如果有生产日期字段，可以使用 el-date-picker -->
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
/**
 * 商品列表页面
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProductPage,
  addProduct,
  updateProduct,
  updateProductStatus,
  deleteProduct,
  getCategoryTree
} from '@/api/product'

/**
 * 状态定义
 */
const loading = ref(false)
const total = ref(0)
const productList = ref([])
const categoryOptions = ref([]) // 分类树数据

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  categoryId: null,
  status: null
})

// 对话框配置
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

// 表单数据
const form = reactive({
  id: null,
  name: '',
  barcode: '',
  categoryId: null,
  spec: '',
  unit: '',
  price: 0,
  costPrice: 0,
  stock: 0,
  lowStockThreshold: 10,
  status: 1,
  remark: '',
  shelfLifeDays: 0
})

const formRef = ref(null)

// 校验规则
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  barcode: [{ required: true, message: '请输入商品条码', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入销售价', trigger: 'blur' }]
}

/**
 * 生命周期
 */
onMounted(() => {
  loadCategoryTree()
  getList()
})

/**
 * 方法定义
 */

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const res = await getCategoryTree()
    categoryOptions.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    // 处理级联选择器返回数组的情况，只取最后一个值
    const params = { ...queryParams }
    if (Array.isArray(params.categoryId)) {
      params.categoryId = params.categoryId[params.categoryId.length - 1]
    }

    const res = await getProductPage(params)
    productList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.name = ''
  queryParams.categoryId = null
  queryParams.status = null
  queryParams.pageNum = 1
  getList()
}

// 分页
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 添加
const handleAdd = () => {
  dialog.title = '新增商品'
  dialog.type = 'add'
  dialog.visible = true
  resetFormState()
}

// 编辑
const handleEdit = (row) => {
  dialog.title = '编辑商品'
  dialog.type = 'edit'
  dialog.visible = true
  Object.assign(form, row)
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除商品 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    getList()
  })
  .catch(() => {})
}

// 状态更改
const handleStatusChange = async (row) => {
  const text = row.status === 1 ? '上架' : '下架'
  try {
    await updateProductStatus(row.id, row.status)
    ElMessage.success(`${text}成功`)
  } catch (error) {
    // 失败则回滚状态
    row.status = row.status === 1 ? 0 : 1
  }
}

// 提交
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialog.type === 'add') {
          await addProduct(form)
          ElMessage.success('新增成功')
        } else {
          await updateProduct(form)
          ElMessage.success('修改成功')
        }
        dialog.visible = false
        getList()
      } catch (error) {
        console.error(error)
      }
    }
  })
}

// 重置表单
const resetFormState = () => {
  form.id = null
  form.name = ''
  form.barcode = ''
  form.categoryId = null
  form.spec = ''
  form.unit = ''
  form.price = 0
  form.costPrice = 0
  form.stock = 0
  form.lowStockThreshold = 10
  form.status = 1
  form.remark = ''
  form.shelfLifeDays = 0
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
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

