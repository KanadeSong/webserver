package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.InterPhone;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/17 15:38
 */
public interface InterPhoneRepository extends JpaRepository<InterPhone,Long>, JpaSpecificationExecutor<InterPhone> {
}
