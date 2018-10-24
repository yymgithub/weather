package com.example.yym.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.yym.bean.City;
import com.example.yym.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyApplication  extends Application {
    private static  final String  TAG="MyAPP";
    private static MyApplication myApplication;
    private  CityDB mCityDB;
    private ArrayList<City> mCityList;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        myApplication =this;
        mCityDB=openCityDB();
        initCityList();
    }
    public  static  MyApplication getInstance(){
        return myApplication;
    }

    //生成读取数据库的类,将数据库中的数据读入到内存
    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "databases1"
                    + File.separator;//从数据库中读取城市列表

            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            } }
        return new CityDB(this, path);
    }

    private void initCityList(){
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        int i=0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            String cityProvince=city.getProvince();
            String firstPY=city.getFirstPY();
            String allPY=city.getAllPY();
            String allFirstPy=city.getAllFristPY();
            Log.d(TAG,cityCode+":"+cityName+":"+cityProvince+":"+firstPY+":"+allPY+allFirstPy);
        }
        Log.d(TAG,"i="+i);
        return true;
    }
//其他类获取application初始化时从数据库中获取得到的城市数据
    public ArrayList<City> getCityList() {
        return mCityList;
    }

}
