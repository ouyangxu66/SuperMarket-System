import request from '@/utils/request'

/**
 * Dashboard 数据看板 API
 */
export function getDashboardOverview(params) {
  return request({
    url: '/dashboard/overview',
    method: 'get',
    params
  })
}

/**
 * 导出销售概览
 */
export function exportDashboardSales(params) {
  return request({
    url: '/dashboard/export-sales',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出热销商品排行统计
 */
export function exportDashboardHotProducts(params) {
  return request({
    url: '/dashboard/export-hot-products',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
