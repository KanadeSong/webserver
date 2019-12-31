package com.seater.smartmining.dao;

import com.seater.smartmining.entity.InterPhone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/17 15:42
 */
public interface InterPhoneDaoI {

    InterPhone get(Long id) throws IOException;
    InterPhone save(InterPhone log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<InterPhone> query();
    Page<InterPhone> query(Specification<InterPhone> spec);
    Page<InterPhone> query(Pageable pageable);
    Page<InterPhone> query(Specification<InterPhone> spec, Pageable pageable);
    List<InterPhone> getAll();
    List<InterPhone> queryWx(Specification<InterPhone> spec);
}
