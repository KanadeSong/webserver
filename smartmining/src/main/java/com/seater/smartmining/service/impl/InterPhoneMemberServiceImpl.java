package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.InterPhoneMemberDaoI;
import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.service.InterPhoneMemberServiceI;
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
 * @Date 2019/8/27 11:32
 */
@Service
public class InterPhoneMemberServiceImpl implements InterPhoneMemberServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    InterPhoneMemberDaoI interPhoneMemberDaoI;

    @Override
    public InterPhoneMember get(Long id) throws IOException {
        return interPhoneMemberDaoI.get(id);
    }

    @Override
    public InterPhoneMember save(InterPhoneMember log) throws IOException {
        return interPhoneMemberDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        interPhoneMemberDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        interPhoneMemberDaoI.delete(ids);
    }

    @Override
    public Page<InterPhoneMember> query(Pageable pageable) {
        return interPhoneMemberDaoI.query(pageable);
    }

    @Override
    public Page<InterPhoneMember> query() {
        return interPhoneMemberDaoI.query();
    }

    @Override
    public Page<InterPhoneMember> query(Specification<InterPhoneMember> spec) {
        return interPhoneMemberDaoI.query(spec);
    }

    @Override
    public Page<InterPhoneMember> query(Specification<InterPhoneMember> spec, Pageable pageable) {
        return interPhoneMemberDaoI.query(spec, pageable);
    }

    @Override
    public List<InterPhoneMember> getAll() {
        return interPhoneMemberDaoI.getAll();
    }

    @Override
    public List<InterPhoneMember> queryWx(Specification<InterPhoneMember> spec) {
        return interPhoneMemberDaoI.queryWx(spec);
    }

    @Override
    public void deleteAllByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneMemberDaoI.deleteAllByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public void deleteAllFixByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneMemberDaoI.deleteAllFixByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public List<InterPhoneMember> findAllByInterPhoneGroupId(Long interPhoneGroupId) {
        return interPhoneMemberDaoI.findAllByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public void batchSave(List<InterPhoneMember> interPhoneMemberList) {
        interPhoneMemberDaoI.batchSave(interPhoneMemberList);
    }

    @Override
    public List<InterPhoneMember> findAllByProjectId(Long projectId) {
        return interPhoneMemberDaoI.findAllByProjectId(projectId);
    }

    @Override
    public void deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(Long interPhoneGroupId, Long userObjectId, UserObjectType userObjectType) {
        interPhoneMemberDaoI.deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(interPhoneGroupId, userObjectId, userObjectType);
    }

    @Override
    public void deleteAllByScheduleId(Long scheduleId) {
        interPhoneMemberDaoI.deleteAllByScheduleId(scheduleId);
    }
}
