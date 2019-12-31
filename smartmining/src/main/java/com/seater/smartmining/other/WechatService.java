package com.seater.smartmining.other;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.ReplyMoney;
import com.seater.smartmining.domain.SendModelMessage;
import com.seater.smartmining.domain.SendServiceMessage;
import com.seater.smartmining.domain.UnifiedOrder;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.OrderEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.constant.WechatConstant;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.smartmining.utils.wechat.MD5;
import com.seater.smartmining.utils.wechat.RedPackageUtils;
import com.seater.smartmining.utils.wechat.UUIDHexGenerator;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.BeanUtils;
import com.seater.smartmining.utils.wechat.XmlAndJavaObjectConvert;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.seater.smartmining.utils.wechat.ConnectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 11:49
 */
@Service
public class WechatService {

    @Autowired
    private ProjectWxOrderServiceI projectWxOrderServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectCarSetMealServiceI projectCarSetMealServiceI;
    //凭据 AccessToken
    private String accessToken;
    //凭据的失效时间
    private Long expiresTime;

    /**
     * 创建订单接口
     * @param unifiedOrder
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map<String,Object> unifiedOrder(UnifiedOrder unifiedOrder) throws IOException, DocumentException {
        ProjectWxOrder wxOrder = new ProjectWxOrder();
        ProjectCarSetMeal projectCarSetMeal = projectCarSetMealServiceI.get(Long.parseLong(unifiedOrder.getProduct_id()));
        if(projectCarSetMeal == null)
            throw new SmartminingProjectException("商品编号不存在");
        wxOrder.setShopId(projectCarSetMeal.getId());
        wxOrder.setShopName(projectCarSetMeal.getName());
        Map<String,Object> param = new HashMap<>();
        param.put("openid",unifiedOrder.getOpenid());
        unifiedOrder.setAppid(WechatConstant.APPID);
        //设置商户编号
        unifiedOrder.setMch_id(WechatConstant.MATCHID);
        //随机字符串
        String nonceStr = UUIDHexGenerator.getInstance().generate();
        unifiedOrder.setNonce_str(nonceStr);
        //日期
        String today = DateUtils.formatDateByPattern(new Date(), WechatConstant.DATEYEARPATTERN);
        //10位随机数
        String code = RedPackageUtils.createCode(10);
        //设置订单编号
        unifiedOrder.setOut_trade_no(WechatConstant.MATCHID + today + code);
        Map<String, Object> params = BeanUtils.toMap(unifiedOrder);
        Map<String, Object> spara = RedPackageUtils.paraFilter(params);
        String prestr = RedPackageUtils.createLinkString(spara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String key = WechatConstant.ANDSIGN + "key" + WechatConstant.EQUALSIGN + WechatConstant.MATCHSECRET; //商户支付密钥
        String mysign = MD5.sign(prestr, key, SmartminingConstant.ENCODEUTF).toUpperCase();
        //设置签名
        unifiedOrder.setSign(mysign);
        //拆分附加数据 拿到车辆类型和车辆编号
        String[] attachs = unifiedOrder.getAttach().split(WechatConstant.COMMA);
        if(attachs == null || attachs.length < 3)
            throw new SmartminingProjectException("请输入车辆编号和车辆类型");
        String carCode = attachs[0];
        String carType = attachs[1];
        Long projectId = Long.parseLong(attachs[2]);
        wxOrder.setCarCode(carCode);
        wxOrder.setProjectId(projectId);
        if(carType.equals("slagCar")) {
            wxOrder.setCarType(CarType.SlagCar);
            ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
            if(projectCar == null)
                throw new SmartminingProjectException("请输入正确的渣车编号");
            wxOrder.setCarId(projectCar.getId());
            wxOrder.setOwnerId(projectCar.getOwnerId());
            wxOrder.setOwnerName(projectCar.getOwnerName());
            wxOrder.setModelId(projectCar.getModelId());
            wxOrder.setModelName(projectCar.getModelName());
            wxOrder.setBrandId(projectCar.getBrandId());
            wxOrder.setBrandName(projectCar.getBrandName());
            wxOrder.setDriverId(projectCar.getDriverId());
            wxOrder.setDriverName(projectCar.getDriverName());
            wxOrder.setCarPicture(projectCar.getPicturePath());
        } else if(carType.equals("diggingMachine")) {
            wxOrder.setCarType(CarType.DiggingMachine);
            ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, carCode);
            if(diggingMachine == null)
                throw new SmartminingProjectException("请输入正确的挖机编号");
            wxOrder.setCarId(diggingMachine.getId());
            wxOrder.setOwnerId(diggingMachine.getOwnerId());
            wxOrder.setOwnerName(diggingMachine.getOwnerName());
            wxOrder.setModelId(diggingMachine.getModelId());
            wxOrder.setModelName(diggingMachine.getModelName());
            wxOrder.setBrandId(diggingMachine.getBrandId());
            wxOrder.setBrandName(diggingMachine.getBrandName());
            wxOrder.setDriverId(diggingMachine.getDriverId());
            wxOrder.setDriverName(diggingMachine.getDriverName());
            wxOrder.setCarPicture(diggingMachine.getPicturePath());
        } else {
            throw new SmartminingProjectException("请输入正确的车辆类型");
        }
        String respXml = XmlAndJavaObjectConvert.convertToXml(unifiedOrder);
        //CloseableHttpClient closeableHttpClient = ConnectionUtils.defaultSSLClientFile(WechatConstant.WXENCTYPE, WechatConstant.WXSSLBOOKPATH, unifiedOrder.getMch_id());
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        String result = ConnectionUtils.httpsClient(WechatConstant.WXORDERPATH, respXml, closeableHttpClient);
        Map<String,Object> map = XmlAndJavaObjectConvert.parseXmlStr(result);
        if(!map.get("return_code").equals(WechatConstant.WX_SUCCESS))
            throw new SmartminingProjectException(map.get("return_msg").toString());
        Map<String,Object> signMap = new HashMap<>();
        signMap.put("appId",unifiedOrder.getAppid());
        signMap.put("package","prepay_id=" + map.get("prepay_id"));
        String nonceStrSec = UUIDHexGenerator.getInstance().generate();
        signMap.put("nonceStr",nonceStrSec);
        Long now = System.currentTimeMillis()/1000L;
        signMap.put("timeStamp",now);
        signMap.put("signType","MD5");
        Map<String,Object> signPara = RedPackageUtils.paraFilter(signMap);
        String signStr = RedPackageUtils.createLinkString(signPara);
        String secondSign = MD5.sign(signStr, key, SmartminingConstant.ENCODEUTF).toUpperCase();
        signMap.put("paySign",secondSign);
        wxOrder.setPrepayId(map.get("prepay_id").toString());
        wxOrder.setOrderNo(unifiedOrder.getOut_trade_no());
        wxOrder.setStatus(OrderEnum.CREATE);
        wxOrder.setOrderBody(unifiedOrder.getBody());
        if(StringUtils.isNotEmpty(unifiedOrder.getDetail()))
            wxOrder.setDetailDescription(unifiedOrder.getDetail());
        wxOrder.setOpenId(unifiedOrder.getOpenid());
        wxOrder.setAppId(WechatConstant.APPID);
        BigDecimal payMoney = new BigDecimal((float)unifiedOrder.getTotal_fee() / 100).setScale(2, BigDecimal.ROUND_CEILING);
        wxOrder.setTotalAmount(payMoney);
        projectWxOrderServiceI.save(wxOrder);
        return signMap;
    }

    /**
     * 订单信息接口
     * @param choose   1=查询订单信息   2=关闭订单
     * @return
     * @throws UnsupportedEncodingException
     */
    public JSONObject queryOrder(String orderNo, Integer choose) throws UnsupportedEncodingException {
        String path = null;
        if(choose == 1){
            path = WechatConstant.WXQUERYORDERPATH;
        }else if(choose == 2){
            path = WechatConstant.WXCLOSEORDERPATH;
        }
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setAppid(WechatConstant.APPID);
        unifiedOrder.setMch_id(WechatConstant.MATCHID);
        //微信订单号
        unifiedOrder.setTransaction_id(orderNo);
        //随机字符串
        String nonceStr = UUIDHexGenerator.getInstance().generate();
        unifiedOrder.setNonce_str(nonceStr);
        Map<String, Object> params = BeanUtils.toMap(unifiedOrder);
        Map<String, Object> spara = RedPackageUtils.paraFilter(params);
        String prestr = RedPackageUtils.createLinkString(spara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String key = WechatConstant.ANDSIGN + "key" + WechatConstant.EQUALSIGN + WechatConstant.MATCHSECRET; //商户支付密钥
        String mysign = MD5.sign(prestr, key, SmartminingConstant.ENCODEUTF).toUpperCase();
        //设置签名
        unifiedOrder.setSign(mysign);
        String respXml = XmlAndJavaObjectConvert.convertToXml(unifiedOrder);
        CloseableHttpClient closeableHttpClient = ConnectionUtils.defaultSSLClientFile(WechatConstant.WXENCTYPE, WechatConstant.WXSSLBOOKPATH, unifiedOrder.getMch_id());
        String result = ConnectionUtils.httpsClient(path,respXml,closeableHttpClient);
        JSONObject object = JSON.parseObject(result);
        if(!object.getString("result_code").equals(WechatConstant.WX_SUCCESS))
            throw new SmartminingProjectException(object.getString("return_msg"));
        return object;
    }

    /**
     * 退款类接口
     * @param projectWxOrder
     * @return
     * @throws UnsupportedEncodingException
     */
    public Map<String, Object> replyMoney(ProjectWxOrder projectWxOrder) throws IOException, DocumentException {
        String path = WechatConstant.WXREPLYMONEYPATH;
        ReplyMoney replyMoney = new ReplyMoney();
        //appId
        replyMoney.setAppid(WechatConstant.APPID);
        //商户编号
        replyMoney.setMch_id(WechatConstant.MATCHID);
        //微信订单编号
        replyMoney.setTransaction_id(projectWxOrder.getWechatOrderNo());
        //订单总金额
        replyMoney.setTotal_fee((projectWxOrder.getTotalAmount().multiply(new BigDecimal(100))).intValue());
        //退款原因
        replyMoney.setRefund_desc(projectWxOrder.getRefundReason());
        //退款金额
        Integer refundMoney = projectWxOrder.getRefundMoney().multiply(new BigDecimal(100)).intValue();
        //退款金额
        replyMoney.setRefund_fee(refundMoney);
        //随机字符串
        String nonceStr = UUIDHexGenerator.getInstance().generate();
        replyMoney.setNonce_str(nonceStr);
        //日期
        String today = DateUtils.formatDateByPattern(new Date(),WechatConstant.DATEYEARPATTERN);
        //10位随机数
        String code = RedPackageUtils.createCode(10);
        replyMoney.setOut_refund_no(WechatConstant.MATCHID + today + code);

        Map<String, Object> params = BeanUtils.toMap(replyMoney);
        Map<String, Object> spara = RedPackageUtils.paraFilter(params);
        String prestr = RedPackageUtils.createLinkString(spara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String key = WechatConstant.ANDSIGN + "key" + WechatConstant.EQUALSIGN + WechatConstant.MATCHSECRET; //商户支付密钥
        String mysign = MD5.sign(prestr, key, SmartminingConstant.ENCODEUTF).toUpperCase();
        //设置签名
        replyMoney.setSign(mysign);
        String respXml = XmlAndJavaObjectConvert.convertToXml(replyMoney);
        CloseableHttpClient closeableHttpClient = ConnectionUtils.defaultSSLClientFile(WechatConstant.WXENCTYPE, WechatConstant.WXSSLBOOKPATH, replyMoney.getMch_id());
        String result = ConnectionUtils.httpsClient(path,respXml,closeableHttpClient);
        Map<String,Object> map = XmlAndJavaObjectConvert.parseXmlStr(result);
        if(!map.get("result_code").equals(WechatConstant.WX_SUCCESS))
            throw new SmartminingProjectException(map.get("return_msg").toString());
        projectWxOrder.setRefundNo(WechatConstant.APPID + today + code);
        //请求参数
        Map<String,Object> request = new HashMap<>();
        request.put("transactionno",projectWxOrder.getWechatOrderNo());
        //订单对象
        ProjectWxOrder order = projectWxOrderServiceI.getAllByWechatOrderNo(projectWxOrder.getWechatOrderNo());
        order.setRefundMoney(projectWxOrder.getRefundMoney().add(order.getRefundMoney()));
        order.setRefundNo(replyMoney.getOut_refund_no());
        order.setRefundTime(new Date());
        projectWxOrderServiceI.save(order);
        return map;
    }

    /**
     * 获取AccessToken
     * @return
     */
    public String getAccessToken() {
        //当AccessToken为空或者失效才重新获取
        if(accessToken==null||new Date().getTime()>expiresTime) {
            String accessTokenUrl = WechatConstant.WX_GET_ACCESS_TOKEN.replace("${appId}", WechatConstant.APPID).replace("${secret}", WechatConstant.APPSECRET);
            JSONObject object = ConnectionUtils.httpGet(accessTokenUrl);
            if (object.get("access_token") != null) {
                accessToken = object.getString("access_token");
                //获取有效事件
                Long expiresIn = object.getLong("expires_in");
                expiresTime = new Date().getTime() + ((expiresIn-60)*1000);
                return accessToken;
            }
            throw new SmartminingProjectException(object.getString("errmsg"));
        }
        return accessToken;
    }

    /**
     * 模板消息发送
     * @param message  模板发送的对象
     * @return
     */
    public JSONObject sendModelMessage(SendModelMessage message){
        String accessToken = getAccessToken();
        String sendMessagePath = WechatConstant.WX_MODEL_MESSAGE_PATH.replace("${accessToken}", accessToken);
        String request = JSON.toJSONString(message);
        JSONObject object = ConnectionUtils.httpPost(sendMessagePath,request);
        if(object.getInteger("errcode")!=0)
            throw new SmartminingProjectException(object.getString("errmsg") + "，异常详情：" + JSON.toJSONString(object));
        return object;
    }

    /**
     * 统一服务消息发送
     * @param message  模板发送的对象
     * @return
     */
    public JSONObject sendServiceMessage(SendServiceMessage message){
        String accessToken = getAccessToken();
        String sendMessagePath = WechatConstant.WX_SERVICE_MESSAGE_PATH.replace("${accessToken}", accessToken);
        String request = JSON.toJSONString(message);
        JSONObject object = ConnectionUtils.httpPost(sendMessagePath,request);
        if(object.getInteger("errcode")!=0)
            throw new SmartminingProjectException(object.getString("errmsg") + "，异常详情：" + JSON.toJSONString(object));
        return object;
    }

    public JSONObject registerShop(String telephone, String nickName, String password, String payPassword) throws UnsupportedEncodingException {
        String text = "tel" + telephone + "nickname" + nickName + "password" + password + "pay_password" + payPassword;
        Map<String, Object> params = new HashMap<>();
        params.put("tel", telephone);
        params.put("nickname", nickName);
        params.put("password", password);
        params.put("pay_password", payPassword);
        //String text = RedPackageUtils.createLinkString(params);
        String sign = MD5.shopSign(text, WechatConstant.WX_SHOP_KEY);
        params.put("sign", sign);
        String content = RedPackageUtils.createLinkString(params);
        String url = WechatConstant.WX_SHOP_SUFFIX_PATH + WechatConstant.WX_SHOP_REGISTER_PATH;
        JSONObject jsonObject = ConnectionUtils.httpPost(url, content);
        return jsonObject;
    }
}
