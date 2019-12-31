package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 14:24
 */
@Entity
@Table
@Data
public class ProjectDeviceStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private String uid = "";        //终端 UID

    @Column
    private ProjectDeviceType projectDeviceType = ProjectDeviceType.Unknown;        //终端类型

    @Column
    private Date onlineTime = null;      //终端在线时间

    @Column
    private Date unlineTime = null;      //终端离线时间

    @Column
    private Date createTime = new Date();       //创建时间
}
