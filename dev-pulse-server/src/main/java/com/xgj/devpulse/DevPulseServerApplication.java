package com.xgj.devpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients
@EnableAsync
@SpringBootApplication
public class DevPulseServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(DevPulseServerApplication.class, args);
        System.out.println("测试接口：http://localhost:8080/v2/swagger-ui/index.html");
    }
}