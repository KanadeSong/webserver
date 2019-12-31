package com.seater.smartmining.controller


import com.seater.smartmining.entity.CarText
import com.seater.smartmining.entity.init
import com.seater.smartmining.service.CarTextServiceI
import com.sytech.user.helpers.defaultPageSize
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * CarText
 */
@RestController
@javax.transaction.Transactional(rollbackOn = [Exception::class])
@RequestMapping("/api/carText")
class CarTextController(
        val carTextServiceI: CarTextServiceI
) {

    /**
     * @Description 删除 CarText列表
     * @see CarText
     * @url /api/carText/deletes
     * @paramInfo |参数名|必选|类型|说明|
     * @param ids |是 |List<Long> |CarText的id列表
     * @return  {"status":"success","message":""}
     */
    @RequestMapping("/deletes")
    fun deletes(
            @RequestParam(value = "ids[]", required = true) ids: List<Long>
    ): Any? {
        ids.forEach {
            carTextServiceI.delete(it)
        }
        return """{"status":"success","message":""}"""
    }

    /**
     * @Description 查询 CarText
     * @see CarText
     * @url /api/carText/query
     * @paramInfo |参数名|必选|类型|说明|
     * @return <code></code>
     */
    @RequestMapping("/query")
    fun query(
            current: Int?,
            pageSize: Int?
    ): Any? {
        val rt = carTextServiceI.query(
                pageable = PageRequest.of(current?: 0, pageSize?: defaultPageSize)
        )
        return mapOf(
                "content" to rt.content.map {
                    mapOf(
                            "it" to it
                    )
                },
                "totalElements" to rt.totalElements
        )
    }

    /**
     * @Description 新增 CarText
     * @see CarText
     * @url /api/carText/add
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/add")
    fun add(
            carText: CarText
    ): Any? {
        carTextServiceI.add(carText)
        return """{"status":"success","message":""}"""
    }

    /**
     * @Description 修改 CarText
     * @see CarText
     * @url /api/carText/update
     * @return {"status":"success","message":""}
     */
    @RequestMapping("/update")
    fun update(
            carText: CarText
    ): Any? {
        val old = carTextServiceI.get(carText.id)?: throw Exception("对应数据不存在")
        carText.init(old)
        carTextServiceI.update(carText, old)
        return """{"status":"success","message":""}"""
    }
}