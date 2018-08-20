package com.whohim.springboot.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.whohim.baiduAi.DemoException;
import com.whohim.baiduAi.SdkEntity;
import com.whohim.baiduAi.TtsMain;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.controller.VoiceController;
import com.whohim.springboot.service.IModuleService;
import com.whohim.springboot.service.IVoiceService;
import com.whohim.springboot.util.HttpUtil;
import com.whohim.springboot.util.IoUtil;
import net.sf.json.JSONObject;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;


import static com.whohim.springboot.common.Const.APIKEY;


@Service("iVoiceService")
public class VoiceServiceImpl implements IVoiceService {

    @Autowired
    private IModuleService iModuleService;

    private static final Logger logger = LoggerFactory.getLogger(VoiceController.class);
    private static String destPath;
    private static String path;
    private static final String FileName = "E:\\wpace_j2ee\\upload\\16k.wav";//测试文件
    // 申明UUID
    public String uuid = UUID.randomUUID().toString().replace("-", "");

    /**
     * 微信上传语音接口
     *
     * @param request
     * @param
     * @throws Exception
     */

    public ServerResponse uploadAudio(HttpServletRequest request, HttpServletResponse response, String token) throws Exception {
        String LastResult = "";
        receiveVoiceAndCreateFile(request);
        File file = new File(destPath);
        // 判断文件是否存在
        if ((file.isFile() == false) || (file.exists() == false)) {
            System.out.println("文件不存在");
        } else {
            getPCM(destPath);//转码
            String newDestPath = path + uuid + ".pcm";//-------------改转换格式时顺便改这个-----------//
            SdkEntity.getToken();//识别语音
            LastResult = SdkEntity.method1(newDestPath);//-----------测试时替换这个就可以了-----------//
            IoUtil.deleteFile(newDestPath);//识别之后顺便删除
            IoUtil.deleteFile(destPath);
        }
        System.out.println(LastResult);
        JSONObject jsonObject = JSONObject.fromObject(LastResult);
        //通过getString("")分别取出里面的信息
        String errorNumber = jsonObject.getString("err_no");
        if (!errorNumber.equals("0")) {
            TtsMain.speak("", 2);
            return ServerResponse.createByError( "你在说什么呢？","暂未识别语音。");
        }
        String respResult = jsonObject.getString("result");
        System.out.println("respResult:" + respResult);
        String[] strs = respResult.split("[\"]");
        System.out.println("speakText=" + strs[1]);//strs[1]就是取得JSON里的result数据 语音识别的文本数据
        String speakText = strs[1];
        String text = turing(strs[1]);//text是图灵接口回传的回答文本

        if (LastResult.contains("开灯")) {
            TtsMain.speak("灯", 1);
            return iModuleService.controlLed(token, speakText);
        }
        if (LastResult.contains("关灯")) {
            TtsMain.speak("灯", 1);
            return iModuleService.controlLed(token, speakText);
        }
        if (LastResult.contains("开智能插座")) {
            TtsMain.speak("智能插座", 1);
            return iModuleService.controlLed(token, speakText);
        }
        if (LastResult.contains("关智能插座")) {
            TtsMain.speak("智能插座", 1);
            return iModuleService.controlLed(token, speakText);
        }
        TtsMain.speak(text, 3);
        return ServerResponse.createBySuccess(text,speakText);
    }


    public ServerResponse downAudio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = "result.mp3";
        if (fileName != null) {
            File directory = new File("..");
            String realPath = directory.getCanonicalPath() + "//springboot//";
            System.out.println("mp3Path:" + realPath + fileName);
            File file = new File(realPath, fileName);
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" + fileName);// 设置文件名
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("小程序已经下载啦~");
                    IoUtil.deleteFile(realPath + fileName);
                    System.out.println("result.mp3缓存已删除");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public ServerResponse chatting(String info) throws IOException, DemoException {
        String text = turing(info);
        TtsMain.speak(text, 3);
        return ServerResponse.createBySuccess("", text);
    }


    /**
     * ↓处理各种回调数据成json格式↓
     * ↓返回值给小程序↓
     *
     * @param result 语音识别的数据
     * @param text   图灵回传数据
     */
//    private void dealJsonAndResponseWeChat(String result, String text, HttpServletResponse response) throws IOException {
//        // ------↓处理各种回调数据成json格式↓------ //
//        String str = "{\"result\":\"" + result + "\",\"text\": \"" + text + "\"}";
//        JSONObject Object = JSONObject.fromObject(str);
//        System.out.println(str);
//        // ------↓返回值给小程序↓------ //
//        response.setContentType("application/json");
//        response.setCharacterEncoding("utf-8");
//        PrintWriter printWriter = response.getWriter();
//        printWriter.write(Object.toString());
//        printWriter.flush();
//    }

    /**
     * 图灵机器人接口
     *
     * @param keyword
     * @throws IOException
     */
    private String turing(String keyword) throws IOException {
        String INFO = URLEncoder.encode(keyword, "utf-8");
        String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY
                + "&info=" + INFO;
        String sb = HttpUtil.post(getURL, "");
        JSONObject jsonObject = JSONObject.fromObject(sb.toString());
        //通过getString("")分别取出里面的信息
        String text = jsonObject.getString("text");
        System.out.println("respResult:" + text);
        return text;
    }


    /**
     * 接收微信小程序传送过来的文件并创建目录，创建文件
     *
     * @param request
     * @throws IOException
     */
    private void receiveVoiceAndCreateFile(HttpServletRequest request) throws IOException {
        // 获取文件需要上传到的路径
        File directory = new File("..");
        path = directory.getCanonicalPath() + "//upload//";

        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        logger.debug("path=" + path);
        request.setCharacterEncoding("utf-8"); // 设置编码
        JSONArray jsonArray = new JSONArray();

        try {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            Iterator<String> iterator = req.getFileNames();
            while (iterator.hasNext()) {
                HashMap<String, Object> res = new HashMap<String, Object>();
                MultipartFile file = req.getFile(iterator.next());
                // 获取文件名
                String fileNames = file.getOriginalFilename();
                int split = fileNames.lastIndexOf(".");
                // 获取上传文件的后缀
                String extName = fileNames.substring(split + 1, fileNames.length());
                // 组成新的文件名称、新的路径
                String newName = uuid + "." + extName;
                System.out.println(newName);
                destPath = path + newName;
                logger.debug("destPath=" + destPath);
                // 真正写到磁盘上
                File file1 = new File(destPath);//这里是创建文件
                OutputStream out = new FileOutputStream(file1);
                out.write(file.getBytes());
                res.put("url", destPath);
                // result.setValue(jsonArray);
                jsonArray.add(res);
                out.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 解码为PCM格式
     *
     * @param silk 源silk文件,需要绝对路径!! 例:F:\zhuanma\vg2ub41omgipvrmur1fnssd3tq.silk
     *             该方法生成后的文件路径和解析前的路径一样 后缀改为.wav格式 采样率为16K
     * @return
     */


    private static boolean getPCM(String silk) {
        boolean flag = true;
        String cmd = "";
        cmd = "sh /product/developer/git-repository/silk/converter.sh " + silk + " pcm";
        System.out.println("silk已转码到PCM...");
        return crossDecoderDuring(flag, cmd);
    }

    private static boolean crossDecoderDuring(boolean flag, String cmd) {
        try {
            StringBuilder msg = Lang.execOutput(cmd, Encoding.CHARSET_UTF8);
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("silk_v3_decoder转码出错：" + e.getMessage());
            flag = false;
        }
        return flag;
    }
}
