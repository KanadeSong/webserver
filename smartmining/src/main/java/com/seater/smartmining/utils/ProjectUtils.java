package com.seater.smartmining.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.CheckStatus;
import com.seater.smartmining.enums.GroupType;
import com.seater.smartmining.enums.ReportEnum;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.api.AutoApiUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResult;
import com.seater.smartmining.utils.interPhone.InterPhoneResultArr;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.entity.*;
import com.seater.user.service.*;
import com.seater.user.util.CommonUtil;
import com.sun.istack.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;


/**
 * @Description 项目用到的工具类
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/11 15:04
 */
@Slf4j
@Component
@RestController
@RequestMapping("/api/projectUtils")
public class ProjectUtils {

    @Autowired
    InterPhoneUtil interPhoneUtil;

    @Autowired
    ProjectCarServiceI projectCarServiceI;

    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;

    @Autowired
    SysUserProjectRoleServiceI sysUserProjectRoleServiceI;

    @Autowired
    SysUserServiceI sysUserServiceI;

    @Autowired
    EmployeeServiceI employeeServiceI;

    @Autowired
    SysRoleServiceI sysRoleServiceI;

    @Autowired
    SysRolePermissionServiceI sysRolePermissionServiceI;

    @Autowired
    SysPermissionServiceI sysPermissionServiceI;

    @Autowired
    WorkDateService workDateService;


    /**
     * 创建对应的对讲机账号,创建单个设备或者新建项目内单个人时用到
     *
     * @param projectId      项目id
     * @param userObjectId   使用者主键(人的id或者车的id)
     * @param userObjectType 使用者类型(有车和人,与上面的对应)
     * @return
     */
    public InterPhoneResultArr createTalkBackUserAccount(Long projectId, Long userObjectId, UserObjectType userObjectType, String name) {
        JSONObject result = new JSONObject();
        //  给车生成对讲机账号
        interPhoneUtil.initInterPhoneApiInfo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        InterPhoneResultArr departmentArr = (InterPhoneResultArr) interPhoneUtil.departmentFindByGroupId(jsonObject);
        JSONObject department = departmentArr.getData().getJSONObject(0);
        InterPhoneResultArr talkBackUser = new InterPhoneResultArr();
        if (department != null) {
//            InterPhoneResult department = (InterPhoneResult) interPhoneUtil.createDepartment(jsonObject);
            jsonObject.put("departmentId", department.getString("id"));
            jsonObject.put("id", userObjectId);
            jsonObject.put("projectId", projectId);
            jsonObject.put("name", name);

            //  对讲机账号id
            String accountId = "";
            //  对讲机账号
            String account = "";

            switch (userObjectType) {
                case OilCar:
                    jsonObject.put("type", UserObjectType.SlagCar);
                    talkBackUser = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    if (talkBackUser != null) {
                        accountId = JSONObject.parseObject(JSONObject.toJSONString(talkBackUser.getData().get(0))).getString("id");
                        account = talkBackUser.getData().getJSONObject(0).getString("fullValue");
                        result.put("accountId", accountId);
                        result.put("account", account);

                        try {
                            //  绑定账号到车上面
                            ProjectOtherDevice projectOtherDevice = projectOtherDeviceServiceI.get(userObjectId);
                            projectOtherDevice.setInterPhoneAccount(account);
                            projectOtherDevice.setInterPhoneAccountId(accountId);
                            projectOtherDeviceServiceI.save(projectOtherDevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                            talkBackUser.setMsg(e.getMessage());
                        }
                    }

                    break;
                case Person:
                    jsonObject.put("type", UserObjectType.Person);
                    talkBackUser = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    if (talkBackUser != null) {
                        accountId = JSONObject.parseObject(JSONObject.toJSONString(talkBackUser.getData().get(0))).getString("id");
                        account = talkBackUser.getData().getJSONObject(0).getString("fullValue");
                        result.put("accountId", accountId);
                        result.put("account", account);

                        try {
                            //  绑定账号到人-项目-角色关系上面
                            List<SysUserProjectRole> sysUserProjectRoles = sysUserProjectRoleServiceI.findByUserIdAndProjectIdAndValidIsTrue(userObjectId, projectId);
                            for (SysUserProjectRole sysUserProjectRole : sysUserProjectRoles) {
                                sysUserProjectRole.setInterPhoneAccount(account);
                                sysUserProjectRole.setInterPhoneAccountId(accountId);
                                sysUserProjectRoleServiceI.save(sysUserProjectRole);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            talkBackUser.setMsg(e.getMessage());
                        }
                    }

                    break;
                case SlagCar:
                    jsonObject.put("type", UserObjectType.SlagCar);
                    talkBackUser = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    if (talkBackUser != null) {
                        accountId = JSONObject.parseObject(JSONObject.toJSONString(talkBackUser.getData().get(0))).getString("id");
                        account = talkBackUser.getData().getJSONObject(0).getString("fullValue");
                        result.put("accountId", accountId);
                        result.put("account", account);

                        try {
                            //  绑定账号到车上面
                            ProjectCar projectCar = projectCarServiceI.get(userObjectId);
                            if (null != projectCar) {
                                projectCar.setInterPhoneAccount(account);
                                projectCar.setInterPhoneAccountId(accountId);
                                projectCarServiceI.save(projectCar);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            talkBackUser.setMsg(e.getMessage());
                        }
                    }

                    break;
                case DiggingMachine:
                    jsonObject.put("type", UserObjectType.DiggingMachine);
                    talkBackUser = (InterPhoneResultArr) interPhoneUtil.createTalkBackUser(jsonObject);
                    if (talkBackUser != null) {
                        accountId = JSONObject.parseObject(JSONObject.toJSONString(talkBackUser.getData().get(0))).getString("id");
                        account = talkBackUser.getData().getJSONObject(0).getString("fullValue");
                        result.put("accountId", accountId);
                        result.put("account", account);

                        try {
                            //  绑定账号到车上面
                            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineServiceI.get(userObjectId);
                            if (null != projectDiggingMachine) {
                                projectDiggingMachine.setInterPhoneAccount(account);
                                projectDiggingMachine.setInterPhoneAccountId(accountId);
                                projectDiggingMachineServiceI.save(projectDiggingMachine);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            talkBackUser.setMsg(e.getMessage());
                        }
                    }

                    break;
                default:
                    break;
            }
        }
        return talkBackUser;
//  给车生成对讲机账号 end
    }


    /**
     * 测试用
     *
     * @return
     */
    public static void main(String[] args) {

    }

    /**
     * 快速复制角色和权限到新增项目
     *
     * @param projectIdSource
     * @param projectIdTarget
     * @param request
     * @return
     */
    @PostMapping("/copyRole")
    public Object copyRole(Long projectIdSource, Long projectIdTarget, HttpServletRequest request, @RequestParam(required = false, defaultValue = "false") Boolean isCountdown) {

        try {

            ////////////////////////////////判断
            String back = "\n请先备份后删除数据再执行该动作";
            if (ObjectUtils.isEmpty(projectIdSource) || ObjectUtils.isEmpty(projectIdTarget)) {
                return CommonUtil.errorJson("操作失败,源项目id为空或者目标项目id为空,执行拷贝失败" + back);
            }
            Specification<SysRole> specFlag = new Specification<SysRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectIdTarget));
//                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            if (sysRoleServiceI.queryWx(specFlag).size() != 0) {
                return CommonUtil.errorJson("操作失败,目标项目已存在角色列表,执行拷贝失败" + back);
            }

            Specification<SysPermission> specFlag2 = new Specification<SysPermission>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysPermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectIdTarget));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            if (sysPermissionServiceI.queryWx(specFlag2).size() != 0) {
                return CommonUtil.errorJson("操作失败,目标项目已存在权限列表,执行拷贝失败" + back);
            }
            Specification<SysRolePermission> specFlag3 = new Specification<SysRolePermission>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRolePermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectIdTarget));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            if (sysRolePermissionServiceI.queryWx(specFlag3).size() != 0) {
                return CommonUtil.errorJson("操作失败,目标项目已存在角色-权限列表,执行拷贝失败" + back);
            }
            System.out.println("校验数据完成...即将即将进行权限复制,建议先备份数据再执行操作!!!!");

            /*if (isCountdown){
                int second = 5;
                for (int i = 0; i < second; i++) {
                    System.out.println("倒计时后开始  >>>>>>>>>>  " + (second - i));
                    Thread.sleep(1000);
                }
                System.out.println("开始复制角色和权限以及角色权限关系,请不要进行其他操作 >>>>>>>>>>");

            }*/

            long start = System.currentTimeMillis();
            ////////////////////////////////判断 end


            //  复制角色
            Specification<SysRole> spec = new Specification<SysRole>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<SysRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    list.add(cb.equal(root.get("projectId").as(Long.class), projectIdSource));
//                    list.add(cb.equal(root.get("valid").as(Boolean.class), true));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };

            //旧的角色列表
            List<SysRole> roleList = sysRoleServiceI.queryWx(spec);

            //新的角色列表
            List<SysRole> roleListNew = new ArrayList<>();

            //新的权限列表
            List<SysPermission> sysPermissionListNew = new ArrayList<>();


            for (SysRole sysRole : roleList) {
                SysRole role = new SysRole();
                BeanUtils.copyProperties(sysRole, role);
                role.setId(null);
                role.setAddTime(new Date());
                role.setProjectId(projectIdTarget);

                //新的roleId
                roleListNew.add(sysRoleServiceI.save(role));

                //查出原角色关联的权限
                List<Long> roleIds = new ArrayList<>();
                roleIds.add(sysRole.getId());
                List<SysPermission> permissionList = sysPermissionServiceI.getUserPermissionByRoleIds(roleIds);

                for (SysPermission permission : permissionList) {

                    Specification<SysPermission> specPermission = new Specification<SysPermission>() {
                        List<Predicate> list = new ArrayList<>();

                        @Override
                        public Predicate toPredicate(Root<SysPermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            list.add(cb.equal(root.get("projectId").as(Long.class), projectIdTarget));
                            list.add(cb.equal(root.get("permissionCode").as(String.class), permission.getPermissionCode()));
                            return cb.and(list.toArray(new Predicate[list.size()]));
                        }
                    };
                    List<SysPermission> sysPermissionList = sysPermissionServiceI.queryWx(specPermission);
                    if (sysPermissionList.size() == 0) {
                        SysPermission permissionNew = new SysPermission();
                        BeanUtils.copyProperties(permission, permissionNew);
                        permissionNew.setProjectId(projectIdTarget);
                        permissionNew.setAddTime(new Date());
                        permissionNew.setId(null);
                        sysPermissionListNew.add(sysPermissionServiceI.save(permissionNew));
                    }
                }

            }
            //---------↑↑↑↑↑↑↑↑↑复制了角色和权限↑↑↑↑↑↑↑↑---------------下面复制关系----------------------------


            for (SysRole role : roleList) {
                for (SysRole roleNew : roleListNew) {
                    if (role.getRoleName().equals(roleNew.getRoleName())) {
                        //查出原角色关联的权限
                        List<Long> roleIds = new ArrayList<>();
                        roleIds.add(role.getId());
                        List<SysPermission> permissionList = sysPermissionServiceI.getUserPermissionByRoleIds(roleIds);
                        //查出该角色的角色-权限列表
                        Specification<SysRolePermission> specRolePermission = new Specification<SysRolePermission>() {
                            List<Predicate> list = new ArrayList<Predicate>();

                            @Override
                            public Predicate toPredicate(Root<SysRolePermission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                                list.add(cb.equal(root.get("projectId").as(Long.class), projectIdSource));
                                list.add(cb.equal(root.get("roleId").as(Long.class), role.getId()));
                                return cb.and(list.toArray(new Predicate[list.size()]));
                            }
                        };
                        List<SysRolePermission> sysRolePermissionList = sysRolePermissionServiceI.queryWx(specRolePermission);

                        for (SysRolePermission rolePermission : sysRolePermissionList) {
                            for (SysPermission permission : permissionList) {
                                for (SysPermission permission1 : sysPermissionListNew) {
                                    if (rolePermission.getPermissionId().equals(permission.getId()) && permission.getPermissionCode().equals(permission1.getPermissionCode())) {
                                        SysRolePermission rolePermission1 = new SysRolePermission();
                                        rolePermission1.setProjectId(projectIdTarget);
                                        rolePermission1.setRoleId(roleNew.getId());
                                        rolePermission1.setValid(true);
                                        rolePermission1.setAddTime(new Date());
                                        rolePermission1.setId(null);
                                        rolePermission1.setPermissionId(permission1.getId());
                                        sysRolePermissionServiceI.save(rolePermission1);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            System.out.println("角色复制已完成     >>>>>>>>>>  总耗时:" + (System.currentTimeMillis() - start));
            return CommonUtil.successJson("角色复制已完成     >>>>>>>>>>   总耗时:" + (System.currentTimeMillis() - start));

        } catch (Exception e) {
            return CommonUtil.errorJson(e.getMessage());
        }
    }


    @Autowired
    SlagCarServiceI slagCarServiceI;

    @Autowired
    ProjectServiceI projectServiceI;

    /**
     * 同步车主项目内的渣车到小程序
     *
     * @param request
     * @return
     */
    @PostMapping("/bindProjectCar")
    @Transactional
    public Object bindProjectCar(HttpServletRequest request) throws IOException {

        long projectId = Long.parseLong(request.getHeader("projectId"));
        Project project = projectServiceI.get(projectId);
        List<ProjectCar> projectCarList = projectCarServiceI.getByProjectIdOrderById(projectId);

        List<SysUser> userList = sysUserServiceI.getAll();

        List<JSONObject> relateList = new ArrayList<>();
        Integer count = 0;
        for (SysUser user : userList) {
            for (ProjectCar projectCar : projectCarList) {

                if (user.getId().equals(projectCar.getOwnerId()) &&
                        user.getName().equals(projectCar.getOwnerName())) {

                    //  同步
                    //查询是否已存在绑定车辆,条件为项目内和项目外的车id一致
                    Specification<SlagCar> spec = new Specification<SlagCar>() {
                        List<Predicate> list = new ArrayList<>();

                        @Override
                        public Predicate toPredicate(Root<SlagCar> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            list.add(cb.equal(root.get("projectCarId"), projectCar.getId()));
                            return cb.and(list.toArray(new Predicate[list.size()]));
                        }
                    };
                    List<SlagCar> slagCarList = slagCarServiceI.queryWx(spec);
                    if (slagCarList.size() != 0) {
                        JSONObject relate = new JSONObject();
                        relate.put("msg", "已存在绑定关系");
                        JSONObject carInfo = new JSONObject();
                        carInfo.put("项目内:", projectCar);
                        carInfo.put("项目外:", slagCarList.get(0));
                        relate.put("carInfo", carInfo);
                        relateList.add(relate);
                        count = count + 1;
                        relate.put("count", count);
                        continue;
                    }

                    count = count + 1;
                    SlagCar slagCar = new SlagCar();
                    BeanUtils.copyProperties(projectCar, slagCar);
                    slagCar.setProjectCarId(projectCar.getId());
                    slagCar.setId(null);
                    slagCar.setOwnerId(user.getId());
                    slagCar.setOwnerName(user.getName());
                    slagCar.setCheckStatus(CheckStatus.Checked);
                    slagCar.setAddTime(new Date());
                    slagCar.setProjectId(projectCar.getProjectId());
                    slagCar.setProjectName(project.getName());
                    slagCar.setCodeInProject(projectCar.getCode());
                    SlagCar slagCarNew = slagCarServiceI.save(slagCar);

                    JSONObject relate = new JSONObject();
                    relate.put("msg", "不存在绑定关系,建立绑定关系");
                    relate.put("count", count);
                    JSONObject carInfo = new JSONObject();
                    carInfo.put("项目内:", projectCar);
                    carInfo.put("项目外:", slagCarNew);
                    relate.put("carInfo", carInfo);
                    relateList.add(relate);
                }
            }
        }

        System.out.println(JSONObject.toJSONString(relateList));
        return relateList;
    }

    @Autowired
    DiggingMachineServiceI diggingMachineServiceI;

    @PostMapping("/bindDiggingMachine")
    @Transactional
    public Object bindDiggingMachine(HttpServletRequest request) throws IOException {
        long projectId = Long.parseLong(request.getHeader("projectId"));

        Project project = projectServiceI.get(projectId);
        List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);

        List<SysUser> userList = sysUserServiceI.getAll();

        List<JSONObject> relateList = new ArrayList<>();
        Integer count = 0;
        for (SysUser user : userList) {
            for (ProjectDiggingMachine projectDiggingMachine : projectDiggingMachineList) {

                if (user.getId().equals(projectDiggingMachine.getOwnerId()) &&
                        user.getName().equals(projectDiggingMachine.getOwnerName())) {

                    //  同步
                    //查询是否已存在绑定车辆,条件为项目内和项目外的车id一致
                    Specification<DiggingMachine> spec = new Specification<DiggingMachine>() {
                        List<Predicate> list = new ArrayList<>();

                        @Override
                        public Predicate toPredicate(Root<DiggingMachine> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                            list.add(cb.equal(root.get("projectDiggingMachineId"), projectDiggingMachine.getId()));
                            return cb.and(list.toArray(new Predicate[list.size()]));
                        }
                    };
                    List<DiggingMachine> diggingMachineList = diggingMachineServiceI.queryWx(spec);
                    if (diggingMachineList.size() != 0) {
                        JSONObject relate = new JSONObject();
                        relate.put("msg", "已存在绑定关系");
                        JSONObject diggingMachineInfo = new JSONObject();
                        diggingMachineInfo.put("项目内:", projectDiggingMachine);
                        diggingMachineInfo.put("项目外:", diggingMachineList.get(0));
                        relate.put("diggingMachineInfo", diggingMachineInfo);
                        relateList.add(relate);
                        count = count + 1;
                        relate.put("count", count);
                        continue;
                    }

                    count = count + 1;
                    DiggingMachine diggingMachine = new DiggingMachine();
                    BeanUtils.copyProperties(projectDiggingMachine, diggingMachine);
                    diggingMachine.setProjectDiggingMachineId(projectDiggingMachine.getId());
                    diggingMachine.setId(null);
                    diggingMachine.setOwnerId(user.getId());
                    diggingMachine.setOwnerName(user.getName());
                    diggingMachine.setCheckStatus(CheckStatus.Checked);
                    diggingMachine.setAddTime(new Date());
                    diggingMachine.setProjectId(projectDiggingMachine.getProjectId());
                    diggingMachine.setProjectName(project.getName());
                    diggingMachine.setCodeInProject(projectDiggingMachine.getCode());
                    DiggingMachine diggingMachineNew = diggingMachineServiceI.save(diggingMachine);

                    JSONObject relate = new JSONObject();
                    relate.put("msg", "不存在绑定关系,建立绑定关系");
                    relate.put("count", count);
                    JSONObject diggingMachineInfo = new JSONObject();
                    diggingMachineInfo.put("项目内:", projectDiggingMachine);
                    diggingMachineInfo.put("项目外:", diggingMachineNew);
                    relate.put("diggingMachineInfo", diggingMachineInfo);
                    relateList.add(relate);
                }
            }
        }

        System.out.println(JSONObject.toJSONString(relateList));
        return relateList;
    }

    public static List<JSONObject> toJSONObjectList(List<Object> list) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (Object l : list) {
            jsonObjectList.add(JSONObject.parseObject(JSONObject.toJSONString(l)));
        }
        return jsonObjectList;
    }

    /**
     * 根据list中对象某些字段去重
     *
     * @param list 需要去重的list
     * @return 返回去重后的list
     */
    public static List<ProjectCarFillLog> removeDuplicateCase(List<ProjectCarFillLog> list) {
        Set<ProjectCarFillLog> set = new TreeSet<>(new Comparator<ProjectCarFillLog>() {
            @Override
            public int compare(ProjectCarFillLog o1, ProjectCarFillLog o2) {
                //字符串,则按照asicc码升序排列
                return o1.getEventId().compareTo(o2.getEventId());
            }
        });
        set.addAll(list);
        return new ArrayList<>(set);
    }

    /**
     * 判断给定日期报表和报表类型是否未发布在微信小程序
     *
     * @param projectId  项目id
     * @param reportDate 报表日期
     * @param reportEnum 报表类型
     * @return 判断结果
     */
    public static boolean isReportNotPublish(Long projectId, Date reportDate, ReportEnum reportEnum) {
        ReportPublish reportPublish = AutoApiUtils.returnReportPublishService().findByProjectIdAndReportDateAndReportEnum(projectId, DateUtil.beginOfDay(reportDate), reportEnum);
        return null == reportPublish || !reportPublish.getPublishWx();
    }

    /**
     * 在请求头获取projectId
     *
     * @param request 请求头
     * @return 项目id
     */
    public static Long getProjectId(HttpServletRequest request) {
        if (null != request && null != request.getHeader("projectId")) {
            return Long.parseLong(request.getHeader("projectId"));
        }
        return null;
    }

    /**
     * 根据项目和类型创建默认对讲群组名称
     *
     * @param groupType 类型
     * @param projectId 项目id
     * @return 组名称
     */
    public static String groupName(GroupType groupType, Long projectId) {
        return groupType.getValue() + "-" + projectId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }


    public static String startDateByType(String type) {
        Date date = new Date();
        String dateStr = "";
        switch (type) {
            case "day":
                date = DateUtil.beginOfDay(date);
                break;
            case "week":
                date = DateUtil.beginOfWeek(date);
                break;
            case "month":
                date = DateUtil.beginOfMonth(date);
                break;
            default:
                break;
        }
        dateStr = String.valueOf(date.getTime());
        return dateStr;
    }

    public static String endDateByType(String type) {
        Date date = new Date();
        String dateStr = "";
        switch (type) {
            case "day":
                date = DateUtil.endOfDay(date);
                break;
            case "week":
                date = DateUtil.endOfWeek(date);
                break;
            case "month":
                date = DateUtil.endOfMonth(date);
                break;
            default:
                break;
        }
        dateStr = String.valueOf(date.getTime());
        return dateStr;
    }

    /**
     * 按照type 生成key
     *
     * @param type day week month
     * @return 缓存用的key
     */
    public static String cacheKeyByType(String type) {
        String start = startDateByType(type);
        String end = endDateByType(type);
        return start + ":" + end;
    }

    /**
     * 根据给定时间段生成时间段差值最大的key(开始时间的当天开始,结束时间的当天结束)
     *
     * @param start 开始日期
     * @param end   结束日期
     * @return start:end
     */
    public static String cacheKeyByStartAndEnd(Date start, Date end) {
        start = DateUtil.beginOfDay(start);
        end = DateUtil.endOfDay(end);
        return start.getTime() + ":" + end.getTime();
    }

    /**
     * ["201913","2019226","2019228","2019229","2019230"]
     *
     * @param manager ["201913","2019226","2019228","2019229","2019230"]
     * @return 列表
     */
    public static List<Object> sortManager(String manager) {
        if (manager.length() == 0) {
            return new ArrayList<>();
        }
        String[] split = manager.replace("[", "").
                replace("]", "").
                replace(" ", "").
                replace("\\", "").
                split(",");
        //排序
        Arrays.sort(split);
        List<Object> list = new ArrayList<>();
        for (String s : split) {
            list.add(s + "");
        }
        return list;
    }

    /**
     * @param manager ["201913","2019226","2019228","2019229","2019230"]
     * @return 列表的字符串
     */
    public static String sortManagerToString(String manager) {
        return Arrays.toString(sortManager(manager).toArray()).replace(" ", "");
    }

    /**
     * @param manager ["201913","2019226","2019228","2019229","2019230"]
     * @return 数字列表 [201913,2019226,2019228,2019229,2019230]
     */
    public static List<Long> sortManagerToNumber(String manager) {
        if (manager.length() == 0) {
            return new ArrayList<>();
        }
        String[] split = manager.replace("[", "").
                replace("]", "").
                replace(" ", "").
                replace("\\", "").
                replace("\"", "").
                trim().
                split(",");
        List<Long> list = new ArrayList<>();
        for (String s : split) {
            if (!StringUtils.isEmpty(s)) {
                list.add(Long.parseLong(s));
            }
        }
        Arrays.sort(list.toArray());
        return list;
    }

    /**
     * ["xxx","yyy"]  ===>    xxx,yyy
     *
     * @param managerName ["xxx","yyy"]
     * @return xxx, yyy
     */
    public static String managerName(String managerName) {
        return managerName.
                replace("[", "").
                replace("]", "").
                replace("\"", "").
                replace(" ", "").
                trim();
    }
}
