package com.seater.smartmining.controller;

import com.seater.user.entity.SysRole;
import com.seater.user.entity.UseType;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.springframework.beans.BeanUtils;
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

/**
 * 项目端角色操作
 */
@RestController
@RequestMapping("/api/projectRole")
public class ProjectRoleController {

    @Autowired
    SysRoleServiceI sysRoleServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @RequestMapping("/get")
    public Object get(Long id) {
        try {
            SysRole role = sysRoleServiceI.get(id);
            return role;
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, Boolean isAll, String name, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            if (isAll != null && isAll) {
                Specification<SysRole> spec = new Specification<SysRole>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                        list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                        query.orderBy(cb.asc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                return sysRoleServiceI.queryWx(spec);
            }

            Specification<SysRole> spec = new Specification<SysRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!StringUtils.isEmpty(name)) {
                        list.add(cb.equal(root.get("name").as(Long.class), name));
                    }
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    list.add(cb.equal(root.get("useType").as(UseType.class), UseType.Project));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return sysRoleServiceI.queryWx(spec, PageRequest.of(cur, page));
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @RequestMapping("/getInfo")
    public Object getInfo(HttpServletRequest request) {
        return request.getSession().getAttribute(Constants.SESSION_USER_PERMISSION);
    }

    @PostMapping("/save")
    @Transactional
    public Object save(SysRole sysRole, HttpServletRequest request) {
        try {
            Long projectId = CommonUtil.getProjectId(request);
            //修改
            if (sysRole.getId() > 0L) {
                SysRole role = sysRoleServiceI.get(sysRole.getId());
                if (null != role.getUseType() &&
                        UseType.Default.equals(role.getUseType()) ||
                        null != role.getIsDefault() &&
                                role.getIsDefault()) {
                    return CommonUtil.errorJson(UseType.Default.getName() + "类型不能操作");
                } else {
                    BeanUtils.copyProperties(sysRole, role);
                    role.setRoleName(sysRole.getRoleName());
                    role.setRemark(sysRole.getRemark());
                    role.setUpdateTime(new Date());
                    role.setIsDefault(false);
                    role.setProjectId(projectId);
                    role.setSort(sysRole.getSort());
                    role.setValid(sysRole.getValid());
                    role.setUseType(UseType.Project);
                    return CommonUtil.successJsonData(sysRoleServiceI.save(role));
                }
            }

            sysRole.setProjectId(projectId);
            return CommonUtil.successJsonData(sysRoleServiceI.save(sysRole));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Transactional
    public Object update(SysRole sysRole, HttpServletRequest request) throws IOException {
        try {
            SysRole role = sysRoleServiceI.get(sysRole.getId());
            if (null != role.getUseType() &&
                    UseType.Default.equals(role.getUseType()) ||
                    null != role.getIsDefault() &&
                            role.getIsDefault()) {
                return CommonUtil.errorJson(UseType.Default.getName() + "类型不能操作");
            }
            Long projectId = CommonUtil.getProjectId(request);
            role.setRoleName(sysRole.getRoleName());
            role.setUpdateTime(new Date());
            role.setSort(sysRole.getSort());
            role.setProjectId(projectId);
            role.setRemark(sysRole.getRemark());
            sysRoleServiceI.save(role);
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("修改失败 " + e.getMessage());
        }
    }


    @PostMapping("/deletes")
    public Object deletes(@RequestBody List<Long> ids, HttpServletRequest request) throws IOException {
        for (Long id : ids) {
            SysRole role = sysRoleServiceI.get(id);
            if (null != role.getUseType() &&
                    UseType.Default.equals(role.getUseType()) ||
                    null != role.getIsDefault() &&
                            role.getIsDefault()) {
                return CommonUtil.errorJson(UseType.Default.getName() + "类型不能操作", role);
            }
        }
        sysRoleServiceI.delete(ids);
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/delete")
    @Transactional
    public Object delete(Long id, HttpServletRequest request) throws IOException {
        SysRole role = sysRoleServiceI.get(id);
        if (null != role.getUseType() &&
                UseType.Default.equals(role.getUseType()) ||
                null != role.getIsDefault() &&
                        role.getIsDefault()) {
            return CommonUtil.errorJson(UseType.Default.getName() + "类型不能操作");
        }
        sysRoleServiceI.delete(id);
        return CommonUtil.successJson("操作成功");
    }
}
