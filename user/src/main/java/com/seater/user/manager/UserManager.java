package com.seater.user.manager;

import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 用户管理
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/14 10:01
 */
@Service
public class UserManager {

    @Autowired
    SysUserServiceI sysUserServiceI;

    public boolean accountExist(SysUser sysUser){
        if (sysUser == null) {
            return true;
        }
        if (null == sysUser.getAccount()){
            return true;
        }
        if ("".equals(sysUser.getAccount())){
            return true;
        }
        Specification<SysUser> spec = new Specification<SysUser>() {
            List<Predicate> list = new ArrayList<>();
            @Override
            public Predicate toPredicate(Root<SysUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                list.add(cb.notEqual(root.get("id").as(Long.class), sysUser.getId()));
                list.add(cb.equal(root.get("account").as(String.class), sysUser.getAccount()));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };

        List<SysUser> userList = sysUserServiceI.queryWx(spec);
        if (userList.size() > 0) {
            return true;
        }
        return false;
    }
}
