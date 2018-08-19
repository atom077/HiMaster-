package com.whohim.springboot.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;

import java.io.IOException;
import java.util.List;


public interface IUserService {

    ServerResponse<User> login(String phone, String password) throws Exception;

    ServerResponse<String> loginGetCaptcha(String phone) throws IOException;

    ServerResponse<User> loginCaptcha(String phone, String captcha) throws IOException;

    ServerResponse<String> captcha(String phone) throws IOException;

    ServerResponse<String> register(User user, String captcha) throws IOException;

    ServerResponse<String> forgetGetCaptcha(String phone) throws IOException;

    ServerResponse<String> checkPhone(String str, String type);

    ServerResponse<String> forgetResetPassword(User user, String passwordNew, String captcha) throws IOException;

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse checkAdminRole(User user);

    ServerResponse<PageInfo> getCurrentHimasterList(String raspberrypie, int pageNum, int pageSize);

    ServerResponse <User>addUserRoot(String phone,String rasperrypie);

    ServerResponse<String> bindRaspberryPie(String phone, String raspberrypie);

    ServerResponse<List<User>> getUserStep();

    ServerResponse <String> updateUserStepInfo(User user);

    JSONObject getSessionKeyOropenId(String code);

    ServerResponse<Object> getWeRun(String encryptedData, String iv, String sessionKey, String appId) throws Exception;

    boolean checkToken(String token);
}
