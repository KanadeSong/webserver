package com.seater.user.service;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.Sex;
import com.seater.user.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 处理登陆和授权的相关逻辑的Service层
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/7 0026 14:04
 */
public interface LoginServiceI {

    public SysUser getByAccountAndPassword(String username, String password);

    /**
     * 验证登陆表单
     * @param username  用户名
     * @param password  密码
     * @return  登陆结果
     */
    public JSONObject login(String username, String password);

    /**
     * 登出
     * @return  登出结果
     */
    public JSONObject logout();

    /**
     * 查询当前登陆用户的权限信息
     * @return  当前用户的权限信息
      */
    public JSONObject getInfo(Long userId);

    /**
     * 微信登陆
     * @param code
     * @return
     */
    public JSONObject wxLogin(String code, String mobile);

    /**
     * 微信注册
     * @return
     */
    public JSONObject wxRegister(SysUser sysUser);

    /**
     * 微信修改用户信息
     * @param sysUser
     * @return
     */
    public JSONObject wxEdit(SysUser sysUser);

    /**
     * 获取微信openId
     * @param code 用户code
     * @return 微信openId
     */
    public String wxCode(String code);
    
}
