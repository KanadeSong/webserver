package com.seater.user.entity;


import com.seater.user.enums.UserWorkEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = 0L;

    @Column(nullable = false, unique = true)
    public String account = "";

    @Column(nullable = false)
    private String password = "";

    @Column(nullable = true)
    private  String name = "";

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Sex sex = Sex.Unknow;

    @Column(nullable = false)
    private Boolean valid = true;      //  是否可用

    @Column(nullable = true)
    private String mobile = "";

    @Column
    private String avatar;              //头像

    @Column
    private String address;              //地址

    @Column(nullable = true)
    private Date addTime = new Date();

    @Column(nullable = true)
    private String idNo = "";           //  人员编号

    @Column(nullable = true)
    private String openId;              //  微信openid

    @Column(nullable = true)
    private VipLevel vipLevel = VipLevel.Level0;    //  会员等级

    @Column
    private BigDecimal balance = BigDecimal.ZERO;

    @Column
    private String iccid = "";

    @Column(scale = 6)
    private BigDecimal longitude = BigDecimal.ZERO;     //经度

    @Column(scale = 6)
    private BigDecimal latitude = BigDecimal.ZERO;      //纬度

    @Column
    @Enumerated(EnumType.ORDINAL)
    private UserWorkEnum userWorkStatus = UserWorkEnum.Unknow;        //工作状态

}

