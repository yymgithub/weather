package com.example.yym.weatheryym;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yym.adapter.CityAdapter;
import com.example.yym.app.MyApplication;
import com.example.yym.bean.City;

import java.util.ArrayList;

public class SelectCity  extends Activity implements View.OnClickListener {
    //选择城市界面返回按钮
    private ImageView mBackBtn;
    //选择城市界面可选城市选择按钮
    private ListView cityListView;
    //数据和界面逐条匹配的适配器
    private CityAdapter cityAdapter;
    //数据列表
    private ArrayList<City> myCityList;
    //选择城市界面，当前选中城市，城市名称
    private TextView titleName;
    //用于临时存储当前选择的城市
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        initView();
    }

    private void initView(){
        intent = new Intent();
        mBackBtn=findViewById(R.id.title_back);
        titleName=findViewById(R.id.title_name);
        mBackBtn.setOnClickListener(this);
        cityListView=findViewById(R.id.city_list);
        SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        String cityName=sharedPreferences.getString("cityName","北京");
        titleName.setText(cityName);
        MyApplication myApplication = (MyApplication) getApplication();
        myCityList=myApplication.getCityList();
        //初始化对应的适配器
        cityAdapter = new CityAdapter(myCityList,SelectCity.this );
        //适配器和List控件关联
        cityListView.setAdapter(cityAdapter);
        //ListView控件单条点击事件监听
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityCode=myCityList.get(position).getNumber();
                String cityName=null;
                if(myCityList.get(position).getCity().equals(myCityList.get(position).getProvince())){
                    cityName=myCityList.get(position).getCity();
                }
                else{
                    cityName=myCityList.get(position).getCity()+" "+myCityList.get(position).getProvince();
                }
                intent.putExtra("cityCode",cityCode);
                intent.putExtra("cityName",cityName);
                titleName.setText(cityName);
                Toast.makeText(SelectCity.this,cityCode,Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //点击界面返回主界面按钮
            case R.id.title_back :
                //SharePreferences操作
                if(intent.getStringExtra("cityCode")!=null) {
                    SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cityCode",intent.getStringExtra("cityCode"));
                    editor.putString("cityName",intent.getStringExtra("cityName"));
                    editor.commit();
                }
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }
}
