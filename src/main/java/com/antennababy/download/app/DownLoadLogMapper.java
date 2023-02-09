package com.antennababy.download.app;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author laikang
* @description 针对表【DOWN_LOAD_LOG】的数据库操作Mapper
* @createDate 2023-01-17 16:47:07
* @Entity generator.domain.DownLoadLog
*/
public interface DownLoadLogMapper extends BaseMapper<DownLoadLog> {

    int deleteByPrimaryKey(Long id);

    int insert(DownLoadLog record);

    int insertSelective(DownLoadLog record);

    DownLoadLog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DownLoadLog record);

    int updateByPrimaryKey(DownLoadLog record);

}
