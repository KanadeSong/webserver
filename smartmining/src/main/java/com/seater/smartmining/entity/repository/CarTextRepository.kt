package com.seater.smartmining.entity.repository

import com.seater.smartmining.entity.CarText
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface CarTextRepository : JpaRepository<CarText, Long>, JpaSpecificationExecutor<CarText> {

    @Query("select max(orderNumber) from CarText")
    fun getMaxOrderNumber(): Long?

}
