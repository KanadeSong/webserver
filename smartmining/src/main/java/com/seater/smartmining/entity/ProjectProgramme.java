package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:调度模板 方案表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/11 0011 13:40
 */
@Entity
@Table
@Data
public class ProjectProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;

    @Column
    private String name = "";       //方案名称

    @Column
    private String scheduleTime = "";       //调度时间， 需要前端传cron时间格式

    @Column
    private Long createId = 0L;     //创建人ID

    @Column
    private Boolean start = false;      //是否启动

    @Column
    private String createName = "";     //创建人名称

    @Column
    private Date createTime = new Date();   //创建时间

    @Column
    private Long lastCreateId = 0L;

    @Column
    private String lastCreateName = "";

    @Column
    private Date lastModifyTime = new Date();       //上一次修改时间
}
