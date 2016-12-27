package com.example.rainy.gestureunlock;

import android.app.Activity;
import android.os.Bundle;

import com.example.rainy.gestureunlock.view.UnLockView;

public class MainActivity extends Activity {

    private UnLockView mUnLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnLockView = (UnLockView) findViewById(R.id.unlockview);
        mUnLockView.setmRightPsw("14789");

    }
}
