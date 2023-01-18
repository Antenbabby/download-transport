package com.antennababy.download;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CtrlService  extends ServiceImpl<DownLoadLogMapper, DownLoadLog> {
    public final static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2+1);
    private static final AtomicInteger taskSize=new AtomicInteger(0);
    private static final int maxTaskSize=10;

    @Resource
    DownLoadLogMapper downLoadLogMapper;

    @Value("${file.config.download:}")
    public String DOWNLOAD_PATH;
    @Value("${file.config.checkIsFirstDownLoad: true}")
    private boolean checkIsFirstDownLoad;


    //状态 0下载中,1 完成,2 出错
    public  String  submitTask(HttpServletRequest httpServletRequest,String url){
        //查询30天内同地址的下载
        DownLoadLog downLoadLog = downLoadLogMapper.selectOne(Wrappers.<DownLoadLog>lambdaQuery().eq(DownLoadLog::getOrginUrl, url).orderByDesc(DownLoadLog::getSubmitDate).last(" limit 1 "));
        if(taskSize.get()>=maxTaskSize){
            throw new RuntimeException( "任务已满,请稍后提交.");
        }else {
            if(downLoadLog!=null){
                if ("0".equals(downLoadLog.getStatus())) {
                    throw new RuntimeException("任务正在下载中!");
                }else if(checkIsFirstDownLoad&&"1".equals(downLoadLog.getStatus())){
                    throw new RuntimeException("任务已存在!");
                }
            }
            Runnable task=()->{
                DownLoadLog downLoadLog1 = new DownLoadLog();
                try {
                    File file = FileUtil.file(url);
                    String fileName = FileNameUtil.getName(file);
                    downLoadLog1.setFileName(fileName);

                    downLoadLog1.setStatus("0");
                    downLoadLog1.setOrginUrl(url);
                    String localFileName = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_FORMATTER) + "_" + RandomUtil.randomString(2);
                    if (validateFileName(fileName)) {
                        localFileName=localFileName+ "_" + fileName;
                    }else {
                        downLoadLog1.setFileName(localFileName);
                    }
                    downLoadLog1.setLocalUrl(localFileName);
                    downLoadLog1.setUserAgent(httpServletRequest.getHeader(HttpHeaders.USER_AGENT));
                    downLoadLog1.setUserIp(IPUtil.getIPAddress(httpServletRequest));
                    downLoadLog1.setSubmitDate(new Date());
                    downLoadLogMapper.insert(downLoadLog1);

                    String cmd = StrUtil.format("aria2c -d {} -o {} {}", DOWNLOAD_PATH, localFileName, url);
                    log.info("CtrlUtil.submitTask cmd:{}", cmd);
                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec(cmd);

                    InputStream stderr = proc.getInputStream();
                    InputStreamReader isr = new InputStreamReader(stderr, StandardCharsets.UTF_8);
                    BufferedReader br = new BufferedReader(isr);
                    String line = "";
                    String lastRet = "";
                    while ((line = br.readLine()) != null) {
                        log.info(line);
                        lastRet = line;
                    }
                    if (lastRet.contains("OK")) {
                        log.info("下载完成!");
                        downLoadLog1.setStatus("1");
                        downLoadLog1.setCompleteDate(new Date());
                    } else {
                        log.info("下载失败!");
                        downLoadLog1.setStatus("2");
                    }
                } catch (Exception e) {
                    downLoadLog1.setStatus("2");
                    log.error("CtrlUtil.submitTask",e);
                }finally {
                    taskSize.decrementAndGet();
                    downLoadLogMapper.updateByPrimaryKey(downLoadLog1);
                }
            };
            taskSize.incrementAndGet();
            executorService.execute(task);
            return "任务已提交,请稍后刷新页面在历史中查看.";
        }
    }

    /**
     * 校验文件名
     * @param filename
     * @return
     */
    public static boolean validateFileName(String filename) {
        String REGEX_PATTERN = "^[A-za-z0-9.]{1,255}$";
        if (filename == null) {
            return false;
        }
        return filename.matches(REGEX_PATTERN);
    }
}
