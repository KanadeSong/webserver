package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description 单台油车(给定时间计算的返回实体, 没有数据表)
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/6/9 10:00
 */
@Data
public class OilCarDayReportPartCar {

    @Column(nullable = false)
    private Long projectId = 0L;                    //  项目ID

    private Long oilCarId = 0L;         //  油车id

    private String oilCarCode = "";     //  油车编号

    @Column(nullable = false)
    private Long managerId;         //  该油车的管理人员 使用加油app的人

    @Column(nullable = false)
    private String managerName;         //  该油车的管理人员名称

    @Column(nullable = false)
    private Long earlyTotalVolume = 0L;     //早班总加油量

    private Long earlyTotalAmount = 0L;     //早班总加油金额

    @Column(nullable = false)
    private Long nightTotalVolume = 0L;     //晚班总加油量

    private Long nightTotalAmount = 0L;     //晚班总加油金额

    @Column(nullable = false)
    private Long totalVolumeInTime = 0L;          //时间段内总加油量

    @Column
    private Long totalAmountInTime = 0L;   //时间段内加油金额

    @Column(nullable = false)
    private Long warningVolumeInTime = 0L;   //  时间段内设定的预警量

    @Column(nullable = false)
    private Long totalReadVolumeInTime = 0L;          //时间段内每天的抄表合计

    private Long compareVolumeInTime = 0L;  //  时间端内计算出来的差异值
    
    @Column
    private Boolean isOver = false;     //  是否超过时间段内的预警量

}
