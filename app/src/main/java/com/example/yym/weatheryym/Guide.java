package com.example.yym.weatheryym;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.yym.adapter.PagerViewAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

public class Guide extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<View>  views;
    private ImageView[] dots;
    private int[] ids={R.id.iv1,R.id.iv2,R.id.iv3};
    private Button bun;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initViews();
        initDots();
    }
    //添加ViewPager中所展现的视图
    private void initViews(){
        LayoutInflater inflater =  LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide_pager1,null));
        views.add(inflater.inflate(R.layout.guide_paper2,null));
        views.add(inflater.inflate(R.layout.guide_paper3,null));
        pagerAdapter = new PagerViewAdapter(views,this);
        viewPager = findViewById(R.id.guidePagerView);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
        bun = views.get(2).findViewById(R.id.get_weather);
        bun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Guide.this,MainActivity.class);
                startActivity(intent);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}


