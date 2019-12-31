package com.seater.smartmining.dao;

import com.seater.smartmining.entity.OwnerDriverRelation;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:53
 */
public interface OwnerDriverRelationDaoI {

    OwnerDriverRelation get(Long id) throws IOException;
    OwnerDriverRelation save(OwnerDriverRelation log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<OwnerDriverRelation> query();
    Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example);
    Page<OwnerDriverRelation> query(Pageable pageable);
    Page<OwnerDriverRelation> query(Example<OwnerDriverRelation> example, Pageable pageable);
    List<OwnerDriverRelation> getAll();
}
