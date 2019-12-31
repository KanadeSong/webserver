package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysRoleDaoI;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.UseType;
import com.seater.user.entity.repository.SysRoleRepository;
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
public class SysRoleDaoImpl implements SysRoleDaoI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysRoleRepository sysRoleRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:role:";

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
    public Page<SysRole> query(Example<SysRole> example, Pageable pageable) {
        return sysRoleRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysRole> query(Example<SysRole> example) {
        return sysRoleRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<SysRole> query(Pageable pageable) {
        return sysRoleRepository.findAll(pageable);
    }

    @Override
    public Page<SysRole> query() {
        return sysRoleRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysRole get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysRole.class);
        }
        if(sysRoleRepository.existsById(id))
        {
            SysRole user = sysRoleRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysRole save(SysRole log) throws IOException {
        SysRole log1 = sysRoleRepository.save(log);
        getValueOps().set(getKey(log.getId()), JsonHelper.toJsonString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        sysRoleRepository.deleteByRoleId(id);
    }

    @Override
    public List<SysRole> getAll() {
        return sysRoleRepository.findAll();
    }

    @Override
    public List<SysRole> queryWx(Specification<SysRole> spec) {
        return sysRoleRepository.findAll(spec);
    }

    @Override
    public Page<SysRole> queryWx(Specification<SysRole> spec, Pageable pageable) {
        return sysRoleRepository.findAll(spec,pageable);
    }

    @Override
    public List<SysRole> getByUseType(UseType useType) {
        return sysRoleRepository.getByUseType(useType);
    }

    @Override
    public List<SysRole> getAllByProjectId(Long projectId) {
        return sysRoleRepository.getAllByProjectId(projectId);
    }

    @Override
    public List<SysRole> findAllByIdIsIn(List<Long> ids) {
        return sysRoleRepository.findAllByIdIsIn(ids);
    }

    @Override
    public List<SysRole> findAllByUseTypeAndParentIdIsNull(UseType useType) {
        return sysRoleRepository.findAllByUseTypeAndParentIdIsNull(useType);
    }

    @Override
    public void deleteAllByParentId(Long parentId) {
        sysRoleRepository.deleteAllByParentId(parentId);
    }

    @Override
    public void deleteAllByProjectIdAndIsDefault(Long projectId, Boolean isDefault) {
        sysRoleRepository.deleteAllByProjectIdAndIsDefault(projectId, isDefault);
    }

    @Override
    public void updateAllRoleNameByParentId(String roleName, Long parentId) {
        sysRoleRepository.updateAllRoleNameByParentId(roleName, parentId);
    }

}
