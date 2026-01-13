package com.supermarket.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermarket.inventory.entity.Supplier;
import com.supermarket.inventory.mapper.SupplierMapper;
import com.supermarket.inventory.service.SupplierService;
import org.springframework.stereotype.Service;

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {
    // 基础 CRUD 由 MyBatis-Plus 提供，暂无复杂业务逻辑
}