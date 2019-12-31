package com.seater.smartmining.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/3 0003 16:26
 */
@Data
public class SendServiceMessage {

    private String touser = "";     //接受者
    private SendModelMessage weapp_template_msg = null;     //模板消息内容
}
