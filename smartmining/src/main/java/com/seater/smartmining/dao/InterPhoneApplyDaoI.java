package com.seater.smartmining.dao;

import com.seater.smartmining.entity.InterPhoneApply;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:48
 */
public interface InterPhoneApplyDaoI {
    InterPhoneApply get(Long id) throws IOException;

    InterPhoneApply save(InterPhoneApply log) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    Page<InterPhoneApply> query();

    Page<InterPhoneApply> query(Specification<InterPhoneApply> spec);

    Page<InterPhoneApply> query(Pageable pageable);

    Page<InterPhoneApply> query(Specification<InterPhoneApply> spec, Pageable pageable);

    List<InterPhoneApply> getAll();

    List<InterPhoneApply> queryWx(Specification<InterPhoneApply> spec);

    List<InterPhoneApply> findAllByUserObjectIdAndUserObjectType(Long userObjectId, UserObjectType userObjectType);
}
