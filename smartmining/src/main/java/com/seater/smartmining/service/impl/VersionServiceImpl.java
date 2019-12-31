package com.seater.smartmining.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.dao.VersionDaoI;
import com.seater.smartmining.entity.Version;
import com.seater.smartmining.service.VersionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/4 0004 10:56
 */
@Service
public class VersionServiceImpl implements VersionServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    VersionDaoI versionDaoI;

    @Override
    public Version get(Long id) throws IOException {
        return versionDaoI.get(id);
    }

    @Override
    public Version save(Version log) throws JsonProcessingException {
        return versionDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        versionDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        versionDaoI.delete(ids);
    }

    @Override
    public Page<Version> query() {
        return versionDaoI.query();
    }

    @Override
    public Page<Version> query(Specification<Version> spec) {
        return versionDaoI.query(spec);
    }

    @Override
    public Page<Version> query(Pageable pageable) {
        return versionDaoI.query(pageable);
    }

    @Override
    public Page<Version> query(Specification<Version> spec, Pageable pageable) {
        return versionDaoI.query(spec, pageable);
    }

    @Override
    public List<Version> getAll() {
        return versionDaoI.getAll();
    }
}
