package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcel;
import com.ly.datastatisticalanalysis.listener.SwitchProximityListener;
import com.ly.datastatisticalanalysis.mapper.DaProximitySwitchMapper;
import com.ly.datastatisticalanalysis.model.domain.ProximitySwitch;
import com.ly.datastatisticalanalysis.model.vo.ProximitySwitchVO;
import com.ly.datastatisticalanalysis.service.DaProximitySwitchService;
import com.ly.datastatisticalanalysis.service.ProximitySwitchService;
import com.ly.datastatisticalanalysis.model.domain.DaProximitySwitch;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProximitySwitchImpl implements ProximitySwitchService {

    @Resource
    private DaProximitySwitchService daProximitySwitchService;

    public static List<ProximitySwitchVO> analyzeSwitches(List<ProximitySwitch> proximitySwitchList) {
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();

        for (int i = 0; i < proximitySwitchList.size() - 1; i++) {
            ProximitySwitch currentSwitch = proximitySwitchList.get(i);
            ProximitySwitch nextSwitch = proximitySwitchList.get(i + 1);

            // 按VIN和日期进行分类
            String vin = currentSwitch.getVIN();
            String day = currentSwitch.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            // 统计InputSignal变化次数
            countSignalChanges(result.get(vin).get(day), currentSwitch, nextSwitch);
        }
        // 转换为ProximitySwitchVO对象列表
        return convertToProximitySwitchVOList(result);
    }

    private static void countSignalChanges(Map<String, Integer> signalChanges, ProximitySwitch currentSwitch, ProximitySwitch nextSwitch) {
        for (int i = 0; i <= 15; i++) {
            String signalName = "InputSignal" + i;
            String currentValue = getSignalValue(currentSwitch, signalName);
            String nextValue = getSignalValue(nextSwitch, signalName);

            if (!currentValue.equals(nextValue)) {
                signalChanges.put(signalName, signalChanges.getOrDefault(signalName, 0) + 1);
            }
        }
    }

    private static List<ProximitySwitchVO> convertToProximitySwitchVOList(Map<String, Map<String, Map<String, Integer>>> result) {
        List<ProximitySwitchVO> voList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : result.entrySet()) {
            for (Map.Entry<String, Map<String, Integer>> innerEntry : entry.getValue().entrySet()) {
                ProximitySwitchVO vo = new ProximitySwitchVO();
                vo.setVIN(entry.getKey());
                vo.setDay(innerEntry.getKey());
                vo.setCountInputSignal0(innerEntry.getValue().getOrDefault("InputSignal0", 0).toString());
                vo.setCountInputSignal1(innerEntry.getValue().getOrDefault("InputSignal1", 0).toString());
                vo.setCountInputSignal2(innerEntry.getValue().getOrDefault("InputSignal2", 0).toString());
                vo.setCountInputSignal3(innerEntry.getValue().getOrDefault("InputSignal3", 0).toString());
                vo.setCountInputSignal4(innerEntry.getValue().getOrDefault("InputSignal4", 0).toString());
                vo.setCountInputSignal5(innerEntry.getValue().getOrDefault("InputSignal5", 0).toString());
                vo.setCountInputSignal6(innerEntry.getValue().getOrDefault("InputSignal6", 0).toString());
                vo.setCountInputSignal7(innerEntry.getValue().getOrDefault("InputSignal7", 0).toString());
                vo.setCountInputSignal8(innerEntry.getValue().getOrDefault("InputSignal8", 0).toString());
                vo.setCountInputSignal9(innerEntry.getValue().getOrDefault("InputSignal9", 0).toString());
                vo.setCountInputSignal10(innerEntry.getValue().getOrDefault("InputSignal10", 0).toString());
                vo.setCountInputSignal11(innerEntry.getValue().getOrDefault("InputSignal11", 0).toString());
                vo.setCountInputSignal12(innerEntry.getValue().getOrDefault("InputSignal12", 0).toString());
                vo.setCountInputSignal13(innerEntry.getValue().getOrDefault("InputSignal13", 0).toString());
                vo.setCountInputSignal14(innerEntry.getValue().getOrDefault("InputSignal14", 0).toString());
                vo.setCountInputSignal15(innerEntry.getValue().getOrDefault("InputSignal15", 0).toString());


                voList.add(vo);
            }
        }

        return voList;
    }

    private static String getSignalValue(ProximitySwitch proximitySwitch, String signalName) {
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

    private static void processVoList(List<ProximitySwitchVO> voList) {
        for (ProximitySwitchVO vo : voList) {
            vo.setCountInputSignal0(processCount(vo.getCountInputSignal0()));
            vo.setCountInputSignal1(processCount(vo.getCountInputSignal1()));
            vo.setCountInputSignal2(processCount(vo.getCountInputSignal2()));
            vo.setCountInputSignal3(processCount(vo.getCountInputSignal3()));
            vo.setCountInputSignal4(processCount(vo.getCountInputSignal4()));
            vo.setCountInputSignal5(processCount(vo.getCountInputSignal5()));
            vo.setCountInputSignal6(processCount(vo.getCountInputSignal6()));
            vo.setCountInputSignal7(processCount(vo.getCountInputSignal7()));
            vo.setCountInputSignal8(processCount(vo.getCountInputSignal8()));
            vo.setCountInputSignal9(processCount(vo.getCountInputSignal9()));
            vo.setCountInputSignal10(processCount(vo.getCountInputSignal10()));
            vo.setCountInputSignal11(processCount(vo.getCountInputSignal11()));
            vo.setCountInputSignal12(processCount(vo.getCountInputSignal12()));
            vo.setCountInputSignal13(processCount(vo.getCountInputSignal13()));
            vo.setCountInputSignal14(processCount(vo.getCountInputSignal14()));
            vo.setCountInputSignal15(processCount(vo.getCountInputSignal15()));
        }
    }

    private static String processCount(String count) {
        if (count != null && !count.isEmpty()) {
            double doubleCount = Double.parseDouble(count) / 2.0;
            // Round to the nearest integer
            int roundedCount = (int) Math.round(doubleCount);
            return Integer.toString(roundedCount);
        }
        return "0";
    }

    @Override
    public List<ProximitySwitchVO> CountProximitySwitches() {
        // 定义CSV文件路径
        String filePath = "E:\\Python_code\\bigdata-analysis-model\\proximity_switches\\query_data\\proximity_switches\\202310\\20231003.csv";
        // 创建 ExcelReaderBuilder 对象，并在构造函数中传入监听器实例
        SwitchProximityListener listener = new SwitchProximityListener();
        EasyExcel.read(filePath, ProximitySwitch.class, listener).sheet().doRead();

        List<ProximitySwitch> dataList = listener.getDataList();

        // 使用Lambda表达式和Comparator进行排序
        dataList.sort(
                Comparator.comparing(ProximitySwitch::getVIN)
                        .thenComparing(ProximitySwitch::getDay)
                        .thenComparing(ProximitySwitch::getTime)
        );
        List<ProximitySwitchVO> voList = analyzeSwitches(dataList);
        // 对次数/2，进行四舍五入
        processVoList(voList);

        return voList;
    }

    @Override
    public int insertProximitySwitch(List<ProximitySwitchVO> voList) {
        List<DaProximitySwitch> daProximitySwitches = new ArrayList<>();
        for (ProximitySwitchVO vo : voList) {
            DaProximitySwitch daProximitySwitch = new DaProximitySwitch();
            daProximitySwitch.setVin(vo.getVIN());
            daProximitySwitch.setDay(LocalDate.parse(vo.getDay()));
            daProximitySwitch.setCountInputSignal0(Integer.valueOf(vo.getCountInputSignal0()));
            daProximitySwitch.setCountInputSignal1(Integer.valueOf(vo.getCountInputSignal1()));
            daProximitySwitch.setCountInputSignal2(Integer.valueOf(vo.getCountInputSignal2()));
            daProximitySwitch.setCountInputSignal3(Integer.valueOf(vo.getCountInputSignal3()));
            daProximitySwitch.setCountInputSignal4(Integer.valueOf(vo.getCountInputSignal4()));
            daProximitySwitch.setCountInputSignal5(Integer.valueOf(vo.getCountInputSignal5()));
            daProximitySwitch.setCountInputSignal6(Integer.valueOf(vo.getCountInputSignal6()));
            daProximitySwitch.setCountInputSignal7(Integer.valueOf(vo.getCountInputSignal7()));
            daProximitySwitch.setCountInputSignal8(Integer.valueOf(vo.getCountInputSignal8()));
            daProximitySwitch.setCountInputSignal9(Integer.valueOf(vo.getCountInputSignal9()));
            daProximitySwitch.setCountInputSignal10(Integer.valueOf(vo.getCountInputSignal10()));
            daProximitySwitch.setCountInputSignal11(Integer.valueOf(vo.getCountInputSignal11()));
            daProximitySwitch.setCountInputSignal12(Integer.valueOf(vo.getCountInputSignal12()));
            daProximitySwitch.setCountInputSignal13(Integer.valueOf(vo.getCountInputSignal13()));
            daProximitySwitch.setCountInputSignal14(Integer.valueOf(vo.getCountInputSignal14()));
            daProximitySwitch.setCountInputSignal15(Integer.valueOf(vo.getCountInputSignal15()));
            daProximitySwitches.add(daProximitySwitch);
        }
        boolean b = daProximitySwitchService.saveOrUpdateBatchByMultiId(daProximitySwitches);
        return b ? 1 : 0;
    }

}
