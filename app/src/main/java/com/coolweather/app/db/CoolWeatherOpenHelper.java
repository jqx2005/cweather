package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MyPC on 2016/7/11 0011.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper{
    /**
     * Province 表 建表语句
     */
    public static  final String CREATE_PROVINCE ="create table Province("
            +"id integer primary key autoincrement,"
            +"province_name text,"
            +"province_code text)";

    /**
     * City 表 建表语句
     */
    public static final String CREATE_CITY ="create table City("
            +"id integer primary key autoincrement,"
            +"city_name text,"
            +"city_code text)";
    /**
     * County表 建表语句
     */
    public static final String CREATE_COUNTY="create table County("
            +"id integer primary key autoincrement,"
            +"county_name text,"
            +"county_code text,"
            +"county_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion,int newveriosn){
        ;
    }
}