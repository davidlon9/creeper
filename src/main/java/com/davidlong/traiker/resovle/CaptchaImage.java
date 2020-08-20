package com.davidlong.traiker.resovle;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CaptchaImage {
    public static final Map<Integer,Point> captchaImageCentrePos=new LinkedHashMap<>();

    //答案计算时，忽略了题目部分，所以有40的偏差
    public static final int ANSWER_Y_OFFSET = 40;

    static{
        captchaImageCentrePos.put(1,new Point(33,85));
        captchaImageCentrePos.put(2,new Point(105,85));
        captchaImageCentrePos.put(3,new Point(177,85));
        captchaImageCentrePos.put(4,new Point(250,85));
        captchaImageCentrePos.put(5,new Point(33,157));
        captchaImageCentrePos.put(6,new Point(105,157));
        captchaImageCentrePos.put(7,new Point(177,157));
        captchaImageCentrePos.put(8,new Point(250,157));
    }

    public static void createImage(String base64image,String filePath){
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(Base64.decodeBase64(base64image)));
            Graphics2D g = bi.createGraphics();
            g.setColor(Color.RED);
            g.setFont(new Font("微软雅黑",Font.BOLD,22));

            for (int i = 1; i <=8; i++) {
                Point point = CaptchaImage.captchaImageCentrePos.get(i);
                g.drawString(String.valueOf(i),(int) point.getX()-25,(int) point.getY()-25);
            }
            //生成图片
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
            ImageIO.write(bi,"png",fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAnswer(String imageIdxs){
        if(imageIdxs==null || "".equals(imageIdxs)){
            System.out.println("请输入1-8的数字串，例如:3 4");
            return null;
        }
        String spliter=null;
        if (imageIdxs.contains(",")) {
            spliter=",";
        }else if(imageIdxs.contains(" ")){
            spliter=" ";
        }
        StringBuilder sb=new StringBuilder();
        if(spliter!=null){
            String[] split = imageIdxs.split(spliter);
            try {
                for (String a : split) {
                    int idx = Integer.parseInt(a);
                    Point point = captchaImageCentrePos.get(idx);
                    sb.append(","+(int)point.getX()+","+ ((int)point.getY()-ANSWER_Y_OFFSET));
                }
            } catch (NumberFormatException e) {
                System.out.println("请输入1-8的数字串，例如:3 4");
            }
        }else{
            char[] chars = imageIdxs.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                int idx = ((int)chars[i])-48;
                if(idx>=1 & idx<=8){
                    Point point = captchaImageCentrePos.get(idx);
                    sb.append(","+(int)point.getX()+","+ ((int)point.getY()-ANSWER_Y_OFFSET));
                }else{
                    System.out.println("请输入1-8的数字串，例如:3 4");
                    break;
                }
            }
        }
        sb.delete(0,1);
        return sb.toString();
    }

    public static void main(String[] args) {
        String answer = getAnswer("147");
        System.out.println(answer);
    }
}
