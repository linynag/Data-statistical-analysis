package com.ly.datastatisticalanalysis.service;

import com.ly.datastatisticalanalysis.model.entity.DaWaterHeight;

import java.util.List;

/**
 * 用水量计算
 */
public interface WaterConsumptionService {


    List<DaWaterHeight> calculatedWaterConsumption(String filePath);
}
