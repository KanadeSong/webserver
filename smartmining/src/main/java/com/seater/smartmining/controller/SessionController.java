package com.seater.smartmining.controller;

import com.seater.helpers.JsonHelper;
import com.seater.user.entity.SysUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.seater.user.session.WebSecurityConfig.SESSION_KEY;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @RequestMapping("/web")
    public Object web(HttpServletRequest request)
    {
        try {
            HttpSession session = request.getSession();
            SysUser user = (SysUser) session.getAttribute(SESSION_KEY);
            return JsonHelper.jsonStringToObject("{\"roleType\":\"Super\", \"type\":\"SysUser\", \"username\":\""
                    + user.getAccount() + "\"}", Map.class);

        }
        catch (Exception e)
        {
            return null;
        }
    }

//    @RequestMapping("/menus")
//    public String menus()
//    {
//        return "";
//    }
}
