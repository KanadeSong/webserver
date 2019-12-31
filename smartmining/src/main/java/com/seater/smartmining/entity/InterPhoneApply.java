package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ApplyStatus;
import com.seater.smartmining.utils.interPhone.UserObjectType;
import com.seater.user.entity.Sex;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 对讲机申请审核
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/5/21 10:09
 */
@Entity
@Data
public class InterPhoneApply implements Serializable {

    private static final long serialVersionUID = -3038607762503188452L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;    //  表主键

    private Long projectId = 0L;    //  使用者所在项目

    private String projectName = "";    //  使用者所在项目名称

    private String code = "";    //  如果是车,会有编号

    private Long ownerId = 0L;      //  如果申请类型是车,会有车主id

    private String ownerName = "";      //  如果申请类型是车,会有车主名称

    private Long userObjectId = 0L;  //  使用者的主键  ,一般是车的id 或者人的id

    private UserObjectType userObjectType = UserObjectType.SlagCar;  //  使用者类型 默认渣车

    private String name = "";    //  如果申请类型是人,会有名字

    private Sex sex = Sex.Unknow;   //  如果是人 可以加上性别

    private String mobile = "";  // 可以给个联系电话(车主的,或者是申请人的)

    private Date addTime = new Date();  //  创建时间

    private Boolean status = true;  //  是否有效

    private ApplyStatus applyStatus = ApplyStatus.Edit;   //  审核状态

    private Date updateTime = new Date();

    private String accountName = "";        //  相应对讲机账号中文名,如果使用者是车,这个值就是车的编号,如果使用者是人这个就是人名

    @Column
    private String interPhoneAccount = "";  //  相应对讲机账号

    private String password = "";       //账号对应密码

    @Column
    private String interPhoneAccountId = "";  //  相应对讲机账号id

    private Date interPhoneAddTime = null;  //  对讲机创建时间

    private Boolean activeStatus = false;   //  账号激活状态
}
