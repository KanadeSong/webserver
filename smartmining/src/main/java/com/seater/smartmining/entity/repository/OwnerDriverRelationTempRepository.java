package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.OwnerDriverRelation;
import com.seater.smartmining.entity.OwnerDriverRelationTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description 扫码临时表
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:55
 */
public interface OwnerDriverRelationTempRepository extends JpaRepository<OwnerDriverRelationTemp,Long> {
    
    public OwnerDriverRelationTemp findByDriverId(Long driverId);
    
    public OwnerDriverRelationTemp findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
    public List<OwnerDriverRelationTemp> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE owner_driver_relation \n" +
            "SET valid = 0 \n" +
            "WHERE\n" +
            "	owner_id != ?1 \n" +
            "	AND driver_id = ?2")
    void invalidOthersByOwnerIdAndDriverId(Long ownerId, Long driverId);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE owner_driver_relation \n" +
            "SET valid = 0 \n" +
            "WHERE\n" +
            "	owner_id = ?1 \n" +
            "	AND driver_id = ?2")
    void invalidOpposite(Long ownerId, Long driverId);
}
