package com.example.yym.weatheryym;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SelectCity  extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn=findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back :
                //SharePreferences操作
//                SharedPreferences sharedPreferences=getSharedPreferences("config",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("main_city_code1","101010100");
//                editor.commit();
//                String cityCode=sharedPreferences.getString("main_city_code","101010100");
                Intent i =new Intent();
                i.putExtra("cityCode","101160101");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
}
