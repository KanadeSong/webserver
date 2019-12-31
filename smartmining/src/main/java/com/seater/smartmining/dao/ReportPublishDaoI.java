package com.seater.smartmining.dao;

import com.seater.smartmining.entity.ReportPublish;
import com.seater.smartmining.enums.ReportEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:13
 */
public interface ReportPublishDaoI {
    ReportPublish get(Long id) throws IOException;

    ReportPublish save(ReportPublish log) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    Page<ReportPublish> query();

    ReportPublish query(Specification<ReportPublish> spec);

    Page<ReportPublish> query(Pageable pageable);

    Page<ReportPublish> query(Specification<ReportPublish> spec, Pageable pageable);

    List<ReportPublish> getAll();

    List<ReportPublish> queryWx(Specification<ReportPublish> spec);

    ReportPublish findByProjectIdAndReportDate(Long projectId, Date reportDate);

    ReportPublish findByProjectIdAndReportDateAndReportEnum(Long projectId, Date reportDate, ReportEnum reportEnum);
}
