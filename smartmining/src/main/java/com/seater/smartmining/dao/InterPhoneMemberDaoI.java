package com.seater.smartmining.dao;

import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.utils.interPhone.UserObjectType;
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
public interface InterPhoneMemberDaoI {

    InterPhoneMember get(Long id) throws IOException;

    InterPhoneMember save(InterPhoneMember log) throws IOException;

    void delete(Long id);

    void delete(List<Long> ids);

    Page<InterPhoneMember> query();

    Page<InterPhoneMember> query(Specification<InterPhoneMember> spec);

    Page<InterPhoneMember> query(Pageable pageable);

    Page<InterPhoneMember> query(Specification<InterPhoneMember> spec, Pageable pageable);

    List<InterPhoneMember> getAll();

    List<InterPhoneMember> queryWx(Specification<InterPhoneMember> spec);

    void deleteAllByInterPhoneGroupId(Long interPhoneGroupId);

    void deleteAllFixByInterPhoneGroupId(Long interPhoneGroupId);

    List<InterPhoneMember> findAllByInterPhoneGroupId(Long interPhoneGroupId);

    void batchSave(List<InterPhoneMember> interPhoneMemberList);

    List<InterPhoneMember> findAllByProjectId(Long projectId);

    List<InterPhoneMember> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList);

    void deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(Long interPhoneGroupId, Long userObjectId, UserObjectType userObjectType);

    void deleteAllByScheduleId(Long scheduleId);
}
