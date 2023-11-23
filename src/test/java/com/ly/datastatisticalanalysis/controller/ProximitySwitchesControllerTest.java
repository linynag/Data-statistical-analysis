package com.ly.datastatisticalanalysis.controller;

import com.ly.datastatisticalanalysis.common.ErrorCode;
import com.ly.datastatisticalanalysis.exception.BusinessException;
import com.ly.datastatisticalanalysis.model.entity.DaProximitySwitch;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
class ProximitySwitchesControllerTest {
    @Resource
    private ProximitySwitchService proximitySwitchService;

    private static boolean isValidDateFile(String fileName, String dateFormat, String startDate, String endDate) {
        try {
            LocalDate fileDate = LocalDate.parse(fileName.substring(0, 8), DateTimeFormatter.ofPattern(dateFormat));
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern(dateFormat));
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern(dateFormat));

            return !fileDate.isBefore(start) && !fileDate.isAfter(end);
        } catch (Exception e) {
            // 处理解析异常或无效文件名
            return false;
        }
    }

    @Test
    void testHandleProximitySwitchALLFile() {

        String directoryPath = "E:\\Python_code\\bigdata-analysis-model\\proximity_switches\\query_data\\proximity_switches\\202310\\";

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    if (isValidDateFile(file.getName(), "yyyyMMdd", "20231001", "20231031")) {
                        List<DaProximitySwitch> proximitySwitchVOS = proximitySwitchService.countProximitySwitches(file.getAbsolutePath());
                        Integer i = proximitySwitchService.insertProximitySwitch(proximitySwitchVOS);
                        if (i < 0) {
                            throw new BusinessException(ErrorCode.FAIL);
                        }
                        assertTrue(i >= 0, "插入操作应该返回正整数");
                    }
                }
            }
        }
    }

    @Test
    void testHandleProximitySwitchMergeFile() {
        String filePath = "E:\\Python_code\\bigdata-analysis-model\\proximity_switches\\query_data\\proximity_switches\\202310\\20231005.csv";
        List<DaProximitySwitch> proximitySwitchVOS = proximitySwitchService.countProximitySwitches(filePath);
        Integer i = proximitySwitchService.insertProximitySwitch(proximitySwitchVOS);
        if (i < 0) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        assertTrue(i >= 0, "插入操作应该返回正整数");
    }


}