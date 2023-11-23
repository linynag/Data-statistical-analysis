package com.ly.datastatisticalanalysis.model.dto;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class WaterLevelPressureDTO {
    private String vin;
    private String day;
    private String time;
    private String waterLevelPressure;

    // 新增方法，将String类型的时间转换为Date类型
    public Date parseTimeString() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(this.time);
    }

    // 新增方法，将String类型的水位高度转换为Double类型
    public Double parseWaterLevelPressureString() {
        return Double.parseDouble(this.waterLevelPressure);
    }
}
