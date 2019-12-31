package com.seater.smartmining.entity;

import com.seater.smartmining.enums.GroupType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 对讲组
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/8/27 10:18
 */
@Data
@Entity
public class InterPhoneGroup implements Serializable {

    private static final long serialVersionUID = -1297147762906381585L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    /**
     * 组类型
     */
    @Column(length = 1000)
    private GroupType groupType = GroupType.Unknown;

    /**
     * 如果groupType类型是渣场,可以有渣场id,渣场组是固定不变的,即使没成员都要一直存在
     */
    private Long slagSiteId = 0L;

    /**
     * 是否有效
     */
    private Boolean isValid = true;

    /**
     * 项目id
     */
    private Long projectId = 0L;

    /**
     * 第三方对讲群组id
     */
    private String groupIdThird = "";

    /**
     * 我方创建的对讲组唯一标识(可以是uuid,可以是排班组的管理员id列表),只当标记用,可以不同于排班组的groupCode
     * 例如:c57a8e59-db4d-11e9-8717-123456789123 或者 组类型为Manage 时 ["2019226","2019228","2019229","2019230"]
     */
    private String groupCode = "";

    /**
     * 组名称  (默认:groupType.value-projectId-UUID前八位)
     */
    private String name = "";

    /**
     * 群组优先级,取第三方默认的
     */
    private Integer priority = 5;

    /**
     * 组描述跟第三方的字段一样  (默认:groupType.value-projectId-UUID前八位)
     */
    @Column(length = 1000)
    private String description = "";

    /**
     * 是否已同步
     */
    private Boolean isSyn = false;

    /**
     * 创建日期
     */
    private Date addTime = new Date();

    /**
     * 修改日期
     */
    private Date updateTime = new Date();

    /**
     * 描述
     *//*
    private String remark = "";*/

}
