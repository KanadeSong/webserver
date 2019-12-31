package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.SendModelMessage;
import com.seater.smartmining.domain.UnifiedOrder;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.OrderEnum;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.constant.WechatConstant;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.other.WechatService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.smartmining.utils.wechat.WxIpUtils;
import com.seater.smartmining.utils.wechat.XmlAndJavaObjectConvert;
import com.seater.user.util.CommonUtil;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:19
 */
@RestController
@RequestMapping("/api/projectWxOrder")
public class ProjectWxOrderController {

    @Autowired
    private WechatService wechatService;
    @Autowired
    private ProjectWxOrderServiceI projectWxOrderServiceI;

    /**
     * 创建订单接口
     *
     * @param request
     * @param unifiedOrder
     * @return
     */
    @RequestMapping("/create")
    @ResponseBody
    public Result createOrder(HttpServletRequest request, UnifiedOrder unifiedOrder) {
        unifiedOrder.setFee_type(WechatConstant.WXFEETYPE);
        String ip = WxIpUtils.getIp(request);
        unifiedOrder.setSpbill_create_ip(ip);
        unifiedOrder.setTrade_type(WechatConstant.WXPAYTYPE);
        unifiedOrder.setNotify_url(WechatConstant.WXPAYPAYSULTURL);
        Map<String, Object> map = null;
        try {
            map = wechatService.unifiedOrder(unifiedOrder);
            return Result.ok(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (DocumentException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping("/query")
    public Result query(Integer current, Integer pageSize, Long projectId, String openId, String orderNo, String carCode, CarType carType){
        int cur = (current == null  || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectWxOrder> spec = new Specification<ProjectWxOrder>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<ProjectWxOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (StringUtils.isNotEmpty(openId))
                    list.add(cb.equal(root.get("openId").as(String.class), openId));
                if(StringUtils.isNotEmpty(orderNo))
                    list.add(cb.equal(root.get("orderNo").as(String.class), orderNo));
                if(StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode").as(String.class), "" + carCode + ""));
                if(carType != null)
                    list.add(cb.equal(root.get("carType").as(CarType.class), carType));
                if(projectId != null && projectId != 0)
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectWxOrderServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestParam List<Long> ids){
        projectWxOrderServiceI.delete(ids);
        return Result.ok();
    }

    /**
     * 商城注册接口
     * @return
     */
    @RequestMapping("/register")
    public Result register(@RequestParam String telephone, @RequestParam String nickName, @RequestParam String password, @RequestParam String payPassword){
        JSONObject jsonObject = null;
        try {
            jsonObject = wechatService.registerShop(telephone, nickName, password, payPassword);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(jsonObject);
    }
}
