package com.pluginx.plugin.business;

import android.os.Bundle;

import com.pluginx.DebugActivity;
import com.pluginx.plugin.PluginActivity;
import com.pluginx.plugin.util.LogUtil;

public class TestLaunchActivity extends DebugActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LogUtil.i("DebugLaunchActivity: onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        LogUtil.i("DebugLaunchActivity: onDestroy");
    }

    @Override
    protected PluginActivity getDebugPluginActivity() {
        return new TestPluginFragmentActivity();
    }

}
