package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectCarStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/18 0018 10:51
 */
@Entity
@Table
@Data
public class ProjectWorkTimeByCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long carId = 0L;

    @Column
    private String carCode = "";

    //工作开始时间
    @Column(nullable = false)
    private Date startTime = null;

    //工作结束时间
    @Column
    private Date endTime = null;

    //班次
    @Column
    private Shift shift = Shift.Unknown;

    //状态
    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectCarStatus status = ProjectCarStatus.Unknow;

    @Column
    private Date createTime = new Date();
}
