package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.InterPhoneGroupDaoI;
import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.entity.repository.InterPhoneGroupRepository;
import com.seater.smartmining.enums.GroupType;
import com.seater.user.dao.GlobalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:39
 */
@Component
public class InterPhoneGroupDaoImpl implements InterPhoneGroupDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    InterPhoneGroupRepository interPhoneGroupRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhoneGroup:";

    String getKey(Long id) {
        return keyGroup + id.toString();
    }

    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }


    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    @Override
    public Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec, Pageable pageable) {
        return interPhoneGroupRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec) {
        return interPhoneGroupRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<InterPhoneGroup> query(Pageable pageable) {
        return interPhoneGroupRepository.findAll(pageable);
    }

    @Override
    public Page<InterPhoneGroup> query() {
        return interPhoneGroupRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public InterPhoneGroup get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, InterPhoneGroup.class);
        }
        if (interPhoneGroupRepository.existsById(id)) {
            InterPhoneGroup log = interPhoneGroupRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public InterPhoneGroup save(InterPhoneGroup log) throws IOException {
        InterPhoneGroup log1 = interPhoneGroupRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        interPhoneGroupRepository.deleteById(id);
    }

    @Override
    public List<InterPhoneGroup> getAll() {
        return interPhoneGroupRepository.findAll();
    }

    @Override
    public List<InterPhoneGroup> queryWx(Specification<InterPhoneGroup> spec) {
        return interPhoneGroupRepository.findAll(spec);
    }

    @Override
    public InterPhoneGroup findBySlagSiteIdAndGroupType(Long slagSiteId, GroupType groupType) {
        return interPhoneGroupRepository.findBySlagSiteIdAndGroupType(slagSiteId, groupType);
    }

    @Override
    public List<InterPhoneGroup> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList) {
        return interPhoneGroupRepository.findAllByProjectIdAndGroupTypeIn(projectId, groupTypeList);
    }

    @Override
    public InterPhoneGroup findByGroupTypeAndProjectId(GroupType groupType, Long projectId) {
        return interPhoneGroupRepository.findByGroupTypeAndProjectId(groupType, projectId);
    }

    @Override
    public InterPhoneGroup findByScheduleId(Long scheduleId) {
        return interPhoneGroupRepository.findByScheduleId(scheduleId);
    }

    @Override
    public InterPhoneGroup findByScheduleIdAndInterPhoneGroupId(Long scheduleId, Long interPhoneGroupId) {
        return interPhoneGroupRepository.findByScheduleIdAndInterPhoneGroupId(scheduleId, interPhoneGroupId);
    }

    @Override
    public InterPhoneGroup findByNameAndGroupType(String name, GroupType groupType) {
        return interPhoneGroupRepository.findByNameAndGroupType(name, groupType);
    }

    @Override
    public InterPhoneGroup findByGroupCodeAndGroupTypeAndProjectId(String groupCode, GroupType groupType, Long projectId) {
        return interPhoneGroupRepository.findByGroupCodeAndGroupTypeAndProjectId(groupCode, groupType, projectId);
    }

}
