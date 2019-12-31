package com.seater.smartmining.entity;

import com.seater.smartmining.enums.PricingTypeEnums;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/3/6 0006 14:21
 */
public class ScheduledInfo /*extends ScheduledRequest*/{

    private List<ScheduledRequest> scheduledDiggingMachineList = new ArrayList<>();

    public List<ScheduledRequest> getScheduledDiggingMachineList() {
        return scheduledDiggingMachineList;
    }

    public void setScheduledDiggingMachineList(List<ScheduledRequest> scheduledDiggingMachineList) {
        this.scheduledDiggingMachineList = scheduledDiggingMachineList;
    }
}
