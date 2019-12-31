package com.seater.smartmining.service;

import com.seater.smartmining.entity.InterPhoneSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/9/19 14:23
 */
public interface InterPhoneScheduleServiceI {

    InterPhoneSchedule get(Long id) throws IOException;

    InterPhoneSchedule save(InterPhoneSchedule log) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    Page<InterPhoneSchedule> query();

    Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec);

    Page<InterPhoneSchedule> query(Pageable pageable);

    Page<InterPhoneSchedule> query(Specification<InterPhoneSchedule> spec, Pageable pageable);

    List<InterPhoneSchedule> getAll();

    List<InterPhoneSchedule> queryWx(Specification<InterPhoneSchedule> spec);

    void deleteAllByScheduleId(Long scheduleId);

    void deleteAllByProjectId(Long projectId);

    void deleteAllByInterPhoneGroupId(Long interPhoneGroupId);
}
