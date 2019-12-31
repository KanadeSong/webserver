package com.seater.smartmining.controller;

import com.seater.smartmining.entity.ProjectAperture;
import com.seater.smartmining.entity.ProjectBlastWorkInfo;
import com.seater.smartmining.entity.ProjectExplosive;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.ProjectApertureServiceI;
import com.seater.smartmining.service.ProjectBlastWorkInfoServiceI;
import com.seater.smartmining.service.ProjectExplosiveServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.SysUser;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:爆破工作信息表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/12 0012 13:01
 */
@RestController
@RequestMapping("/api/projectBlastWorkInfo")
public class ProjectBlastWorkInfoController extends BaseController{

    @Autowired
    private ProjectBlastWorkInfoServiceI projectBlastWorkInfoServiceI;
    @Autowired
    private ProjectApertureServiceI projectApertureServiceI;
    @Autowired
    private ProjectExplosiveServiceI projectExplosiveServiceI;
    @Autowired
    private WorkDateService workDateService;

    @RequestMapping("/save")
    public Result save(HttpServletRequest request, ProjectBlastWorkInfo log){
        try {
            Long projectId = CommonUtil.getProjectId(request);
            SysUser sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_INFO);
            Date date = new Date();
            Map<String, Date> dateMap = workDateService.getWorkTime(projectId, date);
            Date start = dateMap.get("start");
            if(date.getTime() < start.getTime())
                start = DateUtils.subtractionOneDay(start);
            Date dateIdentification = DateUtils.createReportDateByMonth(start);
            //钻孔单价
            BigDecimal priceByAperture = BigDecimal.ZERO;
            //炸药单价
            BigDecimal priceByExplosive = BigDecimal.ZERO;
            if (log.getApertureId() != null && log.getApertureId() != 0) {
                ProjectAperture aperture = projectApertureServiceI.get(log.getApertureId());
                log.setApertureName(aperture.getName());
                priceByAperture = aperture.getPrice();
            }
            if(log.getExplosiveId() != null && log.getExplosiveId() != 0){
                ProjectExplosive explosive = projectExplosiveServiceI.get(log.getExplosiveId());
                log.setExplosiveName(explosive.getName());
                priceByExplosive = explosive.getPrice();
            }
            BigDecimal amountByAperture = priceByAperture.multiply(log.getApertureMeters()).setScale(2, BigDecimal.ROUND_CEILING);
            BigDecimal amountByExplosive = priceByExplosive.multiply(log.getExplosiveCount()).setScale(2, BigDecimal.ROUND_CEILING);
            BigDecimal amount = amountByAperture.add(amountByExplosive);
            log.setAmount(amount);
            log.setProjectId(projectId);
            log.setCreateId(sysUser.getId());
            log.setCreateName(sysUser.getAccount());
            log.setCreateTime(date);
            log.setDateIdentification(dateIdentification);
            projectBlastWorkInfoServiceI.save(log);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok();
    }

    @RequestMapping(value = "/delete", produces = "application/json")
    public Result delete(@RequestBody List<Long> ids){
        projectBlastWorkInfoServiceI.delete(ids);
        return Result.ok();
    }

    @RequestMapping("/query")
    public Result query(HttpServletRequest request, Integer current, Integer pageSize, String carCode, Date startTime, Date endTime, Long explosiveId){
        int cur = (current == null  || current < 1) ? 0 : current - 1;
        int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;
        Long projectId = CommonUtil.getProjectId(request);
        Specification<ProjectBlastWorkInfo> spec = new Specification<ProjectBlastWorkInfo>() {
            List<Predicate> list = new ArrayList<Predicate>();
            @Override
            public Predicate toPredicate(Root<ProjectBlastWorkInfo> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                if (projectId != null)
                    list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                if(StringUtils.isNotEmpty(carCode))
                    list.add(cb.like(root.get("carCode").as(String.class), "%"+ carCode +"%"));
                if(explosiveId != null)
                    list.add(cb.equal(root.get("explosiveId").as(Long.class), explosiveId));
                if(startTime != null && endTime != null)
                    list.add(cb.between(root.get("dateIdentification").as(Date.class), startTime, endTime));
                query.orderBy(cb.asc(root.get("id").as(Long.class)));
                return cb.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return Result.ok(projectBlastWorkInfoServiceI.query(PageRequest.of(cur, page), spec));
    }
}
