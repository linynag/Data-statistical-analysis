package com.ly.datastatisticalanalysis;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ly.datastatisticalanalysis.mapper")
@EnableMPP
public class DataStatisticalAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataStatisticalAnalysisApplication.class, args);
    }

}
