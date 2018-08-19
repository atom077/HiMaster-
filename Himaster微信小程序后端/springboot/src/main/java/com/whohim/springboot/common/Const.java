package com.whohim.springboot.common;


import java.nio.charset.Charset;

public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String PHONE = "phone";
    public static final Charset CHARSET = Charset.forName("gb2312");
    public static String APIKEY = "f57ffd3a115840378f5b8bd6560d91a8";//图灵机器人
    public static String ledOnContext = "CL+M3+SWH+ON";//开关灯指令
    public static String ledOffContext = "CL+M3+SWH+OFF";
    public static String ledOnMark = "CLM3SWHON";
    public static String ledOffMark = "CLM3SWHOFF";
    public static String doorOnContent = "CL+M2+STP+ON";
    public static String doorOffContent = "CL+M2+STP+OFF";
    public static String doorOnMark = "CLM2STPON";
    public static String doorOffMark = "CLM2STPOFF";
    public static String smartSocketOnContent = "CL+M3+SWH+ON";
    public static String smartSocketOffContent = "CL+M3+SWH+OFF";
    public static String smartSocketOnMark = "CLM3SWHON";
    public static String smartSocketOffMark = "CLM3SWHOFF";
    public static String homePattern = "MD+HM";
    public static String safePattern = "MD+SF";
    public static String sleepPattern = "MD+SP";
    public static String adminImagePath = "//upload//image//admin//";
    public static String userImagePath = "//upload//image//user//";
    public static String session ="onLine";
    public static String selectDoorRecord = "CL+M2+DOR+%";






    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1;//管理员
    }


}
