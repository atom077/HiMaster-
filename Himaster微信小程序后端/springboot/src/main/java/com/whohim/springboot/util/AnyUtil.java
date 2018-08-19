package com.whohim.springboot.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class AnyUtil {
    /**
     * map 排序算法-升序
     *
     * @param map
     * @return
     */
    public static Map sort(Map map) {
        Map<Object, Object> mapVK = new TreeMap<Object, Object>(
                new Comparator<Object>() {
                    public int compare(Object obj1, Object obj2) {
                        String v1 = (String) obj1;
                        String v2 = (String) obj2;
                        int s = v2.compareTo(v1);
                        return s;
                    }
                }
        );
        Set col = map.keySet();
        Iterator iter = col.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Integer value = (Integer) map.get(key);
            mapVK.put(key, value);
        }
        return mapVK;
    }

    /**
     * 十位时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds + "000")));
    }

}
