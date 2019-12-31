package com.seater.user.controller;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.aliyun.SmsUtils;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.wx.WxPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Description 微信小程序相关的敏感操作
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/29 15:00
 */
@RestController
@RequestMapping("/api/wxOption")
public class WxOptionController {

    @Autowired
    SysUserServiceI sysUserServiceI;

    /**
     * 验证用户手机号码
     *
     * @param mobile
     * @return
     */
    @PostMapping("/checkMobile")
    public Object checkMobile(String mobile) {
        try {
            JSONObject sms = SmsUtils.sendSms(mobile);
            if (sms.get("Message").equals("OK") && sms.get("Code").equals("OK")) {
                return CommonUtil.successJson(sms);
            } else {
                return CommonUtil.errorJson(sms.get("Message").toString() + " " + sms.get("Code").toString());
            }
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败," + e.getMessage());
        }
    }

    /**
     * 升级服务
     *
     * @param openId
     * @return
     */
//    @PostMapping("/upgrade")
    public Object upgradeWx(String openId) {
        return null;
    }

    /**
     * 前端付费请求
     *
     * @param openId
     * @param orderId
     * @param totalFee
     * @param request
     * @return
     */
//    @PostMapping("/payWx")
    public Object payWx(String openId, String orderId, String totalFee, HttpServletRequest request) {

        if (StrUtil.isBlankIfStr(openId) || sysUserServiceI.getByOpenId(openId) == null) {
            return CommonUtil.errorJson("openId错误,支付失败");
        }
        //  TODO 微信支付,未完成...
        return WxPay.wxPay(openId, orderId, totalFee, request);
    }

    /**
     * 微信付费成功的回调
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/notifyWx")
    public Object notifyWx(HttpServletRequest request, HttpServletResponse response) {
        try {
            //  TODO 微信支付成功的回调
            String openId = "";
            SysUser sysUser = sysUserServiceI.getByOpenId(openId);
            sysUser.setVipLevel(sysUser.getVipLevel().next());
            sysUserServiceI.save(sysUser);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("升级失败" + e.getMessage());
        }
    }

    /**
     * 生成二维码给前端
     *
     * @param url
     * @param response
     * @return
     */
    @GetMapping("/qrCode")
    public Object qrCode(String url, HttpServletResponse response) {
        QrConfig config = new QrConfig();
//        response.setContentType("application/octet-stream");
        //  logo
        config.setImg(new File(Constants.LOGO_PATH));
        //  错误级别 /** H = ~30% correction */ 至少30%
        config.setErrorCorrection(ErrorCorrectionLevel.H);
        //  暂时不用写流的方式
//        ServletOutputStream outputStream = response.getOutputStream();
//        QrCodeUtil.generate(url, config, ImgUtil.IMAGE_TYPE_PNG,outputStream);

        //  转base64返回前端,微信小程序最好是png格式
        return ImgUtil.toBase64(QrCodeUtil.generate(url, config), ImgUtil.IMAGE_TYPE_PNG);
    }

}
