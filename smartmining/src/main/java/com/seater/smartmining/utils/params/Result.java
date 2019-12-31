package com.seater.smartmining.utils.params;

import com.seater.smartmining.constant.SmartminingConstant;

import java.util.HashMap;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/26 0026 14:15
 */
public class Result extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public Result(){
        put("status",true);
    }

    public static Result error(){
        Result result = new Result();
        result.put("status", false);
        result.put("msg", SmartminingConstant.ERRORMESSAGE);
        return result;
    }

    public static Result error(String msg){
        Result result = new Result();
        result.put("status", false);
        result.put("msg", msg);
        return result;
    }

    public static Result ok(){
        Result result = new Result();
        return result;
    }

    public static Result ok(Object data){
        Result result = new Result();
        result.put("data",data);
        return result;
    }

    public static Result ok(Object data, Long totalCount){
        Result result = new Result();
        result.put("data",data);
        result.put("totalCount", totalCount);
        return result;
    }

    public static Result ok(Object data, Integer totalCount) {
        Result result = new Result();
        result.put("data", data);
        result.put("totalCount", totalCount);
        return result;
    }

    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
