package com.ly.datastatisticalanalysis.controller;

import com.ly.datastatisticalanalysis.common.BaseResponse;
import com.ly.datastatisticalanalysis.common.ErrorCode;
import com.ly.datastatisticalanalysis.common.ResultUtils;
import com.ly.datastatisticalanalysis.exception.BusinessException;
import com.ly.datastatisticalanalysis.model.domain.DaProximitySwitch;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController()
@RequestMapping("/ProximitySwitches")
public class ProximitySwitchesController {

    @Resource
    private ProximitySwitchService proximitySwitchService;

    @PostMapping("/HandleProximitySwitch")
    public BaseResponse<T> handleProximitySwitch() {
        List<DaProximitySwitch> proximitySwitchVOS = proximitySwitchService.countProximitySwitches();
        Integer i = proximitySwitchService.insertProximitySwitch(proximitySwitchVOS);
        if (i < 0) {
            throw new BusinessException(ErrorCode.FAIL);
        }
        return ResultUtils.success();
    }

}
