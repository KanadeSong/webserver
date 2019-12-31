package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.MatchingDegreeDaoI;
import com.seater.smartmining.entity.MatchingDegree;
import com.seater.smartmining.service.MatchingDegreeServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 11:59
 */
@Service
public class MatchingDegreeServiceImpl implements MatchingDegreeServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    MatchingDegreeDaoI matchingDegreeDaoI;

    @Override
    public MatchingDegree get(Long id) throws IOException {
        return matchingDegreeDaoI.get(id);
    }

    @Override
    public MatchingDegree save(MatchingDegree log) throws IOException {
        return matchingDegreeDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        matchingDegreeDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        matchingDegreeDaoI.delete(ids);
    }

    @Override
    public Page<MatchingDegree> query() {
        return matchingDegreeDaoI.query();
    }

    @Override
    public Page<MatchingDegree> query(Specification<MatchingDegree> spec) {
        return matchingDegreeDaoI.query(spec);
    }

    @Override
    public Page<MatchingDegree> query(Pageable pageable) {
        return matchingDegreeDaoI.query(pageable);
    }

    @Override
    public Page<MatchingDegree> query(Specification<MatchingDegree> spec, Pageable pageable) {
        return matchingDegreeDaoI.query(spec, pageable);
    }

    @Override
    public List<MatchingDegree> getAll() {
        return matchingDegreeDaoI.getAll();
    }

    @Override
    public void deleteByProjectIdAndTimeAndType(Long projectId, Date date, Integer type, Integer shifts) {
        matchingDegreeDaoI.deleteByProjectIdAndTimeAndType(projectId, date, type, shifts);
    }

    @Override
    public void batchSave(List<MatchingDegree> degreeList) {
        matchingDegreeDaoI.batchSave(degreeList);
    }

    @Override
    public List<Map> getAllByProjectIdAndStartTimeAndEndTimeByWeek(Long projectId, Date startTime, Date endTime) {
        return matchingDegreeDaoI.getAllByProjectIdAndStartTimeAndEndTimeByWeek(projectId, startTime, endTime);
    }
}
