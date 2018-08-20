package com.whohim.springboot.service;

import com.whohim.baiduAi.DemoException;
import com.whohim.springboot.common.ServerResponse;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface IModuleService {


    ServerResponse controlLed(String token, String speakText) throws IOException, DemoException;

    ServerResponse controlDoor(String token, String speakText) throws IOException, DemoException;

    ServerResponse controlsmartSocket(String token, String speakText) throws IOException, DemoException;

    ServerResponse<Map> getModuleStatusAndData(String token) throws Exception;

    ServerResponse choosePattern(String token, String md);

    ServerResponse getWeather(String lon, String lat) throws IOException;

    String getPhone(String token);

    ServerResponse getDoorData(String token);
}
