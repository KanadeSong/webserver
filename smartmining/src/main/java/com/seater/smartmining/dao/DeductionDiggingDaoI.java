package com.seater.smartmining.dao;

import com.seater.smartmining.entity.DeductionDigging;
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
 * @Date 2019/5/8 0008 0:42
 */
public interface DeductionDiggingDaoI {
    DeductionDigging get(Long id) throws IOException;
    DeductionDigging save(DeductionDigging log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<DeductionDigging> query();
    Page<DeductionDigging> query(Specification<DeductionDigging> spec);
    Page<DeductionDigging> query(Pageable pageable);
    Page<DeductionDigging> query(Specification<DeductionDigging> spec, Pageable pageable);
    List<DeductionDigging> getAll();
    DeductionDigging getAllByProjectIdAndMachineIdAndReportDate(Long projectId, Long machineId, Date reportDate);
    List<DeductionDigging> getAllByProjectIdAndReportDate(Long projectId, Date reportDate);
    List<DeductionDigging> getAllByProjectIdAndTime(Long projectId, Date startTime, Date endTime);
}
