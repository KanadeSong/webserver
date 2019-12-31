package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.InterPhoneGroup;
import com.seater.smartmining.enums.GroupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:21
 */
public interface InterPhoneGroupRepository extends JpaRepository<InterPhoneGroup, Long>, JpaSpecificationExecutor<InterPhoneGroup> {
    InterPhoneGroup findBySlagSiteIdAndGroupType(Long slagSiteId, GroupType groupType);

    List<InterPhoneGroup> findAllByProjectIdAndGroupTypeIn(Long projectId, List<GroupType> groupTypeList);

    InterPhoneGroup findByGroupTypeAndProjectId(GroupType groupType, Long projectId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	g.* \n" +
            "FROM\n" +
            "	inter_phone_group g,\n" +
            "	inter_phone_schedule s \n" +
            "WHERE\n" +
            "	g.id = s.inter_phone_group_id \n" +
            "	AND s.schedule_id = ?1")
    InterPhoneGroup findByScheduleId(Long scheduleId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	g.* \n" +
            "FROM\n" +
            "	inter_phone_group g,\n" +
            "	inter_phone_schedule s \n" +
            "WHERE\n" +
            "	g.id = s.inter_phone_group_id \n" +
            "	AND s.schedule_id = ?1 \n" +
            "	AND s.id = ?2")
    InterPhoneGroup findByScheduleIdAndInterPhoneGroupId(Long scheduleId, Long interPhoneGroupId);

    InterPhoneGroup findByNameAndGroupType(String name, GroupType groupType);

    InterPhoneGroup findByGroupCodeAndGroupTypeAndProjectId(String groupCode, GroupType groupType, Long projectId);
}
