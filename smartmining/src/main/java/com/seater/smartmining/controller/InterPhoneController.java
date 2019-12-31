package com.seater.smartmining.controller;

import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.InterPhoneApply;
import com.seater.smartmining.enums.ApplyStatus;
import com.seater.smartmining.service.InterPhoneApplyServiceI;
import com.seater.smartmining.service.InterPhoneServiceI;
import com.seater.smartmining.utils.ProjectUtils;
import com.seater.smartmining.utils.interPhone.InterPhoneResultArr;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import com.seater.user.util.constants.Constants;
import com.seater.user.util.constants.PermissionConstants;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Description TODO 对讲机预留接口
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/16 14:43
 */
@RestController
@RequestMapping("/api/interPhone")
@RequiresPermissions(value = PermissionConstants.INTER_PHONE_ACCESS)
public class InterPhoneController {

    @Autowired
    private InterPhoneServiceI interPhoneServiceI;

    @Autowired
    private InterPhoneUtil interPhoneUtil;

    @Autowired
    private ProjectUtils projectUtils;

    @Autowired
    private InterPhoneApplyServiceI interPhoneApplyServiceI;


    @PostMapping("/test")
    public Object test() {
        interPhoneServiceI.test();
        return null;
    }

    @PostMapping("/authorize")
    public Object authorize(String uid) {
//        interPhoneServiceI.authorize(uid);
        InterPhoneUtil interPhoneUtil = new InterPhoneUtil();
        interPhoneUtil.authorize();
        return null;
    }

    @PostMapping("/login")
    public Object login() {
        return interPhoneServiceI.login(new JSONObject());
    }

    @PostMapping("/getDepmartPage")
    public Object getDepmartPage() {
        interPhoneServiceI.getDepmartPage(new JSONObject());
        return null;
    }

    @PostMapping("/getDepmartList")
    public Object getDepmartList(HttpServletRequest request) {
        Long projectId = CommonUtil.getProjectId(request);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        return interPhoneServiceI.getDepmartList(jsonObject);
    }

    @PostMapping("/addDepmart")
    public Object addDepmart(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.addDepmart(jsonObject);
    }

    @PostMapping("/updateDepmart")
    public Object updateDepmart(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.updateDepmart(jsonObject);
    }

    @PostMapping("/deleteDepmart")
    public Object deleteDepmart(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.deleteDepmart(jsonObject);
    }

    @PostMapping("/addTalkBackGroup")
    public Object addTalkBackGroup(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.addTalkBackGroup(jsonObject);
    }

    @PostMapping("/updateTalkBackGroup")
    public Object updateTalkBackGroup(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.updateTalkBackGroup(jsonObject);
    }

    @PostMapping("/getTalkBackGroupPage")
    public Object getTalkBackGroupPage(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.getTalkBackGroupPage(jsonObject);
    }

    @PostMapping("/deleteTalkBackGroup")
    public Object deleteTalkBackGroup(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.deleteTalkBackGroup(jsonObject);
    }

    @PostMapping("/getTalkBackUserPage")
    public Object getTalkBackUserPage(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.getTalkBackUserPage(jsonObject);
    }

    @PostMapping("/addTalkBackUser")
    public Object addTalkBackUser(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.addTalkBackUser(jsonObject);
    }

    @PostMapping("/updateTalkBackUser")
    public Object updateTalkBackUser(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.updateTalkBackUser(jsonObject);
    }

    @PostMapping("/deleteTalkBackUser")
    public Object deleteTalkBackUser(@RequestBody JSONObject jsonObject) {
        return interPhoneServiceI.deleteTalkBackUser(jsonObject);
    }

    /**
     * web端创建对讲机账号
     *
     * @param projectId      项目id
     * @param userObjectId   使用者id
     * @param userObjectType 使用者类型
     * @param name           账号中文名
     * @return 返回
     */
    @PostMapping("/createTalkBackUser")
    public Object createTalkBackUser(Long projectId, Long userObjectId, UserObjectType userObjectType, String name) {
        return projectUtils.createTalkBackUserAccount(projectId, userObjectId, userObjectType, name);
    }

    /**
     * 同意申请
     *
     * @param jsonObject 申请
     * @return
     */
    @PostMapping("/passApply")
    public Object passApply(@RequestBody JSONObject jsonObject) {
        try {
            List<Long> ids = jsonObject.getJSONArray("ids").toJavaList(Long.class);
            Boolean isBatchRenew = jsonObject.getBoolean("isBatchRenew");

            for (Long id : ids) {
                InterPhoneApply interPhoneApply = interPhoneApplyServiceI.get(id);
                if (!StringUtils.isEmpty(interPhoneApply.getInterPhoneAccount()) && !StringUtils.isEmpty(interPhoneApply.getInterPhoneAccountId())) {
                    return Result.error("已绑定对讲账号,id" + id);
                }
            }
            interPhoneUtil.initInterPhoneApiInfo();

            /*//  判断创建账号的额度是否足够
            if (Constants.isCheckLimit) {
                JSONObject limit = (JSONObject) interPhoneUtil.getCreditByGroupId(null);
                if (null != limit &&
                        null != limit.getInteger("data") &&
                        limit.getInteger("data") <= Constants.INTER_PHONE_CREDIT_MONTH_PER_ACC)
                    Result.error("创建账号额度不足,当前额度为:" + limit.get("data"));
            }*/
            List<JSONObject> results = new ArrayList<>();
            ;
            for (Long id : ids) {
                InterPhoneApply interPhoneApply = interPhoneApplyServiceI.get(id);
                InterPhoneResultArr talkBackUserAccount = projectUtils.createTalkBackUserAccount(interPhoneApply.getProjectId(), interPhoneApply.getUserObjectId(), interPhoneApply.getUserObjectType(), interPhoneApply.getAccountName());
                interPhoneApply.setInterPhoneAccount(talkBackUserAccount.getData().getJSONObject(0).getString("fullValue"));
                interPhoneApply.setInterPhoneAccountId(talkBackUserAccount.getData().getJSONObject(0).getString("id"));
                interPhoneApply.setApplyStatus(ApplyStatus.Pass);
                interPhoneApply.setInterPhoneAddTime(new Date());
                interPhoneApplyServiceI.save(interPhoneApply);

                //激活账号
                if (null != isBatchRenew && isBatchRenew) {
                    JSONObject active = new JSONObject();
                    active.put("talkbackUserId", interPhoneApply.getInterPhoneAccountId());
                    active.put("creditMonths", Constants.INTER_PHONE_CREDIT_MONTHS);
                    JSONObject renew = interPhoneUtil.batchRenew(active);
                    if (renew.getInteger("code") != -1) {
                        interPhoneApply.setActiveStatus(true);
                        interPhoneApply.setInterPhoneAddTime(new Date());
                        interPhoneApplyServiceI.save(interPhoneApply);
                    }
                    results.add(renew);
                }
            }
            return Result.ok(results);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取集团额度
     */
    @PostMapping("/getCredit")
    public Object getCreditByGroupId() {
        Object credit = interPhoneUtil.getCreditByGroupId(null);
        return Result.ok(credit);
    }

}
