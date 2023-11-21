package com.ly.datastatisticalanalysis.service;

import com.ly.datastatisticalanalysis.model.domain.DaProximitySwitch;

import java.util.List;

/**
 * 对接近开关的处理
 *
 * @author:LY @date: 2023/11/20
 **/



public interface ProximitySwitchService {

    /**
     * @return
     */
    public List<DaProximitySwitch> countProximitySwitches();

    public int insertProximitySwitch(List<DaProximitySwitch> voList);

}
