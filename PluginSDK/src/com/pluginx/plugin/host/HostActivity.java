package com.pluginx.plugin.host;

import com.pluginx.plugin.Plugin;
import com.pluginx.plugin.PluginActivity;
import com.pluginx.plugin.PluginManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

/**
 * 宿主Activity 
 * 插件自身不具备创建Activity的能力
 * 所以插件寄生在宿主Activity上
 * @author michaelzuo
 *
 */
public class HostActivity extends FragmentActivity {
    
    /**
     * 插件加载类型
     */
    public static final String EXTRA_NAME_PLUGIN_LOAD_TYPE = "EXTRA_NAME_PLUGIN_LOAD_TYPE";
    
    /**
     * 从内存中加载插件
     */
    public static final int PLUGIN_LOAD_TYPE_MEMORY = 1;
    
    /**
     * 动态加载方式加载插件
     */
    public static final int PLUGIN_LOAD_TYPE_DYNAMIC = 2;
    
    /**
     * extra name 记录PluginActivity的类名 用来做校验, 避免宿主Activity加载了错误的插件
     */
    private static final String EXTRA_NAME_PLUGIN_CLASS_NAME = "EXTRA_NAME_PLUGIN_CLASS_NAME";
    
    /**
     * 宿主对应的插件Activity实体
     */
    private PluginActivity mPluginActivity;
    
    /**
     * 启动宿主Activity 动态加载
     * 直接调用这个方法 需要先提前动态加载插件完毕
     * @param context
     * @param classNameOfPlugin 插件的类名
     * @param baseIntent
     * @return 是否启动成功 如果根据indexAtPluginManager无法从PluginManager中找到插件 或者 找到的插件 不是一个PluginActivity 返回false
     */
    public static void startHostActivity(Context context, String classNameOfPlugin, Intent baseIntent) {
        startHostActivity(context, classNameOfPlugin, baseIntent, PLUGIN_LOAD_TYPE_DYNAMIC);
    }
    
    /**
     * 启动宿主Activity 动态加载
     * 直接调用这个方法 需要先提前动态加载插件完毕
     * @param context
     * @param classNameOfPlugin 插件的类名
     * @param baseIntent
     * @param loadType 加载类型 {@link #PLUGIN_LOAD_TYPE_DYNAMIC #PLUGIN_LOAD_TYPE_MEMORY}
     * @return 是否启动成功 如果根据indexAtPluginManager无法从PluginManager中找到插件 或者 找到的插件 不是一个PluginActivity 返回false
     */
    public static void startHostActivity(Context context, String classNameOfPlugin, Intent baseIntent, int loadType) {
        Intent intent = getLaunchIntent(context, classNameOfPlugin, baseIntent);
        intent.putExtra(EXTRA_NAME_PLUGIN_LOAD_TYPE, loadType);
        context.startActivity(intent);
    }
    
    /**
     * 得到启动对应插件的intent
     * @param context
     * @param classNameOfPlugin
     * @param baseIntent
     * @return
     */
    public static Intent getLaunchIntent(Context context, String classNameOfPlugin, Intent baseIntent) {
        Intent intent = new Intent(baseIntent);
        intent.setClass(context, HostActivity.class);
        intent.putExtra(EXTRA_NAME_PLUGIN_CLASS_NAME, classNameOfPlugin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPluginActivity = pluginValidate(getIntent());
        
        if (mPluginActivity == null) {
            return;
        }
        
        // 传递宿主Activity给插件Activity
        mPluginActivity.setHost(this);

        int pluginLoadType = getIntent().getIntExtra(EXTRA_NAME_PLUGIN_LOAD_TYPE, 0);
        switch (pluginLoadType) {
            case PLUGIN_LOAD_TYPE_MEMORY:
                // 内存方式加载不需要设置context的代理,因为插件和宿主都运行在同一个context下
                break;
            case PLUGIN_LOAD_TYPE_DYNAMIC:
                // 动态加载方式再装配代理
                mPluginActivity.assembleDelegate();
                break;
        }
        
        // notify PluginActivity onCreate
        mPluginActivity.setIntent(getIntent());
        mPluginActivity.onCreate(savedInstanceState);
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        if (mPluginActivity != null) {
            mPluginActivity.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (mPluginActivity != null) {
            mPluginActivity.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        if (mPluginActivity != null) {
            mPluginActivity.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (mPluginActivity != null) {
            mPluginActivity.onDestroy();
        }
    }

    @Override
    public void onBackPressed() {
        // 交由插件处理
        if (mPluginActivity != null) {
            mPluginActivity.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 先交由插件处理 如果插件处理了 则直接返回true 表示 MotionEvent已经被处理
        if (mPluginActivity != null && mPluginActivity.dispatchTouchEvent(ev)) {
            return true;
        }
        
        return super.dispatchTouchEvent(ev);
    }
    
    /**
     * 插件有效性判断
     * 
     * 如果 插件已经失效 则抛出异常
     * 
     * important 在正常逻辑下 是不应该会出现插件失效的
     * 
     * 在这里抛出异常 起的是 ASSERT的作用
     * 
     * @param intent
     * @return HostActivity当前展示的插件
     */
    private PluginActivity pluginValidate(Intent intent) {
        String pluginClassName = intent.getStringExtra(EXTRA_NAME_PLUGIN_CLASS_NAME);
        
        Plugin plugin = PluginManager.getInstance().getPlugin(pluginClassName);
        
        // 如果Plugin为空 或者 不是一个PluginActivity 表示插件列表资源和索引的对应关系已经失效 则直接Finish掉自己 
        if (plugin == null || !(plugin instanceof PluginActivity)) {
            finish();
            throw new RuntimeException(pluginClassName + " is null or is not a PluginActivity");
        }
        
        // 如果Plugin的ClassName对应不上 表示宿主目前使用的插件 并不是想启动的插件
        String currentPluginClassName = plugin.getClass().getName();
        if (!currentPluginClassName.equals(pluginClassName)) {
            finish();
            throw new RuntimeException("current plugin is " + currentPluginClassName + " but target is " + pluginClassName);
        }
        
        
        return (PluginActivity)plugin;
    }
}
