package com.seater.user.config.shiro;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.LoginServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description 微信小程序openId的Realm
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/25 20:01
 */
@Slf4j
public class WxOpenIdRealm extends AuthorizingRealm {

    @Autowired
    LoginServiceI loginServiceI;

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

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
    public  boolean isPermitted(PrincipalCollection principals, String permission){
        SysUser user = (SysUser)principals.getPrimaryPrincipal();
        // 进入项目时设置的项目id
        Long projectId = (Long) SecurityUtils.getSubject().getSession().getAttribute("projectId");
        List<SysUserProjectRole> userProjectRoleList = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(user.getId(), projectId);
        for (SysUserProjectRole userProjectRole : userProjectRoleList) {
            // 平台设置的默认项目里的超级管理员管理员
            if (userProjectRole.getRoleId().equals(Constants.SUPER_USER_ACCOUNT_IN_PROJECT)) {
                return true;
            }
        }
        return super.isPermitted(principals, permission);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // redis中读取权限信息 登陆时set进去的现在拿出来
        String openId = SecurityUtils.getSubject().getPrincipal().toString();       //  微信登陆时用户名就是openId 这里可以直接拿
        SysUser sysUser = sysUserServiceI.getByOpenId(openId);

        if (ObjectUtils.isEmpty(sysUser)){
            return info;
        }

        String userPermission = getValueOps().get(getKey(openId));
        if (userPermission == null) {
            //空值就查一次数据库,再存redis
            JSONObject user = loginServiceI.getInfo(sysUser.getId());
            if (user!= null){
                userPermission = JSONObject.toJSONString(user);
            }
            try {
                getValueOps().set(getKey(openId), userPermission, Constants.SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("保存权限数据到redis失败, openId:{} ,异常:{}", openId, e.getMessage());
                e.printStackTrace();
            }
        } else {
            //  redis续期
            redisTemplate.expire(getKey(openId), Constants.SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        Long projectId = (Long) SecurityUtils.getSubject().getSession().getAttribute("projectId");
        JSONObject permissionObject = JSONObject.parseObject(userPermission);   //  项目内所有权限
        Collection<String> collection_o = new ArrayList<>();
        if (permissionObject != null && projectId != null) {
            //  项目的鉴权数组
//            collection_o = (Collection<String>) permissionObject.get(Constants.PERMISSION_ARRAY);
//            System.out.println(permissionObject.get(Constants.PERMISSION_ARRAY));
            collection_o = (Collection<String>) SearchPermissionUtils.searchPermissionByProject(projectId, permissionObject);
        }
        info.addStringPermissions(collection_o);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String openId = (String) token.getPrincipal();
        //  微信小程序登陆openId登陆
        SysUser sysUser = sysUserServiceI.getByOpenId(openId);
        if (sysUser == null) {
            throw new UnknownAccountException("匹配微信用户失败,用户不存在");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(sysUser, "", this.getClass().getSimpleName());
        //  保存用户信息到session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_INFO, sysUser);
        //  保存用户权限信息到session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_PERMISSION, loginServiceI.getInfo(sysUser.getId()));
        return info;
    }
}
