package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.InterPhoneSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import javax.transaction.Transactional;


/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:21
 */
public interface InterPhoneScheduleRepository extends JpaRepository<InterPhoneSchedule, Long>, JpaSpecificationExecutor<InterPhoneSchedule> {

    @Modifying
    @Transactional
    void deleteAllByScheduleId(Long scheduleId);

    @Modifying
    @Transactional
    void deleteAllByProjectId(Long projectId);

    @Modifying
    @Transactional
    void deleteAllByInterPhoneGroupId(Long interPhoneGroupId);

}
