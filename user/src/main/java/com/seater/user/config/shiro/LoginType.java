package com.seater.user.config.shiro;

/**
 * @Description 登陆类型 *****十分重要*****:value 必须要和对应 Realm名称一致,且不能重名,
 * 比如: PasswordRealm这个类 对应LoginType这个枚举里面的 Password(0, "PasswordRealm")  >>PasswordRealm<< 值 
 * 要是需要加上其他方式登陆就加一个
 * 可以看这个类的判断 >>>UserModularRealmAuthenticator<<< --> realm.getName().contains(loginType)***
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/21 9:11
 */
public enum LoginType {
    Password(0, "PasswordRealm"),   //  用户密码登陆的realm
    Wx(1, "WxOpenIdRealm"),         //  微信openId登陆的realm
    ;

    private Integer code;
    private String value;

    LoginType(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
