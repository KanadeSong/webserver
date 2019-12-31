package com.seater.smartmining.mqtt.domain;

import lombok.Data;

/**
 * @Description 司机端app登陆返回payload
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/9/27 10:36
 */
@Data
public class LoginReply {
    private String cmdInd = "";
    private String carCode = "";
    private Long projectId = 0L;
    private Long carId = 0L;
    private String password = "";
    /**
     * 命令状态，0-成功，其他-失败
     */
    private Integer cmdStatus = 0;

}
