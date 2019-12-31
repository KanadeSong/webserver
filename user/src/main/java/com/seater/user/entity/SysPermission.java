package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 权限
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/9 0029 11:29
 */

@Data
@Entity
@NoArgsConstructor
public class SysPermission implements Serializable {


    private static final long serialVersionUID = -6580147650489331435L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;               //  权限ID

    @Column(nullable = true)
    private String menuCode;            //  归属菜单,前端判断并展示菜单使用,

    @Column(nullable = true)
    private String menuName;            //  菜单的中文释义

    @Column(nullable = true)
    private String menuUrl;             //  路由路径

    @Column(nullable = true)
    private String permissionCode;      //  权限的代码/通配符,对应代码中@RequiresPermissions 的value

    @Column(nullable = true)
    private String permissionName;      //  本权限的中文释义

    @Column(nullable = true)
    private Long parentId;              //  父ID

    @Column(nullable = true)
    private String parentIdTree;        //  菜单结构树   1/1-1/1-1-1/...

    @Column(nullable = true)
    private String type;                //  授权类型 ：menu菜单；permission权限

    @Column(nullable = true)
    private Long sort;                  //  排序

    @Column(nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private RequiredPermission requiredPermission=RequiredPermission.Unknow;     //  是否本菜单必选权限 0.未知 1.必选 2.非必选 ,通常是"列表"权限是必选

    @Column(nullable = true)
    private Date addTime = new Date();               //  创建时间

    private Boolean valid = true;                  //  是否有效

    @Column(nullable = true)
    private Long projectId;              //  项目id

    private Date updateTime = null;               //  修改时间

    private UseType useType = UseType.Project;  //使用类型
}
