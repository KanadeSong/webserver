package com.seater.smartmining.utils.file;
import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.utils.BrowserUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: Excel表格工具类
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/31 0031 11:27
 */
public class ExcelUtils {

    /**
     *  判断是否是合并单元格
     * @param sheet
     * @param row 行下标
     * @param column 列下标
     * @return
     */
    public static boolean isMergedRegion(Sheet sheet, int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  判断是否包含合并单元格
     * @param sheet
     * @return
     */
    public static boolean hasMerged(Sheet sheet){
        return sheet.getNumMergedRegions() > 0 ? true : false;
    }

    /**
     *  合并单元格
     * @param sheet
     * @param firstRow 开始行
     * @param lastRow 结束行
     * @param firstCol 开始列
     * @param lastCol 结束列
     */
    public static void mergeRegion(Sheet sheet, int firstRow, int lastRow,
                                   int firstCol, int lastCol){
        CellRangeAddress cra = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        for(int i = firstRow;i<=lastRow;i++){
            for(int j = firstCol;j<=lastCol;j++){
                Row row = sheet.getRow(i);
                row.createCell(j);
            }
        }
        sheet.addMergedRegion(cra);
    }

    /**
     *  获取单元格的值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell){
        if(cell != null) {
            if (cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
                return String.valueOf(cell.getBooleanCellValue());
            } else if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
                return cell.getCellFormula();
            } else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                return String.valueOf(cell.getNumericCellValue());
            } else {
                return cell.getStringCellValue();
            }
        }
        return null;
    }

    /**
     * 获取合并单元格的值
     * @param sheet
     * @param row  行
     * @param column 列
     * @return
     */
    public static String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();
        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }
        return null ;
    }

    public static CellStyle setCellStyleUtilsByMonthReport(Workbook workbook){
        //设置写入单元格的样式
        CellStyle setStyle = workbook.createCellStyle();
        setStyle.setWrapText(true);         //自动换行
        setStyle.setAlignment(HorizontalAlignment.CENTER);      //水平居中
        setStyle.setVerticalAlignment(VerticalAlignment.CENTER);        //垂直居中
        setStyle.setBorderBottom(BorderStyle.THIN);     //下边框样式
        setStyle.setBorderLeft(BorderStyle.THIN);       //左边框样式
        setStyle.setBorderRight(BorderStyle.THIN);      //有边框样式
        setStyle.setBorderTop(BorderStyle.THIN);        //上边框样
        setStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());      //下边框颜色
        setStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());    //左边框颜色
        setStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());    //有边框颜色
        setStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());     //上边颜色
        //设置写入字体样式
        Font font = workbook.createFont();
        //加粗
        font.setBold(true);
        //字体风格
        font.setFontName("宋体");
        //字体大小
        font.setFontHeightInPoints((short)11);
        setStyle.setFont(font);
        return setStyle;
    }

    /**
     * 生成保存的文件的文件名
     * @param keyWord 常量 关键字
     * @return
     */
    public static String getSaveFileName(String keyWord, Date reportDate, HttpServletRequest request) throws UnsupportedEncodingException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reportDate);
        int month = calendar.get(Calendar.MONTH) + 1;
        //下载保存的文件名
        String fileName = keyWord.replace("${month}" ,String.valueOf(month)) + SmartminingConstant.XLSSUFFIX;
        fileName = BrowserUtils.getFileName(fileName,request);
        return fileName;
    }
}
