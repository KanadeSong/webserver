package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/6 0006 17:21
 */
@Entity
@Table
@Data
public class ProjectMqttUpdateExct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private String cmdInd = "";

    @Column
    private Long pktID = 0L;

    @Column
    private Long projectID = 0L;

    @Column
    private Long slagcarID = 0L;

    @Column
    private String slagcarCode = "";

    @Column
    private String schexctCode = "";

    @Column
    private Date createTime = new Date();
}
