
package com.pluginx.plugin.business;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.pluginx.plugin.PluginMessage;
import com.pluginx.plugin.fragment.PluginFragmentActivity;
import com.pluginx.sample.R;

public class TestPluginFragmentActivity extends PluginFragmentActivity {

    public TestPluginFragmentActivity() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // 插件启动时注册全部content provider
        getContext().registerProvider("com.test.provider", new CustomerProvider(getContext()));

        setContentView(R.layout.plugin_one);
        
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        startFragment(Fragment1.class, new Intent());
    }

    @Override
    protected Bitmap getIcon(Resources arg0) {
        Bitmap bm = ((BitmapDrawable)arg0.getDrawable(R.drawable.ic_launcher)).getBitmap();
        return bm;
    }

    @Override
    protected String getTitle(Resources arg0) {
        return arg0.getString(R.string.app_name);
    }

    @Override
    public void handleMessage(PluginMessage msg) {
        // TODO Auto-generated method stub
        
    }

}
