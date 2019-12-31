package com.seater.smartmining.controller

import com.seater.user.entity.SysUser
import com.seater.user.entity.SysUserProjectRole
import com.seater.user.entity.repository.SysRoleRepository
import com.seater.user.entity.repository.SysUserProjectRoleRepository
import com.seater.user.service.SysUserServiceI
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.persistence.criteria.Predicate


/**
 *
 */
@RestController
@javax.transaction.Transactional(rollbackOn = [Exception::class])
@RequestMapping("/api/NewSysUser")
class NewSysUserController(
        val sysUserServiceI: SysUserServiceI,
        val sysRoleRepository: SysRoleRepository,
        val sysUserProjectRoleRepository: SysUserProjectRoleRepository
) {

    /**
     *
     */
    @RequestMapping("/query")
    fun query(
            current: Int?,
            pageSize: Int?,
            projectId: Long?
    ): Any? {
        var cur = (current ?: 0) - 1
        var page = pageSize ?: 10
        if (cur < 0) cur = 0
        if (page < 0) page = 10
        else if (page > 10000) page = 10000

        var spec = Specification<SysUser> { root, query, cb ->
            var ls = mutableListOf<Predicate>()
            if (projectId != null) {
                val ruSub = query.subquery(Long::class.java)
                val ruRoot = ruSub.from(SysUserProjectRole::class.java)
                var ls2 = mutableListOf<Predicate>()
                ls2.add(cb.equal(ruRoot.get<Long>("projectId"), projectId))
                ruSub.where(cb.and(*ls2.toTypedArray<Predicate>()))
                ruSub.select(ruRoot.get<Long>("userId"))
                ls.add(cb.`in`(root.get<Long>("id")).value(ruSub))
            }
            cb.and(*ls.toTypedArray())
        }

        val rt = sysUserServiceI.query(spec, PageRequest.of(cur, page))
        val sysRoleAll = sysRoleRepository.findAll()
        val roleUserAll = sysUserProjectRoleRepository.findAll()
        return mapOf(
                "content" to rt.content.map {
                    val userId = it.id
                    mapOf(
                            "it" to it,
                            "other" to mapOf<String, Any?>(
                                    "sysRoleList" to roleUserAll.filter {
                                        val f = it.userId == userId
                                        if (projectId == null) f else f && it.projectId == projectId
                                    }.map {
                                        val roleId = it.roleId
                                        mapOf(
                                                "sysUserProjectRole" to it,
                                                "sysRole" to sysRoleAll.filter { it.id == roleId }.firstOrNull()
                                        )
                                    }.filter { it.get("sysRole") != null }
                            )
                    )
                },
                "totalElements" to rt.totalElements
        )
    }
}