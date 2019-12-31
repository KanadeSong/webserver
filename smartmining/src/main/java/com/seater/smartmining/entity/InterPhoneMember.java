package com.seater.smartmining.entity;

import com.seater.smartmining.utils.interPhone.UserObjectType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 对讲组的组员(相当于对讲组的明细表)
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 10:18
 */
@Data
@Entity
public class InterPhoneMember implements Serializable {

    private static final long serialVersionUID = -5548918819036504630L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    /**
     * 项目id
     */
    private Long projectId = 0L;

    /**
     * 使用者类型
     */
    private UserObjectType userObjectType = UserObjectType.Person;

    /**
     * 使用者的主键  ,一般是车的id 或者人的id
     */
    private Long userObjectId = 0L;

    /**
     * 第三方对讲群组id,不需要这个了
     */
//    private String groupIdThird = "";

    /**
     * 人名或者车编号(如果)
     */
    private String userObjectName = "";

    /**
     * 组id(InterPhoneGroup id)
     */
    private Long interPhoneGroupId = 0L;

    /**
     * 组员标识id(格式:userObjectId-userObjectType-projectId)
     *//*
    private String memberId = "";*/

    /**
     * 相应对讲机账号
     */
    private String interPhoneAccount = "";

    /**
     * 相应对讲机账号id
     */
    private String interPhoneAccountId = "";

    /**
     * 发言优先级,取第三方默认的
     */
    private Integer priority = 5;

    /**
     * 排班组id
     */
    private Long scheduleId = 0L;

    /**
     * 是否为固定成员(删除成员时不会删除固定成员)
     */
    private Boolean isFixed = false;

    /**
     * 描述
     *//*
    private String remark = "";*/

}
