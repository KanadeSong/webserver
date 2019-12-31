package com.seater.user.dao;

import com.seater.user.entity.Recommend;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:49
 */
public interface RecommendDaoI {

    public Recommend get(Long id) throws IOException;
    public Recommend save(Recommend log) throws IOException;
    public void delete(Long id);
    public void delete(List<Long> ids);
    public Page<Recommend> query();
    public Page<Recommend> query(Example<Recommend> example);
    public Page<Recommend> query(Pageable pageable);
    public Page<Recommend> query(Example<Recommend> example, Pageable pageable);
    public List<Recommend> getAll();
    public Recommend findByRecommendIdAndBeRecommendId(Long recommendId, Long beRecommendId);
}
