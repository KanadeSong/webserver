package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.ReportPublishDaoI;
import com.seater.smartmining.entity.ReportPublish;
import com.seater.smartmining.enums.ReportEnum;
import com.seater.smartmining.service.ReportPublishServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:08
 */
@Service
public class ReportPublishServiceImpl implements ReportPublishServiceI {

    @Autowired
    ReportPublishDaoI reportPublishDaoI;

    @Override
    public ReportPublish get(Long id) throws IOException {
        return reportPublishDaoI.get(id);
    }

    @Override
    public ReportPublish save(ReportPublish log) throws IOException {
        return reportPublishDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        reportPublishDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        reportPublishDaoI.delete(ids);
    }

    @Override
    public Page<ReportPublish> query() {
        return reportPublishDaoI.query();
    }

    @Override
    public ReportPublish query(Specification<ReportPublish> spec) {
        return reportPublishDaoI.query(spec);
    }

    @Override
    public Page<ReportPublish> query(Pageable pageable) {
        return reportPublishDaoI.query(pageable);
    }

    @Override
    public Page<ReportPublish> query(Specification<ReportPublish> spec, Pageable pageable) {
        return reportPublishDaoI.query(spec, pageable);
    }

    @Override
    public List<ReportPublish> getAll() {
        return reportPublishDaoI.getAll();
    }

    @Override
    public List<ReportPublish> queryWx(Specification<ReportPublish> spec) {
        return reportPublishDaoI.queryWx(spec);
    }

    @Override
    public ReportPublish findByProjectIdAndReportDate(Long projectId, Date reportDate) {
        return reportPublishDaoI.findByProjectIdAndReportDate(projectId, reportDate);
    }

    @Override
    public ReportPublish findByProjectIdAndReportDateAndReportEnum(Long projectId, Date reportDate, ReportEnum reportEnum) {
        return reportPublishDaoI.findByProjectIdAndReportDateAndReportEnum(projectId, reportDate, reportEnum);
    }
}
