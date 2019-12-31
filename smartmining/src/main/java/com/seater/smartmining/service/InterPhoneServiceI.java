package com.seater.smartmining.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @Description 对讲机群组切换,参数请参考文档 >>>>> 融洽科技-运营平台第三方接口
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/15 9:32
 */
public interface InterPhoneServiceI {

    public Object test();

    /**
     * 认证获取token
     * @param uid
     * @return
     */
    public Object authorize(String uid);

    /**
     * 登陆
     * @param jsonObject
     * @return
     */
    public Object login(JSONObject jsonObject);

    /**
     * 分页查询部门
     * @param jsonObject
     * @return
     */
    public Object getDepmartPage(JSONObject jsonObject);

    /**
     * 查询所有部门
     *
     * @param jsonObject
     * @return
     */
    public Object getDepmartList(JSONObject jsonObject);

    /**
     * 新增部门
     * @param jsonObject
     * @return
     */
    public Object addDepmart(JSONObject jsonObject);

    /**
     * 修改部门
     * @param jsonObject
     * @return
     */
    public Object updateDepmart(JSONObject jsonObject);

    /**
     * 删除部门
     * @param jsonObject
     * @return
     */
    public Object deleteDepmart(JSONObject jsonObject);

    /**
     * 新增群组
     * @param jsonObject
     * @return
     */
    public Object addTalkBackGroup(JSONObject jsonObject);

    /**
     * 修改群组
     * @param jsonObject
     * @return
     */
    public Object updateTalkBackGroup(JSONObject jsonObject);

    /**
     * 分页查询群组
     * @param jsonObject
     * @return
     */
    public Object getTalkBackGroupPage(JSONObject jsonObject);

    /**
     * 删除群组
     * @param jsonObject
     * @return
     */
    public Object deleteTalkBackGroup(JSONObject jsonObject);

    /**
     * 分页查询账号
     * @param jsonObject
     * @return
     */
    public Object getTalkBackUserPage(JSONObject jsonObject);

    /**
     * 新增账号
     * @param jsonObject
     * @return
     */
    public Object addTalkBackUser(JSONObject jsonObject);

    /**
     * 修改账号
     * @param jsonObject
     * @return
     */
    public Object updateTalkBackUser(JSONObject jsonObject);

    /**
     * 删除账号
     * @param jsonObject
     * @return
     */
    public Object deleteTalkBackUser(JSONObject jsonObject);

}
