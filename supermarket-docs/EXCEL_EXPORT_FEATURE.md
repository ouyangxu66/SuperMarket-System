# Excel 报表导出功能实现文档

## 1. 概述
本项目已使用 **Alibaba EasyExcel** 完成了核心业务模块的数据导出功能，实现了从后端流式写入到前端无刷新下载的全链路打通。

## 2. 功能清单
支持以下四个板块的 Excel 导出：
1. **首页看板 (Dashboard)**: 导出近期销售趋势统计数据（日期、销售额、订单数、客单价）。
2. **销售管理 (Sale)**: 导出销售流水记录（订单号、创建时间、会员姓名、总金额、支付方式）。
3. **商品管理 (Product)**: 导出商品档案数据（商品名称、条码、分类、进价、售价、状态）。
4. **库存管理 (Inventory)**: 导出库存清单（商品名称、条码、当前库存、预警阈值）。

## 3. 技术方案

### 3.1 后端技术栈
- **核心组件**: `com.alibaba:easyexcel` (3.3.4)
- **响应模式**: HTTP Response Stream (流式输出，有效降低大文件导出的内存占用)
- **文件名编码**: `URLEncoder` (UTF-8) + `StandardCharsets.UTF_8` (防止中文文件名乱码)
- **API 规范**: 
    - 接口不返回 JSON 包装类 `Result<T>`，而是直接返回 `void`。
    - 直接将 Excel 二进制流写入 `HttpServletResponse` 的 `OutputStream`。

### 3.2 前端技术栈
- **网络层**: Axios + 响应拦截器改造
- **数据流**: `Blob` 对象处理 (`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`)
- **下载交互**: 动态创建 HTML5 `<a>` 标签 + `URL.createObjectURL` 触发浏览器下载。

## 4. 详细实现细节

### 4.1 后端实现步聚
1. **依赖配置**: 在 `pom.xml` 中引入 EasyExcel 依赖。
2. **定义导出模型 (ExcelVO)**:
   - 为每个业务模块创建了专用的 VO 类 (如 `SaleOrderExcelVO`)。
   - 使用 `@ExcelProperty` 注解自定义 Excel 表头名称。
   - 使用 `@ColumnWidth` 控制列宽。
   - **设计原则**: 将数据库 Entity 转换为 ExcelVO，只暴露业务需要的字段，进行必要的数据格式化（如状态映射为文本）。
3. **Service 层逻辑**:
   - 实现了查询数据并将 Entity 映射为 ExcelVO 的逻辑。
   - 调用 `EasyExcel.write(response.getOutputStream(), Clazz).sheet("SheetName").doWrite(dataList)` 完成写入。
4. **Controller 层接口**:
   - 新增 `/export` 端点。
   - 设置正确的响应头：
     - `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
     - `Content-Disposition: attachment;filename=xxx.xlsx`

### 4.2 前端实现步聚
1. **网络拦截器 (`src/utils/request.js`)**:
   - 修改了 Axios 响应拦截器，透传 `responseType === 'blob'` 的响应，跳过 `code !== 200` 的业务错误检查。
2. **API 封装**:
   - 在 `src/api` 下的相关模块文件中添加导出方法，明确指定 `responseType: 'blob'`。
3. **Vue 视图交互**:
   - 在各列表页面或看板页面的工具栏添加“导出 Excel”按钮。
   - 点击按钮触发 API 调用，成功后通过 Blob 触发文件下载，失败则提示错误信息。

## 5. 涉及文件列表

### 后端文件 (Modified & Created)
*   **配置**: `pom.xml`
*   **Dashboard 模块**:
    *   `src/main/java/com/supermarket/dashboard/vo/DashboardSalesExcelVO.java` (Created)
    *   `src/main/java/com/supermarket/dashboard/service/DashboardService.java`
    *   `src/main/java/com/supermarket/dashboard/service/impl/DashboardServiceImpl.java`
    *   `src/main/java/com/supermarket/dashboard/controller/DashboardController.java`
*   **Sale 模块**:
    *   `src/main/java/com/supermarket/sale/vo/SaleOrderExcelVO.java` (Created)
    *   `src/main/java/com/supermarket/sale/controller/SaleController.java`
*   **Product 模块**:
    *   `src/main/java/com/supermarket/product/vo/ProductExcelVO.java` (Created)
    *   `src/main/java/com/supermarket/product/service/ProductService.java`
    *   `src/main/java/com/supermarket/product/service/impl/ProductServiceImpl.java`
    *   `src/main/java/com/supermarket/product/controller/ProductController.java`
*   **Inventory 模块**:
    *   `src/main/java/com/supermarket/inventory/vo/InventoryExcelVO.java` (Created)
    *   `src/main/java/com/supermarket/inventory/service/InventoryService.java`
    *   `src/main/java/com/supermarket/inventory/service/impl/InventoryServiceImpl.java`
    *   `src/main/java/com/supermarket/inventory/controller/InventoryController.java`

### 前端文件 (Modified)
*   **工具类**: `src/utils/request.js`
*   **API**:
    *   `src/api/dashboard.js`
    *   `src/api/sale.js`
    *   `src/api/product.js`
    *   `src/api/inventory.js`
*   **Views**:
    *   `src/views/dashboard/DashboardView.vue`
    *   `src/views/sale/SaleOrderListView.vue`
    *   `src/views/product/ProductListView.vue`
    *   `src/views/inventory/InventoryListView.vue`
