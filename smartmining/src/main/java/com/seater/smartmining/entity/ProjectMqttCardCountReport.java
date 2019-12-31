package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/13 0013 11:09
 */
@Entity
@Table
@Data
public class ProjectMqttCardCountReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long errorCode = 0L;

    @Column(columnDefinition = "text")
    private String message = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;        //调度模式

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Integer count = 0;      //数量

    @Column
    private Date dateIdentification = new Date(0);

    @Column
    private Shift shift = Shift.Unknown;

    @Column
    private Date createTime = new Date();
}
