package com.seater.smartmining.controller;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.utils.file.FileUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/23 17:33
 */
@RestController
@RequestMapping("/api/file")
public class FileController extends BaseController {

    @PostMapping("/uploadPlat")
    public Object uploadPlat(Long projectId, String code, MultipartFile multipartFile, CarType carType) {
        return FileUtils.upLoad(multipartFile, carType, code, projectId);
    }

}
