package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "carId", "materialId"}, name = "repeatCheck")})
public class ProjectCarLoadMaterialSet  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;  //参与的项目编号

    @Column
    private Long carID = 0L;        //车辆ID

    @Column(nullable = false)
    private Long materialId = 0L;   //物料ID

    @Column(nullable = false)
    private String materialName = "";        //物料名称

    @Column(nullable = false)
    private Integer standardHeight = 0;     //设定高度

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

    public Long getCarID() {
        return carID;
    }

    public void setCarID(Long carID) {
        this.carID = carID;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getStandardHeight() {
        return standardHeight;
    }

    public void setStandardHeight(Integer standardHeight) {
        this.standardHeight = standardHeight;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }
}
