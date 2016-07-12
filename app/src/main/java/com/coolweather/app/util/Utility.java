package com.coolweather.app.util;

import android.content.Context;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

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
}
