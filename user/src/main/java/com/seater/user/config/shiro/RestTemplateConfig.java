package com.seater.user.config.shiro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Description 配置一个RestTemplate ,主要用来请求微信接口获取openId
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/26 18:17
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时时间为单位为60秒
        factory.setReadTimeout(1000 * 60);
        //连接超时时间设置为10秒
        factory.setConnectTimeout(1000 * 10);                                        
        return factory;
    }
}
