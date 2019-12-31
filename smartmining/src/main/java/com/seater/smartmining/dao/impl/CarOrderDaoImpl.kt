package com.seater.smartmining.dao.impl

import com.seater.smartmining.dao.CarOrderDaoI
import com.seater.smartmining.entity.CarOrder
import com.seater.smartmining.entity.repository.CarOrderRepository
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
class CarOrderDaoImpl(
        val applicationContext: ApplicationContext,
        val stringRedisTemplate: StringRedisTemplate,
        val CarOrderRepository: CarOrderRepository

) : CarOrderDaoI {

    val log = LoggerFactory.getLogger(CarOrderDaoImpl::class.java)
    var redisCacheTimeout = redisDefaultTimeout
    val keyGroup = "entity:CarOrder:"
    fun getKey(id: Long) = "${keyGroup}${id}"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    override fun delete(ids: List<Long>) {
        for (id in ids) {
            delete(id)
        }
    }

    override fun query(spec: Specification<CarOrder>?, pageable: Pageable): Page<CarOrder> {
        return CarOrderRepository.findAll(spec, pageable)
    }

    override fun get(id: Long): CarOrder? {
        if (id == 0L) return null
//        var key = getKey(id)
//        var CarOrder: CarOrder? = valueOps.jsonGet(key)
//        if (CarOrder != null) {
//            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
//            return CarOrder
//        }
        if (CarOrderRepository.existsById(id)) {
            var CarOrder = CarOrderRepository.getOne(id)
            //valueOps.jsonSet(key, CarOrder, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return CarOrder
        }
        return null
    }

    override fun save(CarOrder: CarOrder): CarOrder {
        val c = CarOrderRepository.save(CarOrder)

        if(c.orderNumber == null) c.orderNumber = 0L
        val keyMaxOrderNumber = "method:CarOrder:getMaxOrderNumber"
        var maxOrderNumber: Long? = valueOps.jsonGet(keyMaxOrderNumber)
        if (maxOrderNumber != null) {
            if (c.orderNumber!! > maxOrderNumber) valueOps.jsonSet(keyMaxOrderNumber, c.orderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
            else stringRedisTemplate.expire(keyMaxOrderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
        }
        //valueOps.jsonSet(getKey(id = CarOrder.id), c, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return c
    }


    override fun getMaxOrderNumber(): Long {
        val key = "method:CarOrder:getMaxOrderNumber"
        var maxOrderNumber: Long? = valueOps.jsonGet(key)
        if (maxOrderNumber != null) {
            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return maxOrderNumber
        }
        maxOrderNumber = CarOrderRepository.getMaxOrderNumber() ?: 0L
        valueOps.jsonSet(key, maxOrderNumber, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return maxOrderNumber
    }


    override fun delete(id: Long) {
        if (id == 0L) return
        //valueOps.operations.delete(getKey(id = id))
        CarOrderRepository.deleteById(id)
    }

}
