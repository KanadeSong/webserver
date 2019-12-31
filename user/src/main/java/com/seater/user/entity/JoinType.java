package com.seater.user.entity;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/7 18:23
 */
public enum  JoinType {

    Unknown("Unknown"),
    CarOwner("CarOwner"),
    Employee("Employee");

    private String value;
    JoinType(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
