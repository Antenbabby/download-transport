package com.antennababy.download;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class CtrlController {
    @Autowired
    CtrlService ctrlService;

    @RequestMapping("submit")
    public Res submitTask(String url, HttpServletRequest httpServletRequest){
        try {
            Assert.notEmpty(url);
            String ret = ctrlService.submitTask(httpServletRequest,url);
            return Res.success(ret);
        } catch (Exception e) {
            log.error("新建任务出错!",e);
            return Res.fail("新建任务出错."+e.getMessage());
        }
    }
    @RequestMapping("list")
    public Res list(ListQueryReq req){
        try {
            if(req==null){
                req=new ListQueryReq();
                req.setPageIndex(0);
            }
            List<DownLoadLog> list = ctrlService.list(Wrappers.<DownLoadLog>lambdaQuery()
                    .like(StrUtil.isNotEmpty(req.getKeyWords()), DownLoadLog::getFileName, req.getKeyWords()).or().like(StrUtil.isNotEmpty(req.getKeyWords()), DownLoadLog::getOrginUrl, req.getKeyWords())
                    .orderByDesc(DownLoadLog::getSubmitDate)
                    .last(StrUtil.format(" limit {},10", req.getPageIndex()==null?0:req.getPageIndex())));
            return Res.success(list);
        } catch (Exception e) {
            log.error("操作出错!",e);
            return Res.fail("操作出错.");
        }
    }
    @RequestMapping("get")
    public Res list(Long id){
        try {
            Assert.notNull(id);
            DownLoadLog ret = ctrlService.getById(id);
            return Res.success(ret);
        } catch (Exception e) {
            log.error("操作出错!",e);
            return Res.fail("操作出错.");
        }
    }
}
