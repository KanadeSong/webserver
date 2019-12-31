package com.seater.smartmining.service.impl

import com.seater.smartmining.dao.CarTextDaoI
import com.seater.smartmining.entity.CarText
import com.seater.smartmining.service.CarTextServiceI
import com.sytech.user.helpers.redisLockDefaultTimeout
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
@javax.transaction.Transactional(rollbackOn = [Exception::class])
class CarTextServiceImpl(
        val applicationContext: ApplicationContext,
        val CarTextDaoI: CarTextDaoI,
        val em: EntityManager
) : CarTextServiceI {
    val log = LoggerFactory.getLogger(CarTextServiceImpl::class.java)
    val entityName = "CarText"

    val updateLock = false
    var redisLockTimeout = redisLockDefaultTimeout
    val keyGroup = "lock:CarText:"
    fun getKey(id: Long) = "${keyGroup}${id}"

    override fun get(id: Long?): CarText? {
        if (id == null) return null
        return CarTextDaoI.get(id)
    }

    override fun save(CarText: CarText): CarText {
        return CarTextDaoI.save(CarText = CarText.apply {
        })
    }

    override fun add(CarText: CarText): CarText {
        val new = save(CarText = CarText.apply{
            this.orderNumber = this.orderNumber?: (CarTextDaoI.getMaxOrderNumber() + 1)
        })
        return new
    }

    override fun update(CarText: CarText, old: CarText): CarText {
        val new = save(CarText = CarText)
        return new
    }

    override fun delete(id: Long) {
        val CarText = get(id) ?: return
        CarTextDaoI.delete(id = id)
    }

    override fun delete(ids: List<Long>) {
        CarTextDaoI.delete(ids = ids)
    }

    override fun query(spec: Specification<CarText>?, pageable: Pageable): Page<CarText> {
        return CarTextDaoI.query(spec = spec, pageable = pageable)
    }

}
