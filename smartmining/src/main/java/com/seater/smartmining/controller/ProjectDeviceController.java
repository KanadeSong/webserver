package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.domain.DeviceRequest;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectDevice;
import com.seater.smartmining.entity.ProjectDeviceStatus;
import com.seater.smartmining.entity.Version;
import com.seater.smartmining.enums.ProjectDeviceType;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.service.ProjectDeviceServiceI;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.service.VersionServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectDevice")
public class ProjectDeviceController {
    @Autowired
    private ProjectDeviceServiceI projectDeviceServiceI;
    @Autowired
    private VersionServiceI versionServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private ExcelReportService excelReportService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, String phoneNumber, String code, ProjectDeviceStatus projectDeviceStatus, ProjectDeviceType projectDevice) {
        try {
            int cur = (current == null  || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<ProjectDevice> spec = new Specification<ProjectDevice>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDevice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (phoneNumber != null && !phoneNumber.isEmpty())
                        list.add(cb.like(root.get("phoneNumber").as(String.class), "%" + phoneNumber + "%"));

                    if (code != null && !code.isEmpty())
                        list.add(cb.like(root.get("deviceCode").as(String.class), "%" + code + "%"));

                    if (projectDeviceStatus != null)
                        list.add(cb.equal(root.get("status").as(ProjectDeviceStatus.class), projectDeviceStatus));
                    if(projectDevice != null)
                        list.add(cb.equal(root.get("projectDevice").as(ProjectDevice.class), projectDevice));
                    if (request.getHeader("projectId") != null){
                        list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    }

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return projectDeviceServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/getByUid")
    public Result getByUid(String uid/*, String deviceCode*/){
        try {
            ProjectDevice projectDevice = projectDeviceServiceI.getByUid(uid);
            if (projectDevice != null)
                throw new SmartminingProjectException("该UID编号已经存在");
            /*ProjectDevice device = projectDeviceServiceI.getAllByDeviceCode(deviceCode);
            if(device != null)
                throw new SmartminingProjectException("该DeviceCode终端编号已经存在");*/
            return Result.ok();
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMsg());
        }
    }

    @RequestMapping("/save")
    public Object save(ProjectDevice projectDevice) {
        try {
            if(projectDevice.getCreateDate() == null)
                projectDevice.setCreateDate(new Date());
            projectDeviceServiceI.save(projectDevice);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping(value = "/batchSave", produces = "application/json")
    @Transactional
    public Result batchSave(@RequestBody List<ProjectDevice> projectDeviceList){
        projectDeviceServiceI.batchSave(projectDeviceList);
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @Transactional
    public Object delete(@RequestBody List<Long> ids) {
        try {
            projectDeviceServiceI.delete(ids);
            return "{\"status\":true}";
        }
        catch (Exception e)
        {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/getAll")
    public Result getAll(HttpServletRequest request){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        List<ProjectDevice> projectDeviceList = projectDeviceServiceI.getAllCarDeviceAndMachineDevice(projectId);
        return Result.ok(projectDeviceList);
    }

    @RequestMapping(value = "/setDevicesVersion", produces = "application/json")
    @Transactional
    public Result modifyVersionId(@RequestBody List<DeviceRequest> requestList) {
        try {
            for (DeviceRequest request : requestList) {
                ProjectDevice projectDevice = projectDeviceServiceI.get(request.getId());
                Version version = versionServiceI.get(request.getVersionId());
                projectDevice.setVersionId(version.getId());
                projectDevice.setFileName(version.getFileName());
                projectDevice.setHardwareVersion(version.getHardwareVersion());
                projectDevice.setSoftwareVersion(version.getSoftwareVersion());
                projectDeviceServiceI.save(projectDevice);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping("/writeCard")
    public Result writeCard(ProjectDevice projectDevice){

        return Result.ok();
    }

    /**
     * 终端数据导入
     * @param //file  导入文件 txt
     * @return
     */
    /*@RequestMapping("/import")
    public Result importData(@RequestParam("file") MultipartFile file){
        try {
            String fileName = DateUtils.formatDateByPattern(new Date(), SmartminingConstant.YEARMONTHDAUFORMAT *//*+ SmartminingConstant.EXCELSUFFIX*//*);
            String path = FileUtils.uploadFile(file, fileName);
            FileReader fr=new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            List<ProjectDevice> projectDeviceList = new ArrayList<>();
            String line = "";
            while ((line=br.readLine())!=null) {
                String[] message = line.split(" "); // 一次读入一行数据
                ProjectDevice device = new ProjectDevice();
                device.setDeviceCode(message[0]);
                device.setUid(message[1]);
                System.out.println("终端编号：" + message[0]);
                System.out.println("UID：" + message[1]);
                Integer deviceType = Integer.valueOf(message[2]);
                if(deviceType == 1)
                    device.setDeviceType(ProjectDeviceType.SlagFieldDevice);
                else if(deviceType == 2)
                    device.setDeviceType(ProjectDeviceType.DiggingMachineDevice);
                else if(deviceType == 3)
                    device.setDeviceType(ProjectDeviceType.DetectionDevice);
                else if(deviceType == 4)
                    device.setDeviceType(ProjectDeviceType.ScheduledDevice);
                else if(deviceType == 5)
                    device.setDeviceType(ProjectDeviceType.SlagTruckDevice);
                else
                    device.setDeviceType(ProjectDeviceType.Unknown);
                if(!"0".equals(message[3]))
                    device.setProjectId(Long.parseLong(message[3]));
                Integer start = Integer.valueOf(message[4]);
                if(start == 0)
                    device.setVaild(false);
                else
                    device.setVaild(true);
                device.setHardwareVersion(message[5]);
                if(!"0".equals(message[6]))
                    device.setProductionBatchNumber(message[6]);
                device.setCreateDate(new Date());
                projectDeviceList.add(device);
            }
            projectDeviceServiceI.batchSave(projectDeviceList);
            return Result.ok();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }*/

    @RequestMapping("/import")
    public Result importData(@RequestParam("file") MultipartFile file){
        try {
            List<ProjectDevice> projectDeviceList = new ArrayList<>();
            String fileName = DateUtils.formatDateByPattern(new Date(), SmartminingConstant.YEARMONTHDAUFORMAT) + SmartminingConstant.XLSSUFFIX;
            String path = FileUtils.uploadFile(file, fileName);
            File localFile = new File(path);
            InputStream is = new FileInputStream(localFile);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            //获取到当前sheet的总行数
            Integer rowCount = sheet.getLastRowNum();
            StringBuffer sb = new StringBuffer();
            sb.append("终端编号：");
            for (int i = 0; i < rowCount; i++) {
                ProjectDevice projectDevice = new ProjectDevice();
                //去除第一行 第一行的数据不需要读取
                Row row = sheet.getRow(i + 1);
                //获取到终端编号
                Cell cellOne = row.getCell(0);
                if(cellOne != null) {
                    if (StringUtils.isEmpty(cellOne.getStringCellValue()))
                        continue;
                    projectDevice.setDeviceCode(cellOne.getStringCellValue());
                }

                //获取到终端UID
                Cell cellTwo = row.getCell(1);
                if(cellTwo != null) {
                    String uid = cellTwo.getStringCellValue();
                    ProjectDevice device = projectDeviceServiceI.getAllByDeviceCode(uid);
                    if(device != null) {
                        sb.append(uid + "\t");
                        continue;
                    }
                    if (StringUtils.isEmpty(uid))
                        continue;
                    projectDevice.setUid(uid);
                }

                //获取到终端类型
                Cell cellThree = row.getCell(2);
                if(cellThree != null) {
                    if (cellThree.getNumericCellValue() < 1)
                        continue;
                    Integer deviceVal = new Double(cellThree.getNumericCellValue()).intValue();
                    if (deviceVal == 1)
                        projectDevice.setDeviceType(ProjectDeviceType.SlagFieldDevice);
                    else if (deviceVal == 2)
                        projectDevice.setDeviceType(ProjectDeviceType.DiggingMachineDevice);
                    else if (deviceVal == 3)
                        projectDevice.setDeviceType(ProjectDeviceType.DetectionDevice);
                    else if (deviceVal == 4)
                        projectDevice.setDeviceType(ProjectDeviceType.ScheduledDevice);
                    else if (deviceVal == 5)
                        projectDevice.setDeviceType(ProjectDeviceType.SlagTruckDevice);
                    else
                        throw new SmartminingProjectException((i + 1) + "行终端类型不正确");
                }

                //获取到项目ID
                Cell cellFour = row.getCell(3);
                if(cellFour != null) {
                    if (cellFour.getNumericCellValue() < 1)
                        projectDevice.setProjectId(Long.parseLong(cellFour.getStringCellValue()));
                }

                //获取到启用状态
                Cell cellFive = row.getCell(4);
                if(cellFive != null) {
                    if (cellFive.getNumericCellValue() < 1)
                        continue;
                    Integer status = new Double(cellFive.getNumericCellValue()).intValue();
                    if (status == 0)
                        projectDevice.setStatus(ProjectDeviceStatus.OffLine);
                    else if (status == 1)
                        projectDevice.setStatus(ProjectDeviceStatus.OnLine);
                    else
                        throw new SmartminingProjectException((i + 1) + "行启用状态不正确, 0-停用   1-启用");
                }

                //获取到硬件版本号
                Cell cellSix = row.getCell(5);
                if(cellSix != null) {
                    if (StringUtils.isEmpty(cellSix.getStringCellValue()))
                        continue;
                    projectDevice.setHardwareVersion(cellSix.getStringCellValue());
                }

                //获取到生产批次号
                Cell cellSeven = row.getCell(6);
                if(cellSeven != null) {
                    if (StringUtils.isNotEmpty(cellSeven.getStringCellValue()))
                        projectDevice.setProductionBatchNumber(cellSeven.getStringCellValue());
                }
                projectDeviceList.add(projectDevice);
            }
            sb.append("已经存在，以上终端插入失败");
            projectDeviceServiceI.batchSave(projectDeviceList);
            return Result.ok();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/export", produces = "application/json")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestBody List<ProjectDevice> projectDeviceList){
        OutputStream outputStream = null;
        try {
            List<Project> projectList = projectServiceI.getAll();
            Map<Long, Integer> projectMapIndex = new HashMap<>();
            for(int i = 0; i < projectList.size(); i++){
                projectMapIndex.put(projectList.get(i).getId(), i);
            }
            String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
            File file = new File(path + File.separator + SmartminingConstant.WORKFILENAME);
            if (!file.exists())
                file.createNewFile();
            String filePath = file.getPath();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row rowFirst = sheet.createRow(0);
            rowFirst.setHeight((short) (15.625 * 30));
            Cell oneCell = rowFirst.createCell(0);
            oneCell.setCellValue("序号");
            Cell twoCell = rowFirst.createCell(1);
            twoCell.setCellValue("终端编号");
            Cell threeCell = rowFirst.createCell(2);
            threeCell.setCellValue("终端UID");
            Cell fourCell = rowFirst.createCell(3);
            fourCell.setCellValue("终端类型");
            Cell fiveCell = rowFirst.createCell(4);
            fiveCell.setCellValue("所属项目");
            Cell sixCell = rowFirst.createCell(5);
            sixCell.setCellValue("启用状态");
            Cell sevenCell = rowFirst.createCell(6);
            sevenCell.setCellValue("创建日期");
            Cell eightCell = rowFirst.createCell(7);
            eightCell.setCellValue("硬件版本号");
            Cell nineCell = rowFirst.createCell(8);
            nineCell.setCellValue("生产批次号");
            Cell tenCell = rowFirst.createCell(9);
            tenCell.setCellValue("绑定设备");
            Cell elevenCell = rowFirst.createCell(10);
            elevenCell.setCellValue("在线状态");
            int i = 0;
            for(ProjectDevice device : projectDeviceList){
                Row row = sheet.createRow(i + 1);
                row.setHeight((short) (15.625 * 30));

                Cell one = row.createCell(0);
                one.setCellValue(i + 1);

                Cell two = row.createCell(1);
                two.setCellValue(device.getDeviceCode());

                Cell three = row.createCell(2);
                three.setCellValue(device.getUid());

                Cell four = row.createCell(3);
                if(device.getDeviceType().compareTo(ProjectDeviceType.SlagFieldDevice) == 0)
                    four.setCellValue("渣场终端");
                else if(device.getDeviceType().compareTo(ProjectDeviceType.DiggingMachineDevice) == 0)
                    four.setCellValue("挖机终端");
                else if(device.getDeviceType().compareTo(ProjectDeviceType.DetectionDevice) == 0)
                    four.setCellValue("检测终端");
                else if(device.getDeviceType().compareTo(ProjectDeviceType.ScheduledDevice) == 0)
                    four.setCellValue("调度终端");
                else if(device.getDeviceType().compareTo(ProjectDeviceType.SlagTruckDevice) == 0)
                    four.setCellValue("渣车终端");
                else
                    four.setCellValue("未知");

                Cell five = row.createCell(4);
                Integer index = projectMapIndex.get(device.getProjectId());
                if(index != null)
                    five.setCellValue(projectList.get(index).getName());
                else
                    five.setCellValue("未知");

                Cell six = row.createCell(5);
                if(device.getVaild())
                    six.setCellValue("启用");
                else
                    six.setCellValue("禁用");

                Cell seven = row.createCell(6);
                seven.setCellValue(DateUtils.formatDateByPattern(device.getCreateDate(), SmartminingConstant.DATEFORMAT));

                Cell eight = row.createCell(7);
                eight.setCellValue(device.getHardwareVersion());

                Cell nine = row.createCell(8);
                nine.setCellValue(device.getProductionBatchNumber());

                Cell ten = row.createCell(9);
                ten.setCellValue(device.getCode());

                Cell eleven = row.createCell(10);
                if(device.getStatus().compareTo(ProjectDeviceStatus.OnLine) == 0)
                    eleven.setCellValue("在线");
                else
                    eleven.setCellValue("离线");
                i++;
            }
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            excelReportService.downLoadFile(response, request, filePath, new Date());
            FileUtils.delFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
