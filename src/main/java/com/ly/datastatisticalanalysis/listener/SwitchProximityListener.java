package com.ly.datastatisticalanalysis.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ly.datastatisticalanalysis.model.dto.ProximitySwitchDTO;

import java.util.ArrayList;
import java.util.List;

public class SwitchProximityListener extends AnalysisEventListener<ProximitySwitchDTO> {
    private List<ProximitySwitchDTO> dataList = new ArrayList<>();

    @Override
    public void invoke(ProximitySwitchDTO data, AnalysisContext context) {
        // 处理每一行数据
        dataList.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据解析完成后的操作，可以在这里进行一些收尾工作
    }

    // 获取解析后的数据列表
    public List<ProximitySwitchDTO> getDataList() {
        return dataList;
    }
}