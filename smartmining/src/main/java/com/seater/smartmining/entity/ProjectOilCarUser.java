package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description 油车-管理员关系
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/1 16:37
 */
@Entity
@Data
public class ProjectOilCarUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    private Long oilCarId = 0L;     //油车id

    private Long projectId = 0L;     //项目id

    private Long managerId = 0L;    //油车管理员id

    private String managerName = "";    //油车管理员名称

    private Date addTime = new Date();  //创建日期

    private Boolean isValid = true; //是否有效

    private Date updateTime = null; //更新时间
}
