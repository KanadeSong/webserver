package com.seater.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.user.config.shiro.SearchPermissionUtils;
import com.seater.user.dao.SysUserDaoI;
import com.seater.user.entity.SysPermission;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.manager.RoleManager;
import com.seater.user.service.LoginServiceI;
import com.seater.user.service.SysPermissionServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//import org.jasypt.encryption.StringEncryptor;

@RestController
@RequestMapping("/api/web")
public class LoginController {
    @Autowired
    SysUserDaoI sysUserDaoI;

    @Autowired
    LoginServiceI loginServiceI;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SysPermissionServiceI sysPermissionServiceI;

    @Autowired
    SysUserProjectRoleServiceI userProjectRoleServiceI;

    @Autowired
    RoleManager roleManager;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:userPermission:";

    String getKey(String id) {
        return keyGroup + id.toString();
    }

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = redisTemplate.opsForValue();
        return valueOps;
    }

    //   @Autowired
    //   StringEncryptor stringEncryptor;

    /**
     * 根据所选择项目抽取相应权限
     *
     * @param projectId
     * @param loginType @{com.seater.user.config.shiro.LoginType.getCode()}
     * @param request
     * @return
     */
    @PostMapping("/setProjectId")
    public Object setProjectId(Long projectId, Integer loginType, HttpServletRequest request, HttpSession session) {
        SecurityUtils.getSubject().getSession().setAttribute("projectId", projectId);
        String userPermissionStr;
        JSONObject userPermission = new JSONObject();
        switch (loginType) {
            case 0:
                //  session的可以直接拿到
                userPermission = (JSONObject) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_PERMISSION);
                break;
            case 1:
                //  微信小程序登陆的要去 redis中查找
                SysUser sysUser = (SysUser) SecurityUtils.getSubject().getPrincipal();
                userPermissionStr = getValueOps().get(getKey(sysUser.getOpenId()));
                userPermission = JSONObject.parseObject(userPermissionStr);
                break;
            default:
                userPermissionStr = new JSONObject().toJSONString();
                userPermission = JSONObject.parseObject(userPermissionStr);
                break;
        }
        Collection<String> permissionList = (ArrayList<String>) SearchPermissionUtils.searchPermissionByProject(projectId, userPermission);
        //  如果是管理员,取出所有的权限
        SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
        List<SysUserProjectRole> projectRoleList = userProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(sysUser.getId(), projectId);
        Boolean flag = false;
        for (SysUserProjectRole role : projectRoleList) {
            if (role.getRoleId().equals(Constants.SUPER_USER_ACCOUNT_IN_PROJECT)) {
                flag = true;
            }
        }
        if (sysUser.getAccount().equals(Constants.SUPER_USER_ACCOUNT)) {
            flag = true;
        }
        if (flag) {
            Collection<String> superPermission = new ArrayList<>();
            //  取出所有权限
            List<SysPermission> sysPermissions = sysPermissionServiceI.getAll();
            for (SysPermission sysPermission : sysPermissions) {
                superPermission.add(sysPermission.getPermissionCode());
            }
            //排序设置为0
            SecurityUtils.getSubject().getSession().setAttribute(Constants.ROLE_MIX_SORT, 0L);
            return CommonUtil.successJsonData(superPermission);
        }

        //  不是管理员角色,按照角色的sort进行排序 取最小值的排序(值越小权限越大)
        JSONArray projectList = userPermission.getJSONArray(Constants.PROJECT_LIST);
        JSONArray roleListInProject = new JSONArray();
        for (Object project : projectList) {
            JSONObject projectObject = JSONObject.parseObject(JSONObject.toJSONString(project));
            if (projectObject.getJSONObject(Constants.PROJECT).getLong("id").equals(projectId)) {
                roleListInProject = projectObject.getJSONArray(Constants.ROLE_LIST_IN_PROJECT);
            }
        }
        List<Long> sortArr = new ArrayList<>();

        for (Object role : roleListInProject) {
            SysRole sysRole = JSONObject.parseObject(JSONObject.toJSONString(role), SysRole.class);
            sortArr.add(sysRole.getSort());
        }
        Arrays.sort(sortArr.toArray());
        SecurityUtils.getSubject().getSession().setAttribute(Constants.ROLE_MIX_SORT, sortArr.size() == 0 ? null : sortArr.get(0));

        return CommonUtil.successJsonData(permissionList);
    }

    /**
     * web端,app登陆
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Object Login(String username, String password, HttpSession session) {
        return loginServiceI.login(username, password);
    }

    /**
     * 通用退出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public Object LoginOut(HttpSession session) {
        return loginServiceI.logout();
    }

    /**
     * 微信小程序登陆
     * @param openId
     * @param mobile
     * @return
     */
    @PostMapping("/wxLogin")
    public Object wxLogin(@RequestParam(value = "openId", required = true) String openId, String mobile) {
        return loginServiceI.wxLogin(openId, mobile);
    }

    /**
     * 返回用户openId
     * @param code
     * @return
     * @throws IOException
     */
    @PostMapping("/wxCode")
    public Object wxCode(@RequestParam(value = "code", required = true) String code) throws IOException {
        return loginServiceI.wxCode(code);
    }

    /**
     * 微信小程序注册接口
     * @param sysUser
     * @return
     */
    @PostMapping("/wxRegister")
    public Object wxRegister(@RequestBody(required = true) JSONObject sysUser) {
        return loginServiceI.wxRegister(JSONObject.parseObject(JSONObject.toJSONString(sysUser.get("sysUser")), SysUser.class));
    }

    /**
     * 微信小程序修改用户信息接口
     * @param sysUser
     * @return
     */
    @PostMapping("/wxEdit")
    public Object wxEdit(@RequestBody(required = true) JSONObject sysUser) {
        return loginServiceI.wxEdit(JSONObject.parseObject(JSONObject.toJSONString(sysUser.get("sysUser")), SysUser.class));
    }
}
