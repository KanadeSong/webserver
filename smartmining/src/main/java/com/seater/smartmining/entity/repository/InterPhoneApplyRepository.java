package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.InterPhoneApply;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:50
 */
public interface InterPhoneApplyRepository extends JpaRepository<InterPhoneApply,Long>, JpaSpecificationExecutor<InterPhoneApply> {

    List<InterPhoneApply> findAllByUserObjectIdAndUserObjectType(Long userObjectId, UserObjectType userObjectType);
}
