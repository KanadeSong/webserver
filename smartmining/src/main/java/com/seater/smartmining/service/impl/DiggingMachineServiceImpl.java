package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.DiggingMachineDaoI;
import com.seater.smartmining.entity.DiggingMachine;
import com.seater.smartmining.service.DiggingMachineServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:15
 */
@Service
public class DiggingMachineServiceImpl implements DiggingMachineServiceI {
    
    @Autowired
    DiggingMachineDaoI diggingMachineDaoI;

    @Override
    public DiggingMachine get(Long id) throws IOException {
        return diggingMachineDaoI.get(id);
    }

    @Override
    public void delete(Long id) {
        diggingMachineDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        diggingMachineDaoI.delete(ids);
    }

    @Override
    public DiggingMachine save(DiggingMachine log) throws IOException {
        return diggingMachineDaoI.save(log);
    }

    @Override
    public Page<DiggingMachine> query(Specification<DiggingMachine> spec, Pageable pageable) {
        return diggingMachineDaoI.query(spec, pageable);
    }

    @Override
    public List<DiggingMachine> queryWx(Specification<DiggingMachine> spec) {
        return diggingMachineDaoI.queryWx(spec);
    }

    @Override
    public Page<DiggingMachine> findByOwnerId(Long userId, Pageable pageable) {
        return diggingMachineDaoI.findByOwnerId(userId,pageable);
    }

    @Override
    public List<Map[]> findByOwnerId(Long userId) {
        return diggingMachineDaoI.findByOwnerId(userId);
    }

    @Override
    public List<DiggingMachine> findByOwnerIdAndDriverId(Long ownerId, Long driverId) {
        return diggingMachineDaoI.findByOwnerIdAndDriverId(ownerId,driverId);
    }

    @Override
    public DiggingMachine findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId) {
        return diggingMachineDaoI.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId,driverId);
    }
}
