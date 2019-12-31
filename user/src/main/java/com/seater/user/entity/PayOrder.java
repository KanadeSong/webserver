package com.seater.user.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description 微信订单
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/29 11:52
 */
@Data
//@Entity
public class PayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String total_fee;//总金额 
    private String body;//商品描述 
    private String detail;//商品详情    
    private String attach;//附加数据 
    private String time_start;//交易起始时间 
    private String time_expire;//交易结束时间 
    private String openid;//用户标识


}
