package com.seater.smartmining;

import com.sytech.user.repository.BaseRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.seater.user", "com.seater.smartmining", "com.sytech.user"})
@EnableJpaRepositories(basePackages = {"com.seater.user", "com.seater.smartmining", "com.sytech.user"}, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan(basePackages = {"com.seater.user", "com.seater.smartmining"})
@EnableScheduling
public class SmartminingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartminingApplication.class, args);
    }
}
