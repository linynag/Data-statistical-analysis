package com.ly.datastatisticalanalysis.service;

import com.ly.datastatisticalanalysis.model.entity.DaProximitySwitch;

import java.util.List;

/**
 * 对接近开关的处理
 *
 * @author:LY @date: 2023/11/20
 **/


public interface ProximitySwitchService {

    /**
     * 统计接近开关次数
     */
    List<DaProximitySwitch> countProximitySwitches(String filePath);

    /**
     * 写入数据库
     */
    int insertProximitySwitch(List<DaProximitySwitch> voList);

}
