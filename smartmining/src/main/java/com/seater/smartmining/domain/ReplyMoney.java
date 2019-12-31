package com.seater.smartmining.domain;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/22 0022 10:40
 */
@Data
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplyMoney {

    //小程序编号
    private String appid;
    //商户号
    private String mch_id;
    //随机字符串
    private String nonce_str;
    //签名
    private String sign;
    //签名类型
    private String sign_type;
    //微信订单号
    private String transaction_id;
    //商户订单号
    private String out_trade_no;
    //商户退款单号
    private String out_refund_no;
    //订单金额
    private Integer total_fee;
    //退款金额
    private Integer refund_fee;
    //货币种类
    private String refund_fee_type;
    //退款原因
    private String refund_desc;
    //退款资金来源
    private String refund_account;
    //退款结果通知url
    private String notify_url;
}
