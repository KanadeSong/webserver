package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 15:14
 */
@Entity
@Table
@Data
public class ProjectUserTrajectoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long userId = 0L;       //用户ID

    @Column
    private String userAccount = "";        //用户账号

    @Column(columnDefinition = "text")
    private String runningTrajectory = "";          //对应用户的定位信息

    @Column
    private Date createTime = new Date();       //创建时间
}
