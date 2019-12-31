package com.seater.smartmining.dao.impl

import com.systech.helpers.redis.jsonGet
import com.systech.helpers.redis.jsonSet
import com.seater.smartmining.dao.ProjectScheduleDetailDaoI
import com.seater.smartmining.entity.ProjectScheduleDetail
import com.seater.smartmining.entity.repository.ProjectScheduleDetailRepository
import com.seater.user.dao.GlobalSet.redisDefaultTimeout
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class ProjectScheduleDetailDaoImpl(
        val applicationContext: ApplicationContext,
        val stringRedisTemplate: StringRedisTemplate,
        val projectScheduleDetailRepository: ProjectScheduleDetailRepository

) : ProjectScheduleDetailDaoI {

    val log = LoggerFactory.getLogger(ProjectScheduleDetailDaoImpl::class.java)
    var redisCacheTimeout = redisDefaultTimeout
    val keyGroup = "entity:projectScheduleDetail:"
    val keyGroupP = "getByPC:projectScheduleDetail:"
    fun getKey(id: Long) = "${keyGroup}${id}"
    fun getKeyP(pid: Long, cid: Long) = "${keyGroup}:${pid}:${cid}:"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    override fun delete(ids: List<Long>) {
        for (id in ids) {
            delete(id)
        }
    }

    override fun query(spec: Specification<ProjectScheduleDetail>?, pageable: Pageable): Page<ProjectScheduleDetail> {
        return projectScheduleDetailRepository.findAll(spec, pageable)
    }

    override fun get(id: Long): ProjectScheduleDetail? {
        if (id == 0L) return null
        var key = getKey(id)
        var projectScheduleDetail: ProjectScheduleDetail? = valueOps.jsonGet(key)
        if (projectScheduleDetail != null) {
            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectScheduleDetail
        }
        projectScheduleDetail = projectScheduleDetailRepository.getOne(id)
        if (projectScheduleDetail != null) {
            valueOps.jsonSet(key, projectScheduleDetail, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectScheduleDetail
        }
        return null
    }

    override fun save(projectScheduleDetail: ProjectScheduleDetail): ProjectScheduleDetail {
        val c = projectScheduleDetailRepository.save(projectScheduleDetail.apply{
            this.updateTime = Date()
        })

        valueOps.jsonSet(getKey(id = c.id), c, redisCacheTimeout, TimeUnit.MILLISECONDS)
        valueOps.jsonSet(getKeyP(pid = c.projectId, cid = c.projectCarId), c, redisCacheTimeout, TimeUnit.MILLISECONDS)
        return c
    }


    override fun delete(id: Long) {
        val c = get(id)?: return
        if (id == 0L) return

        valueOps.operations.delete(getKey(id = id))
        valueOps.operations.delete(getKeyP(pid = c.projectId, cid = c.projectCarId))
        projectScheduleDetailRepository.deleteById(id)
    }

    override fun getByProjectCarIdAndProjectId(projectCarId: Long, projectId: Long): ProjectScheduleDetail? {
        var key = getKeyP(projectId, projectCarId)
        var projectScheduleDetail: ProjectScheduleDetail? = valueOps.jsonGet(key)
        if (projectScheduleDetail != null) {
            stringRedisTemplate.expire(key, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectScheduleDetail
        }
        val projectScheduleDetailList = projectScheduleDetailRepository.findByProjectCarIdAndProjectId(projectCarId, projectId).sortedByDescending { it.id }
        projectScheduleDetail = projectScheduleDetailList.firstOrNull()
        if (projectScheduleDetail != null) {
//            if(projectScheduleDetailList.size > 1){
//                projectScheduleDetailList.forEach {
//                    if(it.id != projectScheduleDetail.id) delete(it.id)
//                }
//            }
            //valueOps.jsonSet(key, projectScheduleDetail, redisCacheTimeout, TimeUnit.MILLISECONDS)
            return projectScheduleDetail
        }
        return null
    }

    override fun findByProjectDigIdInAndProjectId(projectDigIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail> {
        return projectScheduleDetailRepository.findByProjectDiggingMachineIdInAndProjectId(projectDigIdList, projectId)
    }

    override fun findByProjectSiteIdInAndProjectId(projectSiteIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail> {
        return projectScheduleDetailRepository.findByProjectSlagSiteIdInAndProjectId(projectSiteIdList, projectId)
    }

}
