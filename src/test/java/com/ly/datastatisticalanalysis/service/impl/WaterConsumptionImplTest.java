package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.WaterConsumptionListener;
import com.ly.datastatisticalanalysis.model.dto.ProximitySwitchDTO;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import com.ly.datastatisticalanalysis.model.entity.DaProximitySwitch;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;


class WaterConsumptionImplTest {

    public static void analyzeSwitches(List<WaterLevelPressureDTO> waterLevelPressureList) {
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();

        for (int i = 0; i < waterLevelPressureList.size() - 1; i++) {
            WaterLevelPressureDTO current = waterLevelPressureList.get(i);
            WaterLevelPressureDTO next = waterLevelPressureList.get(i + 1);

            String vin = current.getVin();
            String day = current.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            Map<String, Integer> pressureChanges = result.get(vin).get(day);

            try {
                Date currentTime = current.parseTimeString();
                Date nextTime = next.parseTimeString();

                Double currentWaterPressure = current.parseWaterLevelPressureString();
                Double nextWaterPressure = next.parseWaterLevelPressureString();

                long timeDifferenceInSeconds = (nextTime.getTime() - currentTime.getTime()) / 1000;
                double waterLevelChangeRate = (nextWaterPressure - currentWaterPressure) / timeDifferenceInSeconds;

                // 动态阈值，可以根据实际情况调整系数
                double dynamicThreshold = calculateDynamicThreshold(vin, day, waterLevelChangeRate);

                // 判断是否为正常加水
                if (waterLevelChangeRate > dynamicThreshold) {
                    pressureChanges.put("waterAdded", pressureChanges.getOrDefault("waterAdded", 0) +
                            (int) (nextWaterPressure - currentWaterPressure));
                    pressureChanges.put("waterAdditionCount", pressureChanges.getOrDefault("waterAdditionCount", 0) + 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 打印结果
        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : result.entrySet()) {
            System.out.println("VIN: " + entry.getKey());
            for (Map.Entry<String, Map<String, Integer>> dayEntry : entry.getValue().entrySet()) {
                System.out.println("  Day: " + dayEntry.getKey());
                for (Map.Entry<String, Integer> statsEntry : dayEntry.getValue().entrySet()) {
                    System.out.println("    " + statsEntry.getKey() + ": " + statsEntry.getValue());
                }
            }
        }
    }

    // 计算动态阈值，这里简单示例，实际应用中需要更复杂的算法
    private static double calculateDynamicThreshold(String vin, String day, double waterLevelChangeRate) {
        // 这里可以考虑使用滑动窗口、滤波器等方法来平滑历史变化率数据
        // 在实际应用中可能需要更复杂的算法
        return 1.5 * waterLevelChangeRate; // 举例：简单地将当前变化率的1.5倍作为阈值
    }


    @Test
    void calculatedWaterConsumption() {
        String filePath = "E:\\Python_code\\bigdata-analysis-model\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\202310\\20231002.csv";

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterLevelPressureDTO.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();

        // dataList.stream().filter(WaterLevelPressureDTO::getWaterLevelPressure).forEach(System.out::println);

        dataList.sort(
                Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime));

        for (WaterLevelPressureDTO waterLevelPressureDTO : dataList) {
            System.out.println(waterLevelPressureDTO);
        }
    }
}