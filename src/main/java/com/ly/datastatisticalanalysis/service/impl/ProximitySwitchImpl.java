package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.SwitchProximityListener;
import com.ly.datastatisticalanalysis.model.entity.DaProximitySwitch;
import com.ly.datastatisticalanalysis.model.dto.ProximitySwitchDTO;
import com.ly.datastatisticalanalysis.service.DaProximitySwitchService;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProximitySwitchImpl implements ProximitySwitchService {

    @Resource
    private DaProximitySwitchService daProximitySwitchService;

    public static List<DaProximitySwitch> analyzeSwitches(List<ProximitySwitchDTO> proximitySwitchList) {
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();

        for (int i = 0; i < proximitySwitchList.size() - 1; i++) {
            ProximitySwitchDTO currentSwitch = proximitySwitchList.get(i);
            ProximitySwitchDTO nextSwitch = proximitySwitchList.get(i + 1);

            // 按VIN和日期进行分类
            String vin = currentSwitch.getVIN();
            String day = currentSwitch.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            // 统计每个InputSignal变化次数
            countSignalChanges(result.get(vin).get(day), currentSwitch, nextSwitch);
        }
        // 转换为ProximitySwitchVO对象列表
        return convertToProximitySwitchVOList(result);
    }

    private static void countSignalChanges(Map<String, Integer> signalChanges, ProximitySwitchDTO currentSwitch, ProximitySwitchDTO nextSwitch) {
        for (int i = 0; i <= 15; i++) {
            String signalName = "InputSignal" + i;
            String currentValue = getSignalValue(currentSwitch, signalName);
            String nextValue = getSignalValue(nextSwitch, signalName);

            if (!currentValue.equals(nextValue)) {
                signalChanges.put(signalName, signalChanges.getOrDefault(signalName, 0) + 1);
            }
        }
    }

    private static List<DaProximitySwitch> convertToProximitySwitchVOList(Map<String, Map<String, Map<String, Integer>>> result) {

        List<DaProximitySwitch> list = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : result.entrySet()) {
            for (Map.Entry<String, Map<String, Integer>> innerEntry : entry.getValue().entrySet()) {
                DaProximitySwitch da = new DaProximitySwitch();
                da.setVin(entry.getKey());
                da.setDay(LocalDate.parse(innerEntry.getKey()));

                da.setCountInputSignal0(innerEntry.getValue().getOrDefault("InputSignal0", 0) / 2);
                da.setCountInputSignal1(innerEntry.getValue().getOrDefault("InputSignal1", 0) / 2);
                da.setCountInputSignal2(innerEntry.getValue().getOrDefault("InputSignal2", 0) / 2);
                da.setCountInputSignal3(innerEntry.getValue().getOrDefault("InputSignal3", 0) / 2);
                da.setCountInputSignal4(innerEntry.getValue().getOrDefault("InputSignal4", 0) / 2);
                da.setCountInputSignal5(innerEntry.getValue().getOrDefault("InputSignal5", 0) / 2);
                da.setCountInputSignal6(innerEntry.getValue().getOrDefault("InputSignal6", 0) / 2);
                da.setCountInputSignal7(innerEntry.getValue().getOrDefault("InputSignal7", 0) / 2);
                da.setCountInputSignal8(innerEntry.getValue().getOrDefault("InputSignal8", 0) / 2);
                da.setCountInputSignal9(innerEntry.getValue().getOrDefault("InputSignal9", 0) / 2);
                da.setCountInputSignal10(innerEntry.getValue().getOrDefault("InputSignal10", 0) / 2);
                da.setCountInputSignal11(innerEntry.getValue().getOrDefault("InputSignal11", 0) / 2);
                da.setCountInputSignal12(innerEntry.getValue().getOrDefault("InputSignal12", 0) / 2);
                da.setCountInputSignal13(innerEntry.getValue().getOrDefault("InputSignal13", 0) / 2);
                da.setCountInputSignal14(innerEntry.getValue().getOrDefault("InputSignal14", 0) / 2);
                da.setCountInputSignal15(innerEntry.getValue().getOrDefault("InputSignal15", 0) / 2);
                list.add(da);
            }
        }

        return list;
    }

    private static String getSignalValue(ProximitySwitchDTO proximitySwitch, String signalName) {
        switch (signalName) {
            case "InputSignal0":
                return proximitySwitch.getInputSignal0();
            case "InputSignal1":
                return proximitySwitch.getInputSignal1();
            case "InputSignal2":
                return proximitySwitch.getInputSignal2();
            case "InputSignal3":
                return proximitySwitch.getInputSignal3();
            case "InputSignal4":
                return proximitySwitch.getInputSignal4();
            case "InputSignal5":
                return proximitySwitch.getInputSignal5();
            case "InputSignal6":
                return proximitySwitch.getInputSignal6();
            case "InputSignal7":
                return proximitySwitch.getInputSignal7();
            case "InputSignal8":
                return proximitySwitch.getInputSignal8();
            case "InputSignal9":
                return proximitySwitch.getInputSignal9();
            case "InputSignal10":
                return proximitySwitch.getInputSignal10();
            case "InputSignal11":
                return proximitySwitch.getInputSignal11();
            case "InputSignal12":
                return proximitySwitch.getInputSignal12();
            case "InputSignal13":
                return proximitySwitch.getInputSignal13();
            case "InputSignal14":
                return proximitySwitch.getInputSignal14();
            case "InputSignal15":
                return proximitySwitch.getInputSignal15();

            default:
                throw new IllegalArgumentException("Invalid signal name: " + signalName);
        }
    }


    @Override
    public List<DaProximitySwitch> countProximitySwitches(String filePath ) {
        // 定义CSV文件路径
        // 创建 ExcelReaderBuilder 对象，并在构造函数中传入监听器实例
        SwitchProximityListener listener = new SwitchProximityListener();
        EasyExcelFactory.read(filePath, ProximitySwitchDTO.class, listener).sheet().doRead();

        List<ProximitySwitchDTO> dataList = listener.getDataList();

        // 使用Lambda表达式和Comparator进行排序
        dataList.sort(
                Comparator.comparing(ProximitySwitchDTO::getVIN)
                        .thenComparing(ProximitySwitchDTO::getDay)
                        .thenComparing(ProximitySwitchDTO::getTime)
        );

        return analyzeSwitches(dataList);
    }

    @Override
    public int insertProximitySwitch(List<DaProximitySwitch> voList) {
        boolean b = daProximitySwitchService.saveOrUpdateBatchByMultiId(voList);
        return b ? 1 : 0;
    }
}
