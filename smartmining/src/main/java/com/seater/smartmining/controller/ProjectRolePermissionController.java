package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.*;
import com.seater.user.manager.RoleManager;
import com.seater.user.service.SysPermissionServiceI;
import com.seater.user.service.SysRolePermissionServiceI;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/projectRolePermission")
public class ProjectRolePermissionController {

    @Autowired
    SysPermissionServiceI sysPermissionServiceI;

    @Autowired
    SysRolePermissionServiceI sysRolePermissionServiceI;

    @Autowired
    SysRoleServiceI sysRoleServiceI;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;
    @Autowired
    RoleManager roleManager;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @RequestMapping("/get")
    public Object get(Long id) {
        try {
            SysPermission role = sysPermissionServiceI.get(id);
            return role;
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }


    @RequestMapping("/queryByRoleId")
    public Object queryByRole(Long roleId, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            return roleManager.getPermissionsByRootAndProjectId(projectId);

        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @RequestMapping("/getInfo")
    public Object getInfo(HttpServletRequest request) {
        return request.getSession().getAttribute(Constants.SESSION_USER_PERMISSION);
    }

    @PostMapping("/save")
    public Object save(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            Long roleId = Long.parseLong(jsonObject.get("roleId").toString());
            SysRole role = sysRoleServiceI.get(roleId);
            if (null != role.getUseType() &&
                    UseType.Default.equals(role.getUseType()) ||
                    null != role.getIsDefault() &&
                            role.getIsDefault()) {
                return CommonUtil.errorJson(UseType.Default.getName() + "类型不能操作");
            }
            List<Integer> permissionIds = (List<Integer>) jsonObject.get("permissionIds");
            sysRolePermissionServiceI.deleteByRoleIdAndProjectIdAndUseType(roleId, projectId, UseType.Project);

            for (Integer permissionId : permissionIds) {
                SysRolePermission sysRolePermission = new SysRolePermission();
                sysRolePermission.setRoleId(roleId);
                sysRolePermission.setProjectId(projectId);
                sysRolePermission.setUseType(UseType.Project);
                sysRolePermission.setPermissionId(Long.parseLong(permissionId + ""));
                sysRolePermissionServiceI.save(sysRolePermission);
            }
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

}
