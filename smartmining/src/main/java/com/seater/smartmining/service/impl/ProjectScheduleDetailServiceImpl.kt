package com.seater.smartmining.service.impl


import com.seater.smartmining.dao.ProjectScheduleDetailDaoI
import com.seater.smartmining.entity.*
import com.seater.smartmining.entity.repository.*
import com.seater.smartmining.enums.*
import com.seater.smartmining.service.*
import com.seater.smartmining.utils.schedule.*
import com.systech.helpers.jsonStringToObject
import com.systech.helpers.redis.jsonGet
import com.systech.helpers.toJsonString
import com.sytech.user.exception.SytechLockFailException
import com.sytech.user.exception.SytechNoFindIdException
import com.sytech.user.filterEntity.QueryFlagFilter
import com.sytech.user.helpers.redisLockDefaultTimeout
import khronos.toString
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.querydsl.QPageRequest
import org.springframework.data.querydsl.QSort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Service
@javax.transaction.Transactional(rollbackOn = [Exception::class])
class ProjectScheduleDetailServiceImpl(
        val applicationContext: ApplicationContext,
        val projectScheduleDetailDaoI: ProjectScheduleDetailDaoI,
        val em: EntityManager,
        val projectScheduleServiceI: ProjectScheduleServiceI,
        val scheduleCarServiceI: ScheduleCarServiceI,
        val scheduleMachineServiceI: ScheduleMachineServiceI,
        val projectDiggingMachineEfficiencyServiceI: ProjectDiggingMachineEfficiencyServiceI,
        val projectServiceI: ProjectServiceI,
        val projectCarServiceI: ProjectCarServiceI,
        val projectSlagSiteServiceI: ProjectSlagSiteServiceI,
        val carOrderServiceI: CarOrderServiceI,
        val projectCarRepository: ProjectCarRepository,
        val projectDiggingMachineRepository: ProjectDiggingMachineRepository,
        val projectDiggingMachineEfficiencyRepository: ProjectDiggingMachineEfficiencyRepository,
        val carOrderRepository: CarOrderRepository,
        val projectScheduleRepository: ProjectScheduleRepository,
        val stringRedisTemplate: StringRedisTemplate,
        val projectDeviceRepository: ProjectDeviceRepository
) : ProjectScheduleDetailServiceI {
    val log = LoggerFactory.getLogger(ProjectScheduleDetailServiceImpl::class.java)
    val entityName = "ProjectScheduleDetail"

    val updateLock = false
    var redisLockTimeout = redisLockDefaultTimeout
    val keyGroup = "lock:projectScheduleDetail:"
    fun getKey(id: Long) = "${keyGroup}${id}"

    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    fun getIsAutoKey(projectCarUid: String) = "autocarlock:projectcaruid:${projectCarUid}"

    override fun isAuto(carUid: String): Boolean {
        val key = getIsAutoKey(carUid)
        val isAutoFlag: Boolean? = valueOps.jsonGet(key)
        return isAutoFlag?: false
    }

    override fun get(id: Long?): ProjectScheduleDetail? {
        if (id == null) return null
        return projectScheduleDetailDaoI.get(id)
    }

    override fun getUsed(id: Long?): ProjectScheduleDetail? {
        if (id == null) return null
        val projectScheduleDetail = projectScheduleDetailDaoI.get(id) ?: return null

        return projectScheduleDetail
    }

    override fun save(projectScheduleDetail: ProjectScheduleDetail): ProjectScheduleDetail {
        return projectScheduleDetailDaoI.save(projectScheduleDetail.apply {
            if(this.carUid.isNullOrBlank()){
                if(this.carCode.isNullOrBlank()){
                    val car = projectCarRepository.getById(this.projectCarId).firstOrNull()
                    this.carCode = car?.code?: ""
                }
                if(!this.carCode.isNullOrBlank()) {
                    val projectDevice = projectDeviceRepository.findByProjectIdAndCodeAndDeviceType(this.projectId, this.carCode, ProjectDeviceType.SlagTruckDevice).filter { it.vaild }.firstOrNull()
                    this.carUid = projectDevice?.uid?: ""
                    this.carCode = projectDevice?.code?: ""
                }
            }
            if(this.projectDiggingMachineId > 0L) {
                val dig = projectDiggingMachineRepository.getById(this.projectDiggingMachineId).firstOrNull()
                if(dig != null){
                    this.diggingMachineCode = dig.code
                }
            }
            if(this.disable){
                this.carOrderId = 0L
                this.diggingMachineId = 0L
                this.projectSlagSiteId = 0L
                this.projectDiggingMachineId = 0L
            }
        })
    }

    override fun delete(id: Long) {
        projectScheduleDetailDaoI.delete(id = id)
    }

    override fun delete(ids: List<Long>) {
        projectScheduleDetailDaoI.delete(ids = ids)
    }



    override fun query(spec: Specification<ProjectScheduleDetail>?, pageable: Pageable): Page<ProjectScheduleDetail> {
        return projectScheduleDetailDaoI.query(spec = spec, pageable = pageable)
    }

    /**
     *
     */

    override fun queryPage(
            current: Int?,
            pageSize: Int?,
            groupCode: String?,
            projectId: Long?,
            disable: Boolean?,
            showReady: Boolean?,
            managerId: Long?
    ): Page<ProjectScheduleDetail> {

        val minPageSize = 10
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<ProjectScheduleDetail> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (!groupCode.isNullOrBlank()) {
                ls.add(cb.equal(root.get<String>("groupCode"), groupCode))
            }

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            if (disable != null) {
                ls.add(cb.equal(root.get<Boolean>("disable"), disable))
            }

            if (showReady != null && showReady == false) {
                ls.add(cb.notEqual(root.get<ProjectDeviceType>("jobsts"), FixJobStatus.Ready))
            }

            if(managerId != null && projectId != null){
                val ps = projectScheduleRepository.getAllByProjectIdAndManagerId(projectId, "%\"${managerId}\"%", 0, 1000)
                val t = cb.`in`(root.get<String>("groupCode"))
                ps.forEach {
                    t.value(it.groupCode)
                }
                ls.add(t)
            }

            //ls.add(cb.equal(root.get<Boolean>("disable"), false))

            cb.and(*ls.toTypedArray())
        }

        return query(pageable = PageRequest.of(cur, page), spec = spec)
    }

    override fun findByProjectCarIdAndProjectIdOrderByIdDesc(projectCarId: Long, projectId: Long): List<ProjectScheduleDetail> {
        val c = projectScheduleDetailDaoI.getByProjectCarIdAndProjectId(projectCarId, projectId)
        if(c == null) return listOf()
        else return listOf(c)
    }

    override fun reset(groupCode: String, projectId: Long): List<ProjectScheduleDetail>? {
        val project = projectServiceI.get(projectId)?: return null
        val schedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)?: return null
        if(schedule.dispatchMode != ProjectDispatchMode.Auto) return null
        val rt = queryPage(
                current = null,
                pageSize = 100000,
                groupCode = groupCode,
                projectId = projectId,
                disable = false,
                showReady = null,
                managerId = null
        ).content.toMutableList()
        val digIdList = rt.map { it.projectDiggingMachineId }.toSet()
        val carIdList = rt.map { it.projectCarId }.toSet()
        val projectSchedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
        val scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
        val scheduleMachineList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
        val scheduleMachineIdList = scheduleMachineList.map { it.machineId }
        val scheduleCarIdList = scheduleCarList.map { it.carId }
        val restScheduleCarList = scheduleCarList.filter { !carIdList.contains(it.carId) }.toMutableList()
        val projectCarAll = projectCarServiceI.all

        rt.filter { !scheduleMachineIdList.contains(it.projectDiggingMachineId) || !scheduleCarIdList.contains(it.projectCarId) }.forEach {
            delete(it.id)
            rt.remove(it)
        }

        val pdmeList = projectDiggingMachineEfficiencyServiceI.queryPage(
                current = null,
                pageSize = 10000,
                projectDiggingMachineId = null,
                groupCode = groupCode,
                projectId = projectId
        ).content.toMutableList()
        val rpdmeList = pdmeList.filter { it.efficiency != null && it.efficiency!! > BigDecimal.ZERO }
        val avg = if(rpdmeList.size > 0) rpdmeList.sumByDouble { it.efficiency!!.toDouble() } / rpdmeList.size else 1.0
        val eList = scheduleMachineList.map {
            val mid = it.machineId
            val t = rpdmeList.filter { it.projectDiggingMachineId == mid }.firstOrNull()
            if(t == null)
                ProjectDiggingMachineEfficiency().apply {
                    this.projectDiggingMachineId = mid
                    this.machineCode = it.machineCode
                    this.projectId = projectId
                    this.efficiency = avg.toBigDecimal()
                }
            else t
        }.sortedBy { it.efficiency }

        val allE = eList.sumByDouble { it.efficiency!!.toDouble() }.toBigDecimal()
        val allNum = scheduleCarList.size
        var restNum = allNum
        val digAllotmentList = eList.map {
            val digId = it.projectDiggingMachineId
            val dig = scheduleMachineList.filter { it.machineId == digId }.firstOrNull()
            var num = ((it.efficiency!! * allNum.toBigDecimal()) / allE).toInt()
            if(num < 1) num = 1
            if(num > restNum) num = restNum
            restNum -= num
            DigAllotment().apply {
                this.digId = it.projectDiggingMachineId
                this.num = num
                this.restNum = num
                this.projectId = projectId

                this.diggingMachineCode = it.machineCode
                this.diggingMachineId = dig?.machineId?: 0L
                this.brandId = dig?.diggingMachineBrandId?: 0L
                this.brandName = dig?.diggingMachineBrandName?: ""
                this.modelId = dig?.diggingMachineModelId?: 0L
                this.modelName = dig?.diggingMachineModelName?: ""
                this.placeId = schedule.placeId?: 0L
                this.placeName = schedule.placeName?: ""
                this.materialId = dig?.materialId?: 0L
                this.materiaName = dig?.materialName?: ""
                this.distance = dig?.distance?: 0L
                this.pricingType = dig?.pricingType?: PricingTypeEnums.Unknow
            }
        }
        digAllotmentList.forEachIndexed { index, digAllotment ->
            if((digAllotmentList.size - index) <= restNum){
                val num = restNum / (digAllotmentList.size - index)
                digAllotment.apply {
                    this.num += num
                    this.restNum += num
                }
                restNum -= num
            }
        }

        val restList = mutableListOf<ProjectScheduleDetail>()
        rt.forEach {
            val digId = it.projectDiggingMachineId
            val digAllotment = digAllotmentList.filter { it.digId == digId }.firstOrNull()
            if(digAllotment != null){
                digAllotment.apply {
                    this.restNum --
                    if(this.restNum < 0){
                        rt.remove(it)
                        restList.add(it)
                    }
                }
            }
            else{
                delete(it.id)
                rt.remove(it)
            }
        }
        digAllotmentList.filter { it.restNum > 0 }.forEach {
            while(it.restNum > 0){
                val digId = it.digId
                val dig = scheduleMachineList.filter { it.machineId == digId }.firstOrNull()
                if(restList.size > 0){
                    val psd = restList.get(0)
                    val r = save(
                            psd.apply {
                                this.projectDiggingMachineId = it.digId
                                this.projectId = projectId
                                this.groupCode = groupCode

                                this.diggingMachineCode = it.diggingMachineCode
                                this.diggingMachineId = dig?.machineId?: 0L
                                this.brandId = dig?.diggingMachineBrandId?: 0L
                                this.brandName = dig?.diggingMachineBrandName?: ""
                                this.modelId = dig?.diggingMachineModelId?: 0L
                                this.modelName = dig?.diggingMachineModelName?: ""
                                this.placeId = schedule.placeId?: 0L
                                this.placeName = schedule.placeName?: ""
                                this.materialId = dig?.materialId?: 0L
                                this.materiaName = dig?.materialName?: ""
                                this.distance = dig?.distance?: 0L
                                this.pricingType = dig?.pricingType?: PricingTypeEnums.Unknow
                            }
                    )
                    rt.add(r)
                    restList.removeAt(0)
                    it.restNum --
                }
                else if(restScheduleCarList.size > 0){
                    val restCar = restScheduleCarList.get(0)
                    val carId = restCar.carId
                    val car = scheduleCarList.filter { it.carId == carId }.firstOrNull()
                    val projectCar = projectCarAll.filter { it.id == (car?.carId?: 0L) }.firstOrNull()
                    val r = save(
                            ProjectScheduleDetail().apply {
                                this.projectDiggingMachineId = it.digId
                                this.projectCarId = carId
                                this.carUid = projectCar?.uid?: ""
                                this.carCode = projectCar?.code?: ""
                                this.projectId = projectId
                                this.groupCode = groupCode

                                this.diggingMachineCode = it.diggingMachineCode
                                this.diggingMachineId = dig?.machineId?: 0L
                                this.brandId = dig?.diggingMachineBrandId?: 0L
                                this.brandName = dig?.diggingMachineBrandName?: ""
                                this.modelId = dig?.diggingMachineModelId?: 0L
                                this.modelName = dig?.diggingMachineModelName?: ""
                                this.placeId = schedule.placeId?: 0L
                                this.placeName = schedule.placeName?: ""
                                this.materialId = dig?.materialId?: 0L
                                this.materiaName = dig?.materialName?: ""
                                this.distance = dig?.distance?: 0L
                                this.pricingType = dig?.pricingType?: PricingTypeEnums.Unknow

                                //this.carCode = car?.carCode?: ""
                                this.carBrandId = car?.carBrandId?: 0L
                                this.carBrandName = car?.carBrandName?: ""
                                this.carModelId = car?.carModelId?: 0L
                                this.carModelName = car?.carModelName?: ""
                            }
                    )
                    rt.add(r)
                    restScheduleCarList.removeAt(0)
                    it.restNum --
                }
                else it.restNum = 0
            }
        }

        return rt.toList()
    }

    override fun clean(): Boolean {
        var spec = Specification<ProjectScheduleDetail> { root, query, cb ->
            var ls = mutableListOf<Predicate>()
            val digSub = query.subquery(String::class.java)
            val digRoot = digSub.from(ProjectSchedule::class.java)
            digSub.select(digRoot.get<String>("groupCode"))
            ls.add(cb.`in`(root.get<String>("groupCode")).value(digSub).not())

            cb.and(*ls.toTypedArray())
        }

        val rt = query(pageable = PageRequest.of(0, 10000), spec = spec)
        rt.content.forEach {
            delete(it.id)
        }
        return true
    }

    override fun initByWorkInfo(projectCarWorkInfo: ProjectCarWorkInfo) {
        //println("---------------------------------------------------initByWorkInfo------------------------------------------------${projectCarWorkInfo.toJsonString()}")
        val r = projectCarWorkInfo
        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(r.carId!!, r.getProjectId()!!)
        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()?: ProjectScheduleDetail(projectId = r.projectId, projectCarId = r.carId)
        var isUpdate = (r.id != projectScheduleDetail.projectCarWorkInfoId)
        val rt = save(projectScheduleDetail.apply {
            if(this.projectCarWorkInfoStatus != r.status || isUpdate) {
                this.autoScheduleType = when (r.status) {
                    ProjectCarWorkStatus.WaitLoadCheckUp, ProjectCarWorkStatus.WaitLoadUp -> AutoScheduleType.WaitForDiggingMachineSchedule
                    ProjectCarWorkStatus.WaitCheckUp -> AutoScheduleType.WaitForSlagSiteSchedule
                    else -> AutoScheduleType.WaitForDiggingMachineSchedule
                }
                this.projectId = projectCarWorkInfo.projectId
                this.projectCarWorkInfoStatus = r.status
                this.carOrderId = 0L
                this.projectCarWorkInfoId = r.id
                isUpdate = true
            }
            this.disable = false
//            if((projectScheduleDetail.timeLoad?: Date(0)) < r.timeLoad) {
//                this.timeLoad = r.timeLoad
//                this.carOrderId = 0L
//            }
            if((projectScheduleDetail.timeLoad) < (r.timeLoad?: projectScheduleDetail.timeLoad)) {
                isUpdate = true
                this.timeLoad = r.timeLoad ?: this.timeLoad
            }
        })
        if(isUpdate) initStatus(rt)
    }

    override fun initByCar(projectCar: ProjectCar) {
        if(projectCar.code == "0863") println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByCar------------------------------------------------${projectCar.toJsonString()}")
        val r = projectCar
        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(r.id!!, r.getProjectId()!!)
        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()?: ProjectScheduleDetail(projectId = r.projectId, projectCarId = r.id)
        var isUpdate = false
        val rt = save(projectScheduleDetail.apply {
            if (!r.vaild || r.status != ProjectCarStatus.Working) {
                if (!this.disable) {
                    this.autoScheduleType = AutoScheduleType.Unknow
                    this.disable = true
                    //this.carOrderId = 0L
                }
            }
            else if(r.vaild && r.status == ProjectCarStatus.Working){
                if (this.disable) {
                    this.autoScheduleType = AutoScheduleType.Unknow
                    this.disable = false
                    this.carOrderId = 0L

                    this.diggingMachineId = 0L
                    this.diggingMachineCode = ""
                    this.projectDiggingMachineId = 0L
                    this.groupCode = ""
                    this.projectSlagSiteName = ""
                    this.projectSlagSiteId = 0L
                    this.jobsts = FixJobStatus.Ready

                    this.workTimeNum = (this.workTimeNum?: 0) + 1

                    isUpdate = true
                }
            }
        })
        //if(isUpdate) initStatus(rt)
    }

    data class FixMap(
            var pktID: Int = 0,
            var projectID: Long = 0L,
            var devCode: String = "",
            var devID: Long = 0L,
            var latitude: BigDecimal = BigDecimal.ZERO,
            var longitude: BigDecimal = BigDecimal.ZERO,
            var altitude: BigDecimal = BigDecimal.ZERO,
            var distance: BigDecimal? = null,
            var tload: Date = Date(0),
            var jobsts: FixJobStatus = FixJobStatus.Unknow,
            var errorcur: Int = 0,
            var errormax: Int = 0,
            var iccount: Int = 0,
            var slagSiteID: String = "",
            var slagDist: String = "",
            var slagSiteIDList: List<Long> = listOf(),
            var slagDistList: List<BigDecimal> = listOf(),
            var car: ProjectCar = ProjectCar(),
            var dig: ProjectDiggingMachine? = null,
            var machineId: Long = 0L,
            var scheduleCar: ScheduleCar? = null,
            var scheduleAuto: ProjectSchedule? = null,
            var device: ProjectDevice? = null
    )

    fun FixMap.init(carUid: String, machineId: Long){
        this.machineId = machineId
        this.slagSiteIDList = this.slagSiteID.split(",").map {
            val idStr = it.trim()
            if(idStr.isNullOrBlank()) 0L
            else idStr.toLong()
        }
        this.slagDistList = this.slagDist.split(",").map {
            val distStr = it.trim()
            if(distStr.isNullOrBlank()) BigDecimal.ZERO
            else distStr.toBigDecimal()
        }
        this.car = projectCarRepository.findByProjectIdAndUidAndIsVaild(this.projectID, carUid, true).firstOrNull()?: ProjectCar()
        this.dig = projectDiggingMachineRepository.getById(machineId).filter { it.status == DiggingMachineStatus.Working && (it.stopStatus == DiggingMachineStopStatus.Normal || it.stopStatus == DiggingMachineStopStatus.PAUSE) }.firstOrNull()
        this.scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(this.projectID, car.id, true).filter { !it.fault }.firstOrNull()
        this.scheduleAuto = projectScheduleRepository.findByProjectIdAndGroupCodeAndDispatchMode(this.projectID, this.scheduleCar?.groupCode?: "", ProjectDispatchMode.Auto).sortedByDescending { it.id }.firstOrNull()
        this.device = projectDeviceRepository.findByProjectIdAndCodeAndDeviceType(car.projectId, car.code, ProjectDeviceType.SlagTruckDevice).firstOrNull()
        val realDigDistance = this.distance
        if(realDigDistance != null){
            if(realDigDistance <= BigDecimal.ZERO) this.distance = null
            else if(realDigDistance > BigDecimal(100)) this.distance = null
        }
    }

    fun FixMap.check(): Boolean {
        if(this.car.id <= 0L) return false
        if(this.projectID <= 0L) return false
        //if(this.jobsts == FixJobStatus.Unknow) return false
        return true
    }

    override fun initByFix(carUid: String, machineId: String, payload: String, isNear: Boolean) {
        if(payload.contains("0863")) println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMap0------------------------------------------------${carUid}: ${machineId}/")
        val fixMap: FixMap = payload.jsonStringToObject()?: return
        fixMap.init(carUid, machineId.toLong())
        if(fixMap.car.code == "0863") println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMap------------------------------------------------${fixMap}: ${fixMap.car.status}")
        if(!fixMap.check()) return

        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(fixMap.car.id, fixMap.projectID)
        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()?: ProjectScheduleDetail(projectId = fixMap.projectID, projectCarId = fixMap.car.id)
        if(fixMap.tload.time <= 0) fixMap.tload = projectScheduleDetail.timeLoad
        if(projectScheduleDetail.timeLoad.time > fixMap.tload.time) return

        var isUpdate = false
        val digFix = projectDiggingMachineRepository.getById(projectScheduleDetail.projectDiggingMachineId)
        //val oldStopDig = digFix.filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.PAUSE }.firstOrNull()
        val oldDig = digFix.filter { it.status == DiggingMachineStatus.Working && (it.stopStatus == DiggingMachineStopStatus.Normal || it.stopStatus == DiggingMachineStopStatus.PAUSE) }.firstOrNull()

        save(projectScheduleDetail.apply {
            this.realDigId = fixMap.machineId
            this.realDigDistance = fixMap.distance?: this.realDigDistance
            this.realSiteIdList = fixMap.slagSiteID
            this.realSiteDistanceList = fixMap.slagDist
            if(this.autoScheduleType != AutoScheduleType.WaitForDiggingMachineSchedule && this.autoScheduleType != AutoScheduleType.DiggingMachine) this.nearDig = false

            this.groupCode = fixMap.scheduleCar?.groupCode?: this.groupCode
            this.carUid = fixMap.device?.uid?: this.carUid

            this.offlineIs = false

            if(!(fixMap.dig?.code?: "").isNullOrBlank()) this.diggingMachineCode = fixMap.dig?.code?: this.diggingMachineCode
            if(!(fixMap.car?.code?: "").isNullOrBlank()) this.carCode = fixMap.car?.code?: this.carCode

            if(isNear){
//                if((this.realDigDistance?: defaultDistance) < nearDistance){
//                    if(!(this.nearDig?: false)) this.nearDigTime = Date()
//                    //this.leaveDigTime = null
//                }
                if(!(this.nearDig?: false)) this.nearDigTime = Date()
                this.nearDig = true
                this.secondAuto = true
            }
            else {
//                if ((this.realDigDistance ?: defaultDistance) < nearDistance) {
//                    if (!(this.nearDig ?: false)) this.nearDigTime = Date()
//                    //this.leaveDigTime = null
//                    //this.nearDig = true
//                } else {
//                    //this.nearDigTime = null
//                    if (this.nearDig ?: false) this.leaveDigTime = Date()
//                    this.nearDig = false
//                }
                if (this.nearDig ?: false) this.leaveDigTime = Date()
                this.nearDig = false
            }
            if(this.autoScheduleType == AutoScheduleType.DiggingMachine){
                if((fixMap.dig == null || oldDig == null) && this.carOrderId > 0L){
                    this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                    this.carOrderId = 0L
                    this.projectDiggingMachineId = 0L
                    this.changeTime = Date()
                    isUpdate = true
                    //if(this.realDigDistance != null && this.realDigDistance!! > BigDecimal.ZERO && this.realDigDistance!! < BigDecimal(0.2) && !(this.nearDig?: false)) this.secondAuto = true
                    this.reAuto = true
                    //println("--------------------------------------------------check------------------------------------------${fixMap}")
                }
            }
            else if(this.autoScheduleType == AutoScheduleType.WaitForSlagSiteSchedule || this.autoScheduleType == AutoScheduleType.SlagSite){
                this.reAuto = false
                this.secondAuto = false
            }
        })
        val disable = !((fixMap.car.vaild && fixMap.car.status == ProjectCarStatus.Working && fixMap.scheduleCar != null && fixMap.scheduleAuto != null))
        if(disable) {
            if(projectScheduleDetail.disable != disable){
                save(projectScheduleDetail.apply {
                    this.disable = disable
                })
            }
            return
        }
        else {
            projectScheduleDetail.apply {
                this.disable = disable
                this.groupCode = fixMap.scheduleCar?.groupCode?: ""
            }
        }
        //println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMap2------------------------------------------------${projectScheduleDetail}")

        val digSet = listOf(FixJobStatus.Unknow, FixJobStatus.SlagSite)
        val siteSet = listOf(FixJobStatus.Loaded, FixJobStatus.Checked)
        val rt = save(projectScheduleDetail.apply {
            if(this.timeLoad.time < fixMap.tload.time) {

                isUpdate = true
                this.timeLoad = fixMap.tload
                when(fixMap.jobsts){
                    FixJobStatus.Loaded, FixJobStatus.Checked -> {
                        this.autoScheduleType = AutoScheduleType.WaitForSlagSiteSchedule
//                        this.projectSlagSiteId = 0L
//                        this.projectSlagSiteName = ""
                        val dig = fixMap.dig
                        this.projectDiggingMachineId = dig?.id?: 0L
                        this.diggingMachineCode = dig?.code?: ""
                        this.diggingMachineId = dig?.diggingMachineId?: 0L
                    }
                    FixJobStatus.SlagSite, FixJobStatus.Unknow -> {
                        this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                    }
                    else -> return
                }
            }
            else{
                //println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMapCar4------------------------------------------------${this.jobsts}: ${fixMap.jobsts}")

                if(digSet.contains(this.jobsts) && digSet.contains(fixMap.jobsts) && this.carOrderId > 0) return
                if(siteSet.contains(this.jobsts) && siteSet.contains(fixMap.jobsts) && this.carOrderId > 0) return

                //println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMapCar5------------------------------------------------${fixMap.dig.toJsonString()}")
                when(fixMap.jobsts){
                    FixJobStatus.Loaded, FixJobStatus.Checked -> {
                        this.autoScheduleType = AutoScheduleType.WaitForSlagSiteSchedule
//                        this.projectSlagSiteId = 0L
//                        this.projectSlagSiteName = ""
                        val dig = fixMap.dig
                        this.projectDiggingMachineId = dig?.id?: 0L
                        this.diggingMachineCode = dig?.code?: ""
                        this.diggingMachineId = dig?.diggingMachineId?: 0L
                        isUpdate = true
                    }
                    FixJobStatus.SlagSite, FixJobStatus.Unknow -> {
                        this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                        isUpdate = true
                    }
                    else -> return
                }
            }
            this.jobsts = fixMap.jobsts

            if(isUpdate) {
                this.carOrderId = 0L
                this.changeTime = Date()
            }
        })

        if(isUpdate) {
            //println("--------------------------------------------------check-------------------------1-----------------${rt}")
            initStatus(rt)
        }
    }

    override fun initByFixLoad(carUid: String, machineId: String, payload: String) {
        val fixMap: FixMap = payload.jsonStringToObject()?: return
        fixMap.init(carUid, machineId.toLong())
        //println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++initByFixMap------------------------------------------------${fixMap}: ${fixMap.car.status}")
        if(!fixMap.check()) return

        var isUpdate = false

        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(fixMap.car.id, fixMap.projectID)
        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()?: ProjectScheduleDetail(projectId = fixMap.projectID, projectCarId = fixMap.car.id)
        if(fixMap.tload.time <= 0) fixMap.tload = projectScheduleDetail.timeLoad
        if(projectScheduleDetail.timeLoad.time > fixMap.tload.time) return
        val rt = save(projectScheduleDetail.apply {
            this.realDigId = fixMap.machineId
            this.realDigDistance = fixMap.distance
            this.realSiteIdList = fixMap.slagSiteID
            this.realSiteDistanceList = fixMap.slagDist
            //if(this.projectDiggingMachineId == fixMap.machineId) {

            if((this.realDigDistance?: defaultDistance) < nearDistance){
                if(!(this.nearDig?: false)) this.nearDigTime = Date()
                //this.leaveDigTime = null
            }
            this.nearDig = true
            //}

            if(this.autoScheduleType == AutoScheduleType.DiggingMachine){
                if(fixMap.dig == null && this.carOrderId > 0L){
                    this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                    this.carOrderId = 0L
                    this.projectDiggingMachineId = 0L
                    this.changeTime = Date()
                    isUpdate = true
                    this.reAuto = true
                }
            }
            else if(this.autoScheduleType == AutoScheduleType.SlagSite){
                this.reAuto = false
            }
        })

        if(isUpdate) {
            initStatus(rt)
        }
    }

    fun findByProjectCarIdInAndProjectIdAndDisableIsFalse(carIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail>{
        return carIdList.map{
            projectScheduleDetailDaoI.getByProjectCarIdAndProjectId(it, projectId)?: ProjectScheduleDetail()
        }.filter { it.id > 0L && it.disable == false }
    }

    fun findByProjectDiggingMachineIdInAndProjectIdAndDisableIsFalse(projectDigIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail> {
        return projectScheduleDetailDaoI.findByProjectDigIdInAndProjectId(projectDigIdList, projectId).filter { it.disable == false }
    }

    fun findByProjectSlagSiteIdInAndProjectIdAndDisableIsFalse(projectSiteIdList: List<Long>, projectId: Long): List<ProjectScheduleDetail> {
        return projectScheduleDetailDaoI.findByProjectSiteIdInAndProjectId(projectSiteIdList, projectId).filter { it.disable == false }
    }

    fun initStatus(projectScheduleDetail: ProjectScheduleDetail){
        val carId = projectScheduleDetail.projectCarId
        val projectId = projectScheduleDetail.projectId
        if(projectId <= 0) return
        val project = projectServiceI.get(projectId)
        val date = Date()
        val ymd = date.toString("yyyy-MM-dd")
        val hsm1 = project.earlyStartTime?.toString("HH:mm:ss")?: "00:00:00"
        val hsm2 = project.nightStartTime?.toString("HH:mm:ss")?: "00:00:00"
        val startTime1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ymd + " " + hsm1)
        val startTime2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ymd + " " + hsm2)
        var startTime = Date(0)
        if(date <= Date(startTime1.time + 1000*60*30)) startTime = startTime1
        else if(date <= Date(startTime2.time + 1000*60*30)) startTime = startTime2
        val sc = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(projectId, carId, true).firstOrNull()?: return
        val groupCode = sc.groupCode
        val schedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
        val scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode).filter{
            (it.isVaild?: false) && !(it.fault?: false)
        }
        val scheduleCarIdList = scheduleCarList.map { it.carId }
        val carList = projectCarServiceI.getByProjectIdAndIsVaild(projectId, true).filter { scheduleCarIdList.contains(it.id) }
        val projectDigAll = projectDiggingMachineRepository.getAllByProjectIdAndIsVaildAndSelected(projectId, true).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }
        val projectDigIdAll = projectDigAll.map { it.id }
        val slagSiteIdList = schedule.slagSiteId.replace("\"", "").replace("[", "").replace("]", "").split(",").map { it.toLong() }
        val digList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode).filter{ it.isVaild && !it.fault && projectDigIdAll.contains(it.machineId) }

        val car = carList.filter { it.id == carId }.firstOrNull()
        if(digList.size <= 0 && (projectScheduleDetail.autoScheduleType == AutoScheduleType.WaitForDiggingMachineSchedule || projectScheduleDetail.autoScheduleType == AutoScheduleType.DiggingMachine)) {
            println("暂无可分配挖机")
            save(projectScheduleDetail.apply {
                this.autoScheduleType = AutoScheduleType.DiggingMachine
                this.updateTime = Date()
                this.projectDiggingMachineId = 0L
                this.diggingMachineCode = ""
                this.carCode = car?.code?: ""
                this.carUid = car?.uid?: ""
                this.disable = (schedule.dispatchMode != ProjectDispatchMode.Auto)
                this.projectId = projectId
                this.offlineIs = false
            })
            return
        }
        //println("-------------autoScheduleForDiggingMachine---digList--${digList}")

        val effs = projectDiggingMachineEfficiencyRepository.findAll()

        val carOrderList = carOrderRepository.findByCarIdAndCarOrderState(carId, CarOrderState.Ready)
        val carOrderTemp = carOrderList.filter { it.carOrderType == CarOrderType.Temp }.sortedByDescending { it.id }.firstOrNull()
        val carOrderFix = carOrderList.filter { it.carOrderType == CarOrderType.Fix }.sortedByDescending { it.id }.firstOrNull()
        var digFix = if(carOrderTemp != null && (carOrderTemp.fixDig?: false)) carOrderTemp.diggingMachineId else null
        var siteFix = if(carOrderTemp != null && (carOrderTemp.fixSite?: false)) carOrderTemp.slagSiteId else null
        digFix = (if(carOrderFix != null && (carOrderFix.fixDig?: false)) carOrderFix.diggingMachineId else null)?: digFix
        siteFix = (if(carOrderFix != null && (carOrderFix.fixSite?: false)) carOrderFix.slagSiteId else null)?: siteFix
        if(carOrderFix != null && carOrderFix.sendNum > 0){
            carOrderServiceI.add(carOrderFix.copy().apply {
                this.id = 0L
                //this.orderTime = Date()
                this.rid = Date().time % 1000
                this.sendTime = null
                //this.sendNum ++

                this.carJobStatus = projectScheduleDetail?.jobsts?: FixJobStatus.Unknow
                this.realDigId = projectScheduleDetail?.realDigId
                this.realDigDistance = projectScheduleDetail?.realDigDistance
                this.realSiteIdList = projectScheduleDetail?.realSiteIdList
                this.realSiteDistanceList = projectScheduleDetail?.realSiteDistanceList
                this.groupCode = projectScheduleDetail?.groupCode
            })
            carOrderServiceI.save(carOrderFix.apply {
                this.carOrderState = CarOrderState.End
                //this.success = true
            })
        }

        when(projectScheduleDetail.autoScheduleType){
            AutoScheduleType.WaitForDiggingMachineSchedule -> {
                val scheduleDetailList = findByProjectCarIdInAndProjectIdAndDisableIsFalse(carList.map { it.id }, projectId)
                val scheduleDetailDigList = findByProjectDiggingMachineIdInAndProjectIdAndDisableIsFalse(digList.map { it.machineId }, projectId)
                var isInit = false
                scheduleDetailList.filter { it.projectCarId == carId }.forEach {
                    if(!isInit) {
                        it.apply {
                            this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
                        }
                        isInit = true
                    }
                    else {
                        it.apply {
                            this.autoScheduleType = AutoScheduleType.Unknow
                        }
                    }
                }
                val digAutoInfo = digList.map {
                    ProjectDiggingMachineScheduleInfo().apply {
                        this.groupCode = groupCode
                        this.projectDiggingMachineId = it.machineId
                        //val projectDig = projectDigAll.filter { it.id == this.projectDiggingMachineId }.firstOrNull()
                        val eff = effs.filter { it.projectDiggingMachineId == this.projectDiggingMachineId }.firstOrNull()
                        this.distance = it.distance  //100
                        //this.intervalTimeLong = (projectDig?.intervalTime?: 2) * 1000 * 60L
                        this.intervalTimeLong = if(eff == null || eff.lastHourCarNum <= 0) 10*1000*60L else (1000*60*60L)/eff.lastHourCarNum
                        this.projectId = projectId
                        //println("---------------------autoScheduleForDiggingMachine---scheduleDetailDigList--${scheduleDetailDigList}")
                        val realScheduleDigList = scheduleDetailDigList.filter { it.projectDiggingMachineId == this.projectDiggingMachineId && it.autoScheduleType == AutoScheduleType.DiggingMachine }
                        //println("---------------------autoScheduleForDiggingMachine-----------realScheduleDigList----------${realScheduleDigList}")
                        realScheduleDigList.forEach {
                            if(this.lastScheduleTime < it.carOrderTime) this.lastScheduleTime = it.carOrderTime
                            if(this.firstScheduleTime == null) this.firstScheduleTime = it.carOrderTime
                            if(this.firstScheduleTime != null && this.firstScheduleTime!! > it.carOrderTime) this.firstScheduleTime = it.carOrderTime
                            this.waitNum = (this.waitNum?: 0) + 1
                        }
                    }
                }.toArrayList()

//                val projectCarScheduleInfoList = autoScheduleForDiggingMachine(AutoScheduleInfo().apply {
//                    this.workScheduleInfo = WorkScheduleInfo().apply {
//
//                        this.waitTimeLong = 1000 * 60 * 5L
//                        this.startTime = startTime //Date(0)
//                        this.groupCode = groupCode
//                        this.projectId = projectId
//                    }
//                    this.projectDiggingMachineScheduleInfoList = digList.map {
//                        ProjectDiggingMachineScheduleInfo().apply {
//                            this.groupCode = groupCode
//                            this.projectDiggingMachineId = it.machineId
//                            //val projectDig = projectDigAll.filter { it.id == this.projectDiggingMachineId }.firstOrNull()
//                            val eff = effs.filter { it.projectDiggingMachineId == this.projectDiggingMachineId }.firstOrNull()
//                            this.distance = it.distance  //100
//                            //this.intervalTimeLong = (projectDig?.intervalTime?: 2) * 1000 * 60L
//                            this.intervalTimeLong = if(eff == null || eff.lastHourCarNum <= 0) 10*1000*60L else (1000*60*60L)/eff.lastHourCarNum
//                            this.projectId = projectId
//                            println("---------------------autoScheduleForDiggingMachine---scheduleDetailDigList--${scheduleDetailDigList}")
//                            val realScheduleDigList = scheduleDetailDigList.filter { it.projectDiggingMachineId == this.projectDiggingMachineId && it.autoScheduleType == AutoScheduleType.DiggingMachine }
//                            println("---------------------autoScheduleForDiggingMachine-----------realScheduleDigList----------${realScheduleDigList}")
//                            realScheduleDigList.forEach {
//                                if(this.lastScheduleTime < it.carOrderTime) this.lastScheduleTime = it.carOrderTime
//                                if(this.firstScheduleTime == null) this.firstScheduleTime = it.carOrderTime
//                                if(this.firstScheduleTime != null && this.firstScheduleTime!! > it.carOrderTime) this.firstScheduleTime = it.carOrderTime
//                                this.waitNum = (this.waitNum?: 0) + 1
//                            }
//                        }
//                    }.toArrayList()
//                    this.projectCarScheduleInfoList = scheduleDetailList.filter { !it.disable && it.autoScheduleType != AutoScheduleType.Unknow && (it.autoScheduleType == AutoScheduleType.DiggingMachine || (it.autoScheduleType == AutoScheduleType.WaitForDiggingMachineSchedule && it.projectCarId == projectScheduleDetail.projectCarId)) }.map {
//                        ProjectCarScheduleInfo().apply {
//                            this.autoScheduleType = it.autoScheduleType
//                            this.groupCode = groupCode
//                            this.projectId = projectId
//                            this.projectCarId = it.projectCarId
//                            this.diggingMachineTime = it.carOrderTime
//                            this.projectDiggingMachineId = it.projectDiggingMachineId
//                        }
//                    }.toArrayList()
//                })
//                //println("######################################initbystatusDig#############################${projectCarScheduleInfoList.toJsonString()}")
//                val projectCarScheduleInfo = projectCarScheduleInfoList.filter { it.projectCarId == carId && it.projectDiggingMachineId > 0L }.firstOrNull()?: ProjectCarScheduleInfo()
                val projectCarScheduleInfo = ProjectCarScheduleInfo()
                val minWaitDig = digAutoInfo.minBy { it.waitNum }!!
                val digAutoList = digAutoInfo.filter { it.waitNum == minWaitDig.waitNum }
                val digAuto = if(digAutoList.size <= 1) {
                    digAutoList.firstOrNull()!!
                } else {
                    val date = Date()
                    val cdate = Date(date.time - 1000*60*30L)
                    digAutoList.forEach {
                        val lastDigCarOrder = carOrderRepository.findByDiggingMachineIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(it.projectDiggingMachineId, cdate, CarOrderState.End).filter { it.sendNum > 0 && (it.autoScheduleType == AutoScheduleType.SlagSite || it.autoScheduleType == AutoScheduleType.WaitForSlagSiteSchedule) }.firstOrNull()
                        if(lastDigCarOrder != null && (date > lastDigCarOrder.createTime)){
                            val ms = (date.time - lastDigCarOrder.createTime.time) / 1000
                            val plus = if(ms <= 0) BigDecimal.ONE else BigDecimal.ONE/(ms.toBigDecimal())
                            it.apply {
                                this.compareValue = (this.waitNum?: 0).toBigDecimal() + plus
                            }
                        }
                    }
                    val minCompareAuto = digAutoList.minBy { (it.compareValue) }!!
                    val minCompareAutoList = digAutoList.filter { it.compareValue <= minCompareAuto.compareValue }
                    if(minCompareAutoList.size <= 1){
                        minCompareAuto
                    }else {
                        minCompareAutoList.minBy { (it.firstScheduleTime ?: Date(0)) }!!
                    }
                }

                projectCarScheduleInfo.projectDiggingMachineId = digAuto.projectDiggingMachineId
                if(projectCarScheduleInfo != null){
                    if(digFix != null) projectCarScheduleInfo.projectDiggingMachineId = digFix
                    val dig = digList.filter { it.id == projectCarScheduleInfo.projectDiggingMachineId }.firstOrNull()
                    save(projectScheduleDetail.apply {
                        this.autoScheduleType = AutoScheduleType.DiggingMachine
                        this.updateTime = Date()
                        this.projectDiggingMachineId = projectCarScheduleInfo.projectDiggingMachineId
                        this.diggingMachineCode = dig?.machineCode?: ""
                        this.carCode = car?.code?: ""
                        this.carUid = car?.uid?: ""
                        this.disable = (schedule.dispatchMode != ProjectDispatchMode.Auto)
                        this.projectId = projectId
                    })
//                    add(CarOrder().apply {
//                        this.sendTime = Date()
//                        this.diggingMachineId = projectCarScheduleInfo.projectDiggingMachineId
//                        this.diggingMachineCode = dig?.machineCode?: ""
//                        this.carCode = car?.code?: ""
//                        this.carId = carId
//                        this.carOrderType = CarOrderType.Auto
//                        this.rid = rid.toLong()
//                    })
                }
                else null

                //println("----------------------------check----------2--------------------${projectCarScheduleInfo}")

                carOrderServiceI.sendCar(carId, groupCode, Date().time % 1000, projectId, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.request, CarOrderType.Auto, projectScheduleDetail)
            }
            AutoScheduleType.WaitForSlagSiteSchedule -> {
                val scheduleDetailList = findByProjectCarIdInAndProjectIdAndDisableIsFalse(listOf(carId), projectId).toMutableList()
                val projectSiteAll = projectSlagSiteServiceI.getAllByProjectId(projectId)
                var isInit = false
                scheduleDetailList.filter { it.projectCarId == carId }.forEach {
                    if(!isInit) {
                        it.apply {
                            this.autoScheduleType = AutoScheduleType.WaitForSlagSiteSchedule
                        }
                        isInit = true
                    }
                    else {
                        it.apply {
                            this.autoScheduleType = AutoScheduleType.Unknow
                        }
                    }
                }
                val scheduleDetailSiteList = (findByProjectSlagSiteIdInAndProjectIdAndDisableIsFalse(slagSiteIdList, projectId))
                val scheduleDetailDigList = findByProjectDiggingMachineIdInAndProjectIdAndDisableIsFalse(digList.map { it.machineId }, projectId)
                val projectCarScheduleInfoList = autoScheduleForSlagSite(AutoScheduleInfo().apply {
                    this.projectDiggingMachineScheduleInfoList = digList.map {
                        ProjectDiggingMachineScheduleInfo().apply {
                            this.groupCode = groupCode
                            this.projectDiggingMachineId = it.machineId
                            //val projectDig = projectDigAll.filter { it.id == this.projectDiggingMachineId }.firstOrNull()
                            val eff = effs.filter { it.projectDiggingMachineId == this.projectDiggingMachineId }.firstOrNull()
                            this.distance = it.distance //100
                            //this.intervalTimeLong = (projectDig?.intervalTime?: 2) * 1000 * 60L
                            this.intervalTimeLong = if(eff == null || eff.lastHourCarNum <= 0) 10*1000*60L else (1000*60*60L)/eff.lastHourCarNum
                            this.projectId = projectId
                            scheduleDetailDigList.filter { it.projectDiggingMachineId == this.projectDiggingMachineId && it.autoScheduleType == AutoScheduleType.DiggingMachine }.forEach {
                                if(this.lastScheduleTime < it.carOrderTime) this.lastScheduleTime = it.carOrderTime
                            }
                        }
                    }.toArrayList()
                    this.projectCarScheduleInfoList = scheduleDetailList.filter { it.autoScheduleType != AutoScheduleType.Unknow }.map {
                        ProjectCarScheduleInfo().apply {
                            this.autoScheduleType = it.autoScheduleType
                            this.groupCode = groupCode
                            this.projectId = projectId
                            this.projectCarId = it.projectCarId
                            this.slagSiteTime = it.carOrderTime
                            this.slagSiteId = it.projectSlagSiteId
                            this.projectDiggingMachineId = it.projectDiggingMachineId
                        }
                    }.toArrayList()
                    //println("+++++++++++++++++++++++++++++++++++++++++++++++++initbyss----------------------------------------------${this.projectCarScheduleInfoList}")

                    this.projectSlagSiteScheduleInfoList = slagSiteIdList.map {
                        val siteId = it
                        val site = projectSiteAll.filter { it.id == siteId }.firstOrNull()
                        val intervalTime = site?.intervalTime?: 0
                        val distance = site?.distance?: 0
                        ProjectSlagSiteScheduleInfo().apply {
                            this.slagSiteId = it
                            this.intervalTimeLong = (if(intervalTime > 2) intervalTime else 10) * 1000*60L //1000 * 60 * 2L
                            this.distance = if(distance > 100) distance else 1000
                            this.projectId = projectId
                            scheduleDetailSiteList.filter { it.projectSlagSiteId == this.slagSiteId && it.autoScheduleType == AutoScheduleType.SlagSite }.forEach {
                                if(this.lastScheduleTime < it.carOrderTime) this.lastScheduleTime = it.carOrderTime
                            }
                        }
                    }.toArrayList()
                })
                //println("######################################initbystatusSite#############################${projectCarScheduleInfoList.toJsonString()}")

                val projectCarScheduleInfo = projectCarScheduleInfoList.filter { it.projectCarId == carId && it.slagSiteId > 0L }.firstOrNull()
                if(projectCarScheduleInfo != null){
                    if(siteFix != null) projectCarScheduleInfo.slagSiteId = siteFix
                    val site = projectSlagSiteServiceI.get(projectCarScheduleInfo.slagSiteId)
                    save(projectScheduleDetail.apply {
                        this.autoScheduleType = AutoScheduleType.SlagSite
                        this.updateTime = Date()
                        this.projectSlagSiteId = projectCarScheduleInfo.slagSiteId
                        this.projectSlagSiteName = site?.name?: ""
                        this.carCode = car?.code?: ""
                        this.carUid = car?.uid?: ""
                        this.disable = (schedule.dispatchMode != ProjectDispatchMode.Auto)
                        this.projectId = projectId
                        this.reAuto = false
                    })
//                    add(CarOrder().apply {
//                        this.sendTime = Date()
//                        this.slagSiteId = projectCarScheduleInfo.slagSiteId
//                        this.slagSiteName = site?.name?: ""
//                        this.carId = carId
//                        this.carCode = car?.code?: ""
//                        this.carOrderType = CarOrderType.Auto
//                        this.rid = rid.toLong()
//                    })
                }
                else null

                carOrderServiceI.sendCar(carId, groupCode, Date().time % 1000, projectId, AutoScheduleType.WaitForSlagSiteSchedule, SendType.request, CarOrderType.Auto, projectScheduleDetail)
            }
            else -> return
        }
        //if(carOrderAuto != null) send(carOrderAuto, rid, sendType)
        return
    }

    override fun replyCar(car: ProjectCar, scheduleCar: ScheduleCar, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType) {
        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(car.id, projectId)
        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()?: return
//        if(projectScheduleDetail != null) {
//            val dig = projectDiggingMachineRepository.getById(projectScheduleDetail.diggingMachineId).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }.firstOrNull()
//            if (dig == null) {
////            carOrderServiceI.cancel(thisCarOrder.apply {
////                this.reAuto = true
////                this.reAutoTime = Date()
////            })
//                return initStatus(projectScheduleDetail.apply {
//                    this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
//                    this.carOrderId = 0L
//                    this.reAuto = true
//                })
//            }
//        }
        return carOrderServiceI.sendCar(car.getId()!!, scheduleCar.groupCode, rid, projectId, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.reply, CarOrderType.Auto, projectScheduleDetail)
//        val projectScheduleDetailList = findByProjectCarIdAndProjectIdOrderByIdDesc(car.id, projectId)
//        val projectScheduleDetail = projectScheduleDetailList.firstOrNull()
//        val distance = projectScheduleDetail?.realDigDistance?: BigDecimal(10)
//        val sl = BigDecimal(0.2)
//        val el = BigDecimal(0.6)
//        val thisCarOrder = carOrderRepository.findByCarIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(car.id, Date(Date().time - 1000*60*60), CarOrderState.End).firstOrNull()
//        if(thisCarOrder != null && !(thisCarOrder.reAuto?: false) && thisCarOrder.carOrderType == CarOrderType.Auto && projectScheduleDetail != null && !projectScheduleDetail.disable && projectScheduleDetail.autoScheduleType == AutoScheduleType.DiggingMachine && !(projectScheduleDetail.nearDig?: true) && !(projectScheduleDetail.reAuto?: false) && projectScheduleDetail.carOrderId > 0L && distance > sl && distance < el ){
//            if(thisCarOrder != null && !(thisCarOrder.reAuto?: false)) {
//                val scheduleCarList = scheduleCarServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.groupCode).filter{
//                    (it.isVaild?: false) && !(it.fault?: false)
//                }
//                val scheduleCarIdList = scheduleCarList.map { it.carId }
//                val carList = projectCarServiceI.getByProjectIdAndIsVaild(projectId, true).filter { scheduleCarIdList.contains(it.id) }
//                val scheduleDetailList = findByProjectCarIdInAndProjectIdAndDisableIsFalse(carList.map { it.id }, projectId)
//                val projectDigAll = projectDiggingMachineRepository.getAllByProjectIdAndIsVaildAndSelected(projectId, true).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }
//                val projectDigIdAll = projectDigAll.map { it.id }
//                val digList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, scheduleCar.groupCode).filter{ it.isVaild && !it.fault && projectDigIdAll.contains(it.machineId) }
//                if(digList.size > 0) {
//                    val scheduleDetailDigList = findByProjectDiggingMachineIdInAndProjectIdAndDisableIsFalse(digList.map { it.machineId }, projectId)
//                    var isInit = false
//                    scheduleDetailList.filter { it.projectCarId == car.id }.forEach {
//                        if (!isInit) {
//                            it.apply {
//                                this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
//                            }
//                            isInit = true
//                        } else {
//                            it.apply {
//                                this.autoScheduleType = AutoScheduleType.Unknow
//                            }
//                        }
//                    }
//                    val digAutoInfo = digList.map {
//                        ProjectDiggingMachineScheduleInfo().apply {
//                            this.groupCode = groupCode
//                            this.projectDiggingMachineId = it.machineId
//                            //val projectDig = projectDigAll.filter { it.id == this.projectDiggingMachineId }.firstOrNull()
//                            //val eff = effs.filter { it.projectDiggingMachineId == this.projectDiggingMachineId }.firstOrNull()
//                            this.distance = it.distance  //100
//                            //this.intervalTimeLong = (projectDig?.intervalTime?: 2) * 1000 * 60L
//                            //this.intervalTimeLong = if(eff == null || eff.lastHourCarNum <= 0) 10*1000*60L else (1000*60*60L)/eff.lastHourCarNum
//                            this.projectId = projectId
//                            //println("---------------------autoScheduleForDiggingMachine---scheduleDetailDigList--${scheduleDetailDigList}")
//                            val realScheduleDigList = scheduleDetailDigList.filter { it.projectDiggingMachineId == this.projectDiggingMachineId && it.autoScheduleType == AutoScheduleType.DiggingMachine }
//                            //println("---------------------autoScheduleForDiggingMachine-----------realScheduleDigList----------${realScheduleDigList}")
//                            realScheduleDigList.forEach {
//                                if (this.lastScheduleTime < it.carOrderTime) this.lastScheduleTime = it.carOrderTime
//                                if (this.firstScheduleTime == null) this.firstScheduleTime = it.carOrderTime
//                                if (this.firstScheduleTime != null && this.firstScheduleTime!! > it.carOrderTime) this.firstScheduleTime = it.carOrderTime
//                                this.waitNum = (this.waitNum ?: 0) + 1
//                                if ((it.nearDig ?: false)) this.realWaitNum++
//                            }
//                        }
//                    }
//                    val max = digAutoInfo.maxBy{ it.waitNum }!!
//                    val min = digAutoInfo.minBy{ it.waitNum }!!
//                    val myDig = digAutoInfo.filter { it.projectDiggingMachineId == projectScheduleDetail.projectDiggingMachineId }.firstOrNull()
//                    if(myDig != null && (max.waitNum - min.waitNum) > 1 && min.waitNum < 2 && myDig.waitNum == max.waitNum && myDig.realWaitNum > 0 ){
//                        carOrderServiceI.cancel(thisCarOrder.apply {
//                            this.reAuto = true
//                            this.reAutoTime = Date()
//                        })
//                        return initStatus(projectScheduleDetail.apply {
//                            this.autoScheduleType = AutoScheduleType.WaitForDiggingMachineSchedule
//                            this.carOrderId = 0L
//                            this.reAuto = true
//                        })
//                    }
//                }
//
//                carOrderServiceI.save(thisCarOrder.apply {
//                    this.reAuto = true
//                })
//            }
//        }
//        return if(thisCarOrder != null) carOrderServiceI.sendCarDetail(car.getId()!!, scheduleCar.groupCode, rid, projectId, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.reply, CarOrderType.Auto, thisCarOrder)
//        else carOrderServiceI.sendCar(car.getId()!!, scheduleCar.groupCode, rid, projectId, AutoScheduleType.WaitForDiggingMachineSchedule, SendType.reply, CarOrderType.Auto)
    }

    data class DigAllotment(
            var digId: Long = 0L,
            var num: Int = 0,
            var restNum: Int = 0,

            var diggingMachineCode :String = "",      //项目中的挖机编号

            var projectId: Long = 0L,    //参与的项目编号

            var diggingMachineId: Long = 0L,    //车主选进来项目的车的id

            var createTime: Date = Date(),

            var updateTime: Date = Date(),

            var brandId: Long = 0L,      //品牌ID

            var brandName: String = "", //品牌名

            var modelId: Long = 0L,      //型号ID

            var modelName: String = "", //型号名

            var groupCode: String = "",      // 分组编号

            var placeId: Long = 0L,      //工作地点ID

            var placeName: String = "",      //工作地点名称

            var materialId: Long? = null,   //当前装载物料ID

            var materiaName: String? = null, //物料名称

            var distance: Long? = null,    //当前运距

            var pricingType: PricingTypeEnums = PricingTypeEnums.Unknow     //计价方式
    )
}
