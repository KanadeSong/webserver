package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Description 对讲机
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/15 9:54
 */
@Entity
@Data
public class InterPhone {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //  自增主键
    
    private String uid;     //  设备uid
    
    private Boolean valid;  //  是否有效
    
    private Date addTime;   //  创建时间
    
    private String interPhoneAccount;   //  对讲机相应账号
}
