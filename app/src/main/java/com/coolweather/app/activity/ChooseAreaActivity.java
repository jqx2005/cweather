package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 2016/7/13 0013.
 */
public class ChooseAreaActivity extends Activity {
    public  static  final int LEVEL_PROVINCE = 0;
    public  static final int LEVEL_CITY = 1;
    public  static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private CoolWeatherDB coolWeatherDB;
    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> provincesList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的市级
     */
    private City selectedCity;

    /**
    *当前选中的级别
    */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView =(ListView)findViewById(R.id.list_view);
        textView = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provincesList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvince();    //加载省级数据
    }
    /**
     * 查询全国所有的省份，优先从数据库，如果为空则从服务器提取
     */
    private void queryProvince(){
        provincesList = coolWeatherDB.loadProvinces();
        if(provincesList.size() > 0 ){
            datalist.clear();
            for(Province p : provincesList){
                datalist.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null , "province");
        }
    }
    /**
     * 查询选中的省份的所有城市，优先从数据库中读取，没有则从服务中读取
     */
    private void queryCities(){
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size() > 0){
            datalist.clear();
            for(City c :  cityList){
                datalist.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    /**
     * 查询选中的城市的所有县市，优先从数据库中读取，没有则从服务中读取
     */
    private void queryCounties(){
        countyList = coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size() > 0){
            datalist.clear();
            for(County c :  countyList){
                datalist.add(c.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    /**
     * 根据传入的代号和类型从服务器读取省市区的数据
     */
    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }
              //todo   closeProgressDialog();
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("County".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     *显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出
     */
    @Override
    public void onBackPressed(){
        if(currentLevel == LEVEL_CITY){
            queryProvince();
        }else if(currentLevel == LEVEL_COUNTY){
            queryCities();
        }else
            finish();
    }

}
