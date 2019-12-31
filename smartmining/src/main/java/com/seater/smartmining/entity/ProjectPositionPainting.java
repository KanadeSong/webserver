package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PaintingEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/12/18 0018 9:13
 */
@Entity
@Table
@Data
public class ProjectPositionPainting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long placeId = 0L;      //平台ID 可能是渣场ID、工作平台ID等

    @Column
    private String placeName = "";      //平台名称

    @Column
    private PaintingEnum paintingType = PaintingEnum.Unknow;        //平台类型

    @Column(columnDefinition = "text")
    private String positionText = "";       //圈出来的坐标

    @Column
    private Long createId = 0L;         //创建人ID

    @Column
    private String createName = "";     //创建人名称

    @Column
    private Long modifyId = 0L;     //修改人ID

    @Column
    private String modifyName = "";     //修改人名称

    @Column
    private Date createTime = new Date();       //创建时间

    @Column
    private Date modifyTime = new Date(0);       //修改时间
}
