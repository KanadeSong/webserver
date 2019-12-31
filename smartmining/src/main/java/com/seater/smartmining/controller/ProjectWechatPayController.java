package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.constant.WechatConstant;
import com.seater.smartmining.domain.SendModelMessage;
import com.seater.smartmining.domain.SendServiceMessage;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.OrderEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.other.WechatService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.wechat.XmlAndJavaObjectConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/3 0003 10:58
 */
@RestController
@RequestMapping("/api/projectWechatPay")
public class ProjectWechatPayController {

    @Autowired
    private WechatService wechatService;
    @Autowired
    private ProjectWxOrderServiceI projectWxOrderServiceI;
    @Autowired
    private ProjectCarSetMealServiceI projectCarSetMealServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private SmartminingExceptionService smartminingExceptionService;
    @Autowired
    private SlagCarServiceI slagCarServiceI;

    /**
     * 小程序回调地址
     *
     * @param request
     */
    @RequestMapping("/paysult")
    @ResponseBody
    @Transactional
    public String paySult(HttpServletRequest request) {
        String return_code = null;
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String resultStr = new String(outSteam.toByteArray(), SmartminingConstant.ENCODEUTF);
            Map<String, Object> resultMap = XmlAndJavaObjectConvert.parseXmlStr(resultStr);
            return_code = resultMap.get("return_code").toString();
            //订单编号 我方生成的
            String orderNo = resultMap.get("out_trade_no").toString();
            //支付方式编号
            String payTypeNo = resultMap.get("bank_type").toString();
            if (return_code.equals(WechatConstant.WX_SUCCESS)) {
                //付款金额
                Integer payMoney = Integer.valueOf(resultMap.get("total_fee").toString());
                //微信用户编号
                String openId = resultMap.get("openid").toString();
                String appId = resultMap.get("appid").toString();
                String wechatOrderNo = resultMap.get("transaction_id").toString();
                ProjectWxOrder wxOrder = projectWxOrderServiceI.getAllByOrderNoAndAppIdAndOpenId(orderNo, appId, openId);
                Integer totalAmount = (wxOrder.getTotalAmount().multiply(new BigDecimal(100))).intValue();
                SendServiceMessage sendServiceMessage = new SendServiceMessage();
                SendModelMessage message = new SendModelMessage();
                sendServiceMessage.setTouser(wxOrder.getOpenId());
                if(payMoney == totalAmount) {
                    wxOrder.setStatus(OrderEnum.FINISH);
                    wxOrder.setTimeStart(new Date());
                    wxOrder.setWechatOrderNo(wechatOrderNo);
                    wxOrder.setPayPeople(openId);
                    BigDecimal money = new BigDecimal((float) payMoney / 100).setScale(2, BigDecimal.ROUND_CEILING);
                    wxOrder.setTotalAmount(money);
                    //todo 编写逻辑处理
                    CarType carType = wxOrder.getCarType();
                    ProjectCarSetMeal projectCarSetMeal = projectCarSetMealServiceI.get(wxOrder.getShopId());
                    //续费时长
                    Integer endDay = projectCarSetMeal.getEndDay();
                    Date startTime = new Date();
                    Date endTime = DateUtils.getAddDate(startTime, endDay);
                    wxOrder.setTimeExpire(endTime);
                    if (carType.compareTo(CarType.SlagCar) == 0) {
                        ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(wxOrder.getProjectId(), wxOrder.getCarCode());
                        if (projectCar.getExpireDate() != null && projectCar.getExpireDate().getTime() > startTime.getTime()) {
                            //秒数
                            Long second = DateUtils.calculationHour(startTime, projectCar.getExpireDate());
                            endTime = DateUtils.getAddSecondDate(endTime, second.intValue());
                        }
                        SlagCar slagCar = slagCarServiceI.getAllByProjectIdAndCodeInProject(wxOrder.getProjectId(), wxOrder.getCarCode());
                        if(slagCar != null){
                            slagCar.setDeducted(true);
                            slagCar.setDeductedDate(startTime);
                            slagCar.setExpireDate(endTime);
                            slagCar.setPrepayId(wxOrder.getPrepayId());
                            slagCarServiceI.save(slagCar);
                        }
                        projectCar.setDeducted(true);
                        projectCar.setDeductedDate(startTime);
                        projectCar.setExpireDate(endTime);
                        projectCar.setPrepayId(wxOrder.getPrepayId());
                        projectCarServiceI.save(projectCar);
                    } else if (carType.compareTo(CarType.DiggingMachine) == 0) {
                        ProjectDiggingMachine diggingMachine = projectDiggingMachineServiceI.getByProjectIdAndCode(wxOrder.getProjectId(), wxOrder.getCarCode());
                        if (diggingMachine.getExpireDate() != null && diggingMachine.getExpireDate().getTime() > startTime.getTime()) {
                            //秒数
                            Long second = DateUtils.calculationHour(startTime, diggingMachine.getExpireDate());
                            endTime = DateUtils.getAddSecondDate(endTime, second.intValue());
                        }
                        diggingMachine.setDeducted(true);
                        diggingMachine.setDeductedDate(startTime);
                        diggingMachine.setExpireDate(endTime);
                        projectDiggingMachineServiceI.save(diggingMachine);
                    }
                    message.setForm_id(wxOrder.getPrepayId());
                    message.setTemplate_id(WechatConstant.WX_PAY_SUCCESS_MODEL_NO);
                    //Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
                    Map<String, Map<String, String>> keyWord = new HashMap<>();
                    Map<String, String> value01 = new HashMap<>();
                    value01.put("value", wxOrder.getOrderNo());
                    keyWord.put("keyword1", value01);
                    Map<String, String> value02 = new HashMap<>();
                    value02.put("value", wxOrder.getShopName());
                    keyWord.put("keyword2", value02);
                    Map<String, String> value03 = new HashMap<>();
                    value03.put("value", payTypeNo);
                    keyWord.put("keyword3", value03);
                    Map<String, String> value04 = new HashMap<>();
                    value04.put("value", wxOrder.getTotalAmount() + " 元");
                    keyWord.put("keyword4", value04);
                    Map<String, String> value05 = new HashMap<>();
                    value05.put("value", DateUtils.formatDateByPattern(startTime, SmartminingConstant.DATEFORMAT) + " 至 " + DateUtils.formatDateByPattern(endTime, SmartminingConstant.DATEFORMAT));
                    keyWord.put("keyword5", value05);
                    //data.put("data", keyWord);
                    //String json = JSON.toJSONString(data);
                    message.setData(keyWord);
                    sendServiceMessage.setWeapp_template_msg(message);
                    projectWxOrderServiceI.save(wxOrder);
                }
                wechatService.sendServiceMessage(sendServiceMessage);
            } else {
                projectWxOrderServiceI.deleteByOrderNo(orderNo);
            }
        } catch (SmartminingProjectException e){
            smartminingExceptionService.save(e);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return WechatConstant.WX_PAY_FAIL_BACK;
        } catch (Exception e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return WechatConstant.WX_PAY_FAIL_BACK;
        }
        return WechatConstant.WX_PAY_SUCCESS_BACK;
    }

    public static void main(String[] args){
        SendServiceMessage sendServiceMessage = new SendServiceMessage();
        SendModelMessage message = new SendModelMessage();
        sendServiceMessage.setTouser("testtesttesttesttest");
        message.setTemplate_id(WechatConstant.WX_PAY_SUCCESS_MODEL_NO);
        Map<String, Map<String, Map<String, String>>> data = new HashMap<>();
        Map<String, Map<String, String>> keyWord = new HashMap<>();
        Map<String, String> value01 = new HashMap<>();
        value01.put("value", "测试");
        keyWord.put("thing4", value01);
        Map<String, String> value02 = new HashMap<>();
        value02.put("value", "Test20191203163259");
        keyWord.put("character_string1", value02);
        Map<String, String> value03 = new HashMap<>();
        value03.put("value", "0.5元");
        keyWord.put("amount2", value03);
        Map<String, String> value04 = new HashMap<>();
        value04.put("value", "2019-12-03 16:33:00 至 2020 01-03 16:33:00");
        keyWord.put("date3", value04);
        //data.put("data", keyWord);
        //String json = JSON.toJSONString(data);
        message.setData(keyWord);
        sendServiceMessage.setWeapp_template_msg(message);
        String result = JSON.toJSONString(sendServiceMessage);
        System.out.println(result);
    }
}
