package com.seater.smartmining.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seater.helpers.JsonHelper;
import com.seater.smartmining.entity.InterPhone;
import com.seater.smartmining.service.InterPhoneServiceI;
import com.seater.smartmining.utils.interPhone.InterPhoneResult;
import com.seater.smartmining.utils.interPhone.InterPhoneUtil;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.dao.GlobalSet;
import com.seater.user.util.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/15 9:37
 */
@Slf4j
@Service
public class InterPhoneServiceImpl implements InterPhoneServiceI {

    @Autowired
    private InterPhoneUtil interPhoneUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:interPhone:";
    String keyGroupLoginInfo = "entity:interPhoneLoginInfo:";

    String getKey(Long id) {
        return keyGroup + id.toString();
    }
    String getInfoKey(String path) {
        return keyGroupLoginInfo + path.split(BASE_URL,path.length())[1] ;
    }
    ValueOperations<String, String> getValueOps() {
        if (valueOps == null) valueOps = stringRedisTemplate.opsForValue();
        return valueOps;
    }

    //  key
    private static final String UID = "uid";

    private static final String ID = "id";

    private static final String CODE = "code";

    private static final String DATA = "data";

    private static final String TOKEN = "token";

    private static final String USER_NAME = "userName";

    private static final String NAME = "name";

    private static final String PASSWORD = "password";

    private static final String SORT = "sort";

    private static final String ADMINISTRATOR = "administrator";

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

    private static final String PAGE = "Page";

    private static final String SIZE = "Size";

    private static final String BUSINESS = "business";

    private static final String BUSINESS_RENTS = "businessRents";

    private static final String CREDIT_MONTHS = "creditMonths";

    private static final String DEAD_LINE = "deadline";

    private static final String NUMBER = "number";

    private static final String GROUP_SEGMENT = "groupSegment";

    private static final String PERMISSION = "permission";

    private static final String CALL_IN_PERMISSIONS = "callInPermissions";

    private static final String CALL_OUT_PERMISSIONS = "callOutPermissions";

    private static final String GPS_PERMISSION = "gpsPermission";

    private static final String ENABLED = "enabled";

    private static final String INTERVAL = "interval";

    //  URL
//    private static final String BASE_URL = "http://210.51.2.211:8080/rchat/sdk/";
    private static final String BASE_URL = "http://210.51.2.211:6002/sdk/";

    private static final String URL_AUTHORIZE = BASE_URL + "authorize";

    private static final String URL_LOGIN = BASE_URL + "login";

    private static final String URL_GET_DEPMART_PAGE = BASE_URL + "getDepmartPage";

    private static final String URL_ADD_DEPMART = BASE_URL + "addDepmart";

    private static final String URL_UPDATE_DEPMART = BASE_URL + "updateDepmart";

    private static final String URL_DELETE_DEPMART = BASE_URL + "deleteDepmart";

    private static final String URL_ADD_TALKBACK_GROUP = BASE_URL + "addTalkBackGroup";

    private static final String URL_UPDATE_TALKBACK_GROUP = BASE_URL + "updateTalkBackGroup";

    private static final String URL_GET_TALKBACK_GROUP_PAGE = BASE_URL + "getTalkBackGroupPage";

    private static final String URL_DELETE_TALKBACK_GROUP = BASE_URL + "deleteTalkBackGroup";

    private static final String URL_GET_TALKBACK_USER_PAGE = BASE_URL + "getTalkBackUserPage";

    private static final String URL_ADD_TALKBACK_USER = BASE_URL + "addTalkBackUser";

    private static final String URL_UPDATE_TALKBACK_USER = BASE_URL + "updateTalkBackUser";

    private static final String URL_DELETE_TALKBACK_USER = BASE_URL + "deleteTalkBackUser";

    //  dynamic
    //  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private String uid = Constants.INTER_PHONE_UID;

    private String token = "";

    private Integer code;

    private String username = Constants.INTER_PHONE_USERNAME;

    private String name = "super";

    private String password = Constants.INTER_PHONE_PASSWORD;

    private String sort = "createdAt,desc";
    //  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    @Override
    public Object test() {
        try {
            String path = "http://192.168.1.155:8082/api/interPhone/interPhoneTest";
            JSONObject requestJson = new JSONObject();
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(USER_NAME, username);
            requestJson.put(PASSWORD, password);
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(path));
            System.out.println(JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return false;
        }
    }

    @Override
    public Object authorize(String uid) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put(UID, uid);
            RequestEntity<Map> requestEntity = new RequestEntity<>(null, HttpMethod.GET, new URI(URL_AUTHORIZE + "?" + UID + "=" + uid));
            System.out.println(JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            InterPhoneResult data = (InterPhoneResult) exchange.getBody();

            if (StringUtils.isEmpty(data)) {
                throw new NullPointerException("authorize...请求第三方对讲接口失败...");
            }
            data.getData().put(USER_NAME, username);
            data.getData().put(PASSWORD, password);
            this.uid = (String) data.getData().get(UID);
            this.token = (String) data.getData().get(TOKEN);
            this.code = (Integer) data.getData().get(CODE);
            getValueOps().set(getInfoKey(URL_AUTHORIZE), JsonHelper.toJsonString(data), Constants.INTER_PHONE_REDIS_TIMEOUT, TimeUnit.MILLISECONDS);

            System.out.println(JSONObject.toJSONString(exchange));
            System.out.println(JSONObject.toJSONString(data));
            log.info("当前时间戳:    >>>>>   " + System.currentTimeMillis());
            log.info("登陆成功  >>>>>   uid:{}", this.uid);
            log.info("登陆成功  >>>>>   token:{}", this.token);
            log.info("登陆成功  >>>>>   code:{}", this.code);
            return Result.ok(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Object login(JSONObject jsonObject) {
        try {
            InterPhoneResult info = JSONObject.parseObject(getValueOps().get(getInfoKey(URL_AUTHORIZE)), InterPhoneResult.class);
            if (StringUtils.isEmpty(info)) {
                //  信息过期,重新认证
                String OUT_OFF_TIME = "认证信息已过期,正在重新登陆...";
                log.info(OUT_OFF_TIME);
                InterPhoneUtil interPhoneUtil = new InterPhoneUtil();
                interPhoneUtil.authorize();
                throw new NullPointerException(OUT_OFF_TIME);
            }
            System.out.println(JSONObject.parseObject(info.getData().toJSONString()));
            info.getData().remove(CODE);
            RequestEntity<Map> requestEntity = new RequestEntity<>(info.getData(), HttpMethod.POST, new URI(URL_LOGIN));
            System.out.println(JSONObject.toJSONString(requestEntity));
            ResponseEntity<InterPhoneResult> exchange = restTemplate.exchange(requestEntity, InterPhoneResult.class);
            System.out.println(JSONObject.toJSONString(exchange));
            InterPhoneResult data = (InterPhoneResult) exchange.getBody();
            getValueOps().set(getInfoKey(URL_LOGIN), JsonHelper.toJsonString(data), Constants.INTER_PHONE_REDIS_TIMEOUT, TimeUnit.MILLISECONDS);
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前时间戳:   >>>>>   " + System.currentTimeMillis() + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Object getDepmartPage(JSONObject jsonObject) {
        try {
            InterPhoneResult data = JSONObject.parseObject(getValueOps().get(getInfoKey(URL_LOGIN)), InterPhoneResult.class);
            if (StringUtils.isEmpty(data)){
                //  重新登陆
                InterPhoneUtil interPhoneUtil = new InterPhoneUtil();
                interPhoneUtil.authorize();
                //  再拿一次
                data = JSONObject.parseObject(getValueOps().get(getInfoKey(URL_LOGIN)), InterPhoneResult.class);
            }
//            String groupId = data.getData().get(GROUP_ID).toString();
//            JSONObject requestJson = new JSONObject();
//            requestJson.put(SORT, sort);
//            requestJson.put(GROUP_ID, groupId);
//            requestJson.put(UID, uid);
//            requestJson.put(TOKEN, token);
            RequestEntity<Map> requestEntity = new RequestEntity<>(data.getData(), HttpMethod.GET, new URI(URL_GET_DEPMART_PAGE));
            System.out.println(JSONObject.toJSONString(requestEntity));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            //  拿到部门列表
            System.out.println(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Object getDepmartList(JSONObject jsonObject) {
        InterPhoneResult interPhoneResult = interPhoneUtil.getInterPhoneResult();
        return interPhoneUtil.departmentFindByGroupId(interPhoneResult.getData());
    }

    @Override
    public Object addDepmart(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();
            JSONObject administrator = new JSONObject();

            administrator.put(NAME, name);
            administrator.put(USER_NAME, username);
            administrator.put(PASSWORD, password);

            JSONObject group = new JSONObject();
            String groupId = (String) jsonObject.get(GROUP_ID);
            group.put(ID, groupId);


            JSONObject privilege = new JSONObject();
            privilege.put(OVER_LEVEL_CALL_TYPE, 0);
            privilege.put(OVER_GROUP_ENABLED, 0);
            privilege.put(GPS_ENABLED, 0);


            requestJson.put(ADMINISTRATOR, administrator);
            requestJson.put(GROUP, group);
            requestJson.put(NAME, name);
            requestJson.put(PRIVILEGE, privilege);
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            System.out.println(JSONObject.toJSONString(requestJson));

            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_ADD_DEPMART));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Object updateDepmart(JSONObject jsonObject) {

        try {
            JSONObject requestJson = new JSONObject();
            JSONObject administrator = new JSONObject();

            administrator.put(PASSWORD, password);
            administrator.put(NAME, name);
            administrator.put(USER_NAME, username);
            administrator.put(PASSWORD, password);

            JSONObject group = new JSONObject();
            String groupId = (String) jsonObject.get(GROUP_ID);
            group.put(ID, groupId);


            JSONObject privilege = new JSONObject();
            privilege.put(OVER_LEVEL_CALL_TYPE, 0);
            privilege.put(OVER_GROUP_ENABLED, 0);
            privilege.put(GPS_ENABLED, 0);


            String projectId = (String) jsonObject.get("projectId");
            requestJson.put(ID, projectId);
            requestJson.put(ADMINISTRATOR, administrator);
            requestJson.put(GROUP, group);
            requestJson.put(NAME, name);
            requestJson.put(PRIVILEGE, privilege);
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_UPDATE_DEPMART));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return Result.ok(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @Override
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

    @Override
    public Object addTalkBackGroup(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject agent = new JSONObject();
            agent.put(ID, "待定");

            JSONObject department = new JSONObject();
            department.put(ID, "待定");

            JSONObject group = new JSONObject();
            group.put(ID, "待定");

            JSONObject users = new JSONObject();
            users.put(ID, "待定");

            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(DEPARTMENT, department);
            requestJson.put(DEPARTMENT_ID, "bf4898b0-14c4-4c88-8b6f-b93a76d99199");
            requestJson.put(DESCRIPTION, "12324");
            requestJson.put(GROUP, group);
            requestJson.put(NAME, "待定");
            requestJson.put(PRIORITY, "待定");
            requestJson.put(SEARCH_NAME, "待定");
            requestJson.put(USERS, users);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(URL_ADD_TALKBACK_GROUP));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object updateTalkBackGroup(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            //  TODO
            JSONArray businessRents = new JSONArray();
            JSONObject business = new JSONObject();
            business.put(ID, "待定");
            businessRents.add(business);

            JSONObject department = new JSONObject();
            department.put(ID, "待定");

            JSONObject group = new JSONObject();
            group.put(ID, "待定");

            JSONObject number = new JSONObject();
            JSONObject groupSegment = new JSONObject();
            groupSegment.put(ID, "待定");
            number.put(ID, "待定");
            number.put(GROUP_SEGMENT, groupSegment);


            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(BUSINESS_RENTS, businessRents);
            requestJson.put(DEPARTMENT, department);
            requestJson.put(GROUP, group);
            requestJson.put(NUMBER, number);
            requestJson.put(PASSWORD, password);
            requestJson.put(PERMISSION, "待定");
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_UPDATE_TALKBACK_GROUP));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object getTalkBackGroupPage(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(PAGE, jsonObject.get(PAGE));
            requestJson.put(SIZE, jsonObject.get(SIZE));
            requestJson.put(SORT, sort);

            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_GET_TALKBACK_USER_PAGE));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object deleteTalkBackGroup(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(ID, jsonObject.get(ID));
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.GET, new URI(URL_DELETE_TALKBACK_GROUP));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object getTalkBackUserPage(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();


            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(PAGE, jsonObject.get(PAGE));
            requestJson.put(SIZE, jsonObject.get(SIZE));
            requestJson.put(SORT, sort);
            requestJson.put(GROUP_ID, jsonObject.get(GROUP_ID));
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(URL_GET_TALKBACK_GROUP_PAGE));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object addTalkBackUser(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject agent = new JSONObject();
            agent.put(ID, "待定");

            JSONObject department = new JSONObject();
            department.put(ID, "待定");

            JSONObject group = new JSONObject();
            group.put(ID, "待定");

            JSONObject users = new JSONObject();
            users.put(ID, "待定");

            System.out.println();
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(DEPARTMENT, department);
            requestJson.put(DEPARTMENT_ID, "bf4898b0-14c4-4c88-8b6f-b93a76d99199");
            requestJson.put(DESCRIPTION, "12324");
            requestJson.put(GROUP, group);
            requestJson.put(NAME, "待定");
            requestJson.put(PRIORITY, "待定");
            requestJson.put(SEARCH_NAME, "待定");
            requestJson.put(USERS, users);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(URL_ADD_TALKBACK_USER));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Object updateTalkBackUser(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();

            JSONObject agent = new JSONObject();
            agent.put(ID, "待定");

            JSONObject department = new JSONObject();
            department.put(ID, "待定");

            JSONObject group = new JSONObject();
            group.put(ID, "待定");

            JSONObject users = new JSONObject();
            users.put(ID, "待定");

            requestJson.put(ID, jsonObject.get(ID));
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            requestJson.put(DEPARTMENT, department);
            requestJson.put(DEPARTMENT_ID, "bf4898b0-14c4-4c88-8b6f-b93a76d99199");
            requestJson.put(DESCRIPTION, "12324");
            requestJson.put(GROUP, group);
            requestJson.put(NAME, "待定");
            requestJson.put(PRIORITY, "待定");
            requestJson.put(SEARCH_NAME, "待定");
            requestJson.put(USERS, users);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(URL_UPDATE_TALKBACK_USER));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object deleteTalkBackUser(JSONObject jsonObject) {
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put(ID, (String) jsonObject.get(ID));
            requestJson.put(UID, uid);
            requestJson.put(TOKEN, token);
            System.out.println(JSONObject.toJSONString(requestJson));
            RequestEntity<Map> requestEntity = new RequestEntity<>(requestJson, HttpMethod.POST, new URI(URL_DELETE_TALKBACK_USER));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(requestEntity, JSONObject.class);
            System.out.println(JSONObject.toJSONString(exchange));
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
