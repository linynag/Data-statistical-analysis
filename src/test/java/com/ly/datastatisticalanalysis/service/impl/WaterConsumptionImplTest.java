package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.WaterConsumptionListener;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class WaterConsumptionImplTest {

    public static void analyzeWaterHeight(List<WaterLevelPressureDTO> waterLevelPressureList) {
        // 单车每天的统计
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();

        // 单车一天所有行程的统计
        List<List<WaterLevelPressureDTO>> trips = new ArrayList<>();

        // 遍历数据，按时间段划分行程
        List<WaterLevelPressureDTO> currentTrip = new ArrayList<>();
        for (int i = 0; i < waterLevelPressureList.size() - 1; i++) {
            WaterLevelPressureDTO current = waterLevelPressureList.get(i);
            WaterLevelPressureDTO next = waterLevelPressureList.get(i + 1);

            currentTrip.add(current);

            // 判断是否为新的时间段
            if (isStartOfNewTrip(current, next)) {
                trips.add(currentTrip);
                currentTrip = new ArrayList<>();
            }
        }

        // 处理最后一段行程
        if (!currentTrip.isEmpty()) {
            trips.add(currentTrip);
        }

        // 遍历所有行程，进行统计
        for (List<WaterLevelPressureDTO> trip : trips) {
            analyzeSingleTrip(trip, result);
        }

        // 遍历行程间，进行统计
        for (int i = 0; i < trips.size() - 1; i++) {
            analyzeInterTrip(trips.get(i), trips.get(i + 1), result);
        }

        // 打印结果
        printResult(result);
    }

    private static void analyzeSingleTrip(List<WaterLevelPressureDTO> trip, Map<String, Map<String, Map<String, Integer>>> result) {
        if (trip.size() > 1) {
            WaterLevelPressureDTO first = trip.get(0);
            WaterLevelPressureDTO last = trip.get(trip.size() - 1);

            String vin = first.getVin();
            String day = first.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            Map<String, Integer> dayStats = result.get(vin).get(day);

            // 判断单个行程内是否有正常加水
            double waterLevelChange = last.parseWaterLevelPressureString() - first.parseWaterLevelPressureString();
            if (waterLevelChange > 20) {
                // 记录为正常的加水
                dayStats.put("waterAdded", dayStats.getOrDefault("waterAdded", 0) + (int) waterLevelChange);
                String format = String.format("单个行程加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, first.getTime(), (int) waterLevelChange);
                System.out.printf(format);
                dayStats.put("waterAdditionCount", dayStats.getOrDefault("waterAdditionCount", 0) + 1);
            }
        }
    }

    private static void analyzeInterTrip(List<WaterLevelPressureDTO> firstTrip, List<WaterLevelPressureDTO> secondTrip,
                                         Map<String, Map<String, Map<String, Integer>>> result) {
        if (!firstTrip.isEmpty() && !secondTrip.isEmpty()) {
            WaterLevelPressureDTO lastOfFirst = firstTrip.get(firstTrip.size() - 1);
            WaterLevelPressureDTO firstOfSecond = secondTrip.get(0);

            String vin = lastOfFirst.getVin();
            String day = lastOfFirst.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            Map<String, Integer> dayStats = result.get(vin).get(day);

            try {
                Date lastTime = lastOfFirst.parseTimeString();
                Date nextTime = firstOfSecond.parseTimeString();

                long timeDifferenceInSeconds = (nextTime.getTime() - lastTime.getTime()) / 1000;

                // 判断时间差是否大于60秒，如果是则检测水位增加情况
                if (timeDifferenceInSeconds > 60) {
                    double waterLevelChange = firstOfSecond.parseWaterLevelPressureString() -
                            lastOfFirst.parseWaterLevelPressureString();

                    // 检测水位增加是否大于20
                    if (waterLevelChange > 20) {
                        // 记录为正常的加水
                        dayStats.put("waterAdded", dayStats.getOrDefault("waterAdded", 0) + (int) waterLevelChange);
                        String format = String.format("行程间加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, lastOfFirst.getTime(), (int) waterLevelChange);
                        System.out.printf(format);
                        dayStats.put("waterAdditionCount", dayStats.getOrDefault("waterAdditionCount", 0) + 1);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isStartOfNewTrip(WaterLevelPressureDTO current, WaterLevelPressureDTO next) {
        try {
            Date currentTime = current.parseTimeString();
            Date nextTime = next.parseTimeString();

            long timeDifferenceInSeconds = (nextTime.getTime() - currentTime.getTime()) / 1000;

            // 判断时间差是否大于20秒，如果是则认为是新的时间段
            return timeDifferenceInSeconds > 20;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void printResult(Map<String, Map<String, Map<String, Integer>>> result) {
        // 打印结果
        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : result.entrySet()) {
            System.out.println("VIN: " + entry.getKey());
            for (Map.Entry<String, Map<String, Integer>> dayEntry : entry.getValue().entrySet()) {
                System.out.println("  Day: " + dayEntry.getKey());
                for (Map.Entry<String, Integer> statEntry : dayEntry.getValue().entrySet()) {
                    System.out.println("    " + statEntry.getKey() + ": " + statEntry.getValue());
                }
            }
        }
    }


    @Test
    void calculatedWaterConsumption() {
        String filePath = "E:\\Python_code\\bigdata-analysis-model\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\merged_water_height_202301002.csv";

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterLevelPressureDTO.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();

        // dataList.stream().filter(WaterLevelPressureDTO::getWaterLevelPressure).forEach(System.out::println);

        dataList.sort(
                Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime));



        analyzeWaterHeight(dataList);
    }
}