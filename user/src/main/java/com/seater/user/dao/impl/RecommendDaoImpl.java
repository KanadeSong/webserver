package com.seater.user.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seater.helpers.JsonHelper;
import com.seater.user.dao.GlobalSet;
import com.seater.user.dao.RecommendDaoI;
import com.seater.user.entity.Recommend;
import com.seater.user.entity.repository.RecommendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Author by sytech.xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/3/28 22:50
 */
@Repository
public class RecommendDaoImpl implements RecommendDaoI {

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RecommendRepository recommendRepository;

    ValueOperations<String, String> valueOps = null;
    String keyGroup = "entity:recommend:";

    String getKey(Long id) {return keyGroup + id.toString();}
    ValueOperations<String, String> getValueOps() {if(valueOps == null) valueOps = stringRedisTemplate.opsForValue(); return valueOps;}


    @Override
    public void delete(List<Long> ids) {
        for(Long id : ids)
        {
            delete(id);
        }
    }

    @Override
    public Page<Recommend> query(Example<Recommend> example, Pageable pageable) {
        return recommendRepository.findAll(example, pageable);
    }

    @Override
    public Page<Recommend> query(Example<Recommend> example) {
        return recommendRepository.findAll(example, PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Page<Recommend> query(Pageable pageable) {
        return recommendRepository.findAll(pageable);
    }

    @Override
    public Page<Recommend> query() {
        return recommendRepository.findAll(PageRequest.of(0, GlobalSet.defaultPageSize));
    }

    @Override
    public Recommend get(Long id) throws IOException {
        if(id == 0L) return null;

        String key = getKey(id);
        String obj = getValueOps().get(key);
        if(obj != null)
        {
            stringRedisTemplate.expire(key, GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return JsonHelper.jsonStringToObject(obj, Recommend.class);
        }
        if(recommendRepository.existsById(id))
        {
            Recommend user = recommendRepository.findById(id).get();
            getValueOps().set(key, new ObjectMapper().writeValueAsString(user), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
            return user;
        }

        return null;
    }

    @Override
    public Recommend save(Recommend log) throws IOException {
        Recommend log1 = recommendRepository.save(log);
        getValueOps().set(getKey(log.getId()), JsonHelper.toJsonString(log), GlobalSet.redisDefaultTimeout, TimeUnit.MILLISECONDS);
        return log1;
    }

    @Override
    public void delete(Long id) {
        if(id == 0L) return;
        getValueOps().getOperations().delete(getKey(id));
        recommendRepository.deleteById(id);
    }

    @Override
    public List<Recommend> getAll() {
        return recommendRepository.findAll();
    }

    @Override
    public Recommend findByRecommendIdAndBeRecommendId(Long recommendId, Long beRecommendId){
        return recommendRepository.findByRecommendIdAndBeRecommendId(recommendId,beRecommendId);
    }
}
