-- 新增商品图片字段
ALTER TABLE `product`
ADD COLUMN `image_url` VARCHAR(255) DEFAULT '' COMMENT '商品图片URL' AFTER `unit`;
