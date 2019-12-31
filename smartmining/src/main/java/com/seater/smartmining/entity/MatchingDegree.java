package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.enums.TimeTypeEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 10:46
 */
@Entity
@Table
@Data
public class MatchingDegree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private TimeTypeEnum timeType = TimeTypeEnum.UNKNOW;        //时间类型

    @Column
    private Long carId = 0L;        //渣车ID

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Long finishCount = 0L;      //完成车数

    @Column
    private Long uploadCountByMachine = 0L;     //挖机上传车数

    @Column
    private Long uploadCountByCar = 0L;         //渣场上传车数(带装载时间)

    @Column
    private Long uploadCountByCheck = 0L;       //检测终端上传车数

    @Column
    private Long uploadCountByCheckTime = 0L;       // 渣场上传车数(带检测时间)

    @Column
    private Long uploadTotalCountByCar = 0L;        //渣场上传总车数

    @Column
    private Long uploadCountByCarDevice = 0L;       //渣车上传车数

    @Column
    private BigDecimal locationPercent = new BigDecimal(0);         //定位成功率  渣车上传车数/渣场上传车数

    @Column
    private BigDecimal degreePercent = new BigDecimal(0);       //匹配率

    @Column
    private BigDecimal writeCardPercent = new BigDecimal(0);        //写卡成功率

    @Column
    private BigDecimal finishPercent = new BigDecimal(0);       //完成率

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;            //班次

    @Column
    private Long unValidCount = 0L;     //上传无效车数

    @Column
    private Date startTime = new Date(0);           //开始时间

    @Column
    private Date endTime = new Date(0);             //结束时间

    @Column
    private Long noMachineIdCount = 0L;         //挖机编号为0的数量

    @Column
    private Date dateIdentification = null;

    @Column
    private Date createTime = new Date(0);
}
