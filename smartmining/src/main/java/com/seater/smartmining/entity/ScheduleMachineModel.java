package com.seater.smartmining.entity;

import com.seater.smartmining.enums.DiggingMachineStatus;
import com.seater.smartmining.enums.PricingTypeEnums;
import lombok.Data;

import javax.persistence.*;

/**
 * @Description:挖机排班方案模板
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 10:21
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"projectId", "programmeId", "machineId","machineCode"}, name = "repeatCheck")})
@Data
public class ScheduleMachineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id = 0L;           //主键ID

    @Column
    public Long projectId = 0L;        //项目ID

    @Column
    private Long programmeId = 0L;      //方案ID

    @Column
    public Long machineId = 0L;        //挖机ID

    @Column
    public String machineCode = "";        //挖机编号

    @Column
    public String groupCode = "";          //分组编号

    @Column
    public PricingTypeEnums pricingType = PricingTypeEnums.Unknow;         //计价方式

    @Column
    public Long materialId = 0L;       //物料编号

    @Column
    public String materialName = "";       //物料名称

    @Column
    public Long distance = 0L;         //运距

    @Column
    public Long diggingMachineBrandId = 0L;     //挖机类型ID

    @Column
    public String diggingMachineBrandName = "";     //挖机类型名称

    @Column
    public Long diggingMachineModelId = 0L;     //挖机品牌ID

    @Column
    public String diggingMachineModelName = "";     //挖机品牌名称

    @Column
    public Long diggingMachineOwnerId = 0L;         //挖机车主ID

    @Column
    public String diggingMachineOwnerName = "";         //挖机车主名称

    @Column
    public Boolean isVaild = true;     //是否有效

    //挖机状态
    @Column
    @Enumerated(EnumType.ORDINAL)
    public DiggingMachineStatus diggingMachineStatus = DiggingMachineStatus.Stop;

    //是否故障
    @Column
    public Boolean fault = false;
}
