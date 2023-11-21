package com.ly.datastatisticalanalysis.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @TableName DA_proximity_switch
 */
@TableName(value = "DA_proximity_switch")
@Data
public class DaProximitySwitch implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * vin
     */
    @MppMultiId
    @TableField("vin")
    private String vin;
    /**
     * 日期
     */
    @MppMultiId
    @TableField("day")
    private LocalDate day;
    /**
     * 输出信号0次数
     */
    @TableField(value = "count_input_signal0")
    private Integer countInputSignal0;
    /**
     * 输出信号1次数
     */
    @TableField(value = "count_input_signal1")
    private Integer countInputSignal1;
    /**
     * 输出信号2次数
     */
    @TableField(value = "count_input_signal2")
    private Integer countInputSignal2;
    /**
     * 输出信号3次数
     */
    @TableField(value = "count_input_signal3")
    private Integer countInputSignal3;
    /**
     * 输出信号4次数
     */
    @TableField(value = "count_input_signal4")
    private Integer countInputSignal4;
    /**
     * 输出信号5次数
     */
    @TableField(value = "count_input_signal5")
    private Integer countInputSignal5;
    /**
     * 输出信号6次数
     */
    @TableField(value = "count_input_signal6")
    private Integer countInputSignal6;
    /**
     * 输出信号7次数
     */
    @TableField(value = "count_input_signal7")
    private Integer countInputSignal7;
    /**
     * 输出信号8次数
     */
    @TableField(value = "count_input_signal8")
    private Integer countInputSignal8;
    /**
     * 输出信号9次数
     */
    @TableField(value = "count_input_signal9")
    private Integer countInputSignal9;
    /**
     * 输出信号10次数
     */
    @TableField(value = "count_input_signal10")
    private Integer countInputSignal10;
    /**
     * 输出信号11次数
     */
    @TableField(value = "count_input_signal11")
    private Integer countInputSignal11;
    /**
     * 输出信号12次数
     */
    @TableField(value = "count_input_signal12")
    private Integer countInputSignal12;
    /**
     * 输出信号13次数
     */
    @TableField(value = "count_input_signal13")
    private Integer countInputSignal13;
    /**
     * 输出信号14次数
     */
    @TableField(value = "count_input_signal14")
    private Integer countInputSignal14;
    /**
     * 输出信号15次数
     */
    @TableField(value = "count_input_signal15")
    private Integer countInputSignal15;
}