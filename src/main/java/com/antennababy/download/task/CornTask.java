package com.antennababy.download.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.antennababy.download.app.DownLoadLog;
import com.antennababy.download.app.DownLoadLogMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class CornTask {
    //任务开关
    @Value("${delete_task_switch:true}")
    String deleteTaskSwitch;
    //删除几天前的文件
    @Value("${delete_task_offset:7}")
    String deleteTaskOffset;

    @Autowired
    DownLoadLogMapper downLoadLogMapper;
    @Value("${file.config.download:}")
    String DOWNLOAD_PATH;

    //每天凌晨1点执行
    @Scheduled(cron = "0 0 1 * * ? ")
//    @Scheduled(cron = "0/10 * * * * ? ")
    public void testScheduleTask() {
        if(!"true".equalsIgnoreCase(deleteTaskSwitch)){
            return;
        }
        log.info("定时任务删除文件任务启动.");
        try {
            List<DownLoadLog> downLoadLogs = downLoadLogMapper.selectList(Wrappers.<DownLoadLog>lambdaQuery()
                    .lt(DownLoadLog::getSubmitDate, DateUtil.offsetDay(new Date(), Integer.parseInt(deleteTaskOffset) * -1)));
            downLoadLogs.forEach(x -> {
                downLoadLogMapper.deleteById(x.getId());
                if (StrUtil.isNotEmpty(x.getLocalUrl())) {
                    log.info(DOWNLOAD_PATH + "/" + x.getLocalUrl());
                    File file = FileUtil.file(DOWNLOAD_PATH + "/" + x.getLocalUrl());
                    file.delete();
                }
            });
            log.info("定时任务删除文件任务成功.共删除{}条",downLoadLogs.size());
        } catch (Exception e) {
            log.error("定时删除文件任务出错.",e);
        }
    }
}
