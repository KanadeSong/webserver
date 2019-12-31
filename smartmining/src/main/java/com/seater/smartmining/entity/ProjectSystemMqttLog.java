package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import com.seater.smartmining.enums.ProjectMqttEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/21 0021 12:03
 */
@Entity
@Table
@Data
public class ProjectSystemMqttLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //ID

    @Column
    private String uid = "";        //终端UID

    @Column
    private String projectCode = "";        //项目中终端的编号

    @Column
    private ProjectDeviceType projectDevice = ProjectDeviceType.Unknown;        //终端类型

    @Column
    private ProjectMqttEnum projectMqtt = ProjectMqttEnum.Unknow;       //终端请求类型

    @Column(columnDefinition = "text")
    private String requestParams = "";      //请求参数

    @Column
    private Boolean valid = false;       //是否请求成功

    @Column(columnDefinition = "text")
    private String responseParams = "";     //响应数据

    @Column(columnDefinition = "text")
    private String elseInfo = "";       // 其他信息

    @Column(columnDefinition = "text")
    private String remark = "";     //备注

    @Column(columnDefinition = "text")
    private String errorMessage = "";       //异常信息

    @Column
    private ProjectMqttEnum projectMqttEnum = ProjectMqttEnum.Unknow;

    @Column
    private Date createTime = new Date();
}
