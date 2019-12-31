package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectScheduledDaoI;
import com.seater.smartmining.entity.ProjectScheduled;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.service.ProjectScheduledServiceI;
import com.seater.smartmining.utils.string.StringUtils;
import com.seater.user.entity.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated
@Service
public class ProjectScheduledServiceImpl implements ProjectScheduledServiceI {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ProjectScheduledDaoI projectScheduledDaoI;

    @Override
    public ProjectScheduled get(Long id) throws IOException {
        return projectScheduledDaoI.get(id);
    }

    @Override
    public ProjectScheduled save(ProjectScheduled log) throws IOException{
        return projectScheduledDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectScheduledDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectScheduledDaoI.delete(ids);
    }

    @Override
    public Page<ProjectScheduled> query(Pageable pageable) {
        return projectScheduledDaoI.query(pageable);
    }

    @Override
    public Page<ProjectScheduled> query() {
        return projectScheduledDaoI.query();
    }

    @Override
    public Page<ProjectScheduled> query(Specification<ProjectScheduled> spec) {
        return projectScheduledDaoI.query(spec);
    }

    @Override
    public Page<ProjectScheduled> query(Specification<ProjectScheduled> spec, Pageable pageable) {
        return projectScheduledDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectScheduled> getAll() {
        return projectScheduledDaoI.getAll();
    }

    @Override
    public List<ProjectScheduled> getByProjectIdOrderById(Long projectId) {
        return projectScheduledDaoI.getByProjectIdOrderById(projectId);
    }

    @Override
    public ProjectScheduled getByProjectIdAndDiggingMachineIdAndCarIdOrderById(Long projectId, Long diggingMachineId, Long carId) {
        return projectScheduledDaoI.getByProjectIdAndDiggingMachineIdAndCarIdOrderById(projectId, diggingMachineId, carId);
    }

    @Override
    public List<Map> getByProjectIdPage(Long projectId, int cur, int page) {
        return projectScheduledDaoI.getByProjectIdPage(projectId, cur, page);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndDiggingMachineId(Long projectId, Long machineId) {
        Specification<ProjectScheduled> spec = new Specification<ProjectScheduled>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectScheduled> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("diggingMachineId").as(Long.class), machineId));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return query(spec, PageRequest.of(0,1000000)).get().collect(Collectors.toList());
        //return projectScheduledDaoI.getAllByProjectIdAndDiggingMachineId(projectId, machineId);
    }

    @Override
    public void deleteByDiggingMachineIdAndProjectId(Long machineId, Long projectId) {
        projectScheduledDaoI.deleteByDiggingMachineIdAndProjectId(machineId, projectId);
    }

    @Override
    public void deleteByCarIdAndProjectId(Long carId, Long projectId) {
        projectScheduledDaoI.deleteByCarIdAndProjectId(carId, projectId);
    }

    @Override
    public List<Map> getByProjectIdAndCarIdOnDigging(Long projectId, Long carId) {
        return projectScheduledDaoI.getByProjectIdAndCarIdOnDigging(projectId, carId);
    }

    @Override
    public List<Map> getByProjectIdCount(Long projectId) {
        return projectScheduledDaoI.getByProjectIdCount(projectId);
    }

    @Override
    public ProjectScheduled getByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledDaoI.getByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public List<Map> getByProjectIdOnGroupId(Long projectId, int current, int page) {
        return projectScheduledDaoI.getByProjectIdOnGroupId(projectId, current, page);
    }

    @Override
    public List<Map> getByProjectIdAndManagerIdOnGroupId(Long projectId, String managerId, int current, int page) {
        return projectScheduledDaoI.getByProjectIdAndManagerIdOnGroupId(projectId, managerId, current, page);
    }

    @Override
    public List<Map> getByAllProjectIdOnGroupId(Long projectId) {
        return projectScheduledDaoI.getByAllProjectIdOnGroupId(projectId);
    }

    @Override
    public List<ProjectScheduled> getByProjectIdAndGroupCode(Long projectId, String groupCode) {
        Specification<ProjectScheduled> spec = new Specification<ProjectScheduled>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectScheduled> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                list.add(cb.equal(root.get("groupCode").as(String.class), groupCode));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
//        return query(spec, PageRequest.of(0,1000000)).get().collect(Collectors.toList());
        return query(spec, PageRequest.of(0,1000000)).getContent();
        //return projectScheduledDaoI.getByProjectIdAndGroupCode(projectId, groupCode);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineId(Long projectId, Long machineId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndDiggingMachineId(projectId, machineId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(Long projectId, Long machineId, String managerId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndDiggingMachineIdAndManagerId(projectId, machineId, managerId);
    }

    @Override
    public List<Map> getByProjectIdAndDiggingMachineId(Long projectId, Long diggingMachineId) {
        return projectScheduledDaoI.getByProjectIdAndDiggingMachineId(projectId, diggingMachineId);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledDaoI.getAllByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public void deleteByDiggingMachineCodeAndProjectId(String machineCode, Long projectId) {
        projectScheduledDaoI.deleteByDiggingMachineCodeAndProjectId(machineCode, projectId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarId(Long projectId, Long carId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndCarId(projectId, carId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndManagerId(Long projectId, Long carId, String managerId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndCarIdAndManagerId(projectId, carId, managerId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineId(projectId, carId, machineId);
    }

    @Override
    public List<ProjectScheduled> getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(Long projectId, Long carId, Long machineId, String managerId) {
        return projectScheduledDaoI.getGroupCodeByProjectIdAndCarIdAndDiggingMachineIdAndManagerId(projectId, carId, machineId, managerId);
    }

    @Override
    public List<ProjectScheduled> getAllByProjectIdAndCarIdAndDiggingMachineId(Long projectId, Long carId, Long machineId) {
        return projectScheduledDaoI.getAllByProjectIdAndCarIdAndDiggingMachineId(projectId, carId, machineId);
    }

    @Override
    public List<ProjectScheduled> queryWx(Specification<ProjectScheduled> spec) {
        return projectScheduledDaoI.queryWx(spec);
    }

    @Override
    public void saveOrModify(ProjectScheduled projectScheduled) {
        projectScheduledDaoI.saveOrModify(projectScheduled);
    }

    @Override
    public List<Map> getByProjectIdAndGroupCodeOrderByDiggingMachineId(Long projectId, String groupCode) {
        return projectScheduledDaoI.getByProjectIdAndGroupCodeOrderByDiggingMachineId(projectId, groupCode);
    }

    @Override
    public List<ProjectScheduled> getByProjectIdAndCarIdOrderById(Long projectId, Long carId) {
        return projectScheduledDaoI.getByProjectIdAndCarIdOrderById(projectId, carId);
    }

    @Override
    public List<ProjectScheduled> getByProjectIdAndDiggingMachineIdOrderById(Long projectId, Long diggingMachineId) {
        return projectScheduledDaoI.getByProjectIdAndDiggingMachineIdOrderById(projectId, diggingMachineId);
    }

    @Override
    public List<ProjectScheduled> getAllByQuery(Long projectId, String machineCode, String carCode, Integer current, Integer pageSize, PricingTypeEnums pricingType){
        Specification<ProjectScheduled> spec = new Specification<ProjectScheduled>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectScheduled> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                if(StringUtils.isNotEmpty(machineCode))
                    list.add(cb.like(root.get("diggingMachineCode").as(String.class), "%" + machineCode + "%"));
                if(StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode").as(String.class), "%" + carCode + "%"));
                if(pricingType != null && pricingType.getValue() != 0)
                    list.add(cb.equal(root.get("pricingType").as(Integer.class), pricingType.getValue()));
                query.orderBy(cb.desc(root.get("diggingMachineCode").as(String.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return query(spec, PageRequest.of(current,pageSize)).getContent();
    }

    @Override
    public List<Map> getByAllProjectIdAndPricingType(Long projectId, Integer pricingType) {
        return projectScheduledDaoI.getByAllProjectIdAndPricingType(projectId, pricingType);
    }
}
