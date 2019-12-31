package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:其它设备日报表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/19 0019 12:23
 */
@Entity
@Table
@Data
public class ProjectOtherDeviceDayReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long deviceId = 0L;

    @Column
    private String deviceCode = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    private CarType carType = CarType.Unknow;

    @Column
    private BigDecimal workTime = BigDecimal.ZERO;

    @Column
    private BigDecimal amount = BigDecimal.ZERO;

    @Column
    private BigDecimal fillCount = BigDecimal.ZERO;

    @Column
    private BigDecimal fillAmount = BigDecimal.ZERO;

    @Column
    private Date createTime = null;

    @Column
    private Date dateIdentification = null;     //日期标识
}
