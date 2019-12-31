package com.seater.user.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.user.util.constants.Constants;
import org.apache.shiro.SecurityUtils;

import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/20 0020 15:07
 */
public class PermissionUtils {

    public static JSONArray getProjectPermission(Long projectId) {
        JSONObject jsonObject = (JSONObject) SecurityUtils.getSubject().getSession().getAttribute(Constants.SESSION_USER_PERMISSION);
        List<JSONObject> jsonObjectList = jsonObject.getObject(Constants.PROJECT_LIST, List.class);
        JSONObject jsonObjectTwo = new JSONObject();
        for (JSONObject object : jsonObjectList) {
            JSONObject jsonObjectThree = object.getJSONObject("project");
            Long id = jsonObjectThree.getLong("id");
            if (projectId == id) {
                jsonObjectTwo = object;
                break;
            }
        }

        JSONArray jsonArray = jsonObjectTwo.getJSONArray(Constants.PERMISSION_ARRAY_IN_PROJECT);
/*
        System.out.println("获取到的字符串：" + JSON.toJSONString(jsonObject));
*/
        return jsonArray;
    }
}
