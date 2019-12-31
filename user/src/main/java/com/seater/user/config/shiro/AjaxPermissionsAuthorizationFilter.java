package com.seater.user.config.shiro;

import com.alibaba.fastjson.JSONObject;
import com.seater.user.util.constants.ErrorEnum;
//import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @Description: 在访问controller前判断是否登录，返回json，不进行重定向。对没有登录的请求进行拦截, 全部返回json信息. 覆盖掉shiro原本的跳转login.jsp的拦截方式
 * @Author xueqichang
 * @Email 87167070@qq.com
 * @Date 2019/1/29 0029 16:18
 */
public class AjaxPermissionsAuthorizationFilter extends FormAuthenticationFilter {

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("code", ErrorEnum.E_20011.getErrorCode());
		jsonObject.put("msg", ErrorEnum.E_20011.getErrorMsg());
		jsonObject.put("status", false);
		PrintWriter out = null;
		HttpServletResponse res = (HttpServletResponse) response;
		try {
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json");
			res.setStatus(401);		//	状态设401未认证
			out = response.getWriter();
			out.println(jsonObject);
		} catch (Exception e) {
		} finally {
			if (null != out) {
				out.flush();
				out.close();
			}
		}
		return false;
	}

	@Bean
	public FilterRegistrationBean registration(AjaxPermissionsAuthorizationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}
	
	
//	暂时不用这种方法
//	@Override
//	protected AuthenticationToken createToken(String username, String password, ServletRequest request, ServletResponse response) {
//		boolean rememberMe = isRememberMe(request);
//		String host = getHost(request);
//		String loginType = LoginType.Password.getValue();
//
//		if(request.getParameter("loginType")!=null && !"".equals(request.getParameter("loginType").trim())){
//			loginType = request.getParameter("loginType");
//		}
//
//		return new UserToken(username, password,loginType,rememberMe,host);
//	}
//
//	public LoginType getLoginTypeParamName() {
//		return loginTypeParamName;
//	}
//
//	public void setLoginTypeParamName(LoginType loginTypeParamName) {
//		this.loginTypeParamName = loginTypeParamName;
//	}
}
