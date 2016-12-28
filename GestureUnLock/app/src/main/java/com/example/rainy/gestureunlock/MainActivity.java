package com.example.rainy.gestureunlock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.rainy.gestureunlock.view.UnLockView;

public class MainActivity extends Activity implements UnLockView.ResponseInput{

    private UnLockView mUnLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnLockView = (UnLockView) findViewById(R.id.unlockview);
        mUnLockView.setmRightPsw("14789");

    }

    @Override
    public void inputOK() {
        Toast.makeText(this, "密码正确", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void inputErr() {

        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
    }
}
