package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.ProjectCarWorkInfo;
import com.seater.smartmining.entity.ProjectDiggingMachineMaterial;
import com.seater.smartmining.entity.ProjectMaterial;
import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.service.ProjectCarWorkInfoServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineMaterialServiceI;
import com.seater.smartmining.service.ProjectMaterialServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.*;

@RestController
@RequestMapping("/api/projectMaterial")
public class ProjectMaterialController {
    @Autowired
    private ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    private ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    private ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }

    @RequestMapping("/query")
//    @RequiresPermissions(PermissionConstants.PROJECT_MATERIAL_QUERY)
    public Object query(HttpServletRequest request, Boolean isAll)
    {
        try {
            if(isAll != null && isAll)
                return projectMaterialServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            Specification<ProjectMaterial> spec = new Specification<ProjectMaterial>() {
                @Override
                public Predicate toPredicate(Root<ProjectMaterial> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    query.orderBy(cb.asc(root.get("id").as(Long.class)));
                    return cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId")));
                }
            };

            return projectMaterialServiceI.query(spec);
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/save")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_MATERIAL_SAVE)
    public Object save(ProjectMaterial projectMaterial, HttpServletRequest request)
    {
        try {
            projectMaterial.setProjectId(Long.parseLong(request.getHeader("projectId")));
            projectMaterialServiceI.save(projectMaterial);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/delete")
    @Transactional
    @RequiresPermissions(PermissionConstants.PROJECT_MATERIAL_DELETE)
    public Object delete(Long id)
    {
        try {
            projectMaterialServiceI.delete(id);
            return new HashMap<String, Object>() {{put("status", "true");}};
        }
        catch (Exception exception)
        {
            return new HashMap<String, Object>() {{put("status", "false"); put("msg", exception.getMessage());}};
        }
    }

    @RequestMapping("/material")
    public Result reportMaterial(HttpServletRequest request, @RequestParam Integer choose, Date startTime, Date endTime){
        Long projectId = Long.parseLong(request.getHeader("projectId"));
        if (endTime == null || endTime.getTime() == 0){
            endTime = DateUtils.subtractionOneDay(new Date());
        }
        endTime = DateUtils.getEndDateByNow(endTime);
        if (startTime == null || startTime.getTime() == 0){
            if(choose == 1) {
                startTime = DateUtils.subtractionOneDay(endTime);
            } else if(choose == 2) {
                startTime = DateUtils.getHalfYearAgo(endTime);
                startTime = DateUtils.getStartDate(startTime);
            } else {
                startTime = new Date();
            }
        }else{
            startTime = DateUtils.getEndDateByNow(startTime);
            startTime = DateUtils.subtractionOneDay(startTime);
        }
        List<Map> countMapList = new ArrayList<>();
        switch (choose){
            case 1:
                countMapList = projectCarWorkInfoServiceI.getByProjectIdAndTime(projectId, endTime);
                break;
            case 2:
                countMapList = projectCarWorkInfoServiceI.getByProjectIdAndBetweenTime(projectId, startTime, endTime);
                break;
            case 3:
                countMapList = projectCarWorkInfoServiceI.getByProjectIdAndBetweenTimeHistory(projectId, endTime);
                break;
        }
        //创建物料索引
        Map<Long, Integer> countIndex = new HashMap<>();
        //总数量
        Long count = 0L;
        //总方量
        BigDecimal cubic = new BigDecimal(0);
        for(int i = 0; i < countMapList.size(); i++) {
            count = countMapList.get(i).get("count") != null ? Long.parseLong(countMapList.get(i).get("count").toString()) + count : count;
            cubic = countMapList.get(i).get("cubic") != null ? new BigDecimal((float) Long.parseLong(countMapList.get(i).get("cubic").toString()) / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP).add(cubic) : cubic;
            Long materialId = countMapList.get(i).get("material_id") != null ? Long.parseLong(countMapList.get(i).get("material_id").toString()) : 0L;
            countIndex.put(materialId, i);
        }
        //查询所有的物料
        List<ProjectMaterial> materialList = projectMaterialServiceI.getByProjectIdOrderById(projectId);
        List<Map> resultList = new ArrayList<>();
        for(int i = 0; i < materialList.size(); i++){
            Map map = new HashMap();
            map.put("materialId", materialList.get(i).getId());
            map.put("materialName", materialList.get(i).getName());
            Integer index = countIndex.get(materialList.get(i).getId());
            Long countNum = index != null ? Long.parseLong(countMapList.get(index).get("count").toString()) : 0L;
            BigDecimal cubicNum = index != null ? new BigDecimal((float)Long.parseLong(countMapList.get(index).get("cubic").toString()) / 1000000L).setScale(2, BigDecimal.ROUND_HALF_UP) : new BigDecimal(0);
            map.put("count", countNum);
            map.put("cubic", cubicNum);
            map.put("totalCount", count);
            map.put("totalCubic", cubic);
            resultList.add(map);
        }
        return Result.ok(resultList);
    }
}
