package com.seater.smartmining.domain;

import com.seater.smartmining.entity.ProjectSchedule;
import com.seater.smartmining.entity.ScheduleCar;
import com.seater.smartmining.entity.ScheduleMachine;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/5/24 0024 11:10
 */
@Data
public class ScheduleResponse {

    private ProjectSchedule projectSchedule;

    private List<ScheduleMachine> scheduleMachineList = new ArrayList<>();

    private List<ScheduleCar> scheduleCarList = new ArrayList<>();
}
