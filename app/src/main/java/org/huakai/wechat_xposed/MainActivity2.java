package org.huakai.wechat_xposed;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by dell on 2019-1-8.
 */

public class MainActivity2 extends Activity {

    private PopupWindow mPopupWindow;
    private View rootView;
    private int select_index=1;
    private OnGotsmsBroadcastReceiver reciver = new OnGotsmsBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("wxRobot.action.on_ume_test_Respond");
//        registerReceiver(reciver, filter);
        Button button = (Button)findViewById(R.id.button);
        RadioGroup radioGroup=(RadioGroup)findViewById(R.id.radioGroup_sex_id);
        radioGroup.setOnCheckedChangeListener(listener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(reciver);
    }

    private void onButtonClick(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date d1=new Date(time);
        String t1=format.format(d1);
        switch (select_index){
            case 1:
                C2sGetFlightStatusByCode(t1);
                break;
            case 2:
                C2sSearchFlyByArea(t1);
                break;
            case 3:
                C2sGetPreFlightList(t1);
                break;
            default:
                break;
        }
    }

    private void C2sGetFlightStatusByCode(String t1){
        Intent mIntent = new Intent();
        mIntent.setAction("wxRobot.action.on_ume_test");
        mIntent.putExtra("what", select_index);
        mIntent.putExtra("flight_no", "CA1353");
        mIntent.putExtra("date", t1);
        mIntent.putExtra("rpid","1060029");
        mIntent.putExtra("vername","5.0");
        sendBroadcast(mIntent);
    }

    private void C2sSearchFlyByArea(String t1){
        t1 = t1.replace("-","");
        Intent mIntent = new Intent();
        mIntent.setAction("wxRobot.action.on_ume_test");
        mIntent.putExtra("what", select_index);
        mIntent.putExtra("rendcity", "ZHA");
        mIntent.putExtra("rstartcity", "PEK");
        mIntent.putExtra("date", t1);
        mIntent.putExtra("rpid","300028");
        mIntent.putExtra("vername","1.0");
        sendBroadcast(mIntent);
    }

    private void C2sGetPreFlightList(String t1){
        Intent mIntent = new Intent();
        mIntent.setAction("wxRobot.action.on_ume_test");
        mIntent.putExtra("what", select_index);
        mIntent.putExtra("destAirportCode", "PEK");
        mIntent.putExtra("deptAirportCode", "SYX");
        mIntent.putExtra("date", t1);
        mIntent.putExtra("rpid","1060030");
        mIntent.putExtra("flight_no","CA1345");
        mIntent.putExtra("regNo","B6961");
        mIntent.putExtra("std","15:30");
        sendBroadcast(mIntent);
    }

    private RadioGroup.OnCheckedChangeListener listener=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int id= group.getCheckedRadioButtonId();
            switch (id) {
                case R.id.search_by_no:
                    select_index=1;
                    break;
                case R.id.search_by_city:
                    select_index=2;
                    break;
                case R.id.search_per_flight:
                    select_index=3;
                    break;
                default:
                    break;
            }
        }
    };

    private class OnGotsmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
