# 📊 Dashboard 专项说明文档

## 一、文档概述

本文档用于说明超市管理系统首页大屏 / 管理驾驶舱（Dashboard）模块的设计目标、路由权限、前后端实现、接口结构、页面组成、运行依赖、验收方式与后续规划。

### 适用范围
- 后台首页看板页面：`/dashboard`
- 前端页面：`supermarket-frontend/src/views/dashboard/DashboardView.vue`
- 前端接口封装：`supermarket-frontend/src/api/dashboard.js`
- 前端路由配置：`supermarket-frontend/src/router/index.js`
- 后端聚合接口：`GET /dashboard/overview`
- 后端实现模块：`supermarket-backend/src/main/java/com/supermarket/dashboard/`

### 技术栈
- 后端：Spring Boot + MyBatis-Plus + Spring Security + JWT
- 前端：Vue 3 + Vite + Element Plus
- 图表：ECharts

### 文档目标
- 统一首页大屏改造的实现口径
- 说明 dashboard 聚合接口与页面分区职责
- 为后续扩展会员等级、专题图表、数据中间表提供基础文档

---

## 二、背景与改造目标

当前系统原首页同时承担以下职责：
- 后台布局容器
- 左侧导航菜单
- 默认首页内容展示

随着销售、库存、会员模块逐步落地，原有首页静态卡片已无法满足系统演示、管理决策与答辩展示需求，因此首页改造方向调整为：

### 改造目标
1. 保留现有后台布局结构稳定，不破坏导航、顶部栏、权限体系。
2. 新增独立 dashboard 内容页，首页默认进入管理驾驶舱。
3. 用聚合接口集中提供销售、会员、库存三类核心数据。
4. 页面既保留后台管理系统风格，又具备“大屏 / 数据看板”展示效果。
5. 支持趋势图、排行、风险提醒、空态占位与后续扩展。

### 本期展示重点
#### 1. 销售数据
- 今日销售额
- 今日订单数
- 客单价
- 最近 7 / 15 / 30 天销售趋势
- 热销商品排行
- 支付方式分布

#### 2. 会员数据
- 会员总数
- 今日新增会员数
- 本周新增会员数
- 会员储值总额
- 会员积分统计
- 活跃会员数
- 新增会员趋势

#### 3. 库存数据
- 商品总数
- 当前库存总量
- 库存预警商品数
- 临期商品数
- 已过期商品数
- 库存成本估值

### 本期边界
- 不替代销售记录、库存列表、会员列表等业务明细页
- 不实现复杂 BI 拖拽配置
- 不引入实时流式计算
- 会员等级分布先预留扩展位，待等级规则完善后接入

---

## 三、路由与权限设计

## 3.1 路由设计
推荐方案已落地：**新增独立 `DashboardView.vue`，保留 `HomeView.vue` 作为布局容器。**

### 路由结构说明
- `HomeView.vue`：继续负责后台整体布局、左侧菜单、顶部区域与内容出口
- `DashboardView.vue`：负责首页看板内容展示
- `/`：默认重定向到 `/dashboard`
- `/dashboard`：作为登录后的默认首页

### 这样设计的原因
1. 不破坏当前后台布局结构。
2. 便于 dashboard 页面独立演进，不污染布局容器。
3. 后续若新增“独立大屏模式”或“电视墙模式”，可以复用 dashboard 组件能力。
4. 风险更低，影响范围可控。

## 3.2 权限设计
当前权限建议继续复用：`dashboard:view`

### 原因
- 该权限已在任务规划与角色清单中出现
- 语义清晰，适合作为首页看板查看权限
- 不需要重新设计一套新的权限命名

### 当前建议
- 页面菜单可见性：复用 `dashboard:view`
- Dashboard 聚合接口访问权限：建议同步使用 `dashboard:view`
- 若暂未完成前后端真实 perms 守卫，可先保留登录态可访问，并在代码中为后续权限增强预留位置

### 角色适配建议
- `ROLE_ADMIN`：可访问 dashboard 全部内容
- `ROLE_STORE_MANAGER`：可访问 dashboard 全部内容
- `ROLE_WAREHOUSE_KEEPER`：可访问 dashboard，重点关注库存板块
- `ROLE_CASHIER`：可访问 dashboard，重点查看销售概览
- `ROLE_PURCHASER`：可访问 dashboard，重点查看库存与商品概览

---

## 四、前后端实现概览

## 4.1 前端实现概览
前端 dashboard 页面负责：
- 拉取首页聚合数据
- 切换统计时间范围（7 天 / 15 天 / 30 天）
- 渲染核心统计卡片
- 渲染销售趋势图
- 渲染热销商品排行、支付分布、库存预警、临期提醒
- 展示会员扩展统计占位信息

### 主要文件
- `supermarket-frontend/src/views/dashboard/DashboardView.vue`
- `supermarket-frontend/src/api/dashboard.js`
- `supermarket-frontend/src/router/index.js`

### 页面职责边界
- 前端只消费聚合结果，不在页面层拼接多个零散接口
- 页面主要负责展示、交互、异常提示与图表渲染
- 图表使用 ECharts，适合趋势、占比、排行等 dashboard 场景

## 4.2 后端实现概览
后端 dashboard 模块负责统一聚合销售、会员、库存数据，并向前端返回可直接渲染的结构化结果。

### 主要职责
- 接收前端统计参数
- 计算统计时间范围
- 聚合多模块指标
- 返回统一 dashboard DTO / VO 结构
- 对无数据、非法参数、占位能力进行兜底

### 主要实现模块
- `controller`：提供 dashboard 接口入口
- `service`：组织聚合逻辑与业务口径
- `mapper`：执行统计 SQL 与聚合查询
- `vo` / `dto`：封装 dashboard 返回结构
- `resources/mapper/dashboard/`：MyBatis XML 统计 SQL

---

## 五、Dashboard 指标设计

## 5.1 销售指标
### 核心指标
- `todaySalesAmount`：今日销售额
- `todayOrderCount`：今日订单数
- `avgOrderAmount`：今日客单价
- `rangeSalesAmount`：统计周期内销售额
- `rangeOrderCount`：统计周期内订单数

### 图表与排行
- `salesTrend`：最近 N 天销售趋势
- `hotProducts`：热销商品排行
- `paymentDistribution`：支付方式分布

### 建议口径
- 今日：按自然日统计
- 趋势：按日汇总
- 热销排行：按销量或销售额排序，建议文档中写清当前使用的排序口径

## 5.2 会员指标
### 核心指标
- `totalMembers`：会员总数
- `todayNewMembers`：今日新增会员数
- `weekNewMembers`：本周新增会员数
- `totalBalance`：会员储值总额
- `totalPoints`：会员积分总额
- `activeMembers`：近一段时间活跃会员数

### 趋势与扩展
- `newMemberTrend`：新增会员趋势
- `levelDistribution`：会员等级分布（当前预留）

### 占位策略
如果会员等级规则尚未完成：
- 接口可返回空数组
- 或返回 `ready=false`、`pendingReason=等级规则未完成`
- 前端使用“能力建设中”占位卡展示

## 5.3 库存指标
### 核心指标
- `productCount`：商品总数
- `totalStockQuantity`：当前库存总量
- `warningProductCount`：库存预警商品数
- `nearExpiryCount`：临期商品数
- `expiredCount`：已过期商品数
- `stockCostAmount`：库存成本估值

### 风险提醒清单
- `lowStockList`：库存预警商品列表
- `nearExpiryList`：临期商品列表

### 建议口径
- 库存预警：低于安全库存下限
- 临期：距离过期日期小于等于 `nearExpiryDays`
- 过期：过期日期早于当前日期

---

## 六、后端聚合接口设计

## 6.1 接口概览
### 接口名称
首页看板总览接口

### 请求方式
`GET`

### 请求路径
`/dashboard/overview`

### 鉴权方式
- 需要登录态
- 建议复用 `dashboard:view` 权限

## 6.2 请求参数
| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|---|---|---:|---|---|
| `rangeType` | string | 否 | `7d` | 统计周期，可选 `7d` / `15d` / `30d` |
| `topN` | number | 否 | `10` | 热销排行返回数量 |
| `nearExpiryDays` | number | 否 | `7` | 临期商品判断天数 |

### 参数处理建议
- 对非法 `rangeType` 做默认兜底
- 对 `topN` 做范围限制，避免超大查询
- 对 `nearExpiryDays` 做非负与上限校验

## 6.3 响应结构建议
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "sales": {
      "summary": {},
      "trend": [],
      "hotProducts": [],
      "paymentDistribution": []
    },
    "members": {
      "summary": {},
      "newMemberTrend": [],
      "levelDistribution": [],
      "levelFeature": {
        "ready": false,
        "pendingReason": "会员等级规则未完成"
      }
    },
    "inventory": {
      "summary": {},
      "lowStockList": [],
      "nearExpiryList": []
    },
    "meta": {
      "rangeType": "7d",
      "startDate": "2026-03-03",
      "endDate": "2026-03-09",
      "generatedAt": "2026-03-09 15:00:00"
    }
  }
}
```

## 6.4 设计原则
1. **优先聚合接口**：避免前端首页发起多个零散请求。
2. **字段稳定**：无数据时返回空数组与默认值，不让前端猜结构。
3. **前端友好**：趋势数据尽量补齐日期，降低页面判断复杂度。
4. **预留扩展**：会员等级、专题分析、导出能力可逐步追加。

---

## 七、前端页面结构设计

## 7.1 页面分区建议
### A. 顶部信息区
- 页面标题
- 数据更新时间
- 时间范围切换按钮（7 / 15 / 30 天）
- 手动刷新入口

### B. 核心指标卡区
- 今日销售额
- 今日订单数
- 会员总数
- 当前库存总量
- 可扩展显示客单价、预警商品数等

### C. 趋势图区
- 销售趋势图
- 新增会员趋势图

### D. 分布 / 排行区
- 热销商品排行
- 支付方式分布

### E. 风险提醒区
- 库存预警商品列表
- 临期 / 过期商品提醒列表

### F. 会员扩展占位区
- 会员等级分析占位卡
- 后续可扩展复购率、会员贡献度等指标

## 7.2 视觉风格建议
- 保持与 Element Plus 后台管理风格兼容
- 用深色渐变、数据高亮、图表卡片增强“大屏感”
- 信息密度适中，不追求电视墙式夸张布局
- 优先保证管理系统中的可用性与可维护性

## 7.3 为什么建议使用 ECharts
推荐引入 ECharts，原因如下：
1. 趋势图、柱状图、饼图能力成熟。
2. 与 Vue 3 结合成本低。
3. 适合 dashboard 类型页面的快速落地。
4. 可支持后续更多专题图表扩展。
5. 与当前项目技术栈兼容度高。

---

## 八、数据来源与模块依赖

## 8.1 销售数据来源
依赖模块：`sale`

可聚合的基础通常包括：
- 销售订单表
- 销售明细表
- 支付方式字段
- 商品销量与销售额

支持的 dashboard 统计：
- 今日销售额
- 今日订单数
- 销售趋势
- 热销商品排行
- 支付方式分布

## 8.2 会员数据来源
依赖模块：`member`

当前建议可聚合：
- 会员总数
- 今日新增 / 本周新增
- 储值总额
- 积分总额
- 新增趋势

如果会员等级能力尚未完成：
- 本期先不强依赖等级规则
- 接口中保留等级分布字段或占位说明
- 页面展示“待等级规则完成后接入”提示

## 8.3 库存数据来源
依赖模块：`inventory`、`product`

可聚合的基础通常包括：
- 商品总数
- 当前库存数量
- 库存预警数据
- 临期商品数据
- 过期商品数据
- 采购或成本字段（用于库存成本估值）

---

## 九、受影响文件清单

## 9.1 前端
- `supermarket-frontend/src/views/HomeView.vue`
- `supermarket-frontend/src/views/dashboard/DashboardView.vue`
- `supermarket-frontend/src/api/dashboard.js`
- `supermarket-frontend/src/router/index.js`
- `supermarket-frontend/package.json`

## 9.2 后端
- `supermarket-backend/src/main/java/com/supermarket/dashboard/` 下相关文件
- `supermarket-backend/src/main/resources/mapper/dashboard/` 下相关 XML
- 与销售、会员、库存聚合相关的 service / mapper / entity 辅助代码

## 9.3 文档
- `supermarket-docs/API_DOC.md`
- `supermarket-docs/TASK_LIST.md`
- `supermarket-docs/FRONTEND_PROGRESS.md`
- `supermarket-docs/DASHBOARD_DOC.md`

---

## 十、运行与依赖说明

## 10.1 前端依赖
Dashboard 页面图表依赖 `echarts`。

若本地尚未安装，可在前端目录执行：

```bash
npm install echarts
```

## 10.2 运行前提
- 后端数据库已初始化
- 销售、库存、会员存在基础测试数据
- 前端已配置正确的接口访问地址
- 登录后获取 JWT token，并通过请求头携带

## 10.3 数据为空时的表现
- 核心数值显示为 `0`
- 趋势与排行显示为空态
- 会员扩展位显示占位说明
- 不应因为局部无数据导致整个 dashboard 崩溃

---

## 十一、最小测试点与验收步骤

## 11.1 功能验收
- 登录后访问 `/dashboard` 页面成功
- `/` 可自动跳转到 `/dashboard`
- 时间范围切换后卡片与图表同步刷新
- 销售、会员、库存三大区块可正常显示
- 热销排行、支付分布、库存预警、临期提醒正常展示

## 11.2 接口验收
- `GET /dashboard/overview` 返回结构稳定
- 参数缺省时能返回默认统计周期数据
- 参数非法时有兜底，不报 500
- 无数据时返回空数组 / 0 值而不是空对象混乱结构

## 11.3 权限验收
- 未登录用户不能直接获取 dashboard 数据
- 已登录用户可正常访问 dashboard
- 若启用权限守卫，具备 `dashboard:view` 的角色可访问

## 11.4 页面验收
- 页面加载态、空态、异常态可区分
- 图表在数据切换后正常重绘
- 页面在常见后台分辨率下无明显错位

---

## 十二、风险点与前置依赖

### 风险点
1. 销售、会员、库存模块数据口径不一致，可能导致 dashboard 指标理解偏差。
2. 若数据库样本数据不足，趋势图和排行效果不明显。
3. 会员等级规则未完成，会员分析板块需要占位兜底。
4. 聚合 SQL 过多时可能影响首页首屏性能，需要后续评估缓存或中间表。
5. 如果前端缺少 `echarts` 依赖，会导致页面构建失败。

### 前置依赖
- 销售模块基础查询稳定
- 库存预警与临期检测逻辑可复用
- 会员基础统计字段可查询
- dashboard 后端聚合接口已经落地
- 前端完成 `echarts` 依赖安装与接口对接

---

## 十三、后续规划

### 短期规划
- 完善 `dashboard:view` 的前后端真实权限守卫
- 补充会员等级分布与等级规则口径
- 增加核心卡片点击跳转到业务明细页

### 中期规划
- 新增门店维度、员工维度、支付渠道维度筛选
- 增加专题分析图表
- 评估 dashboard 查询缓存或数据中间表

### 长期规划
- 扩展经营驾驶舱能力
- 增加导出、轮播、大屏模式
- 建立统一指标口径说明文档

---

## 十四、关联文档
- `supermarket-docs/API_DOC.md`
- `supermarket-docs/TASK_LIST.md`
- `supermarket-docs/FRONTEND_PROGRESS.md`
- `README.md`

---

## 十五、结论
首页大屏改造建议并已优先采用以下方案：

**新增 dashboard 页面 + 保持布局稳定 + 补充聚合接口**

即：
- `HomeView.vue` 保持后台布局容器角色
- 新增独立 `DashboardView.vue` 承载首页看板内容
- 后端提供 dashboard 聚合接口，减少前端拼装复杂度
- 权限优先复用 `dashboard:view`
- 会员未完成部分通过占位结构预留后续能力

该方案对现有系统侵入性较低、演示效果较好、后续扩展空间也更大。
