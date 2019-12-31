package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.Nozzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 13:42
 */
public interface NozzleRepository extends JpaRepository<Nozzle,Long>, JpaSpecificationExecutor<Nozzle> {
}
