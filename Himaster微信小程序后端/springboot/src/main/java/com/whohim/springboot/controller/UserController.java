package com.whohim.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import com.whohim.springboot.common.Const;
import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;
import com.whohim.springboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.whohim.springboot.common.Const.session;


@Controller
@RequestMapping("/user/")
public class UserController {


    @Autowired
    private IUserService iUserService;

    /**
     * 获取微信小程序的sessionKey 和openId
     * @param code
     * @return
     */
    @RequestMapping(value = "get_sessionkey",method = RequestMethod.POST)
    @ResponseBody
    public JSONObject getSessionKeyOropenId(String code){
        return iUserService.getSessionKeyOropenId(code);
    }
    /**
     * 用户登录
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String phone, String password) throws Exception {
        ServerResponse<User> response = iUserService.login(phone,password);
        if(response.isSuccess()){
            DataCache.setKey(phone,session);
        }
        return response;
    }

    /**
     * 用户登录(验证码方式,获得验证码)
     * @param phone
     * @return
     */
    @RequestMapping(value = "login_get_captcha",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> loginGetCaptcha(String phone) throws IOException {
        ServerResponse<String> response = iUserService.loginGetCaptcha(phone);
        return response;
    }

    /**
     * 用户登录(验证码方式,获得验证码之后，提交电话号码和验证码)
     * @param phone
     * @return
     */
    @RequestMapping(value = "login_captcha",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> loginCaptcha(String phone,String captcha) throws IOException {
        ServerResponse<User> response = iUserService.loginCaptcha(phone,captcha);
        if(response.isSuccess()){
            DataCache.setKey(phone,session);
        }
        return response;
    }

    /**
     * 用户退出
     * @param
     * @return
     */
    @RequestMapping(value = "logout",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(String phone){
        DataCache.getKey(phone);
        return ServerResponse.createBySuccessMessage("登出成功！");
    }

    /**
     * 用户注册
     * @param user
     * @param captcha
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user,String captcha) throws IOException {
        return iUserService.register(user,captcha);
    }

    /**
     * 用户注册验证码
     * @param phone
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "captcha",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>captcha(String phone) throws IOException {
        return iUserService.captcha(phone);
    }

    /**
     * 检验电话号码
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkphone",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkPhone(str,type);
    }

    /**
     * 忘记密码-获得验证码
     * @param phone
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "forget_get_captcha",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetCaptcha(String phone) throws IOException {
        return iUserService.forgetGetCaptcha(phone);
    }


    /**
     * 获取当前登录用户的信息
     * @param
     * @return
     */
    @RequestMapping(value = "get_user_info",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getUserInfo(String phone){
        if(DataCache.getKey(phone) == null){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        }
        return ServerResponse.createBySuccess();
    }


    /**
     * 忘记密码，获得短信之后重设密码
     * @param user
     * @param passwordNew
     * @param captcha
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "forget_reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(User user,String passwordNew,String captcha) throws IOException {
        return iUserService.forgetResetPassword(user,passwordNew,captcha);
    }


    /**
     * 登录中状态重置密码
     * @param
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        if(DataCache.getKey(user.getPhone()) == null){
            return ServerResponse.createByErrorMessage("登录已过期，请重新登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     *.绑定树莓派 /user/bind_raspberryPie
     注意的是，每一个Himaster系统都有一个独立机器码（raspberryPie），和机器密码（pieKey），并且都有一个该系统的管理员账号和密码。
     该管理员账号用于给成员添加管理权限，机器码就是管理员账号，机器密码就是管理员密码。
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "bind_raspberryPie",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> bindRaspberryPie(String phone,String raspberrypie){
        if(DataCache.getKey(phone) == null){
            return ServerResponse.createByErrorMessage("登录已过期，请重新登录");
        }
        return iUserService.bindRaspberryPie(phone,raspberrypie);
    }


    /**
     * 查询所有用户步数
     * @param
     * @return
     */
    @RequestMapping("get_userStep")
    @ResponseBody
    public ServerResponse<List<User>> getUserStep(){
        return iUserService.getUserStep();
    }

    /**
     * 更新用户步数
     * @param
     * @param
     * @return
     */
    @RequestMapping(value = "update_userStep",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse <String> updateUserStep(User user){
        return iUserService.updateUserStepInfo(user);
    }

    /**
     *
     * @param encryptedData 加密数据
     * @param iv 加密方式
     * @param session_key 微信用code换的钥匙
     * @param appid
     * @return
     */
    @RequestMapping(value = "get_werun",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Object> getWeRun(String encryptedData, String iv, String session_key, String appid) throws Exception {
        return iUserService.getWeRun(encryptedData,iv,session_key,appid);
    }




























}
