package com.seater.smartmining.service;

import com.seater.smartmining.entity.Nozzle;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:07
 */
public interface NozzleServiceI {

    Nozzle get(Long id) throws IOException;
    Nozzle save(Nozzle log) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<Nozzle> query();
    Page<Nozzle> query(Specification<Nozzle> spec);
    Page<Nozzle> query(Pageable pageable);
    Page<Nozzle> query(Specification<Nozzle> spec, Pageable pageable);
    List<Nozzle> getAll();
    List<Nozzle> queryWx(Specification<Nozzle> spec);
}
