package com.seater.smartmining.utils;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.utils.string.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/2/23 0023 9:54
 */
public class BrowserUtils {

    /**
     * 根据浏览器信息获取fileName
     * @param fileName
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getFileName(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        //获得浏览器代理信息
        final String userAgent = request.getHeader(SmartminingConstant.HTTP_REUQEST_USER_AGENT);
        if (StringUtils.contains(userAgent, SmartminingConstant.IE_MSIE) || StringUtils.contains(userAgent, SmartminingConstant.IE_TRIDENT)) {//IE浏览器
            fileName = URLEncoder.encode(fileName, SmartminingConstant.ENCODEUTF);
        } else if (StringUtils.contains(userAgent, SmartminingConstant.MOZILLA)) {//google,火狐浏览器
            fileName = new String(fileName.getBytes(), SmartminingConstant.ENCODEISO);
        } else {
            fileName = URLEncoder.encode(fileName, SmartminingConstant.ENCODEUTF);//其他浏览器
        }
        return fileName;
    }
}
