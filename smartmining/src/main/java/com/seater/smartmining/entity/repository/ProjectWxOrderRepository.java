package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectWxOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:10
 */
public interface ProjectWxOrderRepository extends JpaRepository<ProjectWxOrder, Long>, JpaSpecificationExecutor<ProjectWxOrder> {

    ProjectWxOrder getAllByOrderNoAndAppIdAndOpenId(String orderNo, String appId, String openId);
    ProjectWxOrder getAllByWechatOrderNo(String wechatOrderNo);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from project_wx_order where order_no = ?1")
    void deleteByOrderNo(String orderNo);
}
