package com.ly.datastatisticalanalysis.controller;

import com.ly.datastatisticalanalysis.common.BaseResponse;
import com.ly.datastatisticalanalysis.common.ErrorCode;
import com.ly.datastatisticalanalysis.common.ResultUtils;
import com.ly.datastatisticalanalysis.exception.BusinessException;
import com.ly.datastatisticalanalysis.model.vo.ProximitySwitchVO;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProximitySwitchesControllerTest {
    @Resource
    private ProximitySwitchService proximitySwitchService;
    @Test
    void testHandleProximitySwitch() {

        List<ProximitySwitchVO> proximitySwitchVOS = proximitySwitchService.CountProximitySwitches();
        Integer i =proximitySwitchService.insertProximitySwitch(proximitySwitchVOS);
        if (i<0){
            throw new BusinessException(ErrorCode.FAIL);
        }

        assertTrue(i > 0, "插入操作应该返回正整数");
    }



}