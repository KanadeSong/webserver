package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 用户-项目-角色关系表
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/20 18:06
 */
@Data
@Entity
@NoArgsConstructor
public class SysUserProjectRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    public Long userId;                        //  用户id

    @Column
    public Long projectId;                     //  项目id

    @Column
    public Long roleId;                        //  角色id

    @Column
    @Enumerated(EnumType.ORDINAL)
    private DistributeStatus distributeStatus = DistributeStatus.Undistribute;  //  项目分配状态

    @Column
    private Date addTime = new Date();          //  添加时间

    @Column
    private Date invalidTime;                   //  解除时间

    @Column
    private Boolean valid = true;                      //  是否有效

    @Column
    private String interPhoneAccount = "";  //  相应对讲机账号

    @Column
    private String interPhoneAccountId = "";  //  相应对讲机账号id

    private Boolean isRoot = false;     //根?

}
