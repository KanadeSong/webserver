package com.seater.smartmining.controller;

import com.seater.helpers.DateEditor;
import com.seater.helpers.TimeEditor;
import com.seater.smartmining.entity.DiggingMachine;
import com.seater.smartmining.entity.ProjectDiggingMachine;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.service.DiggingMachineServiceI;
import com.seater.smartmining.service.DiggingMachineServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 微信小程序车主创建/查询挖机
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/30 11:10
 */
@RestController
@RequestMapping("/api/diggingMachine")
public class DiggingMachineWxController {

    @Autowired
    private DiggingMachineServiceI diggingMachineServiceI;
    
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DateEditor());
        binder.registerCustomEditor(Time.class, new TimeEditor());
    }
    
    /**
     * 车主找车 (项目外)
     * @param current
     * @param pageSize
     * @param userId
     * @param isAll
     * @param carGroup
     * @return
     */
    @PostMapping("/queryWx")
    public Object queryWx(Integer current,String checkStatus, Integer pageSize, @RequestParam(name = "userId", required = false) Long userId, Boolean isAll, String carGroup) {
        try {
            int cur = (current == null || current < 1) ? 0 : current - 1;
            int page = (pageSize == null || pageSize < 0) ? 10 : pageSize;

            if (isAll != null && isAll)
                return diggingMachineServiceI.findByOwnerId(userId, PageRequest.of(cur, page));

            Specification<DiggingMachine> spec = new Specification<DiggingMachine>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<DiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    if (!ObjectUtils.isEmpty(userId)){
                        list.add(cb.equal(root.get("ownerId").as(Long.class), userId));
                    }
                    if (carGroup != null && !carGroup.isEmpty()) {
                        list.add(cb.like(root.get("carGroup").as(String.class), "%" + carGroup+ "%"));
                    }
                    if (CheckStatus.UnCheck.getValue().equals(checkStatus)){
                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.UnCheck));
                    } /*else {
                        list.add(cb.equal(root.get("checkStatus").as(CheckStatus.class), CheckStatus.Checked));
                    }*/
//                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            return diggingMachineServiceI.query(spec, PageRequest.of(cur, page));
        } catch (Exception exception) {
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", exception.getMessage());
            }};
        }
    }

    /**
     * 新增挖机
     *
     * @param diggingMachine
     * @return
     */
    @PostMapping("/saveWx")
    public Object saveWx(@RequestBody DiggingMachine diggingMachine) {
        try {
            diggingMachine.setValid(false);
            diggingMachine.setCheckStatus(CheckStatus.UnCheck);
            diggingMachineServiceI.save(diggingMachine);
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
     * 生/失效
     * @param id
     * @return
     */
    @PostMapping("/inValidWx")
    public Object inValid(Long id){

        try{
            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(id);
            projectDiggingMachine.setVaild(!projectDiggingMachine.getVaild()); 
            projectDiggingMachineServiceI.save(projectDiggingMachine);
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg","操作成功");
            }};
        }catch (Exception e){
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

    /**
     * 项目外可直接删除
     * @param id
     * @return
     */
    @PostMapping("/deleteWx")
    public Object deleteWx(Long id){
        try{
            diggingMachineServiceI.delete(id);
            return new HashMap<String, Object>() {{
                put("status", "true");
                put("msg", "操作成功");
            }}; 
        }catch (Exception e){
            return new HashMap<String, Object>() {{
                put("status", "false");
                put("msg", e.getMessage());
            }};
        }
    }

}
