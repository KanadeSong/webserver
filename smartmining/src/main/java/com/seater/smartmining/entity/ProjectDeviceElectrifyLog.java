package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/10 0010 16:22
 */
@Entity
@Table
@Data
public class ProjectDeviceElectrifyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //项目ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private String uid = "";        //设备UID

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ProjectDeviceType deviceType = ProjectDeviceType.Unknown;     //设备类型

    @Column
    private Date electrifyTime = new Date(0);

    @Column
    private Date createTime = new Date();
}
