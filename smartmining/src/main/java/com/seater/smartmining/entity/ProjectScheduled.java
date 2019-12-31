package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import com.seater.smartmining.enums.WorkStatusEnums;

import javax.persistence.*;
import java.io.Serializable;

@Deprecated
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"diggingMachineId", "carId"}, name = "repeatCheck")})
public class ProjectScheduled  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = null;    //参与的项目编号

    @Column(nullable = false)
    private Long diggingMachineId = 0L;     //挖机ID

    @Column(nullable = false)
    private String diggingMachineCode = "";     //挖机编号

    @Column(nullable = false)
    private Long diggingMachineBrandId = 0L;      //挖机品牌ID

    @Column(nullable = false)
    private String diggingMachineBrandName = ""; //挖机品牌名

    @Column(nullable = false)
    private Long diggingMachineModelId = 0L;      //挖机型号ID

    @Column(nullable = false)
    private String diggingMachineModelName = ""; //挖机型号名

    @Column(nullable = false)
    private Long diggingMachineOwnerId = 0L;      //挖机车主编号

    @Column(nullable = false)
    private String diggingMachineOwnerName = ""; //挖机车主名称

    @Column
    private Long carId = 0L;     //渣车ID

    @Column
    private String carCode = "";     //渣车编号

    @Column
    private Long carBrandId = 0L;      //渣车品牌ID

    @Column
    private String carBrandName = ""; //渣车品牌名

    @Column
    private Long carModelId = 0L;      //渣车型号ID

    @Column
    private String carModelName = ""; //渣车型号名

    @Column
    private Long carOwnerId = 0L;      //渣车车主编号

    @Column
    private String carOwnerName = ""; //渣车车主名称

    @Column
    private Long materialId = null;   //当前装载物料ID

    @Column
    private String materiaName = null; //物料名称

    @Column
    private Long distance = null;    //当前运距

    @Column
    private PricingTypeEnums pricingType = PricingTypeEnums.Unknow;     //计价方式

    @Column
    private WorkStatusEnums workStatus = WorkStatusEnums.UNKNOW;        //todo 待确认 开机状态

    @Column
    private String groupCode = "";      //组编号

    @Column
    private String managerId = "";        //组长ID

    @Column
    private String managerName = "";        //组长名称

    @Column
    private String employeeId = "";         //组员编号

    @Column
    private String employeeName = "";       //组员名称

    @Column
    private Long createId = 0L;         //创建人ID

    @Column
    private String createName = "";         //创建人名称

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

    public Long getDiggingMachineBrandId() {
        return diggingMachineBrandId;
    }

    public void setDiggingMachineBrandId(Long diggingMachineBrandId) {
        this.diggingMachineBrandId = diggingMachineBrandId;
    }

    public String getDiggingMachineBrandName() {
        return diggingMachineBrandName;
    }

    public void setDiggingMachineBrandName(String diggingMachineBrandName) {
        this.diggingMachineBrandName = diggingMachineBrandName;
    }

    public Long getDiggingMachineModelId() {
        return diggingMachineModelId;
    }

    public void setDiggingMachineModelId(Long diggingMachineModelId) {
        this.diggingMachineModelId = diggingMachineModelId;
    }

    public String getDiggingMachineModelName() {
        return diggingMachineModelName;
    }

    public void setDiggingMachineModelName(String diggingMachineModelName) {
        this.diggingMachineModelName = diggingMachineModelName;
    }

    public Long getDiggingMachineOwnerId() {
        return diggingMachineOwnerId;
    }

    public void setDiggingMachineOwnerId(Long diggingMachineOwnerId) {
        this.diggingMachineOwnerId = diggingMachineOwnerId;
    }

    public String getDiggingMachineOwnerName() {
        return diggingMachineOwnerName;
    }

    public void setDiggingMachineOwnerName(String diggingMachineOwnerName) {
        this.diggingMachineOwnerName = diggingMachineOwnerName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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

    public Long getCarBrandId() {
        return carBrandId;
    }

    public void setCarBrandId(Long carBrandId) {
        this.carBrandId = carBrandId;
    }

    public String getCarBrandName() {
        return carBrandName;
    }

    public void setCarBrandName(String carBrandName) {
        this.carBrandName = carBrandName;
    }

    public Long getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Long carModelId) {
        this.carModelId = carModelId;
    }

    public String getCarModelName() {
        return carModelName;
    }

    public void setCarModelName(String carModelName) {
        this.carModelName = carModelName;
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

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMateriaName() {
        return materiaName;
    }

    public void setMateriaName(String materiaName) {
        this.materiaName = materiaName;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public PricingTypeEnums getPricingType() {
        return pricingType;
    }

    public void setPricingType(PricingTypeEnums pricingType) {
        this.pricingType = pricingType;
    }

    public WorkStatusEnums getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(WorkStatusEnums workStatus) {
        this.workStatus = workStatus;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

}