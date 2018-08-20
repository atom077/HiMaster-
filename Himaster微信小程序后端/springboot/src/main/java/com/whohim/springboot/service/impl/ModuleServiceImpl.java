package com.whohim.springboot.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.whohim.baiduAi.DemoException;
import com.whohim.baiduAi.TtsMain;
import com.whohim.springboot.common.Const;
import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.common.ResponseCode;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.dao.ExecuteMapper;
import com.whohim.springboot.dao.UserMapper;
import com.whohim.springboot.pojo.Execute;
import com.whohim.springboot.service.IModuleService;
import com.whohim.springboot.service.IUserService;
import com.whohim.springboot.util.DesUtil;
import com.whohim.springboot.util.HttpUtil;
import com.whohim.springboot.util.IoUtil;
import com.whohim.springboot.util.PropertiesUtil;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;


import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.whohim.springboot.common.Const.*;


@Service("iOpenService")
public class ModuleServiceImpl implements IModuleService {

    @Autowired
    private ExecuteMapper ExecuteMapper;

    @Autowired
    private IUserService iUserService;


    private static String[] strs;


    public ServerResponse controlLed(String token, String speakText) throws IOException, DemoException {
        return controlModle(token, speakText, ledOnMark, ledOffMark, ledOnContext, ledOffContext, "灯");
    }

    @Override
    public ServerResponse controlDoor(String token, String speakText) throws IOException, DemoException {
        return controlModle(token, speakText, doorOnMark, doorOffMark, doorOnContent, doorOffContent, "门");
    }


    @Override
    public ServerResponse controlsmartSocket(String token, String speakText) throws IOException, DemoException {
        return controlModle(token, speakText, smartSocketOnMark, smartSocketOffMark, smartSocketOnContent, smartSocketOffContent, "智能插座");
    }

    @Override
    public ServerResponse<Map> getModuleStatusAndData(String token) throws Exception {
        if (!iUserService.checkToken(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_TOKEN.getCode(), "密钥无效！");
        }
        String reaspberry = getReaspberry(token);
        String hm = " ", lit = " ", tm = " ";
        hm = DataCache.getKey(reaspberry + "-hm");
        lit = DataCache.getKey(reaspberry + "-lit");
        tm = DataCache.getKey(reaspberry + "-tm");
        String hmStatus = DataCache.getKey(reaspberry + "-hm-status");
        String litStatus = DataCache.getKey(reaspberry + "-lit-status");
        String tmStatus = DataCache.getKey(reaspberry + "-tm-status");
        Map map = new HashMap();
        map.put("HM", hm);
        map.put("LIT", lit);
        map.put("TM", tm);
        map.put("HM-Status", hmStatus);
        map.put("LIT-Status", litStatus);
        map.put("TM-Status", tmStatus);
        return ServerResponse.createBySuccess("成功取得数据及状态！", map);
    }

    @Override
    public ServerResponse choosePattern(String token, String md) {
        if (!iUserService.checkToken(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_TOKEN.getCode(), "密钥无效！");
        }
        String reaspberry = getReaspberry(token);
//      String clPath = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\" + reaspberry + "\\-CL.txt";
        String clPath = "/product/developer/himaster/driverCL/" + reaspberry + "-CL.txt";
        if (md.equals("HM")) {
            IoUtil.PrintStream(clPath, homePattern);
        }
        if (md.equals("SF")) {
            IoUtil.PrintStream(clPath, safePattern);
        }
        if (md.equals("SP")) {
            IoUtil.PrintStream(clPath, sleepPattern);
        }
        return ServerResponse.createBySuccess("选择成功");
    }


    @Override
    public ServerResponse getWeather(String lon, String lat) throws IOException {
        String url = "http://v.juhe.cn/weather/geo";
        String body = "?format=2&key=2846df34dbabb303f31ea31ed780ae68&lon=" + lon + "&lat=" + lat + "";
        String result = HttpUtil.post(url, body);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
        JSONObject jsonObjectToday = jsonObjectResult.getJSONObject("today");
        Map map = new HashMap();
        map.put("temperature", jsonObjectToday.getString("temperature"));
        map.put("weather", jsonObjectToday.getString("weather"));
        map.put("future", jsonObjectResult.getString("future"));
        return ServerResponse.createBySuccess("返回天气数据成功！", map);
    }

    @Override
    public ServerResponse getDoorData(String token) {
        if (!iUserService.checkToken(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_TOKEN.getCode(), "密钥无效！");
        }
        List<Execute> execute = ExecuteMapper.selectDeviceData(Const.selectDoorRecord);
        System.out.println(execute.get(0));
        JSONObject JSONObject = new JSONObject((Map<String, Object>) execute.get(0));
        if (execute == null)
            return ServerResponse.createByErrorMessage("查询设备数据失败！");
        return ServerResponse.createBySuccess("查询设备数据成功", execute);
    }


    /**
     * 解密token得到机器码
     *
     * @param token
     * @return
     */
    public static String getReaspberry(String token) {
        try {
            String phone_reaspberry = DesUtil.decrypt(token, Charset.defaultCharset(), PropertiesUtil.getProperty("desKey"));
            System.out.println(phone_reaspberry);
            strs = phone_reaspberry.split("[&]");
            System.out.println("reaspberry = " + strs[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解密失败！");
        }
        return strs[1];
    }

    /**
     * 解密token得到phone
     *
     * @param token
     * @return
     */
    public String getPhone(String token) {
        try {
            String phone_reaspberry = DesUtil.decrypt(token, Charset.defaultCharset(), PropertiesUtil.getProperty("desKey"));
            System.out.println(phone_reaspberry);
            strs = phone_reaspberry.split("[&]");
            System.out.println("phone = " + strs[0]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解密失败！");
        }
        return strs[0];
    }


    /**
     * 重写的各个模块控制函数
     *
     * @param token
     * @param moduleOnMark
     * @param moduleOffMark
     * @param moduleOnContent
     * @param moduleOffContent
     * @param moduleName
     * @return
     * @throws IOException
     */
    private ServerResponse<Object> controlModle(String token, String speakText, String moduleOnMark, String moduleOffMark, String moduleOnContent, String moduleOffContent, String moduleName) throws IOException, DemoException {
        if (!iUserService.checkToken(token)) {
            TtsMain.speak("密钥无效,请绑定设备！", 3);
            return ServerResponse.createBySuccess("密钥无效，请绑定设备!",speakText);
        }
        String phone = getPhone(token);
        if (DataCache.getKey(phone) == null) {
            TtsMain.speak("登录已过期,请重新登录！", 3);
            return ServerResponse.createByError("登录已过期,请重新登录！",speakText);
        }
        String reaspberry = getReaspberry(token);
        System.out.println(phone.trim());
        String clPath = "/product/developer/himaster/driverCL/" + reaspberry + "/CL.txt";
        String markPath = "/product/developer/himaster/driverCL/" + reaspberry + "/MARK.txt";
        String path = "/product/developer/himaster/driverCL/" + reaspberry;
//        String clPath = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\" + reaspberry + "\\CL.txt";
//        String markPath = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\" + reaspberry + "\\MARK.txt";
//        String path = "C:\\\\Users\\\\Administrator\\\\Desktop\\\\" + reaspberry;
        File clFile = new File(clPath);
        File markFile = new File(markPath);
        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 判断文件是否存在
        if ((clFile.isFile() == false) || (clFile.exists() == false) || (markFile.exists() == false) || (markFile.exists() == false)) {
            System.out.println("文件不存在!正在创建！");
            clFile.createNewFile();
            markFile.createNewFile();
//            IoUtil.PrintStream(markPath, "CLM1LEDOFF\n" + "CLM2STPON\n" + "CLM3SWHON\n");//给各模块状态归零
        }
        StringBuffer s1 = new StringBuffer();
        StringBuffer s2 = new StringBuffer();

        s1.append(moduleOnMark);
        s2.append(moduleOffMark);

        Execute execute = new Execute();
        execute.setPhone(phone);
        execute.setExecuteTime(new Date());

        try {
            if (IoUtil.BufferedReader(markPath).contains(s2)) {
                IoUtil.PrintStream(clPath, moduleOnContent);
                IoUtil.replaceFile(new File(markPath), moduleOffMark, moduleOnMark);
                execute.setActionid(moduleOnContent);
                ExecuteMapper.insert(execute);
            } else {
                IoUtil.PrintStream(clPath, moduleOffContent);
                IoUtil.replaceFile(new File(markPath), moduleOnMark, moduleOffMark);
                execute.setActionid(moduleOffContent);
                ExecuteMapper.insert(execute);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("主人，开关" + moduleName + "失败！",speakText);
        }
        return ServerResponse.createBySuccessMessage("主人，开关" + moduleName + "成功！",speakText);
    }

    public static void delayTime(int a, int b) {
        Thread t1 = new Thread() {
            public void run() {
                try {
                    Thread.sleep(a * b);//1000=1s

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        t1.start();
    }


}
