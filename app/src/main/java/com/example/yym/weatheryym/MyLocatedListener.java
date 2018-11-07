package com.example.yym.weatheryym;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.yym.app.MyApplication;
import com.example.yym.bean.City;

import java.util.ArrayList;

public class MyLocatedListener implements BDLocationListener {
    public String cityAndProvince;
    public String cityCode;
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
          String city=bdLocation.getCity();
          String recity=city.replace("市","");
          String province=bdLocation.getProvince();
          String district =bdLocation.getDistrict();
          String reDistrict =district.replace("区","");
          Log.d("province",recity);
          Log.d("province",district);
          Log.d("province",province);
        ArrayList<City> cities;
        MyApplication myApplication= MyApplication.getInstance();
        cities=myApplication.getCityList();
        for(City cit:cities){
            Log.d("province",cit.getCity());
            if(cit.getCity().equals(reDistrict)){
                cityCode=cit.getNumber();
                cityAndProvince=cit.getCity()+" "+cit.getProvince();
                Log.d("province",cityCode);
            }
        }
    }
}
