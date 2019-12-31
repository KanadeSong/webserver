package com.seater.smartmining.domain;

import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/23 0023 14:01
 */
@Data
public class SendModelMessage {

    private String touser;      //接收者的openId
    private String template_id;     //所需下发的订阅模板ID
    private String page;    //点击模板后跳转的页面  非必须 不填不跳转
    private Map data;        //模板内容
    private String form_id = "";        //支付编号
}
