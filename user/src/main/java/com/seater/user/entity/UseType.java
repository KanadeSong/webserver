package com.seater.user.entity;

/**
 * @Description 使用类型
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/31 11:03
 */
public enum UseType {

    Default("默认", 0),    //默认的在项目内不能删除
    Project("项目", 1);    //项目是在项目内创建的角色或者权限,可以删除

    private String name;
    private Integer value;

    UseType(String name, Integer value) {
        this.name = name;
        this.value = value;
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
