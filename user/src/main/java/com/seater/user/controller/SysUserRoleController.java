package com.seater.user.controller;

import com.seater.user.entity.SysUserRole;
import com.seater.user.service.SysUserRoleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/sysUserRole")
public class SysUserRoleController {
    
    @Autowired
    SysUserRoleServiceI sysUserRoleServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping("/get")
    public Object get(Long id)
    {
        try {
            return sysUserRoleServiceI.get(id);
        }
        catch (Exception e)
        {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @PostMapping("/save")
    public Object save(SysUserRole sysUserRole)
    {
        try {
            sysUserRoleServiceI.save(sysUserRole);
            return "{\"status\":true}";
        }
        catch (Exception e)
        {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

}
