package com.seater.user.dao.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysPermissionDaoI;
import com.seater.user.entity.SysPermission;
import com.seater.user.entity.repository.SysPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class SysPermissionDaoImpl implements SysPermissionDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysPermissionRepository sysPermissionRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:permission:";

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
    public Page<SysPermission> query(Example<SysPermission> example, Pageable pageable) {
        return sysPermissionRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysPermission> query(Example<SysPermission> example) {
        return sysPermissionRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<SysPermission> query(Pageable pageable) {
        return sysPermissionRepository.findAll(pageable);
    }

    @Override
    public Page<SysPermission> query() {
        return sysPermissionRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysPermission get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysPermission.class);
        }
        if(sysPermissionRepository.existsById(id))
        {
            SysPermission user = sysPermissionRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysPermission save(SysPermission log) throws IOException {
        SysPermission log1 = sysPermissionRepository.save(log);
        getValueOps().set(getKey(log.getId()), JsonHelper.toJsonString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        sysPermissionRepository.deleteById(id);
    }

    @Override
    public List<SysPermission> getAll() {
        return sysPermissionRepository.findAll();
    }

    @Override
    public List<SysPermission> queryWx(Specification<SysPermission> spec) {
        return sysPermissionRepository.findAll(spec);
    }

    @Override
    public Page<SysPermission> queryWx(Specification<SysPermission> spec, Pageable pageable) {
        return sysPermissionRepository.findAll(spec,pageable);
    }

    @Override
    public List<SysPermission> findAllByRoleIdAndProjectId(Long roleId, Long projectId) {
        return sysPermissionRepository.findAllByRoleIdAndProjectId(roleId,projectId);
    }

    @Override
    public List<SysPermission> getUserPermissionByRoleIds(List<Long> roleIds) {
        return sysPermissionRepository.getUserPermissionByRoleIds(roleIds);
    }

    @Override
    public List<SysPermission> getUserPermissionByRoleId(Long roleId) {
        return sysPermissionRepository.getUserPermissionByRoleId(roleId);
    }

}
