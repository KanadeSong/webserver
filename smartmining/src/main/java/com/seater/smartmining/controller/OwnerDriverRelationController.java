package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.enums.JoinStatus;
import com.seater.smartmining.entity.OwnerDriverRelation;
import com.seater.smartmining.entity.OwnerDriverRelationTemp;
import com.seater.smartmining.entity.repository.OwnerDriverRelationTempRepository;
import com.seater.smartmining.service.OwnerDriverRelationServiceI;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 车主-司机扫码加入
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 14:41
 */
@RestController
@RequestMapping("/api/ownerDriver")
public class OwnerDriverRelationController {

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    OwnerDriverRelationServiceI ownerDriverRelationServiceI;

    @Autowired
    OwnerDriverRelationTempRepository ownerDriverRelationTempRepository;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }


    /**
     * 司机扫码加入车队
     *
     * @param ownerId
     * @param driverOpenId
     * @return
     */
    @PostMapping("/join")
    @Transactional
    public Object join(Long ownerId, String driverOpenId) {

        try {
            SysUser driver = sysUserServiceI.getByOpenId(driverOpenId); //  司机
            //  没有这个人
            if (driver == null) {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "请先登陆");
                }};
            }

            Specification<OwnerDriverRelation> spec = new Specification<OwnerDriverRelation>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<OwnerDriverRelation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("driverId").as(Long.class), driver.getId()));
                    list.add(cb.equal(root.get("driverOpenId").as(String.class), driverOpenId));
                    list.add(cb.equal(root.get("valid").as(Boolean .class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            List<OwnerDriverRelation> ownerDriverRelations = ownerDriverRelationServiceI.queryWx(spec);
            if (ownerDriverRelations.size() >= 1){
                //  已加入某个车队
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "你已加入其他车队");
                }};
            }

            //  未加入车队
            //  增加一条缓存数据    有则覆盖,否则新建一条
            OwnerDriverRelationTemp temp = ownerDriverRelationTempRepository.findByDriverId(driver.getId());
            if (temp != null) {
                temp.setDriverOpenId(driverOpenId);
                temp.setValid(true);
                temp.setOwnerId(ownerId);
                temp.setUpdateTime(new Date());
                ownerDriverRelationTempRepository.save(temp);
            } else {

                temp = new OwnerDriverRelationTemp();
                temp.setDriverOpenId(driverOpenId);
                temp.setDriverId(driver.getId());
                temp.setValid(true);
                temp.setOwnerId(ownerId);
                temp.setUpdateTime(new Date());
                ownerDriverRelationTempRepository.save(temp);
            }
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }};
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 车主确认让司机加入车队
     *
     * @param ownerId
     * @param driverId
     * @return
     */
    @PostMapping("/confirm")
    @Transactional
    public Object confirm(Long ownerId, Long driverId) {
        try {

            if (ownerDriverRelationServiceI.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId, driverId) != null) {
                //  已加入车队 

                OwnerDriverRelationTemp temp = ownerDriverRelationTempRepository.findByDriverId(driverId);
                //  删除缓存
                if (temp != null) {
                    ownerDriverRelationTempRepository.deleteById(temp.getId());
                }
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "已加入车队,无需重复加入");
                }};
            }

            //  未加入车队
            //  从缓存表中取出
            OwnerDriverRelationTemp temp = ownerDriverRelationTempRepository.findByDriverId(driverId);
            if (temp == null) {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "分享已失效,请重新加入");
                }};
            }
            //  绑定车主-司机
            OwnerDriverRelation ownerDriverRelation = new OwnerDriverRelation();
            BeanUtils.copyProperties(temp, ownerDriverRelation);
            ownerDriverRelation.setDriverId(driverId);
            ownerDriverRelation.setOwnerId(ownerId);
            ownerDriverRelation.setAddTime(new Date()); //  车主确认时间
            ownerDriverRelation.setJoinStatus(JoinStatus.Joined);
            ownerDriverRelation.setValid(true);
            ownerDriverRelationServiceI.save(ownerDriverRelation);
            
            //  删除缓存
            ownerDriverRelationTempRepository.deleteById(temp.getId());
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }};
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 拒绝
     *
     * @param ownerId
     * @param driverId
     * @return
     */
    @PostMapping("/refuse")
    public Object refuse(Long ownerId, Long driverId) {
        try {
            //  拒绝就删除缓存表
            List<OwnerDriverRelationTemp> byOwnerIdAndDriverId = ownerDriverRelationTempRepository.findByOwnerIdAndDriverId(ownerId, driverId);
            for (OwnerDriverRelationTemp o : byOwnerIdAndDriverId) {
                ownerDriverRelationTempRepository.deleteById(o.getId());
            }
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }};
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 解绑司机
     *
     * @param ownerId
     * @param driverId
     * @return
     */
    @PostMapping("/inValid")
    public Object inValid(Long ownerId, Long driverId) {
        try {
            OwnerDriverRelation ownerDriverRelation = ownerDriverRelationServiceI.findByOwnerIdAndDriverIdAndValidIsTrue(ownerId, driverId);
            if (ownerDriverRelation != null) {
                ownerDriverRelation.setValid(false);
                ownerDriverRelation.setInvalidTime(new Date()); //  解绑时间

                ownerDriverRelationServiceI.save(ownerDriverRelation);
                return new HashMap<String, Object>() {{
                    put("status", "true");
                    put("msg", "操作成功");
                }};
            } else {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "分享不存在,解绑失败");
                }};
            }
        } catch (Exception e) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    public Object delete(Long id) {
        try {
            ownerDriverRelationServiceI.delete(id);
            return new HashMap<String, Object>() {{
                put("status", "true");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

}
