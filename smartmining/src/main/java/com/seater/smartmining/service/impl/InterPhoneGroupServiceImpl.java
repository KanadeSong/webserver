package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.InterPhoneGroupDaoI;
import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.service.InterPhoneGroupServiceI;
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
 * @Date 2019/8/27 11:27
 */
@Service
public class InterPhoneGroupServiceImpl implements InterPhoneGroupServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    InterPhoneGroupDaoI interPhoneGroupDaoI;

    @Override
    public InterPhoneGroup get(Long id) throws IOException {
        return interPhoneGroupDaoI.get(id);
    }

    @Override
    public InterPhoneGroup save(InterPhoneGroup log) throws IOException {
        return interPhoneGroupDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        interPhoneGroupDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        interPhoneGroupDaoI.delete(ids);
    }

    @Override
    public Page<InterPhoneGroup> query(Pageable pageable) {
        return interPhoneGroupDaoI.query(pageable);
    }

    @Override
    public Page<InterPhoneGroup> query() {
        return interPhoneGroupDaoI.query();
    }

    @Override
    public Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec) {
        return interPhoneGroupDaoI.query(spec);
    }

    @Override
    public Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec, Pageable pageable) {
        return interPhoneGroupDaoI.query(spec, pageable);
    }

    @Override
    public List<InterPhoneGroup> getAll() {
        return interPhoneGroupDaoI.getAll();
    }

    @Override
    public List<InterPhoneGroup> queryWx(Specification<InterPhoneGroup> spec) {
        return interPhoneGroupDaoI.queryWx(spec);
    }

    @Override
    public InterPhoneGroup findBySlagSiteIdAndGroupType(Long slagSiteId, GroupType groupType) {
        return interPhoneGroupDaoI.findBySlagSiteIdAndGroupType(slagSiteId, groupType);
    }

    @Override
    public List<InterPhoneGroup> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList) {
        return interPhoneGroupDaoI.findAllByProjectIdAndGroupTypeIn(projectId, groupTypeList);
    }

    @Override
    public InterPhoneGroup findByGroupTypeAndProjectId(GroupType groupType, Long projectId) {
        return interPhoneGroupDaoI.findByGroupTypeAndProjectId(groupType, projectId);
    }

    @Override
    public InterPhoneGroup findByScheduleId(Long scheduleId) {
        return interPhoneGroupDaoI.findByScheduleId(scheduleId);
    }

    @Override
    public InterPhoneGroup findByScheduleIdAndInterPhoneGroupId(Long scheduleId, Long interPhoneGroupId) {
        return interPhoneGroupDaoI.findByScheduleIdAndInterPhoneGroupId(scheduleId, interPhoneGroupId);
    }

    @Override
    public InterPhoneGroup findByNameAndGroupType(String name, GroupType groupType) {
        return interPhoneGroupDaoI.findByNameAndGroupType(name, groupType);
    }

    @Override
    public InterPhoneGroup findByGroupCodeAndGroupTypeAndProjectId(String groupCode, GroupType groupType, Long projectId) {
        return interPhoneGroupDaoI.findByGroupCodeAndGroupTypeAndProjectId(groupCode, groupType, projectId);
    }

}
