package com.seater.user.service.impl;

import com.seater.user.dao.SysRoleDaoI;
import com.seater.user.entity.SysRole;
import com.seater.user.entity.UseType;
import com.seater.user.service.SysRoleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    SysRoleDaoI roleDaoI;

    @Override
    public SysRole get(Long id) throws IOException {
        return roleDaoI.get(id);
    }

    @Override
    public SysRole save(SysRole log) throws IOException {
        return roleDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        roleDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        roleDaoI.delete(ids);
    }

    @Override
    public Page<SysRole> query(Example<SysRole> example, Pageable pageable) {
        return roleDaoI.query(example, pageable);
    }

    @Override
    public Page<SysRole> query(Example<SysRole> example) {
        return roleDaoI.query(example);
    }

    @Override
    public Page<SysRole> query(Pageable pageable) {
        return roleDaoI.query(pageable);
    }

    @Override
    public Page<SysRole> query() {
        return roleDaoI.query();
    }

    @Override
    public List<SysRole> getAll() {
        return roleDaoI.getAll();
    }

    @Override
    public List<SysRole> queryWx(Specification<SysRole> spec) {
        return roleDaoI.queryWx(spec);
    }

    @Override
    public Page<SysRole> queryWx(Specification<SysRole> spec, Pageable pageable) {
        return roleDaoI.queryWx(spec,pageable);
    }

    @Override
    public List<SysRole> findCommonByProjectIdAndUseType(Long projectId, UseType useType) {
        Specification<SysRole> spec = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("useType").as(UseType.class), useType));
                list.add(criteriaBuilder.equal(root.get("valid").as(Boolean.class), true));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };

        Specification<SysRole> spec2 = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return roleDaoI.queryWx(spec.or(spec2));
    }

    @Override
    public Page<SysRole> findCommonByProjectIdAndUseType(Long projectId, UseType useType, Pageable pageable) {
        Specification<SysRole> spec = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("useType").as(UseType.class), useType));
                list.add(criteriaBuilder.equal(root.get("valid").as(Boolean.class), true));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };

        Specification<SysRole> spec2 = new Specification<SysRole>() {
            List<Predicate> list = new ArrayList<>();

            @Override
            public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                list.add(criteriaBuilder.equal(root.get("projectId").as(Long.class), projectId));
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return roleDaoI.queryWx(spec.or(spec2), pageable);
    }

    @Override
    public List<SysRole> getByUseType(UseType useType) {
        return roleDaoI.getByUseType(useType);
    }

    @Override
    public List<SysRole> getAllByProjectId(Long projectId) {
        return roleDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<SysRole> findAllByIdIsIn(List<Long> ids) {
        return roleDaoI.findAllByIdIsIn(ids);
    }

    @Override
    public List<SysRole> findAllByUseTypeAndParentIdIsNull(UseType useType) {
        return roleDaoI.findAllByUseTypeAndParentIdIsNull(useType);
    }

    @Override
    public void deleteAllByParentId(Long parentId) {
        roleDaoI.deleteAllByParentId(parentId);
    }

    @Override
    public void deleteAllByProjectIdAndIsDefault(Long projectId, Boolean isDefault) {
        roleDaoI.deleteAllByProjectIdAndIsDefault(projectId, isDefault);
    }

    @Override
    public void updateAllRoleNameByParentId(String roleName, Long parentId) {
        roleDaoI.updateAllRoleNameByParentId(roleName, parentId);
    }
}
