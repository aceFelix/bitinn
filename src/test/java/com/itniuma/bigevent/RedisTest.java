package com.itniuma.bigevent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

@SpringBootTest
// 测试类上添加了这个注解，表示这个类是一个测试类，不会随项目一块打包
// 将来单元测试方法执行之前，会创建一个springIOC容器
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 操作redis的模板类

    @Test
    public void test() {
        // 添加数据
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set("name", "itniuma");
        operations.set("age", "18", 15, TimeUnit.SECONDS); // 设置过期时间
        // 获取数据
        String name = operations.get("name");
        System.out.println(name);
    }
}
