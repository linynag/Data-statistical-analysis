package com.ly.datastatisticalanalysis.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.ly.datastatisticalanalysis.listener.WaterConsumptionListener;
import com.ly.datastatisticalanalysis.model.dto.WaterLevelPressureDTO;
import com.ly.datastatisticalanalysis.model.entity.DaWaterHeight;
import com.ly.datastatisticalanalysis.service.DaWaterHeightService;
import com.ly.datastatisticalanalysis.service.WaterConsumptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WaterConsumptionImpl implements WaterConsumptionService {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Resource
    private DaWaterHeightService daWaterHeightService;

    public static List<DaWaterHeight> analyzeWaterHeight(List<WaterLevelPressureDTO> waterLevelPressureList) {
        // 单车每天的统计
        Map<String, Map<String, Map<String, Integer>>> result = new HashMap<>();
        List<DaWaterHeight> waterHeights = new ArrayList<>();
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
            analyzeSingleTrip(trip, waterHeights);
        }

        // 遍历行程间，进行统计
        for (int i = 0; i < trips.size() - 1; i++) {
            analyzeInterTrip(trips.get(i), trips.get(i + 1), waterHeights);
        }

        // 打印结果
        return waterHeights;
    }

    private static void analyzeSingleTrip(List<WaterLevelPressureDTO> trip
            , List<DaWaterHeight> waterHeights) {
        if (trip.size() > 1) {
            WaterLevelPressureDTO first = trip.get(0);
            WaterLevelPressureDTO last = trip.get(trip.size() - 1);

            String vin = first.getVin();
            String day = first.getDay();


            // 判断单个行程内是否有正常加水
            double waterLevelChange = last.parseWaterLevelPressureString() - first.parseWaterLevelPressureString();
            if (waterLevelChange > 20) {
                DaWaterHeight daWaterHeight = new DaWaterHeight();


                daWaterHeight.setVin(vin);
                daWaterHeight.setDay(LocalDate.parse(day));
                LocalDateTime dateTime = LocalDateTime.parse(first.getTime(), formatter);
                daWaterHeight.setTime(dateTime);
                daWaterHeight.setAddWaterQuantity((int) waterLevelChange);
                daWaterHeight.setAddWaterCount(1);
                waterHeights.add(daWaterHeight);

                // 记录为正常的加水
                String format = String.format("单个行程加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, first.getTime(), (int) waterLevelChange);
                System.out.printf(format);


            }
        }
    }

    private static void analyzeInterTrip(List<WaterLevelPressureDTO> firstTrip, List<WaterLevelPressureDTO> secondTrip,
                                         List<DaWaterHeight> waterHeights) {
        if (!firstTrip.isEmpty() && !secondTrip.isEmpty()) {
            WaterLevelPressureDTO lastOfFirst = firstTrip.get(firstTrip.size() - 1);
            WaterLevelPressureDTO firstOfSecond = secondTrip.get(0);

            String vin = lastOfFirst.getVin();
            String day = lastOfFirst.getDay();

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
                        DaWaterHeight daWaterHeight = new DaWaterHeight();

                        daWaterHeight.setVin(vin);
                        daWaterHeight.setDay(LocalDate.parse(day));
                        LocalDateTime dateTime = LocalDateTime.parse(lastOfFirst.getTime(), formatter);
                        daWaterHeight.setTime(dateTime);
                        daWaterHeight.setAddWaterQuantity((int) waterLevelChange);
                        daWaterHeight.setAddWaterCount(1);
                        waterHeights.add(daWaterHeight);
                        // 记录为正常的加水
                        String format = String.format("行程间加水： 车辆：%s  时间： %s 水位增加 %d \n", vin, lastOfFirst.getTime(), (int) waterLevelChange);
                        System.out.printf(format);
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


    /**
     * 对指定文件进行加水量判断处理
     */
    @Override
    public List<DaWaterHeight> calculatedWaterConsumption(String filePath) {

        WaterConsumptionListener listener = new WaterConsumptionListener();
        EasyExcelFactory.read(filePath, WaterLevelPressureDTO.class, listener).sheet().doRead();
        List<WaterLevelPressureDTO> dataList = listener.getDataList();

        dataList.sort(
                Comparator.comparing(WaterLevelPressureDTO::getVin)
                        .thenComparing(WaterLevelPressureDTO::getDay)
                        .thenComparing(WaterLevelPressureDTO::getTime));


        return analyzeWaterHeight(dataList);
    }

    /**
     * 对该目录下的所有的文件进行加水量判断处理
     */


    boolean insertHeightData(List<DaWaterHeight> waterHeights) {
        return daWaterHeightService.saveOrUpdateBatchByMultiId(waterHeights);
    }

    /**
     * 测试单个文件是否添加正常
     */


}
