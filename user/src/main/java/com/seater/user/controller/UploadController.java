package com.seater.user.controller;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Description 上传文件
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 17:28
 */

@RestController
@RequestMapping("/api/file")
public class UploadController {

    @Autowired
    SysUserServiceI sysUserServiceI;

    @PostMapping("/upload")
    public Object upload(@RequestBody(required = true) MultipartFile multipartFile, @RequestParam(name = "openId", required = true) String openId) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return CommonUtil.errorJson("文件为空,上传失败");
        }
        if (sysUserServiceI.getByOpenId(openId) == null) {
            return CommonUtil.errorJson("用户不存在,上传失败");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        //指定存储路径 用openId分文件夹
        String path = Constants.UPLOAD_PATH + "/" + openId + "/" + originalFilename;
        File resultFile = null;
        try {
            // 先建文件,再写流
            File file = new File(path);
            resultFile = FileUtil.writeFromStream(multipartFile.getInputStream(), file);
            return CommonUtil.successJson(resultFile.getAbsolutePath());
        } catch (Exception e) {
            return CommonUtil.errorJson("保存文件失败" + e.getMessage());
        }
    }

    /**
     * 项目内上传文件
     *
     * @param multipartFile
     * @param mobile        手机号码
     * @return
     */
    @PostMapping("/uploadInProject")
    public Object uploadInProject(@RequestBody(required = true) MultipartFile multipartFile, @RequestParam(name = "mobile", required = true) String mobile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return CommonUtil.errorJson("文件为空,上传失败");
        }
        String originalFilename = multipartFile.getOriginalFilename();
        //指定存储路径 项目内的路径
        String path = Constants.UPLOAD_PATH_IN_PROJECT + "/" + mobile + "/" + originalFilename;
        File resultFile = null;
        try {
            boolean typeFlag = false;
            String type = FileTypeUtil.getType(multipartFile.getInputStream());
            List<String> allowTypeList = JSONArray.parseArray(Constants.UPLOAD_ALLOW_TYPE, String.class);
            for (String allowType : allowTypeList) {
                if (type != null && allowType.contains(type)) {
                    typeFlag = true;
                }
            }
            if (typeFlag) {
                // 先建文件,再写流
                File file = new File(path);
                resultFile = FileUtil.writeFromStream(multipartFile.getInputStream(), file);
                return CommonUtil.successJson(resultFile.getAbsolutePath());
            } else {
                return CommonUtil.errorJson("不支持该文件类型,上传文件失败");
            }

        } catch (Exception e) {
            return CommonUtil.errorJson("保存文件失败" + e.getMessage());
        }
    }
}
