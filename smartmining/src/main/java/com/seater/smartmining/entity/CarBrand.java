package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CarBrand implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(nullable = false)
    private String name = "";

    @Column
    private String remark = "";

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CarType type = CarType.Unknow;

    @Column(nullable = false)
    private Boolean vaild = true;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getRemark() {return remark;}

    public void setRemark(String remark) {this.remark = remark;}

    public CarType getType() {return this.type;}

    public void setType(CarType type) {this.type = type;}

    public Boolean getVaild() {return vaild;}

    public void setVaild(Boolean remark) {this.vaild = vaild;}

}