package com.seater.user.service.impl;

import com.seater.user.dao.RecommendDaoI;
import com.seater.user.entity.Recommend;
import com.seater.user.service.RecommendServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:48
 */
@Service
public class RecommendServiceImpl implements RecommendServiceI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    RecommendDaoI recommendDaoI;

    @Override
    public Recommend get(Long id) throws IOException {
        return recommendDaoI.get(id);
    }

    @Override
    public Recommend save(Recommend log) throws IOException {
        return recommendDaoI.save(log);
    }

    @Override
    public void delete(Long id) {
        recommendDaoI.delete(id);
    }

    @Override
    public void delete(List<Long> ids) {
        recommendDaoI.delete(ids);
    }

    @Override
    public Page<Recommend> query(Example<Recommend> example, Pageable pageable) {
        return recommendDaoI.query(example, pageable);
    }

    @Override
    public Page<Recommend> query(Example<Recommend> example) {
        return recommendDaoI.query(example);
    }

    @Override
    public Page<Recommend> query(Pageable pageable) {
        return recommendDaoI.query(pageable);
    }

    @Override
    public Page<Recommend> query() {
        return recommendDaoI.query();
    }

    @Override
    public List<Recommend> getAll() {
        return recommendDaoI.getAll();
    }

    @Override
    public Recommend findByRecommendIdAndBeRecommendId(Long recommendId, Long beRecommendId) {
        return recommendDaoI.findByRecommendIdAndBeRecommendId(recommendId,beRecommendId);
    }
}
