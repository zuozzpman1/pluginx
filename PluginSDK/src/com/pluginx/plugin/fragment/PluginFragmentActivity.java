
package com.pluginx.plugin.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MotionEvent;

import com.pluginx.plugin.PluginActivity;
import com.pluginx.plugin.fragment.PluginFragment.LifeCircle;

/**
 * Activity中的view 采用 fragment的方式进行管理
 * 
 * @author michaelzuo
 */
public abstract class PluginFragmentActivity extends PluginActivity {

    /**
     * Fragment管理器
     */
    private PluginFragmentManager mFragmentManager;

    public PluginFragmentActivity() {
        mFragmentManager = new PluginFragmentManager();
    }

    /**
     * makes the PluginActivity interacting with the user (based on its
     * containing activity being resumed).
     */
    public void onResume() {
        PluginFragment fragment = mFragmentManager.getFragment();
        // fragment在被start时候 有可能已经调用过onResume了 所以这里需要对lifeCircle的状态进行判断
        if (fragment != null && fragment.getLifeCircle() != LifeCircle.FRONT) {
            fragment.onResume();
        }
    }

    /**
     * PluginActivity is no longer interacting with the user either because its
     * activity is being paused.
     */
    public void onPause() {
        PluginFragment fragment = mFragmentManager.getFragment();
        if (fragment != null) {
            fragment.onPause();
        }
    }

    /**
     * called to do final cleanup of the PluginActivity's fragments.
     */
    public void onDestroy() {
        mFragmentManager.finishAll();
    }

    /**
     * Called by the system when the device configuration changes while your
     * component is running based on Activity onConfigurationChanged
     * 
     * @param newConfig
     */
    public void onConfigurationChanged(Configuration newConfig) {
        PluginFragment fragment = mFragmentManager.getFragment();
        if (fragment != null) {
            fragment.onConfigurationChanged(newConfig);
        }
    }

    /**
     * Android的Back键被点击
     */
    public void onBackPressed() {
        PluginFragment fragment = mFragmentManager.getFragment();

        // 如果fragment为NULL 交由上层处理
        if (fragment != null) {
            fragment.onBackPressed();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        PluginFragment fragment = mFragmentManager.getFragment();

        // 如果fragment处理了 则返回true
        if (fragment != null && fragment.dispatchTouchEvent(ev)) {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 启动一个fragment
     * 
     * @param classOfFragment
     * @param intent 启动fragment的意图描述
     */
    public void startFragment(Class<? extends PluginFragment> classOfFragment, Intent intent,
            int requestCode) {
        mFragmentManager.startPluginFragment(classOfFragment, this, intent, requestCode);
    }

    /**
     * 启动一个fragment
     * 
     * @param classOfFragment
     * @param intent 启动fragment的意图描述
     */
    public void startFragment(Class<? extends PluginFragment> classOfFragment, Intent intent) {
        mFragmentManager.startPluginFragment(classOfFragment, this, intent);
    }

    /**
     * 重写fragment切换时的 进入退出动画 需要在
     * {@link #startPluginFragment(Class, PluginFragmentActivity, Intent)} 之前调用
     * 才会生效
     * 
     * @param enterAnim
     * @param exitAnim
     */
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        mFragmentManager.overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * finish当前fragment
     * 
     * @return 是否已经到底了 没有fragment在可以被finish了
     */
    protected void finishFragment(PluginFragment fragement, int requestCode, int resultCode,
            Intent resultData) {
        mFragmentManager
                .finishPluginFragment(this, fragement, requestCode, requestCode, resultData);

        // 检查Fragment是否都已经被finish了
        boolean isAllFinished = mFragmentManager.isAllFinished();

        // 如果Fragment都被finish掉了 则调用super的onBackPressed()
        if (isAllFinished) {
            super.onBackPressed();
        }

    }

}
