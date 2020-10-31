package com.dlong.creeper.util;

import com.dlong.creeper.annotation.csv.CsvHeader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class CsvWriteUtil {
    private static final String filePath = "D:\\testcsv.csv";

    public static void write(String filePath, boolean append, List<String[]> datas) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter out = new FileWriter(filePath,append);
        CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        for (String[] data : datas) {
            printer.printRecord(data);
        }
        printer.flush();
        printer.close();
    }

    public static <T> void write(String filePath, boolean append, Class<T> headerClass, List<T> datas) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        List<Field> fields = FieldUtil.getDeclaredFieldsWithParent(headerClass);
        Field[] sortedFields=new Field[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            int order = i;
            if(field.isAnnotationPresent(CsvHeader.class)){
                CsvHeader csvHeader = field.getAnnotation(CsvHeader.class);
                order = csvHeader.order()-1;
            }
            if(sortedFields[order] == null){
                sortedFields[order] = field;
            }
        }
        CSVPrinter printer;
        FileWriter out = new FileWriter(filePath,append);
        if(append){
            printer = new CSVPrinter(out, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        }else{
            printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headerClass));
        }
        try {
            for (T t : datas) {
                int size = sortedFields.length;
                Object data[]=new Object[size];
                for (int i = 0; i < size; i++) {
                    Field field = sortedFields[i];
                    if(field!=null){
                        field.setAccessible(true);
                        data[i] = field.get(t);
                    }
                }
                printer.printRecord(data);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }finally {
            printer.flush();
            printer.close();
        }
    }

    public static <T> List<T> read(String filePath, Class<T> headerClass) throws IOException {
        List<Field> fields = FieldUtil.getDeclaredFieldsWithParent(headerClass);
        Field[] sortedFields=new Field[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            int order = i;
            if(field.isAnnotationPresent(CsvHeader.class)){
                CsvHeader csvHeader = field.getAnnotation(CsvHeader.class);
                order = csvHeader.order()-1;
            }
            if(sortedFields[order] == null){
                sortedFields[order] = field;
            }
        }
        Reader reader = new FileReader(filePath);
        try {
            List<CSVRecord> records = CSVFormat.RFC4180.withHeader(headerClass).parse(reader).getRecords();
            List<T> datas = new ArrayList<>();
            int dataSize = records.size();
            for (int i = 1; i < dataSize; i++) {
                CSVRecord csvRecord = records.get(i);
                int size = sortedFields.length;
                T t = headerClass.newInstance();
                for (int j = 0; j < size; j++) {
                    Field field = sortedFields[j];
                    if(field!=null){
                        field.setAccessible(true);
                        Class<?> type = field.getType();
                        if(type.equals(String.class)){
                            field.set(t,csvRecord.get(j));
                        }else if(type.equals(Integer.class)){
                            field.set(t,Integer.parseInt(csvRecord.get(j)));
                        }else if(type.equals(Date.class)){
                            field.set(t,new Date());
                        }
                    }
                }
                datas.add(t);
            }
            return datas;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return new ArrayList<>();
    }

    public static void readOne() throws IOException {
        Reader reader = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(reader);
        for (CSVRecord csvRecord : records) {
            System.out.println(
                    csvRecord.get(0) + "---" + csvRecord.get(1) + "---" + csvRecord.get(2) + "---" + csvRecord.get(3));
        }
        reader.close();
    }

    /**
     * 读取(手动定义标头访问列值)
     *
     * @author : lichenfei
     * @throws IOException
     * @date : 2019年3月18日
     * @time : 下午2:13:21
     *
     */
    public static void readTwo() throws IOException {
        Reader reader = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("name", "sex", "age", "birthday").parse(reader);
        for (CSVRecord csvRecord : records) {
            System.out.println(csvRecord.get("name") + "---" + csvRecord.get("sex") + "---" + csvRecord.get("age")
                    + "---" + csvRecord.get("birthday"));
        }
        reader.close();
    }

    public static void readThree() throws IOException {
        Reader reader = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);// 定义后必须和csv文件中的标头一致
        for (CSVRecord csvRecord : records) {// 第一行不会被打印出来
            System.out.println(csvRecord.get("姓名") + "---" + csvRecord.get("性别") + "---" + csvRecord.get("年龄") + "---"
                    + csvRecord.get("生日"));
        }
        reader.close();
    }

    public static void main(String[] args) throws IOException {
        List<TestCsv> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TestCsv testCsv = new TestCsv();
            testCsv.setName("张"+i);
            testCsv.setSex(i%2==0?"男":"女");
            testCsv.setAge(i+15);
            testCsv.setBirthday(new Date(System.currentTimeMillis()-i*100000));
            datas.add(testCsv);
        }
        write(filePath,true,TestCsv.class,datas);
        List<TestCsv> read = read(filePath, TestCsv.class);
        System.out.println();
    }
}
