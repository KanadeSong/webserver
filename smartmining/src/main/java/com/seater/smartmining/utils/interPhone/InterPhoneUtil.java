package com.seater.smartmining.utils.interPhone;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.entity.InterPhone;
import com.seater.smartmining.entity.Project;
import com.seater.smartmining.entity.ProjectScheduled;
import com.seater.smartmining.service.ProjectCarServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import com.seater.smartmining.service.ProjectScheduledServiceI;
import com.seater.smartmining.service.ProjectServiceI;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description 第三方对讲接口工具类
 * 请参考文档:融洽科技-运营平台第三方接口.docx
 * 注:
 * 部门 = 我的项目    (projectId = 部门名称)
 * 群组 = 我的排班组   (排班组GroupCode = 群组名)
 * 账号(对讲机)单台 = 我的每个人手上的单台对讲机
 * ************************************************************************************
 * 调用该类所有方法时最好先执行下这个    >>>>>               initInterPhoneApiInfo()
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/15 15:13
 */

@Slf4j
@Component
@Configuration
@EnableScheduling
public class InterPhoneUtil {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    ProjectCarServiceI projectCarServiceI;
    @Autowired
    ProjectServiceI projectServiceI;
    @Autowired
    ProjectScheduledServiceI projectScheduledServiceI;

    ValueOperations<String, String> valueOps = null;
    //    String keyGroup = "entity:interPhone:";
    String keyGroupLoginInfo = "interPhone:";

//    private String getKey(Long id) {
//        return keyGroup + id.toString();
//    }

    private String getInfoKey(String path) {
        return keyGroupLoginInfo + path.split(BASE_URL, path.length())[1];
    }

    private String getApiName(String path) {
        return path.split(BASE_URL, path.length())[1];
    }

    private ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }

    //  复用字符
    private static final String QUESTION_MARK = "?";
    private static final String EQUAL_MARK = "=";
    private static final String AND_MARK = "&";
    private static final String MINUS_MARK = "-";

    //  复用的key
    private static final String UID = "uid";
    private static final String USER_OBJECT = "userObject";
    private static final String TYPE = "type";
    //  项目id
    private static final String PROJECT_ID = "projectId";
    //  项目名称
    private static final String PROJECT_NAME = "projectName";
    //  排班组的code
    private static final String GROUP_CODE = "groupCode";
    private static final String ID = "id";
    private static final String IDS = "ids";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CODE = "code";
    private static final String DATA = "data";
    private static final String TOKEN = "token";
    private static final String USER_NAME = "userName";
    private static final String USERNAME = "username";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String SORT = "sort";
    private static final String ADMINISTRATOR = "administrator";
    private static final String ADMINISTRATOR_USER = "administratorUser";
    private static final String OVER_LEVEL_CALL_TYPE = "overLevelCallType";
    private static final String OVER_GROUP_ENABLED = "overGroupEnabled";
    private static final String GPS_ENABLED = "gpsEnabled";
    private static final String PRIVILEGE = "privilege";
    private static final String AGENT = "agent";
    private static final String DEPARTMENT = "department";
    private static final String DEPARTMENT_ID = "departmentId";
    private static final String DESCRIPTION = "description";
    private static final String GROUP = "group";
    private static final String GROUP_ID = "groupId";
    private static final String PRIORITY = "priority";
    private static final String SEARCH_NAME = "searchName";
    private static final String USERS = "users";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String BUSINESS = "business";
    private static final String BUSINESS_RENTS = "businessRents";
    private static final String CREDIT_MONTHS = "creditMonths";
    private static final String DEAD_LINE = "deadline";
    private static final String NUMBER = "number";
    private static final String PERMISSION = "permission";
    private static final String CALL_IN_PERMISSIONS = "callInPermissions";
    private static final String CALL_OUT_PERMISSIONS = "callOutPermissions";
    private static final String GPS_PERMISSION = "gpsPermission";
    private static final String ENABLED = "enabled";
    private static final String INTERVAL = "interval";
    private static final String TALK_BACK_GROUP_ID = "talkBackGroupId";
    private static final String TALK_BACK_NUMBER = "talkbackNumber";
    private static final String GROUP_SEGMENT = "groupSegment";
    private static final String STRATEGY = "strategy";
    private static final String SHORT_VALUE_LENGTH = "shortValueLength";

    //  api的URL
//    private static final String BASE_URL = "http://210.51.2.211:8080/rchat/sdk/";
    private static final String BASE_URL = "http://210.51.2.211:6002/sdk/";
    private static final String URL_AUTHORIZE = BASE_URL + "authorize";
    private static final String URL_LOGIN = BASE_URL + "login";
    private static final String URL_GET_DEPMART_PAGE = BASE_URL + "getDepmartPage";
    private static final String URL_DEPARTMENT_FIND_BY_GROUP_ID = BASE_URL + "departmentFindByGroupId";
    private static final String URL_DEPARTMENT_FIND_BY_ID = BASE_URL + "departmentFindById";
    private static final String URL_ADD_DEPMART = BASE_URL + "addDepmart";
    private static final String URL_CREATE_DEPARTMENT = BASE_URL + "createDepartment";
    private static final String URL_UPDATE_DEPMART = BASE_URL + "updatedepartment";
    private static final String URL_DELETE_DEPMART = BASE_URL + "deleteDepmart";
    private static final String URL_ADD_TALK_BACK_GROUP = BASE_URL + "addTalkBackGroup";
    private static final String URL_CREATE_TALK_BACK_GROUP = BASE_URL + "createTalkBackGroup";
    private static final String URL_UPDATE_TALK_BACK_GROUP = BASE_URL + "updateTalkBackGroup";
    private static final String UPDATE_DEPARTMENT_DEFAULT_GROUP = BASE_URL + "updatedepartmentDefaultGroup";
    private static final String URL_GET_TALK_BACK_GROUP_PAGE = BASE_URL + "getTalkBackGroupPage";
    private static final String URL_TALK_BACK_GROUP_BY_GROUP_ID = BASE_URL + "talkBackGroupByGroupId";
    private static final String URL_DELETE_TALK_BACK_GROUP = BASE_URL + "deleteTalkBackGroup";
    private static final String URL_GET_TALK_BACK_USER_PAGE = BASE_URL + "getTalkBackUserPage";
    private static final String URL_ADD_TALK_BACK_USER = BASE_URL + "addTalkBackUser";
    private static final String URl_CREATE_TALK_BACK_USER = BASE_URL + "createTalkBackUser";
    private static final String URL_UPDATE_TALK_BACK_USER = BASE_URL + "updateTalkBackUser";
    private static final String URL_DELETE_TALK_BACK_USER = BASE_URL + "deleteTalkBackUser";
    private static final String URL_GET_SEGMENT_BY_GROUPID = BASE_URL + "getSegmentByGroupId";
    private static final String URL_TALK_BACK_USER_BY_TALK_BACK_GROUP_ID = BASE_URL + "talkBackUserByTalkBackGroupId";
    private static final String URL_DISPATCH_TALK_BACK_USER = BASE_URL + "dispatchTalkBackUser";
    private static final String URL_DEL_TALK_BACK_USER = BASE_URL + "delTalkBackUser";
    private static final String URL_REMOVE_ALL_TALK_BACK_GROUP = BASE_URL + "removeAllTalkBackGroup";
    private static final String URL_GET_CREDIT_BY_GROUP_ID = BASE_URL + "getCreditByGroupId";
    private static final String URL_BATCH_RENEW = BASE_URL + "batchRenew";

    //  dynamic
    //  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private String uid = Constants.INTER_PHONE_UID;

    private String token = "";

    private Integer code;

    private String username = Constants.INTER_PHONE_USERNAME;

    private String name = Constants.INTER_PHONE_ADMIN_NAME;

    private String password = Constants.INTER_PHONE_PASSWORD;

    private String sort = "createdAt,desc";

    private static final Integer SUCCESS_CODE = 0;    //  第三方对讲接口返回正确的消息码

    private static final Integer TOKEN_TIMEOUT_CODE = -2;    //  第三方对讲接口返回token过期的错误码

    /**
     * 初始化登陆信息
     *
     * @return
     */
    //  1小时自动请求一次
    @Scheduled(fixedRate = 1000 * 60L * 60)
    public synchronized void initInterPhoneApiInfo() {
        reLogin();
    }

    public void reLogin() {
        this.authorize();
        InterPhoneResult login = (InterPhoneResult) login(null);
    }

    /**
     * 定时删除空的对讲组
     */
    /*@Scheduled(fixedRate = 1000 * 60L * 60 * 24)
    public void deleteEmptyGroup(){
        initInterPhoneApiInfo();
        InterPhoneResult interPhoneResult = getInterPhoneResult();
        interPhoneResult.getData().getString("");

    }*/


    /**
     * 获取登陆时返回的InterPhoneResult信息
     *
     * @return
     */
    public InterPhoneResult getInterPhoneResult() {
        //  先从缓存拿
        InterPhoneResult interPhoneResult = JSONObject.parseObject(getValueOps().get(getInfoKey(URL_LOGIN)), InterPhoneResult.class);
        if (ObjectUtils.isEmpty(interPhoneResult)) {
            //  缓存没了就去登陆(登陆时会再存到redis)
            interPhoneResult = (InterPhoneResult) this.login(null);
        }
        //  必须执行这一步,不然保持不了会话状态
        interPhoneResult.setToken(interPhoneResult.getData().getString(TOKEN));
        interPhoneResult.setSession();
        return interPhoneResult;
    }

    /**
     * 授权获取token
     *
     * @return
     */
    public Object authorize() {
        try {
            RequestEntity<Map> requestEntity = new RequestEntity<>(null, HttpMethod.GET, new URI(URL_AUTHORIZE + QUESTION_MARK + UID + EQUAL_MARK + uid));
            log.debug("尝试请求第三方对讲机的认证接口...    >>>>>   " + URL_AUTHORIZE);
            log.debug("开始请求接口    >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据    >>>>>   {}", JSONObject.toJSONString(exchange));
            InterPhoneResult interPhoneResult = (InterPhoneResult) exchange.getBody();

            if (ObjectUtils.isEmpty(interPhoneResult)) {
                throw new NullPointerException(getApiName(URL_AUTHORIZE) + "   >>>>>   请求第三方对讲认证接口失败...返回对象信息为空...  >>>>>   " + JSONObject.toJSONString(interPhoneResult));
            }

            interPhoneResult.getData().put(USER_NAME, username);
            interPhoneResult.getData().put(PASSWORD, password);
            this.uid = (String) interPhoneResult.getData().get(UID);
            this.token = (String) interPhoneResult.getData().get(TOKEN);
            this.code = (Integer) interPhoneResult.getData().get(CODE);
            interPhoneResult.setToken(interPhoneResult.getData().getString(TOKEN));
            interPhoneResult.setHeaders(exchange.getHeaders());
//            getValueOps().set(getInfoKey(AUTHORIZE), JsonHelper.toJsonString(interPhoneResult), Constants.INTER_PHONE_REDIS_TIMEOUT, TimeUnit.MILLISECONDS);
            getValueOps().set(getInfoKey(URL_AUTHORIZE), JSONObject.toJSONString(interPhoneResult), Constants.INTER_PHONE_REDIS_TIMEOUT, TimeUnit.MILLISECONDS);
            return interPhoneResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 登录获取集团
     *
     * @param jsonObject
     * @return
     */
    public Object login(JSONObject jsonObject) {
        try {
            /*InterPhoneResult info = JSONObject.parseObject(getValueOps().get(getInfoKey(URL_AUTHORIZE)), InterPhoneResult.class);
            if (ObjectUtils.isEmpty(info) || ObjectUtils.isEmpty(info.getData())) {
                //  信息过期,重新认证
                log.debug("认证信息已过期,正在重新登陆...");
                //  重新初始化
                info = (InterPhoneResult) this.authorize();
                //  如果还是没返回,就抛异常
                if (info == null) {
                    throw new NullPointerException(getApiName(URL_LOGIN) + "    >>>>>   第三方认证接口请求失败...  >>>>>   ");
                }
            }*/

            InterPhoneResult info = (InterPhoneResult) authorize();
            if (ObjectUtils.isEmpty(info) || ObjectUtils.isEmpty(info.getData())) {
                if (info == null) {
                    throw new NullPointerException(getApiName(URL_LOGIN) + "    >>>>>   第三方认证接口请求失败...  >>>>>   ");
                }
            }

            info.getData().remove(CODE);
            RequestEntity<Map> requestEntity = new RequestEntity<>(info.getData(), HttpMethod.POST, new URI(URL_LOGIN));
            log.debug("尝试请求登陆接口...    >>>>>   " + URL_LOGIN);
            log.debug(JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据    >>>>>   {}", JSONObject.toJSONString(exchange));
            InterPhoneResult interPhoneResult = (InterPhoneResult) exchange.getBody();

            if (ObjectUtils.isEmpty(interPhoneResult) || !interPhoneResult.getCode().equals(SUCCESS_CODE)) {
                //  没数据返回或者返回消息码不为成功时的消息码
                log.debug(" 登陆失败...  >>>>>   结果信息:{}", JSONObject.toJSONString(exchange));
                return null;
            }
            /*
             *  "headers": {"Set-Cookie": ["JSESSIONID=5FD837D5E14AFDA192F4ADD770F997C5; Path=/; HttpOnly"]...
             * */
            String sessionId = exchange.getHeaders().get(SET_COOKIE).get(0).split(";")[0];
            interPhoneResult.setCookie(new HttpCookie(JSESSIONID, sessionId.split(EQUAL_MARK)[1]));
            interPhoneResult.setHeaders(exchange.getHeaders());  //  头也要保存,后续请求用到
            getValueOps().set(getInfoKey(URL_LOGIN), JSONObject.toJSONString(interPhoneResult), Constants.INTER_PHONE_REDIS_TIMEOUT, TimeUnit.MILLISECONDS);
            return interPhoneResult;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 根据集团groupId获取部门列表 如果参数中加入 projectId 则会通过projectId 进行筛选
     *
     * @param jsonObject
     * @return
     */
    public Object departmentFindByGroupId(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            Long projectId = Long.parseLong(jsonObject.getString(PROJECT_ID));
            String paramProjectId = "";
            if (projectId != null) {
                //+ AND_MARK + PROJECT_ID + EQUAL_MARK + projectId)
                paramProjectId = AND_MARK + PROJECT_ID + EQUAL_MARK + projectId;
            }
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_DEPARTMENT_FIND_BY_GROUP_ID + QUESTION_MARK + GROUP_ID + EQUAL_MARK + interPhoneResult.getData().get(GROUP_ID) + paramProjectId));
            log.debug("根据集团id获取部门列表  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            if (null != exchange.getBody().getData() && exchange.getBody().getData().size() != 0) {
                return exchange.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            //  返回空即可
            return null;
        }


    }

    /**
     * 根据项目id获取部门id
     *
     * @param projectId
     * @return
     */
    public String departmentIdFindByProjectId(Long projectId) {
        //访问该方法后续一般会有操作请求,所以先登陆一下
        initInterPhoneApiInfo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PROJECT_ID, projectId);
        InterPhoneResultArr dept = (InterPhoneResultArr) departmentFindByGroupId(jsonObject);
        return dept.getData().getJSONObject(0).getString("id");
    }

    /**
     * 根据id获取单个部门
     *
     * @param jsonObject
     * @return
     */
    public Object departmentFindById(JSONObject jsonObject) {
        try {
            String id = (String) jsonObject.get(ID);
            RequestEntity<Map> requestEntity = new RequestEntity<>(null, HttpMethod.GET, new URI(URL_DEPARTMENT_FIND_BY_ID + QUESTION_MARK + ID + EQUAL_MARK + id));
            log.debug("请求获取单个部门  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 分页查询部门
     *
     * @param jsonObject
     * @return
     */
    public Object getDepmartPage(JSONObject jsonObject) {
        try {
            InterPhoneResult data = getInterPhoneResult();

//            String groupId = data.getData().get(GROUP_ID).toString();
//            JSONObject requestJson = new JSONObject();
//            requestJson.put(SORT, sort);
//            requestJson.put(GROUP_ID, groupId);
//            requestJson.put(UID, uid);
//            requestJson.put(TOKEN, token);
            RequestEntity<Map> requestEntity = new RequestEntity<>(data.getData(), HttpMethod.GET, new URI(URL_GET_DEPMART_PAGE));
            System.out.println(JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            if (ObjectUtils.isEmpty(exchange.getBody()) || !exchange.getBody().getCode().equals(SUCCESS_CODE)) {
                throw new NullPointerException(getApiName(URL_GET_DEPMART_PAGE) + "     >>>>>   获取集团部门失败    " + JSONObject.toJSONString(exchange, SerializerFeature.WRITE_MAP_NULL_FEATURES));
            }
            //  拿到部门列表
            System.out.println(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建部门
     *
     * @param jsonObject
     * @return
     */
    public Object createDepartment(JSONObject jsonObject) {

        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            String projectId = jsonObject.getLong(PROJECT_ID).toString();
            String projectName = jsonObject.getString(PROJECT_NAME);
            JSONObject requestJson = new JSONObject();
            JSONObject administratorUser = new JSONObject();
            // 不同部门不能存在相同的账号和相同的部门管理员姓名
            administratorUser.put(NAME, Constants.SUPER_USER_ACCOUNT + "-" + projectId);
            administratorUser.put(PASSWORD, Constants.INTER_PHONE_PASSWORD);
            administratorUser.put(USERNAME, Constants.SUPER_USER_ACCOUNT + "-" + projectId);
            requestJson.put(ADMINISTRATOR_USER, administratorUser);

            requestJson.put(GROUP_ID, interPhoneResult.getData().get(GROUP_ID));
            //  用projectName命名部门名称
            requestJson.put(NAME, projectName);
            requestJson.put(PROJECT_ID, projectId);

            //  检查是否已经存在对应projectId的部门,如果有就返回
            Object dept = departmentFindByGroupId(requestJson);
            if (dept != null) {
                return dept;
            }

            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_CREATE_DEPARTMENT));
            log.debug("请求创建部门  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 修改部门
     *
     * @param jsonObject
     * @return
     */
    public Object updateDepartment(JSONObject jsonObject) {
        try {
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_UPDATE_DEPMART));
            log.debug(JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除部门
     *
     * @param jsonObject
     * @return
     */
    public Object deleteDepmart(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();
            String id = (String) jsonObject.get(ID);
            requestJson.put(ID, id);
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_DELETE_DEPMART));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 创建群组
     *
     * @param jsonObject
     * @return
     */
    public Object createTalkBackGroup(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();

            JSONObject requestJson = new JSONObject();
            //  部门id
            requestJson.put(DEPARTMENT_ID, jsonObject.get(DEPARTMENT_ID));
            requestJson.put(DESCRIPTION, jsonObject.getString(DESCRIPTION));
            //  集团id
            requestJson.put(GROUP_ID, interPhoneResult.getData().getString(GROUP_ID));
            //  群组名称换成组管理人的名称
            requestJson.put(NAME, jsonObject.getString(NAME));
            requestJson.put(GROUP_CODE, jsonObject.getString(GROUP_CODE));
            //  检查是否已存在群组 如果存在就返回该群组
//            JSONObject description = JSONObject.parseObject(jsonObject.getString(DESCRIPTION).toString());
//            Object check = getTalkBackGroupByProjectIdAndGroupCode(description.get(PROJECT_ID).toString(), description.get(GROUP_CODE).toString());
//            Object talkBackGroupList = getTalkBackGroupList(description);
//            if (!ObjectUtils.isEmpty(talkBackGroupList)) {
//                return talkBackGroupList;
//            }
            //  检查是否已存在群组 end

            //  没有这个群组就新增一个
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_CREATE_TALK_BACK_GROUP));
            log.debug("请求创建群组  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建群组失败" + e.getMessage());
            return null;
        }
    }

    /**
     * 给定项目id和排班组groupCode从数据列表筛选出单个排班组
     *
     * @param interPhoneResultArr 对讲机群组列表
     * @param projectId           给定项目id
     * @param groupCode           给定排班组groupCode
     * @return 筛选出来的单个群组
     */
    public Object getTalkBackGroupByProjectIdAndGroupCode(InterPhoneResultArr interPhoneResultArr, String projectId, String groupCode) {
        initInterPhoneApiInfo();
        String name = projectId + MINUS_MARK + groupCode.substring(0, 8);
        JSONObject description = new JSONObject();
        description.put(PROJECT_ID, projectId);
        description.put(GROUP_CODE, groupCode);
        JSONObject result = null;
        for (Object dept : interPhoneResultArr.getData()) {
            //  转类型拿到单个部门进行比对
            LinkedHashMap<String, Object> deptObj = new LinkedHashMap();
            deptObj = (LinkedHashMap<String, Object>) dept;
            if (deptObj.get(NAME).equals(name)) {
                if (deptObj.get(DESCRIPTION).toString().equals(description.toJSONString())) {
                    result = JSONObject.parseObject(JSONObject.toJSONString(deptObj));
                }
            }
        }
        return result;
    }


    /**
     * 给定项目id和排班组获取单个群组
     *
     * @param projectId
     * @param groupCode
     * @return
     */
    public Object getTalkBackGroupByProjectIdAndGroupCode(String projectId, String groupCode) {
        InterPhoneResultArr interPhoneResultArr = (InterPhoneResultArr) getTalkBackGroupList(null);

        JSONObject interPhoneResult = (JSONObject) getTalkBackGroupByProjectIdAndGroupCode(interPhoneResultArr, projectId, groupCode);
        if (!ObjectUtils.isEmpty(interPhoneResult)) {
            return interPhoneResult;
        } else {
            return null;
        }
    }

    /**
     * 按照条件获取群组列表(集团id,项目id,排班组groupCode)
     *
     * @param jsonObject
     * @return
     */
    public Object getTalkBackGroupList(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            String groupCode = (String) jsonObject.get(GROUP_CODE);
            String paramGroupCode = "";
            if (groupCode != null) {
                paramGroupCode = AND_MARK + GROUP_CODE + EQUAL_MARK + groupCode;
            }
            RequestEntity<Map> requestEntity = new RequestEntity<>(null, interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_TALK_BACK_GROUP_BY_GROUP_ID + QUESTION_MARK + GROUP_ID + EQUAL_MARK + interPhoneResult.getData().get(GROUP_ID) + paramGroupCode));
            log.debug("请求获取群组列表  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 修改群组
     * {
     * "id":"b9aa3c10-e551-4f06-a985-9fae8e6ab6b6",
     * "name":"SDK测试群组",
     * "groupCode":"12313"
     * }
     *
     * @param jsonObject
     * @return
     */
    public Object updateTalkBackGroup(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_UPDATE_TALK_BACK_GROUP));
            log.debug("请求修改群组:   >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据:     >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("修改群组失败" + e.getMessage());
            return null;
        }
    }

    /**
     * 修改默认群组
     *
     * @param jsonObject
     * @return
     */
    public Object updatedepartmentDefaultGroup(JSONObject jsonObject) {
        try {
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.GET, new URI(UPDATE_DEPARTMENT_DEFAULT_GROUP + QUESTION_MARK + DEPARTMENT_ID + EQUAL_MARK + jsonObject.getString(DEPARTMENT_ID) + AND_MARK + TALK_BACK_GROUP_ID + EQUAL_MARK + jsonObject.getString(TALK_BACK_GROUP_ID)));
            log.debug(JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }


    /**
     * 分页查询群组
     *
     * @param jsonObject
     * @return
     */
    public Object getTalkBackGroupPage(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(PAGE, jsonObject.get(PAGE));
            requestJson.put(SIZE, jsonObject.get(SIZE));
            requestJson.put(SORT, sort);

            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_GET_TALK_BACK_USER_PAGE));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 删除群组
     * http://210.51.2.211:6002/sdk/deleteTalkBackGroup?id=b9aa3c10-e551-4f06-a985-9fae8e6ab6b6
     * get请求
     *
     * @param jsonObject 群组id
     * @return
     */
    public Object deleteTalkBackGroup(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_DELETE_TALK_BACK_GROUP + QUESTION_MARK + ID + EQUAL_MARK + jsonObject.getString(ID) + AND_MARK + UID + EQUAL_MARK + interPhoneResult.getData().getString(UID) + AND_MARK + TOKEN + EQUAL_MARK + interPhoneResult.getData().getString(TOKEN)));
            log.debug("请求删除群组    >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询账号
     *
     * @param jsonObject
     * @return
     */
    public Object getTalkBackUserPage(JSONObject jsonObject) {
        try {
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_GET_TALK_BACK_GROUP_PAGE + QUESTION_MARK + PAGE + EQUAL_MARK + jsonObject.get(PAGE) + AND_MARK + SIZE + EQUAL_MARK + jsonObject.get(SIZE) + AND_MARK + SORT + EQUAL_MARK + "fullValue,asc" + AND_MARK + GROUP_ID + EQUAL_MARK + interPhoneResult.getData().getString(GROUP_ID) + AND_MARK + UID + EQUAL_MARK + interPhoneResult.getData().getString(UID) + AND_MARK + TOKEN + EQUAL_MARK + interPhoneResult.getData().getString(TOKEN)));
            log.debug("请求获取集团下所有账号   >>>>>   " + JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug("返回数据,{}", JSONObject.toJSONString(exchange));
            return exchange;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 创建账号
     */
    public Object createTalkBackUser(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            //  集团id
            String groupId = (String) interPhoneResult.getData().get(GROUP_ID);
            //  排班组
//            String groupCode = (String) jsonObject.get(GROUP_CODE);
            //  项目id
            String projectId = jsonObject.get(PROJECT_ID).toString();
            //  部门
//            InterPhoneResultArr deptByProjectId = (InterPhoneResultArr) departmentFindByGroupId(jsonObject);
//            JSONObject department = JSONObject.parseObject(JSONObject.toJSONString(deptByProjectId.getData().get(0)));
//            //  部门id
//            String departmentId = (String) department.get(ID);
//            if (departmentId == null) {
//                //  不存在相应groupCode的部门
//                return null;
//            }
            //  对讲群组
//            JSONObject talkBackGroup = (JSONObject) getTalkBackGroupByProjectIdAndGroupCode(projectId, groupCode);
            //  集团id获取号段列表
            InterPhoneResultArr segment = (InterPhoneResultArr) getSegmentByGroupId(null);

            JSONObject talkbackNumber = new JSONObject();
            JSONObject groupSegment = new JSONObject();
            //  号段拿第一个就行
            groupSegment.put(ID, JSONObject.parseObject(JSONObject.toJSONString(segment.getData().get(0))).get(ID));
            talkbackNumber.put(GROUP_SEGMENT, groupSegment);
            JSONObject requestJson = new JSONObject();
            requestJson.put(GROUP_ID, groupId);
            requestJson.put(DEPARTMENT_ID, jsonObject.getString(DEPARTMENT_ID));
            requestJson.put(TALK_BACK_NUMBER, talkbackNumber);
//            requestJson.put(TALK_BACK_GROUP_ID, talkBackGroup.get(ID));
            requestJson.put(PASSWORD, Constants.INTER_PHONE_ACCOUNT_PASSWORD);
            if (!ObjectUtils.isEmpty(jsonObject.getString(NAME))) {
                requestJson.put(NAME, jsonObject.getString(NAME));
            }
//            JSONObject userObject = new JSONObject();
//            userObject.put(ID, jsonObject.get(ID));
//            userObject.put(TYPE, jsonObject.get(TYPE));
//            userObject.put(PROJECT_ID, projectId);
            //  格式: "id=1-type=SlagCar-projectId=1"
            requestJson.put(USER_OBJECT, "id=" + jsonObject.get(ID) + "-type=" + jsonObject.get(TYPE) + "-projectId=" + projectId);
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URl_CREATE_TALK_BACK_USER + QUESTION_MARK + STRATEGY + EQUAL_MARK + 1 + AND_MARK + SHORT_VALUE_LENGTH + EQUAL_MARK + 4));
            log.debug("请求创建账号  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            InterPhoneResultArr interPhoneResultArr = new InterPhoneResultArr();
            if (null != exchange.getBody()) {
                interPhoneResultArr.setMsg(exchange.getBody().getMsg());
                interPhoneResultArr.setCode(exchange.getBody().getCode());
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(exchange.getBody().getData());
                interPhoneResultArr.setData(jsonArray);
            }
            return interPhoneResultArr;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 修改账号
     *
     * @param jsonObject
     * @return
     */
    public Object updateTalkBackUser(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            if (null == jsonObject.getString("priority")) {
                jsonObject.put("priority", 5);
            }
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_UPDATE_TALK_BACK_USER));
            log.debug(JSONObject.toJSONString(jsonObject));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            log.debug(JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 删除账号
     *
     * @param jsonObject
     * @return
     */
    public Object deleteTalkBackUser(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_DELETE_TALK_BACK_USER));
            log.debug(JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据集团id获取当前集团下的所有号段
     *
     * @param jsonObject
     * @return
     */
    public Object getSegmentByGroupId(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_GET_SEGMENT_BY_GROUPID + QUESTION_MARK + GROUP_ID + EQUAL_MARK + interPhoneResult.getData().get(GROUP_ID)));
            log.debug("根据集团id获取当前集团下的所有号段  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 根据群组id获取当前群组下的所有账号
     *
     * @param jsonObject
     * @return
     */
    public Object talkBackUserByTalkBackGroupId(JSONObject jsonObject) {
        try {
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_TALK_BACK_USER_BY_TALK_BACK_GROUP_ID + QUESTION_MARK + TALK_BACK_GROUP_ID + EQUAL_MARK + jsonObject.get(TALK_BACK_GROUP_ID)));
            log.debug("根据群组id获取当前群组下的所有账号  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 成员调度
     * 参数
     * {
     * "id":"ea6efccb-05d5-419e-b067-a8ef6ec58b251" //第三方组id
     * “ids”:[  //第三方账号id
     * {
     * "id":"82277011bcd64780a2f087cf1f2c9281"
     * },
     * {
     * "id":"f0e4c85bf549414e95072b71ef2b868d"
     * }
     * ]
     * }
     *
     * @param jsonObject 目标群组id 需要调度到目标群组的账号列表
     * @return
     */
    public InterPhoneResultArr dispatchTalkBackUser(JSONObject jsonObject) {
        try {
            if (null == jsonObject.getJSONArray("ids") || jsonObject.getJSONArray("ids").size() == 0) {
                return null;
            }
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            JSONObject requestJson = new JSONObject();
            requestJson.put(IDS, jsonObject.getJSONArray("ids"));
            //  目标群组id
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_DISPATCH_TALK_BACK_USER + QUESTION_MARK + ID + EQUAL_MARK + jsonObject.get(TALK_BACK_GROUP_ID) + AND_MARK + "action" + EQUAL_MARK + "dispatch"));
            log.debug("请求调度操作  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 在群组中移除成员
     * http://210.51.2.211:6002/sdk/delTalkBackUser?id=e5c2fc62-7696-4a9d-afb0-1f9d19c78321
     * post请求
     * {"ids":[
     * {
     * "id":"0c5213c9-aa11-4044-bc59-f7e8ff768de4"
     * },
     * {
     * "id":"8c77ef3e-2ca4-42ff-83e7-ce1cec8cdedf"
     * }
     * ]
     * }
     *
     * @param jsonObject
     * @return
     */
    public Object delTalkBackUser(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            //  目标群组id
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_DEL_TALK_BACK_USER + QUESTION_MARK + ID + EQUAL_MARK + jsonObject.get(TALK_BACK_GROUP_ID) + AND_MARK + "action" + EQUAL_MARK + "dispatch"));
            log.debug("请求移除群组内成员操作  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    public Object removeAllTalkBackGroup(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            //  目标群组id
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_REMOVE_ALL_TALK_BACK_GROUP + QUESTION_MARK + ID + EQUAL_MARK + jsonObject.get(TALK_BACK_GROUP_ID)));
            log.debug("请求清空群组成员操作  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 根据给定条件筛选出相应的账号
     * 这个字段是用来分使用账号的对象的 因为最终使用者(绑定者)有人和车  id 是其主键,type就是用来分人和车的类型
     * userObject 类型为json字符串 >>>>>  例如:"userObject":"id=20-type=DiggingMachine-projectId=1"
     *
     * @param jsonObject
     * @return
     */
    public Object findTalkBackUserByTalkBackGroupId(JSONObject jsonObject) {
        try {
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            String paramUserObject = "";
            if (jsonObject.getString(USER_OBJECT) != null) {
                paramUserObject = AND_MARK + USER_OBJECT + EQUAL_MARK + jsonObject.getString(USER_OBJECT).toString();
            }
            //  目标群组id
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_TALK_BACK_USER_BY_TALK_BACK_GROUP_ID + QUESTION_MARK + TALK_BACK_GROUP_ID + EQUAL_MARK + jsonObject.getString(TALK_BACK_GROUP_ID) + paramUserObject));
            log.debug("根据群组id获取群组下的所有账号  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResultArr> exchange = restTemplate.exchange(requestEntity, InterPhoneResultArr.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 根据集团id获取额度
     *
     * @param jsonObject
     * @return
     */
    public Object getCreditByGroupId(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            //  目标群组id
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.GET, new URI(URL_GET_CREDIT_BY_GROUP_ID + QUESTION_MARK + GROUP_ID + EQUAL_MARK + interPhoneResult.getData().getString(GROUP_ID)));
            log.debug("根据集团id获取额度  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return null;
        }
    }

    /**
     * 激活账号
     * 固定的 businessId : 5203bc84-7bc6-4cb7-aac5-6c0916b365b2
     * {
     * "talkbackUserId":"d1ce70a62fd047098fc768892d2fc2ae",
     * "businessId":"5203bc84-7bc6-4cb7-aac5-6c0916b365b2",
     * "creditMonths":3
     * }
     *
     * @param jsonObject 目标账号列表
     * @return
     */
    public JSONObject batchRenew(JSONObject jsonObject) {
        try {
            initInterPhoneApiInfo();
            jsonObject.put("businessId", "5203bc84-7bc6-4cb7-aac5-6c0916b365b2");
            if (null == jsonObject.getInteger("creditMonths")) {
                jsonObject.put("creditMonths", Constants.INTER_PHONE_CREDIT_MONTHS);
            }
            if (StringUtils.isEmpty(jsonObject.getString("talkbackUserId"))) {
                log.error("没有账号id,激活失败");
                return null;
            }
            InterPhoneResult interPhoneResult = getInterPhoneResult();
            RequestEntity<Map> requestEntity = new RequestEntity<>(jsonObject, interPhoneResult.getHeaders(), HttpMethod.POST, new URI(URL_BATCH_RENEW));
            log.debug("激活账号  >>>>>   {}", JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            log.debug("返回数据  >>>>>   {}", JSONObject.toJSONString(exchange));
            return exchange.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
