package com.xgj.outsourcing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync            // 开启异步能力
@SpringBootApplication
public class OutsourcingServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(OutsourcingServerApplication.class, args);
        System.out.println("测试接口：http://localhost:8080/v2/swagger-ui/index.html");
    }

}
