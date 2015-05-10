
package com.pluginx.plugin.business;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqliteDemo {

    private CustomerDBHelper dbHelper;

    private int index;

    public SqliteDemo(Context context) {
        dbHelper = new CustomerDBHelper(context);
    }

    public void addItem() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String user = "user" + index;
        String pass = "pass" + index;
        String sql = "insert into user(username,password) values ('" + user + "','" + pass + "')";
        db.execSQL(sql);// 执行SQL语句

        index++;
    }

    public String query() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("select * from user", null);

        String toast = "";
        while (c.moveToNext()) {
            String user = c.getString(c.getColumnIndex("username"));
            String password = c.getString(c.getColumnIndex("password"));

            toast += user + " " + password + "\n";
        }

        return toast;
    }
}
