package com.seater.smartmining.dao.impl

import com.seater.smartmining.dao.CarTextDaoI
import com.seater.smartmining.entity.CarText
import com.seater.smartmining.entity.repository.CarTextRepository
import com.seater.user.dao.GlobalSet.redisDefaultTimeout
import com.systech.helpers.redis.jsonGet
import com.systech.helpers.redis.jsonSet
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class CarTextDaoImpl(
        val applicationContext: ApplicationContext,
        val stringRedisTemplate: StringRedisTemplate,
        val CarTextRepository: CarTextRepository

) : CarTextDaoI {

    val log = LoggerFactory.getLogger(CarTextDaoImpl::class.java)
    var redisCacheTimeout = redisDefaultTimeout
    val keyGroup = "entity:CarText:"
    fun getKey(id: Long) = "${keyGroup}${id}"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    override fun delete(ids: List<Long>) {
        for (id in ids) {
            delete(id)
        }
    }

    override fun query(spec: Specification<CarText>?, pageable: Pageable): Page<CarText> {
        return CarTextRepository.findAll(spec, pageable)
    }

    override fun get(id: Long): CarText? {
        if (id == 0L) return null
//        var key = getKey(id)
//        var CarText: CarText? = valueOps.jsonGet(key)
//        if (CarText != null) {
//            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
//            return CarText
//        }
        if (CarTextRepository.existsById(id)) {
            var CarText = CarTextRepository.getOne(id)
            //valueOps.jsonSet(key, CarText, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return CarText
        }
        return null
    }

    override fun save(CarText: CarText): CarText {
        val c = CarTextRepository.save(CarText)

        if(c.orderNumber == null) c.orderNumber = 0L
        val keyMaxOrderNumber = "method:CarText:getMaxOrderNumber"
        var maxOrderNumber: Long? = valueOps.jsonGet(keyMaxOrderNumber)
        if (maxOrderNumber != null) {
            if (c.orderNumber!! > maxOrderNumber) valueOps.jsonSet(keyMaxOrderNumber, c.orderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
            else stringRedisTemplate.expire(keyMaxOrderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
        }
        //valueOps.jsonSet(getKey(id = CarText.id), c, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return c
    }


    override fun getMaxOrderNumber(): Long {
        val key = "method:CarText:getMaxOrderNumber"
        var maxOrderNumber: Long? = valueOps.jsonGet(key)
        if (maxOrderNumber != null) {
            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return maxOrderNumber
        }
        maxOrderNumber = CarTextRepository.getMaxOrderNumber() ?: 0L
        valueOps.jsonSet(key, maxOrderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return maxOrderNumber
    }


    override fun delete(id: Long) {
        if (id == 0L) return
        //valueOps.operations.delete(getKey(id = id))
        CarTextRepository.deleteById(id)
    }

}
