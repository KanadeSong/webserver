package com.seater.smartmining.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/19 0019 11:10
 */
@Entity
@Table
@Data
public class ProjectDiggingReportByPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long machineId = 0L;        //挖机ID

    @Column
    private String machineCode = "";        //挖机编号

    @Column
    private Date dateIdentification = null;         //日期标识

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;        //班次

    @Column
    private BigDecimal workTime = new BigDecimal(0);

    @Column
    private Long placeId = 0L;          //平台ID

    @Column
    private String placeName = "";          //平台名称

    @Column
    private Date createDate = new Date();           //创建时间
}
