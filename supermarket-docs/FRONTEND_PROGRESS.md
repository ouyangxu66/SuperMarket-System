# 前端开发进度 (Frontend Development Progress)

## 📌 任务列表 (Task List)

### 1. 项目初始化 (Project Initialization)
- [x] 初始化 Vue 3 + Vite 项目
- [x] 配置 Element Plus
- [x] 配置 Axios
- [x] 配置 Vue Router
- [x] 创建基础目录结构 (src: api, assets, components, router, stores, utils, views)
- [x] 引入 ECharts 依赖用于 dashboard 趋势图展示

### 2. 认证模块 (Auth Module)
- [x] 登录页面 (Login Page) `/login`
- [x] 注册页面 (Register Page) `/register` (UI Only, API Pending)
- [x] 登录功能对接 (Login API Integration)
- [ ] 用户信息存储 (Pinia Store for User Info)
- [ ] 路由守卫 (Router Guards for Auth)

### 3. 主页 / 数据看板 (Home / Dashboard)
- [x] 仪表盘/首页布局 (Dashboard Layout)
- [x] 侧边栏导航 (Sidebar Navigation)
- [x] 顶部导航栏 (Top Navbar)
- [x] 首页布局页与内容页解耦，`HomeView.vue` 作为后台布局容器
- [x] 新增首页看板路由 `/dashboard`，并将 `/` 默认重定向到 `/dashboard`
- [x] 新增管理驾驶舱页面 `src/views/dashboard/DashboardView.vue`
- [x] 新增 dashboard 聚合 API 封装 `src/api/dashboard.js`
- [x] 看板接入销售、会员、库存三大区块聚合数据
- [x] 看板支持时间范围切换（7天 / 15天 / 30天）
- [x] 看板接入销售趋势图、热销商品排行、库存预警、临期提醒、支付方式分布
- [x] 会员等级扩展区预留占位展示，待后续等级规则完善后接入

### 4. 员工管理模块 (Employee Module)
- [x] 员工列表页面 (Employee List Page)
- [x] 员工新增/编辑 (Employee Create/Edit)
- [x] 员工删除 (Employee Delete)
- [x] 工号、岗位、入职时间、备注字段展示与编辑

### 5. 商品管理模块 (Product Module)
- [x] 商品列表页面 (Product List Page)
- [x] 商品新增/编辑 (Product Create/Edit)
- [x] 商品分类管理 (Category Management)

### 6. 库存管理模块 (Inventory Module)
- [x] 库存列表 (Inventory List)
- [x] 库存盘点 (Inventory Count)
- [x] 盘点详情录入 (Inventory Count Detail)
- [x] 库存预警与临期提醒 (Stock & Expiration Alerts)
- [x] 首页看板展示库存预警商品与临期商品提醒

### 7. 会员管理模块 (Member Module)
- [x] 会员列表页面 (Member List Page)
- [x] 会员新增/编辑/删除 (Member Create/Edit/Delete)
- [x] 会员详情查看 (Member Detail View)
- [x] 会员余额充值与余额调整弹窗 (Balance Recharge & Adjust Dialog)
- [x] 会员余额流水查看 (Balance Flow View)
- [x] 会员积分调整弹窗 (Point Adjust Dialog)
- [x] 会员积分流水查看 (Point Flow View)
- [x] 收银台会员快速查询接口对接 (Quick Member Lookup for Cashier)
- [x] 首页看板展示会员总数、今日新增、本周新增、储值总额、积分统计
- [x] 首页看板展示会员扩展统计占位区，支持后续等级分析扩展

### 8. 销售管理模块 (Sale Module)
- [x] 最小收银台页面 (Cashier Page) `/sale/cashier`
- [x] 商品搜索与购物车 (Product Search & Cart)
- [x] 模拟支付结算 (Mock Checkout)
- [x] 收银台会员快速绑定 (Quick Member Binding)
- [x] 销售记录页面 (Sale Order List Page) `/sale/order-list`
- [x] 销售订单详情弹窗 (Sale Order Detail Dialog)
- [x] 销售记录筛选（订单号/会员姓名/支付方式/时间范围）
- [x] 销售记录顶部统计卡片 (Order Count / Sales Amount / Member Orders / Points)
- [x] 销售页展示会员快照与本单赠送积分字段
- [x] 前端静态校验通过（会员/销售相关页面已完成构建验证）

### 9. 个人中心 (Profile)
- [x] 个人信息展示 (User Info Display)
- [x] 基本资料修改 (Basic Info Edit)
- [x] 修改密码 (Change Password)

## 📝 开发日志 (Development Log)
- **2026-01-15**: 项目初始化完成，创建了基本的项目结构和配置文件。
- **2026-03-06**: 完成“用户管理”到“员工管理”的前端语义升级，保留 `/user` 接口与认证链路兼容。
- **2026-03-08**: 完成首页大屏 / 管理驾驶舱第一阶段改造：新增 `DashboardView.vue`、`dashboard.js`、`/dashboard` 路由，保留 `HomeView.vue` 作为布局页，并接入销售、会员、库存聚合看板展示。
- **2026-03-08**: 更新 dashboard 接口文档与前端依赖清单，引入 ECharts 用于销售趋势图，补充支付方式分布、热销商品排行、库存预警与临期提醒展示。
- **2026-03-08**: 完成会员管理模块第一阶段前端落地：新增 `MemberListView.vue`、`member.js`，支持会员基础 CRUD、余额充值/调整、积分调整、余额/积分流水查看。
- **2026-03-08**: 完成销售前端最小闭环：新增 `sale.js`、`CashierView.vue`、`SaleOrderListView.vue`，支持收银台模拟支付、会员绑定、销售记录查询、订单详情查看。
- **2026-03-08**: 完成销售管理增强：销售记录页支持订单号、会员姓名、支付方式、时间范围筛选，并新增顶部统计卡片展示，用于毕业设计答辩演示。
- **2026-03-08**: 完成会员与销售前端联动验收：收银台支持按手机号/卡号/会员编号快速绑定会员，销售记录页可展示会员姓名、手机号与本单积分。
- **2026-03-09**: 同步前端文档与 README，补充最小收银端、会员流水、销售统计卡片、安全增量 SQL 脚本说明，并记录前端静态校验结果。
