package com.davidlong.creeper.util;

import org.apache.log4j.Logger;

import java.io.*;

public class FileUtil {

    private static Logger logger= Logger.getLogger(FileUtil.class);
    /**
     * 获取上一级的路径
     * @param filePath
     * @return
     */
    public static String getPrePath(String filePath){
        int i = filePath.lastIndexOf(File.separator);
        if(i!=-1){
            return 	filePath.substring(0, i);
        }
        return filePath;
    }

    /**
     * 获取上N级的路径
     * @param filePath
     * @return
     */
    public static String getPrePath(String filePath,int n){
        for (int i = 0; i < n; i++) {
            filePath=getPrePath(filePath);
        }
        return 	filePath;
    }


    /**
     * 获取文件名称加后缀
     * @param file
     * @return
     */
    public static String getNameWithSuffix(String file){
       return file.substring(file.lastIndexOf(File.separator)+1);
    }

    /**
     * 获取文件名称
     * @param file
     * @return
     */
    public static String getName(String file){
        return file.substring(file.lastIndexOf(File.separator)+1,file.indexOf("."));
    }

    public static void createIfNotExists(File file) throws IOException {
        if (file.exists()) return;
        File parentFile = file.getParentFile();
        if(parentFile!=null && !parentFile.exists()){
            parentFile.mkdirs();
        }
        file.createNewFile();
    }

    public static void createIfNotExists(File file,File parent) throws IOException {
        if (!file.exists()) {
            if(file.isFile()){
                file.createNewFile();
            }
        }
    }

    public static void clearFileContent(File file){
        try {
            createIfNotExists(file);
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
            System.out.println("clear "+file.getName()+" content!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(File file,String str) throws IOException{
        FileWriter buffer = null;
        try {
            FileUtil.createIfNotExists(file);
            buffer = new FileWriter(file);
            buffer.write(str);
        } catch (IOException e) {
            throw e;
        }finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public static String read(File file) throws IOException{
        StringBuilder sb=new StringBuilder();
        BufferedReader reader = null;
        try {
            FileUtil.createIfNotExists(file);
            reader = new BufferedReader(new FileReader(file));
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
        } catch (IOException e) {
            throw e;
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return sb.toString();
    }

    public static String read(InputStream inputStream) throws IOException{
        StringBuilder sb=new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
        } catch (IOException e) {
            throw e;
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw e;
            }
        }
        return sb.toString();
    }


    public static void copyFile(File sourceFile,File targetFile)
            throws IOException{
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    public static void copyFile(InputStream sourceFile,File targetFile)
            throws IOException{
        // 新建文件输入流并对它进行缓冲
        BufferedInputStream inBuff=new BufferedInputStream(sourceFile);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        sourceFile.close();
    }

    public static void copyIfNotExist(String path,String resourceName) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            logger.info(path+" 文件不存在，正在从Resource中复制该文件");
            createFile(file);
            InputStream resourceAsStream = FileUtil.class.getClassLoader().getResourceAsStream(resourceName);
            if(resourceAsStream==null){
                logger.error("classpath下没有该资源文件"+resourceName);
            }else{
                FileUtil.copyFile(resourceAsStream,file);
                logger.info("成功复制资源文件，到"+path);
            }
        }
    }

    public static void createFile(File file) throws IOException{
        File parentFile = file.getParentFile();
        if(!parentFile.exists()){
            createFile(parentFile);
        }
        if(isDirectory(file)){
            file.mkdir();
        }else{
            file.createNewFile();
        }
    }


    public static String read(String filePath)throws IOException {
        return read(new File(filePath));
    }

    public static boolean isFile(File file){
        return file.getName().contains(".");
    }

    public static boolean isDirectory(File file){
        return !isFile(file);
    }

}
