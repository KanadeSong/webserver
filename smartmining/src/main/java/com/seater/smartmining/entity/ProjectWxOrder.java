package com.seater.smartmining.entity;

import com.seater.smartmining.enums.OrderEnum;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:微信订单实体类
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 12:31
 */
@Entity
@Table
@Data
public class ProjectWxOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;

    @Column
    private String orderNo = "";        //订单编号

    @Column
    private String appId = "";      //APPID

    @Column
    private String orderBody = "";      // 订单描述

    @Column
    private String openId = "";     //微信openId

    @Column
    private Long carId = 0L;    //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column
    private Long ownerId = 0L;      //车主ID

    @Column
    private String ownerName = "";      //车主名称

    @Column(nullable = false)
    private Long brandId = 0L;      //品牌ID

    @Column(nullable = false)
    private String brandName = ""; //品牌名

    @Column(nullable = false)
    private Long modelId = 0L;      //型号ID

    @Column(nullable = false)
    private String modelName = ""; //型号名

    @Column(columnDefinition = "text")
    private String carPicture = "";     //车照

    @Column
    private String driverId = "";       //  司机ID

    @Column
    private String driverName = "";     //司机名称

    @Column
    private String payPeople = "";      //付款人

    @Column
    private CarType carType = CarType.Unknow;

    @Column
    private Long shopId = 0L;       //商品ID

    @Column
    private String shopName = "";       //商品名称

    @Column
    private Date timeStart = null;      //交易开始时间

    @Column
    private Date timeExpire = null;     //交易结束时间

    @Column
    private String detailDescription = "";      //详情描述

    @Column
    private String wechatOrderNo = "";         //微信订单编号

    @Column
    private BigDecimal totalAmount = BigDecimal.ZERO;       //订单金额

    @Column
    private OrderEnum status = OrderEnum.Unknow;

    @Column
    private String refundReason = "";           //退款原因

    @Column
    private BigDecimal refundMoney = BigDecimal.ZERO;       //退款金额

    @Column
    private String refundNo = "";           //退款编号

    @Column
    private Date refundTime = null;         //退款时间

    @Column
    private Boolean valid = true;       //是否有效

    @Column
    private Date createTime = new Date();       //创建时间

    @Column
    private String prepayId = "";       //小程序预支付ID
}
