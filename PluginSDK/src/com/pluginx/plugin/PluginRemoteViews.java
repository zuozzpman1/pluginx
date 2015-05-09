package com.pluginx.plugin;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

/**
 * 对RemoteViews的封装
 * 
 * @author dazhengyang
 */
public class PluginRemoteViews extends RemoteViews {

    private PluginContext context;

    public static PluginRemoteViews buildInstance(PluginContext context, String packageName,
            int layoutId) {
        layoutId = IDConverter.toHostId(context, "layout", layoutId);
        PluginRemoteViews view = new PluginRemoteViews(context, packageName, layoutId);
        return view;
    }

    private PluginRemoteViews(PluginContext context, String packageName, int layoutId) {
        super(packageName, layoutId);
        this.context = context;
    }

    @Override
    public void setOnClickFillInIntent(int viewId, Intent fillInIntent) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        super.setOnClickFillInIntent(viewId, fillInIntent);
    }

    @Override
    public void setOnClickPendingIntent(int viewId, PendingIntent pendingIntent) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        super.setOnClickPendingIntent(viewId, pendingIntent);
    }

    @Override
    public void setImageViewBitmap(int viewId, Bitmap bitmap) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        super.setImageViewBitmap(viewId, bitmap);
    }
    
    @Override
    public void setImageViewResource(int viewId, int srcId) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        srcId = IDConverter.toHostId(context, "drawable", srcId);
        super.setImageViewResource(viewId, srcId);
    }
    
    @Override
    public void setTextViewText(int viewId, CharSequence text) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        super.setTextViewText(viewId, text);
    }
    
    @Override
    public void setProgressBar(int viewId, int max, int progress, boolean indeterminate) {
        viewId = IDConverter.toHostId(context, "id", viewId);
        super.setProgressBar(viewId, max, progress, indeterminate);
    }
}
