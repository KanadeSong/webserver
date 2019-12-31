package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.InterPhoneMemberDaoI;
import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.entity.repository.InterPhoneMemberRepository;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.utils.interPhone.UserObjectType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:27
 */
@Component
public class InterPhoneMemberDaoImpl implements InterPhoneMemberDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    InterPhoneMemberRepository interPhoneMemberRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhoneMember:";

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
    public Page<InterPhoneMember> query(Specification<InterPhoneMember> spec, Pageable pageable) {
        return interPhoneMemberRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InterPhoneMember> query(Specification<InterPhoneMember> spec) {
        return interPhoneMemberRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<InterPhoneMember> query(Pageable pageable) {
        return interPhoneMemberRepository.findAll(pageable);
    }

    @Override
    public Page<InterPhoneMember> query() {
        return interPhoneMemberRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public InterPhoneMember get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, InterPhoneMember.class);
        }
        if (interPhoneMemberRepository.existsById(id)) {
            InterPhoneMember log = interPhoneMemberRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public InterPhoneMember save(InterPhoneMember log) throws IOException {
        InterPhoneMember log1 = interPhoneMemberRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        interPhoneMemberRepository.deleteById(id);
    }

    @Override
    public List<InterPhoneMember> getAll() {
        return interPhoneMemberRepository.findAll();
    }

    @Override
    public List<InterPhoneMember> queryWx(Specification<InterPhoneMember> spec) {
        return interPhoneMemberRepository.findAll(spec);
    }

    @Override
    public void deleteAllByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneMemberRepository.deleteAllByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public void deleteAllFixByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneMemberRepository.deleteAllFixByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public List<InterPhoneMember> findAllByInterPhoneGroupId(Long interPhoneGroupId) {
        return interPhoneMemberRepository.findAllByInterPhoneGroupId(interPhoneGroupId);
    }

    @Override
    public void batchSave(List<InterPhoneMember> interPhoneMemberList) {
        interPhoneMemberRepository.saveAll(interPhoneMemberList);
    }

    @Override
    public List<InterPhoneMember> findAllByProjectId(Long projectId) {
        return interPhoneMemberRepository.findAllByProjectId(projectId);
    }

    @Override
    public List<InterPhoneMember> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList) {
        if (groupTypeList.size() == 0) {
            return new ArrayList<>();
        }
        List<Integer> type = new ArrayList<>();
        for (GroupType groupType : groupTypeList) {
            type.add(groupType.ordinal());
        }
        return interPhoneMemberRepository.findAllByProjectIdAndGroupTypeIn(projectId, type);
    }

    @Override
    public void deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(Long interPhoneGroupId, Long userObjectId, UserObjectType userObjectType) {
        interPhoneMemberRepository.deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(interPhoneGroupId, userObjectId, userObjectType);
    }

    @Override
    public void deleteAllByScheduleId(Long scheduleId) {
        interPhoneMemberRepository.deleteAllByScheduleId(scheduleId);
    }


}
