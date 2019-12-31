package com.seater.smartmining.entity.repository;

import com.seater.smartmining.entity.ProjectOilCarUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/2 18:32
 */
public interface ProjectOilCarUserRepository extends JpaRepository<ProjectOilCarUser, Long>, JpaSpecificationExecutor<ProjectOilCarUser> {

    void deleteAllByOilCarId(Long oilCarId);
}
