package com.seater.smartmining.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.seater.smartmining.entity.Version;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/4 0004 10:49
 */
public interface VersionServiceI {

    Version get(Long id) throws IOException;
    Version save(Version log) throws JsonProcessingException;
    void delete(Long id);
    void delete(List<Long> ids);
    Page<Version> query();
    Page<Version> query(Specification<Version> spec);
    Page<Version> query(Pageable pageable);
    Page<Version> query(Specification<Version> spec, Pageable pageable);
    List<Version> getAll();
}
