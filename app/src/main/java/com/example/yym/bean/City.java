package com.example.yym.bean;
//数据库中对应的bean类
public class City {
    //省份
    private String province;
    //城市名称
    private String city;
    //城市编码
    private String number;
    private String firstPY;
    private String allPY;
    private String allFristPY;

    public City(String province,String city,String number,String firstPY,String allPY,String allFristPY){
        this.province=province;
        this.city=city;
        this.number=number;
        this.firstPY=firstPY;
        this.allPY=allPY;
        this.allFristPY=allFristPY;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFirstPY() {
        return firstPY;
    }

    public void setFirstPY(String firstPY) {
        this.firstPY = firstPY;
    }

    public String getAllPY() {
        return allPY;
    }

    public void setAllPY(String allPY) {
        this.allPY = allPY;
    }

    public String getAllFristPY() {
        return allFristPY;
    }

    public void setAllFristPY(String allFristPY) {
        this.allFristPY = allFristPY;
    }
}
