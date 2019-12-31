package com.seater.smartmining.controller;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.ProjectCarTotalCountReportByTotal;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.report.ExcelReportService;
import com.seater.smartmining.service.ProjectCarTotalCountReportByTotalServiceI;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/30 0030 13:50
 */
@RestController
@RequestMapping("/api/projectCarTotalCountReportByTotal")
public class ProjectCarTotalCountReportByTotalController extends BaseController{

    @Autowired
    private ProjectCarTotalCountReportByTotalServiceI projectCarTotalCountReportByTotalServiceI;
    @Autowired
    private ExcelReportService excelReportService;

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, @RequestParam Date startTime, @RequestParam Date endTime, Shift shift){
        int cur = (current == null || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Date startDate = DateUtils.createReportDateByMonth(startTime);
        Date endDate = DateUtils.createReportDateByMonth(endTime);
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectCarTotalCountReportByTotal> spec = new Specification<ProjectCarTotalCountReportByTotal>() {
            List<Predicate> list = new ArrayList<Predicate>();

            @Override
            public Predicate toPredicate(Root<ProjectCarTotalCountReportByTotal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.between(root.get("dateIdentification").as(Date.class), startDate, endDate));
                if(projectId != null && projectId != 0)
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                if(shift != null)
                    list.add(cb.equal(root.get("shift").as(Shift.class), shift));
                query.orderBy(cb.desc(root.get("dateIdentification").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectCarTotalCountReportByTotalServiceI.query(spec, PageRequest.of(cur, page)));
    }

    @RequestMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response, @RequestParam Date startTime, @RequestParam Date endTime, Shift shift) {
        Long projectId = CommonUtil.getProjectId(request);
        OutputStream outputStream = null;
        try {
            Specification<ProjectCarTotalCountReportByTotal> spec = new Specification<ProjectCarTotalCountReportByTotal>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarTotalCountReportByTotal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                    if (projectId != null && projectId != 0)
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    if (shift != null)
                        list.add(cb.equal(root.get("shift").as(Shift.class), shift));
                    query.orderBy(cb.desc(root.get("dateIdentification").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarTotalCountReportByTotal> totalList = projectCarTotalCountReportByTotalServiceI.queryAll(spec);
            //生成表格
            String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
            File file = new File(path + File.separator + SmartminingConstant.EXCEPTIONFILENAME);
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
            oneFirst.setCellValue("班次日期");
            Cell twoFirst = rowFirst.createCell(2);
            twoFirst.setCellValue("班别");
            Cell threeFirst = rowFirst.createCell(3);
            threeFirst.setCellValue("渣场数量");
            Cell fourFirst = rowFirst.createCell(4);
            fourFirst.setCellValue("合法数");
            Cell fiveFirst = rowFirst.createCell(5);
            fiveFirst.setCellValue("异常数");
            Cell sixFirst = rowFirst.createCell(6);
            sixFirst.setCellValue("正常率");
            Cell sevenFirst = rowFirst.createCell(7);
            sevenFirst.setCellValue("异常率");
            Cell eightFirst = rowFirst.createCell(8);
            eightFirst.setCellValue("正常合并车数");
            Cell nineFirst = rowFirst.createCell(9);
            nineFirst.setCellValue("纯渣场上传车数");
            Cell tenFirst = rowFirst.createCell(10);
            tenFirst.setCellValue("自动容错");
            Cell elevenFirst = rowFirst.createCell(11);
            elevenFirst.setCellValue("后台异常车数");
            Cell twelveFirst = rowFirst.createCell(12);
            twelveFirst.setCellValue("终端异常车数");
            Cell thirteenFirst = rowFirst.createCell(13);
            thirteenFirst.setCellValue("未安装终端");
            Cell fourteenFirst = rowFirst.createCell(14);
            fourteenFirst.setCellValue("未按规定装载");
            Cell fifteenFirst = rowFirst.createCell(15);
            fifteenFirst.setCellValue("排班不存在");
            Cell sixteenFirst = rowFirst.createCell(16);
            sixteenFirst.setCellValue("渣车不存在");
            Cell seventeenFirst = rowFirst.createCell(17);
            seventeenFirst.setCellValue("渣场不存在");
            Cell eighteenFirst = rowFirst.createCell(18);
            eighteenFirst.setCellValue("不支持混编");
            Cell nineteenFirst = rowFirst.createCell(19);
            nineteenFirst.setCellValue("物料不存在");
            Cell twentyFirst = rowFirst.createCell(20);
            twentyFirst.setCellValue("渣车终端未上传");
            Cell twentyOneFirst = rowFirst.createCell(21);
            twentyOneFirst.setCellValue("排班丢失");
            Cell twentyTwoFirst = rowFirst.createCell(22);
            twentyTwoFirst.setCellValue("挖机不存在");
            Cell twentyThreeFirst = rowFirst.createCell(23);
            twentyThreeFirst.setCellValue("疑似终端异常");
            Cell twentyFourFirst = rowFirst.createCell(24);
            twentyFourFirst.setCellValue("容错失败");
            int i = 0;
            for (ProjectCarTotalCountReportByTotal total : totalList) {
                Row row = sheet.createRow(i + 1);
                row.setHeight((short) (15.625 * 30));
                Cell zero = row.createCell(0);
                zero.setCellValue(i + 1);
                Cell one = row.createCell(1);
                one.setCellValue(DateUtils.formatDateByPattern(total.getDateIdentification(), SmartminingConstant.DATEFORMAT));
                Cell two = row.createCell(2);
                if (total.getShift().compareTo(Shift.Early) == 0)
                    two.setCellValue("早班");
                else if (total.getShift().compareTo(Shift.Night) == 0)
                    two.setCellValue("晚班");
                else
                    two.setCellValue("未知");
                Cell three = row.createCell(3);
                three.setCellValue(total.getTotalCount());
                Cell four = row.createCell(4);
                four.setCellValue(total.getFinishCount());
                Cell five = row.createCell(5);
                five.setCellValue(total.getExceptionCount());
                Cell six = row.createCell(6);
                six.setCellValue(total.getFinishPercent().multiply(new BigDecimal(100)) + "%");
                Cell seven = row.createCell(7);
                seven.setCellValue(total.getExceptionPercent().multiply(new BigDecimal(100)) + "%");
                Cell eight = row.createCell(8);
                eight.setCellValue(total.getSuccessCount());
                Cell nine = row.createCell(9);
                nine.setCellValue(total.getOnlyBySlagSiteSuccessCount());
                Cell ten = row.createCell(10);
                ten.setCellValue(total.getAutoMergeSuccessCount());
                Cell eleven = row.createCell(11);
                eleven.setCellValue(total.getFailByBackStageCount());
                Cell twelve = row.createCell(12);
                twelve.setCellValue(total.getDeviceUnLineErrorCount());
                Cell thirteen = row.createCell(13);
                thirteen.setCellValue(total.getNoHaveDeviceCount());
                Cell fourteen = row.createCell(14);
                fourteen.setCellValue(total.getWorkErrorCount());
                Cell fifteen = row.createCell(15);
                fifteen.setCellValue(total.getWithoutScheduleCount());
                Cell sixteen = row.createCell(16);
                sixteen.setCellValue(total.getWithoutCarCodeCount());
                Cell seventeen = row.createCell(17);
                seventeen.setCellValue(total.getWithoutSlagSiteCodeCount());
                Cell eighteen = row.createCell(18);
                eighteen.setCellValue(total.getScheduleErrorCount());
                Cell nineteen = row.createCell(19);
                nineteen.setCellValue(total.getWithoutLoaderCount());
                Cell twenty = row.createCell(20);
                twenty.setCellValue(total.getWithoutSlagCarDeviceCount());
                Cell twentyOne = row.createCell(21);
                twentyOne.setCellValue(total.getLostScheduleCount());
                Cell twentyTwo = row.createCell(22);
                twentyTwo.setCellValue(total.getWithoutDiggingMachineCount());
                Cell twentyThree = row.createCell(23);
                twentyThree.setCellValue(total.getDeviceErrorLikeCount());
                Cell twentyFour = row.createCell(24);
                twentyFour.setCellValue(total.getRecoverWorkInfoFailCount());
                i++;
            }
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
            excelReportService.downLoadFile(response, request, filePath, new Date());
            FileUtils.delFile(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
