package com.ly.datastatisticalanalysis.service;

import com.ly.datastatisticalanalysis.model.vo.ProximitySwitchVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
    public List<ProximitySwitchVO> CountProximitySwitches();

    public int insertProximitySwitch(List<ProximitySwitchVO> voList);

}
