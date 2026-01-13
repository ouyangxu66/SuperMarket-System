package com.supermarket.inventory.scheduler;

import com.supermarket.inventory.service.InventoryService;
import com.supermarket.product.entity.Product;
import com.supermarket.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 库存预警和临期商品检测定时任务
 * 定期检查库存情况和临期商品，进行预警
 */
@Component
public class InventoryAlertScheduler {

    private static final Logger logger = LoggerFactory.getLogger(InventoryAlertScheduler.class);

    private final InventoryService inventoryService;
    private final ProductService productService;

    // 构造器注入
    public InventoryAlertScheduler(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    /**
     * 定时检查库存预警
     * 每隔5分钟执行一次（可根据实际需求调整）
     * cron表达式：秒 分 时 日 月 周
     * 以下配置表示每5分钟执行一次（在每小时的第0分、第5分、第10分...执行）
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkLowStockAlert() {
        logger.info("开始执行库存预警检查...");
        
        try {
            // 获取库存不足的商品列表
            List<Product> lowStockProducts = inventoryService.getLowStockProducts();
            
            if (lowStockProducts.isEmpty()) {
                logger.info("库存检查完成，暂无库存不足商品");
            } else {
                logger.warn("发现 {} 个商品库存不足:", lowStockProducts.size());
                for (Product product : lowStockProducts) {
                    logger.warn("商品名称: {}, 商品条码: {}, 当前库存: {}, 预警阈值: {}", 
                               product.getName(), 
                               product.getBarcode(), 
                               product.getStock(), 
                               product.getLowStockThreshold());
                }
                
                // TODO: 在这里可以添加发送通知的逻辑，比如邮件、短信或系统内消息
                // 例如：sendLowStockNotification(lowStockProducts);
            }
        } catch (Exception e) {
            logger.error("库存预警检查过程中发生异常: ", e);
        }
        
        logger.info("库存预警检查完成");
    }

    /**
     * 定时检查临期商品
     * 每天凌晨2点执行（可根据实际需求调整）
     * 检查7天内即将过期的商品
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void checkExpiringSoonAlert() {
        logger.info("开始执行临期商品检测...");
        
        try {
            // 获取7天内即将过期的商品（基于商品表中的到期日期）
            List<Product> expiringSoonProducts = getExpiringSoonProducts(7);
            
            if (expiringSoonProducts.isEmpty()) {
                logger.info("临期商品检测完成，暂无临期商品");
            } else {
                logger.warn("发现 {} 个商品即将在7天内过期:", expiringSoonProducts.size());
                for (Product product : expiringSoonProducts) {
                    logger.warn("商品名称: {}, 商品条码: {}, 到期日期: {}, 库存数量: {}", 
                               product.getName(), 
                               product.getBarcode(),
                               product.getEarliestExpirationDate(),
                               product.getStock());
                }
                
                // TODO: 在这里可以添加发送通知的逻辑，比如邮件、短信或系统内消息
                // 例如：sendExpiringSoonNotification(expiringSoonProducts);
            }
        } catch (Exception e) {
            logger.error("临期商品检测过程中发生异常: ", e);
        }
        
        logger.info("临期商品检测完成");
    }

    /**
     * 定时检查已过期商品
     * 每周日凌晨3点执行（可根据实际需求调整）
     */
    @Scheduled(cron = "0 0 3 * * 0") // 每周日凌晨3点执行
    public void checkExpiredAlert() {
        logger.info("开始执行已过期商品检测...");
        
        try {
            // 获取已过期的商品
            List<Product> expiredProducts = getExpiredProducts();
            
            if (expiredProducts.isEmpty()) {
                logger.info("已过期商品检测完成，暂无过期商品");
            } else {
                logger.warn("发现 {} 个商品已过期:", expiredProducts.size());
                for (Product product : expiredProducts) {
                    logger.warn("商品名称: {}, 商品条码: {}, 到期日期: {}, 库存数量: {}", 
                               product.getName(),
                               product.getBarcode(),
                               product.getEarliestExpirationDate(),
                               product.getStock());
                }
                
                // TODO: 在这里可以添加处理过期商品的逻辑
                // 例如：sendExpiredNotification(expiredProducts);
            }
        } catch (Exception e) {
            logger.error("已过期商品检测过程中发生异常: ", e);
        }
        
        logger.info("已过期商品检测完成");
    }

    /**
     * 获取即将过期的商品列表
     * @param days 天数
     * @return 即将过期的商品列表
     */
    private List<Product> getExpiringSoonProducts(int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date expiringDate = calendar.getTime();
        
        // 使用商品服务查询即将过期的商品
        return productService.getExpiringSoonProducts(expiringDate);
    }

    /**
     * 获取已过期的商品列表
     * @return 已过期的商品列表
     */
    private List<Product> getExpiredProducts() {
        // 使用商品服务查询已过期的商品
        return productService.getExpiredProducts();
    }
}