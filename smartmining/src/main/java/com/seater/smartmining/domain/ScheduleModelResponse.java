package com.seater.smartmining.domain;

import com.seater.smartmining.entity.ProjectScheduleModel;
import com.seater.smartmining.entity.ScheduleCarModel;
import com.seater.smartmining.entity.ScheduleMachineModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/11/15 0015 11:37
 */
@Data
public class ScheduleModelResponse {

    private ProjectScheduleModel projectScheduleModel;

    private List<ScheduleMachineModel> scheduleMachineList = new ArrayList<>();

    private List<ScheduleCarModel> scheduleCarList = new ArrayList<>();
}
