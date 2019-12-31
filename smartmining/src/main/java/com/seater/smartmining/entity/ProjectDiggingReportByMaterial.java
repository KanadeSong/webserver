package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/20 0020 10:41
 */
@Entity
@Table
@Data
public class ProjectDiggingReportByMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long machineId = 0L;

    @Column
    private String machineCode = "";

    @Column
    private Long materialId = 0L;

    @Column
    private String materialName = "";

    @Column
    private Date dateIdentification = null;         //日期标识

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;        //班次

    @Column
    private BigDecimal workTime = new BigDecimal(0);

    @Column
    private Date createDate = new Date();           //创建时间
}
