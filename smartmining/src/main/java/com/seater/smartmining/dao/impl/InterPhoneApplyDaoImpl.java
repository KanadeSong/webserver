package com.seater.smartmining.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.dao.InterPhoneApplyDaoI;
import com.seater.smartmining.entity.InterPhoneApply;
import com.seater.smartmining.entity.repository.InterPhoneApplyRepository;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:49
 */
@Component
public class InterPhoneApplyDaoImpl implements InterPhoneApplyDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    InterPhoneApplyRepository interPhoneApplyRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhoneApply:";

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
    public Page<InterPhoneApply> query(Specification<InterPhoneApply> spec, Pageable pageable) {
        return interPhoneApplyRepository.findAll(spec, pageable);
    }

    @Override
    public Page<InterPhoneApply> query(Specification<InterPhoneApply> spec) {
        return interPhoneApplyRepository.findAll(spec, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<InterPhoneApply> query(Pageable pageable) {
        return interPhoneApplyRepository.findAll(pageable);
    }

    @Override
    public Page<InterPhoneApply> query() {
        return interPhoneApplyRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public InterPhoneApply get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, InterPhoneApply.class);
        }
        if (interPhoneApplyRepository.existsById(id)) {
            InterPhoneApply log = interPhoneApplyRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return log;
        }

        return null;
    }

    @Override
    public InterPhoneApply save(InterPhoneApply log) throws IOException {
        InterPhoneApply log1 = interPhoneApplyRepository.save(log);
        getValueOps().set(getKey(log1.getId()), JsonHelper.toJsonString(log1), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        interPhoneApplyRepository.deleteById(id);
    }

    @Override
    public List<InterPhoneApply> getAll() {
        return interPhoneApplyRepository.findAll();
    }

    @Override
    public List<InterPhoneApply> queryWx(Specification<InterPhoneApply> spec) {
        return interPhoneApplyRepository.findAll(spec);
    }

    @Override
    public List<InterPhoneApply> findAllByUserObjectIdAndUserObjectType(Long userObjectId, UserObjectType userObjectType) {
        return interPhoneApplyRepository.findAllByUserObjectIdAndUserObjectType(userObjectId, userObjectType);
    }
}
