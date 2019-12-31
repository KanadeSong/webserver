package com.seater.user.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Description 多realm方式登陆时用到
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/26 9:35
 */
public class UserModularRealmAuthenticator extends ModularRealmAuthenticator {
    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        // 强制转换回自定义的 UserToken
        UserToken userToken = (UserToken) authenticationToken;
        // 登录类型
        String loginType = userToken.getLoginType();
        // 所有Realm
        Collection<Realm> realms = getRealms();
        // 登录类型对应的所有Realm
        List<Realm> typeRealms = new ArrayList<>();
        for (Realm realm : realms) {
            //  Realm的类名称必须要和loginType 的value一致,不然可能抛null异常
//            或者这个
// Authentication token of type [class com.seater.user.config.shiro.UserToken] could not be authenticated by any configured realms.  Please ensure that at least one realm can authenticate these tokens
            if (realm.getName().contains(loginType)) {
                typeRealms.add(realm);
            }
        }

        // 判断是单Realm还是多Realm 然后调用父类方法
        if (typeRealms.size() == 1){
            return doSingleRealmAuthentication(typeRealms.get(0), userToken);
        }
        else{
            return doMultiRealmAuthentication(typeRealms, userToken);
        }

    }
}
