package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ProjectWxOrderDaoI;
import com.seater.smartmining.entity.ProjectWxOrder;
import com.seater.smartmining.service.ProjectWxOrderServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:17
 */
@Service
public class ProjectWxOrderServiceImpl implements ProjectWxOrderServiceI {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ProjectWxOrderDaoI projectWxOrderDaoI;


    @Override
    public ProjectWxOrder get(Long id) throws IOException {
        return projectWxOrderDaoI.get(id);
    }

    @Override
    public ProjectWxOrder save(ProjectWxOrder log) throws IOException {
        return projectWxOrderDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        projectWxOrderDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        projectWxOrderDaoI.delete(ids);
    }

    @Override
    public Page<ProjectWxOrder> query() {
        return projectWxOrderDaoI.query();
    }

    @Override
    public Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec) {
        return projectWxOrderDaoI.query(spec);
    }

    @Override
    public Page<ProjectWxOrder> query(Pageable pageable) {
        return projectWxOrderDaoI.query(pageable);
    }

    @Override
    public Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec, Pageable pageable) {
        return projectWxOrderDaoI.query(spec, pageable);
    }

    @Override
    public List<ProjectWxOrder> getAll() {
        return projectWxOrderDaoI.getAll();
    }

    @Override
    public ProjectWxOrder getAllByOrderNoAndAppIdAndOpenId(String orderNo, String appId, String openId) {
        return projectWxOrderDaoI.getAllByOrderNoAndAppIdAndOpenId(orderNo, appId, openId);
    }

    @Override
    public ProjectWxOrder getAllByWechatOrderNo(String wechatOrderNo) {
        return projectWxOrderDaoI.getAllByWechatOrderNo(wechatOrderNo);
    }

    @Override
    public void deleteByOrderNo(String orderNo) {
        projectWxOrderDaoI.deleteByOrderNo(orderNo);
    }
}
