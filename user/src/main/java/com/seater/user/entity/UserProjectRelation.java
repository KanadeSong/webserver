package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 用户申请加入项目缓存表
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/8 10:03
 */
@Data
@Entity
@NoArgsConstructor
public class UserProjectRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userId;           //  用户id 邀请人id(出示二维码的人)

    @Column
    private String joinerName;           //  待加入人名称

    @Column
    private String mobile;           //  用户手机号

    @Column
    private Long joinerId;          //  待加入人员id

    @Column
    private String joinerOpenId;    // 待加如人员openId

    @Column
    private Long projectId;         //  项目id

    @Column
    private Long roleId;         //  项目内角色id

    @Column
    private JoinType joinType = JoinType.Unknown;              // 加入类型

    @Column
    private Date addTime = new Date();      //  添加时间

    @Column
    private Date updateTime;                       //  更新时间

    @Column
    private Boolean valid = true;          //  是否有效

    @Column
    private JoinStatus joinStatus = JoinStatus.Unorganized;         //  扫码加入状态

}
