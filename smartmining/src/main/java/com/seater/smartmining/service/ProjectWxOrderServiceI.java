package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectWxOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 14:16
 */
public interface ProjectWxOrderServiceI {

    ProjectWxOrder get(Long id) throws IOException;
    ProjectWxOrder save(ProjectWxOrder log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<ProjectWxOrder> query();
    Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec);
    Page<ProjectWxOrder> query(Pageable pageable);
    Page<ProjectWxOrder> query(Specification<ProjectWxOrder> spec, Pageable pageable);
    List<ProjectWxOrder> getAll();
    ProjectWxOrder getAllByOrderNoAndAppIdAndOpenId(String orderNo, String appId, String openId);
    ProjectWxOrder getAllByWechatOrderNo(String wechatOrderNo);
    void deleteByOrderNo(String orderNo);
}
