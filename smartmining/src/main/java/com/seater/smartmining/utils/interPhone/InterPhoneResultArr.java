package com.seater.smartmining.utils.interPhone;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;

/**
 * @Description 第三方对讲接口的数据解析和请求类 接受/请求的数据(data)类型是数组
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/15 15:27
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class InterPhoneResultArr extends InterPhoneResultBase {
    
    private JSONArray data;    //  返回数据 类型位数组

    public InterPhoneResultArr() {
        super();
    }
}
