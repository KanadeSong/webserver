package com.seater.smartmining.utils.interPhone;

/**
 * @Description 使用对讲机的绑定者枚举
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/11 11:32
 */
public enum UserObjectType {

    SlagCar("SlagCar"),                 //  渣车  ===> ProjectCar
    DiggingMachine("DiggingMachine"),   //  挖机  ===> ProjectDiggingMachine
    OilCar("OilCar"),                   //  油车  ===> ProjectOtherDevice
    Person("Person")                    //  人(每个人可以在不同项目有不同的对讲账号)    ===> SysUserProjectRole
    ;

    private String value;
    UserObjectType(String value)
    {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
