package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.SlagCar;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.SlagCarServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.service.SysUserServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 微信小程序车主创建/查询渣车
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:10
 */
@RestController
@RequestMapping("/api/slagCar")
public class SlagCarWxController {

    @Autowired
    private SlagCarServiceI slagCarServiceI;
    @Autowired
    private SysUserServiceI sysUserServiceI;
    @Autowired
    private ProjectCarServiceI projectCarServiceI;

    /**
     * 车主找车 (项目外)
     *
     * @param current
     * @param pageSize
     * @param userId
     * @param isAll
     * @param carGroup
     * @return
     */
    @PostMapping("/queryWx")
    public Object queryWx(Integer current, Integer pageSize, @RequestParam(name = "userId", required = false) Long userId, Long driverId, Boolean isAll, String carGroup) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            if (isAll != null && isAll)
                return slagCarServiceI.findByOwnerId(userId, PageRequest.of(cur, page));

            Specification<SlagCar> spec = new Specification<SlagCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SlagCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!ObjectUtils.isEmpty(userId)) {
                        list.add(cb.equal(root.get("ownerId").as(Long.class), userId));
                    }
                    if (carGroup != null && !carGroup.isEmpty()) {
                        list.add(cb.like(root.get("carGroup").as(String.class), "%" + carGroup + "%"));
                    }
                    if(!ObjectUtils.isEmpty(driverId))
                        list.add(cb.equal(root.get("driverId").as(Long.class), driverId));
//                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            Page<SlagCar> resultPage = slagCarServiceI.query(spec, PageRequest.of(cur, page));
            List<SlagCar> resultList = resultPage.getContent();
            Long totalCount = resultPage.getTotalElements();
            int totalPages = resultPage.getTotalPages();
            if(resultList == null || resultList.size() < 1){
                resultList = new ArrayList<>();
                Specification<ProjectCar> specTwo = new Specification<ProjectCar>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                        if (!ObjectUtils.isEmpty(userId)) {
                            list.add(cb.equal(root.get("ownerId").as(Long.class), userId));
                        }
                        if (carGroup != null && !carGroup.isEmpty()) {
                            list.add(cb.like(root.get("carGroup").as(String.class), "%" + carGroup + "%"));
                        }
                        if(!ObjectUtils.isEmpty(driverId))
                            list.add(cb.equal(root.get("driverId").as(Long.class), driverId));
//                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                        query.orderBy(cb.desc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                Page<ProjectCar> projectCarPage = projectCarServiceI.query(specTwo, PageRequest.of(cur, page));
                List<ProjectCar> projectCarList = projectCarPage.getContent();
                totalCount = projectCarPage.getTotalElements();
                totalPages = projectCarPage.getTotalPages();
                if(projectCarList != null){
                    for(ProjectCar car : projectCarList){
                        SlagCar slagCar = new SlagCar();
                        slagCar.setId(car.getId());
                        slagCar.setDriverId(car.getDriverId());
                        slagCar.setDriverName(car.getDriverName());
                        slagCar.setOwnerId(car.getOwnerId());
                        slagCar.setOwnerName(car.getOwnerName());
                        slagCar.setBrandId(car.getBrandId());
                        slagCar.setBrandName(car.getBrandName());
                        slagCar.setModelId(car.getModelId());
                        slagCar.setModelName(car.getModelName());
                        slagCar.setLength(car.getLength());
                        slagCar.setWidth(car.getWidth());
                        slagCar.setHeight(car.getHeight());
                        slagCar.setThickness(car.getThickness());
                        slagCar.setCalcCapacity(car.getCalcCapacity());
                        slagCar.setValid(car.getVaild());
                        resultList.add(slagCar);
                    }
                }
            }
            Map map = new HashMap();
            map.put("content", resultList);
            map.put("totalElements", totalCount);
            map.put("totalPages", totalPages);
            return map;
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 新增渣车
     *
     * @param slagCar
     * @return
     */
    @PostMapping("/saveWx")
    public Object saveWx(@RequestBody SlagCar slagCar) {
        try {
//            if (slagCarServiceI.findByOwnerIdAndDriverId(slagCar.getOwnerId(), slagCar.getDriverId()).size() > 0) {
//                return new HashMap<String, Object>() {{
//                    put("status", "false");
//                    put("msg", "已存在司机");
//                }};
//            }
            if (StringUtils.isEmpty(slagCar.getCalcCapacity())) {
                return new HashMap<String, Object>() {{
                    put("status", "false");
                    put("msg", "容积不能为空");
                }};
            }
            //  项目外的车未生效和未检查
            slagCar.setValid(false);
            slagCar.setCheckStatus(CheckStatus.UnCheck);
            slagCarServiceI.save(slagCar);
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }};
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }


    /**
     * 项目外可直接删除
     *
     * @param id
     * @return
     */
    @PostMapping("/deleteWx")
    public Object deleteWx(Long id) {
        try {
            slagCarServiceI.delete(id);
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

    @RequestMapping("/saveDriver")
    public Result saveDriver(@RequestParam String openId, @RequestParam Long carId) {
        try {
            SlagCar slagCar = slagCarServiceI.get(carId);
            if(slagCar == null)
                return Result.error("车辆ID" + carId + "不存在");
            SysUser user = sysUserServiceI.getByOpenId(openId);
            if(user == null)
                return Result.error("用户openId" + openId + "不存在");
            slagCar.setDriverId(user.getId().toString());
            slagCar.setDriverName(user.getName());
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

}
