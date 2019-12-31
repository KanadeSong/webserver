package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/2 0002 22:26
 */
@Entity
@Table
@Data
public class ProjectMqttParamsRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Column
    private String request = "";

    @Column(columnDefinition = "text")
    private String mattParams = "";

    @Column(columnDefinition = "text")
    private String requestParams = "";

    @Column(columnDefinition = "text")
    private String queryParams = "";

    @Column
    private Date createTime = null;
}
