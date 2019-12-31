package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.WorkMergeErrorLogDaoI;
import com.seater.smartmining.entity.WorkMergeErrorLog;
import com.seater.smartmining.service.WorkMergeErrorLogServiceI;
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
 * @Date 2019/7/4 0004 11:26
 */
@Service
public class WorkMergeErrorLogServiceImpl implements WorkMergeErrorLogServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    WorkMergeErrorLogDaoI workMergeErrorLogDaoI;

    @Override
    public WorkMergeErrorLog get(Long id) throws IOException {
        return workMergeErrorLogDaoI.get(id);
    }

    @Override
    public WorkMergeErrorLog save(WorkMergeErrorLog log) throws IOException {
        return workMergeErrorLogDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        workMergeErrorLogDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        workMergeErrorLogDaoI.delete(ids);
    }

    @Override
    public Page<WorkMergeErrorLog> query() {
        return workMergeErrorLogDaoI.query();
    }

    @Override
    public Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec) {
        return workMergeErrorLogDaoI.query(spec);
    }

    @Override
    public Page<WorkMergeErrorLog> query(Pageable pageable) {
        return workMergeErrorLogDaoI.query(pageable);
    }

    @Override
    public Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec, Pageable pageable) {
        return workMergeErrorLogDaoI.query(spec, pageable);
    }

    @Override
    public List<WorkMergeErrorLog> getAll() {
        return workMergeErrorLogDaoI.getAll();
    }
}
