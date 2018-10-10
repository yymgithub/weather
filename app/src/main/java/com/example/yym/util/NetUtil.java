package com.example.yym.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.renderscript.RenderScript;

public class NetUtil {
    public static final int NETWORN_NONE=0;
    public static final int NETWORN_WIFI=1;
    public static final int NETWORN_MOBILE=2;

    public static int getNetWorkState(Context context){
        ConnectivityManager connManager= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connManager.getActiveNetworkInfo();
        if(networkInfo==null) return NETWORN_NONE;
        int nType=networkInfo.getType();
        if(nType==NETWORN_WIFI)
            return NETWORN_WIFI;
        else if(nType==NETWORN_MOBILE)
            return NETWORN_MOBILE;
        return NETWORN_NONE;

    }

}
