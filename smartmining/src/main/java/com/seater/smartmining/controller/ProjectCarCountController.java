package com.seater.smartmining.controller;

import com.seater.smartmining.entity.CarType;
import com.seater.smartmining.entity.ProjectCarCount;
import com.seater.smartmining.entity.ProjectDiggingMachine;
import com.seater.smartmining.entity.Shift;
import com.seater.smartmining.service.ProjectCarCountServiceI;
import com.seater.smartmining.service.ProjectDiggingMachineServiceI;
import com.seater.smartmining.utils.date.DateUtils;
import com.seater.smartmining.utils.params.Result;
import com.seater.user.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/16 0016 17:51
 */
@RestController
@RequestMapping("/api/projectCarCount")
public class ProjectCarCountController extends BaseController{

    @Autowired
    private ProjectCarCountServiceI projectCarCountServiceI;
    @Autowired
    private ProjectDiggingMachineServiceI projectDiggingMachineServiceI;

    @RequestMapping("/recovery")
    public Result recoveryData(HttpServletRequest request, Date date, Shift shift, CarType carType) throws IOException {
        Long projectId = CommonUtil.getProjectId(request);
        List<ProjectDiggingMachine> projectDiggingMachineList = projectDiggingMachineServiceI.getByProjectIdOrderById(projectId);
        //生成索引
        Map<String, Integer> machineIndexMap = new HashMap<>();
        for(int i = 0; i < projectDiggingMachineList.size(); i++){
            machineIndexMap.put(projectDiggingMachineList.get(i).getCode(), i);
        }
        List<ProjectCarCount> projectCarCountList = projectCarCountServiceI.getAllByProjectIdAndDateIdentificationAndShiftsAndCarType(projectId, date, shift.getAlias(), carType.getValue());
        for(ProjectCarCount count : projectCarCountList){
            String machineCode = count.getCarCode();
            Integer index = machineIndexMap.get(machineCode);
            if(index == null)
                continue;
            ProjectDiggingMachine projectDiggingMachine = projectDiggingMachineList.get(index);
            Date startTime = projectDiggingMachine.getStartWorkTime();
            Date endTime = projectDiggingMachine.getEndWorkTime();
            Long workTime = 0L;
            if(startTime != null && startTime.getTime() != 0){
                if(endTime != null && endTime.getTime() != 0){
                    workTime = DateUtils.calculationHour(startTime, endTime);
                }else{
                    workTime = DateUtils.calculationHour(startTime, new Date());
                }
            }
            count.setWorkTime(workTime);
            projectCarCountServiceI.save(count);
        }
        return Result.ok();
    }
}
