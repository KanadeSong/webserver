package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectPlace;
import com.seater.smartmining.enums.PlaceEnum;
import com.seater.smartmining.enums.PlaceStatusEnum;
import com.seater.smartmining.service.ProjectPlaceServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.smartmining.utils.string.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:48
 */
@RestController
@RequestMapping("/api/projectplace")
public class ProjectPlaceController {

    @Autowired
    private ProjectPlaceServiceI projectPlaceServiceI;

    @RequestMapping(value = "/save", produces = "application/json")
    @Transactional
    public Result save(HttpServletRequest request, @RequestBody List<ProjectPlace> projectPlaceList){
        try{
            for(ProjectPlace projectPlace : projectPlaceList) {
                Long projectId = Long.parseLong(request.getHeader("projectId"));
                projectPlace.setProjectId(projectId);
                projectPlace.setCreateDate(new Date());
                projectPlaceServiceI.save(projectPlace);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, String name, PlaceEnum place, PlaceStatusEnum placeStatus, Integer current, Integer pageSize){
        try{
            Long projectId = Long.parseLong(request.getHeader("projectId"));
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
            Specification<ProjectPlace> spec = new Specification<ProjectPlace>() {
                List<Predicate> list = new ArrayList<Predicate>();
                @Override
                public Predicate toPredicate(Root<ProjectPlace> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    if(StringUtils.isNotEmpty(name))
                        list.add(cb.like(root.get("placeName").as(String.class), "%"+ name +"%"));
                    if(place != null)
                        list.add(cb.equal(root.get("place").as(Integer.class), place.getAlians()));
                    if(placeStatus != null)
                        list.add(cb.equal(root.get("placeStatus").as(Integer.class), placeStatus.getAlians()));
                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return Result.ok(projectPlaceServiceI.query(spec, PageRequest.of(cur, page)));
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    @Transactional
    public Result delete(@RequestBody List<Long> ids){
        try{
            projectPlaceServiceI.delete(ids);
            return Result.ok();
        }catch (Exception e){
            return Result.error(e.getMessage());
        }
    }
}
