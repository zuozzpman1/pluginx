
package com.pluginx.plugin.business;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.pluginx.plugin.PluginContentProvider;
import com.pluginx.plugin.PluginContext;


public class CustomerProvider extends PluginContentProvider {
    private CustomerDBHelper dbHelper;

    public CustomerProvider(PluginContext context) {
        super(context);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new CustomerDBHelper(this.getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("select * from user", null);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
