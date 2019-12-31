package com.seater.smartmining.entity;

import com.seater.smartmining.enums.JoinStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 车主-司机关系
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/27 22:21
 */
@Data
@Entity
public class OwnerDriverRelation {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    
    private Long ownerId;                       //  车主id
    
    @Column
    private Long driverId;                      //  司机id

    @Column
    private String driverOpenId;                  //  司机openId
    
    @Column
    private Date addTime = new Date();                       //  添加时间

    @Column
    private Date invalidTime;                       //  解除时间
    
    @Column
    private JoinStatus joinStatus = JoinStatus.Unorganized;     //  扫码加入状态
    
    @Column
    private Boolean valid = false;                      //  是否有效
}
