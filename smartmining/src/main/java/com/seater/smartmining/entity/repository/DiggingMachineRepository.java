package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.DiggingMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:09
 */
public interface DiggingMachineRepository extends JpaRepository<DiggingMachine,Long>, JpaSpecificationExecutor<DiggingMachine> {
    Page<DiggingMachine> findByOwnerId(Long ownerId, Pageable pageable);
    
    @Query(nativeQuery = true, value = "SELECT\n" +
            "	* \n" +
            "FROM\n" +
            "	project_digging_machine \n" +
            "WHERE\n" +
            "	owner_id = ?1")
    List<Map[]> findByOwnerId(Long ownerId);

    List<DiggingMachine> findByOwnerIdAndDriverId(Long ownerId, Long driverId);
    
    public DiggingMachine findByOwnerIdAndDriverIdAndValidIsTrue(Long ownerId, Long driverId);
}
