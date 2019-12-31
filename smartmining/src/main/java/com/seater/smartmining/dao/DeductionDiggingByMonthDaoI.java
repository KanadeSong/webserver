package com.seater.smartmining.dao;

import com.seater.smartmining.entity.DeductionDiggingByMonth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/10 0010 13:43
 */
public interface DeductionDiggingByMonthDaoI {

    DeductionDiggingByMonth get(Long id) throws IOException;
    DeductionDiggingByMonth save(DeductionDiggingByMonth log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<DeductionDiggingByMonth> query();
    Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec);
    Page<DeductionDiggingByMonth> query(Pageable pageable);
    Page<DeductionDiggingByMonth> query(Specification<DeductionDiggingByMonth> spec, Pageable pageable);
    List<DeductionDiggingByMonth> getAll();
    List<DeductionDiggingByMonth> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<DeductionDiggingByMonth> saveAll(List<DeductionDiggingByMonth> saveList);
}
