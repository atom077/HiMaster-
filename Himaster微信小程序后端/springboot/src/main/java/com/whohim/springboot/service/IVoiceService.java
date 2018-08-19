package com.whohim.springboot.service;

import com.whohim.baiduAi.DemoException;
import com.whohim.springboot.common.ServerResponse;
import com.whohim.springboot.pojo.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IVoiceService {

     ServerResponse uploadAudio(HttpServletRequest request, HttpServletResponse response,String token) throws Exception;

     ServerResponse downAudio(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

     ServerResponse  chatting(String info) throws IOException, DemoException;


}
