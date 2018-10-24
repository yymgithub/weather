package com.example.yym.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yym.bean.City;
import com.example.yym.weatheryym.R;

import java.util.ArrayList;
//自定义adapter用于数据和界面的显示匹配
public class CityAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<City> myCityList;

    //从构造函数中获取cityList和Context对象.
    public CityAdapter(ArrayList<City> myCityList, Context context) {
        this.myCityList = myCityList;
        this.mContext = context;

    }
    @Override
    public int getCount() {
        return myCityList.size();
    }

    @Override
    public Object getItem(int position) {
        return myCityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //将数据和显示匹配到一起
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.city_item,parent,false);
        TextView cityName=convertView.findViewById(R.id.cityName);
//        TextView cityCode=convertView.findViewById(R.id.cityCode);
        if(myCityList.get(position).getCity().equals(myCityList.get(position).getProvince())){
            cityName.setText(myCityList.get(position).getCity());
        }
        else{
            cityName.setText(myCityList.get(position).getCity()+"  "+myCityList.get(position).getProvince());
        }
//        cityCode.setText(myCityList.get(position).getNumber());
        return convertView;
    }
}
