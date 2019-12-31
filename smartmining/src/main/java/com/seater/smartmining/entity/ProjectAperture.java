package com.seater.smartmining.entity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Description:钻孔表
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/10 0010 17:49
 */
@Entity
@Table
@Data
public class ProjectAperture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;       //主键ID

    @Column
    @NotEmpty(message = "钻孔名称不能为空")
    private String name = "";       //钻孔名称

    @Column
    private Long projectId = 0L;        //项目ID

    @Column
    private String remark = "";         //备注

    @Column
    @NotNull(message = "钻孔单价不能为空")
    private BigDecimal price = BigDecimal.ZERO;   //单价 元/米

    @Column
    private Boolean valid = true;       //是否有效
}
