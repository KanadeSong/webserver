package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Description:台时及包方结算扣除金额
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/16 0016 11:09
 */
@Entity
@Table
@Data
public class ProjectDiggingPartCountAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    private Long projectId = 0L;    //项目编号

    @Column
    private Long countId = 0L;      //对应报表的ID

    @Column
    private Long rend = 0L;         //房租

    @Column
    private Long grandRent = 0L;    //累计房租

    @Column
    private Long amountByMails = 0L;        //伙食费

    @Column
    private Long grandAmountByMails = 0L;       //累计伙食费

    @Column
    private Long subsidyAmount = 0L;        //补贴

    @Column
    private Long grandSubsidyAmount = 0L;       //累计补贴

    @Column
    private Long balance = 0L;          //余额 未输入以上数据 扣掉油费的余额

    @Column
    private Long balanceTotal = 0L;     //合计总余额

    @Column
    private Long balanceGrand = 0L;     //累计总余额
}
