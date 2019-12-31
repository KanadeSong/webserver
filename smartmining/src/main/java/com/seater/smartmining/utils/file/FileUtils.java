package com.seater.smartmining.utils.file;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/31 0031 14:44
 */
public class FileUtils {

    /**
     * 创建文件
     *
     * @param path
     * @return
     */
    public static String createFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();//创建目录
        }
        return f.getPath();
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldfile); //读入原文件
                FileOutputStream fs = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[10240];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取resouce文件夹下指定文件的绝对路径
     *
     * @param path 指定文件的路径（相对路径 resouce文件夹）
     * @return
     */
    public static String getFilePathOnResouce(String path) {
        File file = null;
        try {
            file = ResourceUtils.getFile("classpath:" + path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 删除指定文件
     *
     * @param path
     */
    public static void delFile(String path) {
        File file = new File(path);
        file.delete();
    }

    public static String uploadFile(MultipartFile file, String fileName) {
        String filePath = SmartminingConstant.UPLOADFILEPATH;
        File targetFile = new File(filePath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        File oldFile = new File(filePath + File.separator + fileName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
        FileOutputStream out = null;
        String path = "";
        try {
            path = filePath + File.separator + fileName;
            out = new FileOutputStream(path);
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("文件上传的路径为：" + path);
        return path;
    }

    /**
     * 解压文件夹
     *
     * @param zipPath 要解压的文件路径
     * @param descDir 指定的解压目录
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static String decompressZip(String zipPath, String descDir) throws IOException {
        File zipFile = new File(zipPath);
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        String unzipPath = "";
        ZipFile zip = new ZipFile(zipFile, Charset.forName("gbk"));//防止中文目录，乱码
        for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            //指定解压后的文件夹+当前zip文件的名称
            String outPath = (descDir + zipEntryName).replace("/", File.separator);
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            //保存文件路径信息（可利用md5.zip名称的唯一性，来判断是否已经解压）
            System.err.println("当前zip解压之后的路径为：" + outPath);
            if(outPath.indexOf(".") == -1)
                unzipPath = outPath;
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[2048];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
        }
        //必须关闭，要不然这个zip文件一直被占用着，要删删不掉，改名也不可以，移动也不行，整多了，系统还崩了。
        zip.close();
        return unzipPath;
    }

    /**
     * 读取文本
     *
     * @param path 文本路径
     * @return
     * @throws IOException
     */
    public static String readText(String path) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String line = null;
        //定义一个空字符串来接受读到的字符串
        String str = "";
        //循环把读取到的字符赋给str
        while ((line = in.readLine()) != null) {
            str += line;
        }
        in.close();
        return str;
    }

    /**
     * 读取指定位置的内容
     *
     * @param path
     * @param from
     * @param to
     * @return
     */
    public static String read(String path, int from, int to) {
        byte[] result = new byte[to - from + 1];
        try {
            FileInputStream fis = new FileInputStream(path);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.skip(from - 1);
            bis.read(result, 0, to - from + 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(result);
    }

    /**
     * 上传文件
     *
     * @param multipartFile 上传文件
     * @param carType       设备类型
     * @return 文件路径
     */
    public static JSONObject upLoad(MultipartFile multipartFile, CarType carType, String code, Long projectId) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return CommonUtil.errorJson("文件为空,上传失败");
        }
        //指定存储路径 项目内的路径
        String finalName = createFileName(multipartFile, carType, projectId, code, true);
        String path = Constants.UPLOAD_PATH_PLAT + "/" + finalName;
        File resultFile = null;
        try {
            boolean typeFlag = checkFile(multipartFile);
            if (typeFlag) {
                // 先建文件,再写流
                File file = new File(path);
                resultFile = FileUtil.writeFromStream(multipartFile.getInputStream(), file);
                // 输出为 "\\usr\\java\\upload\\img\\uploadInPlat\\timg.jpg-69a7217c-b021-4eca-9a3a-56ec59796f2f-timg.jpg"
                //服务器配置了映射,/usr/java/upload/img => /upload/img,直接切
                return CommonUtil.successJson(resultFile.getPath().substring(9));
            } else {
                return CommonUtil.errorJson("不支持该文件类型,上传文件失败");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson("保存文件失败" + e.getMessage());
        }
    }

    /**
     * 创建文件名
     *
     * @param carType      设备类型
     * @param projectId    项目id
     * @param code         设备编号
     * @param isUniqueName 是否拼接UUID
     */
    private static String createFileName(MultipartFile multipartFile, CarType carType, Long projectId, String code, Boolean isUniqueName) {
        String fileName = multipartFile.getOriginalFilename();
        String separator = "-";
        if (isUniqueName) {
            fileName += separator + UUID.randomUUID().toString();
        }
        if (!ObjectUtils.isEmpty(projectId)) {
            fileName += separator + projectId;
        }
        if (!ObjectUtils.isEmpty(carType)) {
            fileName += separator + carType.getName();
        }
        if (!StringUtils.isEmpty(code)) {
            fileName += separator + code;
        }
        fileName += separator + multipartFile.getOriginalFilename();
        return fileName;
    }

    /**
     * 校验文件是否为项目常量允许的格式
     *
     * @param multipartFile 文件
     * @return 是否为允许格式
     * @throws IOException
     */
    private static Boolean checkFile(MultipartFile multipartFile) throws IOException {
        boolean typeFlag = false;
        String type = FileTypeUtil.getType(multipartFile.getInputStream());
        List<String> allowTypeList = JSONArray.parseArray(Constants.UPLOAD_ALLOW_TYPE, String.class);
        for (String allowType : allowTypeList) {
            if (type != null && allowType.contains(type)) {
                typeFlag = true;
            }
        }
        return typeFlag;
    }

    public static void main(String[] args) {
        String sver = FileUtils.read("D:\\SlagCar_App200a.udb", 33, 37);
        /*String hver = FileUtils.read("D:\\SlagCar_App200a.udb", 64, 95);
        String devt = FileUtils.read("D:\\SlagCar_App200a.udb", 0, 31);*/
        System.out.println("sver:" + sver);
        /*System.out.println("hver:" + hver);
        System.out.println("devt:" + devt);*/
    }
}
