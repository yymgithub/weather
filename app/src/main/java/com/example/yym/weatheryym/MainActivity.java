package com.example.yym.weatheryym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.yym.adapter.PagerViewAdapter;
import com.example.yym.util.NetUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.WeakHashMap;

import com.example.yym.bean.TodayWeather;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int LA = 1;
    //对应主界面刷新按钮
    private ImageView mUpdateBtn;
    //对应主界面的切换城市按钮
    private ImageView mCitySelect;
    //对应主界面城市，时间等控件
    private TextView cityTv,timeTv,humidityTv,weekTv,
            pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv;
    //对应主界面显示天气情况的图片控件，对应界面表示PM的图片控件
    private ImageView weatherImg,pmImg;
    //界面进度条控件
    private ProgressBar progressBar;
    //定义界面,实现获取未来6天天气
    private PagerViewAdapter pagerViewAdapter;
    private ViewPager viewPager;
    private ArrayList<View> views;
    private ImageView[] dots;
    private int[] ids={R.id.iv_mian1,R.id.iv_mina2};
    //界面显示其余天天气控件
    private TextView  dateOtv1,dateOtv2,dateOtv3,dateOtv4,tempertureOtv1,tempertureOtv2,tempertureOtv3,tempertureOtv4,
            cliamteOtv1,windOtv1,cliamteOtv2,windOtv2,cliamteOtv3,windOtv3,cliamteOtv4,windOtv4;
    private ImageView typeImg1,typeImg2,typeImg3,typeImg4;

    //定位功能实现
    private ImageView locatedImg;
    public LocationClient locationClient=null;
    private MyLocatedListener myLocatedListener=new MyLocatedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加对应界面为activity_main.xml
        setContentView(R.layout.activity_main);
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        //设置控件点击监听事件
        mUpdateBtn.setOnClickListener(this);
        locatedImg= findViewById(R.id.title_location);
        locatedImg.setOnClickListener(this);
        mCitySelect=findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        progressBar=findViewById(R.id.title_update_progress);
        progressBar.setVisibility(View.GONE);
        //检查网络连接是否正常
        if(NetUtil.getNetWorkState(this)!=NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this,"网络ok",Toast.LENGTH_LONG);
            }
        else{
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG);
            }
        initViews();
        initDots();
        initView();


        //SharePreferences操作，首次进入时取得城市天气为上次退出时所选择
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("cityCode","101010100");
//            editor.putString("cityName","北京");
//            editor.commit();
            String cityCode=sharedPreferences.getString("cityCode","101010100");
            queryWeahterCode(cityCode);
            locationClient=new LocationClient(getApplicationContext());
            locationClient.registerLocationListener(myLocatedListener);
            initLocation();

    }

    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        option.setOpenGps(true); // 打开gps
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        locationClient.setLocOption(option);
    }
    @Override
    public void onClick(View v) {
        //主界面点击为切换城市按钮
        if(v.getId()==R.id.title_city_manager){
            Intent i= new Intent(this,SelectCity.class);
//            startActivity(i);
            startActivityForResult(i,1);
        }
        //主界面点击为界面刷新按钮
        if(v.getId()==R.id.title_update_btn){
            //用SharedPreferences文件作为界面之间城市存储变化的方式
            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.GONE);
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) title.getLayoutParams();
//            layoutParams.addRule(RelativeLayout., R.id.imageButtonCursor);
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String cityCode=sharedPreferences.getString("cityCode","101010100");
            Log.d("myWeather",cityCode);
            queryWeahterCode(cityCode);
        }
        if(v.getId()==R.id.title_location){
            progressBar.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.GONE);
            if(locationClient.isStarted()){
                locationClient.stop();
            }
            locationClient.start();
            final Handler LAHandler = new Handler(){
                public void handleMessage(Message msg){
                    switch(msg.what){
                        case LA:
                            if(msg.obj!=null){
                                queryWeahterCode(myLocatedListener.cityCode);
                                SharedPreferences sharedPreferences= getSharedPreferences("config",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("cityCode",myLocatedListener.cityCode);
                                editor.putString("cityName",myLocatedListener.cityAndProvince);
                                editor.commit();
                                progressBar.setVisibility(View.GONE);
                                mUpdateBtn.setVisibility(View.VISIBLE);
                            }
                            myLocatedListener.cityCode=null;
                            break;
                        default:break;
                    }
                }
            };


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while(myLocatedListener.cityCode==null){
                            Thread.sleep(2000);
                        }
                        Message msg= new Message();
                        msg.what=LA;
                        msg.obj=myLocatedListener.cityCode;
                        LAHandler.sendMessage(msg);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
//用于接受子界面返回时的操作
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode ==1 && resultCode==RESULT_OK){
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String newCityCode = sharedPreferences.getString("cityCode","101010100");
            Log.d("myWeather","选择城市代码为"+newCityCode);
                queryWeahterCode(newCityCode);
        }
    }

//HTTP获取数据
    private  void queryWeahterCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con=null;
                ArrayList<TodayWeather> weathers=null;
                try{
                    URL url=new URL(address);
                    con=(HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in=con.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String reaponseStr=response.toString();
                    Log.d("myWeather",reaponseStr);
                    weathers= parseXML(reaponseStr);
                    if(weathers!=null){
                        Log.d("myWeather",weathers.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=weathers;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }finally {
                    if(con!=null){
                        con.disconnect();
                    }
                }
            }
        }

        ).start();
    }
//解析通过网络获取得到的XML文件
    private ArrayList<TodayWeather> parseXML(String xmldata){
        ArrayList<TodayWeather> weathers= null;
        TodayWeather todayWeather =null;
//        int fengxiangCount=0;
//        int fengliCount=0;
//        int dataCount=0;
//        int highCount=0;
//        int lowCount=0;
//        int typeCount=0;

        String city = null;
        String updateTime= null;
        String shidu = null;
        String wendu = null;
        String quality = null;
        String pm2_5 = null;
        String fengli = null;
        String fengxiang =null;
        try{
            XmlPullParserFactory fac=XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser=fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType =xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    //eventType为读入xml文档指针，为开头一行，去掉
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            weathers=new ArrayList<TodayWeather>();
                        }
                        if(weathers!=null) {
                                if (xmlPullParser.getName().equals("city")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                                    city = xmlPullParser.getText();
                                } else if (xmlPullParser.getName().equals("updatetime")) {


                                    eventType = xmlPullParser.next();
                                    updateTime = xmlPullParser.getText();
//                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("shidu")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                    shidu = xmlPullParser.getText();
                                } else if (xmlPullParser.getName().equals("wendu")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                    wendu = xmlPullParser.getText();
                                } else if (xmlPullParser.getName().equals("pm25")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                    pm2_5 = xmlPullParser.getText();
                                } else if (xmlPullParser.getName().equals("quality")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                    quality = xmlPullParser.getText();
                                } else if (xmlPullParser.getName().equals("fengxiang")) {
                                    if(fengxiang != null){
                                        eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                        todayWeather.setFengxiang(xmlPullParser.getText());
                                    }
                                    else{
                                        eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                        fengxiang = xmlPullParser.getText();
                                    }

                                } else if (xmlPullParser.getName().equals("fengli")) {
                                    if(fengli != null){
                                        eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                        todayWeather.setFengli(xmlPullParser.getText());
                                    }
                                    else{
                                        eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                        fengli = xmlPullParser.getText();
                                    }
                                } else if (xmlPullParser.getName().equals("date")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeahter", "date: " + xmlPullParser.getText());
                                    todayWeather.setDate(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("high")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                    todayWeather.setHigh(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("low")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                    todayWeather.setLow(xmlPullParser.getText());
                                } else if (xmlPullParser.getName().equals("type")) {
                                    eventType = xmlPullParser.next();
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                    todayWeather.setType(xmlPullParser.getText());
                                }else if (xmlPullParser.getName().equals("weather")) {
                                    todayWeather = new TodayWeather();
                                    todayWeather.setQuality(quality);
                                    todayWeather.setPm25(pm2_5);
                                    todayWeather.setWendu(wendu);
                                    todayWeather.setShidu(shidu);
                                    todayWeather.setUpdatetime(updateTime);
                                    todayWeather.setCity(city);
                                    todayWeather.setFengli(fengli);
                                    todayWeather.setFengxiang(fengxiang);
                                }

//                            if (xmlPullParser.getName().equals("city")) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "city: " + xmlPullParser.getText());
//                                toadayWeather.setCity(xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("updatetime")) {
//                                eventType = xmlPullParser.next();
//                                toadayWeather.setUpdatetime(xmlPullParser.getText());
////                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("shidu")) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
//                                toadayWeather.setShidu(xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("wendu")) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
//                                toadayWeather.setWendu(xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("pm25")) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
//                                toadayWeather.setPm25(xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("quality")) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
//                                toadayWeather.setQuality(xmlPullParser.getText());
//                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
//                                toadayWeather.setFengxiang(xmlPullParser.getText());
//                                fengxiangCount++;
//                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
//                                toadayWeather.setFengli(xmlPullParser.getText());
//                                fengliCount++;
//                            } else if (xmlPullParser.getName().equals("date") && dataCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeahter", "date: " + xmlPullParser.getText());
//                                toadayWeather.setDate(xmlPullParser.getText());
//                                dataCount++;
//                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "high: " + xmlPullParser.getText());
//                                toadayWeather.setHigh(xmlPullParser.getText());
//                                highCount++;
//                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "low: " + xmlPullParser.getText());
//                                toadayWeather.setLow(xmlPullParser.getText());
//                                lowCount++;
//                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
//                                eventType = xmlPullParser.next();
////                                Log.d("myWeather", "type: " + xmlPullParser.getText());
//                                toadayWeather.setType(xmlPullParser.getText());
//                                typeCount++;
//                            }
                        }
                            break;
                        //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        if(xmlPullParser.getName().equals("day")){
                            weathers.add(todayWeather);
                        }
                        break;
                }
                eventType=xmlPullParser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e ){
            e.printStackTrace();
        }
        Log.d("weathers",weathers.toString());
        return weathers;
    }
//主界面控件的初始化
    void initView(){
        city_name_Tv=findViewById(R.id.title_city_name);
        cityTv=findViewById(R.id.city);
        timeTv=findViewById(R.id.time);
        humidityTv=findViewById(R.id.humidity);
        weekTv=findViewById(R.id.week_today);
        pmDataTv=findViewById(R.id.pm_data);
        pmQualityTv=findViewById(R.id.pm2_5_quality);
        pmImg=findViewById(R.id.pm2_5_img);
        temperatureTv=findViewById(R.id.tmperature);
        climateTv=findViewById(R.id.climate);
        windTv=findViewById(R.id.wind);
        weatherImg=findViewById(R.id.weather_img);


        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        dateOtv1=views.get(0).findViewById(R.id.nextWeather1);
        dateOtv2=views.get(0).findViewById(R.id.nextWeather2);
        dateOtv3=views.get(1).findViewById(R.id.nextWeather3);
        dateOtv4=views.get(1).findViewById(R.id.nextWeather4);
        tempertureOtv1= views.get(0).findViewById(R.id.next_tmperature1);
        tempertureOtv2=views.get(0).findViewById(R.id.next_tmperature2);
        tempertureOtv3=views.get(1).findViewById(R.id.next_tmperature3);
        tempertureOtv4=views.get(1).findViewById(R.id.next_tmperature4);
        cliamteOtv1=views.get(0).findViewById(R.id.next_climate1);
        windOtv1=views.get(0).findViewById(R.id.next_wind1);
        cliamteOtv2=views.get(0).findViewById(R.id.next_climate2);
        windOtv2=views.get(0).findViewById(R.id.next_wind2);
        cliamteOtv3=views.get(1).findViewById(R.id.next_climate3);
        windOtv3=views.get(1).findViewById(R.id.next_wind3);
        cliamteOtv4=views.get(1).findViewById(R.id.next_climate4);
        windOtv4=views.get(1).findViewById(R.id.next_wind4);
        typeImg1=views.get(0).findViewById(R.id.nextPic1);
        typeImg2=views.get(0).findViewById(R.id.nextPic2);
        typeImg3=views.get(1).findViewById(R.id.nextPic3);
        typeImg4=views.get(1).findViewById(R.id.nextPic4);

        dateOtv1.setText("N/A");
        dateOtv2.setText("N/A");
        dateOtv3.setText("N/A");
        dateOtv4.setText("N/A");
        tempertureOtv1.setText("N/A");
        tempertureOtv2.setText("N/A");
        tempertureOtv3.setText("N/A");
        tempertureOtv4.setText("N/A");
        cliamteOtv1.setText("N/A");
        windOtv1.setText("N/A");
        cliamteOtv2.setText("N/A");
        windOtv2.setText("N/A");
        cliamteOtv3.setText("N/A");
        windOtv3.setText("N/A");
        cliamteOtv4.setText("N/A");
        windOtv4.setText("N/A");


    }

//更新返回的数据到界面表示
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+ "发布");
        humidityTv.setText("湿度:"+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        if(todayWeather.getPm25()!=null){
            int pm25=Integer.parseInt(todayWeather.getPm25());
            if(pm25<=50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }
            else if(pm25>50&&pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }
            else if(pm25>100&&pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }
            else if(pm25>150&&pm25<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }
            else if(pm25>200&&pm25<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }
            else{
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }
        if(todayWeather.getType()!=null){
            switch(todayWeather.getType()){
                case "暴雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "晴":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    weatherImg.setImageResource((R.drawable.biz_plugin_weather_shachenbao));
                    break;
                case "特大暴雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴" :
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪" :
                    weatherImg.setImageResource((R.drawable.biz_plugin_weather_zhenxue));
                    break;
                case "阵雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    weatherImg.setImageResource((R.drawable.biz_plugin_weather_zhongxue));
                    break;
                case "中雨":
                    weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                default:
                    break;
            }
        }
        Toast.makeText(MainActivity.this,"更新成功!",Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        mUpdateBtn.setVisibility(View.VISIBLE);
    }
//主线程处理子线程返回的数据
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    ArrayList<TodayWeather> weathers = (ArrayList<TodayWeather>) msg.obj;
                    Log.d("main_weather",weathers.toString());
                    updateTodayWeather(weathers.get(0));
                    updateOtherDayWeather(weathers);
                    break;
                default:
                    break; }
        } };

    //添加ViewPager中所展现的视图
    private void initViews(){
        LayoutInflater inflater =  LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.first_three_weather,null));
        views.add(inflater.inflate(R.layout.second_three_weather,null));
        pagerViewAdapter = new PagerViewAdapter(views,this);
        viewPager = findViewById(R.id.mainPagerView);
        viewPager.setAdapter(pagerViewAdapter);
        viewPager.setOnPageChangeListener(this);
    }
    private void initDots(){
        dots =new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i]=findViewById(ids[i]);
        }
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        for (int j=0;j<ids.length;j++){
            if(j==i){
                dots[j].setImageResource(R.drawable.page_indicator_focused);
            }
            else{
                dots[j].setImageResource(R.drawable.page_indicator_unfocused
                );
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void updateOtherDayWeather(ArrayList<TodayWeather> weathers){
        dateOtv1.setText(weathers.get(1).getDate());
        dateOtv2.setText(weathers.get(2).getDate());
        dateOtv3.setText(weathers.get(3).getDate());
        dateOtv4.setText(weathers.get(4).getDate());
        tempertureOtv1.setText(weathers.get(1).getHigh()+"~"+weathers.get(1).getLow());
        tempertureOtv2.setText(weathers.get(2).getHigh()+"~"+weathers.get(2).getLow());
        tempertureOtv3.setText(weathers.get(3).getHigh()+"~"+weathers.get(3).getLow());
        tempertureOtv4.setText(weathers.get(4).getHigh()+"~"+weathers.get(4).getLow());
        cliamteOtv1.setText(weathers.get(1).getType());
        windOtv1.setText(weathers.get(1).getFengxiang());
        cliamteOtv2.setText(weathers.get(2).getType());
        windOtv2.setText(weathers.get(2).getFengxiang());
        cliamteOtv3.setText(weathers.get(3).getType());
        windOtv3.setText(weathers.get(3).getFengxiang());
        cliamteOtv4.setText(weathers.get(4).getType());
        windOtv4.setText(weathers.get(4).getFengxiang());

        if(weathers.get(1).getType()!=null){
            upateImg(weathers.get(1).getType(),1);
        }
        if(weathers.get(2).getType()!=null){
            upateImg(weathers.get(2).getType(),2);
        }
        if(weathers.get(3).getType()!=null){
            upateImg(weathers.get(3).getType(),3);
        }
        if(weathers.get(4).getType()!=null){
            upateImg(weathers.get(4).getType(),4);
        }


    }

    private void upateImg(String s,int i){
        if (i == 1) {
            switch(s){
                case "暴雪":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "晴":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    typeImg1.setImageResource((R.drawable.biz_plugin_weather_shachenbao));
                    break;
                case "特大暴雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴" :
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪" :
                    typeImg1.setImageResource((R.drawable.biz_plugin_weather_zhenxue));
                    break;
                case "阵雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    typeImg1.setImageResource((R.drawable.biz_plugin_weather_zhongxue));
                    break;
                case "中雨":
                    typeImg1.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                default:
                    break;
            }

        }
        if (i == 2) {
            switch(s){
                case "暴雪":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "晴":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    typeImg2.setImageResource((R.drawable.biz_plugin_weather_shachenbao));
                    break;
                case "特大暴雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴" :
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪" :
                    typeImg2.setImageResource((R.drawable.biz_plugin_weather_zhenxue));
                    break;
                case "阵雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    typeImg2.setImageResource((R.drawable.biz_plugin_weather_zhongxue));
                    break;
                case "中雨":
                    typeImg2.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                default:
                    break;
            }

        }
        if (i == 3) {
            switch(s){
                case "暴雪":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "晴":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    typeImg3.setImageResource((R.drawable.biz_plugin_weather_shachenbao));
                    break;
                case "特大暴雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴" :
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪" :
                    typeImg3.setImageResource((R.drawable.biz_plugin_weather_zhenxue));
                    break;
                case "阵雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    typeImg3.setImageResource((R.drawable.biz_plugin_weather_zhongxue));
                    break;
                case "中雨":
                    typeImg3.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                default:
                    break;
            }

        }
        if (i == 4) {
            switch(s){
                case "暴雪":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "暴雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "大雪":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "大雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_dayu);
                    break;
                case "多云":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "雷阵雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨冰雹":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                    break;
                case "晴":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "沙尘暴":
                    typeImg4.setImageResource((R.drawable.biz_plugin_weather_shachenbao));
                    break;
                case "特大暴雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "雾":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "小雪":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "小雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "阴" :
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雨夹雪":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "阵雪" :
                    typeImg4.setImageResource((R.drawable.biz_plugin_weather_zhenxue));
                    break;
                case "阵雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "中雪":
                    typeImg4.setImageResource((R.drawable.biz_plugin_weather_zhongxue));
                    break;
                case "中雨":
                    typeImg4.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                default:
                    break;
            }

        }

    }
}
