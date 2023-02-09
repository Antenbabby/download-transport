package com.antennababy.download.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("DOWN_LOAD_LOG")
public class DownLoadLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    //原始文件名
    private String fileName;
    //本地文件名
    private String localUrl;
    private String orginUrl;
    //状态 0下载中,1 完成,2 出错
    private String status;
    private Date submitDate;
    private Date completeDate;
    private String userIp;
    private String userAgent;
}
