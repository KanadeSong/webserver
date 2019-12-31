package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ProjectCheckLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ProjectCheckLogDaoI {
     ProjectCheckLog get(Long id) throws IOException;
     ProjectCheckLog save(ProjectCheckLog log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectCheckLog> query();
     Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec);
     Page<ProjectCheckLog> query(Pageable pageable);
     Page<ProjectCheckLog> query(Specification<ProjectCheckLog> spec, Pageable pageable);
     List<ProjectCheckLog> getAll();
     List<Map> getCheckCountByProjectIDAndTimeCheck(Long projectId, Date startTime, Date endTime);
}
