package com.seater.smartmining.entity;

import com.seater.smartmining.enums.ReportEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 记录每天是否可发布报表
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/23 15:32
 */
@Data
@Entity
public class ReportPublish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;
    // 项目id
    private Long projectId;
    // 微信小程序是否可看报表
    private Boolean publishWx = false;
    // 报表日期
    private Date reportDate;
    // 更新日期
    private Date updateDate = null;
    // 添加日期
    private Date addDate = new Date();
    // 更新人id
    private Long userId = null;
    // 更新人名称
    private String userName = null;
    // 报表类型
    @Enumerated(EnumType.ORDINAL)
    private ReportEnum reportEnum = ReportEnum.Unknown;

}
