package com.ly.datastatisticalanalysis.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ly.datastatisticalanalysis.model.domain.ProximitySwitch;

import java.util.ArrayList;
import java.util.List;

public class SwitchProximityListener extends AnalysisEventListener<ProximitySwitch> {
    private List<ProximitySwitch> dataList = new ArrayList<>();

    @Override
    public void invoke(ProximitySwitch data, AnalysisContext context) {
        // 处理每一行数据
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据解析完成后的操作，可以在这里进行一些收尾工作
    }

    // 获取解析后的数据列表
    public List<ProximitySwitch> getDataList() {
        return dataList;
    }
}