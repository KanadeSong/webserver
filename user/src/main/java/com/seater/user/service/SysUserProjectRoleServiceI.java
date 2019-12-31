package com.seater.user.service;

import com.seater.user.entity.SysUserProjectRole;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/16 9:09
 */
public interface SysUserProjectRoleServiceI {

    public SysUserProjectRole get(Long id) throws IOException;
    public SysUserProjectRole save(SysUserProjectRole log) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<SysUserProjectRole> query();
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> spec);
    public List<SysUserProjectRole> queryWx(Specification<SysUserProjectRole> spec);
    public Page<SysUserProjectRole> query(Pageable pageable);
    public Page<SysUserProjectRole> query(Specification<SysUserProjectRole> spec, Pageable pageable);
    public List<SysUserProjectRole> getAll();
    public List<SysUserProjectRole> findAllByUserIdAndValid(Long userId, Boolean valid);
    public List<SysUserProjectRole> findByUserIdAndProjectIdAndValidIsTrue(Long userId, Long projectId);
    public Page<Map[]> findByProjectIdAndValidIsTrue(Long projectId, String name,Long sort, PageRequest pageRequest);
    public List<Map[]> findByProjectIdAndValidIsTrue(Long projectId);
    public int inValidProjectByUserIdAndProjectId(Long userId, Long projectId);
    public void deleteByUserIdAndProjectId(Long userId, Long projectId);

    Page<Map[]> findAll(PageRequest pageRequest);
    List<SysUserProjectRole> findAllByProjectAndIsRoot(Long projectId,Boolean isRoot);

    void updateRoleIdByRoleIdAndProjectId(Long newRoleId, Long roleId, Long projectId);

    void updateRoleIdByRoleId(Long newRoleId, Long roleId);

    void deleteAllByProjectIdAndIsRoot(Long projectId, Boolean isRoot);

    void deleteAllByRoleId(Long roleId);
}
