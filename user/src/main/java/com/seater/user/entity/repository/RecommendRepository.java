package com.seater.user.entity.repository;

import com.seater.user.entity.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:51
 */
public interface RecommendRepository extends JpaRepository<Recommend,Long> {
    
    public Recommend findByRecommendIdAndBeRecommendId(Long recommendId, Long beRecommendId);
}
