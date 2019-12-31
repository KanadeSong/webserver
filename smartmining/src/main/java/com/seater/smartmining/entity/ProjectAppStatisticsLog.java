package com.seater.smartmining.entity;

import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;
import org.springframework.dao.DataAccessException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/11 0011 14:34
 */
@Table
@Entity
@Data
public class ProjectAppStatisticsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Integer carAttendanceCount = 0;       //渣车出勤数

    @Column
    private Integer carOnLineCount = 0;           //渣车上线数

    @Column
    private BigDecimal carPercent = new BigDecimal(0);      //渣车开工率

    @Column
    private Integer diggingMachineAttendanceCount = 0;        //挖机出勤数

    @Column
    private Integer diggingMachineOnLineCount = 0;            //挖机在线数

    @Column
    private BigDecimal diggingMachinePercent = new BigDecimal(0);   //挖机开工率

    @Column
    private Long totalCubic = 0L;       //装载总方量

    @Column
    private Integer exceptionCount = 0;         //异常车数

    @Column
    private Integer workOnCount = 0;        //装载车数

    @Column
    private Long avgCubic = 0L;        //平均装载/小时

    private BigDecimal avgCars = new BigDecimal(0);     //平均车辆

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;        //班次

    @Column
    private Long totalTime = 0L;        //总台时

    @Column
    private Date reportDate = new Date(0L);         //统计时间

    @Column
    private Date createDate = new Date(0L);         //创建时间

    @Column
    private Long projectId = 0L;

    @Column
    private Integer unLoadCount = 0;    //未卸载车数

    @Column
    private Integer toCheckCount = 0;    //待检验车数

    @Column
    private Integer finishCount = 0;    //完成车数

    @Column
    private BigDecimal passPercent = new BigDecimal(0); //合格率

    @Column
    private Long shiftFill = 0L;    //当班加油量

    @Column
    private Long mileCount = 0L;    //里程数

    @Transient
    private List<JSONObject> carPerCube;    //装载材料车数/方数（根据材料）
    
}
