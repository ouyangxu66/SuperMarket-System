<div align="center">

# 🛒 超市管理系统

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7+-green?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-brightgreen?style=for-the-badge&logo=vue.js)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/github/license/your-username/supermarket-system?style=for-the-badge)](LICENSE)

> 🔧 基于Spring Boot + Vue.js的现代化超市管理系统，专为中小型超市设计
> 
> 💡 **新手友好** | 🚀 **快速上手** | 📚 **详细文档**

</div>

## 📚 目录

- [项目概述](#项目概述)
- [核心特性](#核心特性)
- [功能模块](#功能模块)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [新手入门](#新手入门)
  - [环境准备](#环境准备)
  - [快速安装](#快速安装)
  - [项目启动](#项目启动)
  - [常见问题](#常见问题)
- [使用指南](#使用指南)
- [API接口](#api接口)
- [项目结构](#项目结构)
- [业务流程](#业务流程)
- [代码规范](#代码规范)
- [扩展功能](#扩展功能)
- [项目亮点](#项目亮点)
- [联系方式](#联系方式)

## 📋 项目概述

**超市管理系统**是一个现代化的全栈Web应用，采用前后端分离架构设计。系统旨在帮助中小型超市实现数字化管理，涵盖商品管理、库存管理、销售管理、用户权限等核心业务流程。

### ✨ 核心特性

- 🏪 **模块化设计** - 按业务模块划分，易于维护和扩展
- 🔐 **安全认证** - JWT无状态认证，支持角色权限管理
- 📊 **数据驱动** - 基于MySQL的数据存储，支持复杂查询
- ⚡ **高性能** - MyBatis-Plus增强ORM，提升数据访问效率
- 📱 **响应式设计** - 适配不同屏幕尺寸的设备
- 🚀 **实时预警** - 库存预警、临期商品检测等智能提醒

## 🎯 功能模块

### 1. 🛡️ 用户与权限管理
- ✅ **用户认证** - 登录/登出、JWT Token验证
- ✅ **角色管理** - 店长、收银员等多角色权限控制
- ✅ **用户管理** - 用户信息增删改查、密码加密存储
- ✅ **权限控制** - 细粒度的接口访问权限管理

### 2. 📦 商品管理
- ✅ **分类管理** - 支持多级商品分类，树形结构展示
- ✅ **商品档案** - 商品信息完整管理（名称、条码、价格等）
- ✅ **条码管理** - 唯一性校验，支持扫码枪操作
- ✅ **状态管理** - 商品上下架状态控制

### 3. 📉 库存管理
- ✅ **库存预警** - 当库存数量低于预设阈值时自动预警
- ✅ **临期检测** - 基于商品过期日期的智能预警机制
- ✅ **库存盘点** - 支持库存盘点和差异管理
- ✅ **进货管理** - 供应商管理、进货单处理
- ✅ **库存追踪** - 完整的库存变动记录

### 4. 👥 会员管理
- ✅ **会员档案** - 支持会员基础信息增删改查、分页检索、逻辑删除
- ✅ **余额体系** - 支持会员储值、余额调整、余额流水追踪
- ✅ **积分体系** - 支持积分人工调整、消费赠分、积分流水追踪
- ✅ **销售联动** - 销售订单支持绑定会员，自动回写会员累计消费与积分
- ✅ **扩展预留** - 已预留会员等级、积分抵扣、Dashboard 统计字段扩展能力

### 5. 💰 销售管理
- ✅ **收银台** - 已实现最小可用收银端，支持商品搜索、购物车、模拟支付
- ✅ **会员收银联动** - 收银台支持快速绑定会员，并在结算后自动赠送积分
- ✅ **销售记录** - 支持销售流水分页、订单详情、商品明细查看
- ✅ **筛选统计** - 支持按订单号、会员姓名、支付方式、时间范围筛选，并展示顶部统计卡片
- ✅ **模拟支付** - 毕业设计场景下采用“点击立即结算即支付成功”的模拟支付方式

### 6. 🏭 供应商管理
- ✅ **供应商档案** - 供应商信息完整管理
- ✅ **合作追踪** - 供应商合作记录和评价

## 🛠️ 技术栈

### 后端技术栈
| 技术 | 版本 | 说明 | 学习资源 |
|------|------|------|----------|
| **Spring Boot** | 2.7+ | 核心框架 | [官方文档](https://spring.io/projects/spring-boot) |
| **Spring Security** | 2.7+ | 安全框架 | [官方文档](https://spring.io/projects/spring-security) |
| **JWT** | 0.11+ | 令牌认证 | [JWT官网](https://jwt.io/) |
| **MyBatis-Plus** | 3.5+ | 持久层框架 | [官方文档](https://baomidou.com/) |
| **MySQL** | 8.0+ | 数据库 | [官方文档](https://dev.mysql.com/doc/) |
| **Maven** | 3.6+ | 项目构建 | [官方文档](https://maven.apache.org/) |

### 前端技术栈
| 技术 | 版本 | 说明 | 学习资源 |
|------|------|------|----------|
| **Vue.js** | 3.x | 前端框架 | [官方文档](https://vuejs.org/) |
| **Element Plus** | 2.x | UI组件库 | [官方文档](https://element-plus.org/) |
| **Axios** | 1.x | HTTP客户端 | [官方文档](https://axios-http.com/) |
| **Vite** | 4.x | 构建工具 | [官方文档](https://vitejs.dev/) |

### 开发工具
- **IDE**: IntelliJ IDEA / VS Code
- **数据库工具**: MySQL Workbench / Navicat
- **API测试**: Postman / Insomnia
- **版本控制**: Git
- **操作系统**: Windows 10+/macOS/Linux

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │   Vue.js    │  │  Element    │  │   Axios     │        │
│  │             │  │   Plus      │  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                        后端层                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  Spring     │  │ MyBatis-    │  │   JWT       │        │
│  │  Boot       │  │   Plus      │  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                        数据层                               │
│                    ┌─────────────┐                         │
│                    │   MySQL     │                         │
│                    │  InnoDB     │                         │
│                    └─────────────┘                         │
└─────────────────────────────────────────────────────────────┘
```

## 👋 新手入门

### 环境准备

在开始之前，请确保您的系统已安装以下软件：

#### 🖥️ 后端环境
```bash
# 1. 安装 Java 8+ (推荐 OpenJDK 11+)
# Windows: https://adoptium.net/
# macOS: brew install openjdk
# Linux: sudo apt install openjdk-11-jdk

# 2. 安装 Maven 3.6+
# Windows: https://maven.apache.org/download.cgi
# macOS: brew install maven
# Linux: sudo apt install maven

# 3. 安装 MySQL 8.0+
# 下载地址: https://dev.mysql.com/downloads/mysql/
```

#### 🌐 前端环境
```bash
# 1. 安装 Node.js 16+
# 下载地址: https://nodejs.org/
# 推荐使用 LTS 版本
```

#### 🛠️ 开发工具
- **IDE**: IntelliJ IDEA（推荐）或 VS Code
- **数据库管理**: MySQL Workbench 或 Navicat
- **API测试**: Postman（可选，用于接口测试）

### 🚀 快速安装

#### 1. 克隆项目

```bash
# 打开命令行工具
git clone <repository-url>
cd Supermarket-System
```

**💡 小贴士**: 如果没有安装 Git，可以直接下载 ZIP 压缩包解压。

#### 2. 数据库配置

**步骤一：创建数据库**
```sql
-- 连接到 MySQL
-- 执行以下 SQL 语句创建数据库
CREATE DATABASE supermarket_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**步骤二：执行数据库脚本**
```bash
# Windows 用户
cd supermarket-database\sql
mysql -u root -p supermarket_db < 01_user_role.sql
mysql -u root -p supermarket_db < 02_product.sql
mysql -u root -p supermarket_db < 03_inventory.sql
mysql -u root -p supermarket_db < 04_sales.sql
mysql -u root -p supermarket_db < 05_member.sql
```

```bash
# Linux/macOS 用户
cd supermarket-database/sql
mysql -u root -p supermarket_db < 01_user_role.sql
mysql -u root -p supermarket_db < 02_product.sql
mysql -u root -p supermarket_db < 03_inventory.sql
mysql -u root -p supermarket_db < 04_sales.sql
mysql -u root -p supermarket_db < 05_member.sql
```

**💡 重要提示**: 
- 执行脚本前请确保 MySQL 服务已启动
- `supermarket_db` 是数据库名，可根据需要修改
- 执行顺序不能颠倒，因为有表关联关系
- 如果数据库中已经存在 `sale_order` / `sale_detail` 历史数据，请不要直接重跑 `04_sales.sql`
- 已提供安全增量脚本 `supermarket-database/sql/04_sales_upgrade_safe.sql`，可用于仅补齐销售会员联动字段而不删除原有销售表数据

#### 3. 后端配置

**步骤一：修改数据库连接信息**
```yaml
# 打开文件: supermarket-backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/supermarket_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root              # 修改为你的数据库用户名
    password: password          # 修改为你的数据库密码
```

**步骤二：启动后端服务**
```bash
cd supermarket-backend
mvn spring-boot:run
```

或者：
- 打开 IntelliJ IDEA
- 导入项目
- 运行 [SupermarketApplication](file:///D:/Supermarket-System/supermarket-backend/src/main/java/com/supermarket/SupermarketApplication.java#L7-L14) 类

### 🎉 项目启动

#### 启动后端服务
```bash
# 在 supermarket-backend 目录下执行
mvn spring-boot:run
```

服务启动后，访问 `http://localhost:8080` 应该能看到 Spring Boot 的欢迎页面。

#### 启动前端服务
```bash
cd supermarket-frontend  # 确保前端项目在此目录
npm install              # 安装依赖
npm run dev              # 启动开发服务器
```

前端启动后，访问 `http://localhost:5173` 即可使用系统。

### ❓ 常见问题

#### 1. 数据库连接失败
**问题**: `Access denied for user 'root'@'localhost'`
**解决**: 检查 `application.yml` 中的用户名和密码是否正确

#### 2. 端口被占用
**问题**: `Address already in use`
**解决**: 修改 `application.yml` 中的端口号
```yaml
server:
  port: 8081  # 改为其他可用端口
```

#### 3. Maven 依赖下载失败
**问题**: `Could not transfer artifact`
**解决**: 检查网络连接或配置 Maven 镜像源

#### 4. 前端启动失败
**问题**: `npm install` 报错
**解决**: 
- 确保 Node.js 版本 >= 16
- 清除 npm 缓存: `npm cache clean --force`
- 重新安装: `npm install`

### 🎯 默认账号

系统预设了管理员账号，启动后可直接登录：

```bash
# 默认管理员账号
用户名: admin
密码: 123456
```

## 📖 使用指南

### 登录系统
1. 打开浏览器，访问 `http://localhost:5173`
2. 输入用户名和密码
3. 点击登录按钮

### 主要功能操作
1. **商品管理**: 添加、编辑、删除商品信息
2. **库存管理**: 查看库存状态、设置预警阈值
3. **会员管理**: 管理会员档案、储值余额、积分流水
4. **销售管理**: 在收银台完成模拟结算、绑定会员、查看销售记录与订单详情
5. **用户管理**: 管理员工账号、设置权限

## 📡 API接口

### 认证接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `POST` | `/auth/login` | 用户登录 | `{username, password}` | `{token}` |
| `GET` | `/auth/info` | 获取当前用户信息 | - | `{userInfo}` |
| `POST` | `/auth/logout` | 用户登出 | - | `{success}` |

### 用户管理接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `GET` | `/user/page?pageNum=1&pageSize=10` | 分页查询用户 | 分页参数 | 用户列表 |
| `POST` | `/user` | 新增用户 | 用户信息 | 操作结果 |
| `PUT` | `/user` | 修改用户 | 用户信息 | 操作结果 |
| `DELETE` | `/user/{id}` | 删除用户 | 用户ID | 操作结果 |

### 商品管理接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `GET` | `/api/product/page` | 分页查询商品 | 分页参数 | 商品列表 |
| `POST` | `/api/product` | 新增商品 | 商品信息 | 操作结果 |
| `PUT` | `/api/product` | 修改商品 | 商品信息 | 操作结果 |
| `DELETE` | `/api/product/{id}` | 删除商品 | 商品ID | 操作结果 |
| `GET` | `/api/product/barcode/{barcode}` | 根据条码查询 | 条码号 | 商品信息 |

### 库存管理接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `GET` | `/inventory/low-stock` | 获取库存预警商品 | - | 预警商品列表 |
| `GET` | `/inventory/check-low-stock/{id}` | 检查单个商品库存 | 商品ID | 布尔值 |
| `GET` | `/api/product/expiring-soon` | 获取临期商品 | - | 临期商品列表 |
| `GET` | `/api/product/expired` | 获取过期商品 | - | 过期商品列表 |

### 销售管理接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `POST` | `/api/sale/checkout` | 收银台结算（支持绑定会员） | 销售信息、`memberId` | 订单号 |
| `GET` | `/api/sale/page` | 销售流水查询 | 分页参数 + 订单号/会员姓名/支付方式/时间范围 | 销售记录 |
| `GET` | `/api/sale/{id}` | 销售订单详情 | 订单ID | 主单 + 明细 |

### 会员管理接口
| 方法 | 接口 | 说明 | 参数 | 返回 |
|------|------|------|------|------|
| `GET` | `/member/page` | 分页查询会员 | 关键字/手机号/卡号/状态 | 会员列表 |
| `POST` | `/member` | 新增会员 | 会员信息 | 操作结果 |
| `PUT` | `/member` | 修改会员 | 会员信息 | 操作结果 |
| `DELETE` | `/member/{id}` | 删除会员（逻辑删除） | 会员ID | 操作结果 |
| `GET` | `/member/simple` | 收银台快速查会员 | 手机号/卡号/会员编号 | 会员简要信息 |
| `POST` | `/member/balance/recharge` | 会员充值 | 充值信息 | 操作结果 |
| `POST` | `/member/balance/adjust` | 余额调整 | 调整信息 | 操作结果 |
| `GET` | `/member/balance/flow/page` | 查询余额流水 | 分页参数 | 余额流水 |
| `POST` | `/member/point/adjust` | 积分调整 | 调整信息 | 操作结果 |
| `GET` | `/member/point/flow/page` | 查询积分流水 | 分页参数 | 积分流水 |

## 📊 项目结构

```
Supermarket-System/
├── supermarket-backend/          # 后端项目
│   ├── src/main/java/com/supermarket/
│   │   ├── auth/                # 认证模块
│   │   ├── common/              # 通用组件
│   │   ├── config/              # 配置类
│   │   ├── dashboard/           # 首页统计模块
│   │   ├── inventory/           # 库存模块
│   │   ├── member/              # 会员模块（主档/余额/积分）
│   │   ├── product/             # 商品模块
│   │   ├── sale/                # 销售模块（收银/销售记录/会员联动）
│   │   └── user/                # 用户模块
│   └── src/main/resources/
│       ├── application.yml      # 配置文件
│       └── mapper/              # MyBatis XML
├── supermarket-frontend/         # 前端项目
│   └── src/
│       ├── api/
│       │   ├── member.js        # 会员接口
│       │   ├── sale.js          # 销售接口
│       │   └── ...
│       └── views/
│           ├── dashboard/
│           ├── inventory/
│           ├── member/
│           │   └── MemberListView.vue
│           ├── product/
│           ├── sale/
│           │   ├── CashierView.vue
│           │   └── SaleOrderListView.vue
│           └── user/
├── supermarket-database/         # 数据库脚本
│   └── sql/
│       ├── 04_sales.sql
│       ├── 04_sales_upgrade_safe.sql
│       ├── 05_member.sql
│       └── ...
├── supermarket-docs/            # 项目文档
│   ├── API_DOC.md               # API文档
│   ├── FRONTEND_PROGRESS.md     # 前端进度文档
│   └── TASK_LIST.md             # 任务清单
└── README.md                    # 项目说明
```

## 🏭 业务流程

### 📦 商品入库流程
```
供应商 → 进货单 → 商品库存增加 → 库存记录更新 → 供应商结算
```

### 💰 销售流程
```
顾客选购商品 → 收银台添加购物车 → （可选）绑定会员 → 点击立即结算模拟支付成功
→ 扣减库存 → 生成销售主单/明细 → 回写会员累计消费 → 自动赠送积分
```

### 👥 会员消费联动流程
```
会员查询/绑定 → 提交销售订单 → 销售单保存会员快照 → 更新会员累计消费金额/次数/最后消费时间
→ 生成积分流水 → 会员当前积分增加
```

### ⚠️ 库存预警流程
```
系统定时检查 → 库存数量 ≤ 预警阈值 → 发送预警通知 → 管理员处理
```

## 🚧 扩展功能

### ✅ 已实现的扩展功能
- 📉 **库存预警机制** - 当库存低于阈值时自动提醒
- 📅 **临期商品检测** - 基于商品过期日期的预警
- 📋 **库存盘点功能** - 支持定期盘点和差异管理
- ⏱️ **自动化定时任务** - 定期执行库存检查
- 👥 **会员管理模块** - 会员档案、余额体系、积分体系、销售联动
- 🧾 **最小收银端** - 支持购物车、模拟支付、会员绑定、销售记录与订单详情查询

### 📝 待实现的功能
- 📊 **报表与数据分析** - 销售报表、库存报表
- 🛒 **采购计划管理** - 基于销售数据的采购建议
- 📞 **消息通知系统** - 短信、邮件通知
- 🎟️ **积分抵扣与退款回滚** - 进一步完善会员营销闭环

## 📈 项目亮点

### 🔍 技术亮点
- **架构设计** - 清晰的分层架构，易于维护
- **安全实现** - 完整的认证授权机制
- **性能优化** - MyBatis-Plus提升查询效率
- **代码质量** - 规范的注释和命名

### 🎯 业务亮点
- **智能预警** - 库存和临期商品自动提醒
- **完整流程** - 已打通从进货、库存、会员到销售收银的完整业务链
- **会员营销闭环** - 支持会员绑定销售、自动赠分、余额与积分流水留痕
- **销售管理可视化** - 收银台、销售记录、订单详情、统计卡片一体化展示
- **数据安全** - 逻辑删除和数据备份机制

## 📬 联系方式

如需继续完善本项目，可优先参考以下文档：
- `supermarket-docs/API_DOC.md`：接口清单与示例
- `supermarket-docs/TASK_LIST.md`：任务规划与开发优先级
- `supermarket-docs/FRONTEND_PROGRESS.md`：前端页面实现进度

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持我们！**

**🎯 适合学习 Spring Boot、Vue.js 的初学者，欢迎 Star 和 Fork！**

</div>

