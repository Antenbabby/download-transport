package com.antennababy.download;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class CtrlController {
    @Autowired
    CtrlService ctrlService;

    @Value("${file.config.download:}")
    public String DOWNLOAD_PATH;

    public static final int PAGE_SIZE=10;

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
                    .last(StrUtil.format(" limit {},{}", PAGE_SIZE*(req.getPageIndex()==null?0:req.getPageIndex()),PAGE_SIZE)));
            return Res.success(list);
        } catch (Exception e) {
            log.error("操作出错!",e);
            return Res.fail("操作出错.");
        }
    }
    @RequestMapping("get")
    public Res getById(Long id){
        try {
            Assert.notNull(id);
            DownLoadLog ret = ctrlService.getById(id);
            return Res.success(ret);
        } catch (Exception e) {
            log.error("操作出错!",e);
            return Res.fail("操作出错.");
        }
    }

    @RequestMapping("/fetch/{fileName}")
    public void download(@PathVariable String  fileName, HttpServletRequest request, HttpServletResponse response, @RequestHeader(required = false) String range) {
           Assert.notNull(fileName);
            //文件位置
            File music = new File(DOWNLOAD_PATH+"/"+fileName);


            //开始下载位置
            long startByte = 0;
            //结束下载位置
            long endByte = music.length() - 1;

            //有range的话
            if (range != null && range.contains("bytes=") && range.contains("-")) {
                //坑爹地方一：http状态码要为206
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

                range = range.substring(range.lastIndexOf("=") + 1).trim();
                String[] ranges = range.split("-");
                try {
                    //判断range的类型
                    if (ranges.length == 1) {
                        //类型一：bytes=-2343
                        if (range.startsWith("-")) {
                            endByte = Long.parseLong(ranges[0]);
                        }
                        //类型二：bytes=2343-
                        else if (range.endsWith("-")) {
                            startByte = Long.parseLong(ranges[0]);
                        }
                    }
                    //类型三：bytes=22-2343
                    else if (ranges.length == 2) {
                        startByte = Long.parseLong(ranges[0]);
                        endByte = Long.parseLong(ranges[1]);
                    }

                } catch (NumberFormatException e) {
                    startByte = 0;
                    endByte = music.length() - 1;
                }
            } else {
                //没有ranges即全部一次性传输，需要用200状态码，这一行应该可以省掉，因为默认返回是200状态码
                response.setStatus(HttpServletResponse.SC_OK);
            }

            //要下载的长度（endByte为总长度-1，这时候要加回去）
            long contentLength = endByte - startByte + 1;
            //文件类型
            String contentType = request.getServletContext().getMimeType(fileName);


            //各种响应头设置
            //参考资料：https://www.ibm.com/developerworks/cn/java/joy-down/index.html
            //坑爹地方二：看代码
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType(contentType);
            response.setHeader("Content-Type", contentType);
            //这里文件名换你想要的，inline表示浏览器可以直接使用（比如播放音乐，我方便测试用的）
            //参考资料：http://hw1287789687.iteye.com/blog/2188500
            response.setHeader("Content-Disposition", "inline;filename="+fileName);
            response.setHeader("Content-Length", String.valueOf(contentLength));
            //坑爹地方三：Content-Range，格式为
            // [要下载的开始位置]-[结束位置]/[文件总大小]
            response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + music.length());


            BufferedOutputStream outputStream = null;
            RandomAccessFile randomAccessFile = null;
            //已传送数据大小
            long transmitted = 0;
            try {
                randomAccessFile = new RandomAccessFile(music, "r");
                outputStream = new BufferedOutputStream(response.getOutputStream());
                byte[] buff = new byte[4096];
                int len = 0;
                randomAccessFile.seek(startByte);
                //坑爹地方四：判断是否到了最后不足4096（buff的length）个byte这个逻辑（(transmitted + len) <= contentLength）要放前面！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
                //不然会先读取randomAccessFile，造成后面读取位置出错，找了一天才发现问题所在
                while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                    outputStream.write(buff, 0, len);
                    transmitted += len;
                    //停一下，方便测试（本地下载传输速度特别快，没反应过来就下载好了），实际生产环境中用的时候需要删除这一行
//                    Thread.sleep(10);
                }
                //处理不足buff.length部分
                if (transmitted < contentLength) {
                    len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                    outputStream.write(buff, 0, len);
                    transmitted += len;
                }

                outputStream.flush();
                response.flushBuffer();
                randomAccessFile.close();
                log.info("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
            } catch (ClientAbortException e) {
                log.info("用户停止下载：" + startByte + "-" + endByte + "：" + transmitted);
                //捕获此异常表示拥护停止下载
            } catch (IOException e) {
                log.error("用户下载文件出错:",e);
            } finally {
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    log.error("关闭文件IOException:",e);
                }
            }

        }
}
