package com.seater.user.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 角色
 * @Author xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/9 0029 11:29
 */

@Data
@Entity
@NoArgsConstructor
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1102549994465821059L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = 0L;               //  角色ID

    @Column
    private Long projectId = 0L;               //  所属项目id

    @Column
    private Long sort;                  //  排序

    @Column(nullable = true)
    public String roleName = "";            //  角色名

    @Column(nullable = true)
    private Date addTime = new Date();               //  创建时间

    @Column(nullable = false)
    private Boolean valid = true;      //  是否有效

    private Date updateTime = new Date();               //  修改时间

    private UseType useType = UseType.Project;   //使用类型

    private Long parentId;      //父id   平台端需要用 父id = null 屏蔽子角色 //深度是2足够了(两层)

    private String defaultName;      //默认名称(平台端预定义名称,默认取父id对应角色名称)

    private Boolean isDefault = false; //是否为默认角色

    private String remark = "";     //备注
}
