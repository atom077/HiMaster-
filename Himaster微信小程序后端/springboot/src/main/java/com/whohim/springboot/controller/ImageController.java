package com.whohim.springboot.controller;


import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.common.ResponseCode;
import com.whohim.springboot.common.ServerResponse;

import com.whohim.springboot.service.IImageService;
import com.whohim.springboot.service.IModuleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;


import java.io.IOException;

@Controller
@RequestMapping("/image")
public class ImageController {


    @Autowired
    private IImageService iImageService;

    @Autowired
    private IModuleService iModuleService;

    /**
     * 上传管理员照片
     *
     * @param
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/admin_upload")
    @ResponseBody
    public ServerResponse adminUpload(String token, String openid, @RequestParam("user_face") MultipartFile file) throws Exception {
        if (token.equals(org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(), "密钥不能为空！");
        }
        String phone = iModuleService.getPhone(token);
        if (DataCache.getKey(phone) == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_RElOGIN.getCode(), "登录已过期！");
        }
//			//填充业务
        return iImageService.adminUpload(file, token, openid);
    }

    /**
     * 保存录入的管理员照片并重命名
     *
     * @param
     * @param
     * @param
     * @param
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/save_adminFace", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse savaAdminFace(String openid) throws ServletException, IOException {
//			//填充业务
        return iImageService.savaAdminFace(openid);
    }


    /**
     * 人脸对比
     *
     * @param
     * @param
     * @param
     * @param
     * @param
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/face_contrast", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse faceContrast(String openid, String token, @RequestParam("user_photo") MultipartFile file) throws ServletException, IOException {
        if (token.equals(org.apache.commons.lang3.StringUtils.EMPTY)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NONE_TOKEN.getCode(), "密钥不能为空！");
        }
        String phone = iModuleService.getPhone(token);
        if (DataCache.getKey(phone) == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_RElOGIN.getCode(), "登录已过期！");
        }
// 填充业务
        return iImageService.faceContrast(openid, token, file);

    }


}
