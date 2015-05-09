
package com.pluginx.plugin;

import java.util.ArrayList;

import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;

/**
 * ContentProvider
 * 
 * @author dazhengyang
 */
public abstract class PluginContentProvider {
    private PluginContext context;

    public PluginContentProvider(PluginContext context) {
        this.context = context;
    }

    public PluginContext getContext() {
        return context;
    }

    public abstract boolean onCreate();

    public abstract int delete(Uri uri, String selection, String[] selectionArgs);

    public abstract String getType(Uri uri);

    public abstract Uri insert(Uri uri, ContentValues values);

    public abstract Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder);

    public abstract int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs);
    
    public ContentProviderResult[] applyBatch(ArrayList<PluginContentProviderOperation> operations)
            throws OperationApplicationException {
        final int numOperations = operations.size();
        final ContentProviderResult[] results = new ContentProviderResult[numOperations];
        for (int i = 0; i < numOperations; i++) {
            results[i] = operations.get(i).apply(this, results, i);
        }
        return results;
    }

}
