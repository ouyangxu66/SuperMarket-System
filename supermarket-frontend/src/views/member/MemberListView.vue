<template>
  <div class="member-list-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入手机号、姓名、昵称、会员编号或卡号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="会员编号">
          <el-input
            v-model="queryParams.memberNo"
            placeholder="请输入会员编号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="会员卡号">
          <el-input
            v-model="queryParams.cardNo"
            placeholder="请输入会员卡号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增会员</el-button>
        <el-button type="danger" icon="Delete" :disabled="selection.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="memberList"
        style="width: 100%"
        border
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="memberNo" label="会员编号" min-width="160" />
        <el-table-column prop="cardNo" label="会员卡号" min-width="160" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120">
          <template #default="scope">
            {{ scope.row.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="gender" label="性别" width="90" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.gender === 1" type="primary">男</el-tag>
            <el-tag v-else-if="scope.row.gender === 0" type="danger">女</el-tag>
            <el-tag v-else type="info">未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="储值余额" width="120" align="right">
          <template #default="scope">
            {{ formatMoney(scope.row.balance) }}
          </template>
        </el-table-column>
        <el-table-column prop="points" label="当前积分" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180" align="center">
          <template #default="scope">
            {{ formatDateTime(scope.row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="180">
          <template #default="scope">
            {{ scope.row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="420" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link icon="View" @click="handleView(scope.row)">详情</el-button>
            <el-button type="success" link @click="handleRecharge(scope.row)">充值</el-button>
            <el-button type="warning" link @click="handleAdjustBalance(scope.row)">余额调整</el-button>
            <el-button type="info" link @click="handleViewBalanceFlow(scope.row)">余额流水</el-button>
            <el-button type="success" link @click="handleAdjustPoint(scope.row)">积分调整</el-button>
            <el-button type="info" link @click="handleViewPointFlow(scope.row)">积分流水</el-button>
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="620px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="会员卡号" prop="cardNo">
              <el-input v-model="form.cardNo" placeholder="请输入会员卡号">
                <template #append>
                  <el-button @click="generateCardNo">生成</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入会员姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="会员昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入会员昵称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="0">女</el-radio>
                <el-radio :label="2">未知</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择生日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">正常</el-radio>
                <el-radio :label="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册渠道" prop="registerChannel">
              <el-select v-model="form.registerChannel" placeholder="请选择注册渠道" style="width: 100%">
                <el-option label="线下门店" value="OFFLINE" />
                <el-option label="APP" value="APP" />
                <el-option label="小程序" value="MINI_PROGRAM" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="会员详情" v-model="detailDialog.visible" width="520px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="会员编号">{{ detail.memberNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="会员卡号">{{ detail.cardNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detail.name || '-' }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detail.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ genderText(detail.gender) }}</el-descriptions-item>
        <el-descriptions-item label="储值余额">{{ formatMoney(detail.balance) }}</el-descriptions-item>
        <el-descriptions-item label="当前积分">{{ detail.points ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === 1 ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="注册渠道">{{ detail.registerChannel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ formatDate(detail.birthday) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDateTime(detail.registerTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="会员充值" v-model="balanceDialog.rechargeVisible" width="460px" @close="resetBalanceForm">
      <el-form :model="balanceForm" :rules="balanceRules" ref="balanceFormRef" label-width="88px">
        <el-form-item label="会员姓名">
          <el-input :model-value="balanceDialog.memberName" disabled />
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          <el-input-number v-model="balanceForm.amount" :min="0.01" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="来源说明" prop="source">
          <el-input v-model="balanceForm.source" placeholder="如：门店现金充值" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="balanceForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="balanceDialog.rechargeVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRecharge">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="余额调整" v-model="balanceDialog.adjustVisible" width="460px" @close="resetBalanceForm">
      <el-form :model="balanceForm" :rules="balanceRules" ref="balanceFormRef" label-width="88px">
        <el-form-item label="会员姓名">
          <el-input :model-value="balanceDialog.memberName" disabled />
        </el-form-item>
        <el-form-item label="调整金额" prop="changeAmount">
          <el-input-number v-model="balanceForm.changeAmount" :precision="2" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="来源说明" prop="source">
          <el-input v-model="balanceForm.source" placeholder="如：人工修正" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="balanceForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="balanceDialog.adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBalanceAdjust">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="余额流水" v-model="balanceFlowDialog.visible" width="820px">
      <el-table :data="balanceFlowList" border v-loading="balanceFlowDialog.loading">
        <el-table-column prop="bizType" label="业务类型" width="140" />
        <el-table-column prop="bizNo" label="业务单号" min-width="180" />
        <el-table-column prop="changeType" label="方向" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.changeType === 1 ? 'success' : 'danger'">
              {{ scope.row.changeType === 1 ? '增加' : '减少' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="beforeBalance" label="变动前" width="100" align="right">
          <template #default="scope">{{ formatMoney(scope.row.beforeBalance) }}</template>
        </el-table-column>
        <el-table-column prop="changeAmount" label="变动金额" width="100" align="right">
          <template #default="scope">{{ formatMoney(scope.row.changeAmount) }}</template>
        </el-table-column>
        <el-table-column prop="afterBalance" label="变动后" width="100" align="right">
          <template #default="scope">{{ formatMoney(scope.row.afterBalance) }}</template>
        </el-table-column>
        <el-table-column prop="source" label="来源说明" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" align="center">
          <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="balanceFlowDialog.pageNum"
          v-model:page-size="balanceFlowDialog.pageSize"
          :page-sizes="[10, 20, 30]"
          :background="true"
          layout="total, sizes, prev, pager, next"
          :total="balanceFlowDialog.total"
          @size-change="fetchBalanceFlow"
          @current-change="fetchBalanceFlow"
        />
      </div>
      <template #footer>
        <el-button type="primary" @click="balanceFlowDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="积分调整" v-model="pointDialog.visible" width="460px" @close="resetPointForm">
      <el-form :model="pointForm" :rules="pointRules" ref="pointFormRef" label-width="88px">
        <el-form-item label="会员姓名">
          <el-input :model-value="pointDialog.memberName" disabled />
        </el-form-item>
        <el-form-item label="调整积分" prop="changePoints">
          <el-input-number v-model="pointForm.changePoints" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="来源说明" prop="source">
          <el-input v-model="pointForm.source" placeholder="如：活动补偿" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="pointForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitPointAdjust">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="积分流水" v-model="pointFlowDialog.visible" width="820px">
      <el-table :data="pointFlowList" border v-loading="pointFlowDialog.loading">
        <el-table-column prop="bizType" label="业务类型" width="140" />
        <el-table-column prop="bizNo" label="业务单号" min-width="180" />
        <el-table-column prop="changeType" label="方向" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.changeType === 1 ? 'success' : 'danger'">
              {{ scope.row.changeType === 1 ? '增加' : '减少' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="beforePoints" label="变动前" width="100" align="center" />
        <el-table-column prop="changePoints" label="变动积分" width="100" align="center" />
        <el-table-column prop="afterPoints" label="变动后" width="100" align="center" />
        <el-table-column prop="source" label="来源说明" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" align="center">
          <template #default="scope">{{ formatDateTime(scope.row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pointFlowDialog.pageNum"
          v-model:page-size="pointFlowDialog.pageSize"
          :page-sizes="[10, 20, 30]"
          :background="true"
          layout="total, sizes, prev, pager, next"
          :total="pointFlowDialog.total"
          @size-change="fetchPointFlow"
          @current-change="fetchPointFlow"
        />
      </div>
      <template #footer>
        <el-button type="primary" @click="pointFlowDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addMember,
  adjustMemberBalance,
  adjustMemberPoint,
  deleteMember,
  deleteMemberBatch,
  getMemberBalanceFlowPage,
  getMemberById,
  getMemberPage,
  getMemberPointFlowPage,
  rechargeMemberBalance,
  updateMember
} from '@/api/member'

const loading = ref(false)
const total = ref(0)
const memberList = ref([])
const selection = ref([])
const formRef = ref(null)
const balanceFormRef = ref(null)
const pointFormRef = ref(null)
const balanceFlowList = ref([])
const pointFlowList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: undefined,
  phone: '',
  memberNo: '',
  cardNo: ''
})

const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const detailDialog = reactive({
  visible: false
})

const balanceDialog = reactive({
  rechargeVisible: false,
  adjustVisible: false,
  memberId: null,
  memberName: ''
})

const pointDialog = reactive({
  visible: false,
  memberId: null,
  memberName: ''
})

const balanceFlowDialog = reactive({
  visible: false,
  loading: false,
  memberId: null,
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const pointFlowDialog = reactive({
  visible: false,
  loading: false,
  memberId: null,
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = ref({
  id: undefined,
  cardNo: '',
  name: '',
  nickname: '',
  phone: '',
  gender: 2,
  birthday: '',
  status: 1,
  remark: '',
  registerChannel: 'OFFLINE',
  levelId: null
})

const balanceForm = ref({
  memberId: null,
  amount: 0,
  changeAmount: 0,
  source: '',
  remark: ''
})

const pointForm = ref({
  memberId: null,
  changePoints: 0,
  source: '',
  remark: ''
})

const detail = ref({})

const rules = {
  cardNo: [
    { required: true, message: '请输入会员卡号', trigger: 'blur' },
    { min: 2, max: 32, message: '会员卡号长度需在 2 到 32 个字符之间', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9_-]+$/, message: '会员卡号仅支持字母、数字、下划线和中划线', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入会员姓名', trigger: 'blur' },
    { max: 50, message: '会员姓名长度不能超过 50 个字符', trigger: 'blur' }
  ],
  nickname: [
    { max: 50, message: '会员昵称长度不能超过 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  remark: [
    { max: 255, message: '备注长度不能超过 255 个字符', trigger: 'blur' }
  ]
}

const balanceRules = {
  amount: [
    {
      validator: (_rule, value, callback) => {
        if (balanceDialog.rechargeVisible && (!value || Number(value) <= 0)) {
          callback(new Error('充值金额必须大于 0'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  changeAmount: [
    {
      validator: (_rule, value, callback) => {
        if (balanceDialog.adjustVisible && (!value || Number(value) === 0)) {
          callback(new Error('调整金额不能为 0'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  source: [
    { required: true, message: '请输入来源说明', trigger: 'blur' },
    { max: 64, message: '来源说明长度不能超过 64 个字符', trigger: 'blur' }
  ],
  remark: [
    { max: 255, message: '备注长度不能超过 255 个字符', trigger: 'blur' }
  ]
}

const pointRules = {
  changePoints: [
    {
      validator: (_rule, value, callback) => {
        if (!value || Number(value) === 0) {
          callback(new Error('积分调整值不能为 0'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  source: [
    { required: true, message: '请输入来源说明', trigger: 'blur' },
    { max: 64, message: '来源说明长度不能超过 64 个字符', trigger: 'blur' }
  ],
  remark: [
    { max: 255, message: '备注长度不能超过 255 个字符', trigger: 'blur' }
  ]
}

onMounted(() => {
  fetchMemberList()
})

const fetchMemberList = async () => {
  loading.value = true
  try {
    const res = await getMemberPage(queryParams)
    memberList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取会员列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  fetchMemberList()
}

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.status = undefined
  queryParams.phone = ''
  queryParams.memberNo = ''
  queryParams.cardNo = ''
  queryParams.pageNum = 1
  fetchMemberList()
}

const handleSelectionChange = (val) => {
  selection.value = val
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  fetchMemberList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  fetchMemberList()
}

const handleAdd = () => {
  resetForm()
  dialog.type = 'add'
  dialog.title = '新增会员'
  dialog.visible = true
  generateCardNo()
}

const handleEdit = (row) => {
  resetForm()
  dialog.type = 'edit'
  dialog.title = '编辑会员'
  dialog.visible = true
  nextTick(() => {
    Object.assign(form.value, {
      id: row.id,
      cardNo: row.cardNo || '',
      name: row.name || '',
      nickname: row.nickname || '',
      phone: row.phone || '',
      gender: row.gender ?? 2,
      birthday: formatDateValue(row.birthday),
      status: row.status ?? 1,
      remark: row.remark || '',
      registerChannel: row.registerChannel || 'OFFLINE',
      levelId: row.levelId ?? null
    })
  })
}

const handleView = async (row) => {
  try {
    const res = await getMemberById(row.id)
    detail.value = res.data || {}
    detailDialog.visible = true
  } catch (error) {
    console.error('获取会员详情失败', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确认删除会员 "${row.name}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await deleteMember(row.id)
      ElMessage.success('删除成功')
      fetchMemberList()
    })
    .catch(() => {})
}

const handleBatchDelete = () => {
  if (selection.value.length === 0) return

  const ids = selection.value.map(item => item.id)
  ElMessageBox.confirm(
    `确认批量删除 ${ids.length} 名会员吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
    .then(async () => {
      await deleteMemberBatch(ids)
      ElMessage.success('批量删除成功')
      fetchMemberList()
    })
    .catch(() => {})
}

const handleRecharge = (row) => {
  resetBalanceForm()
  balanceDialog.memberId = row.id
  balanceDialog.memberName = row.name
  balanceDialog.rechargeVisible = true
  balanceForm.value.memberId = row.id
  balanceForm.value.source = '门店现金充值'
}

const handleAdjustBalance = (row) => {
  resetBalanceForm()
  balanceDialog.memberId = row.id
  balanceDialog.memberName = row.name
  balanceDialog.adjustVisible = true
  balanceForm.value.memberId = row.id
  balanceForm.value.source = '人工修正'
}

const handleViewBalanceFlow = async (row) => {
  balanceFlowDialog.visible = true
  balanceFlowDialog.memberId = row.id
  balanceFlowDialog.pageNum = 1
  balanceFlowDialog.pageSize = 10
  await fetchBalanceFlow()
}

const handleAdjustPoint = (row) => {
  resetPointForm()
  pointDialog.memberId = row.id
  pointDialog.memberName = row.name
  pointDialog.visible = true
  pointForm.value.memberId = row.id
  pointForm.value.source = '活动补偿'
}

const handleViewPointFlow = async (row) => {
  pointFlowDialog.visible = true
  pointFlowDialog.memberId = row.id
  pointFlowDialog.pageNum = 1
  pointFlowDialog.pageSize = 10
  await fetchPointFlow()
}

const submitForm = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const payload = {
        ...form.value,
        cardNo: form.value.cardNo.trim(),
        name: form.value.name.trim(),
        nickname: form.value.nickname?.trim() || '',
        phone: form.value.phone.trim(),
        remark: form.value.remark?.trim() || ''
      }

      if (dialog.type === 'add') {
        await addMember(payload)
        ElMessage.success('新增成功')
      } else {
        await updateMember(payload)
        ElMessage.success('修改成功')
      }
      dialog.visible = false
      fetchMemberList()
    } catch (error) {
      console.error('保存会员失败', error)
      ElMessage.error(error?.response?.data?.message || error?.message || '保存失败，请稍后重试')
    }
  })
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.value = {
    id: undefined,
    cardNo: '',
    name: '',
    nickname: '',
    phone: '',
    gender: 2,
    birthday: '',
    status: 1,
    remark: '',
    registerChannel: 'OFFLINE',
    levelId: null
  }
}

const resetBalanceForm = () => {
  if (balanceFormRef.value) {
    balanceFormRef.value.resetFields()
  }
  balanceForm.value = {
    memberId: null,
    amount: 0,
    changeAmount: 0,
    source: '',
    remark: ''
  }
}

const resetPointForm = () => {
  if (pointFormRef.value) {
    pointFormRef.value.resetFields()
  }
  pointForm.value = {
    memberId: null,
    changePoints: 0,
    source: '',
    remark: ''
  }
}

const submitRecharge = async () => {
  if (!balanceFormRef.value) return
  await balanceFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await rechargeMemberBalance({
        memberId: balanceDialog.memberId,
        amount: balanceForm.value.amount,
        source: balanceForm.value.source.trim(),
        remark: balanceForm.value.remark?.trim() || ''
      })
      ElMessage.success('充值成功')
      balanceDialog.rechargeVisible = false
      fetchMemberList()
    } catch (error) {
      console.error('会员充值失败', error)
      ElMessage.error(error?.response?.data?.message || error?.message || '充值失败，请稍后重试')
    }
  })
}

const submitBalanceAdjust = async () => {
  if (!balanceFormRef.value) return
  await balanceFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await adjustMemberBalance({
        memberId: balanceDialog.memberId,
        changeAmount: balanceForm.value.changeAmount,
        source: balanceForm.value.source.trim(),
        remark: balanceForm.value.remark?.trim() || ''
      })
      ElMessage.success('余额调整成功')
      balanceDialog.adjustVisible = false
      fetchMemberList()
    } catch (error) {
      console.error('余额调整失败', error)
      ElMessage.error(error?.response?.data?.message || error?.message || '余额调整失败，请稍后重试')
    }
  })
}

const submitPointAdjust = async () => {
  if (!pointFormRef.value) return
  await pointFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await adjustMemberPoint({
        memberId: pointDialog.memberId,
        changePoints: Number(pointForm.value.changePoints),
        source: pointForm.value.source.trim(),
        remark: pointForm.value.remark?.trim() || ''
      })
      ElMessage.success('积分调整成功')
      pointDialog.visible = false
      fetchMemberList()
    } catch (error) {
      console.error('积分调整失败', error)
      ElMessage.error(error?.response?.data?.message || error?.message || '积分调整失败，请稍后重试')
    }
  })
}

const fetchBalanceFlow = async () => {
  if (!balanceFlowDialog.memberId) return
  balanceFlowDialog.loading = true
  try {
    const res = await getMemberBalanceFlowPage({
      pageNum: balanceFlowDialog.pageNum,
      pageSize: balanceFlowDialog.pageSize,
      memberId: balanceFlowDialog.memberId
    })
    balanceFlowList.value = res.data.records || []
    balanceFlowDialog.total = res.data.total || 0
  } catch (error) {
    console.error('获取余额流水失败', error)
  } finally {
    balanceFlowDialog.loading = false
  }
}

const fetchPointFlow = async () => {
  if (!pointFlowDialog.memberId) return
  pointFlowDialog.loading = true
  try {
    const res = await getMemberPointFlowPage({
      pageNum: pointFlowDialog.pageNum,
      pageSize: pointFlowDialog.pageSize,
      memberId: pointFlowDialog.memberId
    })
    pointFlowList.value = res.data.records || []
    pointFlowDialog.total = res.data.total || 0
  } catch (error) {
    console.error('获取积分流水失败', error)
  } finally {
    pointFlowDialog.loading = false
  }
}

const formatDateTime = (timeStr) => {
  if (!timeStr) return ''
  return String(timeStr).replace('T', ' ')
}

const formatDateValue = (timeStr) => {
  if (!timeStr) return ''
  return String(timeStr).replace('T', ' ').slice(0, 10)
}

const formatDate = (timeStr) => {
  if (!timeStr) return ''
  return String(timeStr).replace('T', ' ').slice(0, 10)
}

const formatMoney = (value) => {
  const amount = Number(value || 0)
  return amount.toFixed(2)
}

const genderText = (gender) => {
  if (gender === 1) return '男'
  if (gender === 0) return '女'
  return '未知'
}

const buildCardNo = () => {
  const now = new Date()
  const pad = (value) => String(value).padStart(2, '0')
  return `VIP${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`
}

const generateCardNo = () => {
  if (dialog.type === 'add' && !form.value.cardNo) {
    form.value.cardNo = buildCardNo()
  } else if (dialog.type !== 'add') {
    form.value.cardNo = buildCardNo()
  }
  if (formRef.value) {
    formRef.value.validateField('cardNo', () => {})
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 20px;
  display: flex;
  gap: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
