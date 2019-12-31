package com.seater.user.entity;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/1 15:43
 */
public enum DistributeStatus {
    Undistribute("未分配"),
    Distributed("已分配");

    private String value;
    DistributeStatus(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
