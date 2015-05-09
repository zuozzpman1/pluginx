
package com.pluginx.plugin;

import java.util.ArrayList;

import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

/**
 * 插件版ContentResolver，不支持跨进程查数据
 * 
 * @author dazhengyang
 */
public class PluginContentResolver {

    private PluginContext mContext;

    public PluginContentResolver(PluginContext context) {
        this.mContext = context;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        PluginContentProvider provider = mContext.findProvider(uri);
        if (provider == null)
            return null;
        return provider.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public String getType(Uri uri) {
        PluginContentProvider provider = mContext.findProvider(uri);
        if (provider == null)
            return null;
        return provider.getType(uri);
    }

    public Uri insert(Uri uri, ContentValues values) {
        PluginContentProvider provider = mContext.findProvider(uri);
        if (provider == null)
            return null;
        return provider.insert(uri, values);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        PluginContentProvider provider = mContext.findProvider(uri);
        if (provider == null)
            return 0;
        return provider.delete(uri, selection, selectionArgs);
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        PluginContentProvider provider = mContext.findProvider(uri);
        if (provider == null)
            return 0;
        return provider.update(uri, values, selection, selectionArgs);
    }

    public ContentProviderResult[] applyBatch(String authority,
            ArrayList<PluginContentProviderOperation> operations) throws RemoteException,
            OperationApplicationException {
        PluginContentProvider provider = mContext.findProvider(authority);
        if (provider == null)
            return null;
        return provider.applyBatch(operations);
    }

}
