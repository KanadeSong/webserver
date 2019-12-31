package com.seater.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.MD5Helper;
import com.seater.user.config.shiro.LoginType;
import com.seater.user.config.shiro.UserToken;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.LoginDaoI;
import com.seater.user.entity.*;
import com.seater.user.entity.repository.SysRoleRepository;
import com.seater.user.entity.repository.SysUserProjectRoleRepository;
import com.seater.user.entity.repository.UProjectRepository;
import com.seater.user.manager.RoleManager;
import com.seater.user.service.LoginServiceI;
import com.seater.user.service.SysRoleServiceI;
import com.seater.user.service.SysUserRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 处理登陆和授权的相关逻辑的Service层实现
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/7 0026 14:04
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginServiceI {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    LoginDaoI loginDaoI;

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    SysUserRoleServiceI sysUserRoleServiceI;

    @Autowired
    SysRoleServiceI sysRoleServiceI;

    @Autowired
    SysUserProjectRoleRepository sysUserProjectRoleRepository;

    @Autowired
    SysRoleRepository sysRoleRepository;

    @Autowired
    UProjectRepository uProjectRepository;

    @Autowired
    LoginServiceI loginServiceI;

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

    @Override
    public SysUser getByAccountAndPassword(String username, String password) {
        return loginDaoI.getByAccountAndPassword(username, password);
    }

    /**
     * 验证登陆表单
     *
     * @param username 用户名
     * @param password 密码
     * @return 登陆结果
     */
    @Override
    public JSONObject login(String username, String password) {

        JSONObject info = new JSONObject();
        Subject currentUser = SecurityUtils.getSubject();

        currentUser.logout();
        currentUser.getSession().removeAttribute(Constants.SESSION_USER_INFO);
        currentUser.getSession().removeAttribute(Constants.SESSION_USER_PERMISSION);

        //  如果当前有人登陆了,先提示退出,避免重复请求数据库
//        if (currentUser.isAuthenticated()) {
//            info.put("status", true);
//            info.put("msg", "当前已登陆,如需切换账号,请先退出");
//            return info;
//        }


        UserToken token = new UserToken(username, password, LoginType.Password);
        try {
            currentUser.login(token);
            JSONObject data = new JSONObject();
//            data.put(Constants.SESSION_USER_INFO,currentUser.getSession().getAttribute(Constants.SESSION_USER_INFO));
            data.put(Constants.SESSION_USER_PERMISSION, currentUser.getSession().getAttribute(Constants.SESSION_USER_PERMISSION));
            info.put("info", data);

            info.put("status", true);
            info.put("msg", "用户名为:" + token.getPrincipal() + "登陆成功");
        } catch (UnknownAccountException uae) {
            info.put("status", false);
            info.put("msg", "用户名为: " + token.getPrincipal() + "的用户账号或者密码不正确 , " + uae.getMessage());
        } catch (IncorrectCredentialsException ice) {
            info.put("status", false);
            info.put("msg", "用户名为：" + token.getPrincipal() + "的用户密码不正确 , " + ice.getMessage());
        } catch (LockedAccountException lae) {
            info.put("status", false);
            info.put("msg", "用户名为：" + token.getPrincipal() + "已被冻结 , " + lae.getMessage());
        } catch (AuthenticationException e) {
            info.put("status", false);
            info.put("msg", "未知错误！" + e.getMessage());
        }
        return info;
    }

    /**
     * 登出动作
     *
     * @return 登出结果
     */
    @Override
    public JSONObject logout() {

        Subject subject = SecurityUtils.getSubject();
        //  执行subject的登出动作
        subject.logout();
        //  清除session中的user信息和权限信息
        subject.getSession().removeAttribute(Constants.SESSION_USER_INFO);
        subject.getSession().removeAttribute(Constants.SESSION_USER_PERMISSION);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", true);
        jsonObject.put("msg", "登出成功");
        return jsonObject;
    }

    /**
     * 查询当前登陆用户的权限等信息
     * 注:角色分项目外(【用户-角色关系】)和项目内(进入项目后【用户-项目-角色关系】)
     * 0.   获取当前用户的session会话
     * 1.   从session中读取登陆时保存的user信息
     * 2.   查询当前用户的角色和权限信息
     * (PS: 分两次查询,第一次查出用户的角色列表,第二次根据查出来的角色列表查询权限,合成1次查询去重会比较困难,暂时无法解决,故采用分步查询方式)
     * 3.   拆分整理成所需格式 (主要是构造一对多的JSON)
     * 4.   封装返回
     *
     * @return 当前登录用户的权限信息
     */
    @Override
    @SuppressWarnings(value = "unchecked")
    public JSONObject getInfo(Long userId) {

        System.out.println(">>>>>>>>>>>>>>>>>>");
        long time = System.currentTimeMillis();
        System.out.println(time);

        //  参考来源:  https://github.com/Heeexy/SpringBoot-Shiro-Vue 注意: 例子写的(用户-角色 => 1-1),本项目修改成 1-多
        //  返回对象
        JSONObject jsonObject = new JSONObject();
        //  0.  获取当前用户的session会话
        Session session = SecurityUtils.getSubject().getSession();
        //  1.  从session中读取登陆时保存的user信息
//        SysUser user = (SysUser) session.getAttribute(Constants.SESSION_USER_INFO);
        SysUser user = new SysUser();
        try {
            user = sysUserServiceI.get(userId);
        } catch (IOException e) {
            return CommonUtil.errorJson("获取用户权限失败," + e.getMessage());
        }
        //  2.  查询当前用户的角色和权限信息
        //  2.1 查询当前登陆用户的角色信息   条件：1.用户id 2.项目id

        JSONObject project = new JSONObject();                              //  单个项目
        List<JSONObject> projectListJSON = new ArrayList<>();               //  项目列表

        //  项目内权限列表去重用到
        Set<JSONObject> jsonObjectSet = new HashSet<>();
        //  最后返回项目的JSON 去重后 + 拆权限和角色列表后的项目对象
        JSONObject projectDistinct = new JSONObject();
        //  最后返回项目的JSON 去重后 + 拆权限和角色列表后的项目对象列表
        List<JSONObject> projectDistinctList = new ArrayList<>();

        //  用户-项目-角色关系
        List<SysUserProjectRole> userProjectRoleList = sysUserProjectRoleRepository.findAllByUserIdAndValid(user.getId(), true);

        //  如果为管理员账号，取出所有数据
//        if (user.getAccount().equals(Constants.SUPER_USER_ACCOUNT) && user.getPassword().equals(Constants.SUPER_USER_PASSWORD)) {
//            userProjectRoleList = sysUserProjectRoleRepository.findAll();
//        }
        boolean isSuperInProject = false;
        if (roleManager.isSuperInProject(user)) {
            isSuperInProject = true;
        }
        //  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        //  必须要有转换对象,因为之后会进行转换....
        if (userProjectRoleList.size() != 0) {
            for (SysUserProjectRole sysUserProjectRole : userProjectRoleList) {
                project = new JSONObject();
                UProject uProject = uProjectRepository.getById(sysUserProjectRole.getProjectId());  //  项目列表
                if (uProject == null) {
                    log.info("项目不存在,项目id:{}", sysUserProjectRole.getProjectId());
                    continue;
                }
//                List<SysRole> rolesInProject = sysRoleRepository.findAllByUserIdAndProjectId(sysUserProjectRole.getUserId(), sysUserProjectRole.getProjectId());                               //    角色列表


//                List<SysUserProjectRole> userProjectRoles = sysUserProjectRoleRepository.findAllByUserIdAndProjectIdAndValidIsTrue(sysUserProjectRole.getUserId(), sysUserProjectRole.getProjectId());
                List<Long> roleIds = new ArrayList<>();

                for (SysUserProjectRole userProjectRole : userProjectRoleList) {
                    if (userProjectRole.getUserId().equals(user.getId()) && userProjectRole.getProjectId().equals(uProject.getId())) {
                        roleIds.add(userProjectRole.getRoleId());
                    }
                }

                //1.根据绑定项目关系拿到角色id
                List<SysRole> rolesInProject = sysRoleRepository.findAllByIdIsIn(roleIds);

                List<Long> roleListId = new ArrayList<>();

//                if (roleManager.isSuperInProject(sysUserProjectRole.getRoleId())) {
//                    rolesInProject = sysRoleServiceI.findCommonByProjectIdAndUseType(sysUserProjectRole.getProjectId(), UseType.Default);
//                }
                //  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                //2.*****提取父角色*****
                for (SysRole sysRole : rolesInProject) {
                    roleListId.add(sysRole.getId());
                    if (null != sysRole.getParentId()) {
                        roleListId.add(sysRole.getParentId());
                    }
                }
                List<SysPermission> permissionInProject = new ArrayList<>();
                if (roleListId.size() != 0) {
                    //3.查出权限
                    permissionInProject = loginDaoI.getUserPermissionByRoleIds(roleListId);
                }

                //超级管理员就拿全部
                if (isSuperInProject) {
                    permissionInProject = roleManager.getAllPermission();
                }


                project.put(Constants.PROJECT, uProject);                                   //  项目列表
                project.put(Constants.ROLE_LIST_IN_PROJECT, rolesInProject);                //  项目内角色列表,
                project.put(Constants.PERMISSION_LIST_IN_PROJECT, permissionInProject);     //  项目内权限

                projectListJSON.add(project);                                               //  加进去项目列表,同项目多角色时这个项目列表重复属于正常,存在三元关系
            }

            //  取出项目列表去重(对整个项目数组去重)
            jsonObjectSet = new HashSet<>();
            jsonObjectSet.addAll(projectListJSON);

            //  重新整理每个项目
            List<String> menuList = new ArrayList<>();
            List<String> permissionArray = new ArrayList<>();


            try {
                List<JSONObject> jsonObjectList = (List<JSONObject>) JSONObject.toJSON(jsonObjectSet);
                for (JSONObject jsonObject1 : jsonObjectList) {

                    Set<String> menuSetInProject = new HashSet<>();

                    projectDistinct = new JSONObject();
                    menuList = new ArrayList<>();
                    permissionArray = new ArrayList<>();

                    //  _d >>>>> distinct
                    //  取出项目
                    UProject project_d = (UProject) jsonObject1.get(Constants.PROJECT);
                    //  取出项目中角色列表
                    List<SysRole> roleList_d = (List<SysRole>) jsonObject1.get(Constants.ROLE_LIST_IN_PROJECT);
                    //  取出项目中权限列表
                    List<SysPermission> permission_d = (List<SysPermission>) jsonObject1.get(Constants.PERMISSION_LIST_IN_PROJECT);

                    //  遍历 抽取权限值和菜单等东西,后续需要什么 大概在这加就行了,然后整合回去
                    for (SysPermission permission : permission_d) {
                        menuList.add(permission.getMenuCode());
                        permissionArray.add(permission.getPermissionCode());
                    }
                    menuSetInProject.addAll(menuList);  //  去重

                    //  项目回填
                    projectDistinct.put(Constants.PROJECT, project_d);
                    //  角色列表回填
                    projectDistinct.put(Constants.ROLE_LIST_IN_PROJECT, roleList_d);
                    //  菜单列表回填
                    projectDistinct.put(Constants.MENU_LIST_IN_PROJECT, menuSetInProject);
                    //  权限列表回填
                    projectDistinct.put(Constants.PERMISSION_LIST_IN_PROJECT, permission_d);
                    //  后台接口验证数组回填
                    projectDistinct.put(Constants.PERMISSION_ARRAY_IN_PROJECT, permissionArray);
                    //  加进去项目列表
                    projectDistinctList.add(projectDistinct);
                }

            } catch (Exception e) {
                log.error("用户id:" + user.getId() + "解析JSON出错,抽取权限等信息失败...{}", e.getMessage());
                throw new ClassCastException("解析JSON出错...");
            }
        }
        System.out.println(">>>>>>>>>>>>>>>>>>");
        long time1 = System.currentTimeMillis();
        System.out.println(time1 - time);
        System.out.println("query cost time");
//        log.info(JSONObject.toJSONString(projectListJSON, SerializerFeature.DisableCircularReferenceDetect));

        //  用户-角色关系
        List<SysRole> sysRoleList = loginDaoI.getUserRolesByUserId(user.getId());

        //  >>>>>如果当前用户为管理员账号,在MyShiroRealm.doGetAuthenticationInfo 验证通过证明已经是登陆了管理员的账号<<<<<
        if (user.getAccount().equals(Constants.SUPER_USER_ACCOUNT) && user.getPassword().equals(Constants.SUPER_USER_PASSWORD)) {
            log.info("当前账号为超级管理员账号:{}", Constants.SUPER_USER_ACCOUNT);
            sysRoleList = loginDaoI.getAllRoles();  //  管理员有全部角色
        }

        List<Long> roleIdList = new ArrayList<>();                  //  项目外角色id列表
        List<String> permissionList = new ArrayList<>();            //  权限列表
        List<String> routeList = new ArrayList<>();                 //  前端路由列表
        Set<String> set = new HashSet<>();                          //  去重后前端路由返回
        List<SysPermission> sysPermissionList = new ArrayList<>();  //  项目内权限

        //  3.  拆分整理成所需格式 (主要是构造一对多的JSON),便于对接口权限认证 >>>>> permissionList
        if (sysRoleList.size() != 0) {
            for (SysRole sysRole : sysRoleList) {
                roleIdList.add(sysRole.getId());
            }

            //  2.2 根据当前用户的角色列表查询权限信息
            sysPermissionList = loginDaoI.getUserPermissionByRoleIds(roleIdList);
        }
        if (sysPermissionList.size() != 0) {
            for (SysPermission sysPermission : sysPermissionList) {
                permissionList.add(sysPermission.getPermissionCode());
                routeList.add(sysPermission.getMenuCode());
            }
            set.addAll(routeList);  //  利用set的特性去重
        }

        //  4.  封装成JSON对象返回

        jsonObject.put(Constants.MENU_LIST, set);                           //  菜单列表
        jsonObject.put(Constants.PERMISSION_ARRAY, permissionList);         //  权限列表 一对多,数组 主要在后台接口验证用到
        jsonObject.put(Constants.PERMISSION_LIST, sysPermissionList);       //  权限列表 完整,无去重 返回前端
        jsonObject.put(Constants.ROLE_LIST, sysRoleList);                   //  角色列表
        jsonObject.put(Constants.PROJECT_LIST, projectDistinctList);        //  项目列表
        jsonObject.put(Constants.SESSION_USER_INFO, user);                  //  用户信息

        //  5. 打印验证
//        System.out.println(jsonObject);
        System.out.println("整理时间");
        System.out.println(System.currentTimeMillis() - time1);
        return jsonObject;
    }

    /**
     * 微信登陆
     *
     * @param openId 用户openId
     * @return 登陆结果
     */
    @Override
    @Transactional
    public JSONObject wxLogin(String openId, String mobile) {
        JSONObject info = new JSONObject();
        Subject currentUser = SecurityUtils.getSubject();
        String result = "";
//        if (currentUser.isAuthenticated()) {
//            info.put("status", true);
//            info.put("msg", "当前已登陆,如需切换账号,请先退出");
//            return info;
//        }

        //  数据库中查找
        SysUser sysUser = new SysUser();
        sysUser = sysUserServiceI.getByOpenId(openId);
        //  数据库里面没有, 新增用户
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setMobile(mobile);
            sysUser.setAddTime(new Date());
            sysUser.setOpenId(openId);
            sysUser.setPassword(MD5Helper.encode(Constants.WX_APP_DEFAULT_USER_PASSWORD));
            if (StringUtils.isEmpty(sysUser.getMobile())) {
                //  没手机号码就用UUID
                sysUser.setAccount(UUID.randomUUID().toString());
            } else {
                sysUser.setAccount(mobile);
            }
            sysUser.setSex(Sex.Unknow);
            sysUser.setPassword("");
            sysUser.setValid(true);

            try {
                sysUser = sysUserServiceI.save(sysUser);
            } catch (IOException e) {
                return CommonUtil.errorJson("新增用户失败," + e.getMessage());
            }

            //  添加默认角色
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(Constants.WX_APP_DEFAULT_ROLE);
            sysUserRole.setUserId(sysUser.getId());
            sysUserRole.setAddTime(new Date());
            sysUserRole.setValid(true);
            try {
                sysUserRoleServiceI.save(sysUserRole);
            } catch (IOException e) {
                result = " 用户id:" + sysUser.getId() + ",添加用户角色失败," + e.getMessage();
                log.warn(result);
            }
        }

        UserToken token = new UserToken(sysUser.getOpenId(), LoginType.Wx);

        try {
            currentUser.login(token);
            //  拉取权限信息
            JSONObject permissionInfo = loginServiceI.getInfo(sysUser.getId());
            info.put("status", true);
            info.put("msg", "登陆成功" + result);
            info.put("info", permissionInfo);
            //  删除
//            getValueOps().getOperations().delete(getKey(sysUser.getOpenId()));
            //  保存
//            System.out.println(JSONObject.toJSONString(permissionInfo));
            getValueOps().set(getKey(sysUser.getOpenId()), JSONObject.toJSONString(permissionInfo), Constants.SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            info.put("status", false);
            info.put("msg", e.getMessage());
        }

        return info;
    }

    /**
     * 微信注册
     *
     * @param sysUser 用户表单
     * @return 返回结果
     */
    @Override
    @Transactional
    public JSONObject wxRegister(SysUser sysUser) {
        try {
            if (sysUser.getOpenId() == null || sysUser.getOpenId().equals("")) {
                return CommonUtil.errorJson("新增用户失败,openId不能为空");
            }
            if (sysUser.getMobile().equals("") || sysUser.getMobile() == null) {
                return CommonUtil.errorJson("新增用户失败,手机号不能为空");
            }
            SysUser byOpenId = sysUserServiceI.getByOpenId(sysUser.getOpenId());
            byOpenId.setMobile(sysUser.getMobile());
            sysUserServiceI.save(byOpenId);
            return CommonUtil.successJson("注册成功");
        } catch (Exception e) {
            return CommonUtil.errorJson("注册失败 " + e.getMessage());
        }
    }

    @Override
    public JSONObject wxEdit(SysUser sysUser) {
        try {
            if (sysUser.getOpenId() == null || sysUser.getOpenId().isEmpty()) {
                return CommonUtil.errorJson("修改用户失败,openId不能为空");
            }
            if (sysUser.getName().equals("") || sysUser.getName() == null) {
                return CommonUtil.errorJson("修改用户失败,名称不能为空");
            }
            if (sysUser.getAddress().equals("") || sysUser.getAddress() == null) {
                return CommonUtil.errorJson("修改用户失败,地址不能为空");
            }
            if (sysUser.getSex().equals("") || sysUser.getSex() == null) {
                return CommonUtil.errorJson("修改用户失败,性别不能为空");
            }
            SysUser byOpenId = sysUserServiceI.getByOpenId(sysUser.getOpenId());
            if (byOpenId != null) {
                byOpenId.setName(sysUser.getName());
                byOpenId.setSex(sysUser.getSex());
                byOpenId.setAddress(sysUser.getAddress());
                byOpenId.setAvatar(sysUser.getAvatar());
                byOpenId.setIdNo(sysUser.getIdNo());
                sysUserServiceI.save(byOpenId);
                return CommonUtil.successJson("操作成功");
            } else {
                return CommonUtil.errorJson("用户不存在");
            }

        } catch (Exception e) {
            return CommonUtil.errorJson("修改用户失败 " + e.getMessage());
        }
    }

    /**
     * 根据用户code获取用户微信openId
     *
     * @param code 用户code
     * @return
     */
    @Override
    public String wxCode(String code) {
        String path = Constants.WX_OPEN_API + "?appid=" + Constants.WX_APP_ID + "&secret=" + Constants.WX_APP_SECRET + "&js_code=" + code;
        String response = "";
        try {
            response = restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<String>(new HttpHeaders()), String.class).getBody();
        } catch (Exception e) {
            response = e.getMessage();
        }
        return response;
    }

}
