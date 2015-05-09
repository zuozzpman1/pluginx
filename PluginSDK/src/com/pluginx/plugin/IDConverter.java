
package com.pluginx.plugin;

/**
 * 资源id转换器 ，用于宿主与插件间的转换
 * 
 * @author dazhengyang
 */
public class IDConverter {

    /**
     * plugin resource id -> host resource id
     * 
     * @param context
     * @param pluginResId
     * @return
     */
    public static int toHostId(PluginContext context, String defType, int pluginResId) {

        String name = context.getResources().getResourceEntryName(pluginResId);

        IPluginContextDelegate del = context.clearDelegate();

        pluginResId = context.getResources().getIdentifier(name, defType, context.getPackageName());

        context.setDelegate(del);

        return pluginResId;
    }
}
