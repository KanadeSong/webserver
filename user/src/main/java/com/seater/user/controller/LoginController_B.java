package com.seater.user.controller;

import com.seater.helpers.MD5Helper;
import com.seater.user.dao.SysUserDaoI;
import com.seater.user.entity.SysUser;
import com.seater.user.session.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

//import org.jasypt.encryption.StringEncryptor;

/**
 * 旧的登陆方式 留作备份
 */
@RestController
//@RequestMapping("/api/web")
public class LoginController_B {
    @Autowired
    SysUserDaoI sysUserDaoI;

 //   @Autowired
 //   StringEncryptor stringEncryptor;

    @PostMapping("/login")
    public String Login(String username, String password, HttpSession session)
    {
        List<SysUser> users = sysUserDaoI.findByAccountAndPassword(username, MD5Helper.encode(password));
        if(users.size() > 0) {
            session.setAttribute(WebSecurityConfig.SESSION_KEY, users.get(0));
            return "{\"status\":true}";
        }
        else{
            return "{\"status\":false}";
        }
    }

    @PostMapping("/logout")
    public String LoginOut(HttpSession session)
    {
        session.removeAttribute(WebSecurityConfig.SESSION_KEY);
        return "{\"status\":true}";
    }
}
