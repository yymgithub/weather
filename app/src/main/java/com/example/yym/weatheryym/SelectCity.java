package com.example.yym.weatheryym;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yym.adapter.CityAdapter;
import com.example.yym.app.MyApplication;
import com.example.yym.bean.City;
import com.example.yym.lay.ClearEditText;

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
    //搜索后筛选的数据
    private ArrayList<City> filterCityList;
    //选择城市界面，当前选中城市，城市名称
    private TextView titleName;
    //用于临时存储当前选择的城市
    private Intent intent;
    //城市搜索框
    private ClearEditText clearEditText;
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
        clearEditText=findViewById(R.id.search_edit);
        mBackBtn.setOnClickListener(this);
        cityListView=findViewById(R.id.city_list);
        SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
        String cityName=sharedPreferences.getString("cityName","北京");
        titleName.setText(cityName);
        MyApplication myApplication = (MyApplication) getApplication();
        myCityList=myApplication.getCityList();
        //初始化对应的适配器
        cityAdapter = new CityAdapter(myCityList,SelectCity.this );
        filterCityList =  new ArrayList<>();
        for(City city:myCityList){
            filterCityList.add(city);
        }
        //适配器和List控件关联
        cityListView.setAdapter(cityAdapter);
        //ListView控件单条点击事件监听
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cityCode=filterCityList.get(position).getNumber();
                String cityName=null;
                if(filterCityList.get(position).getCity().equals(filterCityList.get(position).getProvince())){
                    cityName=filterCityList.get(position).getCity();
                }
                else{
                    cityName=filterCityList.get(position).getCity()+" "+filterCityList.get(position).getProvince();
                }
                intent.putExtra("cityCode",cityCode);
                intent.putExtra("cityName",cityName);
                titleName.setText(cityName);
                Toast.makeText(SelectCity.this,cityCode,Toast.LENGTH_LONG);
            }
        });
        clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             filterData(s.toString());
             cityListView.setAdapter(cityAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    //根据搜索框输入字母尽心城市列表的筛选
    private void filterData(String searchCityName){
        filterCityList.clear();
        if(TextUtils.isEmpty(searchCityName.trim())){
            for(City city:myCityList){
                filterCityList.add(city);
            }
        }
        else{
            Log.d("myWeather",searchCityName);
            for(City city:myCityList){
                //输入字符为子字符串时
                if(city.getCity().contains(searchCityName)||city.getProvince().contains(searchCityName)||city.getFirstPY().contains(searchCityName)||city.getAllPY().contains(searchCityName)||city.getFirstPY().toLowerCase().contains(searchCityName)||city.getAllPY().toLowerCase().contains(searchCityName)) {
                    filterCityList.add(city);
//                    Log.d("myWeather",city.toString());
                }
            }
        }
        cityAdapter.updateListView(filterCityList);
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
