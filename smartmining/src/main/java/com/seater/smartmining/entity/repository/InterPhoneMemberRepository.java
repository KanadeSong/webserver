package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.InterPhoneMember;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 11:21
 */
public interface InterPhoneMemberRepository extends JpaRepository<InterPhoneMember, Long>, JpaSpecificationExecutor<InterPhoneMember> {

    @Modifying
    @Query(nativeQuery = true, value = "delete from inter_phone_member where inter_phone_group_id = ?1 and is_fixed = false")
    @Transactional
    void deleteAllByInterPhoneGroupId(Long interPhoneGroupId);

    @Modifying
    @Query(nativeQuery = true, value = "delete from inter_phone_member where inter_phone_group_id = ?1 and is_fixed = true")
    void deleteAllFixByInterPhoneGroupId(Long interPhoneGroupId);

    List<InterPhoneMember> findAllByInterPhoneGroupId(Long interPhoneGroupId);

    List<InterPhoneMember> findAllByProjectId(Long projectId);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "	m.* \n" +
            "FROM\n" +
            "	inter_phone_member m,\n" +
            "	inter_phone_group g \n" +
            "WHERE\n" +
            "	m.inter_phone_group_id = g.id \n" +
            "	AND g.project_id = ?1 \n" +
            "	AND g.group_type in (?2)")
    List<InterPhoneMember> findAllByProjectIdAndGroupTypeIn(Long projectId, List<Integer> groupTypeList);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE \n" +
            "FROM\n" +
            "\tinter_phone_member \n" +
            "WHERE\n" +
            "\tinter_phone_group_id = ?1 \n" +
            "\tAND project_id = ?2 \n" +
            "\tAND user_object_type = ?3 \n" +
            "\tAND is_fixed = FALSE")
    void deleteAllByInterPhoneGroupIdAndUserObjectIdAndUserObjectType(Long interPhoneGroupId, Long userObjectId, UserObjectType userObjectType);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE \n" +
            "FROM\n" +
            "\tinter_phone_member \n" +
            "WHERE\n" +
            "\tschedule_id = ?1 \n" +
            "\tAND is_fixed = FALSE")
    void deleteAllByScheduleId(Long scheduleId);

}
