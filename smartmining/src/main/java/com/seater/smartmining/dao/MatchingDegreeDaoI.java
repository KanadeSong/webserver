package com.seater.smartmining.dao;

import com.seater.smartmining.entity.MatchingDegree;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/1 0001 11:46
 */
public interface MatchingDegreeDaoI {

    MatchingDegree get(Long id) throws IOException;
    MatchingDegree save(MatchingDegree log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<MatchingDegree> query();
    Page<MatchingDegree> query(Specification<MatchingDegree> spec);
    Page<MatchingDegree> query(Pageable pageable);
    Page<MatchingDegree> query(Specification<MatchingDegree> spec, Pageable pageable);
    List<MatchingDegree> getAll();
    void deleteByProjectIdAndTimeAndType(Long projectId, Date date, Integer type, Integer shifts);
    void batchSave(List<MatchingDegree> degreeList);
    List<Map> getAllByProjectIdAndStartTimeAndEndTimeByWeek(Long projectId, Date startTime, Date endTime);
}
