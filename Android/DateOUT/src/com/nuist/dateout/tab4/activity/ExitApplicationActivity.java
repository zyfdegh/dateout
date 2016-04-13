package com.nuist.dateout.tab4.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.nuist.dateout.R;
import edu.nuist.dateout.activity.MainActivity;
import edu.nuist.dateout.app.DateoutApp;

public class ExitApplicationActivity extends Activity implements OnClickListener
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);
        
        initView();
    }
    
    private void initView()
    {
        Button exitYes = (Button)findViewById(R.id.exitBtn0);
        Button exitNo = (Button)findViewById(R.id.exitBtn1);
        exitNo.setOnClickListener(this);
        exitYes.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.exitBtn0:
                finishApplication();
                break;
            case R.id.exitBtn1:
                finishPeresentActivity();
                break;
            default:
                break;
        }
    }
    
    /**
     * 否按钮后关闭当前Dialog Activity
     */
    private void finishPeresentActivity()
    {
        finish();
    }
    
    /**
     * 是按钮后 关闭应用程序
     * 
     */
    private void finishApplication()
    {
        
        // ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        // manager.restartPackage(getPackageName());
        
        finish();
        MainActivity.instance.finish();
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, ExitApplicationActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
        
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        DateoutApp.getInstance().onTerminate();
        System.exit(0);
    }
}
