package com.pluginx.plugin.fragment;

import android.graphics.Bitmap;

import java.util.Stack;

/**
 * 管理一系列的PluginFragment间的回退逻辑
 * @author michaelzuo
 *
 */
public class PluginFragmentNavigation {
    
    /**
     * 回退栈
     */
    private Stack<BackStackEntry> mBackStacks;
    
    PluginFragmentNavigation() {
        mBackStacks = new Stack<BackStackEntry>();
    }
    
    /**
     * 压栈
     * @param backStackEntry
     */
    public void pushBackStackEntry(BackStackEntry backStackEntry) {
        mBackStacks.push(backStackEntry);
    }
    
    /**
     * 得到栈顶的{@link BackStackEntry} 但是不出栈
     * @return
     */
    public BackStackEntry getTopBackStackEntry() {
        int location = mBackStacks.size() - 1;
        
        if (location >= 0) {
            return mBackStacks.get(location);
        }
        
        return null;
    }
    
    /**
     * 出栈
     * @return 如果栈已经空了 则返回空
     */
    public BackStackEntry popBackStackEntry() {
        if (mBackStacks.isEmpty()) {
            return null;
        }
        
        return mBackStacks.pop();
    }

    /**
     * 根据class删除entry 
     * @param clz
     */
    public void removeEntryByClass(Class<? extends PluginFragment> clz) {
        for (int i = 0; i < mBackStacks.size(); i++) {
            BackStackEntry entry = mBackStacks.get(i);
            if (entry.getBackPluginFragmentClass().equals(clz)) {
                mBackStacks.remove(i);
                return;
            }
        }
    }
    
    /**
     * 栈中是否已经空了
     * @return
     */
    public boolean isEmpty() {
        return mBackStacks.isEmpty();
    }
    
    /**
     * 清空栈
     */
    public void clear() {
        mBackStacks.clear();
    }
    
    /**
     * build BackStackEntry with out icon
     * @param title
     * @param backFragment
     * @return
     */
    public static BackStackEntry buildBackStackEntry(final String title,
            final Class<? extends PluginFragment> backFragment) {
        return buildBackStackEntry(title, null, backFragment);
    }
    
    /**
     * build BackStackEntry
     * @param title
     * @param icon
     * @param backFragment
     * @param bundle
     * @return
     */
    public static BackStackEntry buildBackStackEntry(final String title, final Bitmap icon,
            final Class<? extends PluginFragment> backFragment) {
        return new BackStackEntry() {

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public Bitmap getIcon() {
                return icon;
            }

            @Override
            public Class<? extends PluginFragment> getBackPluginFragmentClass() {
                return backFragment;
            }

        };
    }
    
    /**
     * Representation of an entry on the {@link PluginFragment} back stack
     * @author michaelzuo
     *
     */
    public static interface BackStackEntry {
        
        /**
         * 得到待回退的{@link PluginFragment} 的类型
         * @return
         */
        public Class<? extends PluginFragment> getBackPluginFragmentClass();
        
        /**
         * 待回退的{@link PluginFragment}的title
         * @return
         */
        public String getTitle();
        
        /**
         * 待回退的{@link PluginFragment}的icon
         * @return
         */
        public Bitmap getIcon();
    }

}
