package com.seater.user.util.aliyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.seater.user.util.constants.Constants;

/**
 * @Description 阿里云短信验证工具
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 17:33
 */
public class SmsUtils {
    //随机生成验证码
    public synchronized static int getNewCode() {
        int i = (int) (Math.random() * 9999) + 100;
        if (i < 1000){
            i = getNewCode();   //  至少四位
        }
        return  i; //每次调用生成一次四位数的随机数;
    }
    //  版本,固定值
    private static final String VERSION = "2017-05-25";
    //  短信服务,固定值
    private static final String ACTION = "SendSms";
    //  产品域名,开发者无需替换
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    
    public static JSONObject sendSms(String mobile){
        //  https://api.aliyun.com/new#/?product=Dysmsapi&api=SendSms 基于阿里云短信服务参考和修改
        int validCode = getNewCode();
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",          // 地域ID
                Constants.ALY_KEY,      // RAM账号的AccessKey ID
                Constants.ALY_SECRET); // RAM账号Access Key Secret
        
        IAcsClient client = new DefaultAcsClient(profile);
        // 创建API请求并设置参数
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain(DOMAIN);
        request.setVersion(VERSION);
        request.setAction(ACTION);
        request.putQueryParameter("RegionId", Constants.ALY_SMS_LOCATION);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", Constants.ALY_SMS_SIGN_NAME);
        request.putQueryParameter("TemplateCode", Constants.ALY_SMS_TEMPLATE_CODE);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + validCode + "\"}");
        
        // 发起请求并处理应答或异常
        CommonResponse response = new CommonResponse();
        
        //  返回结果对象
        JSONObject jsonObject = new JSONObject();
        try {
            response = client.getCommonResponse(request);
            jsonObject = JSONObject.parseObject(response.getData());
            jsonObject.put("mobile",mobile);
            jsonObject.put("validCode",validCode);
//            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
            jsonObject.put("error",e.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
            jsonObject.put("error",e.getMessage());
        }
        return jsonObject;
    }
}
