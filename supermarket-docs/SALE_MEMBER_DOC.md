# 🧾 销售与会员管理专项说明文档

## 一、文档概述

本文档用于统一说明当前超市管理系统中“会员管理 + 销售管理”两大模块的设计目标、数据库结构、前后端实现、页面能力、接口范围、会员销售联动逻辑、最小验收标准与后续扩展规划。

### 适用范围
- 会员管理模块：`/member/*`
- 销售收银模块：`/sale/*`
- 前端会员页面：`supermarket-frontend/src/views/member/MemberListView.vue`
- 前端收银页面：`supermarket-frontend/src/views/sale/CashierView.vue`
- 前端销售记录页面：`supermarket-frontend/src/views/sale/SaleOrderListView.vue`
- 前端接口封装：`supermarket-frontend/src/api/member.js`、`supermarket-frontend/src/api/sale.js`
- 后端会员模块：`supermarket-backend/src/main/java/com/supermarket/member/`
- 后端销售模块：`supermarket-backend/src/main/java/com/supermarket/sale/`
- 数据库脚本：`supermarket-database/sql/05_member.sql`、`supermarket-database/sql/04_sales.sql`、`supermarket-database/sql/04_sales_upgrade_safe.sql`

### 技术栈
- 后端：Spring Boot + MyBatis-Plus + Spring Security + JWT
- 前端：Vue 3 + Vite + Element Plus
- 数据库：MySQL

### 文档目标
- 统一会员管理与销售管理的实现口径
- 说明首期最小闭环已经做到什么程度
- 区分“已实现能力”与“后续预留能力”
- 为答辩展示、后续联调、后续迭代提供依据

---

## 二、业务目标与最小闭环

当前这两个模块的核心目标不是一次性做成完整 CRM + POS，而是先完成一个适合毕业设计演示、同时具备后续扩展空间的最小业务闭环。

### 2.1 本期最小闭环
1. 支持会员建档、查询、编辑、逻辑删除。
2. 支持会员余额充值、余额人工调整、积分人工调整。
3. 支持余额流水、积分流水留痕，可追溯变动来源。
4. 支持最小收银台：商品搜索、购物车、模拟支付、立即结算。
5. 支持销售订单落库、销售明细落库、库存扣减。
6. 支持收银时绑定会员，并在销售完成后为会员累计消费与赠送积分。
7. 支持销售记录分页查询、条件筛选、订单详情查看。

### 2.2 本期明确不做完整实现的能力
以下内容已做字段或结构预留，但当前不作为首期已完成能力：
- 会员等级自动计算
- 积分抵扣现金
- 会员余额直接支付扣款
- 销售退款回滚会员余额/积分
- 问卷系统与抽奖发放
- 收银员身份与登录态强绑定

---

## 三、当前实现现状总览

## 3.1 会员模块现状
当前仓库内，会员模块已经具备可复用且可演示的基础实现，已完成内容包括：
- 会员主表设计
- 会员等级表预留
- 会员余额流水表
- 会员积分流水表
- 会员基础 CRUD
- 分页列表与关键字筛选
- 收银台快速查询会员
- 余额充值 / 余额调整
- 积分调整
- 余额流水 / 积分流水分页查询
- dashboard 会员统计聚合

### 已落地能力
- 手机号唯一校验
- 会员卡号唯一校验
- 余额不能为负
- 积分调整留痕
- 会员逻辑删除
- 删除会员前考虑历史销售记录

## 3.2 销售模块现状
当前销售模块已经具备“最小可用收银端”能力，已完成内容包括：
- 销售主单 / 明细表设计
- 收银台结算接口
- 商品库存校验
- 结算后自动扣减库存
- 销售主单、明细落库
- 销售记录分页查询
- 销售订单详情查询
- 销售记录支付方式筛选
- 销售记录顶部统计卡片
- 销售与会员绑定联动
- 会员消费赠分

### 当前实现边界
- 当前支付为“模拟支付成功”，不接第三方支付网关
- `cashier_id` 当前由服务端固定写入 `1L`，适合毕业设计演示，后续可对接当前登录用户
- 当前已支持“消费赠分”，但“积分抵扣”“余额支付”仍为预留能力

---

## 四、会员管理模块设计

## 4.1 会员主数据设计
会员主表建议并已按以下方向落地：
- `member_no`：会员编号，系统内部唯一编号
- `card_no`：会员卡号
- `name`：会员姓名
- `nickname`：会员昵称
- `phone`：手机号
- `gender`：性别
- `birthday`：生日
- `status`：状态
- `remark`：备注
- `balance`：当前储值余额
- `points`：当前消费积分
- `total_recharge_amount`：累计充值金额
- `total_consume_amount`：累计消费金额
- `total_consume_count`：累计消费次数
- `last_consume_time`：最后消费时间
- `last_recharge_time`：最后充值时间
- `register_time`：注册时间
- `register_channel`：注册渠道
- `level_id`：会员等级 ID（预留）
- `is_deleted`：逻辑删除标记

## 4.2 会员基础功能
### 已实现能力
- 新增会员
- 编辑会员
- 根据 ID 查询详情
- 分页查询会员列表
- 关键字检索（手机号、姓名、昵称、会员编号、会员卡号）
- 状态筛选
- 单条逻辑删除
- 批量逻辑删除

### 删除规则
会员删除优先采用逻辑删除，原因如下：
1. 会员可能已经有关联销售订单。
2. 余额 / 积分流水必须保留审计痕迹。
3. 适合毕业设计场景下的数据可追溯要求。

### 唯一性约束
- 手机号唯一
- 会员卡号唯一
- 会员编号唯一

---

## 五、余额与积分体系设计

## 5.1 为什么不能只存主表快照
余额和积分如果只存在会员主表，会带来两个问题：
1. 无法追踪每次变动来源。
2. 后续接入充值、消费、抵扣、退款、活动赠送时无法审计。

因此当前设计采用：
- 主表存当前余额 / 当前积分快照
- 流水表存每次变动记录

这是本项目中更合理也更可扩展的方案。

## 5.2 余额流水设计
余额流水表 `member_balance_flow` 记录以下核心字段：
- `member_id`
- `change_type`：增加 / 减少
- `biz_type`：业务类型
- `biz_no`：关联业务单号
- `before_balance`
- `change_amount`
- `after_balance`
- `operator_id`
- `operator_name`
- `source`
- `remark`
- `create_time`
- `is_deleted`

### 当前已覆盖的余额业务类型
- 充值 `RECHARGE`
- 人工调整 `MANUAL_ADJUST`

### 预留业务类型
- 销售扣款 `SALE_CONSUME`
- 退款返还 `REFUND`
- 开卡赠送 `OPEN_GIFT`

## 5.3 积分流水设计
积分流水表 `member_point_flow` 记录以下核心字段：
- `member_id`
- `change_type`
- `biz_type`
- `biz_no`
- `before_points`
- `change_points`
- `after_points`
- `operator_id`
- `operator_name`
- `source`
- `remark`
- `create_time`
- `is_deleted`

### 当前已覆盖的积分业务类型
- 消费赠分 `CONSUME_EARN`
- 人工调整 `MANUAL_ADJUST`

### 预留业务类型
- 积分抵扣 `POINT_DEDUCT`
- 活动赠送 `ACTIVITY_GIFT`
- 退款回滚 `REFUND_ROLLBACK`
- 过期扣减 `EXPIRE`

## 5.4 当前业务规则
### 余额规则
- 充值金额必须大于 0
- 调整后余额不能为负
- 所有余额变动必须有来源说明
- 所有余额变动必须生成流水记录

### 积分规则
- 积分调整必须留痕
- 积分变动必须说明来源
- 调整后积分不能为负
- 销售赠分基于当前订单实收金额整数部分

---

## 六、销售管理模块设计

## 6.1 收银台目标
当前收银台的目标是做一个最小可演示的销售端，而不是复杂 POS 系统。其最小闭环包括：
- 搜索商品
- 加入购物车
- 调整数量
- 绑定会员
- 选择支付方式
- 模拟支付
- 完成立即结算
- 返回订单号

## 6.2 当前收银端页面能力
页面文件：`supermarket-frontend/src/views/sale/CashierView.vue`

### 已实现功能
- 按商品名称搜索商品
- 仅展示有库存商品
- 购物车数量调整
- 库存不足前端提示
- 会员快速查询与绑定
- 选择支付方式（现金 / 微信 / 支付宝）
- 自动同步应收金额与实收金额
- 点击“立即结算”完成模拟支付
- 成功后显示订单号
- 成功后清空购物车并刷新商品列表

## 6.3 模拟支付说明
当前项目明确不接入真实支付，只保留如下逻辑：
- 录入订单
- 设置支付方式
- 点击结算即视为支付成功
- 订单状态记为已支付

这符合毕业设计项目“可演示、可闭环、低复杂度”的定位。

---

## 七、会员与销售联动设计

## 7.1 联动目标
会员模块与销售模块的联动目标，是让收银订单可以绑定会员，并在订单完成后更新会员相关统计与积分信息。

## 7.2 当前已实现联动流程
### 1. 收银台绑定会员
前端通过 `GET /member/simple` 支持按以下任一方式快速查询：
- 手机号
- 会员卡号
- 会员编号

### 2. 销售订单写入会员快照
`sale_order` 当前已落地以下会员快照字段：
- `member_id`
- `member_no`
- `member_name`
- `member_phone`
- `point_earned`
- `point_deducted`
- `point_deduct_amount`

这样做的好处是：
1. 后续即使会员主档修改姓名、手机号，历史订单仍能保留当时快照。
2. 支持销售记录页直接展示会员信息。
3. 为后续积分抵扣、等级快照、退款回滚预留结构。

### 3. 销售成功后更新会员累计消费
当前结算成功后，会更新会员主表中的：
- `total_consume_amount`
- `total_consume_count`
- `last_consume_time`

### 4. 销售成功后赠送积分
当前 `SaleServiceImpl.checkout` 已实现：
- 以实收金额整数部分作为赠送积分
- 调用积分服务进行积分调整
- 自动生成积分流水
- 在订单上回写 `point_earned`

## 7.3 当前未实现但已预留的联动能力
- 余额支付并写入余额扣减流水
- 积分抵扣金额并回写订单抵扣字段
- 会员等级快照写入销售单
- 退款后回滚积分与余额
- 根据等级倍率自动计算积分

---

## 八、数据库设计说明

## 8.1 会员相关表
### 1. `member_level`
会员等级表，用于后续扩展：
- 等级编码
- 等级名称
- 升级门槛
- 积分倍率
- 折扣倍率
- 状态

### 2. `member`
会员主表，保存会员当前状态快照与统计字段。

### 3. `member_balance_flow`
会员余额流水表，用于记录充值、扣款、退款、人工调整等变动。

### 4. `member_point_flow`
会员积分流水表，用于记录消费赠分、积分抵扣、活动赠送、人工调整等变动。

## 8.2 销售相关表
### 1. `sale_order`
销售主单表，当前关键字段包括：
- `order_no`
- `total_amount`
- `real_amount`
- `payment_type`
- `cashier_id`
- `member_id`
- `member_no`
- `member_name`
- `member_phone`
- `point_earned`
- `point_deducted`
- `point_deduct_amount`
- `status`
- `remark`
- `create_time`
- `is_deleted`

### 2. `sale_detail`
销售明细表，保存商品快照、单价、数量、小计金额。

## 8.3 安全增量升级脚本
如果数据库中已经存在旧版 `sale_order` 表，不建议直接重建表。

当前仓库已提供安全增量脚本：
- `supermarket-database/sql/04_sales_upgrade_safe.sql`

其作用是：
- 不删除原有表结构
- 不删除历史销售数据
- 仅补充会员联动与积分相关字段
- 补充 `member_id` 索引

---

## 九、后端接口设计

## 9.1 会员接口
### 会员主档
| 接口 | 方法 | 说明 |
|---|---|---|
| `/member/page` | GET | 分页查询会员列表 |
| `/member/{id}` | GET | 查询会员详情 |
| `/member` | POST | 新增会员 |
| `/member` | PUT | 修改会员 |
| `/member/{id}` | DELETE | 删除会员（逻辑删除） |
| `/member/batch` | DELETE | 批量删除会员 |
| `/member/simple` | GET | 收银台快速查询会员 |

### 余额接口
| 接口 | 方法 | 说明 |
|---|---|---|
| `/member/balance/recharge` | POST | 会员充值 |
| `/member/balance/adjust` | POST | 余额人工调整 |
| `/member/balance/flow/page` | GET | 分页查询余额流水 |

### 积分接口
| 接口 | 方法 | 说明 |
|---|---|---|
| `/member/point/adjust` | POST | 积分人工调整 |
| `/member/point/flow/page` | GET | 分页查询积分流水 |

## 9.2 销售接口
| 接口 | 方法 | 说明 |
|---|---|---|
| `/sale/checkout` | POST | 收银台结算 |
| `/sale/page` | GET | 分页查询销售订单 |
| `/sale/{id}` | GET | 查询销售订单详情 |

## 9.3 接口统一风格
当前接口统一采用：
- `Result<T>` 返回结构
- JSON 请求体
- JWT 登录态访问
- 业务错误使用统一异常返回

---

## 十、前端页面与路由设计

## 10.1 已落地路由
- `/member/list`：会员管理
- `/sale/cashier`：收银台
- `/sale/order-list`：销售记录

## 10.2 会员管理页面
页面文件：`supermarket-frontend/src/views/member/MemberListView.vue`

### 页面能力
- 列表查询
- 条件筛选
- 新增弹窗
- 编辑弹窗
- 详情弹窗
- 批量删除
- 会员充值弹窗
- 余额调整弹窗
- 余额流水弹窗
- 积分调整弹窗
- 积分流水弹窗

### 风格说明
前端保持现有项目统一风格：
- Element Plus 列表页
- 对话框表单
- API 单独封装
- 列表 + 弹窗 + 分页 的后台管理模式

## 10.3 销售记录页面
页面文件：`supermarket-frontend/src/views/sale/SaleOrderListView.vue`

### 已实现能力
- 分页查询销售记录
- 订单号筛选
- 会员姓名筛选
- 支付方式筛选
- 时间范围筛选
- 查看订单详情
- 展示会员姓名与手机号
- 展示本单积分
- 顶部统计卡片

### 统计卡片当前口径
当前页面顶部统计卡片以“当前页数据”为口径，主要展示：
- 订单总数
- 当前页销售额
- 会员订单数
- 当前页发放积分

该口径适合当前页面轻量展示，若后续需要全局统计，可再新增聚合接口。

---

## 十一、最小验收清单

## 11.1 会员模块验收
- 可以新增会员
- 可以按手机号 / 会员编号 / 卡号 / 姓名检索会员
- 可以查看会员详情
- 可以编辑会员资料
- 可以逻辑删除会员
- 手机号重复时不能新增成功
- 会员卡号重复时不能新增成功

## 11.2 余额与积分验收
- 会员充值后余额增加
- 余额调整后主表余额与流水一致
- 调整后余额不能为负
- 积分调整后主表积分与流水一致
- 积分调整必须有来源说明
- 余额与积分流水均可分页查询

## 11.3 销售模块验收
- 收银台可搜索商品
- 商品可加入购物车
- 商品库存不足时不能超量结算
- 点击“立即结算”可模拟支付成功
- 销售主单与明细可正常落库
- 商品库存能正确扣减
- 销售记录页可按条件查询
- 销售详情可查看商品明细

## 11.4 会员销售联动验收
- 收银台可绑定会员
- 销售订单可保存会员快照
- 会员消费后累计消费金额与次数更新
- 会员消费后自动赠送积分
- 赠分后积分流水可追踪
- 销售记录中可查看会员姓名、手机号、本单积分

---

## 十二、风险点与边界说明

### 12.1 当前已处理的边界
1. 会员不存在时不能绑定销售订单。
2. 停用会员不能绑定到销售订单。
3. 购物车为空时不能结算。
4. 商品数量必须大于 0。
5. 商品库存不足时不能结算。
6. 实收金额必须大于 0。
7. 余额不能被调整成负数。
8. 删除会员前需要关注历史销售数据，当前采用逻辑删除规避物理删除风险。

### 12.2 当前仍需后续补强的点
1. 退款逆向流程尚未完善。
2. `cashier_id` 仍未对接真实登录用户。
3. 余额支付尚未接入销售扣款流程。
4. 积分抵扣尚未参与订单结算。
5. 会员等级规则与等级分析尚未启用。

---

## 十三、后续规划

### 短期规划
- 完善会员基础管理剩余细节与更多校验提示
- 将会员模块任务状态从“进行中”推进到“已完成”
- 为销售记录页增加更多汇总统计口径

### 中期规划
- 接入积分抵扣逻辑
- 接入余额支付逻辑
- 为销售单增加会员等级快照
- 退款时回滚库存、余额、积分
- 将 `cashier_id` 对接当前登录收银员

### 长期规划
- 会员等级自动计算
- 会员营销玩法（问卷、抽奖、活动赠分）
- 会员复购率、会员贡献度、等级分布分析
- 销售与会员联动专题报表

---

## 十四、关联文件清单

### 文档
- `supermarket-docs/API_DOC.md`
- `supermarket-docs/TASK_LIST.md`
- `supermarket-docs/FRONTEND_PROGRESS.md`
- `supermarket-docs/DASHBOARD_DOC.md`
- `README.md`

### 数据库
- `supermarket-database/sql/05_member.sql`
- `supermarket-database/sql/04_sales.sql`
- `supermarket-database/sql/04_sales_upgrade_safe.sql`

### 后端
- `supermarket-backend/src/main/java/com/supermarket/member/controller/MemberController.java`
- `supermarket-backend/src/main/java/com/supermarket/member/controller/MemberBalanceController.java`
- `supermarket-backend/src/main/java/com/supermarket/member/controller/MemberPointController.java`
- `supermarket-backend/src/main/java/com/supermarket/sale/controller/SaleController.java`
- `supermarket-backend/src/main/java/com/supermarket/sale/service/impl/SaleServiceImpl.java`

### 前端
- `supermarket-frontend/src/api/member.js`
- `supermarket-frontend/src/api/sale.js`
- `supermarket-frontend/src/views/member/MemberListView.vue`
- `supermarket-frontend/src/views/sale/CashierView.vue`
- `supermarket-frontend/src/views/sale/SaleOrderListView.vue`
- `supermarket-frontend/src/router/index.js`

---

## 十五、结论

当前仓库中的会员管理与销售管理，已经形成一个较完整的首期闭环：

- 会员主档可管理
- 余额与积分具备流水审计能力
- 收银台可模拟支付完成结算
- 销售记录可查询与查看详情
- 会员可与销售订单绑定
- 销售后可累计消费并自动赠分

从毕业设计角度看，这套实现已经具备：
1. 明确的业务闭环
2. 可展示的前后端页面
3. 可追溯的数据结构
4. 可扩展的会员体系基础

后续若继续迭代，建议按照以下顺序推进：

**先积分抵扣，再余额支付，再退款回滚，再会员等级规则。**
