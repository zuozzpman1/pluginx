package com.pluginx;

import com.pluginx.plugin.PluginActivity;
import com.pluginx.plugin.PluginContext;
import com.pluginx.plugin.PluginManager;
import com.pluginx.plugin.host.HostActivity;
import com.pluginx.plugin.util.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * 在插件进行开发时 提供一个调试的入口
 * 不需要 走插件打包 和 动态加载流程,减少开发成本
 * 在正式发布时 功能会被封掉
 * @author michaelzuo
 *
 */
public class DebugActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 只在DEBUG情况下才开启
        if (!LogUtil.DEBUG) {
            return;
        }
        
        // 内存加载插件
        PluginActivity pluginActivity = getDebugPluginActivity();
        pluginActivity.setContext(new PluginContext(this));
        
        PluginManager.getInstance().memoryLoad(pluginActivity);
        HostActivity.startHostActivity(this, pluginActivity.getClass().getName(), new Intent(), HostActivity.PLUGIN_LOAD_TYPE_MEMORY);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        PluginManager.getInstance().destroy();
        super.onDestroy();
    }

    /**
     * 子类可以返回一个 PluginActivity
     * @param context
     * @return
     */
    protected PluginActivity getDebugPluginActivity() {
        return null;
    }

}
