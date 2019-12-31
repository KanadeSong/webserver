package com.seater.user.util.constants;

/**
 * @Description: 所有Shiro权限的注解值配置    >>>>>   @RequiresPermissions(abc:xxx)
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/19 0026 14:04
 */
public class PermissionConstants {

    //  定义标记
    public static final String SIGN= ":";
    
    //  基本权限描述
    //  增
    public static final String ADD          =        "add";
    //  删
    public static final String DELETE       =        "delete";
    //  查
    public static final String QUERY        =        "query";
    //  改
    public static final String UPDATE       =        "update";
    //  保存
    public static final String SAVE         =        "save";
    //  访问 (多数是整个Controller的访问权限)
    public static final String ACCESS       =        "assess";
    //  ...
    
    
    
    /**
     * 用户增删查改
     */
    public static final String PROJECT_USER = "PROJECT_USER" + SIGN;
    public static final String PROJECT_USER_ADD             =       PROJECT_USER + ADD;
    public static final String PROJECT_USER_DELETE          =       PROJECT_USER + DELETE;
    public static final String PROJECT_USER_QUERY           =       PROJECT_USER + QUERY;
    public static final String PROJECT_USER_UPDATE          =       PROJECT_USER + UPDATE;
    public static final String PROJECT_USER_SAVE            =       PROJECT_USER + SAVE;
    //  ...

    /**
     * 项目增删查改
     */
    public static final String PROJECT = "project" + SIGN;
    public static final String PROJECT_ADD                  =       PROJECT + ADD;
    public static final String PROJECT_DELETE               =       PROJECT + DELETE;
    public static final String PROJECT_QUERY                =       PROJECT + QUERY;
    public static final String PROJECT_UPDATE               =       PROJECT + UPDATE;
    public static final String PROJECT_SAVE                 =       PROJECT + SAVE;
    //  ...

    /**
     * 项目渣车增删改查
     */
    public static final String PROJECT_CAR = "projectCar" + SIGN;
    public static final String PROJECT_CAR_ADD                  =       PROJECT_CAR + ADD;
    public static final String PROJECT_CAR_DELETE               =       PROJECT_CAR + DELETE;
    public static final String PROJECT_CAR_QUERY                =       PROJECT_CAR + QUERY;
    public static final String PROJECT_CAR_UPDATE               =       PROJECT_CAR + UPDATE;
    public static final String PROJECT_CAR_SAVE                 =       PROJECT_CAR + SAVE;

    /**
     * 项目挖机增删改查
     */
    public static final String PROJECT_DIGGING_MACHINE = "projectDiggingMachine" + SIGN;
    public static final String PROJECT_DIGGING_MACHINE_ADD      =       PROJECT_DIGGING_MACHINE + ADD;
    public static final String PROJECT_DIGGING_MACHINE_DELETE   =       PROJECT_DIGGING_MACHINE + DELETE;
    public static final String PROJECT_DIGGING_MACHINE_QUERY    =       PROJECT_DIGGING_MACHINE + QUERY;
    public static final String PROJECT_DIGGING_MACHINE_UPDATE   =       PROJECT_DIGGING_MACHINE + UPDATE;
    public static final String PROJECT_DIGGING_MACHINE_SAVE     =       PROJECT_DIGGING_MACHINE + SAVE;

    /**
     * 
     */
    public static final String PROJECT_MATERIAL = "projectMaterial" + SIGN;
    public static final String PROJECT_MATERIAL_ADD                  =       PROJECT_MATERIAL + ADD;
    public static final String PROJECT_MATERIAL_DELETE               =       PROJECT_MATERIAL + DELETE;
    public static final String PROJECT_MATERIAL_QUERY                =       PROJECT_MATERIAL + QUERY;
    public static final String PROJECT_MATERIAL_UPDATE               =       PROJECT_MATERIAL + UPDATE;
    public static final String PROJECT_MATERIAL_SAVE                 =       PROJECT_MATERIAL + SAVE;

    /**
     * 
     */
    public static final String CAR_BRAND = "carBrand" + SIGN;
    public static final String CAR_BRAND_ADD                  =       CAR_BRAND + ADD;
    public static final String CAR_BRAND_DELETE               =       CAR_BRAND + DELETE;
    public static final String CAR_BRAND_QUERY                =       CAR_BRAND + QUERY;
    public static final String CAR_BRAND_UPDATE               =       CAR_BRAND + UPDATE;
    public static final String CAR_BRAND_SAVE                 =       CAR_BRAND + SAVE;

    /**
     * 
     */
    public static final String CAR_MODEL = "carModel" + SIGN;
    public static final String CAR_MODEL_ADD                  =       CAR_MODEL + ADD;
    public static final String CAR_MODEL_DELETE               =       CAR_MODEL + DELETE;
    public static final String CAR_MODEL_QUERY                =       CAR_MODEL + QUERY;
    public static final String CAR_MODEL_UPDATE               =       CAR_MODEL + UPDATE;
    public static final String CAR_MODEL_SAVE                 =       CAR_MODEL + SAVE;

    
    public static final String PROJECT_SCHEDULED = "appWorkPlan" + SIGN;
    public static final String PROJECT_SCHEDULED_ADD                  =       PROJECT_SCHEDULED + ADD;
    public static final String PROJECT_SCHEDULED_DELETE               =       PROJECT_SCHEDULED + DELETE;
    public static final String PROJECT_SCHEDULED_QUERY                =       PROJECT_SCHEDULED + QUERY;
    public static final String PROJECT_SCHEDULED_UPDATE               =       PROJECT_SCHEDULED + UPDATE;
    public static final String PROJECT_SCHEDULED_SAVE                 =       PROJECT_SCHEDULED + SAVE;

    
    public static final String LOGIN = "login" + SIGN;
    public static final String LOGIN_OILAPP                     =       LOGIN + "oilApp";
    public static final String LOGIN_WEB                        =       LOGIN + "web";
    public static final String LOGIN_LOAD_APP                  =       LOGIN + "loadApp";
    public static final String LOGIN_WX                         =       LOGIN + "wx";
    
    public static final String INTER_PHONE = "interPhone" + SIGN;
    public static final String INTER_PHONE_ADD                  =       INTER_PHONE + ADD;
    public static final String INTER_PHONE_DELETE               =       INTER_PHONE + DELETE;
    public static final String INTER_PHONE_QUERY                =       INTER_PHONE + QUERY;
    public static final String INTER_PHONE_UPDATE               =       INTER_PHONE + UPDATE;
    public static final String INTER_PHONE_SAVE                 =       INTER_PHONE + SAVE;
    public static final String INTER_PHONE_ACCESS               =       INTER_PHONE + ACCESS;
    
}
