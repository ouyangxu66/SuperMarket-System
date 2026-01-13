package com.supermarket.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充处理器
 * 作用：拦截 insert 和 update 操作，自动为指定字段赋值
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的填充策略
     * 对应 @TableField(fill = FieldFill.INSERT)
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 参数说明: "属性名", 值的类型, 值
        // 注意：这里写的是 Java 实体类的属性名 createTime，而不是数据库字段名 create_time
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());

        // 默认状态填充 (如果前端没传)
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "status", Integer.class, 1);
    }

    /**
     * 更新时的填充策略
     * 对应 @TableField(fill = FieldFill.UPDATE) 或 INSERT_UPDATE
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 每次更新数据时，自动将 updateTime 更新为当前时间
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}