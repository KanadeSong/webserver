package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.*;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.mqtt.DeviceMessageHandler;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.SpringUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.ExcelUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.PermissionUtils;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/projectCarWorkInfo")
public class ProjectCarWorkInfoController {
    @Autowired
    ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ProjectAppStatisticsLogServiceI projectAppStatisticsLogServiceI;
    @Autowired
    private ProjectServiceI projectServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectSlagCarLogServiceI projectSlagCarLogServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ProjectCarMaterialServiceI projectCarMaterialServiceI;
    @Autowired
    private ProjectAppStatisticsByCarServiceI projectAppStatisticsByCarServiceI;
    @Autowired
    private ExcelReportService excelReportService;
    @Autowired
    private ProjectCarCountServiceI projectCarCountServiceI;
    @Autowired
    private WorkMergeErrorLogServiceI workMergeErrorLogServiceI;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    @Transactional
    public Object save(Long projectCarWorkInfoId, Score pass, String note, HttpServletRequest request) {
        try {
            Session session = SecurityUtils.getSubject().getSession();
            //  1.  从session中读取登陆时保存的user信息
            SysUser user = (SysUser) session.getAttribute(Constants.SESSION_USER_INFO);
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ProjectCarWorkInfo projectCarWorkInfo = projectCarWorkInfoServiceI.get(projectCarWorkInfoId);
            ProjectCar car = projectCarServiceI.get(projectCarWorkInfo.getCarId());
            if (car == null)
                return "{\"status\":false, \"msg\":\"not found! \"}";
            if (projectCarWorkInfo == null)
                return "{\"status\":false, \"msg\":\"not found! \"}";
            if (StringUtils.isNotEmpty(note))
                projectCarWorkInfo.setNote(note);
            if (pass != null && pass != Score.Unknown)
                projectCarWorkInfo.setPass(pass);
            projectCarWorkInfo.setProjectId(projectId);
            projectCarWorkInfo.setDetailId(user.getId());
            projectCarWorkInfo.setDetailName(user.getName());
            projectCarWorkInfo.setCarOwnerId(car.getOwnerId());
            projectCarWorkInfo.setCarOwnerName(car.getOwnerName());
            projectCarWorkInfoServiceI.save(projectCarWorkInfo);

            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @RequestMapping("/end")
    @Transactional
    public Object end(Long projectCarWorkInfoId, Long distance, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ProjectCarWorkInfo projectCarWorkInfo = projectCarWorkInfoServiceI.get(projectCarWorkInfoId);
            if (projectCarWorkInfo == null)
                return "{\"status\":false, \"msg\":\"not found! \"}";
            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
            projectCarWorkInfo.setDistance(distance);
            projectCarWorkInfoServiceI.save(projectCarWorkInfo);

            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }

    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, HttpServletRequest request, @RequestParam(value = "rangePickerValue", required = false) ArrayList<String> reangePickerValue, String carCode, String digCode, Boolean isQualified, String loadMaterialName, Float minH, Float maxH, Integer choose, Long slagSiteId) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            //判断是查询全部还是筛选
            boolean flag = false;
            JSONArray jsonArray = PermissionUtils.getProjectPermission(projectId);
            if (jsonArray == null)
                throw new SmartminingProjectException("当前用户无任何权限");
            if (jsonArray.contains(SmartminingConstant.ALLDATA))
                flag = true;
            //权限对应的车辆编号集合
            List<String> carCodeList = new ArrayList<>();
            if (!flag) {
                //获取当前用户对象
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
                String params = "\"" + sysUser.getId() + "\"";
                Specification<ProjectSchedule> specification = new Specification<ProjectSchedule>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectSchedule> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        if (!jsonArray.contains(SmartminingConstant.ALLDATA))
                            list.add(cb.like(root.get("managerId").as(String.class), "%" + params + "%"));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectSchedule> schedulePage = projectScheduleServiceI.getAllByQuery(specification);
                List<String> groupCodeList = new ArrayList<>();
                for (ProjectSchedule schedule : schedulePage) {
                    groupCodeList.add(schedule.getGroupCode());
                }
                Specification<ScheduleCar> spec = new Specification<ScheduleCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ScheduleCar> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                        if (groupCodeList.size() > 0) {
                            Expression<String> exp = root.get("groupCode").as(String.class);
                            list.add(exp.in(groupCodeList));
                        }
                        query.orderBy(criteriaBuilder.asc(root.get("id").as(Long.class)));
                        return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByQuery(spec);
                for (ScheduleCar car : scheduleCarList) {
                    carCodeList.add(car.getCarCode());
                }

            }
            Specification<ProjectCarWorkInfo> spec = new Specification<ProjectCarWorkInfo>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (carCode != null && !carCode.isEmpty())
                        list.add(cb.equal(root.get("carCode").as(Long.class), carCode));
                    if (digCode != null && !digCode.isEmpty())
                        list.add(cb.equal(root.get("diggingMachineCode").as(Long.class), digCode));
                    if (isQualified != null)
                        list.add(cb.equal(root.get("pass").as(Score.class), isQualified ? Score.Pass : Score.UnPass));
                    if (slagSiteId != null && slagSiteId != 0L)
                        list.add(cb.equal(root.get("slagSiteId").as(Long.class), slagSiteId));
                    if (choose != null) {
                        if (choose == 1) {
                            list.add(cb.notEqual(root.get("note").as(String.class), ""));
                        } else if (choose == 2) {
                            list.add(cb.equal(root.get("note").as(String.class), ""));
                        }
                    }
                    if (loadMaterialName != null && !loadMaterialName.isEmpty())
                        list.add(cb.equal(root.get("materialName").as(String.class), loadMaterialName));
                    if (minH != null && maxH != null) {
                        list.add(cb.between(root.get("height").as(int.class), (int) (minH * 1000), (int) (maxH * 1000)));
                    } else if (minH != null) {
                        list.add(cb.greaterThanOrEqualTo(root.get("height").as(int.class), (int) (minH * 1000)));
                    } else if (maxH != null) {
                        list.add(cb.lessThanOrEqualTo(root.get("height").as(int.class), (int) (maxH * 1000)));
                    }
                    if (reangePickerValue != null && reangePickerValue.size() == 2) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                            Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                            Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                            list.add(cb.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    if (carCodeList.size() > 0) {
                        Expression<String> exp = root.get("carCode").as(String.class);
                        list.add(exp.in(carCodeList));
                    }
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    query.orderBy(cb.desc(root.get("createDate").as(Date.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return projectCarWorkInfoServiceI.query(spec, PageRequest.of(cur, page));
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/insert", produces = "application/json")
    @Transactional
    public Result save(HttpServletRequest request, @RequestBody List<ProjectCarWorkInfo> workInfoList) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            for (ProjectCarWorkInfo info : workInfoList) {
                info.setProjectId(projectId);
                if (info.getTimeDischarge() != null && info.getTimeDischarge().getTime() > 0) {
                    Map<String, Date> dateMap = workDateService.getWorkTime(projectId, info.getTimeDischarge());
                    Date date = dateMap.get("start");
                    date = DateUtils.createReportDateByMonth(date);
                    info.setDateIdentification(date);
                }
                projectCarWorkInfoServiceI.save(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @Transactional
    public Result delete(@RequestBody List<Long> ids) {
        projectCarWorkInfoServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/upload")
    public Result uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            String fileName = DateUtils.formatDateByPattern(new Date(), SmartminingConstant.YEARMONTHDAUFORMAT + SmartminingConstant.XLSSUFFIX);
            String path = FileUtils.uploadFile(file, fileName);
            File localFile = new File(path);
            InputStream is = new FileInputStream(localFile);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            //获取到当前sheet的总行数
            Integer rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 0; i < rowCount; i++) {
                ProjectCarWorkInfo workInfo = new ProjectCarWorkInfo();
                workInfo.setProjectId(projectId);
                //去除第一行 第一行的数据不需要读取
                Row row = sheet.getRow(i + 1);
                //获取到挖机编号
                Cell cellOne = row.getCell(0);
                if (StringUtils.isNotEmpty(cellOne.getStringCellValue())) {
                    String carCode = cellOne.getStringCellValue();
                    ProjectCar projectCar = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                    workInfo.setCarId(projectCar.getId());
                    workInfo.setCarCode(carCode);
                    workInfo.setCarOwnerId(projectCar.getOwnerId());
                    workInfo.setCarOwnerName(projectCar.getOwnerName());
                }
                //获取到渣车编号
                Cell cellTwo = row.getCell(1);
                if (StringUtils.isNotEmpty(cellTwo.getStringCellValue())) {
                    String diggingCode = cellTwo.getStringCellValue();
                    ProjectDiggingMachine machine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, diggingCode);
                    workInfo.setDiggingMachineId(machine.getId());
                    workInfo.setDiggingMachineCode(diggingCode);
                }
                //获取到检测状态
                Cell cellThree = row.getCell(2);
                if (StringUtils.isNotEmpty(cellThree.getStringCellValue())) {
                    Integer status = Integer.valueOf(cellThree.getStringCellValue());
                    ProjectCarWorkStatus projectCarWorkStatus = ProjectCarWorkStatus.getName(status);
                    workInfo.setStatus(projectCarWorkStatus);
                }
                //获取材料
                Cell cellFour = row.getCell(3);
                String materialName = cellFour.getStringCellValue();
                if (StringUtils.isNotEmpty(materialName)) {
                    ProjectMaterial projectMaterial = projectMaterialServiceI.getByProjectIdAndName(projectId, materialName);
                    workInfo.setMaterialId(projectMaterial.getId());
                    workInfo.setMateriaName(materialName);
                }
                //获取装载时间
                Cell cellFive = row.getCell(4);
                if (StringUtils.isNotEmpty(cellFive.getStringCellValue())) {
                    Date timeLoad = DateUtils.stringFormatDate(cellFive.getStringCellValue(), SmartminingConstant.DATEFORMAT);
                    workInfo.setTimeLoad(timeLoad);
                }
                //获取检测时间
                Cell cellSix = row.getCell(5);
                if (StringUtils.isNotEmpty(cellSix.getStringCellValue())) {
                    Date timeCheck = DateUtils.stringFormatDate(cellSix.getStringCellValue(), SmartminingConstant.DATEFORMAT);
                    workInfo.setTimeCheck(timeCheck);
                }
                //获取卸载时间
                Cell cellSeven = row.getCell(6);
                if (StringUtils.isNotEmpty(cellSeven.getStringCellValue())) {
                    Date timeDischarge = DateUtils.stringFormatDate(cellSeven.getStringCellValue(), SmartminingConstant.DATEFORMAT);
                    workInfo.setTimeDischarge(timeDischarge);
                }
                //获取装载高度
                Cell cellEight = row.getCell(7);
                if (StringUtils.isNotEmpty(cellEight.getStringCellValue())) {
                    Integer height = Integer.valueOf(cellEight.getStringCellValue());
                    workInfo.setHeight(height);
                }
                //获取是否合格
                Cell cellNine = row.getCell(8);
                if (StringUtils.isNotEmpty(cellNine.getStringCellValue())) {
                    Integer value = Integer.valueOf(cellNine.getStringCellValue());
                    Score score = Score.getName(value);
                    workInfo.setPass(score);
                }
                //获取卸载场地
                Cell cellTen = row.getCell(9);
                if (StringUtils.isNotEmpty(cellTen.getStringCellValue())) {
                    Long distance = Long.parseLong(cellTen.getStringCellValue());
                    ProjectSlagSite projectSlagSite = projectSlagSiteServiceI.getByProjectIdAndDistance(projectId, distance);
                    workInfo.setSlagSiteId(projectSlagSite.getId());
                    workInfo.setSlagSiteName(projectSlagSite.getName());
                    workInfo.setDistance(distance);
                    workInfo.setPayableDistance(distance);
                }
                //获取备注
                Cell cellEleven = row.getCell(10);
                if (StringUtils.isNotEmpty(cellEleven.getStringCellValue())) {
                    workInfo.setRemark(cellEleven.getStringCellValue());
                }
                workInfo.setCreateDate(new Date());
                projectCarWorkInfoServiceI.save(workInfo);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping(value = "/stop", produces = "application/json")
    @Transactional
    public Result stopRightNow(@RequestBody HashMap<Long, Long> idDistances) {
        try {
            for (Long id : idDistances.keySet()) {
                if (id != null) {
                    Long distance = idDistances.get(id);
                    ProjectCarWorkInfo info = projectCarWorkInfoServiceI.get(id);
                    info.setPass(Score.Pass);
                    info.setStatus(ProjectCarWorkStatus.Finish);
                    info.setStopByManual(true);
                    if (distance != null) info.setDistance(distance);
                    projectCarWorkInfoServiceI.save(info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.ok();
    }

    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestParam Date startDate, @RequestParam Date endDate, String carCode, Long[] slagIds) {
        OutputStream outputStream = null;
        try {
            Long time = endDate.getTime() - startDate.getTime();
            if (time > (1000 * 3600 * 24 * 7))
                throw new SmartminingProjectException("日期间隔必须小于等于一周");
            Long projectId = Long.parseLong(request.getHeader("projectId"));

            Specification<ProjectCarWorkInfo> spec = new Specification<ProjectCarWorkInfo>() {
                List<Predicate> list = new ArrayList<>();

                @Override
                public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    if (startDate != null)
                        list.add(criteriaBuilder.greaterThan(root.get("createDate").as(Date.class), startDate));
                    if (endDate != null)
                        list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDate").as(Date.class), endDate));
                    if (StringUtils.isNotEmpty(carCode))
                        list.add(criteriaBuilder.equal(root.get("carCode").as(String.class), carCode));
                    if (slagIds != null && slagIds.length > 0) {
                        List<Long> slagList = new ArrayList<>();
                        for (int i = 0; i < slagIds.length; i++) {
                            slagList.add(slagIds[i]);
                        }
                        list.add(root.get("slagSiteId").as(Long.class).in(slagList));
                    }

                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarWorkInfo> projectCarWorkInfoList = projectCarWorkInfoServiceI.queryAllByParams(spec);
            String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
            File file = new File(path + File.separator + SmartminingConstant.WORKFILENAME);
            if (!file.exists())
                file.createNewFile();
            String filePath = file.getPath();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            Row rowFirst = sheet.createRow(0);
            rowFirst.setHeight((short) (15.625 * 30));
            Cell zeroFirst = rowFirst.createCell(0);
            zeroFirst.setCellValue("序号");
            Cell oneFirst = rowFirst.createCell(1);
            oneFirst.setCellValue("渣车编号");
            Cell twoFirst = rowFirst.createCell(2);
            twoFirst.setCellValue("挖机编号");
            Cell threeFirst = rowFirst.createCell(3);
            threeFirst.setCellValue("状态");
            Cell fourFirst = rowFirst.createCell(4);
            fourFirst.setCellValue("是否扣除");
            Cell fiveFirst = rowFirst.createCell(5);
            fiveFirst.setCellValue("材料");
            Cell sixFirst = rowFirst.createCell(6);
            sixFirst.setCellValue("是否合格");
            Cell sevenFirst = rowFirst.createCell(7);
            sevenFirst.setCellValue("备注");
            Cell eightFirst = rowFirst.createCell(8);
            eightFirst.setCellValue("装载时间");
            Cell nineFirst = rowFirst.createCell(9);
            nineFirst.setCellValue("检测时间");
            Cell tenFirst = rowFirst.createCell(10);
            tenFirst.setCellValue("卸载时间");
            Cell elevenFirst = rowFirst.createCell(11);
            elevenFirst.setCellValue("装载高度");
            Cell twelveFirst = rowFirst.createCell(12);
            twelveFirst.setCellValue("卸载场地");
            Cell thirteenFirst = rowFirst.createCell(13);
            thirteenFirst.setCellValue("运距(米)");
            Cell fourteenFirst = rowFirst.createCell(14);
            fourteenFirst.setCellValue("班次");
            for (int i = 0; i < projectCarWorkInfoList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                row.setHeight((short) (15.625 * 30));
                Cell zero = row.createCell(0);
                zero.setCellValue(i + 1);
                Cell one = row.createCell(1);
                one.setCellValue(projectCarWorkInfoList.get(i).getCarCode());
                Cell two = row.createCell(2);
                two.setCellValue(projectCarWorkInfoList.get(i).getDiggingMachineCode());
                Cell three = row.createCell(3);
                three.setCellValue(projectCarWorkInfoList.get(i).getStatus().getValue());
                Cell four = row.createCell(4);
                four.setCellValue(projectCarWorkInfoList.get(i).getIsVaild().getName());
                Cell five = row.createCell(5);
                five.setCellValue(projectCarWorkInfoList.get(i).getMaterialName());
                Cell six = row.createCell(6);
                six.setCellValue(projectCarWorkInfoList.get(i).getPass().getValue());
                Cell seven = row.createCell(7);
                seven.setCellValue(projectCarWorkInfoList.get(i).getRemark());
                Cell eight = row.createCell(8);
                eight.setCellValue(DateUtils.formatDateByPattern(projectCarWorkInfoList.get(i).getTimeLoad(), SmartminingConstant.DATEFORMAT));
                Cell nine = row.createCell(9);
                nine.setCellValue(DateUtils.formatDateByPattern(projectCarWorkInfoList.get(i).getTimeCheck(), SmartminingConstant.DATEFORMAT));
                Cell ten = row.createCell(10);
                ten.setCellValue(DateUtils.formatDateByPattern(projectCarWorkInfoList.get(i).getTimeDischarge(), SmartminingConstant.DATEFORMAT));
                Cell eleven = row.createCell(11);
                eleven.setCellValue(new BigDecimal(projectCarWorkInfoList.get(i).getHeight() / 100L).doubleValue());
                Cell twelve = row.createCell(12);
                twelve.setCellValue(projectCarWorkInfoList.get(i).getSlagSiteName());
                Cell thirteen = row.createCell(13);
                thirteen.setCellValue(new BigDecimal(projectCarWorkInfoList.get(i).getDistance() / 100L).doubleValue());
                Cell fourteent = row.createCell(14);
                fourteent.setCellValue(projectCarWorkInfoList.get(i).getShift().getValue());
            }
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            //outputStream.close();
            excelReportService.downLoadFile(response, request, filePath, new Date());
            FileUtils.delFile(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmartminingProjectException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/queryTemp")
    public Result queryTempInfo(HttpServletRequest request, @RequestParam String carCode, String machineCode, @RequestParam Long slagSiteId, Date timeCheck) {
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        String message = stringRedisTemplate.opsForValue().get("temp_unload" + carCode + projectId);
        if(StringUtils.isEmpty(message)) {
            try {
                stringRedisTemplate.opsForValue().set("temp_unload" + carCode + projectId, "allReady", 5 * 60, TimeUnit.SECONDS);
                Date date = new Date();
                List<ProjectCarWorkInfo> projectCarWorkInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndCarCodeAndDateIdentification(projectId, carCode, date);
                //工作信息
                ProjectCarWorkInfo workInfo = null;
                ProjectCar car = projectCarServiceI.getByProjectIdAndCode(projectId, carCode);
                if (car == null)
                    throw new SmartminingProjectException("该渣车不存在");
                if (projectCarWorkInfoList != null && projectCarWorkInfoList.size() > 0) {
                    workInfo = projectCarWorkInfoList.get(0);
                } else {
                    workInfo = new ProjectCarWorkInfo();
                    workInfo.setProjectId(projectId);
                    workInfo.setCarId(car.getId());
                    workInfo.setCarCode(carCode);
                    workInfo.setCarOwnerId(car.getOwnerId());
                    workInfo.setCarOwnerName(car.getOwnerName());
                    workInfo.setTimeLoad(date);
                    //workInfo.setCreateDate(new Date());
                }
                //方量
                Long cubic = car.getModifyCapacity();
                workInfo.setCubic(cubic);
                workInfo.setTimeCheck(timeCheck);
                List<ScheduleCar> scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, car.getId(), true);
                if (scheduleCar == null || scheduleCar.size() == 0)
                    throw new SmartminingProjectException("该渣车暂未排班");
                List<ScheduleMachine> returnMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.get(0).getGroupCode());

                ProjectSlagSite slagSite = projectSlagSiteServiceI.get(slagSiteId);
                if (slagSite == null)
                    throw new SmartminingProjectException("渣场不存在");
                workInfo.setSlagSiteName(slagSite.getName());
                workInfo.setSlagSiteId(slagSiteId);
                ScheduleMachine scheduleMachine = null;
                if (StringUtils.isNotEmpty(machineCode)) {
                    ProjectDiggingMachine machine = projectDiggingMachineServiceI.getByProjectIdAndCode(projectId, machineCode);
                    scheduleMachine = scheduleMachineServiceI.getByProjectIdAndMachineCode(projectId, machineCode);
                    workInfo.setDiggingMachineId(machine.getId());
                    workInfo.setDiggingMachineCode(machineCode);
                    workInfo.setMaterialId(scheduleMachine.getMaterialId());
                    workInfo.setMaterialName(scheduleMachine.getMaterialName());
                    workInfo.setPricingType(scheduleMachine.getPricingType());
                } else {
                /*List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, car.getId(), true);
                if (scheduleCarList == null || scheduleCarList.size() == 0)
                    throw new SmartminingProjectException("当前渣车并未进行排班");*/
                    List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.get(0).getGroupCode());
                    workInfo.setDiggingMachineId(scheduleMachineList.get(0).getMachineId());
                    workInfo.setDiggingMachineCode(scheduleMachineList.get(0).getMachineCode());
                    workInfo.setMaterialId(scheduleMachineList.get(0).getMaterialId());
                    workInfo.setMaterialName(scheduleMachineList.get(0).getMaterialName());
                    workInfo.setPricingType(scheduleMachineList.get(0).getPricingType());

                }
                workInfo.setTimeDischarge(date);
                Shift shift = workDateService.getShift(workInfo.getTimeDischarge(), projectId);
                workInfo.setShift(shift);
                workInfo.setStatus(ProjectCarWorkStatus.Unknown);
                workInfo.setCreateDate(new Date());

                Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
                Date dateIdentification = dateMap.get("start");
                dateIdentification = DateUtils.createReportDateByMonth(dateIdentification);
                workInfo.setDateIdentification(dateIdentification);
                Map map = new HashMap();
                map.put("scheduleMachineList", returnMachineList);
                map.put("workInfo", workInfo);
                return Result.ok(map);
            } catch (SmartminingProjectException e) {
                stringRedisTemplate.delete("temp_unload" + carCode + projectId);
                e.printStackTrace();
                return Result.error(e.getMsg());
            } catch (IOException e) {
                stringRedisTemplate.delete("temp_unload" + carCode + projectId);
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
        }else {
            return Result.error("该渣场临时卸载正在处理中，请勿重复刷卡");
        }
    }

    /**
     * 临时倒渣
     *
     * @param projectTempSiteLog
     * @return
     * @throws IOException
     */
    @RequestMapping("/saveTemp")
    public Result saveTemp(ProjectTempSiteLog projectTempSiteLog) throws IOException {
        Long projectId = projectTempSiteLog.getProjectId();
        try {
            Project project = projectServiceI.get(projectId);
            Long carId = projectTempSiteLog.getCarId();
            String carCode = projectTempSiteLog.getCarCode();
            Long cubic = projectCarServiceI.get(carId).getModifyCapacity();
            Date timeDischarge = projectTempSiteLog.getTimeDischarge();
            List<ScheduleCar> scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true);
            if (scheduleCarList == null || scheduleCarList.size() < 1)
                throw new SmartminingProjectException("渣车 " + carCode + "暂无对应排班信息");
            ProjectCar projectCar = projectCarServiceI.get(carId);
            if (projectCar == null)
                throw new SmartminingProjectException("渣车 " + carCode + "不存在");
            List<ScheduleMachine> scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndMachineIdAndIsVaild(projectId, projectTempSiteLog.getDiggingMachineId(), true);
            if (scheduleMachineList == null || scheduleMachineList.size() < 1)
                throw new SmartminingProjectException("渣车 " + carCode + "排班中不存在挖机");
            Long diggingMachineId = projectTempSiteLog.getDiggingMachineId();
            if(diggingMachineId == null || diggingMachineId == 0)
                throw new SmartminingProjectException("请选择对应的挖机");
            String diggingMachineCode = projectTempSiteLog.getDiggingMachineCode();
            if(StringUtils.isEmpty(diggingMachineCode))
                throw new SmartminingProjectException("请选择对应的挖机");
            Long materialId = scheduleMachineList.get(0).getMaterialId();
            String materialName = scheduleMachineList.get(0).getMaterialName();
            //计价方式
            PricingTypeEnums pricingType = scheduleMachineList.get(0).getPricingType();
            ProjectSchedule projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCarList.get(0).getGroupCode());
            JSONArray slagSiteArray = JSONArray.parseArray(projectSchedule.getSlagSiteId());
            List<String> slagSiteList = new ArrayList<>();
            if (slagSiteArray != null) {
                for (int i = 0; i < slagSiteArray.size(); i++) {
                    String slagSiteId = slagSiteArray.getString(i);
                    slagSiteList.add(slagSiteId);
                }
            }
            Long distance = scheduleMachineList.get(0).getDistance() + projectTempSiteLog.getDistance();
            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, distance);
            Long payableDistance = distance > maxDistance ? distance : projectCarMaterial.getDistance();
            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (cubic / 1000000L); //精确到分
            Calendar calendar1 = Calendar.getInstance(), calendar2 = Calendar.getInstance();
            calendar2.setTime(project.getEarlyStartTime());
            calendar1.setTime(timeDischarge);
            calendar1.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
            calendar1.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
            calendar1.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
            Date earlyStart = calendar1.getTime();
            if (earlyStart.compareTo(timeDischarge) > 0) {
                calendar1.add(Calendar.DAY_OF_MONTH, -1);
                earlyStart = calendar1.getTime();
            }
            Shift shift = workDateService.getShift(timeDischarge, projectId);
            //日期标识
            Date dateIdentification = DateUtils.createReportDateByMonth(earlyStart);
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            ProjectCarWorkInfo projectCarWorkInfo = new ProjectCarWorkInfo();
            projectCarWorkInfo.setProjectId(projectId);
            projectCarWorkInfo.setCarId(carId);
            projectCarWorkInfo.setCarCode(carCode);
            projectCarWorkInfo.setCubic(cubic);
            projectCarWorkInfo.setTimeLoad(projectTempSiteLog.getTimeLoad());
            projectCarWorkInfo.setTimeDischarge(timeDischarge);
            projectCarWorkInfo.setShift(shift);
            projectCarWorkInfo.setDateIdentification(dateIdentification);
            if (slagSiteList != null && !slagSiteList.contains(projectTempSiteLog.getSlagSiteId().toString())) {
                projectCarWorkInfo.setRemark("卸载场地错误");
            }
            projectCarWorkInfo.setSlagSiteId(projectTempSiteLog.getSlagSiteId());
            projectCarWorkInfo.setSlagSiteName(projectTempSiteLog.getSlagSiteName());
            projectCarWorkInfo.setCarOwnerId(projectCar.getOwnerId());
            projectCarWorkInfo.setCarOwnerName(projectCar.getOwnerName());
            projectCarWorkInfo.setDiggingMachineId(diggingMachineId);
            projectCarWorkInfo.setDiggingMachineCode(diggingMachineCode);
            projectCarWorkInfo.setMaterialId(materialId);
            projectCarWorkInfo.setMateriaName(materialName);
            projectCarWorkInfo.setDistance(distance);
            projectCarWorkInfo.setPayableDistance(payableDistance);
            projectCarWorkInfo.setAmount(amount);
            projectCarWorkInfo.setPricingType(pricingType);
            projectCarWorkInfo.setStatus(ProjectCarWorkStatus.Finish);
            projectCarWorkInfo.setPass(Score.Pass);
            projectCarWorkInfo.setUnLoadUp(true);
            projectCarWorkInfo.setRemark(projectCarWorkInfo.getRemark() + " 临时倒渣");
            projectCarWorkInfoServiceI.save(projectCarWorkInfo);

            //app即时报表展示的数据
            ProjectAppStatisticsByCar appCar = projectAppStatisticsByCarServiceI.getAllByProjectIdAndCarCodeAndShiftAndDate(projectId, carCode, shift.getAlias(), dateIdentification);
            if (appCar == null) {
                appCar = new ProjectAppStatisticsByCar();
            }
            appCar.setCubic(appCar.getCubic() + cubic);
            appCar.setCarCount(appCar.getCarCount() + 1);
            appCar.setShift(ShiftsEnums.converShift(shift.getAlias()));
            appCar.setProjectId(projectId);
            appCar.setCarCode(carCode);
            appCar.setCreateDate(dateIdentification);
            projectAppStatisticsByCarServiceI.save(appCar);
            handler.saveCarInfo(projectId, carCode, carId, CarType.SlagCar, shift, dateIdentification, projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), timeDischarge, payableDistance);
            handler.saveCarInfo(projectId, projectCarWorkInfo.getDiggingMachineCode(), projectCarWorkInfo.getDiggingMachineId(), CarType.DiggingMachine, shift, dateIdentification, projectCarWorkInfo.getMaterialId(), projectCarWorkInfo.getMaterialName(), timeDischarge, payableDistance);

            String cmdInd = "slagcar";
            String replytopic = "smartmining/cloud/count/post/reply";
            handler.handleAndroidAppGetStatus(cmdInd, replytopic, projectId, carCode);
        } catch (Exception ex) {
            stringRedisTemplate.delete("temp_unload" + projectTempSiteLog.getCarCode() + projectId);
            WorkMergeErrorLog errorLog = new WorkMergeErrorLog();
            errorLog.setProjectId(projectTempSiteLog.getProjectId());
            errorLog.setCarCode(projectTempSiteLog.getCarCode());
            errorLog.setCarId(projectTempSiteLog.getCarId());
            errorLog.setMessage(ex.getMessage());
            errorLog.setDetailMessage(JSON.toJSONString(ex.getStackTrace()));
            errorLog.setProjectDevice(ProjectDeviceType.SlagFieldDevice);
            errorLog.setTimeLoad(projectTempSiteLog.getTimeLoad());
            errorLog.setTimeCheck(projectTempSiteLog.getTimeCheck());
            errorLog.setTimeDischarge(projectTempSiteLog.getTimeDischarge());
            errorLog.setCreateDate(new Date());
            errorLog.setUid("临时倒渣");
            try {
                workMergeErrorLogServiceI.save(errorLog);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("添加失败");
            }
            log.error(ex.getMessage());
            log.error("插入作业信息异常，可能是没有对应的排班信息或装载信息");
        }
        return Result.ok();
    }

    //@RequestMapping("/saveTemp")
    public Result saveTemp(HttpServletRequest request, ProjectCarWorkInfo projectCarWorkInfo) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Long distance = projectCarWorkInfo.getDistance();
            Long maxDistance = projectCarMaterialServiceI.getMaxDistanceByProjectId(projectId);
            ProjectCarMaterial projectCarMaterial = projectCarMaterialServiceI.getPayableByProjectIdAndDistance(projectId, projectCarWorkInfo.getDistance());
            Long overPrice = projectCarMaterialServiceI.getOverDistancePriceByProjectId(projectId);
            Long amount = (projectCarMaterial.getPrice() + (distance > maxDistance ? (distance - maxDistance) / 10000 * overPrice : 0)) * (projectCarWorkInfo.getCubic() / 1000000L); //精确到分
            projectCarWorkInfo.setAmount(amount);
            projectCarWorkInfo.setPayableDistance(distance);
            projectCarWorkInfoServiceI.save(projectCarWorkInfo);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    //@RequestMapping("/recovery")
    public Result dataRecovery(HttpServletRequest request, @RequestParam Date startTime, @RequestParam Date endTime) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        projectCarWorkInfoServiceI.deleteAllByProjectIdAndTimeDischarge(projectId, startTime, endTime);
        List<ProjectUnloadLog> projectUnloadLogList = projectUnloadLogServiceI.getAllByProjectIDAndTime(projectId, startTime, endTime);
        List<ProjectSlagCarLog> projectSlagCarLogList = projectSlagCarLogServiceI.getAllByProjectIDAndTimeDischarge(projectId, startTime, endTime);
        //生成索引
        Map<String, Integer> slagCarLogIndexMap = new HashMap<>();
        for (int i = 0; i < projectSlagCarLogList.size(); i++) {
            slagCarLogIndexMap.put(projectSlagCarLogList.get(i).getCarCode() + projectSlagCarLogList.get(i).getTimeLoad().getTime(), i);
        }
        DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
        for (int i = 0; i < projectUnloadLogList.size(); i++) {
            ProjectUnloadLog log = projectUnloadLogList.get(i);
            String carCode = log.getCarCode();
            Date timeLoad = log.getTimeLoad();
            if (timeLoad == null || timeLoad.getTime() == 0) {
                handler.updateCarWorkInfoNew(log);
            } else {
                Integer index = slagCarLogIndexMap.get(carCode + timeLoad.getTime());
                if (index != null) {
                    ProjectSlagCarLog projectSlagCarLog = projectSlagCarLogList.get(index);
                    handler.updateCarWorkInfo(projectSlagCarLog);
                    handler.updateCarWorkInfoNew(log);
                } else
                    continue;
            }
        }
        return Result.ok();
    }

    @RequestMapping("/merge")
    public Result merge() {
        try {
            Date date = DateUtils.stringFormatDate("2019-12-13 07:24:52", SmartminingConstant.DATEFORMAT);
            ProjectUnloadLog log = projectUnloadLogServiceI.getAllByProjectIDAndTimeDischargeAndCarCode(1L, date, "0621");
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            handler.updateCarWorkInfoNew(log);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error();
        }
        return Result.ok();
    }

    @RequestMapping("/recovery")
    public Result recovery(){
        List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.getAllByProjectIdAndDateIdentificationAndShiftAndRemark();
        List<String> dateList = new ArrayList<>();
        for(ProjectCarWorkInfo info : workInfoList){
            dateList.add(info.getCarCode() + info.getTimeDischarge().getTime());
        }
        Date date = DateUtils.createReportDateByMonth(DateUtils.stringFormatDate("2019-12-13 00:00:00", SmartminingConstant.DATEFORMAT));
        List<ProjectMqttCardReport> cardReportList = projectMqttCardReportServiceI.getAllByProjectIdAndDateIdentificationAndShift(1L, date, Shift.Early.getAlias());
        List<Long> ids = new ArrayList<>();
        for(ProjectMqttCardReport report : cardReportList){
            Date time = report.getTimeDischarge();
            String carCode = report.getCarCode();
            String key = carCode + time.getTime();
            if(dateList.contains(key)){
                ids.add(report.getId());
            }
        }
        projectMqttCardReportServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/mergeSlagCar")
    public Result mergeSlagCar(){
        try {
            Date date = DateUtils.stringFormatDate("2019-11-22 13:03:42", SmartminingConstant.DATEFORMAT);
            ProjectSlagCarLog log = projectSlagCarLogServiceI.getAllByProjectIDAndCarCodeAndTerminalTime(1L, "0795", date.getTime());
            DeviceMessageHandler handler = (DeviceMessageHandler) SpringUtils.getBean("deviceMessageHandler");
            handler.updateCarWorkInfo(log);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
