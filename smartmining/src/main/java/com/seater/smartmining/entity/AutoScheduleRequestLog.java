package com.seater.smartmining.entity;

import com.seater.smartmining.enums.AutoScheduleRequestEnum;
import com.seater.smartmining.enums.AutoScheduleRequestTypeEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:智能调度判断请求/响应参数
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/9 0009 14:34
 */
@Entity
@Table
@Data
public class AutoScheduleRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private AutoScheduleRequestEnum request = AutoScheduleRequestEnum.UNKNOW;

    @Column(columnDefinition="text")
    private String requestJson = "";

    @Column
    @Enumerated(EnumType.ORDINAL)
    private AutoScheduleRequestTypeEnum type = AutoScheduleRequestTypeEnum.UNKNOW;

    @Column(columnDefinition="text")
    private String responseJson = "";

    @Column
    private Date requestTime = null;
}
