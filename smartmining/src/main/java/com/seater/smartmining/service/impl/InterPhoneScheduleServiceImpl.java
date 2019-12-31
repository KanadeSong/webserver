package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.InterPhoneScheduleDaoI;
import com.seater.smartmining.entity.InterPhoneSchedule;
import com.seater.smartmining.service.InterPhoneScheduleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:27
 */
@Service
public class InterPhoneScheduleServiceImpl implements InterPhoneScheduleServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    InterPhoneScheduleDaoI interPhoneScheduleDaoI;

    @Override
    public InterPhoneSchedule get(Long id) throws IOException {
        return interPhoneScheduleDaoI.get(id);
    }

    @Override
    public InterPhoneSchedule save(InterPhoneSchedule log) throws IOException {
        return interPhoneScheduleDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        interPhoneScheduleDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        interPhoneScheduleDaoI.delete(ids);
    }

    @Override
    public Page<InterPhoneSchedule> query(Pageable pageable) {
        return interPhoneScheduleDaoI.query(pageable);
    }

    @Override
    public Page<InterPhoneSchedule> query() {
        return interPhoneScheduleDaoI.query();
    }

    @Override
    public Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec) {
        return interPhoneScheduleDaoI.query(spec);
    }

    @Override
    public Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec, Pageable pageable) {
        return interPhoneScheduleDaoI.query(spec, pageable);
    }

    @Override
    public List<InterPhoneSchedule> getAll() {
        return interPhoneScheduleDaoI.getAll();
    }

    @Override
    public List<InterPhoneSchedule> queryWx(Specification<InterPhoneSchedule> spec) {
        return interPhoneScheduleDaoI.queryWx(spec);
    }

    @Override
    public void deleteAllByScheduleId(Long scheduleId) {
        interPhoneScheduleDaoI.deleteAllByScheduleId(scheduleId);
    }

    @Override
    public void deleteAllByProjectId(Long projectId) {
        interPhoneScheduleDaoI.deleteAllByProjectId(projectId);
    }

    @Override
    public void deleteAllByInterPhoneGroupId(Long interPhoneGroupId) {
        interPhoneScheduleDaoI.deleteAllByInterPhoneGroupId(interPhoneGroupId);
    }
}
