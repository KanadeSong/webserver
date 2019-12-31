package com.seater.smartmining.constant;

/**
 * @Description:支付接口常量类
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 12:01
 */
public class WechatConstant {

    //证书路径
    //public static final String WXSSLBOOKPATH = "D:/file/apiclient_cert.p12";
    public static final String WXSSLBOOKPATH = "/usr/java/project/file/1371958102_cert.p12";
    //小程序AppId
    public static final String APPID = "wx54a37c6a3b5a64e1";
    //小程序秘钥
    public static final String APPSECRET = "5145d1bd77ac5b6e6906a7720cd3da97";
    //商户号 公司
    //public static final String MATCHID = "1371958102";
    //商户号 诺馨
    public static final String MATCHID = "1518267181";
    //商户秘钥 诺馨
    public static final String MATCHSECRET = "e5eb9458bdeb476686b42b605dd52e02";
    //商户秘钥 公司
    //public static final String MATCHSECRET = "541646434jljdjjdscfkdjhn5543dcjj";
    //创建订单
    public static final String WXORDERPATH = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //查询订单
    public static final String WXQUERYORDERPATH = "https://api.mch.weixin.qq.com/pay/orderquery";
    //关闭订单
    public static final String WXCLOSEORDERPATH = "https://api.mch.weixin.qq.com/pay/closeorder";
    //申请退款
    public static final String WXREPLYMONEYPATH = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //支付金额方式
    public static final String WXFEETYPE = "CNY";
    //JSAPI支付方式
    public static final String WXPAYTYPE = "JSAPI";
    //小程序支付回调地址
    public static final String WXPAYPAYSULTURL = "https://smartmining.seater.cn/api/v1/projectWechatPay/paysult/";
    //日期格式
    public static final String DATEYEARPATTERN = "yyyyMMdd";
    //&符号
    public static String ANDSIGN = "&";
    //等号
    public static String EQUALSIGN = "=";
    public static String COMMA = ",";
    //加密类型
    public static final String WXENCTYPE = "PKCS12";
    //调用成功
    public static final String WX_SUCCESS = "SUCCESS";
    //回调成功字符串
    public static final String WX_PAY_SUCCESS_BACK = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    //回调失败字符串
    public static final String WX_PAY_FAIL_BACK = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
    //获取AccessToken
    public static final String WX_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${appId}&secret=${secret}";
    //发送订阅消息地址
    public static final String WX_MODEL_MESSAGE_PATH = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=${accessToken}";
    //统一服务消息
    public static final String WX_SERVICE_MESSAGE_PATH = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=${accessToken}";
    //支付成功模板消息编号
    public static final String WX_PAY_SUCCESS_MODEL_NO = "ERhONRg7TzY42eCDd2ARKPwE0xkOZnht1dEdzv0IS-o";
    //到期续费模板消息编号
    public static final String WX_RENEW_MODEL_NO = "hSKkYxDjceG0ygL4raS4GIP8TEgqBXPPHtZVngcoCRc";
    //微信商城前缀地址
    public static final String WX_SHOP_SUFFIX_PATH = "http://shop.seater.cn";
    //微信商城key
    public static final String WX_SHOP_KEY = "390WIg6egr6mlfviJNjdxP1QTF4Zzjcr";
    //微信商城注册地址
    public static final String WX_SHOP_REGISTER_PATH = "/api/common/user/reg";

}
