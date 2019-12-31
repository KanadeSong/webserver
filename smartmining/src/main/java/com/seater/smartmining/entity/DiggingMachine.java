package com.seater.smartmining.entity;

import com.seater.smartmining.enums.CheckStatus;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 微信小程序车主创建挖机,未进入项目
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:03
 */
@Data
@Entity
public class DiggingMachine implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long driverId = 0L;     //司机id

    @Column(nullable = true)
    private String driverName = ""; //司机名称

    @Column(nullable = true)
    private String carGroup;        //所属车队

    @Column(nullable = false)
    private Long ownerId = 0L;      //车主id

    @Column(nullable = false)
    private String ownerName = "";  //车主名称

    @Column(nullable = true)
    private String machineName;         //车主定义车名

    @Column(nullable = false)
    private Long brandId = 0L;      //品牌ID

    @Column(nullable = false)
    private String brandName = "";  //品牌名

    @Column(nullable = false)
    private Long modelId = 0L;      //型号ID

    @Column(nullable = false)
    private String modelName = ""; //型号名

    @Column
    private Boolean valid = true;      //  是否有效

    @Column(nullable = true)
    private String avatar;      //挖机图标 "," 分隔
    
    @Column(nullable = true)
    private Date addTime = new Date();  //  添加时间

    @Column
    private CheckStatus checkStatus = CheckStatus.UnCheck;  //  进入项目时的检查状态
    
    private Long projectDiggingMachineId = 0L;

    private String codeInProject = "";       // 项目内编号,加入项目前是空的,加入项目后填回

    private Long projectId = 0L;        // 项目id,加入项目前是空的,加入项目后填回

    private String projectName = "";    // 项目名称,加入项目前是空的,加入项目后填回

    @Column
    private Long shopId = 0L;       //商品ID

    @Column
    private String shopName = "";       //商品名称

    @Column
    private Boolean deducted = false;       //是否缴费

    @Column
    private Date deductedDate = null;       //缴费日期

    @Column
    private Date expireDate = null;         //到期日期
}
