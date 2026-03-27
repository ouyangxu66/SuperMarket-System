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
    url: '/dashboard/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
