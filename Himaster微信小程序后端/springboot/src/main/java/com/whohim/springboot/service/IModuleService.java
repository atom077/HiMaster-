package com.whohim.springboot.service;

import com.whohim.springboot.common.ServerResponse;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

public interface IModuleService {



    ServerResponse  controlLed(String token) throws IOException;

    ServerResponse  controlDoor(String token ) throws IOException;

    ServerResponse  controlsmartSocket(String  token) throws IOException ;

    ServerResponse<Map> getModuleStatusAndData(String token) throws Exception;

    ServerResponse choosePattern(String token, String md);

    ServerResponse getWeather(String lon,String lat) throws IOException;

    String getPhone(String token);

    ServerResponse getDoorData(String token);
}
