package com.seater.user.util.wx;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.StringUtils;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static cn.hutool.core.util.XmlUtil.xmlToMap;

/**
 * @Description 微信支付相关操作
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/29 14:07
 */
@Slf4j
public class WxPay {
    
//    @Autowired


    /**
     * 发起微信支付
     * @param openId
     * @param orderId
     * @param totalFee
     * @param request
     * @return
     */
    public static Map<String, Object> wxPay(String openId, String orderId, String totalFee, HttpServletRequest request) {
        try {
            //生成的随机字符串 订单编号
            String nonce_str = StringUtils.getRandomStringByLength(32);
            //商品名称
            String body = "测试商品名称";
            //获取客户端的ip地址
            String spbill_create_ip = IpUtil.getIpAddr(request);

            //组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", Constants.WX_APP_ID);
            packageParams.put("mch_id", Constants.WX_MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", orderId);//商户订单号
            packageParams.put("total_fee", totalFee);//支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", Constants.WX_NOTIFY_URL);//支付成功后的回调地址
            packageParams.put("trade_type", Constants.WX_TRADE_TYPE);//支付方式
            packageParams.put("openid", openId);

            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串 

            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
//            String mySign = SecureUtil.sha1(prestr);
            String mySign = PayUtil.sign(prestr, Constants.WX_KEY, "utf-8").toUpperCase();

            //拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml = "<xml>" + "<appid>" + Constants.WX_APP_ID + "</appid>"
                    + "<body><![CDATA[" + body + "]]></body>"
                    + "<mch_id>" + Constants.WX_MCH_ID + "</mch_id>"
                    + "<nonce_str>" + nonce_str + "</nonce_str>"
                    + "<notify_url>" + Constants.WX_NOTIFY_URL + "</notify_url>"
                    + "<openid>" + openId + "</openid>"
                    + "<out_trade_no>" + orderId + "</out_trade_no>"
                    + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                    + "<total_fee>" + totalFee + "</total_fee>"
                    + "<trade_type>" + Constants.WX_TRADE_TYPE + "</trade_type>"
                    + "<sign>" + mySign + "</sign>"
                    + "</xml>";

            log.info("微信小程序用户:{},   >>>>>   发起支付",openId);
            log.info("调试模式_统一下单接口 请求XML数据：{}" + xml);

            //调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest(Constants.WX_PAY_URL, "POST", xml);

            log.info("调试模式_统一下单接口 返回XML数据：{}", result);

            // 将解析结果存储在HashMap中   
//            Map map = PayUtil.doXMLParse(result);
            Map map = XmlUtil.xmlToMap(result);
            

            String return_code = (String) map.get("return_code");//返回状态码

//            Map<String, Object> response = new HashMap<String, Object>();
            JSONObject response = new JSONObject();//返回给小程序端需要的参数
            
            if (return_code.equals("SUCCESS")) {
                log.info("微信小程序用户:{},   >>>>>   支付成功",openId);
                String prepay_id = (String) map.get("prepay_id");//返回的预付单信息   
                response.put("nonceStr", nonce_str);
                response.put("package", prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                //拼接签名需要的参数
                String stringSignTemp = "appId=" + Constants.WX_APP_ID + "&nonceStr=" + nonce_str + "&package=prepay_id=" + prepay_id + "&signType=MD5&timeStamp=" + timeStamp;
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PayUtil.sign(stringSignTemp, Constants.WX_KEY, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }
            response.put("appid", Constants.WX_APP_ID);
            response.put("返回消息", map);
            
            return CommonUtil.successJson(response);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.errorJson("支付失败" + e.getMessage());
        }
    }

}