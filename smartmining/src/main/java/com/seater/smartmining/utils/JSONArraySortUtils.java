package com.seater.smartmining.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/18 0018 21:27
 */
public class JSONArraySortUtils {

    public static String jsonArraySort(JSONArray jsonArray, String key) {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonValues.add(jsonArray.getJSONObject(i));
        }
        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            // You can change "Name" with "ID" if you want to sort by ID

            @Override
            public int compare(JSONObject a, JSONObject b) {
                // 这里是a、b需要处理的业务，需要根据你的规则进行修改。
                BigDecimal aStr = a.getBigDecimal(key);
                BigDecimal bStr = b.getBigDecimal(key);
                return aStr.compareTo(bStr);
                // if you want to change the sort order, simply use the following:
                // return -valA.compareTo(valB);
            }
        });
        for (int i = 0; i < jsonArray.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray.toString();
    }

    public static void main(String[] args) {
        String json = "[{\"machineId\":6,\"machineCode\":\"0056\",\"distance\":46.9724364982983928484827629290521144866943359375,\"latitude\":37.249816,\"longitude\":113.510950},{\"machineId\":16,\"machineCode\":\"0096\",\"distance\":18.524469297959708313783266930840909481048583984375,\"latitude\":37.249937,\"longitude\":113.510857}]";
        JSONArray jsonArray = JSONArray.parseArray(json);
        String text = jsonArraySort(jsonArray, "distance");
        System.out.println(text);
    }
}
