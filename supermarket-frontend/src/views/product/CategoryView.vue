<template>
  <div class="category-list-container">
    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增分类</el-button>
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="categoryList"
        style="width: 100%"
        row-key="id"
        border
        default-expand-all
      >
        <el-table-column prop="name" label="分类名称" sortable />
        <el-table-column prop="sort" label="排序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="danger">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link icon="Plus" @click="handleAddSub(scope.row)">添加子类</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="上级分类">
          <el-tree-select
            v-model="form.parentId"
            :data="categoryOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="请选择上级分类 (不选则为顶级)"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { getCategoryTree } from '@/api/product'

// API functions specific to Category (not in api/product.js yet, so defining inside or using request directly)
// 建议后续移入 src/api/product.js 或 src/api/category.js
// 这里为了快速，直接定义
const addCategory = (data) => request({ url: '/product/category', method: 'post', data })
const updateCategory = (data) => request({ url: '/product/category', method: 'put', data })
const deleteCategory = (id) => request({ url: `/product/category/${id}`, method: 'delete' })

/**
 * 状态定义
 */
const loading = ref(false)
const categoryList = ref([])
// 用于上级分类选择树，需要包含一个虚拟的根节点或者直接用 categoryList
const categoryOptions = ref([])

const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  sort: 0,
  status: 1
})

const formRef = ref(null)

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

/**
 * 生命周期
 */
onMounted(() => {
  getList()
})

/**
 * 方法
 */
const getList = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    categoryList.value = res.data
    // 构建选项树，可以手动添加一个 "顶级分类" 节点，或者直接使用 res.data
    // 这里简单处理，el-tree-select 支持清空 parentId 为 null/0 也就代表顶级
    categoryOptions.value = [
      { id: 0, name: '顶级分类', children: res.data }
    ]
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialog.title = '新增分类'
  dialog.type = 'add'
  dialog.visible = true
  resetFormState()
}

const handleAddSub = (row) => {
  dialog.title = '新增子分类'
  dialog.type = 'add'
  dialog.visible = true
  resetFormState()
  form.parentId = row.id // 自动选中父类
}

const handleEdit = (row) => {
  dialog.title = '编辑分类'
  dialog.type = 'edit'
  dialog.visible = true
  Object.assign(form, row)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除分类 "${row.name}" 及其子分类吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    getList()
  })
  .catch(() => {})
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.parentId === null) form.parentId = 0

        if (dialog.type === 'add') {
          await addCategory(form)
          ElMessage.success('新增成功')
        } else {
          await updateCategory(form)
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

const resetFormState = () => {
  form.id = null
  form.parentId = 0
  form.name = ''
  form.sort = 0
  form.status = 1
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.table-operations {
  margin-bottom: 20px;
}
</style>

