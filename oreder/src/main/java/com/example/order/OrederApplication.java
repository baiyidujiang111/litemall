package com.example.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@MapperScan("com.example.order.mapper")
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","com.example.order"})
@EnableDubbo(scanBasePackages="com.example.order.service.impl")
@EnableDiscoveryClient
public class OrederApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrederApplication.class, args);
    }

}
