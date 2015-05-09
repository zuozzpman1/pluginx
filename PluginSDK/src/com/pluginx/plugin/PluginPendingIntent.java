
package com.pluginx.plugin;

import android.app.PendingIntent;
import android.content.Intent;

/**
 * 对系统pending intent的封装
 * 
 * @author dazhengyang
 */
public class PluginPendingIntent {

    public static PendingIntent getBroadcast(PluginContext context, int requestCode, Intent intent,
            int flags) {
        IPluginContextDelegate delegate = context.clearDelegate();
        PendingIntent i = PendingIntent.getBroadcast(context, requestCode, intent, flags);
        context.setDelegate(delegate);
        return i;
    }

    public static PendingIntent getActivity(PluginContext context,int requestCode, Intent intent,
            int flags) {
        IPluginContextDelegate delegate = context.clearDelegate();
        PendingIntent i = PendingIntent.getActivity(context, requestCode, intent, flags);
        context.setDelegate(delegate);
        return i;
    }
}
