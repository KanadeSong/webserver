package com.seater.user.util.constants;

/**
 * @Description: 该模块所用到的所有设定常量
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/7 0026 14:04
 */
public class Constants {

	public static final String SUCCESS_CODE = "100";
	public static final String SUCCESS_MSG = "请求成功";

	//	微信小程序id
	public static final String WX_APP_ID = "wx54a37c6a3b5a64e1";
	//	小程序secret
	public static final String WX_APP_SECRET = "5145d1bd77ac5b6e6906a7720cd3da97";
	//	微信商户号id
	public static final String WX_MCH_ID = "1371958102";
	//	微信商户key
	public static final String WX_KEY = "商户key";
	//	微信请求接口路径
	public static final String WX_OPEN_API = "https://api.weixin.qq.com/sns/jscode2session";
	//	微信支付成功后的服务器回调url
	public static final String WX_NOTIFY_URL = "https://localhost:8080/api/wxOption/wxNotify";
	//	交易类型，小程序支付的固定值为JSAPI
	public static final String WX_TRADE_TYPE = "JSAPI";
	//	微信统一下单接口地址
	public static final String WX_PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	//	签名方式，固定值
	public static final String WX_SIGN_TYPE = "MD5";

	//	微信小程序 默认用户密码
	public static final String WX_APP_DEFAULT_USER_PASSWORD = "123456";
	//	微信小程序 项目外 默认角色
	public static final Long WX_APP_DEFAULT_ROLE = 10L;
	//	微信小程序 项目内 项目管理员
	public static final Long WX_APP_DEFAULT_ROLE_IN_PROJECT = 20L;
	//	微信小程序 项目内 默认员工角色
	public static final Long WX_APP_EMPLOYEE_ROLE_IN_PROJECT = 120L;
	//	微信小程序 默认车主角色
	public static final Long WX_APP_CAR_OWNER_ROLE_IN_PROJECT = 40L;
	//	微信小程序 默认司机角色
	public static final Long WX_APP_CAR_DRIVER_ROLE_IN_PROJECT = 50L;


	//	阿里云key
	public static final String ALY_KEY = "LTAIf5tf0mIRtEiB";
	//	阿里云secret
	public static final String ALY_SECRET = "AjGuO332d5vajhMxlC5RIhjzIXK4sP";
	//	阿里云短信服务地域ID
	public static final String ALY_SMS_LOCATION = "cn-hangzhou";
	//	阿里云短信验证码签名
	public static final String ALY_SMS_SIGN_NAME = "西特智能";
	//	阿里云短信服务模板代码
	public static final String ALY_SMS_TEMPLATE_CODE = "SMS_162735925";


	//	时间格式
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	//	字符编码 UTF--8
	public static final String ENCODE_UTF_8 = "UTF-8";

	//	头像文件上传路径
	public static final String UPLOAD_PATH = "/usr/java/upload/img";
	//	项目内图片上传路径
    public static final String UPLOAD_PATH_IN_PROJECT = UPLOAD_PATH + "/uploadInProject";
    //  平台端图片上传路径
    public static final String UPLOAD_PATH_PLAT = UPLOAD_PATH + "/uploadInPlat";
    //	允许上传的文件格式,com.alibaba.fastjson.JSONArray格式
	public static final String UPLOAD_ALLOW_TYPE = "[\"jpg\",\"png\",\"jpeg\",\"gif\"]";
	//	项目logo路径
    public static final String LOGO_PATH = UPLOAD_PATH + "/QrCode/logo.jpg";

	//	session过期时间(单位:毫秒)
	public static final Long SESSION_TIMEOUT = 1000 * 60L * 10;

	//	第三方对讲机接口认证信息redis过期时间 接口规定token有效期为 1小时
	public static final Long INTER_PHONE_REDIS_TIMEOUT = 1000 * 60L * 60;
	//	第三方对讲机接口认证请求时间间隔 接口规定token有效期为 1小时 所以1小时重新登陆一次
//	public static final Long INTER_PHONE_AUTHORIZE_TIMEOUT = 1000 * 60L * 60;
	//	第三方对讲接口集团uid
	public static final String INTER_PHONE_UID = "2019032800101";
	//	第三方对讲接口登陆用户名
	public static final String INTER_PHONE_USERNAME = "M_jiekoutest";
	//	第三方对讲接口登陆密码
	public static final String INTER_PHONE_PASSWORD = "12345678";
	//  第三方对讲接口账号的密码
    public static final String INTER_PHONE_ACCOUNT_PASSWORD = "12345678";
    //	第三方对讲账号续费月数,第三方平台默认3个月, 这里修改成一年
    public static final Integer INTER_PHONE_CREDIT_MONTHS = 12;
	//	第三方对讲接口管理员名称
	public static final String INTER_PHONE_ADMIN_NAME = "super";
    //	是否判断第三方对讲机的额度 默认先不判断
	public static final Boolean isCheckLimit = true;
    //	每月单个账号额度 , 额度计价方式:一个账号40元每年
	public static final Integer INTER_PHONE_CREDIT_MONTH_PER_ACC = 0;


	//	系统管理员账号 与数据库中的对应即可
	public static final String SUPER_USER_ACCOUNT = "super";
	//	系统管理员密文密码 与数据库中的对应即可
	public static final String SUPER_USER_PASSWORD = "a5a9d064b468b8e137f3c0d545b4b613";
	//	项目内的超级管理员id
	public static final Long SUPER_USER_ACCOUNT_IN_PROJECT = 1L;


	//  session中存放用户信息的key值
	public static final String SESSION_USER_INFO = "user";
	//  session中存放总的用户权限信息的key值(包含角色和权限等信息)
	public static final String SESSION_USER_PERMISSION = "userPermission";

	//	权限相关key值
	//  项目外的权限列表
	public static final String PERMISSION_LIST = "permissionList";
	//  项目外的权限数组 Controller层中接口使用 @RequiresPermissions 注解时的值
	public static final String PERMISSION_ARRAY = "permissionArray";
	//  项目外角色列表
	public static final String ROLE_LIST = "roleList";
	//  项目外菜单列表
	public static final String MENU_LIST = "menuList";

	//	项目
	public static final String PROJECT = "project";
	//	项目列表
	public static final String PROJECT_LIST = "projectList";
	//	项目中的角色列表
	public static final String ROLE_LIST_IN_PROJECT = "roleListInProject";
	//	项目中的权限
	public static final String PERMISSION_LIST_IN_PROJECT = "permissionListInProject";
    //	项目中的权数数组  注解时的权限数组的key值(Controller层中接口使用 @RequiresPermissions)
	public static final String PERMISSION_ARRAY_IN_PROJECT = "permissionArrayInProject";
	//  项目中菜单列表
	public static final String MENU_LIST_IN_PROJECT= "menuListInProject";
	//	项目中角色的最大权限排序(值越小权限越大)sys_role表
	public static final String ROLE_MIX_SORT = "roleMixSort";
	//	权限相关key值 end

    //	线程执行成功结果
    public static final Boolean THREAD_RUN_SUCCESS = true;
    //	线程执行失败结果
    public static final Boolean THREAD_RUN_FAILED = false;
}
