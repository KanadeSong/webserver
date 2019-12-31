package com.seater.smartmining.entity

import java.util.*
import javax.persistence.*


@Entity
data class CarText(
        @Id
        @GeneratedValue
        var id: Long = 0,

        @Column(columnDefinition = "varchar(255) COMMENT'名字'")
        var name: String? = null,

        @Column(columnDefinition = "datetime COMMENT'创建时间'")
        var createTime: Date? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'项目id'")
        var projectId: Long? = null,

        @Column(columnDefinition = "bigint(20) COMMENT'序号'")
        var orderNumber: Long? = null,

        @Column(columnDefinition = "text COMMENT'辅助任务内容'")
        var message: String? = null
)

fun CarText.init(t: CarText){
        this.name = this.name?: t.name?: ""
        this.createTime = t.createTime?: this.createTime?: Date()
        this.projectId = t.projectId?: this.projectId?: 0L
        this.message = this.message?: t.message?: ""
        this.orderNumber = this.orderNumber?: t.orderNumber?: 0L
}