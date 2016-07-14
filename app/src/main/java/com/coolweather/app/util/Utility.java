package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MyPC on 2016/7/12 0012.
 */
public class Utility {
    /**
     * 解析省数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if((allProvinces != null ) &&(allProvinces.length > 0)){
                for(String p : allProvinces){
                    String[] array = p.split("\\|");    //// TODO: 2016/7/12 0012  do
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析市级信息
     */
    public synchronized  static boolean handleCitiesResponse(CoolWeatherDB coolWeaherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if((allCities != null) &&(allCities.length > 0)){
                for(String c : allCities) {
                    String[] array = c.split("\\|");    //// TODO: 2016/7/12 0012  do
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeaherDB.saveCity(city);
                }
                return true;

            }
        }
        return false;
    }


    /**
     * 解析县级信息
     */
    public synchronized  static boolean handleCountiesResponse(CoolWeatherDB coolWeaherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if((allCounties != null) &&(allCounties.length > 0)){
                for(String c : allCounties) {
                    String[] array = c.split("\\|");    //// TODO: 2016/7/12 0012  do
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeaherDB.saveCounty(county);
                }
                return true;

            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出来的数据保存到本地
     */
    public static  void handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject =new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String weatherCode = weatherinfo.getString("cityid");
            String temp1 = weatherinfo.getString("temp1");
            String temp2 = weatherinfo.getString("temp2");
            String weatherDesp = weatherinfo.getString("weather");
            String publishTime = weatherinfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的天气信息SharePreferences文件中
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weahter,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CANADA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
