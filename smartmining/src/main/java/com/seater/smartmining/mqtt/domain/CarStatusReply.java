package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/31 0031 11:37
 */
public class CarStatusReply {

    private String cmdInd = "";
    private Long pktID = 0L;
    private Long slagcarID = 0L;
    private String slagcarCode = "";
    private String carCode = "";
    private Long projectId = 0L;
    private Long projectID = 0L;
    private Integer status = 0;
    private String statusName = "";
    private String schedule = "";
    private Long carCount = 0L;
    private Integer shift = 0;
    private String message = "";
    private Integer cmdStatus = 0;
    private Long unValidTol = 0L;

    public String getCmdInd() {
        return cmdInd;
    }

    public void setCmdInd(String cmdInd) {
        this.cmdInd = cmdInd;
    }

    public Long getPktID() {
        return pktID;
    }

    public void setPktID(Long pktID) {
        this.pktID = pktID;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Long getCarCount() {
        return carCount;
    }

    public void setCarCount(Long carCount) {
        this.carCount = carCount;
    }

    public Integer getShift() {
        return shift;
    }

    public void setShift(Integer shift) {
        this.shift = shift;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCmdStatus() {
        return cmdStatus;
    }

    public void setCmdStatus(Integer cmdStatus) {
        this.cmdStatus = cmdStatus;
    }

    public Long getSlagcarID() {
        return slagcarID;
    }

    public void setSlagcarID(Long slagcarID) {
        this.slagcarID = slagcarID;
    }

    public String getSlagcarCode() {
        return slagcarCode;
    }

    public void setSlagcarCode(String slagcarCode) {
        this.slagcarCode = slagcarCode;
    }

    public Long getUnValidTol() {
        return unValidTol;
    }

    public void setUnValidTol(Long unValidTol) {
        this.unValidTol = unValidTol;
    }
}
