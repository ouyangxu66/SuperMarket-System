import request from '@/utils/request'

/**
 * 库存管理模块
 */

// 获取库存预警商品列表
export function getLowStockList() {
  return request({
    url: '/inventory/low-stock',
    method: 'get'
  })
}

// 检查单个商品是否库存不足
export function checkLowStock(id) {
  return request({
    url: `/inventory/check-low-stock/${id}`,
    method: 'get'
  })
}

// 获取库存详情
export function getInventoryDetail(id) {
  return request({
    url: `/inventory/detail/${id}`,
    method: 'get'
  })
}

/**
 * 库存盘点模块
 */

// 分页查询盘点列表
export function getInventoryCountPage(query) {
  return request({
    url: '/inventory/count/page',
    method: 'get',
    params: query
  })
}

// 创建盘点任务
export function addInventoryCount(data) {
  return request({
    url: '/inventory/count',
    method: 'post',
    data
  })
}

// 开始盘点
export function startInventoryCount(id) {
  return request({
    url: `/inventory/count/${id}/start`,
    method: 'put'
  })
}

// 完成盘点
export function completeInventoryCount(id) {
  return request({
    url: `/inventory/count/${id}/complete`,
    method: 'put'
  })
}

// 获取盘点详情列表
export function getInventoryCountDetails(countId) {
  return request({
    url: `/inventory/count/${countId}/details`,
    method: 'get'
  })
}

// 添加盘点详情
export function addInventoryCountDetail(countId, data) {
  return request({
    url: `/inventory/count/${countId}/detail`,
    method: 'post',
    data
  })
}
