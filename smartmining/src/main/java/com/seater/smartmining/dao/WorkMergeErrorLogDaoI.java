package com.seater.smartmining.dao;

import com.seater.smartmining.entity.WorkMergeErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/7/4 0004 11:18
 */
public interface WorkMergeErrorLogDaoI {

    WorkMergeErrorLog get(Long id) throws IOException;
    WorkMergeErrorLog save(WorkMergeErrorLog log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<WorkMergeErrorLog> query();
    Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec);
    Page<WorkMergeErrorLog> query(Pageable pageable);
    Page<WorkMergeErrorLog> query(Specification<WorkMergeErrorLog> spec, Pageable pageable);
    List<WorkMergeErrorLog> getAll();
}
