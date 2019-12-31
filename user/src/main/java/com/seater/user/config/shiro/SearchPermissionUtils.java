package com.seater.user.config.shiro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.user.entity.SysRole;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Description 查询权限的工具
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/28 14:42
 */
@Slf4j
public class SearchPermissionUtils {

    /**
     * 根据项目id从权限对象中查询出相应的项目鉴权数组
     *
     * @param projectId  项目id
     * @param jsonObject 权限对象
     * @return
     */
    public static Object searchPermissionByProject(Long projectId, JSONObject jsonObject) {
        //  取出项目列表进行查找
        JSONArray projectList = JSONArray.parseArray(JSONObject.toJSONString(jsonObject.get(Constants.PROJECT_LIST)));
        for (Object project : projectList) {
            //  项目对象和其他信息
            JSONObject projectObj = (JSONObject) JSONObject.parseObject(JSONObject.toJSONString(project)).get(Constants.PROJECT);
            //  如果和给定项目id相同,返回权限数组
            if (projectId.toString().equals(projectObj.get("id").toString())) {
                //  该项目权限数组
                Object permissionArrayInProject = JSONObject.parseObject(JSONObject.toJSONString(project)).get(Constants.PERMISSION_ARRAY_IN_PROJECT);
                //  转回array
                JSONArray permissionArr = JSONArray.parseArray(JSONObject.toJSONString(permissionArrayInProject));
                return permissionArr.toJavaList(String.class);
            }
        }
        //  避免空指针 返回个列表对象
        return new ArrayList<String>();
    }
}
