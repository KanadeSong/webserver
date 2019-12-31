package com.seater.user.config.shiro;

import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

/**
 * @Description: shiro 的配置类
 * @Author xueqicahng
 * @Email 87167070@qq.com
 * @Date 2019/1/29 0029 16:18
 */
@Slf4j
@Configuration
public class ShiroConfig {

    /**
     * 负责ShiroBean的生命周期
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    /**
     * session管理器
     */
    @Bean(name = "sessionManager")
    public SessionManager sessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //设置session过期时间(单位：毫秒)
        sessionManager.setGlobalSessionTimeout(Constants.SESSION_TIMEOUT);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     *  账号密码登陆使用的Realm
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public PasswordRealm shiroRealm(){
        //  自定义的Realm
        PasswordRealm passwordRealm = new PasswordRealm();
        //realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return passwordRealm;
    }

    /**
     * 微信小程序登陆使用的Realm
     * @return
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public WxOpenIdRealm wxOpenIdRealm(){
        WxOpenIdRealm wxOpenIdRealm = new WxOpenIdRealm();
        return wxOpenIdRealm;
    }

    /**
     * 系统自带的Realm管理，主要针对多realm
     * @return
     */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator(){
        //  创建自定义的 ModularRealmAuthenticator
        UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
        //  只要有一个成功就视为登录成功
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        //  设置成多Realm
        Collection<Realm> realms = new ArrayList<>();
        realms.add(shiroRealm());
        realms.add(wxOpenIdRealm());
//        System.out.println("检查  >>>>>   " + shiroRealm().toString());
        modularRealmAuthenticator.setRealms(realms);
        return modularRealmAuthenticator;
    }

    /** 安全管理器
     * 将realm加入securityManager
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        //注意是DefaultWebSecurityManager！！！
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
//        securityManager.setRealm(shiroRealm());
        Collection<Realm> realms = new ArrayList<>();
        realms.add(shiroRealm());
        realms.add(wxOpenIdRealm());
        securityManager.setRealms(realms);
//        System.out.println("检查  >>>>>   " + shiroRealm().toString());
        securityManager.setAuthenticator(modularRealmAuthenticator());
        return securityManager;
    }

    /** shiro filter 工厂类
     * 1.定义ShiroFilterFactoryBean
     * 2.设置SecurityManager
     * 3.配置拦截器
     * 4.返回定义ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        //1 Shiro的核心安全接口,这个属性是必须的
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //2 注册securityManager 不知道为什么,security会报错出红字,用其继承倒是没问题
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        
//        //3.设置自定义拦截器,认证失败时返回JSON 而非重定向  注: key值必须为authc
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new AjaxPermissionsAuthorizationFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        //3.拦截器+配置登录和登录成功之后的url
        //LinkHashMap是有序的，shiro会根据添加的顺序进行拦截
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //配置不会被拦截的连接  这里顺序判断
        //anon，所有的url都可以匿名访问
        //authc：所有url都必须认证通过(登陆)才可以访问
        //user，配置记住我或者认证通过才能访问
        //logout，退出登录
        //配置退出过滤器
//        addInterceptor.excludePathPatterns("/error");
//        addInterceptor.excludePathPatterns("/api/web/login**");
//        addInterceptor.excludePathPatterns("/api/sysUser/save");
        filterChainDefinitionMap.put("/error","anon");
        filterChainDefinitionMap.put("/api/web/**","anon");
        filterChainDefinitionMap.put("/api/projectWechatPay/**","anon");
        filterChainDefinitionMap.put("/api/v1/projectWxOrder/**","anon");
        filterChainDefinitionMap.put("/api/wxOption/checkMobile","anon");
        filterChainDefinitionMap.put("/api/interPhone/**","anon");
        filterChainDefinitionMap.put("/api/version/**","anon");
        filterChainDefinitionMap.put("/api/projecterrorlog/**","anon");
        filterChainDefinitionMap.put("/api/sysUser/**","authc");

        //过滤连接自定义，从上往下顺序执行，所以用LinkHashMap /**放在最下边
        filterChainDefinitionMap.put("/api/**","authc");
        filterChainDefinitionMap.put("/**","authc");
        //设置登录界面，如果不设置为寻找web根目录下的文件
        //设置登录成功后要跳转的连接
//        shiroFilterFactoryBean.setSuccessUrl("/success");
        //设置登录未成功，也可以说无权限界面
//        shiroFilterFactoryBean.setUnauthorizedUrl("/testLogin/unauthc");
        //登陆页面
//        shiroFilterFactoryBean.setLoginUrl("/api/web/login");     
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        log.info(">>>>>>>>>>Shiro拦截工厂注入成功<<<<<<<<<<");
        
        //4
        //返回
        return shiroFilterFactoryBean;
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param defaultWebSecurityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager defaultWebSecurityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
