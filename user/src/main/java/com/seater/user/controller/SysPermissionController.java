package com.seater.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.SysPermission;
import com.seater.user.entity.UseType;
import com.seater.user.service.SysPermissionServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
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
@RequestMapping("/api/sysPermission")
public class SysPermissionController {

    @Autowired
    SysPermissionServiceI sysPermissionServiceI;

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

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String name, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<SysPermission> spec = new Specification<SysPermission>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysPermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(name)) {
                        list.add(cb.equal(root.get("name").as(Long.class), name));
                    }
                    if (!ObjectUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    }
                    list.add(cb.equal(root.get("useType").as(UseType.class), UseType.Default));
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return sysPermissionServiceI.queryWx(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @RequestMapping("/queryByRoleId")
    public Object queryByRole(Long roleId, Long parentId, HttpServletRequest request) {
        try {
            List<Long> roleIdList = new ArrayList<>();
            roleIdList.add(roleId);
            roleIdList.add(parentId);
            return sysPermissionServiceI.getUserPermissionByRoleIds(roleIdList);
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @RequestMapping("/getInfo")
    public Object getInfo(HttpServletRequest request) {
        return request.getSession().getAttribute(Constants.SESSION_USER_PERMISSION);
    }

    @PostMapping("/save")
    public Object save(@RequestBody SysPermission sysPermission, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            sysPermission.setUseType(UseType.Default);
            sysPermissionServiceI.save(sysPermission);
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @PostMapping("/deletes")
    @Transactional
    public Object deletes(@RequestBody List<Long> ids, HttpServletRequest request) throws IOException {
        for (Long id : ids) {
            SysPermission permission = sysPermissionServiceI.get(id);
            if (permission.getUseType().equals(UseType.Default)) {
                return CommonUtil.errorJson(UseType.Default.getName() + "类型不能删除", permission);
            }
        }

        for (Long id : ids) {
            sysPermissionServiceI.delete(id);
        }
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/delete")
    @Transactional
    public Object delete(Long id, HttpServletRequest request) throws IOException {
        SysPermission permission = sysPermissionServiceI.get(id);
        if (permission.getUseType().equals(UseType.Default)) {
            return CommonUtil.errorJson(UseType.Default.getName() + "类型不能删除");
        }
        sysPermissionServiceI.delete(id);
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/deletePlat")
    @Transactional
    public Object deletePlat(Long id, HttpServletRequest request) throws IOException {
        SysPermission permission = sysPermissionServiceI.get(id);
        sysPermissionServiceI.delete(id);
        return CommonUtil.successJson("操作成功");
    }

}
