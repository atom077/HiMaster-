package com.whohim.springboot.util;

import java.io.*;


public class IoUtil {

//        public static void main(String[] args) throws Exception {
//
//       getCreateFileTime("");
//    }


    /**
     * 用新的字符串替代指定的字符串
     *
     * @param file:文件
     * @param keyword:被替代的关键字(不可以带+号)
     * @param newWord:替代关键字的字符串
     */
    public static void replaceFile(File file, String keyword, String newWord) {
        // StringBuilder stringBuilder = new StringBuilder();
        StringBuffer stringBuilder = new StringBuffer("");
        BufferedReader br = null;
        BufferedWriter bw = null;
        boolean flag = false;
        try {
            // 读取文件
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.contains(keyword)) {
                    line = line.replaceAll(keyword, newWord);
                    flag = true;
                }
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            if (flag) {
                // 写文件
                bw = new BufferedWriter(new FileWriter(file));
                //这里用了一个字符转换的类
                bw.write(new ChangeCharest().toGB2312(stringBuilder.toString()));

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bw) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 利用FileInputStream读取文件
     */

    public static String FileInputStream(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while ((fis.read(buf)) != -1) {
            sb.append(new String(buf));
            buf = new byte[1024];// 重新生成，避免和上次读取的数据重复
        }
        fis.close();
        System.out.println("FileInputStream已读取:" + sb);
        return sb.toString();
    }

    /**
     * 在IO操作，利用BufferedReader和BufferedWriter读取效率会更高一点
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String BufferedReader(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            sb.append(temp);
            temp = br.readLine();
        }
        br.close();
        System.out.println("BufferedReader已读取:" + sb);
        return sb.toString();
    }


    /**
     * 写入内容到文件
     *
     * @param
     */

    public static void PrintStream(String path, String context) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            PrintStream p = new PrintStream(out);
            p.println(context);
            System.out.println("已写入" + context + "到" + path);
            p.close();//关闭文件流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void StringBuffer(String path, String context) throws IOException {
        File file = new File(path);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        StringBuffer sb = new StringBuffer();
        sb.append(context);
        out.write(sb.toString().getBytes("utf-8"));
        out.close();
    }


    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名和路径
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除整个文件夹
     *
     * @param path
     */
    public static void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFilesOfDir(files[i]);
        }
        path.delete();
    }

}
