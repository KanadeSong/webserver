package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.SysUserProjectRoleDaoI;
import com.seater.user.entity.SysUserProjectRole;
import com.seater.user.entity.repository.SysUserProjectRoleRepository;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/16 9:13
 */
@Repository
public class SysUserProjectRoleDaoImpl implements SysUserProjectRoleDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    SysUserProjectRoleRepository sysRolePermissionRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:userProjectRole:";

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
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> example, Pageable pageable) {
        return sysRolePermissionRepository.findAll(example, pageable);
    }

    @Override
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> example) {
        return sysRolePermissionRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public List<SysUserProjectRole> queryWx(Specification<SysUserProjectRole> spec) {
        return sysRolePermissionRepository.findAll(spec);
    }

    @Override
    public Page<SysUserProjectRole> query(Pageable pageable) {
        return sysRolePermissionRepository.findAll(pageable);
    }

    @Override
    public Page<SysUserProjectRole> query() {
        return sysRolePermissionRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public SysUserProjectRole get(Long id) throws IOException {
        if (id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if (obj != null) {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, SysUserProjectRole.class);
        }
        if (sysRolePermissionRepository.existsById(id)) {
            SysUserProjectRole user = sysRolePermissionRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public SysUserProjectRole save(SysUserProjectRole log) throws IOException {
        SysUserProjectRole log1 = sysRolePermissionRepository.save(log);
        getValueOps().set(getKey(log.getId()), JsonHelper.toJsonString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log;
    }

    @Override
    public void delete(Long id) {
        if (id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        sysRolePermissionRepository.deleteById(id);
    }

    @Override
    public List<SysUserProjectRole> getAll() {
        return sysRolePermissionRepository.findAll();
    }

    @Override
    public List<SysUserProjectRole> findAllByUserIdAndValid(Long userId, Boolean valid) {
        return sysRolePermissionRepository.findAllByUserIdAndValid(userId, valid);
    }

    @Override
    public List<SysUserProjectRole> findByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId) {
        return sysRolePermissionRepository.findByUserIdAndProjectIdAndValidIsTrue(userId, projectId);
    }

    @Override
    public Page<Map[]> findByProjectIdAndValidIsTrue(Long projectId, String name, Long sort, PageRequest pageRequest) {
        return sysRolePermissionRepository.findByProjectIdAndValidIsTrue(projectId, name, sort, pageRequest);
    }

    @Override
    public List<Map[]> findByProjectIdAndValidIsTrue(Long projectId) {
        return sysRolePermissionRepository.findByProjectIdAndValidIsTrue(projectId);
    }

    @Override
    public int inValidProjectByUserIdAndProjectId(Long userId, Long projectId) {
        return sysRolePermissionRepository.inValidProjectByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public void deleteByUserIdAndProjectId(Long userId, Long projectId) {
        sysRolePermissionRepository.deleteByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Page<Map[]> findAll(PageRequest pageRequest) {
        return sysRolePermissionRepository.findAllJoinedProject(pageRequest);
    }

    @Override
    public List<SysUserProjectRole> findAllByProjectAndIsRoot(Long projectId, Boolean isRoot) {
        return sysRolePermissionRepository.findAllByProjectIdAndIsRoot(projectId, isRoot);
    }

    @Override
    public void updateRoleIdByRoleIdAndProjectId(Long newRoleId, Long roleId, Long projectId) {
        sysRolePermissionRepository.updateRoleIdByRoleIdAndProjectId(newRoleId, roleId, projectId);
    }

    @Override
    public void updateRoleIdByRoleId(Long newRoleId, Long roleId) {
        sysRolePermissionRepository.updateRoleIdByRoleId(newRoleId, roleId);
    }

    @Override
    public void deleteAllByProjectIdAndIsRoot(Long projectId, Boolean isRoot) {
        sysRolePermissionRepository.deleteAllByProjectIdAndIsRoot(projectId, isRoot);
    }

    @Override
    public void deleteAllByRoleId(Long roleId) {
        sysRolePermissionRepository.deleteAllByRoleId(roleId);
    }

}
