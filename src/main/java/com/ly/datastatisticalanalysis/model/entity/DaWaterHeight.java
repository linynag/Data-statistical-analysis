package com.ly.datastatisticalanalysis.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 加水统计表
 * @TableName DA_water_height
 */
@TableName(value ="DA_water_height")
@Data
public class DaWaterHeight implements Serializable {
    /**
     * VIN
     */
    @MppMultiId(value = "VIN")
    private String vin;

    /**
     * day
     */
    @MppMultiId(value = "day")
    private LocalDate day;

    /**
     * 时间
     */
    @MppMultiId(value = "time")
    private LocalDateTime time;

    /**
     * 加水次数
     */
    @TableField(value = "add_water_count")
    private Integer addWaterCount;

    /**
     * 加水量
     */
    @TableField(value = "add_water_quantity")
    private Integer addWaterQuantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}