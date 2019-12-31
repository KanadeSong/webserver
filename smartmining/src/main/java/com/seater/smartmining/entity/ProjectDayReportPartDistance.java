package com.seater.smartmining.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ProjectDayReportPartDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;                    //项目ID

    @Column
    private Long reportId = 0L;            //日报ID

    @Column
    private Date reportDate = new Date(0L);         //报表日期

    @Column
    private Long distance = 0L;   //运距

    @Column
    private Integer totalCount = 0; //本运距当日车数和

    @Column
    private Long totalCubic = 0L; //本运距当日方量和(两位小数)

    @Column
    private Long totalAmount = 0L;   //本运距当日金额和(两位小数)

    @Column
    private Integer grandTotalCount = 0;//本运距本月车数和

    @Column
    private Long grandTotalCubic = 0L; //本运距本月方量和(两位小数)

    @Column
    private Long grandTotalAmount = 0L; //本运距本月金额和(两位小数)

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

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalCubic() {
        return totalCubic;
    }

    public void setTotalCubic(Long totalCubic) {
        this.totalCubic = totalCubic;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getGrandTotalCount() {
        return grandTotalCount;
    }

    public void setGrandTotalCount(Integer grandTotalCount) {
        this.grandTotalCount = grandTotalCount;
    }

    public Long getGrandTotalCubic() {
        return grandTotalCubic;
    }

    public void setGrandTotalCubic(Long grandTotalCubic) {
        this.grandTotalCubic = grandTotalCubic;
    }

    public Long getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(Long grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }
}
