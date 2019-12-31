package com.seater.smartmining.service;

import com.seater.smartmining.entity.OwnerDriverRelation;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:47
 */
public interface OwnerDriverRelationServiceI {
    OwnerDriverRelation get(Long id) throws IOException;
    OwnerDriverRelation save(OwnerDriverRelation log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<OwnerDriverRelation> query();
    Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example);
    Page<OwnerDriverRelation> query(Pageable pageable);
    Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example, Pageable pageable);
    List<OwnerDriverRelation> getAll();
    OwnerDriverRelation findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverOpenId);
    List<OwnerDriverRelation> findByOwnerIdAndDriverId(Long ownerId, Long driverOpenId);
    List<OwnerDriverRelation> queryWx(Specification<OwnerDriverRelation> spec);
}
