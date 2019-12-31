package com.seater.smartmining.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class ProjectWorkTimeSet  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;  //参与的项目编号

    @Column(nullable = false)
    private String name = "";        //班次名称

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ProjectWorkTimePoint startPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    Date startTime = new Date();    //开始时间

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    ProjectWorkTimePoint endPoint = ProjectWorkTimePoint.Today;

    @Column(nullable = false)
    Date endTime = new Date();      //结束时间

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

    public ProjectWorkTimePoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(ProjectWorkTimePoint startPoint) {
        this.startPoint = startPoint;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public ProjectWorkTimePoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(ProjectWorkTimePoint endPoint) {
        this.endPoint = endPoint;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getVaild() {
        return isVaild;
    }

    public void setVaild(Boolean vaild) {
        isVaild = vaild;
    }
}

