package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.DeductionDiggingDaoI;
import com.seater.smartmining.entity.DeductionDigging;
import com.seater.smartmining.service.DeductionDiggingServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/8 0008 1:07
 */
@Service
public class DeductionDiggingServiceImpl implements DeductionDiggingServiceI {

    @Autowired
    DeductionDiggingDaoI deductionDiggingDaoI;

    @Override
    public DeductionDigging get(Long id) throws IOException {
        return deductionDiggingDaoI.get(id);
    }

    @Override
    public DeductionDigging save(DeductionDigging log) throws IOException {
        return deductionDiggingDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        deductionDiggingDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids){
            deductionDiggingDaoI.delete(id);
        }
    }

    @Override
    public Page<DeductionDigging> query() {
        return deductionDiggingDaoI.query();
    }

    @Override
    public Page<DeductionDigging> query(Specification<DeductionDigging> spec) {
        return deductionDiggingDaoI.query(spec);
    }

    @Override
    public Page<DeductionDigging> query(Pageable pageable) {
        return deductionDiggingDaoI.query(pageable);
    }

    @Override
    public Page<DeductionDigging> query(Specification<DeductionDigging> spec, Pageable pageable) {
        return deductionDiggingDaoI.query(spec, pageable);
    }

    @Override
    public List<DeductionDigging> getAll() {
        return deductionDiggingDaoI.getAll();
    }

    @Override
    public DeductionDigging getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate) {
        return deductionDiggingDaoI.getAllByProjectIdAndMachineIdAndReportDate(projectId, machineId, reportDate);
    }

    @Override
    public List<DeductionDigging> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return deductionDiggingDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<DeductionDigging> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime) {
        return deductionDiggingDaoI.getAllByProjectIdAndTime(projectId, startTime, endTime);
    }
}
