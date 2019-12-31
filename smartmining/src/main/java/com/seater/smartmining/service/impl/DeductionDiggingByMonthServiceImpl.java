package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.DeductionDiggingByMonthDaoI;
import com.seater.smartmining.entity.DeductionDiggingByMonth;
import com.seater.smartmining.service.DeductionDiggingByMonthServiceI;
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
 * @Date 2019/5/10 0010 13:49
 */
@Service
public class DeductionDiggingByMonthServiceImpl implements DeductionDiggingByMonthServiceI {

    @Autowired
    DeductionDiggingByMonthDaoI deductionDiggingByMonthDaoI;

    @Override
    public DeductionDiggingByMonth get(Long id) throws IOException {
        return deductionDiggingByMonthDaoI.get(id);
    }

    @Override
    public DeductionDiggingByMonth save(DeductionDiggingByMonth log) throws IOException {
        return deductionDiggingByMonthDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        deductionDiggingByMonthDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        deductionDiggingByMonthDaoI.delete(ids);
    }

    @Override
    public Page<DeductionDiggingByMonth> query() {
        return deductionDiggingByMonthDaoI.query();
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec) {
        return deductionDiggingByMonthDaoI.query(spec);
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Pageable pageable) {
        return deductionDiggingByMonthDaoI.query(pageable);
    }

    @Override
    public Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec, Pageable pageable) {
        return deductionDiggingByMonthDaoI.query(spec, pageable);
    }

    @Override
    public List<DeductionDiggingByMonth> getAll() {
        return deductionDiggingByMonthDaoI.getAll();
    }

    @Override
    public List<DeductionDiggingByMonth> getAllByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return deductionDiggingByMonthDaoI.getAllByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public List<DeductionDiggingByMonth> saveAll(List<DeductionDiggingByMonth> saveList) {
        return deductionDiggingByMonthDaoI.saveAll(saveList);
    }
}
