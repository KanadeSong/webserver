package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ProjectMaterial  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;  //参与的项目编号

    @Column(nullable = false)
    private String name = "";        //物料名称

    @Column
    private String remark = "";     //备注

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }
}
