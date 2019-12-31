package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ProjectDeviceType;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/4 0004 10:16
 */
@Entity
@Table
@Data
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键编号

    @Column
    private String fileName = "";       //文件名

    @Column
    private Long fileSize = 0L;     //文件大小

    @Column
    private String softwareVersion = "";            //软件版本号

    @Column
    private String hardwareVersion = "";            //硬件版本号

    @Column
    private String deviceType = "";     //设备类型

    @Column
    private String aesText = "";
}
