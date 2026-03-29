package com.supermarket.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermarket.product.dto.ProductFormDTO;
import com.supermarket.product.entity.Product;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import java.util.List;

/**
 * 商品服务接口
 * 负责商品的增删改查等功能
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品
     * @param pageNum 页码
     * @param pageSize 条数
     * @param name 商品名 (模糊)
     * @param categoryId 分类ID (精确)
     * @param status 状态 (精确)
     * @return 分页结果
     */
    IPage<Product> queryPage(int pageNum, int pageSize, String name, Long categoryId, Integer status);

    /**
     * 新增商品 (包含条码校验)
     */
    void addProduct(ProductFormDTO dto);

    /**
     * 修改商品
     */
    void updateProduct(ProductFormDTO dto);
    
    /**
     * 修改商品状态 (上架/下架)
     */
    void updateStatus(Long id, Integer status);
    
    /**
     * 获取即将过期的商品列表
     * @param expiringDate 到期日期
     * @return 即将过期的商品列表
     */
    List<Product> getExpiringSoonProducts(Date expiringDate);
    
    /**
     * 获取已过期的商品列表
     * @return 已过期的商品列表
     */
    List<Product> getExpiredProducts();

    /**
     * 导出商品列表
     */
    void export(HttpServletResponse response, String name, Long categoryId, Integer status);

    /**
     * 导出商品导入模板
     */
    void downloadImportTemplate(HttpServletResponse response);

    /**
     * 批量导入商品
     * @param file 传过来的excel文件
     * @return 返回结果信息：成功导入x条，失败y条等
     */
    String importProduct(MultipartFile file);

    /**
     * 物理删除商品
     */
    void physicalDeleteProduct(Long id);
}