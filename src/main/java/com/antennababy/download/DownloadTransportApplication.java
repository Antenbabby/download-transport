package com.antennababy.download;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.antennababy.download")
@EnableScheduling
public class DownloadTransportApplication {

    public static void main(String[] args) {
        SpringApplication.run(DownloadTransportApplication.class, args);
    }

}
