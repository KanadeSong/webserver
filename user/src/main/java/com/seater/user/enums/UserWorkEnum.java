package com.seater.user.enums;

/**
 * @Description:人员工作枚举
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/11 0011 17:10
 */
public enum UserWorkEnum {

    Unknow("未知",0),
    BeginWork("上班",1),
    EndWork("下班",2),
    Leave("请假",3);

    private String name;
    private Integer value;
    UserWorkEnum(String name,Integer value)
    {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.name;
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
