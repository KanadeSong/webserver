package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.NozzleDaoI;
import com.seater.smartmining.entity.Nozzle;
import com.seater.smartmining.service.NozzleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:08
 */
@Service
public class NozzleServiceImpl implements NozzleServiceI {
    
    @Autowired
    NozzleDaoI nozzleDaoI;
    
    @Override
    public Nozzle get(Long id) throws IOException {
        return nozzleDaoI.get(id);
    }

    @Override
    public Nozzle save(Nozzle log) throws IOException {
        return nozzleDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        nozzleDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        nozzleDaoI.delete(ids);
    }

    @Override
    public Page<Nozzle> query() {
        return nozzleDaoI.query();
    }

    @Override
    public Page<Nozzle> query(Specification<Nozzle> spec) {
        return nozzleDaoI.query(spec);
    }

    @Override
    public Page<Nozzle> query(Pageable pageable) {
        return nozzleDaoI.query(pageable);
    }

    @Override
    public Page<Nozzle> query(Specification<Nozzle> spec, Pageable pageable) {
        return nozzleDaoI.query(spec,pageable);
    }

    @Override
    public List<Nozzle> getAll() {
        return nozzleDaoI.getAll();
    }

    @Override
    public List<Nozzle> queryWx(Specification<Nozzle> spec) {
        return nozzleDaoI.queryWx(spec);
    }
}
