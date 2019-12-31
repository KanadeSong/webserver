package com.seater.smartmining.service.impl

import com.seater.smartmining.dao.CarOrderDaoI
import com.seater.smartmining.dao.ProjectScheduleDetailDaoI
import com.seater.smartmining.entity.*
import com.seater.smartmining.entity.repository.*
import com.seater.smartmining.enums.DiggingMachineStatus
import com.seater.smartmining.enums.DiggingMachineStopStatus
import com.seater.smartmining.enums.ProjectCarStatus
import com.seater.smartmining.mqtt.MqttSender
import com.seater.smartmining.service.*
import com.seater.smartmining.utils.schedule.*
import com.seater.user.entity.SysUser
import com.seater.user.session.WebSecurityConfig.SESSION_KEY
import com.systech.helpers.redis.jsonGet
import com.systech.helpers.redis.jsonSet
import com.sytech.user.helpers.redisLockDefaultTimeout
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*
import java.util.concurrent.TimeUnit
import javax.persistence.EntityManager
import javax.persistence.criteria.Predicate

@Service
@javax.transaction.Transactional(rollbackOn = [Exception::class])
class CarOrderServiceImpl(
        val applicationContext: ApplicationContext,
        val projectCarRepository: ProjectCarRepository,
        val projectDiggingMachineServiceI: ProjectDiggingMachineServiceI,
        val projectDiggingMachineRepository: ProjectDiggingMachineRepository,
        val projectSlagSiteServiceI: ProjectSlagSiteServiceI,
        val CarOrderDaoI: CarOrderDaoI,
        val em: EntityManager,
        val carOrderRepository: CarOrderRepository,
        val projectScheduleDetailDaoI: ProjectScheduleDetailDaoI,
        val scheduleCarServiceI: ScheduleCarServiceI,
        val projectScheduleServiceI: ProjectScheduleServiceI,
        val scheduleMachineServiceI: ScheduleMachineServiceI,
        val mqttSender: MqttSender,
        val stringRedisTemplate: StringRedisTemplate,
        val projectScheduleRepository: ProjectScheduleRepository,
        val scheduleCarRepository: ScheduleCarRepository,
        val scheduleMachineRepository: ScheduleMachineRepository
) : CarOrderServiceI {

    val log = LoggerFactory.getLogger(CarOrderServiceImpl::class.java)
    val entityName = "CarOrder"

    val updateLock = false
    var redisLockTimeout = redisLockDefaultTimeout
    val keyGroup = "lock:CarOrder:"
    fun getKey(id: Long) = "${keyGroup}${id}"

    override fun get(id: Long?): CarOrder? {
        if (id == null) return null
        return CarOrderDaoI.get(id)
    }

    override fun save(CarOrder: CarOrder): CarOrder {
        return CarOrderDaoI.save(CarOrder = CarOrder.apply {
            this.updateTime = Date()
            if(this.carOrderRealType == CarOrderRealType.Unknow){
                this.carOrderRealType = when(this.carOrderType){
                    CarOrderType.Auto -> CarOrderRealType.Auto
                    CarOrderType.Temp -> {
                        if(this.reAuto?: false) CarOrderRealType.Auto
                        else CarOrderRealType.Temp
                    }
                    CarOrderType.Text -> CarOrderRealType.Text
                    CarOrderType.Fix -> CarOrderRealType.Fix
                    else -> this.carOrderRealType
                }
            }
        })
    }

    override fun cancel(carOrder: CarOrder): CarOrder {
        return down(carOrder.apply {
            //this.success = false
            this.cancel = true
            this.remark = "取消"
        })
    }

    override fun down(carOrder: CarOrder): CarOrder {
        return save(carOrder.apply {
            this.carOrderState = CarOrderState.End
            this.success = false
        })
    }

    override fun add(CarOrder: CarOrder): CarOrder {
        val new = save(CarOrder = CarOrder.apply{
            this.id = 0L
            this.orderNumber = this.orderNumber?: (CarOrderDaoI.getMaxOrderNumber() + 1)
            if(this.carOrderType == CarOrderType.Text) {
                this.diggingMachineId = null
                this.slagSiteId = null
            }
            if(this.diggingMachineId != null) {
                val dig = projectDiggingMachineRepository.getById(this.diggingMachineId!!).firstOrNull()
                this.diggingMachineCode = dig?.code?: ""
            }
            if(this.slagSiteId != null) {
                val slagSite = projectSlagSiteServiceI.get(this.slagSiteId!!)
                this.slagSiteName = slagSite?.name?: ""
            }

            when(this.carOrderType){
                CarOrderType.Auto -> this.fromUserName = "系统"
                CarOrderType.Text, CarOrderType.Fix, CarOrderType.Temp -> {
                    this.fromUserId = 0L
                    this.fromUserName = ""
                    val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
                    if(request != null) {
                        val session = request.getSession()
                        val user = session.getAttribute(SESSION_KEY) as SysUser
                        this.fromUserId = user.id
                        this.fromUserName = user.account
                    }
                }
            }
        })
        return new
    }

    override fun receiveCar(carId: Long, rid: Long){

    }

    override fun sendCarDetail(carId: Long, groupCode: String, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType, carOrder: CarOrder?, scheduleDetail: ProjectScheduleDetail?) {
        val car = projectCarRepository.getById(carId).firstOrNull()?: return
        val realScheduleDetail = scheduleDetail?: projectScheduleDetailDaoI.getByProjectCarIdAndProjectId(carId, projectId)
        val digWorkAll = projectDiggingMachineRepository.getAllByProjectIdAndIsVaildAndSelected(projectId, true)
        val projectDigAll = digWorkAll.filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }
        val projectDigWorkAll = digWorkAll.filter { it.status == DiggingMachineStatus.Working && (it.stopStatus == DiggingMachineStopStatus.Normal || it.stopStatus == DiggingMachineStopStatus.PAUSE) }
//                .filter{
//                    val key = getKeyLockDig(it.id)
//                    val lockNum: Int? = valueOps.jsonGet(key)
//                    if(lockNum == null || lockNum < 2) true
//                    else false
//                }
        val projectDigIdAll = projectDigAll.map { it.id }
        val projectDigWorkIdAll = projectDigWorkAll.map { it.id }
        val sdAll = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
        val digList = sdAll.filter{ it.isVaild && !it.fault && projectDigIdAll.contains(it.machineId) }
        val digWorkList = sdAll.filter{ it.isVaild && !it.fault && projectDigWorkIdAll.contains(it.machineId) }

        if((realScheduleDetail?.disable?: false) || (projectDigWorkAll.size <= 0 && sendType == SendType.reply)) { // || (scheduleDetail != null && digList.size <= 0 && (scheduleDetail.autoScheduleType == AutoScheduleType.WaitForDiggingMachineSchedule || scheduleDetail.autoScheduleType == AutoScheduleType.DiggingMachine))){
            sendMqZero(car.uid, rid, sendType, projectId)
            return
        }

        val lastCarOrder = carOrder?: carOrderRepository.findByCarIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(carId, Date(Date().time - 1000*60*60), CarOrderState.End).firstOrNull()
        val carOrderList = carOrderRepository.findByCarIdAndCarOrderState(carId, CarOrderState.Ready)
        if(car == null || car.status != ProjectCarStatus.Working || realScheduleDetail == null){
            sendMqZero(car?.uid?: "", rid, sendType, projectId)
            return
        }

        if(carOtherType == CarOrderType.Text) {
            val carOrderText = carOrderList.filter { it.carOrderType == CarOrderType.Text }.sortedByDescending { it.id }.firstOrNull()
            if (carOrderText != null) {
                send(carOrderText, rid, sendType, projectId, realScheduleDetail, lastCarOrder, groupCode, digList, digWorkList)
                return
            }
        }
        else {
            val carOrderTemp = carOrderList.filter { it.carOrderType == CarOrderType.Temp }.sortedByDescending { it.id }.firstOrNull()
            if (carOrderTemp != null) {
                carOrderTemp.apply {
                    if(!(this.fixDig?: false)) {
                        this.diggingMachineId = realScheduleDetail?.projectDiggingMachineId?: 0L
                        this.diggingMachineCode = realScheduleDetail?.diggingMachineCode?: ""
                    }
                    if(!(this.fixSite?: false)) {
                        this.slagSiteId = realScheduleDetail?.projectSlagSiteId?: 0L
                        this.slagSiteName = realScheduleDetail?.projectSlagSiteName?: ""
                    }
                }
                send(carOrderTemp, rid, sendType, projectId, realScheduleDetail, lastCarOrder, groupCode, digList, digWorkList)
                return
            }
            val carOrderFix = carOrderList.filter { it.carOrderType == CarOrderType.Fix }.sortedByDescending { it.id }.firstOrNull()
            if (carOrderFix != null) {
                carOrderFix.apply {
                    if(!(this.fixDig?: false)) {
                        this.diggingMachineId = realScheduleDetail?.projectDiggingMachineId?: 0L
                        this.diggingMachineCode = realScheduleDetail?.diggingMachineCode?: ""
                    }
                    if(!(this.fixSite?: false)) {
                        this.slagSiteId = realScheduleDetail?.projectSlagSiteId?: 0L
                        this.slagSiteName = realScheduleDetail?.projectSlagSiteName?: ""
                    }
                }
                send(carOrderFix, rid, sendType, projectId, realScheduleDetail, lastCarOrder, groupCode, digList, digWorkList)
                return
            }
        }

        if(carOtherType != CarOrderType.Auto) return

        //println("-------------------------------test-----------------------------${scheduleDetail}")

        val oldCarOrderAuto = carOrderRepository.getById(realScheduleDetail?.carOrderId?: 0L)?: null
        var new = (CarOrder()).apply {
            this.sendTime = Date()
            this.sendType = sendType
            this.diggingMachineId = realScheduleDetail?.projectDiggingMachineId?: 0L
            this.diggingMachineCode = realScheduleDetail?.diggingMachineCode?: ""
            this.carCode = car?.code?: ""
            this.carId = carId
            this.carUid = car?.uid?: ""
            this.projectId = projectId
            this.carStatus = car?.status?: ProjectCarStatus.Unknow
            this.carOrderType = CarOrderType.Auto
            this.carJobStatus = realScheduleDetail?.jobsts?: FixJobStatus.Unknow

            this.realDigId = realScheduleDetail?.realDigId
            this.realDigDistance = realScheduleDetail?.realDigDistance
            this.realSiteIdList = realScheduleDetail?.realSiteIdList
            this.realSiteDistanceList = realScheduleDetail?.realSiteDistanceList
            this.groupCode = realScheduleDetail?.groupCode

            this.autoScheduleType = realScheduleDetail?.autoScheduleType?: this.autoScheduleType
            this.slagSiteId = realScheduleDetail?.projectSlagSiteId?: 0L
            this.slagSiteName = realScheduleDetail?.projectSlagSiteName?: ""
            this.rid = rid.toLong()
            this.detailUpdateTime = realScheduleDetail?.updateTime?: Date()
            this.loadTime = realScheduleDetail?.timeLoad
            this.digOrderType = AutoOrderType.From
            this.siteOrderType = AutoOrderType.From
            when(realScheduleDetail.autoScheduleType){
                AutoScheduleType.WaitForDiggingMachineSchedule, AutoScheduleType.DiggingMachine -> this.digOrderType = AutoOrderType.Auto
                AutoScheduleType.WaitForSlagSiteSchedule, AutoScheduleType.SlagSite -> this.siteOrderType = AutoOrderType.Auto
                else -> 0
            }
        }
        var carOrderAuto = oldCarOrderAuto
        if(carOrderAuto == null) {
            if(lastCarOrder != null && lastCarOrder.realDigId == new.realDigId && lastCarOrder.realSiteIdList == new.realSiteIdList && lastCarOrder.autoScheduleType == new.autoScheduleType && lastCarOrder.carOrderType == new.carOrderType && lastCarOrder.sendType == new.sendType && lastCarOrder.diggingMachineId == new.diggingMachineId && lastCarOrder.projectId == new.projectId && lastCarOrder.slagSiteId == new.slagSiteId && lastCarOrder.carId == new.carId){
                carOrderAuto = lastCarOrder.apply {
                    this.reUseNum = (this.reUseNum?: 0) + 1
                    this.realDigDistance = realScheduleDetail?.realDigDistance?: this.realDigDistance
                }
            }
            else carOrderAuto = add(new)
        }
        send(carOrderAuto, rid, sendType, projectId, realScheduleDetail, lastCarOrder, groupCode, digList, digWorkList)
        return
    }

    override fun sendCar(carId: Long, groupCode: String, rid: Long, projectId: Long, autoScheduleType: AutoScheduleType, sendType: SendType, carOtherType: CarOrderType, scheduleDetail: ProjectScheduleDetail?){
        //val lastCarOrder = carOrderRepository.findByCarIdAndCreateTimeAfterAndCarOrderStateOrderByIdDesc(carId, Date(Date().time - 1000*60*60), CarOrderState.End).firstOrNull()
        sendCarDetail(carId, groupCode, rid, projectId, autoScheduleType, sendType, carOtherType, null, scheduleDetail)
    }

    fun send(carOrder: CarOrder, rid: Long, sendType: SendType, projectId: Long, scheduleDetail: ProjectScheduleDetail?, lastCarOrder: CarOrder?, groupCode: String, digList: List<ScheduleMachine>, digWorkList: List<ScheduleMachine>) {
        val scheduleDetail2 = scheduleDetail?: ProjectScheduleDetail(
                projectCarId =  carOrder.carId, projectId = projectId, disable = false
        )
        if(scheduleDetail2.disable){
            sendMqZero(carOrder.carUid, rid, sendType, projectId)
            return
        }

        carOrder.apply {
            if(this.firstSendTime == null) this.firstSendTime = Date()
            val rand = Random(Date().time)
            if(digList.size <= 0) this.diggingMachineId = 0L
            if(digWorkList.size > 0 && (this.diggingMachineId?: 0L) <= 0L){
                if((this.slagSiteId?: 0L) > 0L && (lastCarOrder?.diggingMachineId?: 0L) > 0L){
                    this.diggingMachineId = (lastCarOrder?.diggingMachineId?: 0L)
                    this.digOrderType = AutoOrderType.From
                }
                else {
                    //val digList = scheduleMachineServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
                    var index = rand.nextInt() % digWorkList.size
                    if (index < 0) index = 0 - index
                    if (index > digWorkList.size) index = index % digWorkList.size
                    val dig = digWorkList.get(index)
                    this.diggingMachineId = dig.machineId
                    this.diggingMachineCode = dig.machineCode
                    this.digOrderType = AutoOrderType.Rand
                }
            }
            if((this.slagSiteId?: 0L) <= 0L){
                if((this.diggingMachineId?: 0L) > 0L && (lastCarOrder?.slagSiteId?: 0L) > 0L){
                    this.slagSiteId = (lastCarOrder?.slagSiteId?: 0L)
                    this.siteOrderType = AutoOrderType.From
                }
                else {
                    val schedule = projectScheduleServiceI.getAllByProjectIdAndGroupCode(projectId, groupCode)
                    val slagSiteIdList = schedule.slagSiteId.replace("[\"", "").replace("\"]", "").split("\",\"").map { it.toLong() }
                    var index = rand.nextInt() % slagSiteIdList.size
                    if (index < 0) index = 0 - index
                    if (index > slagSiteIdList.size) index = index % slagSiteIdList.size
                    val slagSiteId = slagSiteIdList.get(index)
                    this.slagSiteId = slagSiteId
                    this.siteOrderType = AutoOrderType.Rand
                }
            }
        }
        carOrder.apply {
            this.sendNum++
            this.rid = rid
            this.sendTime = Date()
        }
        when(carOrder.carOrderType){
            CarOrderType.Text -> {
                save(carOrder.apply {
                    this.carOrderState = CarOrderState.End
                    this.success = false
                    this.remark = "未对接设备"
                })
            }
            CarOrderType.Fix -> {
                sendMq(carOrder, rid, sendType, projectId)
//                save(carOrder.copy().apply {
//                    this.id = 0L
//                    this.orderTime = Date()
//                    this.rid = rid.toLong()
//                    this.sendTime = Date(999999999999999)
//                    this.sendNum ++
//                })
                save(carOrder.apply {
                    //this.carOrderState = CarOrderState.End
                    this.success = true
                })
            }
            CarOrderType.Temp, CarOrderType.Auto -> {
                sendMq(carOrder, rid, sendType, projectId)
                save(carOrder.apply {
                    this.carOrderState = CarOrderState.End
                    this.success = true
                })
            }
            else -> 0
        }
        projectScheduleDetailDaoI.save(
                scheduleDetail2.apply {
                    when(this.autoScheduleType){
                        AutoScheduleType.WaitForDiggingMachineSchedule -> {
                            this.autoScheduleType = AutoScheduleType.DiggingMachine
                        }
                        AutoScheduleType.WaitForSlagSiteSchedule -> {
                            this.autoScheduleType = AutoScheduleType.SlagSite
                        }
                        else -> 0
                    }
                    this.projectDiggingMachineId = carOrder.diggingMachineId?: 0L
                    this.diggingMachineCode = carOrder.diggingMachineCode?: ""
                    this.projectSlagSiteId = carOrder.slagSiteId?: 0L
                    this.projectSlagSiteName = carOrder.slagSiteName?: ""
                    this.carOrderId = carOrder.id
                    if(carOrder.sendNum == 1L) this.carOrderTime = Date()
                    if(carOrder.carOrderType != CarOrderType.Auto) {
                        if(!(carOrder.secondAuto?: false)) {
                            this.reAuto = true
                        }
                        else this.secondAuto = true
                    }
                }
        )
    }

    var autoSite = false

    var redisCacheTimeout = 1000*60*1L
    val keyGroupLock = "entity:AutoDigLock:"
    fun getKeyLockDig(id: Long) = "${keyGroupLock}${id}"
    val valueOps by lazy {
        stringRedisTemplate.opsForValue()
    }

    fun sendMq(carOrder: CarOrder, rid: Long, sendType: SendType, projectId: Long){
        val topic = "smartmining/slagcar/cloud/" + carOrder.carUid + "/${sendType}${autoSendFlag}"
        val siteAll = projectSlagSiteServiceI.all
        val site = siteAll.filter { it.id == carOrder.slagSiteId }.firstOrNull()
        val schedule = projectScheduleRepository.findByProjectIdAndGroupCodeAndDispatchMode(carOrder.projectId, carOrder.groupCode, ProjectDispatchMode.Auto).firstOrNull()
        val scheduleCar = scheduleCarRepository.getAllByProjectIdAndCarIdAndIsVaild(carOrder.projectId, carOrder.carId, true).firstOrNull()
        val scheduleMachine = scheduleMachineRepository.getAllByProjectIdAndMachineIdAndIsVaild(carOrder.projectId, carOrder.diggingMachineId, true).firstOrNull()
        val siteIdList = (schedule?.slagSiteId?: "").replace("\"", "").replace("[", "").replace("]", "").split(",").map {
            val t = it.trim()
            if(it.isNullOrBlank()) 0L
            else it.toLong()
        }
        val siteList = siteAll.filter { siteIdList.contains(it.id) }

//        val key = getKeyLockDig(carOrder.diggingMachineId?: 0L)
//        val lockNum: Int? = valueOps.jsonGet(key)
//        if(sendType == SendType.request && lockNum != null && lockNum < 2){
//            valueOps.jsonSet(key, lockNum + 1, redisCacheTimeout, TimeUnit.MILLISECONDS)
//        }

        var digCode = carOrder.diggingMachineCode?: scheduleMachine?.machineCode?: ""
        val digId = carOrder.diggingMachineId?: 0L
        if(digId <= 0L) digCode = ""
        if(digCode.isNullOrBlank() && digId > 0L){
            val dig = projectDiggingMachineRepository.getById(digId).firstOrNull()
            digCode = dig?.code?: ""
        }

        val payload = mapOf<String, Any>(
                "cmdInd" to "schedule",
                "pktID" to rid,
                "cmdStatus" to 0,
                "projectID" to projectId,
                "slagcarID" to carOrder.carId,
                "excavatorID" to "${digId}",
                "excavatorCode" to "${digCode}",
                "slagSiteID" to if(autoSite) "${carOrder.slagSiteId}" else "${siteList.map { it.id }.joinToString(",")}",
                "position" to if(autoSite) (if(site != null) "${site.longitude}-${site.latitude}" else "") else "${siteList.map { "${it.longitude}-${it.latitude}" }.joinToString(",") }",
                "priceMethod" to "${scheduleMachine?.pricingType?.ordinal?: 0}",
                "schMode" to (schedule?.deviceStartStatus?.ordinal?: 0),
                "exctDist" to "${scheduleMachine?.distance?: 0}",
                "loader" to "${scheduleMachine?.materialId?: 0}",
                "dispatchMode" to 4
        )
        //println("----------------------------------test---------------------------${carOrder}")
        //println("----------------------------------test---------------------------${payload}")
        sendRealMq(topic, payload)
    }

    override fun sendMqZero(carUid: String, rid: Long, sendType: SendType, projectId: Long){
        if(sendType == SendType.request) return
        val topic = "smartmining/slagcar/cloud/" + carUid + "/${sendType}${autoSendFlag}"
        val projectCar = projectCarRepository.getByProjectIdAndUid(projectId, carUid)
        //val scheduleCar = scheduleCarRepository.getAllByProjectIdAndCarIdAndIsVaild(projectId, projectCar.id, true).firstOrNull()
        //val schedule = projectScheduleRepository.findByProjectIdAndGroupCodeAndDispatchMode(projectId, scheduleCar?.groupCode?: "", ProjectDispatchMode.Auto).firstOrNull()

        val payload = mapOf<String, Any>(
                "cmdInd" to "schedule",
                "pktID" to rid,
                "cmdStatus" to 0,
                "projectID" to projectId,
                "slagcarID" to (projectCar?.id?: 0L),
                "excavatorID" to "0",
                "excavatorCode" to "",
                "slagSiteID" to "",
                "position" to "",
                "priceMethod" to "0",
                "schMode" to 0,
                "exctDist" to "0",
                "loader" to "0",
                "dispatchMode" to 4
        )
        //println("----------------------------------testMqZero---------------------------")
        sendRealMq(topic, payload)
    }

    fun sendRealMq(topic: String, payload: Map<String, Any?>){
        mqttSender.sendDeviceReply(topic, payload)
    }

    override fun update(CarOrder: CarOrder, old: CarOrder): CarOrder {
        val new = save(CarOrder = CarOrder)
        return new
    }

    override fun delete(id: Long) {
        val CarOrder = get(id) ?: return
        CarOrderDaoI.delete(id = id)
    }

    override fun delete(ids: List<Long>) {
        CarOrderDaoI.delete(ids = ids)
    }

    override fun query(spec: Specification<CarOrder>?, pageable: Pageable): Page<CarOrder> {
        return CarOrderDaoI.query(spec = spec, pageable = pageable)
    }


    override fun queryPage(
            current: Int?,
            pageSize: Int?,
            projectId: Long?,
            managerId: Long?
    ): Page<CarOrder> {

        val minPageSize = 10
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<CarOrder> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            if(managerId != null && projectId != null){
                val ps = projectScheduleRepository.getAllByProjectIdAndManagerId(projectId, "%\"${managerId}\"%", 0, 1000)
                val t = cb.`in`(root.get<String>("groupCode"))
                ps.forEach {
                    t.value(it.groupCode)
                }
                ls.add(t)
            }

            ls.add(cb.greaterThan(root.get<Long>("sendNum"), 0L))

            cb.and(*ls.toTypedArray())
        }

        return query(pageable = PageRequest.of(cur, page, Sort(Sort.Direction.DESC, "id")), spec = spec)
    }

    override fun queryPage(current: Int?, pageSize: Int?, projectId: Long?, carCode: String?, startTime: Date?, endTime: Date): Page<CarOrder> {
        val minPageSize = 10000
        val maxPageSize = Integer.MAX_VALUE

        var cur = (current ?: 0) - 1
        var page = pageSize ?: minPageSize
        if (cur < 0) cur = 0
        if (page < 0) page = maxPageSize
        else if (page > maxPageSize) page = maxPageSize

        var spec = Specification<CarOrder> { root, query, cb ->
            var ls = mutableListOf<Predicate>()

            if (projectId != null) {
                ls.add(cb.equal(root.get<Long>("projectId"), projectId))
            }

            if(carCode != null && carCode != ""){
                ls.add(cb.equal(root.get<String>("carCode"), carCode))
            }

            if(startTime != null && endTime != null){
                ls.add(cb.between(root.get<Date>("createTime"), startTime, endTime))
            }

            ls.add(cb.greaterThan(root.get<Long>("sendNum"), 0L))

            cb.and(*ls.toTypedArray())
        }

        return query(pageable = PageRequest.of(cur, page, Sort(Sort.Direction.DESC, "createTime")), spec = spec)
    }

    override fun init(carOrder: CarOrder): CarOrder{
        return carOrder.apply {
            println("----------------------------------initbyinit0--------------------------------${carOrder}")
            val car = projectCarRepository.findByProjectIdAndUidAndIsVaild(this.projectId, this.carUid, true).firstOrNull() ?: throw Exception("渣车不存在")
            println("----------------------------------initbyinit1--------------------------------${car}")
            //if(!car.seleted) throw Exception("渣车未排班")
            println("----------------------------------initbyinit2--------------------------------")
            if(!car.vaild || car.status != ProjectCarStatus.Working) throw  java.lang.Exception("渣车状态错误")
            println("----------------------------------initbyinit3--------------------------------")
            val scheduleCar = scheduleCarServiceI.getAllByProjectIdAndCarIdAndIsVaild(this.projectId, car.id, true).filter { !it.fault }.firstOrNull()?: throw Exception("排班信息错误")
            println("----------------------------------initbyinit4--------------------------------${scheduleCar}")
            if(carOrder.diggingMachineId != null && carOrder.diggingMachineId!! > 0L) {
                val dig = projectDiggingMachineRepository.getById(carOrder.diggingMachineId).filter { it.status == DiggingMachineStatus.Working && it.stopStatus == DiggingMachineStopStatus.Normal }.firstOrNull()
                println("----------------------------------initbyinit5--------------------------------${dig}")
                if(dig == null) throw Exception("该挖机未上机")
                val scheduleMachine = scheduleMachineRepository.getAllByProjectIdAndMachineIdAndIsVaild(dig.projectId, dig.id, true).filter { it.groupCode == scheduleCar.groupCode }.firstOrNull()?: throw java.lang.Exception("与挖机不在同一个排班")
            
            println("----------------------------------initbyinit6--------------------------------${scheduleMachine}")
            }
            this.groupCode = scheduleCar.groupCode

            val projectScheduleDetail = projectScheduleDetailDaoI.getByProjectCarIdAndProjectId(car.id, car.projectId)
            if(projectScheduleDetail != null){
                this.carJobStatus = projectScheduleDetail?.jobsts?: FixJobStatus.Unknow
                this.realDigId = projectScheduleDetail?.realDigId
                this.realDigDistance = projectScheduleDetail?.realDigDistance
                this.realSiteIdList = projectScheduleDetail?.realSiteIdList
                this.realSiteDistanceList = projectScheduleDetail?.realSiteDistanceList
                this.groupCode = projectScheduleDetail?.groupCode
            }

            this.carCode = car.code
            this.carUid = car.uid
            
            println("----------------------------------initbyinit7--------------------------------${projectScheduleDetail}")
        }
    }

}

enum class SendType(remark: String){
    request("请求"),
    reply("回应");
}

data class CarStateMap(
        var car: ProjectCar = ProjectCar(),
        var autoScheduleType: AutoScheduleType = AutoScheduleType.Unknow
)
