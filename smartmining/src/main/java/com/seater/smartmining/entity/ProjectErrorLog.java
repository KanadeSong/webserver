package com.seater.smartmining.entity;

import com.seater.smartmining.enums.LoginEnums;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:项目异常日志
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/22 0022 17:17
 */
@Entity
@Table
@Data
public class ProjectErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long userId = 0L;       //用户ID

    @Column
    private String userName = "";       //用户名称

    @Lob
    @Column(columnDefinition="text")
    private String message = "";            //异常信息

    @Column
    private Date errorDate = null;          //异常日期

    @Column
    private LoginEnums loginType = LoginEnums.Unknow;       //登录类型
}
