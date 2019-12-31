package com.seater.smartmining.service;

import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.enums.GroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:26
 */
public interface InterPhoneGroupServiceI {

    InterPhoneGroup get(Long id) throws IOException;

    InterPhoneGroup save(InterPhoneGroup log) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    Page<InterPhoneGroup> query();

    Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec);

    Page<InterPhoneGroup> query(Pageable pageable);

    Page<InterPhoneGroup> query(Specification<InterPhoneGroup> spec, Pageable pageable);

    List<InterPhoneGroup> getAll();

    List<InterPhoneGroup> queryWx(Specification<InterPhoneGroup> spec);

    InterPhoneGroup findBySlagSiteIdAndGroupType(Long slagSiteId, GroupType groupType);

    List<InterPhoneGroup> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList);

    InterPhoneGroup findByGroupTypeAndProjectId(GroupType groupType, Long projectId);

    InterPhoneGroup findByScheduleId(Long scheduleId);

    InterPhoneGroup findByScheduleIdAndInterPhoneGroupId(Long scheduleId, Long interPhoneGroupId);

    InterPhoneGroup findByNameAndGroupType(String name, GroupType groupType);

    InterPhoneGroup findByGroupCodeAndGroupTypeAndProjectId(String groupCode, GroupType groupType, Long projectId);

}
