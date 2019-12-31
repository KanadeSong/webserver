package com.seater.smartmining.constant;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/29 0029 16:18
 */
public class SmartminingConstant {

    //字符编码 UTF--8
    public static final String ENCODEUTF = "UTF-8";

    public static final String ENCODEISO = "ISO-8859-1";

    //yyyy-MM-dd HH:mm:ss
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATEFORMATTWO = "yyy/MM/dd HH:mm:ss";

    //yyyy-MM
    public static final String MONTHDAYFORMAT = "yyyy-MM";

    public static final String YEARMONTHDAUFORMAT = "yyyy-MM-dd";

    //默认异常信息
    public static final String ERRORMESSAGE = "操作失败，未知异常";

    //挖机月度报表excel文件保存路径 服务器
    public static final String EXCELSAVEPATH = "/usr/java/save";
    //public static final String EXCELSAVEPATH = "D:\\excel\\digging\\save";

    //挖机日报表excel模板保存路径 本地
    //public static final String DAYREPORTMODELPATHBYMACHINE = "D:\\excel\\digging\\model\\digging_day_model.et";

    //挖机日报表excel模板保存路径 服务器
    public static final String DAYREPORTMODELPATHBYMACHINE = "/usr/java/model/digging_day_model.et";

    //挖机月度报表excel模板保存路径 服务器
    public static final String MONTHREPORTMODELPATH = "/usr/java/model/digging_month_model.et";

    //挖机月度报表excel模板保存路径 本地
    //public static final String MONTHREPORTMODELPATH = "D:\\excel\\digging\\model\\digging_month_model.et";

    //渣车日报表excel模板保存路径 本地
    //public static final String DAYREPORTMODELPATH = "D:\\excel\\digging\\model\\car_day_model.et";

    //渣车日报表excel模板保存路径 服务器
    public static final String DAYREPORTMODELPATH = "/usr/java/model/car_day_model.et";

    //渣车月度报表excel模板保存路径 服务器
    public static final String CARMONTHREPORTMODELPATH = "/usr/java/model/car_month_model.et";

    //渣车月度报表excel模板保存路径 本地
    //public static final String CARMONTHREPORTMODELPATH = "D:\\excel\\digging\\model\\car_month_model.et";

    //文件上传地址 本地
    //public static final String UPLOADFILEPATH = "D:\\";

    //文件上传地址 服务器
    public static final String UPLOADFILEPATH = "/usr/java/upload";


    //excel报表的文件格式
    public static final String EXCELSUFFIX = ".et";

    public static final String XLSSUFFIX = ".xls";

    //public static final String TEXTSUFFIX = ".txt";

    //生成报表后下载excel的文件名
    public static final String FILENAEMBYDIGGINGMONTH = "挖机${month}月度报表";

    public static final String FILENAMEBYDIGGINGDAY = "挖机${date}报表";

    //生成报表后下载excel的文件名
    public static final String FILENAMEBYCARMONTH = "渣车${month}月度报表";

    //生成报表后下载excel的文件名
    public static final String FILENAMEBYCARDAY = "渣车${date}报表";

    //挖机月度报表表格头部替换字符串
    public static final String HEADERBYYEAR = "${year}";
    public static final String HEADERBYMONTH = "${month}";
    public static final String HEADERBYSTARTDAY = "${start}";
    public static final String HEADERBYENDDAY = "${end}";
    public static final String PROJECTNAME = "${projectName}";
    public static final String HEADERBYDAY = "${day}";

    //HttpServletRequest对象的USER-AGENT
    public static final String HTTP_REUQEST_USER_AGENT = "USER-AGENT";

    //IE浏览器 MSIE
    public static final String IE_MSIE = "MSIE";

    //IE浏览器 Trident
    public static final String IE_TRIDENT = "Trident";

    //火狐浏览器
    public static final String MOZILLA = "Mozilla";

    //软件版本对应的关键字
    public static final String SOFTWAREKEYWORD = "sver:";
    //硬件版本对应的关键字
    public static final String HARDWAREKEYWORD = "hver:";
    //设备类型对应的关键字
    public static final String MACHINETYPEKEYWORD = "devt:";
    //冒号
    public static final String COLON = ":";
    //ZIP文件后缀
    public static final String ZIPSUFFIX = "zip";
    //逗号
    public static final String COMMA = ",";

    //排班权限查询全部
    public static final String ALLDATA = "appWorkPlan:allData";
    //排班权限筛选查询
    public static final String MANAGERELATED = "appWorkPlan:manageRelated";

    //渣场权限查询全部
    public static final String ALLDATASITE = "appSite:allData";
    //渣场权限筛选查询
    public static final String MANAGERELATEDSITE = "appSite:manageRelated";

    public static final String WORKFILENAME = "work.et";

    public static final String EXCEPTIONFILENAME = "exception_report.xls";

    public static final String SLAGSITELOGFILENAME = "slagSiteWork.et";

    public static final String SLAGSITENAME = "slagSite.et";

    public static final String CARDAYREPORTNAME = "渣车日报表";

    //时间相关
    //中文
    public static final String CN_YEAR = "年";

    public static final String CN_MONTH = "月";

    public static final String CN_WEEK = "周";

    public static final String CN_DAY = "日";

    public static final String CN_HOUR = "时";

    public static final String CN_MINUTE = "分";

    public static final String CN_SECOND = "秒";
    //英文
    public static final String EN_YEAR = "year";

    public static final String EN_MONTH = "month";

    public static final String EN_WEEK = "week";

    public static final String EN_DAY = "day";

    public static final String EN_HOUR = "hour";

    public static final String EN_MINUTE = "minute";

    public static final String EN_SECOND = "second";

    //redis缓存常量
    //  当班实时数据
    public static final String CAL_SHIFT_STATIC_DATA = "calShiftStaticData";
    //  项目工作信息排行榜
    public static final String PROJECT_WORK_INFO_RANK = "projectWorkInfoRank";
    //  渣车工作信息排行榜
    public static final String PROJECT_CAR_RANK = PROJECT_WORK_INFO_RANK + ":projectCarRank";
    //  挖机工作信息排行榜
    public static final String DIGGING_MACHINE_RANK = PROJECT_WORK_INFO_RANK + ":diggingMachineRank";
    //  排行榜redis缓存时间(毫秒)
    public static final Long WORK_INFO_RANK_REDIS_TIME_OUT = 120000L;

    public static final String APP_SCHEDULE_IDENTIFICATION = "appSchedule";

    public static final String DIGGING_MACHINE_KEY_WORD = "work";

    public static final String APP_CAR_INFO = "app_car_info:";

    public static final String APP_DIGGING_MACHINE_INFO = "app_digging_machine_info:";

}
