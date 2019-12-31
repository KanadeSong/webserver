package com.seater.smartmining.controller;

import com.seater.smartmining.utils.schedule.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;

import static com.seater.smartmining.utils.schedule.AutoScheduleUtilsKt.autoScheduleForDiggingMachine;
import static com.seater.smartmining.utils.schedule.AutoScheduleUtilsKt.autoScheduleForSlagSite;


@RestController
@RequestMapping("/api/autoSchedule")
public class AutoScheduleController {

    //挖机自动分配
    @RequestMapping("/autoScheduleForDiggingMachine")
    public Object autoScheduleForDiggingMachineFun(){
        String groupCode = "1234";//
        AutoScheduleInfo autoScheduleInfo = new AutoScheduleInfo();

        //上班信息
        WorkScheduleInfo workScheduleInfo = new WorkScheduleInfo();
        workScheduleInfo.setGroupCode(groupCode);
        workScheduleInfo.setStartTime(new Date(1567592951096L));//
        workScheduleInfo.setWaitTimeLong(1000*60*5L);//
        autoScheduleInfo.setWorkScheduleInfo(workScheduleInfo);

        //挖机信息
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo1 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo1.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo1.setProjectDiggingMachineId(1L);//
        projectDiggingMachineScheduleInfo1.setDistance(100);//
        projectDiggingMachineScheduleInfo1.setIntervalTimeLong(1000*60*2L);//
        projectDiggingMachineScheduleInfo1.setLastScheduleTime(new Date(1567592951096L));//
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo2 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo2.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo2.setProjectDiggingMachineId(2L);//
        projectDiggingMachineScheduleInfo2.setDistance(100);//
        projectDiggingMachineScheduleInfo2.setIntervalTimeLong(1000*60L);//
        projectDiggingMachineScheduleInfo2.setLastScheduleTime(new Date((new Date()).getTime() - 1000));//
        ArrayList<ProjectDiggingMachineScheduleInfo> projectDiggingMachineScheduleInfoList = new ArrayList<>();
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo1);
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo2);
        autoScheduleInfo.setProjectDiggingMachineScheduleInfoList(projectDiggingMachineScheduleInfoList);

        //渣车信息
        ProjectCarScheduleInfo projectCarScheduleInfo1 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo1.setAutoScheduleType(AutoScheduleType.DiggingMachine);//
        projectCarScheduleInfo1.setGroupCode(groupCode);//
        projectCarScheduleInfo1.setDiggingMachineTime(new Date(1567592951096L));//
        projectCarScheduleInfo1.setProjectCarId(1L);//
        projectCarScheduleInfo1.setProjectDiggingMachineId(2L);//
        ProjectCarScheduleInfo projectCarScheduleInfo2 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo2.setAutoScheduleType(AutoScheduleType.WaitForDiggingMachineSchedule);//
        projectCarScheduleInfo2.setGroupCode(groupCode);//
        projectCarScheduleInfo2.setProjectCarId(2L);//
        ProjectCarScheduleInfo projectCarScheduleInfo3 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo3.setAutoScheduleType(AutoScheduleType.WaitForDiggingMachineSchedule);//
        projectCarScheduleInfo3.setGroupCode(groupCode);//
        projectCarScheduleInfo3.setProjectCarId(3L);//
        ProjectCarScheduleInfo projectCarScheduleInfo4 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo4.setAutoScheduleType(AutoScheduleType.WaitForDiggingMachineSchedule);//
        projectCarScheduleInfo4.setGroupCode(groupCode);//
        projectCarScheduleInfo4.setProjectCarId(4L);//
        ProjectCarScheduleInfo projectCarScheduleInfo5 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo5.setAutoScheduleType(AutoScheduleType.WaitForDiggingMachineSchedule);//
        projectCarScheduleInfo5.setGroupCode(groupCode);//
        projectCarScheduleInfo5.setProjectCarId(5L);//
        ArrayList<ProjectCarScheduleInfo> projectCarScheduleInfoList = new ArrayList<>();
        projectCarScheduleInfoList.add(projectCarScheduleInfo1);
        projectCarScheduleInfoList.add(projectCarScheduleInfo2);
        projectCarScheduleInfoList.add(projectCarScheduleInfo3);
        projectCarScheduleInfoList.add(projectCarScheduleInfo4);
        projectCarScheduleInfoList.add(projectCarScheduleInfo5);
        autoScheduleInfo.setProjectCarScheduleInfoList(projectCarScheduleInfoList);

        return autoScheduleForDiggingMachine(autoScheduleInfo);
    }

    //渣场自动分配
    @RequestMapping("/autoScheduleForSlagSite")
    public Object autoScheduleForSlagSiteFun(){
        String groupCode = "1234";//
        AutoScheduleInfo autoScheduleInfo = new AutoScheduleInfo();

        //挖机信息
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo1 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo1.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo1.setProjectDiggingMachineId(1L);//
        projectDiggingMachineScheduleInfo1.setDistance(100);//
        projectDiggingMachineScheduleInfo1.setIntervalTimeLong(1000*60*2L);//
        projectDiggingMachineScheduleInfo1.setLastScheduleTime(new Date(1567592951096L));//
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo2 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo2.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo2.setProjectDiggingMachineId(2L);//
        projectDiggingMachineScheduleInfo2.setDistance(10);//
        projectDiggingMachineScheduleInfo2.setIntervalTimeLong(1000*60L);//
        projectDiggingMachineScheduleInfo2.setLastScheduleTime(new Date((new Date()).getTime() - 1000));//
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo3 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo3.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo3.setProjectDiggingMachineId(3L);//
        projectDiggingMachineScheduleInfo3.setDistance(100);//
        projectDiggingMachineScheduleInfo3.setIntervalTimeLong(1000*60*2L);//
        projectDiggingMachineScheduleInfo3.setLastScheduleTime(new Date(1567592951096L));//
        ProjectDiggingMachineScheduleInfo projectDiggingMachineScheduleInfo4 = new ProjectDiggingMachineScheduleInfo();
        projectDiggingMachineScheduleInfo4.setGroupCode(groupCode);//
        projectDiggingMachineScheduleInfo4.setProjectDiggingMachineId(4L);//
        projectDiggingMachineScheduleInfo4.setDistance(100);//
        projectDiggingMachineScheduleInfo4.setIntervalTimeLong(1000*60*2L);//
        projectDiggingMachineScheduleInfo4.setLastScheduleTime(new Date(1567592951096L));//
        ArrayList<ProjectDiggingMachineScheduleInfo> projectDiggingMachineScheduleInfoList = new ArrayList<>();
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo1);
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo2);
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo3);
        projectDiggingMachineScheduleInfoList.add(projectDiggingMachineScheduleInfo4);
        autoScheduleInfo.setProjectDiggingMachineScheduleInfoList(projectDiggingMachineScheduleInfoList);

        //渣车信息
        ProjectCarScheduleInfo projectCarScheduleInfo1 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo1.setAutoScheduleType(AutoScheduleType.SlagSite);//
        projectCarScheduleInfo1.setGroupCode(groupCode);//
        projectCarScheduleInfo1.setDiggingMachineTime(new Date(1567592951096L));//
        projectCarScheduleInfo1.setProjectCarId(1L);//
        projectCarScheduleInfo1.setProjectDiggingMachineId(2L);//
        ProjectCarScheduleInfo projectCarScheduleInfo2 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo2.setAutoScheduleType(AutoScheduleType.WaitForSlagSiteSchedule);//
        projectCarScheduleInfo2.setGroupCode(groupCode);//
        projectCarScheduleInfo2.setProjectCarId(2L);//
        projectCarScheduleInfo2.setProjectDiggingMachineId(1L);//
        projectCarScheduleInfo2.setDiggingMachineTime(new Date(1567592951096L));//
        ProjectCarScheduleInfo projectCarScheduleInfo3 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo3.setAutoScheduleType(AutoScheduleType.WaitForSlagSiteSchedule);//
        projectCarScheduleInfo3.setGroupCode(groupCode);//
        projectCarScheduleInfo3.setProjectCarId(3L);//
        projectCarScheduleInfo3.setProjectDiggingMachineId(2L);//
        projectCarScheduleInfo3.setDiggingMachineTime(new Date(1567592951096L));//
        ProjectCarScheduleInfo projectCarScheduleInfo4 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo4.setAutoScheduleType(AutoScheduleType.WaitForSlagSiteSchedule);//
        projectCarScheduleInfo4.setGroupCode(groupCode);//
        projectCarScheduleInfo4.setProjectCarId(4L);//
        projectCarScheduleInfo4.setProjectDiggingMachineId(3L);//
        projectCarScheduleInfo4.setDiggingMachineTime(new Date(1567592951096L));//
        ProjectCarScheduleInfo projectCarScheduleInfo5 = new  ProjectCarScheduleInfo();
        projectCarScheduleInfo5.setAutoScheduleType(AutoScheduleType.WaitForSlagSiteSchedule);//
        projectCarScheduleInfo5.setGroupCode(groupCode);//
        projectCarScheduleInfo5.setProjectCarId(5L);//
        projectCarScheduleInfo5.setProjectDiggingMachineId(4L);//
        projectCarScheduleInfo5.setDiggingMachineTime(new Date(1567592951096L));//
        ArrayList<ProjectCarScheduleInfo> projectCarScheduleInfoList = new ArrayList<>();
        projectCarScheduleInfoList.add(projectCarScheduleInfo1);
        projectCarScheduleInfoList.add(projectCarScheduleInfo2);
        projectCarScheduleInfoList.add(projectCarScheduleInfo3);
        projectCarScheduleInfoList.add(projectCarScheduleInfo4);
        projectCarScheduleInfoList.add(projectCarScheduleInfo5);
        autoScheduleInfo.setProjectCarScheduleInfoList(projectCarScheduleInfoList);

        //渣场信息
        ProjectSlagSiteScheduleInfo projectSlagSiteScheduleInfo1 = new ProjectSlagSiteScheduleInfo();
        projectSlagSiteScheduleInfo1.setSlagSiteId(1L);//
        projectSlagSiteScheduleInfo1.setLastScheduleTime(new Date(1567592951096L));//
        projectSlagSiteScheduleInfo1.setIntervalTimeLong(1000*60*2L);//
        projectSlagSiteScheduleInfo1.setDistance(1000);//
        ProjectSlagSiteScheduleInfo projectSlagSiteScheduleInfo2 = new ProjectSlagSiteScheduleInfo();
        projectSlagSiteScheduleInfo2.setSlagSiteId(2L);//
        projectSlagSiteScheduleInfo2.setLastScheduleTime(new Date());//
        projectSlagSiteScheduleInfo2.setIntervalTimeLong(1000*60*2L);//
        projectSlagSiteScheduleInfo2.setDistance(500);//
        ArrayList<ProjectSlagSiteScheduleInfo> projectSlagSiteScheduleInfoList = new ArrayList<>();
        projectSlagSiteScheduleInfoList.add(projectSlagSiteScheduleInfo1);
        projectSlagSiteScheduleInfoList.add(projectSlagSiteScheduleInfo2);
        autoScheduleInfo.setProjectSlagSiteScheduleInfoList(projectSlagSiteScheduleInfoList);

        return autoScheduleForSlagSite(autoScheduleInfo);
    }
}
