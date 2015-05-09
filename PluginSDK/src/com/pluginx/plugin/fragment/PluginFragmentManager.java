
package com.pluginx.plugin.fragment;

import com.pluginx.plugin.fragment.PluginFragmentNavigation.BackStackEntry;
import com.pluginx.plugin.fragment.PluginFragmentTransition.IPluginFragmentTransitionListener;

import android.content.Intent;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 管理一组PluginFragment 负责对应PluginFragment的生命周期维护 以及PluginFragment间的切换
 * 
 * @author michaelzuo
 */
public class PluginFragmentManager {

    /**
     * PluginFragment的缓存的大小
     */
    private static final int CACHE_SIZE_OF_FRAGMENT = 10;

    /**
     * PluginFragment的缓存
     */
    private LruCache<Class<? extends PluginFragment>, PluginFragment> mFragmentCache;

    /**
     * PluginFragment导航
     */
    private PluginFragmentNavigation mNavigation;

    /**
     * 当前使用的Fragment
     */
    private PluginFragment mCurFragment;

    /**
     * 进入动画
     */
    private int mEnterAnim;

    /**
     * 退出动画
     */
    private int mExitAnim;

    PluginFragmentManager() {
        mFragmentCache = new LruCache<Class<? extends PluginFragment>, PluginFragment>(
                CACHE_SIZE_OF_FRAGMENT) {

            @Override
            protected void entryRemoved(boolean evicted, Class<? extends PluginFragment> key,
                    PluginFragment oldValue, PluginFragment newValue) {

                // TODO 如果是当前的 则不要回收
                if (oldValue == mCurFragment) {
                    return;
                }

                // TODO saveInstance and prepare to recreate

                // 在fragment移出缓存前 释放掉fragment的资源
                destroyFragment(oldValue);
            }

        };

        mNavigation = new PluginFragmentNavigation();

        resetPendingTransition();
    }

    /**
     * 重置fragment切换的进入和退出动画
     */
    private void resetPendingTransition() {
        mEnterAnim = PluginFragmentTransition.ANIM_DEFAULT;
        mExitAnim = PluginFragmentTransition.ANIM_DEFAULT;
    }

    /**
     * 启动一个PluginFragment
     * 
     * @param classOfFragment
     * @param pluginFragmentActivity
     * @param intent 启动fragment时的意图描述
     */
    public void startPluginFragment(Class<? extends PluginFragment> classOfFragment,
            final PluginFragmentActivity pluginFragmentActivity, Intent intent) {

        startPluginFragment(classOfFragment, pluginFragmentActivity, intent, PluginFragment.DEFAULT_REQUEST_CODE);
    }

    /**
     * 启动一个PluginFragment
     * 
     * @param classOfFragment
     * @param pluginFragmentActivity
     * @param intent 启动fragment时的意图描述
     */
    public void startPluginFragment(Class<? extends PluginFragment> classOfFragment,
            final PluginFragmentActivity pluginFragmentActivity, Intent intent, int requestCode) {

        // 根据class 获得 fragment
        final PluginFragment fragment = getFragmentByClass(classOfFragment, pluginFragmentActivity);

        // 传递意图
        fragment.setIntent(intent);
        // 传递requestCode
        fragment.setRequestCode(requestCode);

        PluginFragment lastFragment = mCurFragment;
        mCurFragment = fragment;

        // 构建BackEntry 指定跳转回上一个fragment
        BackStackEntry backStackEntry = PluginFragmentNavigation.buildBackStackEntry(
                fragment.getTitle(), fragment.getClass());
        mNavigation.pushBackStackEntry(backStackEntry);

        // inflate view
        transitFragment(pluginFragmentActivity, fragment, lastFragment,
                new IPluginFragmentTransitionListener() {

                    @Override
                    public void onEnd(View contentView) {
                        if (contentView != null) {

                            // 设置contentView
                            pluginFragmentActivity.setContentView(contentView);
                        }
                    }
                });

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
        mEnterAnim = enterAnim;
        mExitAnim = exitAnim;
    }

    /**
     * 切换到对应fragment
     * 
     * @param pluginFragmentActivity
     * @param front 需要切到前台的fragment
     * @param back 将要移动到后台fragment
     * @param listener Fragment切换的监听
     */
    private void transitFragment(PluginFragmentActivity pluginFragmentActivity,
            PluginFragment front, PluginFragment back, IPluginFragmentTransitionListener listener) {
        // inflate view
        LayoutInflater layoutInflater = LayoutInflater.from(pluginFragmentActivity.getContext());

        PluginFragmentTransition.transition(pluginFragmentActivity.getContext(), front, back,
                layoutInflater, pluginFragmentActivity, listener, mEnterAnim, mExitAnim);

        // 每次切换后 重置切换动画
        resetPendingTransition();
    }

    /**
     * 根据class 获得对应的fragment 优先从缓存中获取
     * 
     * @param classOfFragment
     * @param pluginFragmentActivity fragment附着的{@link PluginFragmentActivity}
     * @return
     */
    private PluginFragment getFragmentByClass(Class<? extends PluginFragment> classOfFragment,
            PluginFragmentActivity pluginFragmentActivity) {
        // 先从缓存中找fragment
        PluginFragment fragment = mFragmentCache.get(classOfFragment);
        // 如果fragment为null 则构建一个
        if (fragment == null) {
            try {
                fragment = PluginFragmentFactory.createPluginFragment(classOfFragment,
                        pluginFragmentActivity);

                // 加入缓存
                mFragmentCache.put(classOfFragment, fragment);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return fragment;
    }

    /**
     * 销毁一个 {@link PluginFragment}
     * 
     * @param fragment
     */
    private void destroyFragment(PluginFragment fragment) {
        if (fragment == null) {
            return;
        }

        switch (fragment.getLifeCircle()) {
            case UN_CREATE:
                // do nothing
                break;
            case CREATED:
                // 直接destroy
                fragment.onDestroy();
                break;
            case FRONT:
                // 先pause 再destroy
                fragment.onPause();
                fragment.onDestroy();
                break;
            case BACKGROUND:
                // 直接destroy
                fragment.onDestroy();
                break;
            case DESTROYED:
                // do nothing
                break;
        }
    }

    /**
     * 销毁当前的fragment
     */
    private void destroyCurFragment() {
        if (mCurFragment == null) {
            return;
        }

        destroyFragment(mCurFragment);
        mCurFragment = null;
    }

    /**
     * Fragment是否都已经finish了
     * 
     * @return
     */
    public boolean isAllFinished() {
        return mCurFragment == null && mNavigation.isEmpty();
    }

    /**
     * finish当前的fragment
     * 
     * @param pluginFragmentActivity
     */
    public void finishPluginFragment(final PluginFragmentActivity pluginFragmentActivity,
            PluginFragment fragement, final int requestCode, final int resultCode,
            final Intent resultData) {
        BackStackEntry topEntry = mNavigation.getTopBackStackEntry();
        if (!topEntry.getBackPluginFragmentClass().equals(fragement.getClass())) {
            destroyCurFragment();
            mNavigation.removeEntryByClass(fragement.getClass());
            return;
        }

        mNavigation.popBackStackEntry();
        BackStackEntry backStackEntry = mNavigation.getTopBackStackEntry();

        // 如果回退栈为空 则销毁当前的fragment
        if (backStackEntry == null) {
            destroyCurFragment();
            return;
        }

        Class<? extends PluginFragment> classOfFragment = backStackEntry
                .getBackPluginFragmentClass();

        // 根据class获得待回退的fragment
        final PluginFragment fragment = getFragmentByClass(classOfFragment, pluginFragmentActivity);

        destroyFragment(mCurFragment);

        PluginFragment lastFragment = mCurFragment;
        // 设置最新的fragment
        mCurFragment = fragment;

        if (fragment != null) {
            transitFragment(pluginFragmentActivity, fragment, lastFragment,
                    new IPluginFragmentTransitionListener() {

                        @Override
                        public void onEnd(View contentView) {

                            if (requestCode != PluginFragment.DEFAULT_REQUEST_CODE
                                    && resultCode != PluginFragment.DEFAULT_RESULT_CODE) {
                                mCurFragment.onResult(requestCode, resultCode, resultData);
                            }

                            // 设置contentView
                            pluginFragmentActivity.setContentView(contentView);
                        }
                    });
        }
    }

    /**
     * 销毁所有fragment
     */
    public void finishAll() {
        // 销毁缓存中的所有fragment
        mFragmentCache.evictAll();
        // 清空导航栈
        mNavigation.clear();
        // 销毁当前的fragment
        destroyCurFragment();
    }

    /**
     * 得到当前fragment
     * 
     * @return
     */
    public PluginFragment getFragment() {
        return mCurFragment;
    }

}
