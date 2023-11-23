package com.ly.datastatisticalanalysis.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WaterConsumptionListener extends AnalysisEventListener<WaterLevelPressureDTO> {
    private List<WaterLevelPressureDTO> dataList = new ArrayList<>();

    @Override
    public void invoke(WaterLevelPressureDTO data, AnalysisContext context) {
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        for (WaterLevelPressureDTO waterLevelPressureDTO : dataList) {
            log.info(waterLevelPressureDTO.toString());
        }
    }

    public List<WaterLevelPressureDTO> getDataList() {
        return dataList;
    }

}