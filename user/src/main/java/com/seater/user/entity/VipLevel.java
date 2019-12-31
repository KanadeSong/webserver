package com.seater.user.entity;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/27 14:45
 */
public enum VipLevel {
    Level0("VIP0"),
    Level1("VIP1"),
    Level2("VIP2"),
    Level3("VIP3"),
    Level4("VIP4");
    private String value;
    VipLevel(String value)
    {
        this.value = value;
    }

    private static VipLevel[] vals = values();
    
    public VipLevel previous() {
        return vals[(this.ordinal() - 1) % vals.length];
    }

    public VipLevel next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
