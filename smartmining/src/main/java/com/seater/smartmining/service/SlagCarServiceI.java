package com.seater.smartmining.service;

import com.seater.smartmining.entity.SlagCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:12
 */
public interface SlagCarServiceI {

    SlagCar get(Long id) throws IOException;
    void delete(Long id);
    void delete(List<Long> ids);
    SlagCar save(SlagCar log) throws IOException;
    Page<SlagCar> query(Specification<SlagCar> spec, Pageable pageable);
    List<SlagCar> getAllByProjectId(Long projectId);
    List<SlagCar> queryWx(Specification<SlagCar> spec);
    Page<SlagCar> findByOwnerId(Long userId, Pageable pageable);
    public SlagCar findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
    List<SlagCar> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    SlagCar getAllByProjectIdAndCodeInProject(Long projectId, String slagCarCode);
}
