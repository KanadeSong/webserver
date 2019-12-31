package com.seater.smartmining.dao.impl

import com.systech.helpers.redis.jsonGet
import com.systech.helpers.redis.jsonSet
import com.seater.smartmining.dao.ProjectDiggingMachineEfficiencyDaoI
import com.seater.smartmining.entity.ProjectDiggingMachineEfficiency
import com.seater.smartmining.entity.repository.ProjectDiggingMachineEfficiencyRepository
import com.seater.user.dao.GlobalSet.redisDefaultTimeout
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ProjectDiggingMachineEfficiencyDaoImpl(
        val applicationContext: ApplicationContext,
        val stringRedisTemplate: StringRedisTemplate,
        val projectDiggingMachineEfficiencyRepository: ProjectDiggingMachineEfficiencyRepository

) : ProjectDiggingMachineEfficiencyDaoI {

    val log = LoggerFactory.getLogger(ProjectDiggingMachineEfficiencyDaoImpl::class.java)
    var redisCacheTimeout = redisDefaultTimeout
    val keyGroup = "entity:projectDiggingMachineEfficiency:"
    fun getKey(id: Long) = "${keyGroup}${id}"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    override fun delete(ids: List<Long>) {
        for (id in ids) {
            delete(id)
        }
    }

    override fun query(spec: Specification<ProjectDiggingMachineEfficiency>?, pageable: Pageable): Page<ProjectDiggingMachineEfficiency> {
        return projectDiggingMachineEfficiencyRepository.findAll(spec, pageable)
    }

    override fun get(id: Long): ProjectDiggingMachineEfficiency? {
        if (id == 0L) return null
        var key = getKey(id)
        var projectDiggingMachineEfficiency: ProjectDiggingMachineEfficiency? = valueOps.jsonGet(key)
        if (projectDiggingMachineEfficiency != null) {
            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectDiggingMachineEfficiency
        }
        projectDiggingMachineEfficiency = projectDiggingMachineEfficiencyRepository.getOne(id)
        if (projectDiggingMachineEfficiency != null) {
            valueOps.jsonSet(key, projectDiggingMachineEfficiency, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectDiggingMachineEfficiency
        }
        return null
    }

    override fun save(projectDiggingMachineEfficiency: ProjectDiggingMachineEfficiency): ProjectDiggingMachineEfficiency {
        val c = projectDiggingMachineEfficiencyRepository.save(projectDiggingMachineEfficiency)

        valueOps.jsonSet(getKey(id = projectDiggingMachineEfficiency.id), c, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return c
    }


    override fun delete(id: Long) {
        if (id == 0L) return

        valueOps.operations.delete(getKey(id = id))
        projectDiggingMachineEfficiencyRepository.deleteById(id)
    }
}
