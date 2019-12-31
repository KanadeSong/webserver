package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 16:44
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "carId", "terminalTime"}, name = "repeatCheck")})
@Data
public class ProjectSlagCarLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private String cmdInd = "";

    @Column
    private Long pktID = 0L;

    @Column
    private String eventID = "";

    @Column
    private Long projectID = 0L;

    @Column
    private Date timeLoad = null;

    @Column
    private String carCode = "";

    @Column
    private Date timeCheck = null;

    @Column
    private Long carID = 0L;

    private Date timeDischarge = null;

    @Column
    private Long slagfieldID = 0L;

    @Column
    private Long excavatCurrent = 0L;

    @Column
    private Long excavatNext = 0L;

    @Column
    private Long m1fare = 0L;

    @Column
    private Long loader = 0L;

    @Column
    private Integer device = 0;

    @Column
    private Long timeStay = 0L;

    @Column
    private String uid = "";

    @Column
    private Integer priceMethod = 0;        //计价方式

    @Column
    private Integer schMode = 0;        //设备模式

    @Column
    private Integer dispatchMode = 0;           //调度模式

    @Column
    private String slagSiteID = "";        //允许倒渣的渣场ID 多个用逗号隔开

    @Column
    private Long exctDist = 0L;         //该渣车对应挖机的排班运距

    @Column
    private Date recviceDate = new Date();

    @Column
    private Long terminalTime = 0L;
}
