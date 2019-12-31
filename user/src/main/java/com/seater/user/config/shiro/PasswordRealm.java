package com.seater.user.config.shiro;

import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.MD5Helper;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.service.LoginServiceI;
import com.seater.user.service.SysPermissionServiceI;
import com.seater.user.service.SysUserProjectRoleServiceI;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 密码认证的Realm
 * @Author xueqichang
 * @Email 87167070@qq.com
 * @Date 2019/1/29 0029 16:18
 */
@Slf4j
public class PasswordRealm extends AuthorizingRealm {

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    SysPermissionServiceI sysPermissionServiceI;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

    @Autowired
    LoginServiceI loginServiceI;

    @Override
    public  boolean isPermitted(PrincipalCollection principals, String permission){
        SysUser user = (SysUser)principals.getPrimaryPrincipal();
        // 进入项目时设置的项目id
        Long projectId = (Long) SecurityUtils.getSubject().getSession().getAttribute("projectId");
        List<SysUserProjectRole> userProjectRoleList = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(user.getId(), projectId);

        //平台端管理员账号
        if (Constants.SUPER_USER_ACCOUNT.equals(user.getAccount())){
            return true;
        }

        //项目端管理员
        for (SysUserProjectRole userProjectRole : userProjectRoleList) {
            // 平台设置的默认项目里的超级管理员管理员
            if (userProjectRole.getRoleId().equals(Constants.SUPER_USER_ACCOUNT_IN_PROJECT)) {
                return true;
            }
        }
        return super.isPermitted(principals, permission);
    }


    /**
     * 鉴权 访问有权限注解的地方会触发
     *
     * @param principals 当前登陆人
     * @return 鉴权结果和权限信息
     */
    @Override
    @SuppressWarnings("unchecked")
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Session session = SecurityUtils.getSubject().getSession();
        Long projectId = (Long) session.getAttribute("projectId");
        //  session中取出权限
        JSONObject permissionObject = (JSONObject) session.getAttribute(Constants.SESSION_USER_PERMISSION);
//        System.out.println("permission的值为:" + permission);
        //  设置权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Collection<String> collection_o = new ArrayList<>();
        if (permissionObject != null && projectId != null) {
            //  项目的鉴权数组
//            collection_o = (Collection<String>) permissionObject.get(Constants.PERMISSION_ARRAY);
//            System.out.println(permissionObject.get(Constants.PERMISSION_ARRAY));
            collection_o = (Collection<String>) SearchPermissionUtils.searchPermissionByProject(projectId, permissionObject);
        }
        authorizationInfo.addStringPermissions(collection_o);
        return authorizationInfo;
    }

    /**
     * 验证
     *
     * @param token 登陆的token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();
        //  拿到账号密码
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        //  用户
        SysUser sysUser = new SysUser();

        //  密码登陆
        sysUser = sysUserServiceI.getByAccountAndPassword(username, MD5Helper.encode(password));
        if (sysUser == null) {
            throw new UnknownAccountException("匹配账号密码失败");
        }
        info = new SimpleAuthenticationInfo(sysUser, token.getCredentials(), getName());
        //  保存用户信息到session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_INFO, sysUser);
        //  保存用户权限信息到session中
        SecurityUtils.getSubject().getSession().setAttribute(Constants.SESSION_USER_PERMISSION, loginServiceI.getInfo(sysUser.getId()));


        return info;
    }
}
