package com.supermarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class SupermarketApplicationTests {

    /**
     * 生成 BCrypt 加密密码的测试方法
     * 运行此测试，控制台会输出加密后的字符串
     */
    @Test
    void generatePassword() {
        // 1. 创建加密器
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 2. 设置原始密码
        String rawPassword = "123456";

        // 3. 加密
        String encodedPassword = encoder.encode(rawPassword);

        // 4. 输出结果
        System.out.println("===========================================");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密结果: " + encodedPassword);
        System.out.println("===========================================");
    }
}