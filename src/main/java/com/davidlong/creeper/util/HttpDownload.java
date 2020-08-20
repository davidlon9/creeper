package com.davidlong.http.util;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/** 
 * 说明 
 * 利用httpclient下载文件 
 * maven依赖 
 * <dependency> 
*           <groupId>org.apache.httpcomponents</groupId> 
*           <artifactId>httpclient</artifactId> 
*           <version>4.0.1</version> 
*       </dependency> 
*  可下载http文件、图片、压缩文件 
*  bug：获取response header中Content-Disposition中filename中文乱码问题 
 * @author tanjundong 
 * 
 */  
public class HttpDownload {  
  
    public static final int cache = 10 * 1024;
    public static final boolean isWindows;
    public static final String splash;
    public static final String root;  
    static {  
        if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("windows")) {  
            isWindows = true;  
            splash = "\\";  
            root="D:";  
        } else {  
            isWindows = false;  
            splash = "/";  
            root="/search";  
        }  
    }  
      
    /** 
     * 根据url下载文件，文件名从response header头中获取 
     * @param url 
     * @return 
     */  
    public static String download(String url) {  
        return download(url, null);  
    }  
  
    /** 
     * 根据url下载文件，保存到filepath中 
     * @param url 
     * @param filepath 
     * @return 
     */  
    public static String download(String url, String filepath) {  
        try {  
            HttpClient client = new DefaultHttpClient();  
            HttpGet httpget = new HttpGet(url);  
            HttpResponse response = client.execute(httpget);  
  
            HttpEntity entity = response.getEntity();  
            InputStream is = entity.getContent();  
            if (filepath == null)  
                filepath = getFilePath(response);  
            File file = new File(filepath);  
            file.getParentFile().mkdirs();  
            FileOutputStream fileout = new FileOutputStream(file);  
            /** 
             * 根据实际运行效果 设置缓冲区大小 
             */  
            byte[] buffer=new byte[cache];  
            int ch = 0;  
            while ((ch = is.read(buffer)) != -1) {  
                fileout.write(buffer,0,ch);  
            }  
            is.close();  
            fileout.flush();  
            fileout.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }

    /**
     * 根据url下载文件，保存到filepath中
     * @param filepath
     * @return
     */
    public static void download(HttpResponse response, String filepath) {
        try {
            InputStream is = response.getEntity().getContent();
            if (filepath == null)
                filepath = getFilePath(response);
            File file = new File(filepath);
            file.getParentFile().mkdirs();
            FileOutputStream fileout = new FileOutputStream(file);
            /**
             * 根据实际运行效果 设置缓冲区大小
             */
            byte[] buffer=new byte[cache];
            int ch = 0;
            while ((ch = is.read(buffer)) != -1) {
                fileout.write(buffer,0,ch);
            }
            is.close();
            fileout.flush();
            fileout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 
     * 获取response要下载的文件的默认路径 
     * @param response 
     * @return 
     */  
    public static String getFilePath(HttpResponse response) {  
        String filepath = root + splash;  
        String filename = getFileName(response);  
  
        if (filename != null) {  
            filepath += filename;  
        } else {  
            filepath += getRandomFileName();  
        }  
        return filepath;  
    }  
    /** 
     * 获取response header中Content-Disposition中的filename值 
     * @param response 
     * @return 
     */  
    public static String getFileName(HttpResponse response) {  
        Header contentHeader = response.getFirstHeader("Content-Disposition");  
        String filename = null;  
        if (contentHeader != null) {  
            HeaderElement[] values = contentHeader.getElements();  
            if (values.length == 1) {  
                NameValuePair param = values[0].getParameterByName("filename");  
                if (param != null) {  
                    try {  
                        //filename = new String(param.getValue().toString().getBytes(), "utf-8");  
                        //filename=URLDecoder.decode(param.getValue(),"utf-8");  
                        filename = param.getValue();  
                    } catch (Exception e) {  
                        e.printStackTrace();  
                    }  
                }  
            }  
        }  
        return filename;  
    }  
    /** 
     * 获取随机文件名 
     * @return 
     */  
    public static String getRandomFileName() {  
        return String.valueOf(System.currentTimeMillis());  
    }  
    public static void outHeaders(HttpResponse response) {  
        Header[] headers = response.getAllHeaders();  
        for (int i = 0; i < headers.length; i++) {  
            System.out.println(headers[i]);  
        }  
    }  
//    public static void main(String[] args) {
////      String url = "http://bbs.btwuji.com/job.php?action=download&pid=tpc&tid=320678&aid=216617";
//        String url="http://857778.196.cmnet.data.tv002.com/down/098d1f1a4ef3007f26a52ab1f72b357a/2019-04%E3%80%8A%E4%BF%A1%E6%81%AF%E8%83%8C%E5%90%8E%E7%9A%84%E4%BF%A1%E6%81%AF%E3%80%8B.pdf?cts=yd-f-D117A150A35A56Fb022f&ctp=117A150A35A56&ctt=1582564609&limit=1&spd=36000&ctk=098d1f1a4ef3007f26a52ab1f72b357a&chk=d2be928c3c53b7c2a57d9b7557a25a71-2579740&mtd=1";
////      String filepath = "D:\\TestPDF\\a.torrent";
//        String filepath = "D:\\TestPDF\\a.pdf";
//        HttpDownload.download(url, filepath);
//    }

    public static void main(String[] args) {
        int size=3;
        String path="D:\\TestPDF\\a";
        final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 20; i++) {
            queue.add(path + i + ".txt");
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(size);

        for (int i = 0; i < size; i++) {
            threadPool.execute(() -> {
                try {
                    while(queue.size()>0){
                        String poll = queue.poll();
                        if(poll!=null){
                            StringBuilder sb=new StringBuilder();
                            for (int j = 0; j < 100000; j++) {
                                sb.append(Thread.currentThread().getName()).append("\n");
                            }
                            System.out.println(Thread.currentThread().getName()+" ready to write "+poll);
                            FileUtil.write(new File(poll),sb.toString());
                            System.out.println(Thread.currentThread().getName()+" write over "+poll);
                            Thread.sleep(1000);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    threadPool.shutdown();
                }
            });
        }


    }
}