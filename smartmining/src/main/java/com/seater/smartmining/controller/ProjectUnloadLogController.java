package com.seater.smartmining.controller;


import com.alibaba.fastjson.JSON;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectCheckLog;
import com.seater.smartmining.entity.ProjectSlagSite;
import com.seater.smartmining.entity.ProjectUnloadLog;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.exception.SmartminingProjectException;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.service.ProjectSlagSiteServiceI;
import com.seater.smartmining.service.ProjectUnloadLogServiceI;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.util.CommonUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/projectUnloadLog")
public class ProjectUnloadLogController extends BaseController{
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ExcelReportService excelReportService;

    @RequestMapping("/query")
    public Object query(@RequestParam(value = "rangePickerValue", required = false) ArrayList<String> reangePickerValue, Integer current, Integer pageSize, String eventId, Long projectCarId, Long diggingMachineId, HttpServletRequest request) {
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

        Specification<ProjectUnloadLog> spec = new Specification<ProjectUnloadLog>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectUnloadLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if(projectCarId != null) {
                    list.add(cb.equal(root.get("carId").as(Long.class), projectCarId));
                }

                if(diggingMachineId != null) {
                    list.add(cb.equal(root.get("excavatCurrent").as(Long.class), diggingMachineId));
                }

                if(eventId != null) {
                    list.add(cb.like(root.get("eventId").as(String.class), "%" + eventId + "%"));
                }

                if(reangePickerValue != null && reangePickerValue.size() == 2) {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", Locale.ENGLISH);
                        Date startTime = simpleDateFormat.parse(reangePickerValue.get(0));
                        Date endTime = simpleDateFormat.parse(reangePickerValue.get(1));
                        list.add(cb.between(root.get("recviceDate").as(Date.class), startTime, endTime));
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }

                list.add(cb.equal(root.get("projectID").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                query.orderBy(cb.desc(root.get("id").as(Long.class)));

                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        return projectUnloadLogServiceI.query(spec, PageRequest.of(cur, page));
    }

    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestParam Date reportDate, @RequestParam Shift shift, Long[] slagIds) {
        OutputStream outputStream = null;
        try {
            List<Long> slagIdList = new ArrayList<>();
            if (slagIds != null) {
                for (int i = 0; i < slagIds.length; i++) {
                    slagIdList.add(slagIds[i]);
                }
            }
            Long projectId = CommonUtil.getProjectId(request);
            Map<String, Date> dateMap = AutoApiUtils.returnWorkDate().getWorkTime(projectId, reportDate);
            Date startTime = new Date(0);
            Date endTime = new Date(0);
            if (shift.compareTo(Shift.Early) == 0) {
                startTime = dateMap.get("start");
                endTime = dateMap.get("earlyEnd");
            } else if (shift.compareTo(Shift.Night) == 0) {
                startTime = dateMap.get("nightStart");
                endTime = dateMap.get("end");
            }
            reportDate = DateUtils.createReportDateByMonth(reportDate);
            Date startDate = DateUtils.convertDate(startTime.getTime());
            Date endDate = DateUtils.convertDate(endTime.getTime());
            List<ProjectSlagSite> projectSlagSiteList = projectSlagSiteServiceI.getAllByProjectId(projectId);
            //生成索引
            Map<Long, Integer> slagSiteIndexMap = new HashMap<>();
            for (int i = 0; i < projectSlagSiteList.size(); i++) {
                slagSiteIndexMap.put(projectSlagSiteList.get(i).getId(), i);
            }
            Specification<ProjectUnloadLog> spec = new Specification<ProjectUnloadLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectUnloadLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    if (slagIds != null && slagIdList.size() > 0) {
                        Expression<String> exp = root.<String>get("slagfieldID");
                        list.add(exp.in(slagIdList));
                    }
                    list.add(criteriaBuilder.between(root.get("timeDischarge").as(Date.class), startDate, endDate));
                    list.add(criteriaBuilder.equal(root.get("projectID").as(Long.class), projectId));
                    list.add(criteriaBuilder.isTrue(root.get("isVaild")));
                    query.orderBy(criteriaBuilder.asc(root.get("timeDischarge").as(Long.class)));
                    return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectUnloadLog> projectUnloadLogList = projectUnloadLogServiceI.queryParams(spec);
            List<Map> countList = null;
            if(slagIdList.size() >0)
                countList = projectUnloadLogServiceI.getReportInfoGroupBySlagSite(projectId, startTime, endTime, slagIdList);
            else
                countList = projectUnloadLogServiceI.getReportInfoGroup(projectId, startTime, endTime);
            List<Map> totalCountList = null;
            if(slagIdList.size() >0)
                totalCountList = projectUnloadLogServiceI.getTotalReportInfoByCarCodeAndSlagSite(projectId, startTime, endTime, slagIdList);
            else
                totalCountList = projectUnloadLogServiceI.getTotalReportInfoByCarCode(projectId, startTime, endTime);

            String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
            File file = new File(path + File.separator + SmartminingConstant.SLAGSITENAME);
            if (!file.exists())
                file.createNewFile();
            String filePath = file.getPath();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet();
            int indexRow = 0;
            for (int i = 0; i < countList.size(); i++) {
                //车辆ID
                Long carId = Long.parseLong(countList.get(i).get("carid").toString());
                //车辆编号
                String carCode = countList.get(i).get("car_code").toString();
                //渣场ID
                Long slagSiteId = Long.parseLong(countList.get(i).get("slagfieldid").toString());
                Integer slagSiteIndex = slagSiteIndexMap.get(slagSiteId);
                //渣场名称
                String slagSiteName = "";
                if (slagSiteIndex != null)
                    slagSiteName = projectSlagSiteList.get(slagSiteIndex).getName();
                //总车数
                Long count = Long.parseLong(countList.get(i).get("count").toString());
                //卸载时间集合
                List<Date> dateList = new ArrayList<>();
                for (ProjectUnloadLog log : projectUnloadLogList) {
                    if (carCode.equals(log.getCarCode()) && log.getSlagfieldID().compareTo(slagSiteId) == 0)
                        dateList.add(log.getTimeDischarge());
                }

                Row row = sheet.createRow(indexRow);
                row.setHeight((short) (15.625 * 30));

                Cell one = row.createCell(0);
                one.setCellValue("日期：" + DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT));

                Cell two = row.createCell(1);
                if (shift.compareTo(Shift.Early) == 0)
                    two.setCellValue("班次：白班");
                else
                    two.setCellValue("班次：晚班");

                Cell three = row.createCell(2);
                three.setCellValue("渣场：" + slagSiteName);

                Cell four = row.createCell(3);
                four.setCellValue("渣车：" + carCode);

                Cell five = row.createCell(4);
                five.setCellValue("总车数：" + count);

                Row rowTwo = sheet.createRow(indexRow + 1);
                rowTwo.setHeight((short) (15.625 * 30));
                //行下标
                int rowIndex = indexRow + 1;
                //列下标
                int colIndex = 0;
                for (int j = 0; j < dateList.size(); j++) {
                    if (j % 4 == 0 && j != 0) {
                        rowIndex++;
                        rowTwo = sheet.createRow(rowIndex);
                        colIndex = 0;
                        indexRow++;
                    }
                    Cell cell = rowTwo.createCell(colIndex);
                    cell.setCellValue(DateUtils.formatDateByPattern(DateUtils.convertDate(dateList.get(j).getTime()), SmartminingConstant.DATEFORMAT));
                    colIndex++;
                }
                indexRow = indexRow + 3;
            }
            Long totalCountToday = 0L;
            for(int i = 0; i < totalCountList.size(); i++){
                String carCode = totalCountList.get(i).get("car_code").toString();
                Long totalCount = Long.parseLong(totalCountList.get(i).get("count").toString());
                Row row = sheet.createRow(indexRow);
                row.setHeight((short) (15.625 * 30));
                Cell one = row.createCell(0);
                one.setCellValue("渣车编号：" + carCode);
                Cell two = row.createCell(1);
                two.setCellValue("总车数：" + totalCount);
                indexRow = indexRow + 1;
                totalCountToday = totalCountToday  + totalCount;
            }
            Row row = sheet.createRow(indexRow);
            row.setHeight((short) (15.625 * 30));
            Cell one = row.createCell(0);
            one.setCellValue("合计数：" + totalCountToday);

            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            excelReportService.downLoadFile(response, request, filePath, new Date());
            FileUtils.delFile(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
