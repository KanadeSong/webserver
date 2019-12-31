package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysRolePermissionDaoI;
import com.seater.user.entity.SysRolePermission;
import com.seater.user.entity.UseType;
import com.seater.user.entity.repository.SysRolePermissionRepository;
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
public class SysRolePermissionDaoImpl implements SysRolePermissionDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysRolePermissionRepository sysRolePermissionRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:rolePermission:";

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
    public Page<SysRolePermission> query(Example<SysRolePermission> example, Pageable pageable) {
        return sysRolePermissionRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysRolePermission> query(Example<SysRolePermission> example) {
        return sysRolePermissionRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<SysRolePermission> query(Pageable pageable) {
        return sysRolePermissionRepository.findAll(pageable);
    }

    @Override
    public Page<SysRolePermission> query() {
        return sysRolePermissionRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysRolePermission get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysRolePermission.class);
        }
        if(sysRolePermissionRepository.existsById(id))
        {
            SysRolePermission user = sysRolePermissionRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysRolePermission save(SysRolePermission log) throws IOException {
        SysRolePermission log1 = sysRolePermissionRepository.save(log);
        getValueOps().set(getKey(log.getId()), JsonHelper.toJsonString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        sysRolePermissionRepository.deleteById(id);
    }

    @Override
    public List<SysRolePermission> getAll() {
        return sysRolePermissionRepository.findAll();
    }

    @Override
    public List<SysRolePermission> queryWx(Specification<SysRolePermission> spec) {
        return sysRolePermissionRepository.findAll(spec);
    }

    @Override
    public Page<SysRolePermission> queryWx(Specification<SysRolePermission> spec, Pageable pageable) {
        return sysRolePermissionRepository.findAll(spec,pageable);
    }

    @Override
    public void deleteByRoleIdAndProjectId(Long roleId, Long projectId) {
        sysRolePermissionRepository.deleteByRoleIdAndProjectId(roleId,projectId);
    }

    @Override
    public void deleteByRoleIdAndProjectIdAndUseType(Long roleId, Long projectId, UseType useType) {
        sysRolePermissionRepository.deleteByRoleIdAndProjectIdAndUseType(roleId, projectId, useType.getValue());
    }

    @Override
    public void deleteAllByRoleIdAndUseType(Long roleId, UseType useType) {
        sysRolePermissionRepository.deleteAllByRoleIdAndUseType(roleId, useType);
    }

    @Override
    public void deleteAllByRoleId(Long roleId) {
        sysRolePermissionRepository.deleteAllByRoleId(roleId);
    }
}
