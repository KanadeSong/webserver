package com.seater.user.entity;

/**
 * @Description 推荐类型(来源)
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:37
 */
public enum RecommendType {

    Unknown("未知"),
    Wx("微信"),
    Zfb("支付宝");
    private String value;

    private RecommendType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
