package com.dlong.creeper.util;

import com.dlong.creeper.annotation.csv.CsvHeader;

import java.util.Date;

public class TestCsv {
    @CsvHeader(value="姓名1",order = 1)
    private String name;
    @CsvHeader(value="性别3",order = 3)
    private String sex;
    @CsvHeader(value="年龄2",order = 2)
    private Integer age;
    @CsvHeader(value="生日4",order = 4)
    private Date birthday;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
