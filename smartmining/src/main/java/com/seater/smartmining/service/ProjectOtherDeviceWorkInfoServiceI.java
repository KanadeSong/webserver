package com.seater.smartmining.service;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectOtherDevice;
import com.seater.smartmining.entity.ProjectOtherDeviceWorkInfo;
import com.seater.smartmining.enums.ProjectOtherDeviceStatusEnum;
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
 * @Date 2019/10/10 0010 16:02
 */
public interface ProjectOtherDeviceWorkInfoServiceI {

    ProjectOtherDeviceWorkInfo get(Long id) throws IOException;
    ProjectOtherDeviceWorkInfo save(ProjectOtherDeviceWorkInfo log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectOtherDeviceWorkInfo> query();
    Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec);
    Page<ProjectOtherDeviceWorkInfo> query(Pageable pageable);
    Page<ProjectOtherDeviceWorkInfo> query(Specification<ProjectOtherDeviceWorkInfo> spec, Pageable pageable);
    List<ProjectOtherDeviceWorkInfo> getAll();
    ProjectOtherDeviceWorkInfo getAllByProjectIdAndCodeAndCarType(Long projectId, String code, Integer carType, Integer status);
    List<Map> getDayReportByProjectIdAndDateIdentificationAndCarType(Long projectId, Date date, Integer carType);
}
