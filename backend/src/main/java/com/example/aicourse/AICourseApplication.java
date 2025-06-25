package com.example.aicourse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan; // 导入 MapperScan 注解
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AICourseApplication{
    public static void main(String[] args){
        SpringApplication.run(AICourseApplication.class,args);
    }
}