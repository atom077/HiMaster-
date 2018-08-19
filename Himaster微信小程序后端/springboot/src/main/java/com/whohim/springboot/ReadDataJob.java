package com.whohim.springboot;

import com.whohim.springboot.common.DataCache;
import com.whohim.springboot.util.IoUtil;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.IOException;

public class ReadDataJob implements Job{

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String dtrs[],strs[];
        JobDetail detail = jobExecutionContext.getJobDetail();
        String reaspberry = detail.getJobDataMap().getString("reaspberry");
//        String dtPath = "C:\\Users\\Administrator\\Desktop\\"+reaspberry+"\\DT.txt";
//        String stPath = "C:\\Users\\Administrator\\Desktop\\"+reaspberry+"\\ST.txt";
//        String path = "C:\\Users\\Administrator\\Desktop\\"+reaspberry;
        String path = "/product/developer/himaster/driverCL/" + reaspberry;
        String dtPath = "/product/developer/himaster/driverCL/" + reaspberry + "/DT.txt";
        String stPath = "/product/developer/himaster/driverCL/" + reaspberry + "/ST.txt";
        File dtFile = new File(dtPath);
        File stFile = new File(stPath);
        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        if ((dtFile.isFile() == false) || (dtFile.exists() == false) || (stFile.isFile() == false) || (stFile.exists() == false)){
            System.out.println("文件不存在!正在创建！");
            try {
                dtFile.createNewFile();
                stFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String dtContext = null;
        String stContext = null;
        try {
            dtContext = IoUtil.BufferedReader(dtPath);
            stContext = IoUtil.BufferedReader(stPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dtContext);
        if (!dtContext.equals("") || !stContext.equals("")) {

            dtrs = dtContext.split("[+]");
            strs = stContext.split("[+]");

            //处理温度数据
            if (dtrs[2].trim().equals("TM")) {
                String tm = (dtrs[3].trim());
                int tt = Integer.parseInt((tm));//专门用来算tm多少位数的
                float t2 = Float.parseFloat(tm);//如果tm是三位数，进行浮点运算，精确到0.1
                int count = 0;
                while (tt > 0) {
                    tt = tt / 10;
                    count++;
                }
                System.out.println("tm是" + count + "位数");
                if (count == 3)
                    t2 = t2 / 10;
                System.out.println("温度：" + t2);
                tm = String.valueOf(t2);
                DataCache.setKey(reaspberry + "-tm", tm);
            }
            //光照
            if (dtrs[2].trim().equals("LIT")) {
                String lit = dtrs[3].trim();
                DataCache.setKey(reaspberry + "-lit", lit);
            }
            //湿度
            if (dtrs[2].trim().equals("HM")) {
                String hm = dtrs[3].trim();
                DataCache.setKey(reaspberry + "-hm", hm);
            }
            //HM状态
            if (strs[2].trim().equals("HM")) {
                String hm = strs[3].trim();
                DataCache.setKey(reaspberry + "-hm-status", hm);
            }
            //LIT状态
            if (strs[2].trim().equals("LIT")) {
                String lit = strs[3].trim();
                DataCache.setKey(reaspberry + "-lit-status", lit);
            }
            //TM状态
            if (strs[2].trim().equals("TM")) {
                String tm = strs[3].trim();
                DataCache.setKey(reaspberry + "-tm-status", tm);
            }
        }


    }
}
