package com.ly.datastatisticalanalysis.controller;

import com.ly.datastatisticalanalysis.common.ErrorCode;
import com.ly.datastatisticalanalysis.exception.BusinessException;
import com.ly.datastatisticalanalysis.model.domain.DaProximitySwitch;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProximitySwitchesControllerTest {
    @Resource
    private ProximitySwitchService proximitySwitchService;

    @Test
    void testHandleProximitySwitch() {
        List<DaProximitySwitch> proximitySwitchVOS = proximitySwitchService.countProximitySwitches();
        Integer i = proximitySwitchService.insertProximitySwitch(proximitySwitchVOS);
        if (i < 0) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        assertTrue(i > 0, "插入操作应该返回正整数");
    }


}