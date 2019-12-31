package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.MD5Helper;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.entity.Version;
import com.seater.smartmining.domain.VersionResponse;
import com.seater.smartmining.exception.SmartiningFileNotFountException;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.exception.SytechPackSelectFailExcption;
import com.seater.smartmining.exception.service.SmartminingExceptionService;
import com.seater.smartmining.service.ProjectDeviceServiceI;
import com.seater.smartmining.service.VersionServiceI;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/4 0004 10:58
 */
@RestController
@RequestMapping("/api/version")
public class VersionController {

    @Autowired
    private VersionServiceI versionServiceI;
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SmartminingExceptionService smartminingExceptionService;

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<Version> spec = new Specification<Version>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return versionServiceI.query(spec, PageRequest.of(cur, page));
    }

    /**
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        try {
            //文件名称
            String fileName = file.getOriginalFilename();
            String[] fileArray = fileName.split("\\.");
            String suffix = fileArray[1];
            //文件大小
            Long fileSize = file.getSize();
            String sver = "";
            String hver = "";
            String devt = "";
            //获取到本地上传路径
            String path = FileUtils.uploadFile(file, fileName);
            String encodeText = "";
            if(suffix.equals(SmartminingConstant.ZIPSUFFIX)) {
                //加密字符串
                encodeText = MD5Helper.getMD5(path);
                //解压文件
                String unzipPath = FileUtils.decompressZip(path, SmartminingConstant.UPLOADFILEPATH);
                if(StringUtils.isEmpty(unzipPath))
                    throw new SmartminingProjectException("未找到可读取文件");
                if (StringUtils.isNotEmpty(unzipPath)) {
                    String text = FileUtils.readText(unzipPath);
                    sver = StringUtils.substringText(text, SmartminingConstant.SOFTWAREKEYWORD, 100, SmartminingConstant.COLON);
                    hver = StringUtils.substringText(text, SmartminingConstant.HARDWAREKEYWORD, 100, SmartminingConstant.COLON);
                    devt = StringUtils.substringText(text, SmartminingConstant.MACHINETYPEKEYWORD, 100, SmartminingConstant.COLON);
                }
                File fileUnzip = new File(unzipPath);
                fileUnzip.delete();
            }else{
                if(file.getBytes().length > 0){
                    //MD5加密字符串
                    encodeText = MD5Helper.getMD5(path);
                    hver = FileUtils.read(path, 32, 63).replaceAll("\\u0000","");
                    sver = FileUtils.read(path, 64, 95).replaceAll("\\u0000","");
                    devt = FileUtils.read(path, 0, 31).replaceAll("\\u0000","");
                    /*hver = StringUtils.convertByByte(FileUtils.read(path, 33, 37));
                    sver = StringUtils.convertByByte(FileUtils.read(path, 65, 70));
                    devt = StringUtils.convertByByte(FileUtils.read(path, 0, 31));*/
                }
            }
            if(StringUtils.isNotEmpty(sver) && StringUtils.isNotEmpty(hver) && StringUtils.isNotEmpty(devt)) {
                Version version = new Version();
                version.setSoftwareVersion(sver);
                version.setHardwareVersion(hver);
                version.setFileName(fileName);
                version.setFileSize(fileSize);
                version.setDeviceType(devt);
                version.setAesText(encodeText);
                versionServiceI.save(version);
            }else{
                throw new SytechPackSelectFailExcption("上传文件有误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/download")
    public byte[] downloadVersion(String fuid, String fname, Long idx, Integer len) {
        try {
            len = len != null && len != 0 ? len : 1024 * 10;
            if(idx == 0L){
                ProjectDevice device = projectDeviceServiceI.getByUid(fuid);
                Version version = versionServiceI.get(device.getVersionId());
                if(version != null) {
                    if (!version.getFileName().equals(fname))
                        throw new SytechPackSelectFailExcption();
                    stringRedisTemplate.opsForValue().set("sewage:downLoadUpdateFile:" + fuid, fname, 15, TimeUnit.MINUTES);
                }
            }else{
                String redisFname = stringRedisTemplate.opsForValue().get("sewage:downLoadUpdateFile:" + fuid);
                if (!redisFname.equals(fname))
                    throw new SytechPackSelectFailExcption();
            }
            /*File file = new File("D:\\detector.zip");*/
            File file = new File(SmartminingConstant.UPLOADFILEPATH + File.separator + fname);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                if (idx <= file.length()) {
                    byte[] b = file.length() - idx < len ? new byte[Integer.valueOf(String.valueOf(file.length() - idx))] : new byte[len];
                    bis.skip(idx);
                    bis.read(b);
                    bis.close();
                    fis.close();
                    return b;
                }
            }else{
                throw new SmartiningFileNotFountException("文件不存在");
            }
        } catch (Exception e) {
            smartminingExceptionService.save(e);
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/getByUid")
    public String getByUid(String status){
        try {
            Map jsonMap = JSONObject.parseObject(status, Map.class);
            String fuid = jsonMap.get("fuid") != null ? jsonMap.get("fuid").toString() : "";
            ProjectDevice projectDevice = StringUtils.isNotEmpty(fuid) ? projectDeviceServiceI.getByUid(fuid) : null;
            if(projectDevice == null) {
                return "{}";
            }else {
                if(projectDevice.getVersionId() != null) {
                    Version version = versionServiceI.get(projectDevice.getVersionId());
                    if (version == null) {
                        return "{}";
                    } else {
                        VersionResponse response = new VersionResponse();
                        response.setFname(version.getFileName());
                        response.setFdevt(version.getDeviceType());
                        response.setFsize(version.getFileSize().toString());
                        response.setFvers(version.getSoftwareVersion());
                        response.setHvers(version.getHardwareVersion());
                        response.setMd5(version.getAesText());
                        List<VersionResponse> responseList = new ArrayList<>();
                        responseList.add(response);
                        Map map = new HashMap();
                        map.put("Update", responseList);
                        return JSON.toJSONString(map);
                    }
                }else{
                    return "{}";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids){
        try {
            for (int i = 0; i < ids.size(); i++) {
                Version version = versionServiceI.get(ids.get(i));
                List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllByAndFileName(version.getFileName());
                for(ProjectDevice projectDevice : projectDeviceList){
                    projectDevice.setFileName("");
                    projectDevice.setVersionId(0L);
                    projectDevice.setHardwareVersion("");
                    projectDevice.setSoftwareVersion("");
                    projectDeviceServiceI.save(projectDevice);
                }
                String filePath = SmartminingConstant.UPLOADFILEPATH + File.separator + version.getFileName();
                FileUtils.delFile(filePath);
            }
            versionServiceI.delete(ids);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    public static void main(String[] args){
        String test = "123456789";
        String txt = test.substring(test.length() - 4, test.length());
        System.out.println(txt);
    }
}
