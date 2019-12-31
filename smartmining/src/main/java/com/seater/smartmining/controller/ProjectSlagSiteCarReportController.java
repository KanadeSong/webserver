package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectCarWorkInfo;
import com.seater.smartmining.entity.ProjectSlagSiteCarReport;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.schedule.ScheduleService;
import com.seater.smartmining.service.ProjectCarWorkInfoServiceI;
import com.seater.smartmining.service.ProjectSlagSiteCarReportServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/29 0029 15:23
 */
@RestController
@RequestMapping("/api/projectSlagSiteCarReport")
public class ProjectSlagSiteCarReportController {

    @Autowired
    private ProjectSlagSiteCarReportServiceI projectSlagSiteCarReportServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private WorkDateService workDateService;
    @Autowired
    private ExcelReportService excelReportService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, @RequestParam Date reportDate){
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            ScheduleService.slagSiteCarReport(projectId, reportDate, null);
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize,String carCode, Date startTime, Date endTime, String slagSiteName){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Specification<ProjectSlagSiteCarReport> spec = new Specification<ProjectSlagSiteCarReport>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectSlagSiteCarReport> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (carCode != null && !carCode.isEmpty())
                    list.add(criteriaBuilder.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                if (slagSiteName != null && !slagSiteName.isEmpty())
                    list.add(criteriaBuilder.like(root.get("slagSiteName").as(String.class), "%" + slagSiteName + "%"));
                if(startTime != null && endTime != null)
                    list.add(criteriaBuilder.between(root.get("reportDate").as(Date.class), startTime, endTime));
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                return null;
            }
        };
        return Result.ok(projectSlagSiteCarReportServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/detail")
    public Result detail(HttpServletRequest request, @RequestParam String carCode, @RequestParam Long slagSiteId, Integer shift, @RequestParam Date reportDate){
        List<Date> dateList = new ArrayList<>();
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, reportDate);
            Date startTime = dateMap.get("start");
            Date endTime = dateMap.get("end");
            Specification<ProjectCarWorkInfo> spec = new Specification<ProjectCarWorkInfo>() {
                List<Predicate> list = new ArrayList<Predicate>();
                @Override
                public Predicate toPredicate(Root<ProjectCarWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if(shift != null)
                        list.add(criteriaBuilder.equal(root.get("shift").as(Integer.class), shift));
                    list.add(criteriaBuilder.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                    list.add(criteriaBuilder.equal(root.get("slagSiteId").as(Long.class), slagSiteId));
                    list.add(criteriaBuilder.between(root.get("timeDischarge").as(Date.class), startTime, endTime));
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    query.orderBy(criteriaBuilder.asc(root.get("id").as(Long.class)));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarWorkInfo> workInfoList = projectCarWorkInfoServiceI.query(spec).getContent();
            for(ProjectCarWorkInfo info : workInfoList){
                dateList.add(info.getTimeDischarge());
            }
            return Result.ok(dateList);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestParam Date startDate, @RequestParam Date endDate, Long[] slagIds){
        OutputStream outputStream = null;
        try {
            List<Long> slagIdList = new ArrayList<>();
            if(slagIds != null) {
                for (int i = 0; i < slagIds.length; i++) {
                    slagIdList.add(slagIds[i]);
                }
            }
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Specification<ProjectSlagSiteCarReport> spec = new Specification<ProjectSlagSiteCarReport>() {
                List<Predicate> list = new ArrayList<Predicate>();
                @Override
                public Predicate toPredicate(Root<ProjectSlagSiteCarReport> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if(slagIds != null && slagIdList.size() > 0) {
                        Expression<String> exp = root.<String>get("slagSiteId");
                        list.add(exp.in(slagIdList));
                    }
                    Date startTime = DateUtils.convertDate(startDate.getTime());
                    Date endTime = DateUtils.convertDate(endDate.getTime());
                    list.add(criteriaBuilder.between(root.get("reportDate").as(Date.class), startTime, endTime));
                    list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                    query.orderBy(criteriaBuilder.asc(root.get("carCode").as(Long.class)));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            //List<ProjectSlagSiteCarReport> reportList = projectSlagSiteCarReportServiceI.getAllByProjectIdAndReportDate(projectId, reportDate);
            List<ProjectSlagSiteCarReport> reportList = projectSlagSiteCarReportServiceI.queryAll(spec);
            String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
            File file = new File(path + File.separator + SmartminingConstant.SLAGSITENAME);
            if (!file.exists())
                file.createNewFile();
            String filePath = file.getPath();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            int indexRow = 0;
            for(ProjectSlagSiteCarReport report : reportList){
                Row row = sheet.createRow(indexRow);
                row.setHeight((short) (15.625 * 30));

                Cell one = row.createCell(0);
                one.setCellValue("日期：" + DateUtils.formatDateByPattern(report.getReportDate(), SmartminingConstant.YEARMONTHDAUFORMAT));

                Cell two = row.createCell(1);
                if(report.getShift().compareTo(ShiftsEnums.DAYSHIFT) == 0)
                    two.setCellValue("班次：白班");
                else
                    two.setCellValue("班次：晚班");

                Cell three = row.createCell(2);
                three.setCellValue("渣场：" + report.getSlagSiteName());

                Cell four = row.createCell(3);
                four.setCellValue("运距：" + (report.getDistance() / 100L) +" 米");

                Cell five = row.createCell(4);
                five.setCellValue("渣车：" + report.getCarCode());

                Cell six = row.createCell(5);
                six.setCellValue("总车数：" + report.getCount());

                JSONArray detailArray = JSONArray.parseArray(report.getDetailJson());

                Row rowTwo = sheet.createRow(indexRow + 1);
                rowTwo.setHeight((short) (15.625 * 30));
                //行下标
                int rowIndex = indexRow + 1;
                //列下标
                int colIndex = 0;
                for(int i = 0; i < detailArray.size(); i++){
                    if(i % 4 == 0 && i != 0){
                        rowIndex ++;
                        rowTwo = sheet.createRow(rowIndex);
                        colIndex = 0;
                        indexRow ++;
                    }
                    Cell cell = rowTwo.createCell(colIndex);
                    cell.setCellValue(DateUtils.formatDateByPattern(DateUtils.convertDate(detailArray.getLong(i)), SmartminingConstant.DATEFORMAT));
                    colIndex ++;
                }
                indexRow = indexRow + 3;
            }
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            excelReportService.downLoadFile(response, request, filePath, new Date());
            FileUtils.delFile(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
