package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PlaceEnum;
import com.seater.smartmining.enums.PlaceStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description: 作业地点表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/6/6 0006 14:30
 */
@Entity
@Table
@Data
public class ProjectPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;    //项目ID

    @Column
    private String placeName = "";      //工作地点名称

    @Column
    private PlaceEnum place = PlaceEnum.UNKNOW;     //工作地点类型

    @Column
    private PlaceStatusEnum placeStatus = PlaceStatusEnum.UNKNOW;       //工作地点状态

    @Column
    private String remarks = "";

    @Column
    private Date createDate = null;



}
