package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户-角色关系表
 */
@Data
@Entity
@NoArgsConstructor
public class SysUserRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = true)
    private Long userId;                    //  用户id

    @Column(nullable = true)
    public Long roleId;                    //  角色id

    @Column(nullable = true)
    private Date addTime = new Date();      //  创建时间

    @Column(nullable = true)
    private Boolean valid = true;                  //  是否可用
}
