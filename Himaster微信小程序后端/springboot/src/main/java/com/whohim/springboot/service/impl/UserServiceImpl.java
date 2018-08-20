package com.whohim.springboot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;


import com.whohim.springboot.util.*;
import com.whohim.miaodi.IndustrySMS;
import com.whohim.springboot.common.Const;
import com.whohim.springboot.common.ServerResponse;

import com.whohim.springboot.dao.UserMapper;
import com.whohim.springboot.pojo.User;
import com.whohim.springboot.service.IUserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;


import static com.whohim.springboot.common.Const.CHARSET;
import static com.whohim.springboot.util.IoUtil.PrintStream;


@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public static String getcaptcha = "";//验证码

    public static String destPath = "";//保存验证码文件目录


    @Override
    public ServerResponse<User> login(String phone, String password) throws Exception {
        int resultCount = userMapper.checkPhone(phone);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("号码不存在！");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(phone, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        resetToken(user, phone);
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
        user.setPhone(org.apache.commons.lang3.StringUtils.EMPTY);
        user.setRaspberrypie(org.apache.commons.lang3.StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> loginGetCaptcha(String phone) throws IOException {
        //判断电话号码是否存在
        ServerResponse validResponse = this.checkPhone(phone, Const.PHONE);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("号码不存在！");
        }
        return reuseCaptcha(phone, "-login.txt");
    }

    @Override
    public ServerResponse<User> loginCaptcha(String phone, String captcha) throws IOException {
        File directory = new File("..");
        String path = directory.getCanonicalPath() + "//upload//";
        destPath = path + phone + "-login.txt";
        File file = new File(destPath);
        //判断电话号码是不是先前提交的电话号码
        if ((file.isFile() == false) || (file.exists() == false)) {
            return ServerResponse.createByErrorMessage("号码输入错误！");
        }
        if (captcha.equals(IoUtil.BufferedReader(destPath))) {
            //如果输入的验证码与记录的相等，则
            User user = userMapper.selectLoginCaptcha(phone);
            resetToken(user, phone);
            user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);
            user.setPhone(org.apache.commons.lang3.StringUtils.EMPTY);
            user.setRaspberrypie(org.apache.commons.lang3.StringUtils.EMPTY);
            return ServerResponse.createBySuccess("登录成功!", user);
        }
        return ServerResponse.createByErrorMessage("验证码错误!");
    }


    @Override
    public ServerResponse<String> captcha(String phone) throws IOException {
        //判断电话号码是否存在
        ServerResponse validResponse = this.checkPhone(phone, Const.PHONE);
        if (!validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("号码已存在！");
        }
        return reuseCaptcha(phone, "-rigister.txt");
    }


    public ServerResponse<String> register(User user, String captcha) throws IOException {
        knowPhoneWeatherPost(user, "-rigister.txt");
        if (user.getPhone() == null || user.getPhone().length() < 11) {
            return ServerResponse.createBySuccessMessage("号码不能为空或者号码长度不对！");
        }
        //判断验证码是否正确
        if (captcha.equals(IoUtil.BufferedReader(destPath))) {
            user.setRole(Const.Role.ROLE_CUSTOMER);
            //MD5加密
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
            user.setOpenid(user.getOpenid());
            int resultCount = userMapper.updateByPrimaryKeySelective(user);
            if (resultCount == 0) {
                return ServerResponse.createByErrorCodeMessage(3, "注册失败,无法写入数据库！");
            }
            return ServerResponse.createBySuccessMessage("注册成功");
        }
        return ServerResponse.createByErrorCodeMessage(2, "注册失败，验证码不正确！");
    }

    @Override
    public ServerResponse<String> forgetGetCaptcha(String phone) throws IOException {
        //判断电话号码是否存在
        ServerResponse validResponse = this.checkPhone(phone, Const.PHONE);
        if (validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("号码不存在！");
        }
        return reuseCaptcha(phone, "-forget.txt");
    }


    public ServerResponse<String> checkPhone(String str, String type) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.PHONE.equals(type)) {
                int resultCount = userMapper.checkPhone(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("号码已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }


    public ServerResponse<String> forgetResetPassword(User user, String passwordNew, String captcha) throws IOException {
        knowPhoneWeatherPost(user, "-forget.txt");
        if (user.getPhone() == null || user.getPhone().length() < 11) {
            return ServerResponse.createBySuccessMessage("号码不能为空或者号码长度不对！");
        }
        //判断验证码是否正确
        if (captcha.equals(IoUtil.BufferedReader(destPath))) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByPhone(user.getPhone(), md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功!");
            }
        } else {
            return ServerResponse.createByErrorCodeMessage(2, "验证码不正确!");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }


    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getPhone());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }


    /**
     * pagehelper的经典用法
     *
     * @param raspberrypie
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getCurrentHimasterList(String raspberrypie, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.getCurrentHimasterList(raspberrypie);
        PageInfo<User> pageResult = new PageInfo<>(userList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse addUserRoot(String phone, String rasperrypie) {
//        String token = DesUtil.encrypt(phone + "&" + rasperrypie, CHARSET, PropertiesUtil.getProperty("desKey"));
//        System.out.println(token);
//        int updateCount = userMapper.addUserRoot(phone, rasperrypie, token);
//        if (updateCount > 0) {
//            return ServerResponse.createBySuccessMessage("用户添加权限成功！");
//        }
        return ServerResponse.createByErrorMessage("用户添加权限失败！");
    }

    @Override
    public ServerResponse<String> bindRaspberryPie(String phone, String raspberryPie) {
        String uuid = UUID.randomUUID().toString();//动态生成uuid
        String random[] = uuid.split("-");
        String token = DesUtil.encrypt(phone + "&" + raspberryPie + "&" + random[0], CHARSET, PropertiesUtil.getProperty("desKey"));
        int updateCount = userMapper.bindRaspberryPie(phone, raspberryPie, token);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("绑定成功！", token);
        }
        return ServerResponse.createByErrorMessage("绑定失败,请注册！");
    }

    @Override
    public ServerResponse<List<User>> getUserStep() {
        List<User> user = userMapper.selectStepInfo();
        if (user != null) {
            return ServerResponse.createBySuccess("查询成功！", user);
        }
        return ServerResponse.createByErrorMessage("查询步数失败!");
    }

    @Override
    public ServerResponse<String> updateUserStepInfo(User user) {
        user.setOpenid(user.getOpenid());
        user.setStepinfo(user.getStepinfo());
        user.setNickname(user.getNickname());
        user.setAvatarurl(user.getAvatarurl());
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setCreateTime(new Date());
        int checkCount = userMapper.checkStepInfo(user.getOpenid());
        if (checkCount > 0) {
            userMapper.updateByPrimaryKeySelective(user);
            return ServerResponse.createBySuccessMessage("用户步数更新成功！");
        } else {
            int insertCount = userMapper.insert(user);
            if (insertCount > 0) {
                return ServerResponse.createBySuccessMessage("用户步数更新成功！");
            }
        }
        return ServerResponse.createByErrorMessage("用户步数更新失败！");
    }

    @Override
    public JSONObject getSessionKeyOropenId(String code) {
        //微信端登录code值
        String wxCode = code;
        String requestUrl = PropertiesUtil.getProperty("url"); //请求地址 https://api.weixin.qq.com/sns/jscode2session
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("appid", PropertiesUtil.getProperty("appId")); //开发者设置中的appId
        requestUrlParam.put("secret", PropertiesUtil.getProperty("appSecret")); //开发者设置中的appSecret
        requestUrlParam.put("js_code", wxCode); //小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");  //默认参数
        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject(HttpUtil.sendPost(requestUrl, requestUrlParam));
        System.out.println(jsonObject);
        return jsonObject;
    }

    @Override
    public ServerResponse<Object> getWeRun(String encryptedData, String iv, String session_key, String appid) throws Exception {
        JSONObject userinfo = AesUtil.getUserInfo(encryptedData, session_key, iv);
        String stepInfoList = userinfo.getString("stepInfoList");
        JSONArray jsonArray = JSONArray.parseArray(stepInfoList);
        Map map = new HashMap();
        for (Object obj : jsonArray) {//这里遍历JsonArray把timestamp、step都拿出来以键值对形式存储在哈希表里
            JSONObject jsonObject = (JSONObject) obj;
            map.put(AnyUtil.timeStamp2Date(jsonObject.getString("timestamp"), "yyyy-MM-dd"), Integer.parseInt(jsonObject.getString("step")));
        }
        Map<String, Integer> sortMaps = AnyUtil.sort(map);//排序后的Map
        return ServerResponse.createBySuccess("已取得步数！", sortMaps);
    }


    /**
     * 动态更新token
     *
     * @param user
     * @param phone
     */
    private void resetToken(User user, String phone) {
        String raspberryPie = ModuleServiceImpl.getReaspberry(user.getToken());
        String uuid = UUID.randomUUID().toString();//动态生成uuid
        String random[] = uuid.split("-");
        String token = DesUtil.encrypt(phone + "&" + raspberryPie + "&" + random[0], CHARSET, PropertiesUtil.getProperty("desKey"));
        user.setToken(token);
        int updateToken = userMapper.updateByPrimaryKeySelective(user);
        if (updateToken > 0)
            System.out.println("token" + "更新成功!");
    }

    /**
     * 判断电话号码是不是先前提交的电话号码
     *
     * @param user
     * @param suffix 文件后缀名
     * @return
     * @throws IOException
     */
    private ServerResponse<String> knowPhoneWeatherPost(User user, String suffix) throws IOException {
        File directory = new File("..");
        String path = directory.getCanonicalPath() + "//upload//";
        destPath = path + user.getPhone() + suffix;
        File file = new File(destPath);
        //判断电话号码是不是先前提交的电话号码
        if ((file.isFile() == false) || (file.exists() == false)) {
            System.out.println("号码输入错误!");
            return ServerResponse.createByErrorMessage("号码输入错误！");
        }
        return null;
    }

    /**
     * 重用的发送验证码接口
     *
     * @param phone
     * @param suffix 文件名后缀
     * @return
     * @throws IOException
     */
    private ServerResponse<String> reuseCaptcha(String phone, String suffix) throws IOException {
        ModuleServiceImpl.delayTime(100, 1000);
        if (phone == null || phone.length() < 11) {
            return ServerResponse.createBySuccessMessage("号码不能为空或者号码长度不对！");
        }
        getcaptcha = IndustrySMS.execute(phone);
        String[] strs = getcaptcha.split("[,]");
        System.out.println("respCode=" + strs[0]);
        System.out.println("randonNumber=" + strs[1]);
        destPath = createFileAndWrite(phone, strs[1], suffix);
        if (strs[0].equals("00000")) {
            return ServerResponse.createBySuccess("已发出验证码！", strs[1]);
        }
        return ServerResponse.createByErrorMessage("未发出验证码");
    }

    /**
     * 创建文件并写入内容
     *
     * @param phone
     * @param content 写入的内容
     * @param suffix  后缀
     * @throws IOException
     */
    private String createFileAndWrite(String phone, String content, String suffix) throws IOException {
        // 获取文件需要上传到的路径
        File directory = new File("..");
        String path = directory.getCanonicalPath() + "//upload//";
        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        logger.debug("path=" + path);
        destPath = path + phone + suffix;
        logger.debug("destPath=" + destPath);
        File file = new File(destPath);
        // 判断文件是否存在
        if ((file.isFile() == false) || (file.exists() == false)) {
            System.out.println("文件不存在!正在创建！");
            file.createNewFile();
        }
        PrintStream(destPath, content);
        return destPath;
    }


    /**
     * 校验是否是管理员
     *
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


    /**
     * 检查token是否正确，正确则返回true
     *
     * @param token
     * @return
     */
    public boolean checkToken(String token) {
        int resultCount = userMapper.checkToken(token);
        if (resultCount > 0) {
            return true;
        }
        return false;
    }
}
