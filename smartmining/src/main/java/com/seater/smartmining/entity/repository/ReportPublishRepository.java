package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ReportPublish;
import com.seater.smartmining.enums.ReportEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:42
 */
public interface ReportPublishRepository extends JpaRepository<ReportPublish, Long>, JpaSpecificationExecutor<ReportPublish> {

    ReportPublish findByProjectIdAndReportDate(Long projectId, Date reportDate);

    ReportPublish findByProjectIdAndReportDateAndReportEnum(Long projectId, Date reportDate, ReportEnum reportEnum);
}
