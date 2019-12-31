package com.seater.smartmining.entity;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/15 0015 10:03
 */
@Entity
@Table
@Data
public class ProjectCarCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;           //主键ID

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private Long carId = 0L;        //车辆ID

    @Column
    private String carCode = "";        //车辆编号

    @Column(columnDefinition="text")
    private String detailJson = "";         //详情字符串

    @Column
    private Long totalCount = 0L;       //有效总车数

    @Column
    private Long unValidCount = 0L;     //无效总车数

    @Column
    private Long workTime = 0L;         //挖机工作总时长

    @Column
    private Long distance = 0L;         //总运距

    @Column
    private Shift shifts = Shift.Unknown;        //班次

    @Column
    private Date dateIdentification = null;         //日期标识

    @Column
    private CarType carType = CarType.Unknow;       //车辆类型

    @Column
    private Date createTime = new Date();       //创建时间
}
