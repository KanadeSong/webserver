package com.seater.smartmining.quartz;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * @Description 配置Quartz
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/18 12:16
 */
@Slf4j
@Configuration
public class QuartzConfiguration {

    private JobFactory jobFactory;

    public QuartzConfiguration(JobFactory jobFactory) {
        this.jobFactory = jobFactory;
    }

    /**
     * 配置SchedulerFactoryBean
     * <p>
     * 将一个方法产生为Bean并交给Spring容器管理
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        // Spring提供SchedulerFactoryBean为Scheduler提供配置信息,并被Spring容器管理其生命周期
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // 设置自定义Job Factory，用于Spring管理Job bean
        factory.setJobFactory(jobFactory);
        return factory;
    }

    /**
     * 生产scheduler单例
     *
     * @return
     */
    @Bean
    public Scheduler scheduler(SchedulerFactoryBean schedulerFactoryBean) {
        log.info("通过SchedulerFactoryBean生产Quartz的Scheduler单例:{}", JSONObject.toJSONString(schedulerFactoryBean));
        return schedulerFactoryBean.getScheduler();
    }

}
