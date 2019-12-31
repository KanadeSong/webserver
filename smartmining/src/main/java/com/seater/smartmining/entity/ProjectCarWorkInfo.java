package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.VaildEnums;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class ProjectCarWorkInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;  //参与的项目编号

    @Column
    private Long carId = 0L;     //渣车ID

    @Column
    private String carCode = "";     //渣车编号

    @Column
    private Long carOwnerId = 0L;      //渣车车主ID

    @Column
    private String carOwnerName = ""; //渣车车主名

    @Column
    private Long diggingMachineId = 0L;     //挖机ID

    @Column
    private String diggingMachineCode = "";     //挖机编号

    @Column
    private Long materialId = null;   //当前装载物料ID

    @Column
    private String materialName = null; //物料名称

    @Column
    private Date timeLoad = null; //装载时间

    @Column
    private Date timeCheck = null; //检测时间

    @Column
    private Date timeDischarge = null; //御载时间

    @Column
    private Integer height = 0; //装载高度

    @Column
    private Long cubic = 0L;  //装载方量

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Score pass = Score.Unknown; //检测结果

    @Column
    private Long slagSiteId = 0L; //渣场ID

    @Column
    private String slagSiteName = ""; //渣场名称

    @Column
    private Long distance = 0L;   //运距

    @Column
    private Long payableDistance = 0L; //付费运距

    @Column
    private Long amount = 0L;         //金额

    @Column
    private Shift shift = Shift.Unknown;    //班次

    @Column
    private Boolean loadUp = false;    //挖机终端是否已上传

    @Column
    private Boolean checkUp = false;    //检测终端是否已上传

    @Column
    private Boolean unLoadUp = false; //渣场终端是否已上传

    @Column
    private ProjectCarWorkStatus Status = ProjectCarWorkStatus.Unknown;  //状态

    @Column
    private  String remark = ""; //备注

    @Column
    private String note = "";       //异常处理备注

    @Column
    private Date createDate = new Date();   //创建日期

    @Column(nullable = false)
    private VaildEnums isVaild = VaildEnums.VAILD;       //是否有效

    public Integer getMergeCode() {
        return mergeCode;
    }

    public void setMergeCode(Integer mergeCode) {
        this.mergeCode = mergeCode;
    }

    @Column
    private Integer mergeCode = 0;        //合并编号

    public String getMergeMessage() {
        return mergeMessage;
    }

    public void setMergeMessage(String mergeMessage) {
        this.mergeMessage = mergeMessage;
    }

    @Column
    private String mergeMessage = "";       //合并信息说明

    @Column
    private Long detailId = 0L;         //处理人编号

    @Column
    private String detailName = "";     //处理人名称

    @Column
    private PricingTypeEnums pricingType = PricingTypeEnums.Unknow;     //计时计方

    @Column
    private Date dateIdentification = null;         //日期标识

    @Column
    private Boolean stopByManual = false;

    @Column
    private Long workTimeByDiggingId = 0L;          //对应挖机上下班表的主键ID

    public String getAllowSlagSites() {
        return allowSlagSites;
    }

    public void setAllowSlagSites(String allowSlagSites) {
        this.allowSlagSites = allowSlagSites;
    }

    @Column
    private String allowSlagSites = "";     //允许倒渣的渣场

    @Column
    private Boolean infoValid = true;       //超时标识

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public ProjectDispatchMode dispatchMode = ProjectDispatchMode.Unknown;           //调度模式

    public BigDecimal getTimeStay() {
        return timeStay;
    }

    public void setTimeStay(BigDecimal timeStay) {
        this.timeStay = timeStay;
    }

    @Column
    private BigDecimal timeStay = BigDecimal.ZERO;     //等待时长

    public ProjectDispatchMode getDispatchMode() {
        return dispatchMode;
    }

    public void setDispatchMode(ProjectDispatchMode dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

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

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Long getCarOwnerId() {
        return carOwnerId;
    }

    public void setCarOwnerId(Long carOwnerId) {
        this.carOwnerId = carOwnerId;
    }

    public String getCarOwnerName() {
        return carOwnerName;
    }

    public void setCarOwnerName(String carOwnerName) {
        this.carOwnerName = carOwnerName;
    }

    public Long getDiggingMachineId() {
        return diggingMachineId;
    }

    public void setDiggingMachineId(Long diggingMachineId) {
        this.diggingMachineId = diggingMachineId;
    }

    public String getDiggingMachineCode() {
        return diggingMachineCode;
    }

    public void setDiggingMachineCode(String diggingMachineCode) {
        this.diggingMachineCode = diggingMachineCode;
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

    public void setMateriaName(String materiaName) {
        this.materialName = materiaName;
    }

    public Date getTimeLoad() {
        return timeLoad;
    }

    public void setTimeLoad(Date timeLoad) {
        this.timeLoad = timeLoad;
    }

    public Date getTimeCheck() {
        return timeCheck;
    }

    public void setTimeCheck(Date timeCheck) {
        this.timeCheck = timeCheck;
    }

    public Date getTimeDischarge() {
        return timeDischarge;
    }

    public void setTimeDischarge(Date timeDischarge) {
        this.timeDischarge = timeDischarge;
    }

    public Long getCubic() {
        return cubic;
    }

    public void setCubic(Long cubic) {
        this.cubic = cubic;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Score getPass() {
        return pass;
    }

    public void setPass(Score pass) {
        this.pass = pass;
    }

    public Long getSlagSiteId() {
        return slagSiteId;
    }

    public void setSlagSiteId(Long slagSiteId) {
        this.slagSiteId = slagSiteId;
    }

    public String getSlagSiteName() {
        return slagSiteName;
    }

    public void setSlagSiteName(String slagSiteName) {
        this.slagSiteName = slagSiteName;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getPayableDistance() {
        return payableDistance;
    }

    public void setPayableDistance(Long payableDistance) {
        this.payableDistance = payableDistance;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public Boolean getLoadUp() {
        return loadUp;
    }

    public void setLoadUp(Boolean loadUp) {
        this.loadUp = loadUp;
    }

    public Boolean getCheckUp() {
        return checkUp;
    }

    public void setCheckUp(Boolean checkUp) {
        this.checkUp = checkUp;
    }

    public Boolean getUnLoadUp() {
        return unLoadUp;
    }

    public void setUnLoadUp(Boolean unLoadUp) {
        this.unLoadUp = unLoadUp;
    }

    public ProjectCarWorkStatus getStatus() {
        return Status;
    }

    public void setStatus(ProjectCarWorkStatus status) {
        Status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public VaildEnums getIsVaild() {
        return isVaild;
    }

    public void setIsVaild(VaildEnums isVaild) {
        this.isVaild = isVaild;
    }

    public Date getDateIdentification() {
        return dateIdentification;
    }

    public void setDateIdentification(Date dateIdentification) {
        this.dateIdentification = dateIdentification;
    }

    public PricingTypeEnums getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingTypeEnums pricingType) {
        this.pricingType = pricingType;
    }

    public Boolean getStopByManual() {
        return stopByManual;
    }

    public void setStopByManual(Boolean stopByManual) {
        this.stopByManual = stopByManual;
    }

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public Long getWorkTimeByDiggingId() {
        return workTimeByDiggingId;
    }

    public void setWorkTimeByDiggingId(Long workTimeByDiggingId) {
        this.workTimeByDiggingId = workTimeByDiggingId;
    }

    public Boolean getInfoValid() {
        return infoValid;
    }

    public void setInfoValid(Boolean infoValid) {
        this.infoValid = infoValid;
    }

}


