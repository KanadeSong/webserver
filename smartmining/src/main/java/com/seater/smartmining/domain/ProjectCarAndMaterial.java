package com.seater.smartmining.domain;

import com.seater.smartmining.entity.ProjectCar;
import com.seater.smartmining.entity.ProjectCarLoadMaterialSet;
import lombok.Data;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/8/1 0001 10:12
 */
@Data
public class ProjectCarAndMaterial {

    private ProjectCar projectCar;

    private ProjectCarLoadMaterialSet projectCarLoadMaterialSet;
}
