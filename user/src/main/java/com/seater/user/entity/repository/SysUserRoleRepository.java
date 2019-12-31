package com.seater.user.entity.repository;

import com.seater.user.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface SysUserRoleRepository extends JpaRepository<SysUserRole,Long> {
    @Transactional
    public void deleteAllByUserId(Long userId);

    public List<SysUserRole> findByUserId(Long userId);
}
