package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ProjectHourPrice  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private Long brandId = 0L;      //品牌ID

    @Column(nullable = false)
    private String brandName = ""; //品牌名

    @Column(nullable = false)
    private Long modelId = 0L;      //型号ID

    @Column(nullable = false)
    private String modelName = ""; //型号名

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CarType carType = CarType.Unknow;   //车辆类型

    @Column(nullable = false)
    private Long price = 0L;        //时薪

    @Column(nullable = false)
    private Boolean isVaild = true; //是否有效

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public CarType getCarType() {
        return carType;
    }

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }
}
