package com.whohim.springboot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.whohim.baiduAi.FaceMatch;
import com.whohim.springboot.common.ResponseCode;
import com.whohim.springboot.common.ServerResponse;

import com.whohim.springboot.service.IImageService;

import com.whohim.springboot.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;

import static com.whohim.springboot.common.Const.adminImagePath;
import static com.whohim.springboot.common.Const.userImagePath;


@Service("iImageService")
public class ImageServiceImpl implements IImageService {

    @Autowired
    IUserService iUserService;

    @Override
    public ServerResponse adminUpload(@RequestParam("user_face") MultipartFile file, String token, String openid) throws IOException {
        if (!iUserService.checkToken(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_TOKEN.getCode(), "密钥无效！");
        }
        uploadFileAndCreate(file, adminImagePath + openid + "//");
        return ServerResponse.createBySuccessMessage("上传成功！");
    }

    @Override
    public ServerResponse savaAdminFace(String openid) throws IOException {
        File directory = new File("..");
        String path = directory.getCanonicalPath() + adminImagePath + openid + "//";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        int i = 0;
        for (File it : files) {
            if (!it.isDirectory()) {
                i++;
                System.out.println(it.getName());
                File newName = new File(path + "/" + i + ".jpg");
                it.renameTo(newName);
            }
        }
        return ServerResponse.createBySuccessMessage("录入成功！");
    }

    @Override
    public ServerResponse faceContrast(String openid, String token, MultipartFile file) throws IOException {
        if (!iUserService.checkToken(token)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_TOKEN.getCode(), "密钥无效！");
        }
        File directory = new File("..");
        String path = directory.getCanonicalPath() + adminImagePath + openid + "//";
        File userImageFile = uploadFileAndCreate(file, userImagePath);
        String userImagePath = userImageFile.getCanonicalPath();
        System.out.println("userImagePath:" + userImagePath);
        File adminFile = new File(path);
        File[] adminFiles = adminFile.listFiles();
        int i = 0, count = 0;
        for (File it : adminFiles) {
            i++;
            String adminImagePath = path + i + ".jpg";
            File weatherFile = new File(adminImagePath);
            if (!weatherFile.exists()) {
                return ServerResponse.createByErrorCodeMessage(2, "人脸数据不足，请重新录入！");
            }
            JSONObject jsonObject = JSONObject.parseObject(FaceMatch.getResult(adminImagePath, userImagePath));
            JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
            String errorCode = jsonObject.getString("error_code");
            if(!errorCode.equals("0")){
                return ServerResponse.createByErrorMessage("识别失败!");
            }
            float score = jsonObjectResult.getFloat("score");
            if (score > 85) {
                count++;
            }
        }
        if (i < 4)
            return ServerResponse.createByErrorCodeMessage(2, "人脸数据不足，请重新录入！");

        if (count >= 4)
            return ServerResponse.createBySuccessMessage("识别成功!");

        return ServerResponse.createByErrorMessage("识别失败!");
    }

    /**
     * 创建上传的文件并获得该文件路径
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static File uploadFileAndCreate(@RequestParam("user_photo") MultipartFile file, String cusPath) throws IOException {
        if (file.isEmpty()) {
            System.out.println("文件为空");
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);
        // 文件上传后的路径
        File directory = new File("..");
        String path = directory.getCanonicalPath() + cusPath;
        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        File dest = new File(path + fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            System.out.println("上传成功");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }


}
