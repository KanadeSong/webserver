package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 角色-权限关系
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/9 0029 11:29
 */

@Data
@Entity
@NoArgsConstructor
public class SysRolePermission implements Serializable {

    private static final long serialVersionUID = -923214376090039396L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId;             //  项目id

    @Column(nullable = true)
    private Long roleId;              //  角色ID

    @Column(nullable = true)
    private Long permissionId;              //  权限ID

    @Column
    private Boolean valid = true;          //  是否有效

    @Column(nullable = true)
    private Date addTime = new Date();               //  创建时间

    private Date updateTime = null;               //  修改时间

    private UseType useType = UseType.Project;   //使用类型

}
