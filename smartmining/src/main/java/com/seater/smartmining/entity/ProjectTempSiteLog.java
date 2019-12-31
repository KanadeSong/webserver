package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/21 0021 11:24
 */
@Entity
@Table
@Data
public class ProjectTempSiteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Long carId = 0L;

    @Column
    private String carCode = "";

    @Column
    private Long diggingMachineId = 0L;

    @Column
    private String diggingMachineCode = "";

    @Column
    private Date timeLoad = null;

    @Column
    private Date timeDischarge = null;

    @Column
    private Date timeCheck = null;

    @Column
    private Long slagSiteId = 0L;

    @Column
    private String slagSiteName = "";

    @Column
    private Long terminalTime = 0L;       //终端上传的时间戳

    @Column
    private String remark = "";     //备注

    @Column
    private Date createTime = null;

    @Column
    private boolean valid = true;

    @Column
    private Long distance = 0L;

}
