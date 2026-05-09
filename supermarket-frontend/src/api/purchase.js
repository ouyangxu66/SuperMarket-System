import request from '@/utils/request'

/**
 * 采购管理模块 API
 */

// 分页查询采购单列表
export function getPurchasePage(query) {
    return request({
        url: '/store/purchase/page',
        method: 'get',
        params: query
    })
}

// 创建采购单
export function createPurchase(data) {
    return request({
        url: '/store/purchase',
        method: 'post',
        data
    })
}

// 审核采购单
export function auditPurchase(id) {
    return request({
        url: `/store/purchase/${id}/audit`,
        method: 'post'
    })
}

// 作废采购单
export function voidPurchase(id) {
    return request({
        url: `/store/purchase/${id}/void`,
        method: 'post'
    })
}

// 获取供应商列表
export function getSupplierList() {
    return request({
        url: '/inventory/supplier/list',
        method: 'get'
    })
}

// 获取商品列表（用于采购选择）
export function getProductList(query) {
    return request({
        url: '/product/list',
        method: 'get',
        params: query
    })
}
