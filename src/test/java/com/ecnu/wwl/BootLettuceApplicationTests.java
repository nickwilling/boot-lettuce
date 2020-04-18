package com.ecnu.wwl;

import com.ecnu.wwl.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BootLettuceApplicationTests {
    @Autowired
    private UserServiceImpl userService;

    @Test
    void contextLoads() {
        userService.getString("sss");
    }

    @Test
    void t1(){
        userService.getString("redisStr");
    }

    @Test
    void t2(){
        userService.expireStr("test","测试数据有效期");
        System.out.println("操作成功");
    }

    @Test
    void t3(){
        userService.selectById("2222");
    }


}
