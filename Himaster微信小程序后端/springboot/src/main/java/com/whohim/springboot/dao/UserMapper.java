package com.whohim.springboot.dao;

import com.whohim.springboot.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int deleteByPrimaryKey(Integer openid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer openid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkPhone(String phone);

    User selectLogin(@Param("phone") String phone, @Param("password") String password);

    User selectLoginCaptcha(String phone);

    int updatePasswordByPhone(@Param("phone") String phone, @Param("passwordNew") String passwordNew);

    int checkPassword(@Param(value = "password") String passwordOld, @Param("phone") String phone);

    List<User> getCurrentHimasterList(String raspberrypie);

    int bindRaspberryPie(@Param("phone") String phone, @Param("raspberryPie") String raspberryPie,@Param("token")String token);

    int addUserRoot(@Param("phone") String phone, @Param("raspberryPie") String raspberryPie, @Param("token") String token);

    List<User> selectStepInfo();

    int checkStepInfo(String openId);

    int checkToken(String token);
}