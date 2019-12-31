package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ShiftsEnums;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:爆破工作表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/11 0011 10:21
 */
@Entity
@Table
@Data
public class ProjectBlastWorkInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;

    @Column
    private String carCode = "";       //钻孔机编号

    @Column
    private Long apertureId = 0L;       //钻孔ID

    @Column
    private String apertureName = "";       //钻孔名称

    @Column
    private ShiftsEnums shifts = ShiftsEnums.UNKNOW;        //班次

    @Column
    private Long explosiveId = 0L;      //炸药ID

    @Column
    private String explosiveName = "";      //炸药名称

    @Column
    private BigDecimal explosiveCount = BigDecimal.ZERO;        //炸药数量

    @Column
    private BigDecimal apertureMeters = BigDecimal.ZERO;        //钻孔工作距离 单位米

    @Column
    private BigDecimal amount = BigDecimal.ZERO;        //工作金额

    @Column
    private Long createId = 0L;         //创建人ID

    @Column
    private String createName = "";         //创建人账号

    @Column
    private String remark = "";     //备注

    @Column
    private Date createTime = null;     //创建时间

    @Column
    private Date dateIdentification = null;     //日期标识
}
