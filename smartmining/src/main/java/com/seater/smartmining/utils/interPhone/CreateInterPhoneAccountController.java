package com.seater.smartmining.utils.interPhone;

import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectDiggingMachine;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 为给定项目id中的所有设备添加对讲机账号
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/16 15:43
 */
//@RestController
//@RequestMapping("/api/createInterPhoneAccount")
public class CreateInterPhoneAccountController {

    @Autowired
    private ProjectCarServiceI projectCarServiceI;
    
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @Autowired
    private InterPhoneUtil interPhoneUtil;


    @PostMapping("/createAllSlagCarAccount")
    public Object createAllSlagCarAccount(HttpServletRequest request) {

        try {
            long projectId = Long.parseLong(request.getHeader("projectId"));
            interPhoneUtil.initInterPhoneApiInfo();
            JSONObject description = new JSONObject();
            description.put("projectId", projectId);
            //  建部门
            InterPhoneResultArr dept = (InterPhoneResultArr) interPhoneUtil.departmentFindByGroupId(description);

            List<ProjectCar> projectCarsResult = new ArrayList<>();
            List<InterPhoneResultArr> interPhoneResult= new ArrayList<>();
            JSONObject totalResult = new JSONObject();
            List<JSONObject> totalResultList = new ArrayList<>();
            Specification<ProjectCar> spec = new Specification<ProjectCar>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCar> projectCarList = projectCarServiceI.queryWx(spec);
            
            for (ProjectCar projectCar : projectCarList) {
                //  没有账号的就新建
                if (ObjectUtils.isEmpty(projectCar.getInterPhoneAccount()) || ObjectUtils.isEmpty(projectCar.getInterPhoneAccountId())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("projectId",Long.parseLong(request.getHeader("projectId")));
                    jsonObject.put("departmentId",dept.getData().getJSONObject(0).getString("id"));
                    jsonObject.put("id", projectCar.getId());
                    jsonObject.put("type", UserObjectType.SlagCar);
                    jsonObject.put("name", projectCar.getCode());
                    InterPhoneResultArr resultArr = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    projectCar.setInterPhoneAccount(resultArr.getData().getJSONObject(0).getJSONObject("talkbackNumber").getString("fullValue"));
                    projectCar.setInterPhoneAccountId(resultArr.getData().getJSONObject(0).getString("id"));
                    projectCarsResult.add(projectCarServiceI.save(projectCar));
                    interPhoneResult.add(resultArr);
                    totalResult.put("projectCarsResult",projectCarsResult);
                    totalResult.put("interPhoneResult",interPhoneResult);
                    totalResultList.add(totalResult);
                }
            }
            return totalResultList;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/createAllDiggingMachineAccount")
    public Object createAllDiggingMachineAccount(HttpServletRequest request) {

        try {
            long projectId = Long.parseLong(request.getHeader("projectId"));
            interPhoneUtil.initInterPhoneApiInfo();
            JSONObject description = new JSONObject();
            description.put("projectId", projectId);
            //  建部门
            InterPhoneResultArr dept = (InterPhoneResultArr) interPhoneUtil.departmentFindByGroupId(description);

            List<ProjectDiggingMachine> projectDiggingMachineResult = new ArrayList<>();
            List<InterPhoneResultArr> interPhoneResult= new ArrayList<>();
            JSONObject totalResult = new JSONObject();
            List<JSONObject> totalResultList = new ArrayList<>();
            Specification<ProjectDiggingMachine> spec = new Specification<ProjectDiggingMachine>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectDiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    list.add(cb.equal(root.get("projectId").as(Long.class), projectId));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(Long.parseLong(request.getHeader("projectId")));

            for (ProjectDiggingMachine projectDiggingMachine : projectDiggingMachineList) {
                //  没有账号的就新建
                if (ObjectUtils.isEmpty(projectDiggingMachine.getInterPhoneAccount()) || ObjectUtils.isEmpty(projectDiggingMachine.getInterPhoneAccountId())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("projectId",Long.parseLong(request.getHeader("projectId")));
                    jsonObject.put("departmentId",dept.getData().getJSONObject(0).getString("id"));
                    jsonObject.put("id", projectDiggingMachine.getId());
                    jsonObject.put("type", UserObjectType.DiggingMachine);
                    jsonObject.put("name", projectDiggingMachine.getCode());
                    InterPhoneResultArr resultArr = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    projectDiggingMachine.setInterPhoneAccount(resultArr.getData().getJSONObject(0).getJSONObject("talkbackNumber").getString("fullValue"));
                    projectDiggingMachine.setInterPhoneAccountId(resultArr.getData().getJSONObject(0).getString("id"));
                    projectDiggingMachineResult.add(projectDiggingMachineServiceI.save(projectDiggingMachine));
                    interPhoneResult.add(resultArr);
                    totalResult.put("projectDiggingMachineResult",projectDiggingMachineResult);
                    totalResult.put("interPhoneResult",interPhoneResult);
                    totalResultList.add(totalResult);
                }
            }
            return totalResultList;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    
}
