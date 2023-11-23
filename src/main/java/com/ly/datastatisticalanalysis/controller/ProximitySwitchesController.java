package com.ly.datastatisticalanalysis.controller;

import com.ly.datastatisticalanalysis.common.BaseResponse;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController()
@RequestMapping("/ProximitySwitches")
public class ProximitySwitchesController {

    @Resource
    private ProximitySwitchService proximitySwitchService;

    @PostMapping("/HandleProximitySwitch")
    public BaseResponse<T> handleProximitySwitch() {
       return null;
    }

}
