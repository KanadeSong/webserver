package com.seater.smartmining.dao;

import com.seater.smartmining.entity.DiggingMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:19
 */
public interface DiggingMachineDaoI {

    DiggingMachine get(Long id) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    public DiggingMachine save(DiggingMachine log) throws IOException;
    Page<DiggingMachine> query(Specification<DiggingMachine> spec, Pageable pageable);

    List<DiggingMachine> queryWx(Specification<DiggingMachine> spec);
    Page<DiggingMachine> findByOwnerId(Long userId, Pageable pageable);
    List<Map[]> findByOwnerId(Long userId);
    List<DiggingMachine> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    public DiggingMachine findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
}
