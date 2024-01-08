package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.WaterConsumptionListener;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import com.ly.datastatisticalanalysis.model.entity.DaWaterHeight;
import com.ly.datastatisticalanalysis.service.DaWaterHeightService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class WaterConsumptionImplTest {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private DaWaterHeightService daWaterHeightService;

    public static List<DaWaterHeight> analyzeWaterHeightSingleTrip(List<WaterLevelPressureDTO> waterLevelPressureList) {
        // 单车每天的统计
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();
        List<DaWaterHeight> waterHeights = new ArrayList<>();
        // 单车一天所有行程的统计
        List<List<WaterLevelPressureDTO>> trips = new ArrayList<>();

        // 遍历数据，按时间段划分行程 currentTrip 每个行程
        List<WaterLevelPressureDTO> currentTrip = new ArrayList<>();
        for (int i = 0; i < waterLevelPressureList.size() - 1; i++) {
            WaterLevelPressureDTO current = waterLevelPressureList.get(i);
            WaterLevelPressureDTO next = waterLevelPressureList.get(i + 1);

            // 将当前行程加入行程列表
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

        // 遍历单个行程，进行统计
        for (List<WaterLevelPressureDTO> trip : trips) {
            analyzeSingleTrip(trip, result, waterHeights);
        }

        return waterHeights;
    }

    public static List<DaWaterHeight> analyzeWaterHeightInterTrip(List<WaterLevelPressureDTO> waterLevelPressureList) {
        // 单车每天的统计
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();
        List<DaWaterHeight> waterHeights = new ArrayList<>();
        // 单车一天所有行程的统计
        List<List<WaterLevelPressureDTO>> trips = new ArrayList<>();

        // 遍历数据，按时间段划分行程 currentTrip 每个行程
        List<WaterLevelPressureDTO> currentTrip = new ArrayList<>();
        for (int i = 0; i < waterLevelPressureList.size() - 1; i++) {
            WaterLevelPressureDTO current = waterLevelPressureList.get(i);
            WaterLevelPressureDTO next = waterLevelPressureList.get(i + 1);

            // 将当前行程加入行程列表
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

        // 遍历行程间，进行统计
        for (int i = 0; i < trips.size() - 1; i++) {
            analyzeInterTrip(trips.get(i), trips.get(i + 1), result, waterHeights);
        }

        return waterHeights;
    }

    private static void analyzeSingleTrip(List<WaterLevelPressureDTO> trip, Map<String, Map<String, Map<String, Integer>>> result
            , List<DaWaterHeight> waterHeights) {
        if (trip.size() > 1) {
            WaterLevelPressureDTO first = trip.get(0);
            WaterLevelPressureDTO last = trip.get(trip.size() - 1);

            WaterLevelPressureDTO max = trip.stream()
                    .max(Comparator.comparingDouble(dto -> dto.parseWaterLevelPressureString())).orElse(null);

            String vin = first.getVin();
            String day = first.getDay();

            result.putIfAbsent(vin, new HashMap<>());
            result.get(vin).putIfAbsent(day, new HashMap<>());

            Map<String, Integer> dayStats = result.get(vin).get(day);

            // 判断单个行程内是否有正常加水
            double waterLevelChange = last.parseWaterLevelPressureString() - first.parseWaterLevelPressureString();

            if (waterLevelChange > 30) {
                DaWaterHeight daWaterHeight = new DaWaterHeight();

                double maxLevelChange = last.parseWaterLevelPressureString() - first.parseWaterLevelPressureString();
                daWaterHeight.setVin(vin);
                daWaterHeight.setDay(LocalDate.parse(day));
                LocalDateTime dateTime = LocalDateTime.parse(first.getTime(), formatter);
                daWaterHeight.setTime(dateTime);
                daWaterHeight.setAddWaterQuantity((int) maxLevelChange);
                daWaterHeight.setAddWaterCount(1);
                waterHeights.add(daWaterHeight);

                // 记录为正常的加水
                dayStats.put("addWaterCount", dayStats.getOrDefault("waterAdded", 0) + (int) maxLevelChange);
                String format = String.format("单个行程加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, first.getTime(), (int) maxLevelChange);
                System.out.printf(format);
                dayStats.put("addWaterQuantity", dayStats.getOrDefault("waterAdditionCount", 0) + 1);

                log.info("{} , {}  ", first.getVin(), first.getDay());
                log.info("{} , {} ", first.getTime(), first.parseWaterLevelPressureString());
                log.info("{} , {} ", last.getTime(), last.parseWaterLevelPressureString());
                log.info("{} , {} ", max.getTime(), max.parseWaterLevelPressureString());


            }
        }
    }

    private static void analyzeInterTrip(List<WaterLevelPressureDTO> firstTrip, List<WaterLevelPressureDTO> secondTrip,
                                         Map<String, Map<String, Map<String, Integer>>> result,
                                         List<DaWaterHeight> waterHeights) {
        if (!firstTrip.isEmpty() && !secondTrip.isEmpty()) {
            WaterLevelPressureDTO lastOfFirst = firstTrip.get(firstTrip.size() - 1);
            WaterLevelPressureDTO firstOfSecond = secondTrip.get(0);

            // 对于不是同一辆车或者同一天的数据直接返回
            if (!lastOfFirst.getVin().equals(firstOfSecond.getVin())) {
                return;
            }

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

                    Double averageLastWaterLevel = secondTrip.stream()
                            .limit(30)
                            .collect(Collectors.averagingDouble(WaterLevelPressureDTO::parseWaterLevelPressureString));

                    double waterLevelChange = averageLastWaterLevel -
                            lastOfFirst.parseWaterLevelPressureString();

                    // 检测水位增加是否大于20
                    if (waterLevelChange > 30) {
                        DaWaterHeight daWaterHeight = new DaWaterHeight();

                        daWaterHeight.setVin(vin);
                        daWaterHeight.setDay(LocalDate.parse(day));
                        LocalDateTime dateTime = LocalDateTime.parse(lastOfFirst.getTime(), formatter);
                        daWaterHeight.setTime(dateTime);
                        daWaterHeight.setAddWaterQuantity((int) waterLevelChange);
                        daWaterHeight.setAddWaterCount(1);
                        waterHeights.add(daWaterHeight);
                        // 记录为正常的加水
                        dayStats.put("waterAdded", dayStats.getOrDefault("waterAdded", 0) + (int) waterLevelChange);
                        String format = String.format("行程间加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, lastOfFirst.getTime(), (int) waterLevelChange);
                        System.out.printf(format);
                        dayStats.put("waterAdditionCount", dayStats.getOrDefault("waterAdditionCount", 0) + 1);

                        log.info("{} , {}  ", lastOfFirst.getVin(), lastOfFirst.getDay());
                        log.info("{} , {} ", lastOfFirst.getTime(), lastOfFirst.parseWaterLevelPressureString());
                        log.info("{} , {}  ", firstOfSecond.getVin(), firstOfSecond.getDay());
                        log.info("{} , {} ", firstOfSecond.getTime(), firstOfSecond.parseWaterLevelPressureString());
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isStartOfNewTrip(WaterLevelPressureDTO current, WaterLevelPressureDTO next) {
        try {
            // 判断是否VIN相同，如果不同则为新的时间段
            if (!current.getVin().equals(next.getVin())) {
                return true;
            }
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


    // 辅助方法，查找列表中是否已存在相同车辆和日期的记录
    private static DaWaterHeight findExistingRecord(List<DaWaterHeight> daWaterHeightList, String vin, LocalDate day) {
        for (DaWaterHeight existingRecord : daWaterHeightList) {
            if (existingRecord.getVin().equals(vin) && existingRecord.getDay().equals(day)) {
                return existingRecord;
            }
        }
        return null;
    }


    /**
     * 对指定文件进行加水量判断处理
     */


    List<DaWaterHeight> calculatedWaterConsumptionSingleTrip(String filePath) {

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterLevelPressureDTO.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();

        dataList.sort(
                Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime));


        List<DaWaterHeight> waterHeightSingleTrip = analyzeWaterHeightSingleTrip(dataList);

        return waterHeightSingleTrip;
    }

    List<DaWaterHeight> calculatedWaterConsumptionInterTrip(String filePath) {

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterLevelPressureDTO.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();

        // 指定 租赁车 列表
        List<String> specifiedVinList = Arrays.asList("LEWUMC1B3MF145710", "LEWUMC1BONF101620", "LEWUMC1BXMF145705", "LEWTEB120MF102803");

        List<WaterLevelPressureDTO> collect = dataList.stream()
                .filter(dto -> specifiedVinList.contains(dto.getVin()))
                .sorted(Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime))
                .collect(Collectors.toList());

        List<DaWaterHeight> waterHeightInterTrip = analyzeWaterHeightInterTrip(collect);

        return waterHeightInterTrip;
    }

    /**
     * 对该目录下的所有的文件进行加水量判断处理
     *
     * calculatedWaterConsumptionSingleTrip   对单个行程内进行处理  需要车速来辅助判断
     * calculatedWaterConsumptionInterTrip  对行程之间进行处理
     */
    @Test
    void testHandleProximitySwitchALLFile() {

        String directoryPath = "E:\\Python_code\\bigdata-analysis-model" +
                "\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\202312\\";

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".csv") && file.getName().startsWith("merged_water_height")) {
                    List<DaWaterHeight> waterHeights1 = calculatedWaterConsumptionSingleTrip(file.getAbsolutePath());
                    insertHeightData(waterHeights1);

                }
                if (file.isFile() && file.getName().endsWith(".csv") && file.getName().matches("^\\d{8}\\.csv$")) {
                    List<DaWaterHeight> waterHeights2 = calculatedWaterConsumptionInterTrip(file.getAbsolutePath());
                    insertHeightData(waterHeights2);
                }

            }
        }
    }

    boolean insertHeightData(List<DaWaterHeight> waterHeights) {
        return daWaterHeightService.saveOrUpdateBatchByMultiId(waterHeights);
    }

    /**
     * 测试单个文件是否添加正常
     */
    @Test
    void test() {
        String filePath1 = "E:\\Python_code\\bigdata-analysis-model\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\202311\\merged_water_height_20231126.csv";
        String filePath2 = "E:\\Python_code\\bigdata-analysis-model\\water_consumption_of_sprinkler_trucks\\query_data\\water_level\\202311\\20231107.csv";
//        insertHeightData(calculatedWaterConsumption(filePath));
        List<DaWaterHeight> waterHeights1 = calculatedWaterConsumptionSingleTrip(filePath1);
        // insertHeightData(waterHeights1);

        List<DaWaterHeight> waterHeights2 = calculatedWaterConsumptionInterTrip(filePath2);
        // insertHeightData(waterHeights2);
    }


}