package com.seater.smartmining.service.impl


import com.seater.smartmining.dao.ProjectDiggingMachineEfficiencyDaoI
import com.seater.smartmining.entity.ProjectDiggingMachineEfficiency
import com.seater.smartmining.entity.ScheduleMachine
import com.seater.smartmining.service.ProjectDiggingMachineEfficiencyServiceI
import com.sytech.user.exception.SytechLockFailException
import com.sytech.user.exception.SytechNoFindIdException
import com.sytech.user.filterEntity.QueryFlagFilter
import com.sytech.user.helpers.redisLockDefaultTimeout
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest
import org.springframework.data.querydsl.QSort
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
@javax.transaction.Transactional(rollbackOn = [Exception::class])
class ProjectDiggingMachineEfficiencyServiceImpl(
        val applicationContext: ApplicationContext,
        val projectDiggingMachineEfficiencyDaoI: ProjectDiggingMachineEfficiencyDaoI,
        val em: EntityManager
) : ProjectDiggingMachineEfficiencyServiceI {
    val log = LoggerFactory.getLogger(ProjectDiggingMachineEfficiencyServiceImpl::class.java)
    val entityName = "ProjectDiggingMachineEfficiency"

    val updateLock = false
    var redisLockTimeout = redisLockDefaultTimeout
    val keyGroup = "lock:projectDiggingMachineEfficiency:"
    fun getKey(id: Long) = "${keyGroup}${id}"

    override fun get(id: Long?): ProjectDiggingMachineEfficiency? {
        if (id == null) return null
        return projectDiggingMachineEfficiencyDaoI.get(id)
    }


    override fun save(projectDiggingMachineEfficiency: ProjectDiggingMachineEfficiency): ProjectDiggingMachineEfficiency {
        return projectDiggingMachineEfficiencyDaoI.save(projectDiggingMachineEfficiency.apply {
            this.updateTime = Date()
        })
    }

    override fun delete(id: Long) {
        projectDiggingMachineEfficiencyDaoI.delete(id = id)
    }

    override fun delete(ids: List<Long>) {
        projectDiggingMachineEfficiencyDaoI.delete(ids = ids)
    }



    override fun query(spec: Specification<ProjectDiggingMachineEfficiency>?, pageable: Pageable): Page<ProjectDiggingMachineEfficiency> {
        return projectDiggingMachineEfficiencyDaoI.query(spec = spec, pageable = pageable)
    }

    /**
     *
     */

    override fun queryPage(
            current: Int?,
            pageSize: Int?,
            projectDiggingMachineId: Long?,
            groupCode: String?,
            projectId: Long?
    ): Page<ProjectDiggingMachineEfficiency> {

        val minPageSize = 10
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<ProjectDiggingMachineEfficiency> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (projectDiggingMachineId != null) {
                ls.add(cb.equal(root.get<Long>("projectDiggingMachineId"), projectDiggingMachineId))
            }

            if(!groupCode.isNullOrBlank()){
                val digSub = query.subquery(Long::class.java)
                val digRoot = digSub.from(ScheduleMachine::class.java)

                var ls2 = mutableListOf<Predicate>()
                ls2.add(cb.equal(digRoot.get<String>("groupCode"), groupCode))

                digSub.where(cb.and(*ls2.toTypedArray<Predicate>()))
                digSub.select(digRoot.get<Long>("machineId"))
                ls.add(cb.`in`(root.get<Long>("projectDiggingMachineId")).value(digSub))
            }

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            cb.and(*ls.toTypedArray())
        }

        return query(pageable = PageRequest.of(cur, page), spec = spec)
    }

}
