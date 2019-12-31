package com.seater.smartmining.domain;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Description:小程序创建订单实体类
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:31
 */
@Data
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnifiedOrder {

    //小程序编号
    private String appid;
    //商户号
    private String mch_id;
    //设备号
    private String device_info = "WEB";
    //随机字符串
    private String nonce_str;
    //签名
    private String sign;
    //签名类型
    private String sign_type;
    //商品描述
    private String body;
    //商品详情
    private String 	detail;
    //附加数据
    private String attach;
    //商户订单编号
    private String 	out_trade_no;
    //标价币种
    private String fee_type;
    //标价金额
    private Integer total_fee;
    //终端IP
    private String spbill_create_ip;
    //交易起始时间
    private String 	time_start;
    //交易结束时间
    private String time_expire;
    //订单优惠标记
    private String 	goods_tag;
    //通知地址
    private String notify_url;
    //交易类型
    private String trade_type;
    //商品编号
    private String product_id;
    //指定支付方式
    private String limit_pay;
    //用户标识
    private String openid;
    //微信订单编号
    private String transaction_id;
    //唯一unionid
    private String unionid;
    //场景信息 H5支付必传
    private String scene_info;
}
