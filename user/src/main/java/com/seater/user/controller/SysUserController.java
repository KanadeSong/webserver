package com.seater.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.MD5Helper;
import com.seater.user.manager.UserManager;
import com.seater.user.util.aliyun.SmsUtils;
import com.seater.user.util.constants.PermissionConstants;
import com.seater.user.entity.Sex;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserRole;
import com.seater.user.entity.repository.SysUserRepository;
import com.seater.user.service.SysUserRoleServiceI;
import com.seater.user.service.SysUserServiceI;
//import org.jasypt.encryption.StringEncryptor;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/sysUser")
public class SysUserController {
    @Autowired
    SysUserServiceI sysUserServiceI;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SysUserRoleServiceI sysUserRoleServiceI;
    @Autowired
    UserManager userManager;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping("/get")
    public Object get(Long id) {
        try {
            SysUser user = sysUserServiceI.get(id);
            return user;
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/getByAcc")
    public Object getByAcc(String acc) {
        try {
            SysUser user = sysUserRepository.getByAccount(acc);
            return user;
        } catch (Exception e) {
            return "{\"status\":false, \"msg\":" + e.getMessage() + "}";
        }
    }

    @RequestMapping("/getInfo")
    public Object getInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
//        SysUser user = (SysUser) session.getAttribute(Constants.SESSION_USER_INFO);
        JSONObject attribute = (JSONObject) session.getAttribute(Constants.SESSION_USER_PERMISSION);
        return attribute;
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 结果
     */
    @Transactional
    @RequestMapping("/delete")
    public Object delete(@RequestParam(value = "id", required = true) Long id) {
        try {
            if (sysUserServiceI.get(id) != null) {
                sysUserServiceI.delete(id);
                sysUserRoleServiceI.deleteAllByUserId(id);
            } else {
                return CommonUtil.errorJson("用户不存在,操作失败");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
        return CommonUtil.successJson("删除成功");
    }

    /**
     * 失效/生效用户
     *
     * @param id 用户id
     * @return 结果
     */
    @PostMapping("/valid")
    public Object valid(@RequestParam(value = "id", required = true) Long id) {
        try {
            SysUser sysUser = sysUserServiceI.get(id);
            if (sysUser != null) {
                sysUser.setValid(!sysUser.getValid());  //  设置取反即可
                sysUserServiceI.save(sysUser);
            } else {
                return CommonUtil.errorJson("用户不存在,操作失败");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
        return CommonUtil.successJson("操作成功");
    }

    /**
     * 保存/新增用户 包括用户的角色关联 这个属于项目外的用户-角色关系
     */
    @Transactional
    @PostMapping("/save")
    public Object save(@RequestBody JSONObject jsonObject, @RequestParam(name = "id", required = false) Long id) {
        try {
            SysUser sysUser = new SysUser();
            sysUser = JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("sysUser")), SysUser.class);
            List<Long> roleIdList = JSONObject.parseArray(jsonObject.get("roleIdList").toString(), Long.class);
            SysUser userByAccount = sysUserServiceI.getByAccount(sysUser.getAccount());
            if (StringUtils.isEmpty(id)) {  //  新增时id为空
                if (userByAccount != null) {
                    //  新增 且数据库中已存在该用户名
                    return CommonUtil.errorJson("已存在该用户名");
                }
                //  新增用户的密码需要经过加密才能保存到数据库 修改则不用
                // 不提供密码时设置默认密码
                if (StringUtils.isEmpty(sysUser.getPassword())) {
                    sysUser.setPassword(Constants.WX_APP_DEFAULT_USER_PASSWORD);
                }
                if (StringUtils.isEmpty(sysUser.getAccount())) {
                    sysUser.setAccount(sysUser.getMobile());
                }
                sysUser.setPassword(MD5Helper.encode(sysUser.getPassword()));
            }

            sysUser = sysUserServiceI.save(sysUser);    //  保存完成重新赋值,(ID)
            //  删除原有的用户-角色关系列表
            sysUserRoleServiceI.deleteAllByUserId(sysUser.getId());
            //  插入新的用户-角色关系列表
            for (Long roleId : roleIdList) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(sysUser.getId());
                sysUserRoleServiceI.save(sysUserRole);
            }

            return CommonUtil.successJson("操作成功");

        } catch (Exception e) {
            return CommonUtil.errorJson("保存用户失败 " + e.getMessage());
        }
    }

    /**
     * 修改用户
     *
     * @return 结果
     */
    @Transactional
    @PostMapping("/edit")
    public Object edit(@RequestBody SysUser sysUser) {
        try {
            SysUser user = sysUserServiceI.get(sysUser.getId());
            user.setAccount(sysUser.getAccount());
            if (!StringUtils.isEmpty(sysUser.getPassword())) {
                //修改密码
                user.setPassword(MD5Helper.encode(sysUser.getPassword()));
            }
            // 默认取手机号码为账号
            if (StringUtils.isEmpty(user.getAccount())) {
                user.setAccount(sysUser.getMobile());
            }
            user.setMobile(sysUser.getMobile());
            user.setSex(sysUser.getSex());
            sysUserServiceI.save(user);
            return CommonUtil.successJson("操作成功");

        } catch (Exception e) {
            return CommonUtil.errorJson("保存用户失败 " + e.getMessage());
        }
    }

    @PostMapping("/resetPassword")
    public Object resetPassword(Long id, String password, String mobile) {
        try {
            SysUser sysUser = sysUserServiceI.get(id);
            if (sysUser.getMobile().equals(mobile)) {
                String encodePwd = MD5Helper.encode(password);
                sysUser.setPassword(encodePwd);
                sysUserServiceI.save(sysUser);
                return CommonUtil.successJson("操作成功");
            } else {
                return CommonUtil.errorJson("手机号不正确");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @PostMapping("/resetMobile")
    public Object resetMobile(Long id, String moblieOld, String moblieNew) {
        try {
            SysUser sysUser = sysUserServiceI.get(id);
            if (moblieOld.equals(sysUser.getMobile())) {
                sysUser.setMobile(moblieNew);
                sysUserServiceI.save(sysUser);
                return CommonUtil.successJson("操作成功");
            } else {
                return CommonUtil.errorJson("手机号不正确");
            }
        } catch (Exception e) {
            return CommonUtil.errorJson("操作失败 " + e.getMessage());
        }
    }

    @RequestMapping("/query")
    public Object query(Integer current, Integer pageSize, String account, String name, String mobile, Sex sex, Boolean isAll, HttpServletRequest request, Boolean isWx) {
        try {
            if (isAll != null && isAll)
                return sysUserServiceI.getAll();

            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<SysUser> spec = new Specification<SysUser>() {

                @Override
                public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();

                    if (!StringUtils.isEmpty(name)) {
                        list.add(cb.like(root.get("name").as(String.class), "%" + name + "%"));
                    }

                    if (!StringUtils.isEmpty(account)) {
                        list.add(cb.like(root.get("account").as(String.class), "%" + account + "%"));
                    }

                    if (!StringUtils.isEmpty(mobile)) {
                        list.add(cb.like(root.get("mobile").as(String.class), "%" + mobile + "%"));
                    }

                    if (!ObjectUtils.isEmpty(sex)) {
                        list.add(cb.equal(root.get("sex").as(Sex.class), sex));
                    }
                    if (!ObjectUtils.isEmpty(isWx) && isWx){
                        SysUser sysUser = new SysUser();
                        list.add(root.get("openId").isNotNull());
                    }
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));//    没有失效的用户
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));

                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            return sysUserServiceI.query(spec,PageRequest.of(cur,page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    @RequestMapping("/shopUser")
    public Object queryShop(Integer current, Integer pageSize, String openId) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            Specification<SysUser> spec = new Specification<SysUser>() {

                @Override
                public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    if(!ObjectUtils.isEmpty(openId))
                        list.add(cb.like(root.get("openId").as(String.class), "%" + openId + "%"));
                    list.add(root.get("openId").isNotNull());
                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));//    没有失效的用户
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));

                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return new HashMap<String, Object>(){{
                put("status", true);
                put("msg", "请求成功");
                put("data", sysUserServiceI.query(spec,PageRequest.of(cur,page)));
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 车主查自己的司机
     *
     * @param current
     * @param pageSize
     * @param ownerId
     * @return
     */
    @PostMapping("/driverWx")
    public Object driver(Integer current, Integer pageSize, Long ownerId) {

        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Page<Map[]> driverByOwnerIdStatus = sysUserServiceI.getDriverByOwnerIdStatus(ownerId, PageRequest.of(cur, page));
            return driverByOwnerIdStatus;
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 车主查待加入的司机
     *
     * @param current
     * @param pageSize
     * @param ownerId
     * @return
     */
    @PostMapping("/driverTempWx")
    public Object driverTempWx(Integer current, Integer pageSize, Long ownerId) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Page<Map[]> driverByOwnerIdStatus = sysUserServiceI.getDriverByOwnerIdStatusTemp(ownerId, PageRequest.of(cur, page));
            return driverByOwnerIdStatus;
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 项目内的司机
     *
     * @param current
     * @param pageSize
     * @param projectId
     * @return
     */
    @PostMapping("/driverInProWx")
    public Object driverInProWx(Integer current, Integer pageSize, Long projectId) {

        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            return sysUserServiceI.getDriverByProjectId(projectId, PageRequest.of(cur, page));
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    @PostMapping("/saveApp")
    @Transactional
    public Object saveApp(@RequestBody SysUser user) {
        try {
            SysUser sysUser = sysUserServiceI.get(user.getId());
            if (null == sysUser) {
                return CommonUtil.errorJson("用户不存在");
            }
            if (sysUser.account.contains("super")) {
                return CommonUtil.errorJson("权限不足");
            }
            if (sysUser.getName().isEmpty()) {
                return CommonUtil.errorJson("用户名不能为空");
            }
            sysUser.setName(user.getName());
            sysUser.setSex(user.getSex());
            sysUser.setMobile(user.getMobile());
            sysUser.setIdNo(user.getIdNo());
            sysUser.setIccid(user.getIccid());
            sysUserServiceI.save(sysUser);
            return CommonUtil.successJsonData(sysUser);
        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }

    @PostMapping("/savePlat")
    @Transactional
    public Object savePlat(SysUser sysUser , HttpServletRequest request) throws IOException {
        if (userManager.accountExist(sysUser)){
            return CommonUtil.errorJson("当前账号已存在:"+ sysUser.getAccount());
        }
        SysUser user = new SysUser();
        user.setId(null);
        user.setName(sysUser.getName());
        user.setAccount(sysUser.getAccount());
        if (!StringUtils.isEmpty(sysUser.getPassword())){
            user.setPassword(MD5Helper.encode(sysUser.getPassword()));
        }
        user.setMobile(sysUser.getMobile());
        user.setSex(sysUser.getSex());
        SysUser save = sysUserServiceI.save(user);
        return CommonUtil.successJsonData(save);
    }

    @PostMapping("/updatePlat")
    @Transactional
    public Object updatePlat(SysUser sysUser , HttpServletRequest request) throws IOException {
        if (userManager.accountExist(sysUser)){
            return CommonUtil.errorJson("当前账号已存在:"+ sysUser.getAccount());
        }
        SysUser user = sysUserServiceI.get(sysUser.getId());
        user.setMobile(sysUser.getMobile());
        user.setSex(sysUser.getSex());
        user.setIdNo(sysUser.getIdNo());
        user.setAddress(sysUser.getAddress());
        user.setName(sysUser.getName());
        user.setAccount(sysUser.getAccount());
        if (!StringUtils.isEmpty(sysUser.getPassword())){
            user.setPassword(MD5Helper.encode(sysUser.getPassword()));
        }
        user.setMobile(sysUser.getMobile());
        user.setSex(sysUser.getSex());
        SysUser save = sysUserServiceI.save(user);
        return CommonUtil.successJsonData(save);
    }

    @RequestMapping("/modifyBalance")
    public Object modifyBalance(@RequestParam String openId, @RequestParam BigDecimal money) {
        try {
            SysUser user = sysUserServiceI.getByOpenId(openId);
            if (user != null) {
                if (user.getBalance().compareTo(money) > 0) {
                    BigDecimal balance = user.getBalance().subtract(money);
                    user.setBalance(balance);
                    sysUserServiceI.save(user);
                } else {
                    return new HashMap<String, Object>() {{
                        put("status", false);
                        put("msg", "余额不足");
                    }};
                }
            } else {
                return new HashMap<String, Object>() {{
                    put("status", false);
                    put("msg", "用户不存在");
                }};
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap<String, Object>() {{
            put("status", true);
            put("msg", "请求成功");
        }};
    }

}
