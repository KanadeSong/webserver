package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.SlagCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:09
 */
public interface SlagCarRepository extends JpaRepository<SlagCar,Long>, JpaSpecificationExecutor<SlagCar> {
    Page<SlagCar> findByOwnerId(Long ownerId, Pageable pageable);
    SlagCar findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
    List<SlagCar> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    List<SlagCar> getAllByProjectId(Long projectId);
    SlagCar getAllByProjectIdAndCodeInProject(Long projectId, String slagCarCode);
}
