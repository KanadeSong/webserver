package com.seater.smartmining.enums;

/**
 * @Description:作业信息合并成功枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/22 0022 16:27
 */
public enum WorkMergeSuccessEnum {

    UNKNOW("未知",0),
    SingleSlagSiteSuccessMerge("仅渣场数据合并",1),
    SuccessMerge("正常合并",2),
    AutoErrorMerge("自动容错",3),
    HandleErrorMerge("手动恢复", 4);

    private String name;
    private Integer value;

    WorkMergeSuccessEnum(String name, Integer value){
        this.name = name;
        this.value = value;
    }

    public static WorkMergeSuccessEnum convert(Integer value){
        for(WorkMergeSuccessEnum success : WorkMergeSuccessEnum.values()){
            if(success.getValue() == value){
                return success;
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
