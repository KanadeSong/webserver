package com.seater.smartmining.domain;

import com.alibaba.fastjson.JSON;
import com.seater.smartmining.enums.PricingTypeEnums;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/4/25 0025 16:11
 */
@Data
public class WorkTimeModifyByMachine {

    private Integer status = 0;
    private Long machineId = 0L;
    private PricingTypeEnums pricingType = PricingTypeEnums.Unknow;


    public static void main(String[] args){
        List<WorkTimeModifyByMachine> machineList = new ArrayList<>();
        WorkTimeModifyByMachine machine = new WorkTimeModifyByMachine();
        machine.setMachineId(2L);
        machine.setStatus(1);
        machine.setPricingType(PricingTypeEnums.Hour);
        machineList.add(machine);
        System.out.println(JSON.toJSONString(machineList));
    }
}
