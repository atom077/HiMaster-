package com.whohim.springboot.controller;


import com.whohim.baiduAi.DemoException;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;

import com.whohim.springboot.service.IVoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Controller
@RequestMapping("/voice")
public class VoiceController {

    @Autowired
    private IVoiceService iVoiceService;


    @RequestMapping("/upload_audio")
    @ResponseBody
    public ServerResponse uploadAudio(HttpServletRequest request, HttpServletResponse response, String token) throws Exception {
        return iVoiceService.uploadAudio(request, response, token);
    }


    @RequestMapping("/download_audio")
    @ResponseBody
    public ServerResponse<User> downAudio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //填充业务
        return iVoiceService.downAudio(request, response);
    }

    @RequestMapping("/chat")
    @ResponseBody
    public ServerResponse chatting(String info) throws IOException, DemoException {
        return iVoiceService.chatting(info);
    }


}
