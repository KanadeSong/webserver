package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysUserRoleDaoI;
import com.seater.user.entity.SysUserRole;
import com.seater.user.entity.repository.SysUserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SysUserRoleDaoImpl implements SysUserRoleDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysUserRoleRepository sysUserRoleRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:user:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<SysUserRole> query(Example<SysUserRole> example, Pageable pageable) {
        return sysUserRoleRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysUserRole> query(Example<SysUserRole> example) {
        return sysUserRoleRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<SysUserRole> query(Pageable pageable) {
        return sysUserRoleRepository.findAll(pageable);
    }

    @Override
    public Page<SysUserRole> query() {
        return sysUserRoleRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysUserRole get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysUserRole.class);
        }
        if(sysUserRoleRepository.existsById(id))
        {
            SysUserRole user = sysUserRoleRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysUserRole save(SysUserRole sysUserRole) throws IOException {
        SysUserRole user1 = sysUserRoleRepository.save(sysUserRole);
        getValueOps().set(getKey(sysUserRole.getId()), JsonHelper.toJsonString(sysUserRole), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return user1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        sysUserRoleRepository.deleteById(id);
    }

    @Override
    public List<SysUserRole> getAll() {
        return sysUserRoleRepository.findAll();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        sysUserRoleRepository.deleteAllByUserId(userId);
    }


}
