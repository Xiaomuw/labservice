package com.xiaomu.labservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实验室共享平台启动类
 */
@SpringBootApplication
@MapperScan("com.xiaomu.labservice.module.*.mapper")
public class LabserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabserviceApplication.class, args);
    }

}
