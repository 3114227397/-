package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
//<<<<<<< HEAD
import org.springframework.transaction.annotation.EnableTransactionManagement;
//=======
//>>>>>>> 7453505d06a3c871baf6b6df587122da4f9ca383

@Slf4j
@SpringBootApplication
@ServletComponentScan
//<<<<<<< HEAD
@EnableTransactionManagement

public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功...");
    }
}
