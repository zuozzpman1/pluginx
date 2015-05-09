package com.pluginx.plugin.fragment;


/**
 * 创建{@link PluginFragment}的工厂
 * @author michaelzuo
 *
 */
public class PluginFragmentFactory {
    
    /**
     * 根据class创建{@link PluginFragment}
     * 
     * @param classOfFragment 类型
     * @param {@link PluginFragment}附着的{@link PluginFragmentActivity}
     * @return
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static PluginFragment createPluginFragment(
            Class<? extends PluginFragment> classOfFragment, PluginFragmentActivity pluginFragmentActivity) throws InstantiationException, IllegalAccessException {
        PluginFragment fragment = (PluginFragment)classOfFragment.newInstance();
        fragment.onCreate(pluginFragmentActivity);
        return fragment;
    }
}
