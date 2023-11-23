package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.WaterConsumptionListener;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import com.ly.datastatisticalanalysis.service.WaterConsumptionService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WaterConsumptionImpl implements WaterConsumptionService {
    @Override
    public void calculatedWaterConsumption() {
        String filePath = "E:\\Python_code\\bigdata-analysis-model\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\202310\\20231001.csv";

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterConsumptionListener.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();
        dataList.sort(
                Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime)
        );

    }
}
