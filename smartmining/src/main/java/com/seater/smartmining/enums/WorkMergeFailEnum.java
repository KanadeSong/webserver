package com.seater.smartmining.enums;

/**
 * @Description:作业信息合并异常枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/22 0022 18:05
 */
public enum WorkMergeFailEnum {

    UNKNOW("未知",0),
    BackStageError("后台异常",1),
    DeviceUnLineError("终端离线异常",2),
    NoHaveDevice("未安装终端",3),
    WorkError("未按规定装载",4),
    WithoutSchedule("排班不存在", 5),
    WithoutCarCode("渣车不存在", 6),
    WithoutSlagSiteCode("渣场不存在", 7),
    ScheduleError("当前模式不支持混编", 8),
    WithoutLoader("物料不存在", 9),
    WithoutSlagCarDevice("渣车终端未上传", 10),
    LostSchedule("排班丢失", 11),
    WithoutDiggingMachine("挖机不存在", 12),
    DeviceErrorLike("疑似终端异常", 13),
    RecoverWorkInfoFail("容错失败", 14);

    private String name;
    private Integer value;

    WorkMergeFailEnum(String name, Integer value){
        this.name = name;
        this.value = value;
    }

    public static WorkMergeFailEnum convert(Integer value){
        for(WorkMergeFailEnum fail : WorkMergeFailEnum.values()){
            if(fail.getValue() == value){
                return fail;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
