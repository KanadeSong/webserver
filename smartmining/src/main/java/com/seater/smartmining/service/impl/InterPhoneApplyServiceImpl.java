package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.InterPhoneApplyDaoI;
import com.seater.smartmining.entity.InterPhoneApply;
import com.seater.smartmining.service.InterPhoneApplyServiceI;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:46
 */
@Service
public class InterPhoneApplyServiceImpl implements InterPhoneApplyServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    InterPhoneApplyDaoI interPhoneApplyDaoI;

    @Override
    public InterPhoneApply get(Long id) throws IOException {
        return interPhoneApplyDaoI.get(id);
    }

    @Override
    public InterPhoneApply save(InterPhoneApply log) throws IOException {
        return interPhoneApplyDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        interPhoneApplyDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        interPhoneApplyDaoI.delete(ids);
    }

    @Override
    public Page<InterPhoneApply> query(Pageable pageable) {
        return interPhoneApplyDaoI.query(pageable);
    }

    @Override
    public Page<InterPhoneApply> query() {
        return interPhoneApplyDaoI.query();
    }

    @Override
    public Page<InterPhoneApply> query(Specification<InterPhoneApply> spec) {
        return interPhoneApplyDaoI.query(spec);
    }

    @Override
    public Page<InterPhoneApply> query(Specification<InterPhoneApply> spec, Pageable pageable) {
        return interPhoneApplyDaoI.query(spec, pageable);
    }

    @Override
    public List<InterPhoneApply> getAll() {
        return interPhoneApplyDaoI.getAll();
    }

    @Override
    public List<InterPhoneApply> queryWx(Specification<InterPhoneApply> spec) {
        return interPhoneApplyDaoI.queryWx(spec);
    }

    @Override
    public List<InterPhoneApply> findAllByUserObjectIdAndUserObjectType(Long userObjectId, UserObjectType userObjectType) {
        return interPhoneApplyDaoI.findAllByUserObjectIdAndUserObjectType(userObjectId, userObjectType);
    }
}
