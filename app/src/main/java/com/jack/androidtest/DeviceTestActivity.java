package com.jack.androidtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

public class DeviceTestActivity extends Activity {

    private TextView txt_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_test);

        txt_result = (TextView) findViewById(R.id.txt_result);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder();

                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                sb.append("getDeviceId:");
                sb.append(tm.getDeviceId());

                txt_result.setText(sb.toString());

                testFunc();
            }
        });
    }

    /**
     *
     */
    public static void testFunc() {

    }

    /**
     *
     */
    public static void testAdd() {

    }
}
