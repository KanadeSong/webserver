package com.seater.user.entity;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:34
 */
public enum JoinStatus {
    Unorganized("未加入"),
    Joined("已加入");

    private String value;
    JoinStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }

}
