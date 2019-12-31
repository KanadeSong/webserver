package com.seater.smartmining.entity;

import com.seater.smartmining.enums.DiggingMachineStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/18 0018 17:34
 */
@Entity
@Table
@Data
public class ProjectDiggingWorkLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private Integer status = 0;

    @Column
    private Long machineId = 0L;

    @Column
    private String deviceCode = "";

    @Column
    private String message = "";

    @Column
    private Date createTime = null;

    @Column(columnDefinition = "text")
    private String detailMessage = "";
}
