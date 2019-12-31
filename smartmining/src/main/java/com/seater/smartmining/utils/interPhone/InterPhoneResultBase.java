package com.seater.smartmining.utils.interPhone;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;

/**
 * @Description 第三方对讲接口的数据解析和请求基类
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/19 19:02
 */
@Data
@Slf4j
public class InterPhoneResultBase {

    private HttpCookie cookie;      //  cookie
    private HttpHeaders headers;    //  请求头
    private Integer code;           //  返回状态码
    private String msg;             //  返回消息
    private String token;           //  返回token
    
    /**
     * 设置请求头 登陆成功后需要用到
     */
    public void setSession() {
        headers.add("token", token);
        headers.add(HttpHeaders.COOKIE, cookie.getName() + "=" + cookie.getValue());
        log.info("请求头状态     >>>>>   {}", JSONObject.toJSONString(headers));
    }
}
