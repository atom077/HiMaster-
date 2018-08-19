package com.whohim.springboot.dao;

import com.whohim.springboot.pojo.Execute;

import java.util.List;

public interface ExecuteMapper {
    int deleteByPrimaryKey(String phone);

    int insert(Execute record);

    int insertSelective(Execute record);

    Execute selectByPrimaryKey(String phone);

    int updateByPrimaryKeySelective(Execute record);

    int updateByPrimaryKey(Execute record);

    List<Execute> selectDeviceData(String device);
}