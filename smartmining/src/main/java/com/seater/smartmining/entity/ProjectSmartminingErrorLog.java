package com.seater.smartmining.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/9/19 0019 15:07
 */
@Entity
@Table
@Data
public class ProjectSmartminingErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column
    private Long projectId = 0L;

    @Lob
    @Column(columnDefinition = "text")
    private String errorMessage = "";

    @Lob
    @Column
    private String declaringClass = "";

    @Column
    private String methodName = "";

    @Column
    private int lineNumber = 0;

    @Column
    private Date createTime = null;

    @Column
    private Long userId = 0L;

    @Column
    private String userName = "";

    @Lob
    @Column(columnDefinition = "text")
    private String detailMessage = "";

    @Lob
    @Column(columnDefinition = "text")
    private String params = "";

    @Column
    private String typeMessage = "";
}
