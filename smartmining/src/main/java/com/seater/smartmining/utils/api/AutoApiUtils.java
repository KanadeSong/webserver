package com.seater.smartmining.utils.api;

import com.seater.smartmining.report.ReportService;
import com.seater.smartmining.report.WorkDateService;
import com.seater.smartmining.service.*;
import com.seater.user.service.SysUserProjectRoleServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author zenghang
 * @Email 87167070@qq.com
 * @Date 2019/1/31 0031 18:24
 */
@Component
public class AutoApiUtils {

    @Autowired
    ProjectServiceI projectServiceI;
    @Autowired
    ProjectCarWorkInfoServiceI projectCarWorkInfoServiceI;
    @Autowired
    ProjectDayReportServiceI projectDayReportServiceI;
    @Autowired
    ProjectDayReportPartCarServiceI projectDayReportPartCarServiceI;
    @Autowired
    ProjectCarFillLogServiceI projectCarFillLogServiceI;
    @Autowired
    ProjectDayReportPartDistanceServiceI projectDayReportPartDistanceServiceI;
    @Autowired
    ProjectCarServiceI projectCarServiceI;
    @Autowired
    ProjectDiggingMachineServiceI projectDiggingMachineServiceI;
    @Autowired
    ProjectDiggingDayReportTotalServiceI projectDiggingDayReportTotalServiceI;
    @Autowired
    ProjectDiggingMachineMaterialServiceI projectDiggingMachineMaterialServiceI;
    @Autowired
    ProjectHourPriceServiceI projectHourPriceServiceI;
    @Autowired
    ProjectWorkTimeByDiggingServiceI projectWorkTimeByDiggingServiceI;
    @Autowired
    ProjectDiggingDayReportServiceI projectDiggingDayReportServiceI;
    @Autowired
    ProjectMaterialServiceI projectMaterialServiceI;
    @Autowired
    ProjectMonthReportServiceI projectMonthReportServiceI;
    @Autowired
    ProjectMonthReportTotalServiceI projectMonthReportTotalServiceI;
    @Autowired
    ProjectDiggingMonthReportServiceI projectDiggingMonthReportServiceI;
    @Autowired
    ProjectDigginggMonthReportTotalServiceI projectDigginggMonthReportTotalServiceI;
    @Autowired
    WorkDateService workDateService;
    @Autowired
    ProjectDiggingPartCountTotalServiceI projectDiggingPartCountTotalServiceI;
    @Autowired
    ProjectDiggingPartCountServiceI projectDiggingPartCountServiceI;
    @Autowired
    ProjectDiggingPartCountGrandServiceI projectDiggingPartCountGrandServiceI;
    @Autowired
    ProjectCarMaterialServiceI projectCarMaterialServiceI;
    @Autowired
    ProjectSettlementDetailServiceI projectSettlementDetailServiceI;
    @Autowired
    ProjectSettlementSummaryServiceI projectSettlementSummaryServiceI;
    @Autowired
    ProjectSettlementTotalServiceI projectSettlementTotalServiceI;
    @Autowired
    ProjectCubicDetailServiceI projectCubicDetailServiceI;
    @Autowired
    ProjectCubicDetailTotalServiceI projectCubicDetailTotalServiceI;
    @Autowired
    ProjectCubicDetailElseServiceI projectCubicDetailElseServiceI;
    @Autowired
    ReportService reportService;
    @Autowired
    ProjectCarFillMeterReadingLogServiceI projectCarFillMeterReadingLogServiceI;
    @Autowired
    ProjectOtherDeviceServiceI projectOtherDeviceServiceI;
    @Autowired
    DeductionDiggingServiceI deductionDiggingServiceI;
    @Autowired
    DeductionDiggingByMonthServiceI deductionDiggingByMonthServiceI;
    @Autowired
    DeductionBySettlementSummaryServiceI deductionBySettlementSummaryServiceI;
    @Autowired
    private ProjectScheduleServiceI projectScheduleServiceI;
    @Autowired
    private ScheduleMachineServiceI scheduleMachineServiceI;
    @Autowired
    private ScheduleCarServiceI scheduleCarServiceI;
    @Autowired
    private MatchingDegreeServiceI matchingDegreeServiceI;
    @Autowired
    private ProjectUnloadLogServiceI projectUnloadLogServiceI;
    @Autowired
    private ProjectLoadLogServiceI projectLoadLogServiceI;
    @Autowired
    private ProjectSlagSiteServiceI projectSlagSiteServiceI;
    @Autowired
    private ProjectDayReportHistoryServiceI projectDayReportHistoryServiceI;
    @Autowired
    private ProjectDiggingDayReportHistoryServiceI projectDiggingDayReportHistoryServiceI;
    @Autowired
    private ProjectCheckLogServiceI projectCheckLogServiceI;
    @Autowired
    private ProjectScheduleLogServiceI projectScheduleLogServiceI;
    @Autowired
    private ReportPublishServiceI reportPublishServiceI;
    @Autowired
    private ProjectSlagSiteCarReportServiceI projectSlagSiteCarReportServiceI;
    @Autowired
    private ProjectDiggingReportByPlaceServiceI projectDiggingReportByPlaceServiceI;
    @Autowired
    private ProjectDiggingReportByMaterialServiceI projectDiggingReportByMaterialServiceI;
    @Autowired
    private ProjectSlagCarLogServiceI projectSlagCarLogServiceI;
    @Autowired
    private SysUserProjectRoleServiceI sysUserProjectRoleServiceI;
    @Autowired
    private ProjectOtherDeviceWorkInfoServiceI projectOtherDeviceWorkInfoServiceI;
    @Autowired
    private ProjectErrorLoadLogServiceI projectErrorLoadLogServiceI;
    @Autowired
    private ProjectMqttCardReportServiceI projectMqttCardReportServiceI;
    @Autowired
    private ProjectMqttCardCountReportServiceI projectMqttCardCountReportServiceI;
    @Autowired
    private ProjectScheduleModelServiceI projectScheduleModelServiceI;
    @Autowired
    private ScheduleCarModelServiceI scheduleCarModelServiceI;
    @Autowired
    private ScheduleMachineModelServiceI scheduleMachineModelServiceI;
    @Autowired
    private ProjectProgrammeServiceI projectProgrammeServiceI;
    @Autowired
    private ProjectCarTotalCountReportServiceI projectCarTotalCountReportServiceI;
    @Autowired
    private ProjectCarTotalCountReportByTotalServiceI projectCarTotalCountReportByTotalServiceI;


    private static AutoApiUtils autoApiUtils;

    @PostConstruct
    public void init() {
        autoApiUtils = this;
        autoApiUtils.projectServiceI = projectServiceI;
        autoApiUtils.projectCarWorkInfoServiceI = projectCarWorkInfoServiceI;
        autoApiUtils.projectDayReportServiceI = projectDayReportServiceI;
        autoApiUtils.projectDayReportPartCarServiceI = projectDayReportPartCarServiceI;
        autoApiUtils.projectCarFillLogServiceI = projectCarFillLogServiceI;
        autoApiUtils.projectDayReportPartDistanceServiceI = projectDayReportPartDistanceServiceI;
        autoApiUtils.projectCarServiceI = projectCarServiceI;
        autoApiUtils.projectDiggingMachineServiceI = projectDiggingMachineServiceI;
        autoApiUtils.projectDiggingDayReportTotalServiceI = projectDiggingDayReportTotalServiceI;
        autoApiUtils.projectDiggingMachineMaterialServiceI = projectDiggingMachineMaterialServiceI;
        autoApiUtils.projectHourPriceServiceI = projectHourPriceServiceI;
        autoApiUtils.projectWorkTimeByDiggingServiceI = projectWorkTimeByDiggingServiceI;
        autoApiUtils.projectDiggingDayReportServiceI = projectDiggingDayReportServiceI;
        autoApiUtils.projectMonthReportServiceI = projectMonthReportServiceI;
        autoApiUtils.projectMonthReportTotalServiceI = projectMonthReportTotalServiceI;
        autoApiUtils.projectDiggingMonthReportServiceI = projectDiggingMonthReportServiceI;
        autoApiUtils.projectDigginggMonthReportTotalServiceI = projectDigginggMonthReportTotalServiceI;
        autoApiUtils.workDateService = workDateService;
        autoApiUtils.projectDiggingPartCountTotalServiceI = projectDiggingPartCountTotalServiceI;
        autoApiUtils.projectDiggingPartCountServiceI = projectDiggingPartCountServiceI;
        autoApiUtils.projectDiggingPartCountGrandServiceI = projectDiggingPartCountGrandServiceI;
        autoApiUtils.projectCarMaterialServiceI = projectCarMaterialServiceI;
        autoApiUtils.projectSettlementDetailServiceI = projectSettlementDetailServiceI;
        autoApiUtils.projectSettlementSummaryServiceI = projectSettlementSummaryServiceI;
        autoApiUtils.projectSettlementTotalServiceI = projectSettlementTotalServiceI;
        autoApiUtils.projectCubicDetailServiceI = projectCubicDetailServiceI;
        autoApiUtils.projectCubicDetailTotalServiceI = projectCubicDetailTotalServiceI;
        autoApiUtils.projectCubicDetailElseServiceI = projectCubicDetailElseServiceI;
        autoApiUtils.reportService = reportService;
        autoApiUtils.projectCarFillMeterReadingLogServiceI = projectCarFillMeterReadingLogServiceI;
        autoApiUtils.projectOtherDeviceServiceI = projectOtherDeviceServiceI;
        autoApiUtils.deductionDiggingServiceI = deductionDiggingServiceI;
        autoApiUtils.deductionDiggingByMonthServiceI = deductionDiggingByMonthServiceI;
        autoApiUtils.deductionBySettlementSummaryServiceI = deductionBySettlementSummaryServiceI;
        autoApiUtils.projectScheduleServiceI = projectScheduleServiceI;
        autoApiUtils.scheduleMachineServiceI = scheduleMachineServiceI;
        autoApiUtils.scheduleCarServiceI = scheduleCarServiceI;
        autoApiUtils.matchingDegreeServiceI = matchingDegreeServiceI;
        autoApiUtils.projectUnloadLogServiceI = projectUnloadLogServiceI;
        autoApiUtils.projectLoadLogServiceI = projectLoadLogServiceI;
        autoApiUtils.projectSlagSiteServiceI = projectSlagSiteServiceI;
        autoApiUtils.projectDayReportHistoryServiceI = projectDayReportHistoryServiceI;
        autoApiUtils.projectDiggingDayReportHistoryServiceI = projectDiggingDayReportHistoryServiceI;
        autoApiUtils.projectCheckLogServiceI = projectCheckLogServiceI;
        autoApiUtils.projectScheduleLogServiceI = projectScheduleLogServiceI;
        autoApiUtils.reportPublishServiceI = reportPublishServiceI;
        autoApiUtils.projectSlagSiteCarReportServiceI = projectSlagSiteCarReportServiceI;
        autoApiUtils.projectDiggingReportByPlaceServiceI = projectDiggingReportByPlaceServiceI;
        autoApiUtils.projectDiggingReportByMaterialServiceI = projectDiggingReportByMaterialServiceI;
        autoApiUtils.projectSlagCarLogServiceI = projectSlagCarLogServiceI;
        autoApiUtils.sysUserProjectRoleServiceI = sysUserProjectRoleServiceI;
        autoApiUtils.projectOtherDeviceWorkInfoServiceI = projectOtherDeviceWorkInfoServiceI;
        autoApiUtils.projectErrorLoadLogServiceI = projectErrorLoadLogServiceI;
        autoApiUtils.projectMqttCardReportServiceI = projectMqttCardReportServiceI;
        autoApiUtils.projectMqttCardCountReportServiceI = projectMqttCardCountReportServiceI;
        autoApiUtils.projectScheduleModelServiceI = projectScheduleModelServiceI;
        autoApiUtils.scheduleCarModelServiceI = scheduleCarModelServiceI;
        autoApiUtils.scheduleMachineModelServiceI = scheduleMachineModelServiceI;
        autoApiUtils.projectProgrammeServiceI = projectProgrammeServiceI;
        autoApiUtils.projectCarTotalCountReportServiceI = projectCarTotalCountReportServiceI;
        autoApiUtils.projectCarTotalCountReportByTotalServiceI = projectCarTotalCountReportByTotalServiceI;
    }

    public static ProjectServiceI returnProject(){
        return autoApiUtils.projectServiceI;
    }

    public static ProjectCarWorkInfoServiceI returnProjectCarWork(){
        return autoApiUtils.projectCarWorkInfoServiceI;
    }

    public static ProjectDayReportServiceI returnDayReport(){
        return autoApiUtils.projectDayReportServiceI;
    }

    public static ProjectDayReportPartCarServiceI returnDayReportCar(){
        return autoApiUtils.projectDayReportPartCarServiceI;
    }

    public static ProjectCarFillLogServiceI returnProjectCarFill(){
        return autoApiUtils.projectCarFillLogServiceI;
    }

    public static ProjectDayReportPartDistanceServiceI returnDayReportDistance(){
        return autoApiUtils.projectDayReportPartDistanceServiceI;
    }

    public static ProjectCarServiceI returnProjectCar(){
        return autoApiUtils.projectCarServiceI;
    }

    public static ProjectDiggingMachineServiceI returnProjectDiggingMachine(){
        return autoApiUtils.projectDiggingMachineServiceI;
    }

    public static ProjectDiggingDayReportTotalServiceI returnProjectDiggingDayReportTotal(){
        return autoApiUtils.projectDiggingDayReportTotalServiceI;
    }

    public static ProjectDiggingMachineMaterialServiceI returnProjectDiggingMachineMaterial(){
        return autoApiUtils.projectDiggingMachineMaterialServiceI;
    }

    public static ProjectHourPriceServiceI returnProjectHourPrice(){
        return autoApiUtils.projectHourPriceServiceI;
    }

    public static ProjectWorkTimeByDiggingServiceI returnProjectWorkTimeByDigging(){
        return autoApiUtils.projectWorkTimeByDiggingServiceI;
    }

    public static ProjectDiggingDayReportServiceI returnProjectDiggingDayReport(){
        return autoApiUtils.projectDiggingDayReportServiceI;
    }

    public static ProjectMaterialServiceI returnProjectMaterial(){
        return autoApiUtils.projectMaterialServiceI;
    }

    public static ProjectMonthReportServiceI returnProjectMonthReport(){
        return autoApiUtils.projectMonthReportServiceI;
    }

    public static ProjectMonthReportTotalServiceI returnProjectMonthReportTotal(){
        return autoApiUtils.projectMonthReportTotalServiceI;
    }

    public static ProjectDiggingMonthReportServiceI returnProjectDiggingMonthReport(){
        return autoApiUtils.projectDiggingMonthReportServiceI;
    }

    public static ProjectDigginggMonthReportTotalServiceI returnProjectDigginggMonthReportTotal(){
        return autoApiUtils.projectDigginggMonthReportTotalServiceI;
    }

    public static WorkDateService returnWorkDate(){
        return autoApiUtils.workDateService;
    }

    public static ProjectDiggingPartCountTotalServiceI returnProjectDiggingPartCountTotal(){
        return autoApiUtils.projectDiggingPartCountTotalServiceI;
    }

    public static ProjectDiggingPartCountServiceI returnProjectDiggingPartCount(){
        return autoApiUtils.projectDiggingPartCountServiceI;
    }

    public static ProjectDiggingPartCountGrandServiceI returnProjectDiggingPartCountGrand(){
        return autoApiUtils.projectDiggingPartCountGrandServiceI;
    }

    public static ProjectCarMaterialServiceI returnProjectCarMaterial(){
        return autoApiUtils.projectCarMaterialServiceI;
    }

    public static ProjectSettlementDetailServiceI returnProjectSettlementDetail(){
        return autoApiUtils.projectSettlementDetailServiceI;
    }

    public static ProjectSettlementSummaryServiceI returnProjectSettlementSummary(){
        return autoApiUtils.projectSettlementSummaryServiceI;
    }

    public static ProjectSettlementTotalServiceI returnProjectSettlementTotal(){
        return autoApiUtils.projectSettlementTotalServiceI;
    }

    public static ProjectCubicDetailServiceI returnProjectCubicDetail(){
        return autoApiUtils.projectCubicDetailServiceI;
    }

    public static ProjectCubicDetailTotalServiceI returnProjectCubicDetailTotal(){
        return autoApiUtils.projectCubicDetailTotalServiceI;
    }

    public static ProjectCubicDetailElseServiceI returnProjectCubicDetailElse(){
        return  autoApiUtils.projectCubicDetailElseServiceI;
    }

    public static ReportService returnReport(){
        return autoApiUtils.reportService;
    }

    public static ProjectCarFillMeterReadingLogServiceI returnProjectCarFillMeterReadingLogServiceI(){
        return autoApiUtils.projectCarFillMeterReadingLogServiceI;
    }
    public static ProjectOtherDeviceServiceI returnProjectOtherDeviceServiceI(){
        return autoApiUtils.projectOtherDeviceServiceI;
    }
    public static DeductionDiggingServiceI returnDeductionDigging(){
        return autoApiUtils.deductionDiggingServiceI;
    }
    public static DeductionDiggingByMonthServiceI returnDeductionDiggingByMonth(){
        return autoApiUtils.deductionDiggingByMonthServiceI;
    }

    public static DeductionBySettlementSummaryServiceI returnDeductionBySettlementSummary(){
        return autoApiUtils.deductionBySettlementSummaryServiceI;
    }
    public static ProjectScheduleServiceI returnProjectSchedule(){
        return autoApiUtils.projectScheduleServiceI;
    }

    public static ScheduleMachineServiceI returnScheduleMachine(){
        return autoApiUtils.scheduleMachineServiceI;
    }

    public static ScheduleCarServiceI returnScheduleCar(){
        return autoApiUtils.scheduleCarServiceI;
    }

    public static MatchingDegreeServiceI returnMatchDegree(){
        return autoApiUtils.matchingDegreeServiceI;
    }

    public static ProjectUnloadLogServiceI returnProjectUnload(){
        return autoApiUtils.projectUnloadLogServiceI;
    }

    public static ProjectLoadLogServiceI returnProjectLoad(){
        return autoApiUtils.projectLoadLogServiceI;
    }

    public static ProjectSlagSiteServiceI returnProjectSlagSite(){
        return autoApiUtils.projectSlagSiteServiceI;
    }

    public static ProjectDayReportHistoryServiceI returnProjectDayReportHistory(){
        return autoApiUtils.projectDayReportHistoryServiceI;
    }

    public static ProjectDiggingDayReportHistoryServiceI returnProjectDiggingDayReportHistory(){
        return autoApiUtils.projectDiggingDayReportHistoryServiceI;
    }

    public static ProjectCheckLogServiceI returnProjectCheckLog(){
        return autoApiUtils.projectCheckLogServiceI;
    }

    public static ProjectScheduleLogServiceI returnProjectScheduleLog(){
        return autoApiUtils.projectScheduleLogServiceI;
    }

    public static ReportPublishServiceI returnReportPublishService() {
        return autoApiUtils.reportPublishServiceI;
    }

    public static ProjectSlagSiteCarReportServiceI returnProjectSlagSiteCar(){
        return autoApiUtils.projectSlagSiteCarReportServiceI;
    }

    public static ProjectDiggingReportByPlaceServiceI returnProjectDiggingReportByPlace(){
        return autoApiUtils.projectDiggingReportByPlaceServiceI;
    }

    public static ProjectDiggingReportByMaterialServiceI returnProjectDiggingReportByMaterial(){
        return autoApiUtils.projectDiggingReportByMaterialServiceI;
    }

    public static ProjectSlagCarLogServiceI returnProjectSlagCarLog(){
        return autoApiUtils.projectSlagCarLogServiceI;
    }

    public static SysUserProjectRoleServiceI returnSysUserProjectRoleServiceI() {
        return autoApiUtils.sysUserProjectRoleServiceI;
    }

    public static ProjectOtherDeviceWorkInfoServiceI returnProjectOtherDeviceWorkInfoServiceI(){
        return autoApiUtils.projectOtherDeviceWorkInfoServiceI;
    }

    public static ProjectErrorLoadLogServiceI returnProjectErrorLoadLogServiceI(){
        return autoApiUtils.projectErrorLoadLogServiceI;
    }

    public static ProjectMqttCardReportServiceI returnProjectMqttCardReportServiceI(){
        return autoApiUtils.projectMqttCardReportServiceI;
    }

    public static ProjectMqttCardCountReportServiceI returnProjectMqttCardCountReportServiceI(){
        return autoApiUtils.projectMqttCardCountReportServiceI;
    }

    public static ProjectScheduleModelServiceI returnProjectScheduleModelServiceI(){
        return autoApiUtils.projectScheduleModelServiceI;
    }

    public static ScheduleMachineModelServiceI returnScheduleMachineModelServiceI(){
        return autoApiUtils.scheduleMachineModelServiceI;
    }

    public static ScheduleCarModelServiceI returnScheduleCarModelServiceI(){
        return autoApiUtils.scheduleCarModelServiceI;
    }

    public static ProjectProgrammeServiceI returnProjectProgrammeServiceI(){
        return autoApiUtils.projectProgrammeServiceI;
    }

    public static ProjectCarTotalCountReportServiceI returnProjectCarTotalCountReportServiceI(){
        return autoApiUtils.projectCarTotalCountReportServiceI;
    }

    public static ProjectCarTotalCountReportByTotalServiceI returnProjectCarTotalCountReportByTotalServiceI(){
        return autoApiUtils.projectCarTotalCountReportByTotalServiceI;
    }
}
