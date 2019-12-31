package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CarModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CarType type = CarType.Unknow;

    @Column(nullable = false)
    private Long brandId = 0L;

    @Column(nullable = false)
    private String brandName = "";

    @Column(nullable = false)
    private String name = "";

    @Column
    private String remark = "";

    @Column(nullable = false)
    private Boolean vaild = true;

    @Column(nullable = true)
    private Integer length = 0; //车厢长度

    @Column(nullable = true)
    private Integer width = 0; //车厢宽度

    @Column(nullable = true)
    private Integer height = 0; //车厢高度
    
    private String avatar;      //  图标
    
    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public Long getBrandId() {return brandId;}

    public void setBrandId(Long brandId) {this.brandId = brandId;}

    public String getBrandName() {return brandName;}

    public void setBrandName(String brandName) {this.brandName = brandName;}

    public String getRemark() {return remark;}

    public void setRemark(String remark) {this.remark = remark;}

    public CarType getType() {return this.type;}

    public void setType(CarType type) {this.type = type;}

    public Boolean getVaild() {return vaild;}

    public void setVaild(Boolean remark) {this.vaild = vaild;}

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}