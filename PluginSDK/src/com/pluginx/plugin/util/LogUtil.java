package com.pluginx.plugin.util;

import android.util.Log;

public class LogUtil {
    
    public static final boolean DEBUG = true;
    
    private static final String TAG = "Plugin";
    
    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }
    
    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }
}
