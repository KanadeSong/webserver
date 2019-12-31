package com.seater.user.test;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Description TODO 测试用,暂时不删
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/23 18:38
 */
//@Controller
//@RequestMapping("/api/wxOption")
public class WxTestController {
    
//    @RequestMapping("/static/test")
    public Object test(HttpServletRequest request, HttpServletResponse response){
        JSONObject jsonObject = new JSONObject();
        String responseStr = "";
        try{
            RestTemplate restTemplate = new RestTemplate();
            responseStr = restTemplate.getForObject("https://api.weixin.qq.com/sns/jscode2session?appid=wx54a37c6a3b5a64e1&secret=5145d1bd77ac5b6e6906a7720cd3da97&js_code=002TTM6P0hfuZ42ylY9P0LTG6P0TTM67&grant_type=authorization_code", String.class);
            jsonObject = JSONObject.parseObject(responseStr);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println(jsonObject);
        
        return jsonObject;
    }
    
}
