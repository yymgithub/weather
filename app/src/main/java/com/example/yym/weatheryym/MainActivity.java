package com.example.yym.weatheryym;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.yym.bean.TodayWeather;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER = 1;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加对应界面为activity_main.xml
        setContentView(R.layout.activity_main);
        mUpdateBtn=(ImageView)findViewById(R.id.title_update_btn);
        //设置控件点击监听事件
        mUpdateBtn.setOnClickListener(this);
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
            initView();


        //SharePreferences操作，首次进入时取得城市天气为上次退出时所选择
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("cityCode","101010100");
//            editor.putString("cityName","北京");
//            editor.commit();
            String cityCode=sharedPreferences.getString("cityCode","101010100");
            queryWeahterCode(cityCode);
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
            if(NetUtil.getNetWorkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeahterCode(cityCode);
            }
            else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG);
            }
        }
    }
//用于接受子界面返回时的操作
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode ==1 && resultCode==RESULT_OK){
            SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
            String newCityCode = sharedPreferences.getString("cityCode","101010100");
            Log.d("myWeather","选择城市代码为"+newCityCode);
            if(NetUtil.getNetWorkState(this)!=NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeahterCode(newCityCode);
            }
            else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG);
            }
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
                TodayWeather todayWeather=null;
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
                    todayWeather= parseXML(reaponseStr);
                    if(todayWeather!=null){
                        Log.d("myWeather",todayWeather.toString());
                        Message msg =new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj=todayWeather;
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
    private TodayWeather parseXML(String xmldata){
        TodayWeather  toadayWeather=null;
        int fengxiangCount=0;
        int fengliCount=0;
        int dataCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;
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
                            toadayWeather=new TodayWeather();
                        }
                        if(toadayWeather!=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "city: " + xmlPullParser.getText());
                                toadayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                toadayWeather.setUpdatetime(xmlPullParser.getText());
//                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "shidu: " + xmlPullParser.getText());
                                toadayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "wendu: " + xmlPullParser.getText());
                                toadayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                toadayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                toadayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengxiang: " + xmlPullParser.getText());
                                toadayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "fengli: " + xmlPullParser.getText());
                                toadayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dataCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeahter", "date: " + xmlPullParser.getText());
                                toadayWeather.setDate(xmlPullParser.getText());
                                dataCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                toadayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                toadayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
//                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                toadayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                            break;
                        //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        }catch(XmlPullParserException e){
            e.printStackTrace();
        }catch (IOException e ){
            e.printStackTrace();
        }
        return toadayWeather;
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
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break; }
        } };


}
