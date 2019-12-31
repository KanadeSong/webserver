package com.seater.smartmining.utils.string;

import com.seater.smartmining.constant.SmartminingConstant;
import com.seater.smartmining.utils.date.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/31 0031 11:43
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * 一个中文汉字的长度为2 获取字符串长度
     *
     * @param str
     * @return
     */
    public static int getStrLength(String str) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }


    /**
     *  将字符串拆分成数组
     * @param text 要拆分的字符串
     * @param keyWord 关键字
     * @param length 长度
     * @return
     */
    public static String substringText(String text, String keyWord, Integer length, String regex){
        String result = "";
        if (text.contains(keyWord)) {
            int i = text.indexOf(keyWord);
            String changeTex = text.substring(i + 5, i + length);
            int index = changeTex.indexOf(SmartminingConstant.COLON);
            result = changeTex.substring(0, index);
        }
        return result;
    }

    /**
     * 将二进制转换成字符串
     * @param text
     * @return
     */
    public static String convertByByte(String text){
        byte[] bytes = text.getBytes();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i ++){
            sb.append((char)bytes[i]);
        }
        return sb.toString();
    }

    /**
     * 将数组转换成字符串
     * @param objects
     * @return
     */
    public static String converArrayToString(Object[] objects){
        String result = "";
        if(objects != null){
            for(int i = 0; i < objects.length; i++){
                if(i != objects.length - 1){
                    result = result + objects[i] + SmartminingConstant.COMMA;
                }else{
                    result = result + objects[i];
                }
            }
        }
        return result;
    }

    //根据项目ID随机生成10位唯一编码
    public static String createCode(Long projectId){
        Random r=new Random();
        int random = r.nextInt(10);
        Long number = random * projectId;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String monthStr = String.valueOf(month);
        if(month < 10)
            monthStr = "0" + month;
        String dayStr = String.valueOf(day);
        if(day < 10)
            dayStr = "0" + day;
        String code = year + monthStr + dayStr + number;
        return code;
    }
}
