package com.pluginx.plugin.business;


import android.content.Intent;

import com.pluginx.plugin.PluginService;
import com.pluginx.plugin.util.LogUtil;

/**
 * 测试service在插件中运行
 * @author michaelzuo
 *
 */
public class CustomerService extends PluginService {

    public CustomerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }
    
    /**
     * 处理intent输入
     * @param intent
     */
    private void handleCommand(Intent intent) {
        if (intent == null) {
            return;
        }
        
        String msg = intent.getStringExtra("msg");
        LogUtil.i("onStartCommand:" + msg + "is " + this.toString());
    }

}
