package com.seater.smartmining.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ErrorHandler;

/**
 * @Description TODO
 * @Author by xueqichang
 * @Email 1369521908@qq.com
 * @Date 2019/7/12 12:48
 */
@Slf4j
public class ScheduleErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
        t.printStackTrace();
        log.error("自动任务执行出错...");
        log.error("异常信息:{}" + t.getMessage());
    }
}
