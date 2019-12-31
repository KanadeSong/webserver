package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysUserDaoI;
import com.seater.user.entity.SysUser;
import com.seater.user.entity.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class SysUserDaoImpl implements SysUserDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysUserRepository userRepository;

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
    public Page<SysUser> query(Example<SysUser> example, Pageable pageable) {
        return userRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysUser> query(Specification<SysUser> spec, Pageable pageable) {
        return userRepository.findAll(spec,pageable);
    }

    @Override
    public Page<SysUser> query(Example<SysUser> example) {
        return userRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<SysUser> query(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<SysUser> query() {
        return userRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysUser get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysUser.class);
        }
        if(userRepository.existsById(id))
        {
            SysUser user = userRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysUser save(SysUser user) throws IOException {
        SysUser user1 = userRepository.save(user);
        getValueOps().set(getKey(user.getId()), JsonHelper.toJsonString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return user1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        userRepository.deleteById(id);
    }

    @Override
    public List<SysUser> getAll() {
        return userRepository.findAll();
    }

    @Override
    public SysUser getByAccount(String account) {
        return userRepository.getByAccount(account);
    }

    @Override
    public List<SysUser> findByAccountAndPassword(String account, String password) {
        return userRepository.findByAccountAndPassword(account, password);
    }

    @Override
    public SysUser getByAccountAndPassword(String account, String password) {
        return userRepository.getByAccountAndPassword(account,password);
    }

    @Override
    public SysUser getByOpenId(String openId) {
        return userRepository.getByOpenId(openId);
    }

    @Override
    public Page<Map[]> getDriverByOwnerIdStatus(Long ownerId, Pageable pageable) {
        return userRepository.getDriverByOwnerIdStatus(ownerId,pageable);
    }

    @Override
    public Page<Map[]> getDriverByOwnerIdStatusTemp(Long ownerId, Pageable pageable) {
        return userRepository.getDriverByOwnerIdStatusTemp(ownerId,pageable);
    }

    @Override
    public Page<SysUser> getDriverByOwnerId(Long ownerId, Pageable pageable) {
        return userRepository.getDriverByOwnerId(ownerId,pageable);
    }

    @Override
    public Page<SysUser> getDriverByProjectId(Long projectId, Pageable pageable) {
        return userRepository.getDriverByProjectId(projectId,pageable);
    }

    @Override
    public List<SysUser> queryWx(Specification<SysUser> spec) {
        return userRepository.findAll(spec);
    }
}
