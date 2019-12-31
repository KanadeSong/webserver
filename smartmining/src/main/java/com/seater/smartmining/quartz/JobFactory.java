package com.seater.smartmining.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * @Description 让@Autowired注入生效
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/18 17:25
 */
@Component
public class JobFactory extends SpringBeanJobFactory {
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法    
        Object jobInstance = super.createJobInstance(bundle);
        //进行注入    
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
