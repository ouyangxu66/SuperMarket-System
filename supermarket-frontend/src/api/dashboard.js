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
