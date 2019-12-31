package com.seater.user.controller;

import com.seater.user.entity.UseType;
import com.seater.user.entity.SysRole;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
import org.hibernate.query.criteria.internal.predicate.ExistsPredicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sysRole")
public class SysRoleController {

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
    public Object query(Long projectId, Integer current, Integer pageSize, Boolean isAll, String name, HttpServletRequest request, @RequestParam(required = false, defaultValue = "false") Boolean showChildren, String roleName) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<SysRole> spec = new Specification<SysRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!ObjectUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    }
                    //平台端要默认类型
                    list.add(cb.equal(root.get("useType").as(UseType.class), UseType.Default));
                    if (!ObjectUtils.isEmpty(roleName)) {
                        list.add(cb.equal(root.get("roleName").as(String.class), roleName));
                    }
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    //没有父id就是父角色,最好先屏蔽掉子角色
                    if (!showChildren) {
                        list.add(cb.isNull(root.get("parentId")));
                    }
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            if (isAll != null && isAll) {
                return sysRoleServiceI.getAll();
            }
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
    public Object save(SysRole sysRole, HttpServletRequest request) {
        try {
            if (StringUtils.isEmpty(sysRole.getDefaultName())) {
                sysRole.setDefaultName(sysRole.getRoleName());
            }
            sysRole.setUseType(UseType.Default);
            SysRole save = sysRoleServiceI.save(sysRole);
            //根据父id更新子角色名称
            sysRoleServiceI.updateAllRoleNameByParentId(save.getRoleName(), save.getId());
            return "{\"status\":true}";
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @PostMapping("/update")
    @Transactional
    public Object update(SysRole sysRole, HttpServletRequest request) throws IOException {

        try {
            SysRole role = sysRoleServiceI.get(sysRole.getId());
            if (role == null) {
                return CommonUtil.errorJson("不存在该角色,修改失败");
            }
            if (!StringUtils.isEmpty(sysRole.getRemark())) {
                role.setRemark(sysRole.getRemark());
            }
            role.setRoleName(sysRole.getRoleName());
            role.setUpdateTime(new Date());
            role.setSort(sysRole.getSort());
            role.setProjectId(role.getProjectId());
            if (!StringUtils.isEmpty(sysRole.getDefaultName())) {
                sysRole.setDefaultName(sysRole.getRoleName());
            }
            SysRole save = sysRoleServiceI.save(role);
            //根据父id更新子角色名称
            Specification<SysRole> spec = (r, q, cb) -> {
                return cb.equal(r.get("parentId").as(Long.class), save.getId());
            };
            List<SysRole> subRole = sysRoleServiceI.queryWx(spec);
            for (SysRole sub : subRole) {
                sub.setRemark(save.getRemark());
                sub.setRoleName(save.getRoleName());
                sub.setUpdateTime(save.getUpdateTime());
                sub.setSort(save.getSort());
                sub.setValid(save.getValid());
            }
            return CommonUtil.successJson("操作成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("修改失败 " + e.getMessage());
        }
    }


    @PostMapping("/deletes")
    public Object deletes(@RequestBody List<Long> ids, HttpServletRequest request) throws IOException {
        for (Long id : ids) {
            SysRole role = sysRoleServiceI.get(id);
            if (role.getUseType().equals(UseType.Default)) {
                return CommonUtil.errorJson(UseType.Default.getName() + "类型不能删除", role);
            }
        }

        for (Long id : ids) {
            sysRoleServiceI.delete(id);
        }
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/delete")
    @Transactional
    public Object delete(Long id, HttpServletRequest request) throws IOException {
        SysRole role = sysRoleServiceI.get(id);
        if (role.getUseType().equals(UseType.Default)) {
            return CommonUtil.errorJson(UseType.Default.getName() + "类型不能删除");
        }
        sysRoleServiceI.delete(id);
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/deletePlat")
    @Transactional
    public Object deletePlat(Long id, HttpServletRequest request) throws IOException {
        SysRole role = sysRoleServiceI.get(id);
        sysRoleServiceI.deleteAllByParentId(id);
        sysRoleServiceI.delete(id);
        return CommonUtil.successJson("操作成功");
    }

    @PostMapping("/batchDeletePlat")
    @Transactional
    public Object batchDeletePlat(@RequestBody List<Long> ids, HttpServletRequest request) {
        sysRoleServiceI.delete(ids);
        for (Long id : ids) {
            sysRoleServiceI.deleteAllByParentId(id);
        }
        return CommonUtil.successJson("操作成功");
    }
}
