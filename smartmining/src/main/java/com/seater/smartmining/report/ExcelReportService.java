package com.seater.smartmining.report;

import com.alibaba.fastjson.JSONArray;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.file.ExcelUtils;
import com.seater.smartmining.utils.file.FileUtils;
import com.seater.smartmining.utils.params.BeanUtils;
import com.seater.smartmining.utils.string.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Service
public class ExcelReportService {

    @Autowired
    private ProjectServiceI projectServiceI;

    /**
     * 挖机月报表生成excel
     *
     * @param projectDiggingMonthReportList
     * @param reportDate
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public String createDiggingMonthReport(HttpServletRequest request, ProjectDiggingMonthReportTotal total, List<ProjectDiggingMonthReport> projectDiggingMonthReportList, Date reportDate) throws IOException, InvalidFormatException {
        String newPath = returnNewPath(SmartminingConstant.MONTHREPORTMODELPATH, SmartminingConstant.FILENAEMBYDIGGINGMONTH, request);
        Map<String, Object> result = returnExcelSheet(newPath, reportDate);
        Sheet sheet = (Sheet) result.get("sheet");
        //获取到excel的总行数 模板没做好 取到的值为5
        int rowNumber = sheet.getPhysicalNumberOfRows() - 2;
        Workbook workbook = (Workbook) result.get("workbook");
        InputStream is = (InputStream) result.get("is");
        CellStyle style = ExcelUtils.setCellStyleUtilsByMonthReport(workbook);
        int length = projectDiggingMonthReportList.size();
        for (int i = 0; i < length; i++) {
            Row row = sheet.createRow(rowNumber + i);
            row.setHeight((short) (15.625 * 30));
            //设置excel的序号
            Cell cell = row.createCell(0);
            cell.setCellValue(i + 1);
            //设置机器名称
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(projectDiggingMonthReportList.get(i).getMachineName());
            //设置机主名称
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(projectDiggingMonthReportList.get(i).getWorkerName());
            //设置总工时
            Cell cell3 = row.createCell(3);
            BigDecimal workTimeByMachineId = projectDiggingMonthReportList.get(i).getGranWorkCountTime().setScale(1, BigDecimal.ROUND_HALF_UP);
            cell3.setCellValue(workTimeByMachineId.toString());
            //设置计时的总台时
            Cell cell4 = row.createCell(4);
            BigDecimal workTimeByHour = new BigDecimal(0);
            workTimeByHour = projectDiggingMonthReportList.get(i).getGrandWorkTimeByTimer().setScale(1, BigDecimal.ROUND_HALF_UP);
            cell4.setCellValue(workTimeByHour.toString());
            //设置计时的单价
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(projectDiggingMonthReportList.get(i).getSinglePrice() / 100);
            //设置计时的总金额
            Cell cell6 = row.createCell(6);
            Long amountByTimer = projectDiggingMonthReportList.get(i).getGrandTimerAmount() / 100;
            cell6.setCellValue(amountByTimer);
            //设置包方总台时
            Cell cell7 = row.createCell(7);
            BigDecimal timeByCubic = projectDiggingMonthReportList.get(i).getGranWorkTimeByCubic().setScale(2, BigDecimal.ROUND_HALF_UP);
            cell7.setCellValue(timeByCubic.toString());
            //设置包方总车数
            Cell cell8 = row.createCell(8);
            Long totalCars = projectDiggingMonthReportList.get(i).getTotalCount();
            cell8.setCellValue(totalCars);
            //设置包方总方量
            Cell cell9 = row.createCell(9);
            Long totalCubics = projectDiggingMonthReportList.get(i).getGrandTotalCubic() / 1000000;
            cell9.setCellValue(totalCubics);
            //设置包方总金额
            Cell cell10 = row.createCell(10);
            Long totalAmountByCubic = projectDiggingMonthReportList.get(i).getGrandCubeAmout() / 100;
            cell10.setCellValue(totalAmountByCubic);
            //设置包方总用油量
            Cell cell11 = row.createCell(11);
            Long totalUseFill = projectDiggingMonthReportList.get(i).getGrandTotalFill() / 1000;
            cell11.setCellValue(totalUseFill);
            //设置包方用油金额
            Cell cell12 = row.createCell(12);
            Long totalUseFillAmountByCubic = projectDiggingMonthReportList.get(i).getGrandUsingFill() / 100;
            cell12.setCellValue(totalUseFillAmountByCubic);
            //设置包方结余金额
            Cell cell13 = row.createCell(13);
            Long payAmount = projectDiggingMonthReportList.get(i).getPayAmount() / 100;
            cell13.setCellValue(payAmount);
            //设置包月金额为0 （元/月）
            Cell cell14 = row.createCell(14);
            cell14.setCellValue(0);
            //设置工作总金额
            Cell cell15 = row.createCell(15);
            Long totalAmountByWork = totalAmountByCubic + amountByTimer;
            cell15.setCellValue(totalAmountByWork);
            //设置补贴金额
            Cell cell16 = row.createCell(16);
            Long totalSubsidyAmount = projectDiggingMonthReportList.get(i).getSubsidyAmount() / 100;
            cell16.setCellValue(totalSubsidyAmount);
            //设置结算总金额
            Cell cell17 = row.createCell(17);
            Long calculation = payAmount + amountByTimer;
            cell17.setCellValue(calculation);
            //设置总加油量
            Cell cell18 = row.createCell(18);
            cell18.setCellValue(projectDiggingMonthReportList.get(i).getGrandTotalFill() / 1000);
            //设置总加油金额
            Cell cell19 = row.createCell(19);
            cell19.setCellValue(projectDiggingMonthReportList.get(i).getGrandUsingFill() / 100);
            //设置总扣款金额
            Cell cell20 = row.createCell(20);
            Long deduction = projectDiggingMonthReportList.get(i).getDeduction() / 100;
            cell20.setCellValue(deduction);
            //设置应付总金额
            Cell cell21 = row.createCell(21);
            Long shouPay = calculation - deduction + totalSubsidyAmount;
            cell21.setCellValue(shouPay);
            //耗油 小时/升
            Cell cell22 = row.createCell(22);
            BigDecimal useFill = new BigDecimal(0);
            if (workTimeByMachineId != null && workTimeByMachineId.compareTo(new BigDecimal(0)) > 0) {
                useFill = new BigDecimal(totalUseFill).divide(workTimeByMachineId, 2, BigDecimal.ROUND_HALF_UP);
            }
            cell22.setCellValue(useFill.toString());
            Cell cell23 = row.createCell(23);
            BigDecimal carByHour = null;
            if (timeByCubic != null && timeByCubic.compareTo(new BigDecimal(0)) > 0) {
                carByHour = new BigDecimal(totalCars).divide(timeByCubic, 2, BigDecimal.ROUND_HALF_UP);
            } else {
                carByHour = new BigDecimal(0);
            }
            cell23.setCellValue(carByHour.toString());
            //毛利润
            Cell cell24 = row.createCell(24);
            BigDecimal grossProfit = null;
            if (totalCars != null && totalCars != 0) {
                grossProfit = new BigDecimal((float) (totalAmountByWork - totalUseFillAmountByCubic) / totalCars).setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                grossProfit = new BigDecimal(0);
            }
            cell24.setCellValue(grossProfit.toString());
            //油耗
            Cell cell25 = row.createCell(25);
            BigDecimal useFillPercent = new BigDecimal(0);
            if (totalAmountByWork != 0) {
                useFillPercent = new BigDecimal(((float) totalUseFillAmountByCubic / totalAmountByWork) * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            cell25.setCellValue((useFillPercent) + "%");
            for (Cell cell26 : row) {
                cell26.setCellStyle(style);
            }
        }
        /**
         * 合计的写入
         */
        //统计的行标
        int totalIndex = rowNumber + length;
        Row totalRow = writeTotalCount(sheet, totalIndex);
        Cell cellOne = totalRow.createCell(3);
        cellOne.setCellValue(String.valueOf(total.getGranWorkCountTime().setScale(2, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwo = totalRow.createCell(4);
        cellTwo.setCellValue(String.valueOf(total.getGrandWorkTimeByTimer().setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellThree = totalRow.createCell(5);
        cellThree.setCellValue(total.getSinglePrice() / 100);
        Cell cellFour = totalRow.createCell(6);
        cellFour.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandTimerAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellOFive = totalRow.createCell(7);
        cellOFive.setCellValue(String.valueOf(total.getGranWorkTimeByCubic().setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellSix = totalRow.createCell(8);
        cellSix.setCellValue(total.getTotalCount());
        Cell cellSeven = totalRow.createCell(9);
        cellSeven.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandTotalCubic() / 1000000).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellEight = totalRow.createCell(10);
        cellEight.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandCubeAmout() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellNine = totalRow.createCell(11);
        cellNine.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandTotalFill() / 1000).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellTen = totalRow.createCell(12);
        cellTen.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandUsingFill() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellEleven = totalRow.createCell(13);
        cellEleven.setCellValue(String.valueOf(new BigDecimal((float) total.getPayAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwelve = totalRow.createCell(14);
        cellTwelve.setCellValue(String.valueOf(new BigDecimal((float) total.getMonthAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellThirteen = totalRow.createCell(15);
        cellThirteen.setCellValue(String.valueOf(new BigDecimal((float) total.getWorkTotalAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellFourteen = totalRow.createCell(16);
        cellFourteen.setCellValue(String.valueOf(new BigDecimal((float) total.getSubsidyAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellFifteen = totalRow.createCell(17);
        cellFifteen.setCellValue(String.valueOf(new BigDecimal((float) total.getSettlementAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellSixteen = totalRow.createCell(18);
        cellSixteen.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandTotalFill() / 1000).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellSeventeen = totalRow.createCell(19);
        cellSeventeen.setCellValue(String.valueOf(new BigDecimal((float) total.getGrandUsingFill() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellEighteen = totalRow.createCell(20);
        cellEighteen.setCellValue(String.valueOf(new BigDecimal((float) total.getDeduction() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellNineteen = totalRow.createCell(21);
        cellNineteen.setCellValue(String.valueOf(new BigDecimal((float) total.getShouldPayAmount() / 100).setScale(0, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwenty = totalRow.createCell(22);
        cellTwenty.setCellValue(String.valueOf(total.getAvgUseFill().divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwentyOne = totalRow.createCell(23);
        cellTwentyOne.setCellValue(String.valueOf(total.getAvgCar()));
        Cell cellTwentyTwo = totalRow.createCell(24);
        cellTwentyTwo.setCellValue(String.valueOf(total.getGrossProfit().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwentyThree = totalRow.createCell(25);
        cellTwentyThree.setCellValue(total.getOilConsumption().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
        for (Cell cell : totalRow) {
            cell.setCellStyle(style);
        }
        Row rowLast = sheet.createRow(totalIndex + 1);
        //合并单元格
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 1, 0, 1);
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 1, 2, 4);

        rowLast.setHeight((short) (15.625 * 30));
        Cell lastOne = rowLast.createCell(0);
        lastOne.setCellValue("本月出勤车辆");
        lastOne.setCellStyle(style);
        Cell lastTwo = rowLast.createCell(2);
        lastTwo.setCellValue(total.getOnDutyCount().toString());
        Cell lastThree = rowLast.createCell(5);
        lastThree.setCellValue("台");
        lastThree.setCellStyle(style);

        Row rowEnd = sheet.createRow(totalIndex + 2);
        //合并单元格
        ExcelUtils.mergeRegion(sheet, totalIndex + 2, totalIndex + 2, 0, 1);
        ExcelUtils.mergeRegion(sheet, totalIndex + 2, totalIndex + 2, 2, 4);
        rowEnd.setHeight((short) (15.625 * 30));
        Cell endOne = rowEnd.createCell(0);
        endOne.setCellValue("单位成本");
        Cell endTwo = rowEnd.createCell(2);
        //todo 无法计算获取的公式
        endTwo.setCellValue(total.getUnitCost().toString());
        endTwo.setCellStyle(style);
        Cell endThree = rowEnd.createCell(5);
        endThree.setCellValue("元/m³");
        endThree.setCellStyle(style);
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 2, 6, 25);
        for (Cell cell : rowLast) {
            cell.setCellStyle(style);
        }
        for (Cell cell : rowEnd) {
            cell.setCellStyle(style);
        }
        //备注
        Cell cellRemark = rowLast.getCell(6);
        cellRemark.setCellValue("备注：");
        CellStyle rmkStyle = workbook.createCellStyle();
        rmkStyle.setAlignment(HorizontalAlignment.LEFT);
        rmkStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellRemark.setCellStyle(rmkStyle);
        //写入流
        OutputStream out = new FileOutputStream(new File(newPath));
        workbook.write(out);
        out.flush();
        out.close();
        is.close();
        return newPath;
    }

    /**
     * 拷贝报表模板 开始写入
     *
     * @param modelPath
     * @param saveName
     * @return
     */
    public String returnNewPath(String modelPath, String saveName, HttpServletRequest request) throws UnsupportedEncodingException {
        //生成下载文件的文件名
        String fileName = ExcelUtils.getSaveFileName(saveName, new Date(), request);
        //获取保存报表的路径及生成的文件名
        String newPath = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH) + File.separator + fileName + SmartminingConstant.XLSSUFFIX;
        //将模板复制到指定文件夹
        FileUtils.copyFile(modelPath, newPath);
        return newPath;
    }

    /**
     * 设置 excel 头部
     *
     * @param path
     * @param reportDate
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public Map<String, Object> returnExcelSheet(String path, Date reportDate) throws IOException, InvalidFormatException {
        Map<String, Object> result = new HashMap<>();
        //获取年份
        int year = DateUtils.getYeatByDate(reportDate);
        //获取月份
        int month = DateUtils.getMonthByDate(reportDate);
        File file = new File(path);
        InputStream is = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < colCount - 1; i++) {
            sheet.setColumnWidth(i, 256 * 12);
        }
        if (sheet != null) {
            //修改报表头部的日期
            Row rowOne = sheet.getRow(0);
            Cell cellOne = rowOne.getCell(0);
            String valueOne = cellOne.getStringCellValue();
            String newValueOne = valueOne.replace(SmartminingConstant.HEADERBYYEAR, String.valueOf(year)).replace(SmartminingConstant.HEADERBYMONTH, String.valueOf(month));
            cellOne.setCellValue(newValueOne);
            CellRangeAddress ca = sheet.getMergedRegion(0);
            int lastCol = ca.getLastColumn();
            Cell cellTwo = rowOne.getCell(lastCol + 1);
            String valueTwo = cellTwo.getStringCellValue();
            //开始的号数
            int startDay = DateUtils.getDayByDate(DateUtils.getStartDate(reportDate));
            //结束的号数
            int endDay = DateUtils.getDayByDate(DateUtils.getEndDate(reportDate));
            String newValueTow = valueTwo.replace(SmartminingConstant.HEADERBYSTARTDAY, String.valueOf(startDay)).replace(SmartminingConstant.HEADERBYENDDAY, String.valueOf(endDay));
            cellTwo.setCellValue(newValueTow);
        }
        result.put("is", is);
        result.put("workbook", workbook);
        result.put("sheet", sheet);
        return result;
    }

    public Row writeTotalCount(Sheet sheet, Integer rowIndex) {
        Row totalRow = sheet.createRow(rowIndex);
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowIndex, rowIndex, 0, 2);
        totalRow.setHeight((short) (15.625 * 30));
        //设置合计字段
        Cell cellTotalOne = totalRow.createCell(0);
        cellTotalOne.setCellValue("合   计");
        return totalRow;
    }

    /**
     * 渣车月报表
     *
     * @param request
     * @param total
     * @param reportList
     * @param reportDate
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public String createCarMonthReport(HttpServletRequest request, ProjectMonthReportTotal total, List<ProjectMonthReport> reportList, Date reportDate) throws IOException, InvalidFormatException {
        String newPath = returnNewPath(SmartminingConstant.CARMONTHREPORTMODELPATH, SmartminingConstant.FILENAMEBYCARMONTH, request);
        Map<String, Object> result = returnExcelSheet(newPath, reportDate);
        Sheet sheet = (Sheet) result.get("sheet");
        int rowNumber = sheet.getPhysicalNumberOfRows();
        Workbook workbook = (Workbook) result.get("workbook");
        InputStream is = (InputStream) result.get("is");
        //excel的样式
        CellStyle style = ExcelUtils.setCellStyleUtilsByMonthReport(workbook);
        int length = reportList.size();
        for (int i = 0; i < length; i++) {
            ProjectMonthReport report = reportList.get(i);
            Row row = sheet.createRow(rowNumber + i);
            row.setHeight((short) (15.625 * 30));
            //设置excel的序号
            Cell cell = row.createCell(0);
            cell.setCellValue(i + 1);
            //设置机器名称
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(report.getCode());
            //设置机主名称
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(report.getCarOwnerName());
            //设置总车数
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(report.getTotalCount());
            //设置总方量
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(String.valueOf(new BigDecimal((float) report.getTotalCubic() / 1000000).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置总金额
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(String.valueOf(new BigDecimal((float) report.getTotalAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置补贴
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(report.getSubsidyAmount() / 100);
            //设置总加油量
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(report.getTotalFill() / 1000);
            //设置加油总金额
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(report.getTotalAmount() / 100);
            //设置扣款
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(report.getDeduction() / 100);
            //设置应付金额
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(String.valueOf(new BigDecimal((float) report.getShouldPayAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置平均价格 含油
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(String.valueOf(new BigDecimal((float) report.getAvgAmountByFill() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置平均价格 不含油
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(String.valueOf(new BigDecimal((float) report.getAvgAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置里程数
            Cell cell13 = row.createCell(13);
            cell13.setCellValue(String.valueOf(new BigDecimal((float) report.getDistance() / 1000).setScale(1, BigDecimal.ROUND_HALF_UP)));
            //设置油耗
            Cell cell14 = row.createCell(14);
            cell14.setCellValue(report.getOilConsumption().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            for (Cell cell15 : row) {
                cell15.setCellStyle(style);
            }
        }
        /**
         * 合计的写入
         */
        //统计的行标
        int totalIndex = rowNumber + length;
        Row totalRow = writeTotalCount(sheet, totalIndex);
        Cell cellOne = totalRow.createCell(3);
        cellOne.setCellValue(total.getTotalCount());
        Cell cellTwo = totalRow.createCell(4);
        cellTwo.setCellValue(String.valueOf(new BigDecimal((float) total.getTotalCubic() / 1000000).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellThree = totalRow.createCell(5);
        cellThree.setCellValue(String.valueOf(new BigDecimal((float) total.getTotalAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellFour = totalRow.createCell(6);
        cellFour.setCellValue(total.getSubsidyAmount() / 100);
        Cell cellFive = totalRow.createCell(7);
        cellFive.setCellValue(String.valueOf(new BigDecimal((float) total.getTotalFill() / 1000).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellSix = totalRow.createCell(8);
        cellSix.setCellValue(String.valueOf(new BigDecimal((float) total.getTotalAmountByFill() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellSeven = totalRow.createCell(9);
        cellSeven.setCellValue(total.getDeduction() / 100);
        Cell cellEight = totalRow.createCell(10);
        cellEight.setCellValue(String.valueOf(new BigDecimal((float) total.getShouldPayAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellNine = totalRow.createCell(11);
        cellNine.setCellValue(String.valueOf(new BigDecimal((float) total.getAvgAmountByFill() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellTen = totalRow.createCell(12);
        cellTen.setCellValue(String.valueOf(new BigDecimal((float) total.getAvgAmount() / 100).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellEleven = totalRow.createCell(13);
        cellEleven.setCellValue(String.valueOf(new BigDecimal((float) total.getDistance() / 1000).setScale(1, BigDecimal.ROUND_HALF_UP)));
        Cell cellTwelve = totalRow.createCell(14);
        cellTwelve.setCellValue(total.getOilConsumption().multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
        for (Cell cell : totalRow) {
            cell.setCellStyle(style);
        }
        Row rowLast = sheet.createRow(totalIndex + 1);
        //合并单元格
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 1, 0, 2);
        rowLast.setHeight((short) (15.625 * 30));
        Cell lastOne = rowLast.createCell(0);
        lastOne.setCellValue("本月出勤车辆");
        lastOne.setCellStyle(style);
        Cell lastTwo = rowLast.createCell(3);
        lastTwo.setCellValue(total.getOnDutyCount().toString());
        lastTwo.setCellStyle(style);
        Cell lastThree = rowLast.createCell(4);
        lastThree.setCellValue("辆");
        lastThree.setCellStyle(style);
        Cell lastFour = rowLast.createCell(5);
        lastFour.setCellValue("平均每车方量（m³）");
        lastFour.setCellStyle(style);
        Row rowEnd = sheet.createRow(totalIndex + 2);
        //合并单元格
        ExcelUtils.mergeRegion(sheet, totalIndex + 2, totalIndex + 2, 0, 2);
        rowEnd.setHeight((short) (15.625 * 30));
        Cell endOne = rowEnd.createCell(0);
        endOne.setCellValue("单位成本");
        Cell endTwo = rowEnd.createCell(3);
        endTwo.setCellValue(String.valueOf(total.getUnitCost().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP)));
        Cell endThree = rowEnd.createCell(4);
        endThree.setCellValue("元/m³");
        Cell endFour = rowEnd.createCell(5);
        endFour.setCellValue(String.valueOf(total.getAvgCubics()));
        rowEnd.createCell(6);
        //备注
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 2, 7, 7);
        Cell cellRemark = rowLast.getCell(7);
        cellRemark.setCellValue("备注");
        for (Cell cell : rowLast) {
            cell.setCellStyle(style);
        }
        for (Cell cell : rowEnd) {
            cell.setCellStyle(style);
        }
        ExcelUtils.mergeRegion(sheet, totalIndex + 1, totalIndex + 2, 8, 14);
        //写入流
        OutputStream out = new FileOutputStream(new File(newPath));
        workbook.write(out);
        out.flush();
        out.close();
        is.close();
        return newPath;
    }

    /**
     * 完善渣车日报表下载
     * @param request
     * @param body
     * @param cars
     * @param distances
     * @param reportDate
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public String createCarDayReport(HttpServletRequest request, ProjectDayReport body, List<ProjectDayReportPartCar> cars, List<ProjectDayReportPartDistance> distances, Date reportDate) throws IOException, InvalidFormatException {
        Long projectId = body.getProjectId();
        Project project = projectServiceI.get(projectId);
        String path = FileUtils.createFile(SmartminingConstant.EXCELSAVEPATH);
        String fileNameDate = DateUtils.formatDateByPattern(reportDate, SmartminingConstant.YEARMONTHDAUFORMAT);
        File file = new File(path + File.separator + fileNameDate + SmartminingConstant.CARDAYREPORTNAME);
        if (!file.exists())
            file.createNewFile();
        String filePath = file.getPath();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //获取月份
        int month = DateUtils.getMonthByDate(reportDate);
        //获取天数
        int day = DateUtils.getDayByDate(reportDate);
        //设置报表头部信息
        Row rowFirst = sheet.createRow(0);
        rowFirst.setHeight((short) (15.625 * 50));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, 0, 0, 0, 41);
        String fisrtTitleName = project.getName() + "渣车运输日报" + "          " + month + "月" + day + "日";
        //设置写入单元格的样式
        CellStyle firstTitleStyle = workbook.createCellStyle();
        firstTitleStyle.setWrapText(true);         //自动换行
        firstTitleStyle.setAlignment(HorizontalAlignment.CENTER);      //水平居中
        firstTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);        //垂直居中
        //设置写入字体样式
        Font font = workbook.createFont();
        //加粗
        font.setBold(true);
        //字体风格
        font.setFontName("宋体");
        //字体大小
        font.setFontHeightInPoints((short)20);
        firstTitleStyle.setFont(font);
        //标题的单元格
        Cell firstTitleCell = rowFirst.createCell(0);
        firstTitleCell.setCellStyle(firstTitleStyle);
        firstTitleCell.setCellValue(fisrtTitleName);

        //第二行头部
        Row secondRow = sheet.createRow(1);
        secondRow.setHeight((short) (15.625 * 30));
        //第三行 合并单元格使用
        Row thirdRow = sheet.createRow(2);
        thirdRow.setHeight((short) (15.625 * 30));

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), thirdRow.getRowNum(), 0, 0);
        //序号
        Cell oneTitle = secondRow.createCell(0);
        oneTitle.setCellValue("序号");

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), thirdRow.getRowNum(), 1, 1);
        //车辆编号
        Cell twoTitle = secondRow.createCell(1);
        twoTitle.setCellValue("车辆编号");

        //运距
        Cell threeTitle = secondRow.createCell(2);
        threeTitle.setCellValue("运");
        Cell threeTitleByTwo = thirdRow.createCell(2);
        threeTitleByTwo.setCellValue("距");

        int distanceIndex = secondRow.getLastCellNum();
        //运距详情
        for(int i = 0; i < distances.size(); i++){
            ProjectDayReportPartDistance distance = distances.get(i);
            //合并单元格
            ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), thirdRow.getRowNum(), distanceIndex, distanceIndex);
            Cell distanceCell = secondRow.createCell(distanceIndex);
            distanceCell.setCellValue(distance.getDistance() / 100L);
            distanceIndex ++;
        }

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), thirdRow.getRowNum(), distanceIndex, distanceIndex);
        //小计
        Cell fourTitle = secondRow.createCell(distanceIndex);
        fourTitle.setCellValue("小计（车）");
        distanceIndex = distanceIndex + 1;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //车数
        Cell fiveTitle = secondRow.createCell(distanceIndex);
        fiveTitle.setCellValue("车  数");
        //车数合计
        Cell fourTitleByTwo = thirdRow.createCell(distanceIndex);
        fourTitleByTwo.setCellValue("合计");
        //车数累计
        Cell fiveTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        fiveTitleByTwo.setCellValue("累计");
        distanceIndex = distanceIndex + 1 + 1;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), thirdRow.getRowNum(), distanceIndex, distanceIndex);
        //立方/车
        Cell sixTitle = secondRow.createCell(distanceIndex);
        sixTitle.setCellValue("立方/车");
        distanceIndex = distanceIndex + 1;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //方量
        Cell sevenTitle = secondRow.createCell(distanceIndex);
        sevenTitle.setCellValue("方量（m³）");
        //方量合计
        Cell sixTitleByTwo = thirdRow.createCell(distanceIndex);
        sixTitleByTwo.setCellValue("当日");
        //方量累计
        Cell sevenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        sevenTitleByTwo.setCellValue("累计");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //加油
        Cell eightTitle = secondRow.createCell(distanceIndex);
        eightTitle.setCellValue("加油（升）");
        //加油合计
        Cell eightTitleByTwo = thirdRow.createCell(distanceIndex);
        eightTitleByTwo.setCellValue("当日");
        //加油累计
        Cell nineTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        nineTitleByTwo.setCellValue("累计");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //车/升
        Cell nineTitle = secondRow.createCell(distanceIndex);
        nineTitle.setCellValue("车/升");
        //加油合计
        Cell tenTitleByTwo = thirdRow.createCell(distanceIndex);
        tenTitleByTwo.setCellValue("当日");
        //加油累计
        Cell elevenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        elevenTitleByTwo.setCellValue("累计");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //渣车总金额
        Cell tenTitle = secondRow.createCell(distanceIndex);
        tenTitle.setCellValue("渣车总金额（元）");
        //小计
        Cell twelveTitleByTwo = thirdRow.createCell(distanceIndex);
        twelveTitleByTwo.setCellValue("小计（元）");
        //累计
        Cell thirteenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        thirteenTitleByTwo.setCellValue("累计（元）");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //渣车用油金额
        Cell elevenTitle = secondRow.createCell(distanceIndex);
        elevenTitle.setCellValue("渣车用油金额（元）");
        //小计
        Cell fourteenTitleByTwo = thirdRow.createCell(distanceIndex);
        fourteenTitleByTwo.setCellValue("小计（元）");
        //累计
        Cell fifteenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        fifteenTitleByTwo.setCellValue("累计（元）");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //应付金额
        Cell twelveTitle = secondRow.createCell(distanceIndex);
        twelveTitle.setCellValue("应付金额（元）");
        //小计
        Cell sixteenTitleByTwo = thirdRow.createCell(distanceIndex);
        sixteenTitleByTwo.setCellValue("小计（元）");
        //累计
        Cell seventeenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        seventeenTitleByTwo.setCellValue("累计（元）");
        distanceIndex = distanceIndex + 2;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 2);
        //应付金额
        Cell thirteenTitle = secondRow.createCell(distanceIndex);
        thirteenTitle.setCellValue("里程（公里）");
        //当日
        Cell eighteenTitleByTwo = thirdRow.createCell(distanceIndex);
        eighteenTitleByTwo.setCellValue("当日");
        //当月
        Cell nineteenTitleByTwo = thirdRow.createCell(distanceIndex + 1);
        nineteenTitleByTwo.setCellValue("当月");
        //平均
        Cell twentyTitleByTwo = thirdRow.createCell(distanceIndex + 2);
        twentyTitleByTwo.setCellValue("平均");
        distanceIndex = distanceIndex + 3;

        //合并单元格
        ExcelUtils.mergeRegion(sheet, secondRow.getRowNum(), secondRow.getRowNum(), distanceIndex, distanceIndex + 1);
        //油耗%
        Cell fourteentTitle = secondRow.createCell(distanceIndex);
        fourteentTitle.setCellValue("油耗%");
        //当日
        Cell twentyOneTitle = thirdRow.createCell(distanceIndex);
        twentyOneTitle.setCellValue("当日");
        //当月
        Cell twentyTwoTitle = thirdRow.createCell(distanceIndex + 1);
        twentyTwoTitle.setCellValue("当月");

        //开始写入的行数
        int beginRow = thirdRow.getRowNum() + 1;
        //写入excel主体数据
        for(int i = 0; i < cars.size(); i++){
            ProjectDayReportPartCar car = cars.get(i);
            Row oneRow = sheet.createRow(beginRow);
            oneRow.setHeight((short) (15.625 * 30));
            Row twoRow = sheet.createRow(beginRow + 1);
            twoRow.setHeight((short) (15.625 * 30));
            //合并单元格
            ExcelUtils.mergeRegion(sheet, oneRow.getRowNum(), twoRow.getRowNum(), 0, 0);
            //序号
            Cell one = oneRow.createCell(0);
            one.setCellValue(i + 1);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, oneRow.getRowNum(), twoRow.getRowNum(), 1, 1);
            //车辆编号
            Cell two = oneRow.createCell(1);
            two.setCellValue(car.getCarCode() + car.getCarOwnerName());

            Cell three = oneRow.createCell(2);
            three.setCellValue("日");
            Cell twoOne = twoRow.createCell(2);
            twoOne.setCellValue("夜");

            int colIndex = oneRow.getLastCellNum();
            JSONArray dayArray = JSONArray.parseArray(car.getEarlyCountList());
            JSONArray nightArray = JSONArray.parseArray(car.getNightCountList());
            for(int j = 0; j < dayArray.size(); j++){
                Cell dayCell = oneRow.createCell(colIndex);
                if(dayArray.get(j) != null && Long.parseLong(dayArray.get(j).toString()) != 0)
                    dayCell.setCellValue(dayArray.get(j).toString());
                Cell nightCell = twoRow.createCell(colIndex);
                if(nightArray.get(j) != null && Long.parseLong(nightArray.get(j).toString()) != 0)
                    nightCell.setCellValue(nightArray.get(j).toString());
                colIndex++;
            }
            //早班车数
            Cell four = oneRow.createCell(colIndex);
            four.setCellValue(car.getEarlyTotalCount());
            //晚班车数
            Cell twoTwo = twoRow.createCell(colIndex);
            twoTwo.setCellValue(car.getNightTotalCount());
            colIndex = colIndex + 1;

            //当天车数
            Cell five = oneRow.createCell(colIndex);
            five.setCellValue(car.getTotalCount());
            colIndex = colIndex + 1;

            //累计车数
            Cell six = oneRow.createCell(colIndex);
            six.setCellValue(car.getGrandTotalCount());
            colIndex = colIndex + 1;

            //每车立方数
            Cell seven = oneRow.createCell(colIndex);
            seven.setCellValue(new BigDecimal((float)car.getCubicPerTimes() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //当日方量
            Cell eight = oneRow.createCell(colIndex);
            eight.setCellValue(new BigDecimal((float)car.getTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //累计方量
            Cell nine = oneRow.createCell(colIndex);
            nine.setCellValue(new BigDecimal((float)car.getGrandTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //当天加油量
            Cell ten = oneRow.createCell(colIndex);
            ten.setCellValue(new BigDecimal((float)car.getTotalFill() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //累计加油量
            Cell eleven = oneRow.createCell(colIndex);
            eleven.setCellValue(new BigDecimal((float)car.getGrandTotalFill() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //当天平均用油量
            Cell twelve = oneRow.createCell(colIndex);
            twelve.setCellValue(new BigDecimal((float)car.getAvgUsing() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //累计平均用油量
            Cell thirteen = oneRow.createCell(colIndex);
            thirteen.setCellValue(new BigDecimal((float)car.getGrandTotalAvgUsing() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //渣车总金额 小计
            Cell fourteen = oneRow.createCell(colIndex);
            fourteen.setCellValue(new BigDecimal((float)car.getTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //渣车总金额 累计
            Cell fifteen = oneRow.createCell(colIndex);
            fifteen.setCellValue(new BigDecimal((float)car.getGrandTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //渣车用油金额 小计
            Cell sixteen = oneRow.createCell(colIndex);
            sixteen.setCellValue(new BigDecimal((float)car.getTotalAmountFill() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //渣车用油金额 累计
            Cell seventeen = oneRow.createCell(colIndex);
            seventeen.setCellValue(new BigDecimal((float)car.getGrandTotalAmountFill() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //应付总金额 小计
            Cell eighteen = oneRow.createCell(colIndex);
            eighteen.setCellValue(new BigDecimal((float)car.getPayable() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //应付总金额 累计
            Cell nineteen = oneRow.createCell(colIndex);
            nineteen.setCellValue(new BigDecimal((float)car.getGrandTotalPayable() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //里程 当日
            Cell twenty = oneRow.createCell(colIndex);
            twenty.setCellValue(new BigDecimal((float)car.getMileage() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //里程 累计
            Cell twentyOne = oneRow.createCell(colIndex);
            twentyOne.setCellValue(new BigDecimal((float)car.getGrandTotalMileage() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //里程 平均
            Cell twentyTwo = oneRow.createCell(colIndex);
            twentyTwo.setCellValue(new BigDecimal((float)car.getGrandTotalAvgMileage() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            colIndex = colIndex + 1;

            //油耗 当日
            Cell twentyThree = oneRow.createCell(colIndex);
            twentyThree.setCellValue(new BigDecimal((float)car.getPercentOfUsing() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            colIndex = colIndex + 1;

            //油耗 累计
            Cell twentyFour = oneRow.createCell(colIndex);
            twentyFour.setCellValue(new BigDecimal((float)car.getPercentOfMonthUsing() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP) + "%");
            beginRow = beginRow + 2;
        }
        //行下标
        int rowIndex = sheet.getLastRowNum() + 1;
        Row rowOne = sheet.createRow(rowIndex);
        rowOne.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowOne.getRowNum(), rowOne.getRowNum(), 0, 2);
        //当日车数
        Cell cellOne = rowOne.createCell(0);
        cellOne.setCellValue("当日车数");
        //当前行的下标
        int carByDayIndex = rowIndex;
        rowIndex = rowIndex + 1;

        Row rowTwo = sheet.createRow(rowIndex);
        rowTwo.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowTwo.getRowNum(), rowTwo.getRowNum(), 0, 2);
        Cell cellTwo = rowTwo.createCell(0);
        cellTwo.setCellValue("当日方量");
        //当前行的下标
        int cubicByDayIndex = rowIndex;
        rowIndex = rowIndex + 1;

        Row rowThree = sheet.createRow(rowIndex);
        rowThree.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowThree.getRowNum(), rowThree.getRowNum(), 0, 2);
        Cell cellThree = rowThree.createCell(0);
        cellThree.setCellValue("当日金额小计");
        //当前行的下标
        int amountByDayIndex = rowIndex;
        rowIndex = rowIndex + 1;

        Row rowFour = sheet.createRow(rowIndex);
        rowFour.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowFour.getRowNum(), rowFour.getRowNum(), 0, 2);
        Cell cellFour = rowFour.createCell(0);
        cellFour.setCellValue("当月累计车数");
        //当前行的下标
        int carByhistoryIndex = rowIndex;
        rowIndex = rowIndex + 1;

        Row rowFive = sheet.createRow(rowIndex);
        rowFive.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowFive.getRowNum(), rowFive.getRowNum(), 0, 2);
        Cell cellFive = rowFive.createCell(0);
        cellFive.setCellValue("当月累计方量");
        //当前行的下标
        int cubicByhistoryIndex = rowIndex;
        rowIndex = rowIndex + 1;

        Row rowSix = sheet.createRow(rowIndex);
        rowSix.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, rowSix.getRowNum(), rowSix.getRowNum(), 0, 2);
        Cell cellSix = rowSix.createCell(0);
        cellSix.setCellValue("当月累计金额");
        //当前行的下标
        int amountByhistoryIndex = rowIndex;
        rowIndex = rowIndex + 1;
        for(int i = rowOne.getRowNum(); i < rowSix.getRowNum() + 1; i++){
            Row row = sheet.getRow(i);
            row.setHeight((short) (15.625 * 30));
            for(int j = 0; j < distances.size(); j++){
                ProjectDayReportPartDistance distance = distances.get(j);
                Cell cell = row.createCell(j + 3);
                if(i == carByDayIndex){
                    //当日车数
                    if(distance.getTotalCount() != 0)
                        cell.setCellValue(distance.getTotalCount());
                }else if(i == cubicByDayIndex){
                    //当日方量
                    if(distance.getTotalCubic() != 0)
                        cell.setCellValue(new BigDecimal((float)distance.getTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }else if(i == amountByDayIndex){
                    //当日金额小计
                    if(distance.getTotalAmount() != 0)
                        cell.setCellValue(new BigDecimal((float)distance.getTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }else if(i == carByhistoryIndex){
                    //当月累计车数
                    if(distance.getGrandTotalCount() != 0)
                        cell.setCellValue(distance.getGrandTotalCount());
                }else if(i == cubicByhistoryIndex){
                    //当月累计方量
                    if(distance.getGrandTotalCubic() != 0)
                        cell.setCellValue(new BigDecimal((float)distance.getGrandTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }else if(i == amountByhistoryIndex){
                    //当月累计金额
                    if(distance.getGrandTotalAmount() != 0)
                        cell.setCellValue(new BigDecimal((float)distance.getGrandTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        }
        //最后一列的下标
        int colIndex = rowOne.getLastCellNum();
        //合计
        Cell one = rowOne.createCell(colIndex);
        one.setCellValue(body.getTotalCount());
        colIndex = colIndex + 1;

        Cell two = rowOne.createCell(colIndex);
        two.setCellValue(body.getTotalCount());
        colIndex = colIndex + 1;

        Cell three = rowOne.createCell(colIndex);
        three.setCellValue(body.getGrandTotalCount());
        colIndex = colIndex + 1;

        Cell four = rowOne.createCell(colIndex);
        four.setCellValue(new BigDecimal((float)body.getCubicPerTimes() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell five = rowOne.createCell(colIndex);
        five.setCellValue(new BigDecimal((float)body.getTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell six = rowOne.createCell(colIndex);
        six.setCellValue(new BigDecimal((float)body.getGrandTotalCubic() / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell seven = rowOne.createCell(colIndex);
        seven.setCellValue(new BigDecimal((float)body.getTotalFill() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell eight = rowOne.createCell(colIndex);
        eight.setCellValue(new BigDecimal((float)body.getGrandTotalFill() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell nine = rowOne.createCell(colIndex);
        nine.setCellValue(new BigDecimal((float)body.getAvgUsing() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell ten = rowOne.createCell(colIndex);
        ten.setCellValue(new BigDecimal((float)body.getGrandTotalAvgUsing() / 1000L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell eleven = rowOne.createCell(colIndex);
        eleven.setCellValue(new BigDecimal((float)body.getTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell twelve = rowOne.createCell(colIndex);
        twelve.setCellValue(new BigDecimal((float)body.getGrandTotalAmount() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell thirteen = rowOne.createCell(colIndex);
        thirteen.setCellValue(new BigDecimal((float)body.getTotalAmountFill() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell fourteen = rowOne.createCell(colIndex);
        fourteen.setCellValue(new BigDecimal((float)body.getGrandTotalAmountFill() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell fifteent = rowOne.createCell(colIndex);
        fifteent.setCellValue(new BigDecimal((float)body.getPayable() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell sixteen = rowOne.createCell(colIndex);
        sixteen.setCellValue(new BigDecimal((float)body.getGrandTotalPayable() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell seventeen = rowOne.createCell(colIndex);
        seventeen.setCellValue(new BigDecimal((float)body.getMileage() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell eighteen = rowOne.createCell(colIndex);
        eighteen.setCellValue(new BigDecimal((float)body.getGrandTotalMileage() / 100L).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell nineteen = rowOne.createCell(colIndex);
        nineteen.setCellValue(new BigDecimal((float)body.getGrandTotalAvgMileage() / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell twenty = rowOne.createCell(colIndex);
        twenty.setCellValue(new BigDecimal((float)body.getPercentOfUsing() / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        colIndex = colIndex + 1;

        Cell twentyOne = rowOne.createCell(colIndex);
        twentyOne.setCellValue(new BigDecimal((float)body.getPercentOfMonthUsing() / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        rowIndex = rowIndex + 1;
        //其他信息 第一行
        Row carRow = sheet.createRow(rowIndex);
        carRow.setHeight((short) (15.625 * 30));
        Row carRowShow = sheet.createRow(rowIndex + 1);
        carRowShow.setHeight((short) (15.625 * 30));
        //白班装载车数
        Cell carCellByDay = carRow.createCell(0);
        carCellByDay.setCellValue("白班装载车数");
        Cell carCellByDayShow = carRowShow.createCell(0);
        carCellByDayShow.setCellValue(body.getEarlyTotalCount());

        //晚班装载车数
        Cell carCellByNight = carRow.createCell(2);
        carCellByNight.setCellValue("夜班装载车数");
        Cell carCellByNightShow = carRowShow.createCell(2);
        carCellByNightShow.setCellValue(body.getNightTotalCount());

        //平均每部车每天车数
        Cell carPercent = carRow.createCell(4);
        carPercent.setCellValue("每天平均（趟/天）");
        Cell carPercentShow = carRowShow.createCell(4);
        carPercentShow.setCellValue(body.getAvgCountsPerCarPerDay());

        //项总总车辆数
        Cell totalCars = carRow.createCell(6);
        totalCars.setCellValue("项目注册总车数");
        Cell totalCarsShow = carRowShow.createCell(6);
        totalCarsShow.setCellValue(body.getProjectTotalCar());

        //当日总出勤数（辆）
        Cell onDutyCount = carRow.createCell(8);
        onDutyCount.setCellValue("出勤总数（辆）");
        Cell onDutyCountShow = carRowShow.createCell(8);
        onDutyCountShow.setCellValue(body.getOnDutyCount());
        rowIndex = rowIndex + 3;

        //其他信息 第二行
        Row onDutyRow = sheet.createRow(rowIndex);
        onDutyRow.setHeight((short) (15.625 * 30));
        Row onDutyRowShow = sheet.createRow(rowIndex + 1);
        onDutyRowShow.setHeight((short) (15.625 * 30));
        //早班出勤数
        Cell carOnDutyByDay = onDutyRow.createCell(0);
        carOnDutyByDay.setCellValue("早班出勤数（辆）");
        Cell carOnDutyByDayShow = onDutyRowShow.createCell(0);
        carOnDutyByDayShow.setCellValue(body.getEarlyOnDutyCount());

        //白班出勤率
        Cell carOnDutyByDayPercent = onDutyRow.createCell(2);
        carOnDutyByDayPercent.setCellValue("早班出勤率");
        Cell carOnDutyByDayPercentShow = onDutyRowShow.createCell(2);
        carOnDutyByDayPercentShow.setCellValue(body.getEarlyAttendance() + "%");

        //晚班出勤数
        Cell carOnDutyByNight = onDutyRow.createCell(4);
        carOnDutyByNight.setCellValue("晚班出勤数（辆）");
        Cell carOnDutyByNightShow = onDutyRowShow.createCell(4);
        carOnDutyByNightShow.setCellValue(body.getNightOnDutyCount());

        //晚班出勤率
        Cell carOnDutyByNightPercent = onDutyRow.createCell(6);
        carOnDutyByNightPercent.setCellValue("晚班出勤率");
        Cell carOnDutyByNightPercentShow = onDutyRowShow.createCell(6);
        carOnDutyByNightPercentShow.setCellValue(body.getNightAttendance() + "%");
        rowIndex = rowIndex + 3;

        //其他信息 第三行
        Row lastRow = sheet.createRow(rowIndex);
        lastRow.setHeight((short) (15.625 * 30));
        Row lastRowShow = sheet.createRow(rowIndex + 1);
        lastRowShow.setHeight((short) (15.625 * 30));

        //当日出煤车数
        Cell coalCount = lastRow.createCell(0);
        coalCount.setCellValue("当日煤车数（辆）");
        Cell coalCountShow = lastRowShow.createCell(0);
        coalCountShow.setCellValue(body.getCoalCount());

        //累计出煤车数
        Cell grandTotalCoalCount = lastRow.createCell(2);
        grandTotalCoalCount.setCellValue("累计煤车数（辆）");
        Cell grandTotalCoalCountShow = lastRowShow.createCell(2);
        grandTotalCoalCountShow.setCellValue(body.getGrandTotalCoalCount());

        //历史出煤车数
        Cell historyCoalCount = lastRow.createCell(4);
        historyCoalCount.setCellValue("历史煤车数（辆）");
        Cell historyCoalCountShow = lastRowShow.createCell(4);
        historyCoalCountShow.setCellValue(body.getHistoryCoalCount());

        //当日毛利
        Cell grossProfit = lastRow.createCell(6);
        grossProfit.setCellValue("当日毛利（元/车）");
        Cell grossProfitShow = lastRowShow.createCell(6);
        grossProfitShow.setCellValue(new BigDecimal((float)body.getGrossProfit() / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        //本月毛利
        Cell monthGrossProfit = lastRow.createCell(8);
        monthGrossProfit.setCellValue("本月毛利（元/车）");
        Cell monthGrossProfitShow = lastRowShow.createCell(8);
        monthGrossProfitShow.setCellValue(new BigDecimal((float)body.getMonthGrossProfit() / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
        return filePath;
    }

    //挖机日报表下载
    public String createMachineDayReport(HttpServletRequest request, ProjectDiggingDayReportTotal total, List<ProjectDiggingDayReport> projectDiggingDayReports, Date reportDate) throws IOException, InvalidFormatException {
        Long projectId = total.getProjectId();
        Project project = projectServiceI.get(projectId);
        String newPath = returnNewPath(SmartminingConstant.DAYREPORTMODELPATHBYMACHINE, SmartminingConstant.FILENAMEBYDIGGINGDAY, request);
        File file = new File(newPath);
        InputStream is = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < colCount - 1; i++) {
            sheet.setColumnWidth(i, 256 * 12);
        }
        //获取月份
        int month = DateUtils.getMonthByDate(reportDate);
        //获取天数
        int day = DateUtils.getDayByDate(reportDate);
        //修改报表头部的日期
        Row rowOne = sheet.getRow(0);
        Cell cellOne = rowOne.getCell(0);
        String valueOne = cellOne.getStringCellValue();
        String newValue = valueOne.replace(SmartminingConstant.PROJECTNAME, project.getName()).replace(SmartminingConstant.HEADERBYMONTH, String.valueOf(month)).replace(SmartminingConstant.HEADERBYDAY, String.valueOf(day));
        cellOne.setCellValue(newValue);
        Cell cellTwo = rowOne.getCell(25);
        String valueTwo = cellTwo.getStringCellValue();
        String twoNewValue = valueTwo.replace(SmartminingConstant.PROJECTNAME, project.getName()).replace(SmartminingConstant.HEADERBYDAY, String.valueOf(day));
        cellTwo.setCellValue(twoNewValue);
        //获取要开始写入的行的下标
        int index = sheet.getPhysicalNumberOfRows();
        int length = projectDiggingDayReports.size();
        for(int i = 0; i < length; i++){
            ProjectDiggingDayReport report = projectDiggingDayReports.get(i);
            Row oneRow = sheet.createRow(index);
            oneRow.setHeight((short) (15.625 * 30));
            Row twoRow = sheet.createRow(index + 1);
            twoRow.setHeight((short) (15.625 * 30));
            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 0, 0);
            //序号
            Cell one = oneRow.createCell(0);
            one.setCellValue(i + 1);

            //第一行 挖机名称
            Cell two = oneRow.createCell(1);
            two.setCellValue(report.getMachineName());
            //第二行 挖机车主名称
            Cell twoOne = twoRow.createCell(1);
            twoOne.setCellValue(report.getOwnerName());

            //第一行 白班
            Cell three = oneRow.createCell(2);
            three.setCellValue("日");
            //第二行 夜班
            Cell twoTwo = twoRow.createCell(2);
            twoTwo.setCellValue("夜");

            //第一行 小计
            Cell four = oneRow.createCell(3);
            four.setCellValue(report.getSubtotalTimerByDay().toString());
            //第二行 小计
            Cell twoThree = twoRow.createCell(3);
            twoThree.setCellValue(report.getSubtotalTimerByNight().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 4, 4);
            //计时工时
            Cell five = oneRow.createCell(4);
            five.setCellValue(report.getTotalTimeByTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 5, 5);
            //计时单价
            Cell six = oneRow.createCell(5);
            six.setCellValue(report.getPriceByTimer() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 6, 6);
            //计时金额
            Cell seven = oneRow.createCell(6);
            seven.setCellValue(report.getAmountByTimer() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 7, 7);
            //计时车数
            Cell eight = oneRow.createCell(7);
            eight.setCellValue(report.getTotalCountByTimer());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 8, 8);
            //计时方量
            Cell nine = oneRow.createCell(8);
            nine.setCellValue(report.getCubicCountByTimer() / 1000000L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 9, 9);
            //计时平均车数
            Cell ten = oneRow.createCell(9);
            ten.setCellValue(report.getAvgCarByTimer().toString());

            //第一行 包方小计
            Cell eleven = oneRow.createCell(10);
            eleven.setCellValue(report.getSubtotalCubicByDay().toString());
            //第二行 包方小计
            Cell twoFour = twoRow.createCell(10);
            twoFour.setCellValue(report.getSubtotalCubicByNight().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 11, 11);
            //包方总工时
            Cell twelve = oneRow.createCell(11);
            twelve.setCellValue(report.getTotalTimeByCubic().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 12, 12);
            //包方总车数
            Cell thirteen = oneRow.createCell(12);
            thirteen.setCellValue(report.getCarTotalCountByCubic());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 13, 13);
            //包方总方量
            Cell fourteen = oneRow.createCell(13);
            fourteen.setCellValue(report.getTotalCountByCubic());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 14, 14);
            //包方总金额
            Cell fifteen = oneRow.createCell(14);
            fifteen.setCellValue(report.getTotalAmountByCubic() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 15, 15);
            //总工时
            Cell sixteen = oneRow.createCell(15);
            sixteen.setCellValue(report.getTotalWorkTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 16, 16);
            //总金额
            Cell seventeen = oneRow.createCell(16);
            seventeen.setCellValue(report.getTotalAmount() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 17, 17);
            //总加油
            Cell eighteen = oneRow.createCell(17);
            eighteen.setCellValue(report.getTotalGrandFill() / 1000L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 18, 18);
            //总加油金额
            Cell nineteen = oneRow.createCell(18);
            nineteen.setCellValue(report.getTotalAmountByFill() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 19, 19);
            //结余金额
            Cell twenty = oneRow.createCell(19);
            twenty.setCellValue(report.getShouldPayAmount() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 20, 20);
            //平均耗油量
            Cell twentyOne = oneRow.createCell(20);
            twentyOne.setCellValue(report.getAvgUseFill().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 21, 21);
            //平均车辆
            Cell twentyTwo = oneRow.createCell(21);
            twentyTwo.setCellValue(report.getAvgCar().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 22, 22);
            //平均方数
            Cell twentyThree = oneRow.createCell(22);
            twentyThree.setCellValue(report.getAvgCubics().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 23, 23);
            //平均价格
            Cell twentyFour = oneRow.createCell(23);
            twentyFour.setCellValue(report.getAvgAmount().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 24, 24);
            //油耗
            Cell twentyFive = oneRow.createCell(24);
            twentyFive.setCellValue(report.getOilConsumption().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 25, 25);
            //累计计时台时
            Cell twentySix = oneRow.createCell(25);
            twentySix.setCellValue(report.getGrandTimeByTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 26, 26);
            //计时单价
            Cell twentySeven = oneRow.createCell(26);
            twentySeven.setCellValue(report.getPriceByTimer() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 27, 27);
            //累计计时金额
            Cell twentyEight = oneRow.createCell(27);
            twentyEight.setCellValue(report.getGrandAmountByTimer() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 28, 28);
            //累计计时车数
            Cell twentyNine = oneRow.createCell(28);
            twentyNine.setCellValue(report.getGrandTotalCountByTimer());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 29, 29);
            //计时方量
            Cell thirty = oneRow.createCell(29);
            thirty.setCellValue(report.getGrandCubicCountByTimer() / 1000000L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 30, 30);
            //累计计时平均车数
            Cell thirtyOne = oneRow.createCell(30);
            thirtyOne.setCellValue(report.getGrandAvgCarByTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 31, 31);
            //包方累计台时
            Cell thirtyTwo = oneRow.createCell(31);
            thirtyTwo.setCellValue(report.getGrandTimeByCubic().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 32, 32);
            //累计包方总车数
            Cell thirtyThree = oneRow.createCell(32);
            thirtyThree.setCellValue(report.getCountCarsByCubic());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 33, 33);
            //累计总方量
            Cell thirtyFour = oneRow.createCell(33);
            thirtyFour.setCellValue(report.getCountCubic() / 1000000L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 34, 34);
            //累计包方总金额
            Cell thirtyFive = oneRow.createCell(34);
            thirtyFive.setCellValue(report.getCountAmountByCubic() / 100);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 35, 35);
            //累计总台时
            Cell thirtySix = oneRow.createCell(35);
            thirtySix.setCellValue(report.getCountTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 36, 36);
            //累计包方台时
            Cell thirtySeven = oneRow.createCell(36);
            thirtySeven.setCellValue(report.getGrandTimeByCubic().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 37, 37);
            //累计计时台时
            Cell thirtyEight = oneRow.createCell(37);
            thirtyEight.setCellValue(report.getGrandTimeByTimer().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 38, 38);
            //累计包方总金额
            Cell thirtyNine = oneRow.createCell(38);
            thirtyNine.setCellValue(report.getCountAmountByCubic() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 39, 39);
            //累计计时总金额
            Cell forty = oneRow.createCell(39);
            forty.setCellValue(report.getGrandAmountByTimer() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 40, 40);
            //累计总金额
            Cell fortyOne = oneRow.createCell(40);
            fortyOne.setCellValue(report.getGrandWorkAmount() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 41, 41);
            //累计加油量
            Cell fortyTwo = oneRow.createCell(41);
            fortyTwo.setCellValue(report.getGrandTotalGrandFill() / 1000L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 42, 42);
            //累计加油金额
            Cell fortyThree = oneRow.createCell(42);
            fortyThree.setCellValue(report.getGrandTotalAmountByFill() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 43, 43);
            //累计结余金额
            Cell fortyFour = oneRow.createCell(43);
            fortyFour.setCellValue(report.getGrandShouldPayAmount() / 100L);

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 44, 44);
            //累计平均耗油量
            Cell fortyFive = oneRow.createCell(44);
            fortyFive.setCellValue(report.getGrandAvgUseFill().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 45, 45);
            //累计平均车辆
            Cell fortySix = oneRow.createCell(45);
            fortySix.setCellValue(report.getGrandAvgCar().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 46, 46);
            //累计平均方数
            Cell fortySeven = oneRow.createCell(46);
            fortySeven.setCellValue(report.getGrandAvgCubics().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 47, 47);
            //累计平均价格
            Cell fortyEight = oneRow.createCell(47);
            fortyEight.setCellValue(report.getGrandAvgAmount().toString());

            //合并单元格
            ExcelUtils.mergeRegion(sheet, index, index + 1, 48, 48);
            //累计油耗
            Cell fortyNine = oneRow.createCell(48);
            fortyNine.setCellValue(report.getGrandOilConsumption().toString());
            index = index + 2;
        }
        //合计
        Row totalRow = sheet.createRow(index);
        totalRow.setHeight((short) (15.625 * 30));
        //合并单元格
        ExcelUtils.mergeRegion(sheet, index, index, 0, 3);
        Cell one = totalRow.createCell(0);
        one.setCellValue("合计：");
        //计时工时
        Cell two = totalRow.createCell(4);
        two.setCellValue(total.getTotalTimeByTimer().toString());

        //计时单价
        Cell three = totalRow.createCell(5);
        three.setCellValue("");

        //计时金额
        Cell four = totalRow.createCell(6);
        four.setCellValue(total.getAmountByTimer() / 100L);

        //计时车数
        Cell five = totalRow.createCell(7);
        five.setCellValue(total.getTotalCountByTimer());

        //计时方量
        Cell six = totalRow.createCell(8);
        six.setCellValue(total.getCubicCountByTimer() / 1000000L);

        //计时平均车数
        Cell seven = totalRow.createCell(9);
        seven.setCellValue(total.getAvgCarByTimer().toString());

        //包方小计
        Cell eight = totalRow.createCell(10);
        eight.setCellValue(total.getSubtotalByCubic().toString());

        //包方工时
        Cell nine = totalRow.createCell(11);
        nine.setCellValue(total.getTotalTimeByCubic().toString());

        //包方总车数
        Cell ten = totalRow.createCell(12);
        ten.setCellValue(total.getCarTotalCountByCubic());

        //包方总方量
        Cell eleven = totalRow.createCell(13);
        eleven.setCellValue(total.getTotalCountByCubic() / 1000000L);

        //包方总金额
        Cell twelve = totalRow.createCell(14);
        twelve.setCellValue(total.getTotalAmountByCubic() / 100L);

        //总工时
        Cell thirteen = totalRow.createCell(15);
        thirteen.setCellValue(total.getTotalWorkTimer().toString());

        //总金额
        Cell fourteen = totalRow.createCell(16);
        fourteen.setCellValue(total.getTotalAmount() / 100L);

        //总加油量
        Cell fifteen = totalRow.createCell(17);
        fifteen.setCellValue(total.getTotalGrandFill() / 1000L);

        //总加油金额
        Cell sixteen = totalRow.createCell(18);
        sixteen.setCellValue(total.getTotalAmountByFill() / 100L);

        //结余金额
        Cell seventeen = totalRow.createCell(19);
        seventeen.setCellValue(total.getShouldPayAmount() / 100L);

        //平均耗油量
        Cell eighteen = totalRow.createCell(20);
        eighteen.setCellValue(total.getAvgUseFill().toString());

        //平均车辆
        Cell ninteent = totalRow.createCell(21);
        ninteent.setCellValue(total.getAvgCar().toString());

        //平均方数
        Cell twenty = totalRow.createCell(22);
        twenty.setCellValue(total.getAvgCubics().toString());

        //平均价格
        Cell twentyOne = totalRow.createCell(23);
        twentyOne.setCellValue(total.getAvgAmount().toString());

        //油耗
        Cell twentyTwo = totalRow.createCell(24);
        twentyTwo.setCellValue(total.getOilConsumption().toString());

        //累计计时台时
        Cell twentyThree = totalRow.createCell(25);
        twentyThree.setCellValue(total.getGrandTimeByTimer().toString());

        //累计计时单价
        Cell twentyFour = totalRow.createCell(26);
        twentyFour.setCellValue("");

        //累计计时总价
        Cell twentyFive = totalRow.createCell(27);
        twentyFive.setCellValue(total.getGrandAmountByTimer() / 100L);

        //累计计时车数
        Cell twentySix = totalRow.createCell(28);
        twentySix.setCellValue(total.getGrandTotalCountByTimer());

        //累计计时方量
        Cell twentySeven = totalRow.createCell(29);
        twentySeven.setCellValue(total.getGrandCubicCountByTimer() / 1000000L);

        //计时平均车数
        Cell twentyEight = totalRow.createCell(30);
        twentyEight.setCellValue(total.getGrandAvgCarByTimer().toString());

        //累计计方台时
        Cell twentyNine = totalRow.createCell(31);
        twentyNine.setCellValue(total.getGrandTimeByCubic().toString());

        //累计计方总车数
        Cell thirty = totalRow.createCell(32);
        thirty.setCellValue(total.getCountCarsByCubic());

        //累计包方总方量
        Cell thirtyOne = totalRow.createCell(33);
        thirtyOne.setCellValue(total.getCountCarsByCubic() / 1000000L);

        //累计包方总金额
        Cell thirtyTwo = totalRow.createCell(34);
        thirtyTwo.setCellValue(total.getCountAmountByCubic() / 100L);

        //累计总工时
        Cell thirtyThree = totalRow.createCell(35);
        thirtyThree.setCellValue(total.getCountTimer().toString());

        //包方总工时
        Cell thirtyFour = totalRow.createCell(36);
        thirtyFour.setCellValue(total.getGrandTimeByCubic().toString());

        //计时总工时
        Cell thirtyFive = totalRow.createCell(37);
        thirtyFive.setCellValue(total.getGrandTimeByTimer().toString());

        //累计包方金额
        Cell thirtySix = totalRow.createCell(38);
        thirtySix.setCellValue(total.getCountAmountByCubic() / 100L);

        //累计计时金额
        Cell thirtySeven = totalRow.createCell(39);
        thirtySeven.setCellValue(total.getGrandAmountByTimer() / 100L);

        //累计工作总金额
        Cell thirtyEight = totalRow.createCell(36);
        thirtyEight.setCellValue(total.getGrandWorkAmount() / 100L);

        //累计加油量
        Cell thirtyNine = totalRow.createCell(37);
        thirtyNine.setCellValue(total.getGrandTotalGrandFill() / 1000L);

        //累计加油金额
        Cell forty = totalRow.createCell(38);
        forty.setCellValue(total.getGrandTotalAmountByFill() / 100L);

        //累计结余金额
        Cell fortyOne = totalRow.createCell(39);
        fortyOne.setCellValue(total.getGrandShouldPayAmount() / 100L);

        //累计平均耗油量
        Cell fortyTwo = totalRow.createCell(40);
        fortyTwo.setCellValue(total.getGrandAvgUseFill().toString());

        //累计平均车辆
        Cell fortyThree = totalRow.createCell(41);
        fortyThree.setCellValue(total.getGrandAvgCarByTimer().toString());

        //累计平均方数
        Cell fortyFour = totalRow.createCell(42);
        fortyFour.setCellValue(total.getGrandAvgCubics().toString());

        //累计平均价格
        Cell fortyFive = totalRow.createCell(43);
        fortyFive.setCellValue(total.getGrandAvgAmount().toString());

        //累计油耗
        Cell fortySix = totalRow.createCell(44);
        fortySix.setCellValue(total.getGrandOilConsumption().toString());

        //写入流
        OutputStream out = new FileOutputStream(new File(newPath));
        workbook.write(out);
        out.flush();
        out.close();
        is.close();
        return newPath;
    }

    public void downLoadFile(HttpServletResponse response, HttpServletRequest request, String path, Date reportDate) throws IOException {
        //获取到要下载的文件
        File file = new File(path);
        FileInputStream in = null;
        OutputStream out = null;
        if (file.exists()) {
            //生成下载保存的文件名
            String fileName = ExcelUtils.getSaveFileName(SmartminingConstant.FILENAEMBYDIGGINGMONTH, reportDate, request);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding(SmartminingConstant.ENCODEUTF);
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            // 读取要下载的文件，保存到文件输入流
            in = new FileInputStream(file);
            // 创建输出流
            out = response.getOutputStream();
            // 创建缓冲区
            byte buffer[] = new byte[1024];
            int len = 0;
            // 循环将输入流中的内容读取到缓冲区当中
            while ((len = in.read(buffer)) > 0) {
                // 输出缓冲区的内容到浏览器，实现文件下载
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        }
    }
}
