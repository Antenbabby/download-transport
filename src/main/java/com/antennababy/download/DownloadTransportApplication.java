package com.antennababy.download;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.antennababy.download.app")
@EnableScheduling
@Slf4j
public class DownloadTransportApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DownloadTransportApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        log.info("app start");
//        Runtime rt = Runtime.getRuntime();
//        Process proc = rt.exec("\"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" --app=http://localhost:6060 --start-maximized");
    }
}
