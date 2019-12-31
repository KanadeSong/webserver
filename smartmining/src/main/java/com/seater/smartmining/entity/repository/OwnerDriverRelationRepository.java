package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.OwnerDriverRelation;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:55
 */
public interface OwnerDriverRelationRepository extends JpaRepository<OwnerDriverRelation,Long>, JpaSpecificationExecutor<OwnerDriverRelation> {
    
    public OwnerDriverRelation findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
    public List<OwnerDriverRelation> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    
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
