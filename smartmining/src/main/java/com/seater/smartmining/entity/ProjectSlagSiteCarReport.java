package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/29 0029 14:32
 */
@Entity
@Table
@Data
public class ProjectSlagSiteCarReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long slagSiteId = 0L;           //渣场ID

    @Column
    private String slagSiteName = "";       //渣场名称

    @Column
    private Long carId = 0L;        //渣车ID

    @Column
    private String carCode = "";        //渣车编号

    @Column
    private Long distance = 0L;         //运距

    @Column
    private Long count = 0L;        //车数

    @Column
    @Enumerated(EnumType.ORDINAL)
    private ShiftsEnums shift = ShiftsEnums.UNKNOW;        //班次

    @Lob
    @Column(columnDefinition="text")
    private String detailJson = "";

    @Column
    private Date reportDate = null;         //统计时间

    @Column
    private Date createDate = null;     //创建时间
}
