package com.example.yym.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class PagerViewAdapter extends android.support.v4.view.PagerAdapter {
    private ArrayList<View> views;
    private Context context;
    public PagerViewAdapter(ArrayList<View> views,Context context){
        this.views=views;
        this.context=context;
    }
    //返回滑动视图的个数
    @Override
    public int getCount() {
        return views.size();
    }
    //用于创建position所在位置的视图,添加视图并返回视图的对象
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    //用于判断instantiateItem返回的对象是否和当前是同一个对象
    @Override
    public boolean isViewFromObject( View view, Object o) {
        return view==o;
    }

    //用户删除positon所指定的视图
    @Override
    public void destroyItem(ViewGroup container, int position,  Object object) {
         container.removeView(views.get(position));
    }
}
