package com.seater.smartmining.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.entity.*;
import com.seater.smartmining.enums.ShiftsEnums;
import com.seater.smartmining.service.*;
import com.seater.smartmining.utils.params.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/projectCarFillLogCal")
public class ProjectCarFillLogCalController {
    @Autowired
    ProjectCarFillLogServiceI projectCarFillLogServiceI;

    @Autowired
    ProjectCarFillMeterReadingLogServiceI projectCarFillMeterReadingLogServiceI;

    @Autowired
    ProjectServiceI projectServiceI;

    @PostMapping("/cal")
    public Object cal(Date startDate, Date endDate, @RequestParam(required = true) Long warningVolumeInTime, HttpServletRequest request) {
        try {
            Long projectId = Long.parseLong(request.getHeader("projectId"));

            Project project = projectServiceI.get(projectId);
            //  返回结果
            List<OilCarDayReportPartCar> oilCarDayReportPartCarList = new ArrayList<>();


            Set<Long> oilCarIdList = new HashSet<>();
            Set<String> oilCarCodeList = new HashSet<>();
            //  返回的key值


            // 1.根据时间段查出加油历史
            Specification<ProjectCarFillLog> spec = new Specification<ProjectCarFillLog>() {
                List<Predicate> list = new ArrayList<Predicate>();

                @Override
                public Predicate toPredicate(Root<ProjectCarFillLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                    list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));

                    if (!ObjectUtils.isEmpty(projectId)) {
                        list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                    }
                    query.orderBy(cb.desc(root.get("id").as(Long.class)));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            List<ProjectCarFillLog> projectCarFillLogList = projectCarFillLogServiceI.queryWx(spec);


            //  1.1 去重拿出每辆油车
            for (ProjectCarFillLog projectCarFillLog : projectCarFillLogList) {
                oilCarIdList.add(projectCarFillLog.getOilCarId());
                oilCarCodeList.add(projectCarFillLog.getOilCarCode());
            }

            //  算差异值和各种合计
            for (Long oilCarId : oilCarIdList) {
                OilCarDayReportPartCar oilCarDayReportPartCar = new OilCarDayReportPartCar();
                oilCarDayReportPartCar.setWarningVolumeInTime(warningVolumeInTime);
                //  加油历史
                for (ProjectCarFillLog projectCarFillLog : projectCarFillLogList) {
                    if (!ObjectUtils.isEmpty(projectCarFillLog.getManagerId()) && null != projectCarFillLog.getOilCarId() && projectCarFillLog.getOilCarId().equals(oilCarId)) {
                        // 累加得出该车时间段的加油总量(单位转换为升)
                        oilCarDayReportPartCar.setTotalVolumeInTime(oilCarDayReportPartCar.getTotalVolumeInTime() + projectCarFillLog.getVolumn());

                        oilCarDayReportPartCar.setOilCarId(projectCarFillLog.getOilCarId());
                        oilCarDayReportPartCar.setOilCarCode(projectCarFillLog.getOilCarCode());
                        oilCarDayReportPartCar.setManagerId(projectCarFillLog.getManagerId());
                        oilCarDayReportPartCar.setManagerName(projectCarFillLog.getManagerName());
                        oilCarDayReportPartCar.setProjectId(projectCarFillLog.getProjectId());

                        //  分开班次
                        //  如果没有班次,就根据加油时间设定为未知班次
                        if (null == projectCarFillLog.getShift()) {
                            projectCarFillLog.setShift(ShiftsEnums.UNKNOW);
                        }
                        if (projectCarFillLog.getShift().equals(ShiftsEnums.DAYSHIFT)) {
                            oilCarDayReportPartCar.setEarlyTotalVolume(oilCarDayReportPartCar.getEarlyTotalVolume() + projectCarFillLog.getVolumn());
                        }
                        if (projectCarFillLog.getShift().equals(ShiftsEnums.BLACKSHIFT)) {
                            oilCarDayReportPartCar.setNightTotalVolume(oilCarDayReportPartCar.getNightTotalVolume() + projectCarFillLog.getVolumn());
                        }

                    }

                }

                //  每车时间段内的抄表历史
                Specification<ProjectCarFillMeterReadingLog> specRead = new Specification<ProjectCarFillMeterReadingLog>() {
                    List<Predicate> list = new ArrayList<Predicate>();

                    @Override
                    public Predicate toPredicate(Root<ProjectCarFillMeterReadingLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                        list.add(cb.between(root.get("dateIdentification").as(Date.class), DateUtil.beginOfDay(startDate), DateUtil.endOfDay(endDate)));
                        list.add(cb.equal(root.get("oilCarId").as(Long.class), oilCarId));
                        if (!ObjectUtils.isEmpty(projectId)) {
                            list.add(cb.equal(root.get("projectId").as(Long.class), Long.parseLong(request.getHeader("projectId"))));
                        }
                        query.orderBy(cb.desc(root.get("id").as(Long.class)));
                        return cb.and(list.toArray(new Predicate[list.size()]));
                    }
                };
                List<ProjectCarFillMeterReadingLog> readingLogList = projectCarFillMeterReadingLogServiceI.queryWx(specRead);
                for (ProjectCarFillMeterReadingLog readingLog : readingLogList) {
                    oilCarDayReportPartCar.setTotalReadVolumeInTime(oilCarDayReportPartCar.getTotalReadVolumeInTime() + readingLog.getOilMeterTodayTotal());
                }
//                System.out.println(JSONObject.toJSONString(readingLogList));
                if (Math.abs(oilCarDayReportPartCar.getTotalReadVolumeInTime() - oilCarDayReportPartCar.getTotalVolumeInTime()) > warningVolumeInTime) {
                    oilCarDayReportPartCar.setIsOver(true);
                    oilCarDayReportPartCar.setCompareVolumeInTime(Math.abs(oilCarDayReportPartCar.getTotalReadVolumeInTime() - oilCarDayReportPartCar.getTotalVolumeInTime()));
                }

                //  总金额
                oilCarDayReportPartCar.setTotalAmountInTime(oilCarDayReportPartCar.getTotalVolumeInTime() * project.getOilPirce());
                //  早班加油总金额
                oilCarDayReportPartCar.setEarlyTotalAmount(oilCarDayReportPartCar.getEarlyTotalVolume() * project.getOilPirce());
                //  晚班加油总金额
                oilCarDayReportPartCar.setNightTotalAmount(oilCarDayReportPartCar.getNightTotalVolume() * project.getOilPirce());

                oilCarDayReportPartCarList.add(oilCarDayReportPartCar);
            }
            return oilCarDayReportPartCarList;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
}
