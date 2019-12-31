package com.seater.user.config.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @Description 继承UsernamePasswordToken ,添加一个登陆类型 靠这个登陆类型分开登陆时走不同的Realm
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/21 9:08
 */
public class UserToken extends UsernamePasswordToken {

    /**
     * 登陆类型 @LoginType 这个枚举的值
     */
    private String loginType;

    public UserToken() {
        super();
    }

    /**
     * 账号密码登录
     */
    public UserToken(String username, String password, String loginType, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
        this.loginType = loginType;
    }

    public UserToken(String username, String password,
                     LoginType loginType) {
        super(username, password);
        this.loginType = loginType.getValue();
    }

    /**免密登录*/
//    public UserToken(String username) {
//        super(username, "", false, null);
//        this.loginType = LoginType.Wx.getValue();
//    }

    /**
     * 免密登陆,带登陆类型
     *
     * @param username  用户名
     * @param loginType 登陆类型
     */
    public UserToken(String username, LoginType loginType) {
        super(username, "", false, null);
        this.loginType = loginType.getValue();
    }

    public UserToken(String username, String password) {
        super(username, password, false, null);
        this.loginType = LoginType.Password.getValue();
    }

    public String getLoginType() {
        return this.loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
