package com.seater.smartmining.entity;

import com.seater.smartmining.enums.JoinStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 车主-司机关系  因为一车会有多个司机换班(早晚班)
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/27 22:21
 */
@Data
@Entity
public class OwnerDriverRelationTemp {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    
    @Column(nullable = true)
    private Long ownerId;                       //  车主id(用户id)
    
    @Column(unique = true)
    private Long driverId;                      //  司机id

    @Column(unique = true)
    private String driverOpenId;                  //  司机openId
    
    @Column
    private Date addTime = new Date();                       //  添加时间

    @Column
    private Date updateTime;                       //  更新时间
    
//    @Column
//    private Long effectTime = 1000 * 60L;               //  申请有效时间,单位:毫秒
    
    @Column
    private JoinStatus joinStatus = JoinStatus.Unorganized;     //  扫码加入状态
    
    @Column
    private Boolean valid = false;                      //  是否有效
}
