package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @Description 排班组-对讲组 关系
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/9/19 14:07
 */
@Data
@Entity
public class InterPhoneSchedule implements Serializable {

    private static final long serialVersionUID = 6674593907376147371L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    /**
     * 对讲组id
     */
    private Long interPhoneGroupId = 0L;

    /**
     * 排班组id
     */
    private Long scheduleId = 0L;

    /**
     * 项目id
     */
    private Long projectId = 0L;

    /**
     * 排班组的groupCode
     */
    private String groupCode = "";
}
