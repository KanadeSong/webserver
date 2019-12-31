package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 油枪 暂时用不到
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/4/23 11:45
 */
@Data
@Entity
public class Nozzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long projectId;             //  参与的项目id

    @Column(nullable = false)
    private Integer port;               //  加油端口号

    @Column(nullable = false)
    private Long primaryCount = 0L;     //  初始化油表数 单位:毫升

    @Column(nullable = false)
    private Date addTime = new Date();  //  添加时间

    @Column(nullable = false)
    private Boolean valid = true;       //  是否有效
    
    @Column(nullable = false)
    private Long oilCarId;              //  油车id

}
