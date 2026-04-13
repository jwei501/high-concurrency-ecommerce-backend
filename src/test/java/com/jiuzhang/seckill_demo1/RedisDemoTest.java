package com.jiuzhang.seckill_demo1;

import com.jiuzhang.seckill_demo1.service.RedisService;
import com.jiuzhang.seckill_demo1.service.SeckillActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RedisDemoTest {

    @Resource
    private RedisService redisService;

    @Autowired
    SeckillActivityService seckillActivityService;

    @Test
    public void stockTest() {
        String value = redisService.setValue("stock:19", 10L).getValue("stock:19");
        System.out.println(value);
    }

    @Test
    public void pushSeckillInfoToRedisTest() {
        seckillActivityService.pushSeckillInfoToRedis(19);
    }


}
