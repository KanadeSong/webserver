package com.seater.user.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Description 推荐关系表
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:34
 */
@Data
@Entity
public class Recommend {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;
    
    @Column
    private Long recommendId;       //  推荐人id

    @Column
    private Long beRecommendId;       //  被推荐人id
    
    @Column
    private String beRecommendOpenId;     //  被推荐人openId
    
    @Column
    private RecommendType recommendType = RecommendType.Unknown;        //  推荐来源
    
    @Column
    private Date addTime;           //  创建时间
    
    @Column
    private Boolean valid;          //  是否有效
    
}
