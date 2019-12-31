package com.seater.smartmining.service.impl;

import com.seater.smartmining.dao.SlagCarDaoI;
import com.seater.smartmining.entity.SlagCar;
import com.seater.smartmining.entity.SlagCar;
import com.seater.smartmining.service.SlagCarServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:15
 */
@Service
public class SlagCarServiceImpl implements SlagCarServiceI {
    
    @Autowired
    SlagCarDaoI slagCarDaoI;

    @Override
    public SlagCar get(Long id) throws IOException {
        return slagCarDaoI.get(id);
    }

    @Override
    public void delete(Long id) {
        slagCarDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        slagCarDaoI.delete(ids);
    }

    @Override
    public SlagCar save(SlagCar log) throws IOException {
        return slagCarDaoI.save(log);
    }

    @Override
    public Page<SlagCar> query(Specification<SlagCar> spec, Pageable pageable) {
        return slagCarDaoI.query(spec, pageable);
    }

    @Override
    public List<SlagCar> getAllByProjectId(Long projectId) {
        return slagCarDaoI.getAllByProjectId(projectId);
    }

    @Override
    public List<SlagCar> queryWx(Specification<SlagCar> spec) {
        return slagCarDaoI.queryWx(spec);
    }

    @Override
    public Page<SlagCar> findByOwnerId(Long userId, Pageable pageable) {
        return slagCarDaoI.findByOwnerId(userId,pageable);
    }

    @Override
    public SlagCar findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId) {
        return slagCarDaoI.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId,driverId);
    }

    @Override
    public List<SlagCar> findByOwnerIdAndDriverId(Long ownerId, Long driverId) {
        return slagCarDaoI.findByOwnerIdAndDriverId(ownerId,driverId);
    }

    @Override
    public SlagCar getAllByProjectIdAndCodeInProject(Long projectId, String slagCarCode) {
        return slagCarDaoI.getAllByProjectIdAndCodeInProject(projectId, slagCarCode);
    }
}
