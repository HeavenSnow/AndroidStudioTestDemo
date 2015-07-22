package com.jack.androidtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *
 * Created by Jack on 15/7/10.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.btn_device_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_device_test:
                Intent intent = new Intent(this, DeviceTestActivity.class);
                startActivity(intent);
                DeviceTestActivity.testFunc();
                break;
            default:
                break;
        }
    }
    /**
     *
     */
    public void onTest1()
    {

    }
}
