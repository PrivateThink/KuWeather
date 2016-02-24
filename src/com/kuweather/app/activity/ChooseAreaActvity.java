package com.kuweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import net.youmi.android.AdManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuweather.app.R;
import com.kuweather.app.db.CoolWeatherDB;
import com.kuweather.app.model.City;
import com.kuweather.app.model.County;
import com.kuweather.app.model.Province;
import com.kuweather.app.util.HttpCallbackListener;
import com.kuweather.app.util.HttpUtil;
import com.kuweather.app.util.Utility;

public class ChooseAreaActvity extends Activity{

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<>();
	
	//省列表
	private List<Province> provinceList;
	//市列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中的省份
	private Province selectedProvince;
	//选中的城市
	private City selectedCity;
	//当前选中的级别
	private int currentLevel;
	//是否从weatherActivity中跳转过来
	private boolean isFromWeatherActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//初始化有米广告
		AdManager.getInstance(this).init("bb2a6e61ec380c68", "00b6d9c7e56726de", false);
		
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
		
		
		//从sharedpreference文件读取city_selected标志位,如果为true，直接跳转到天气显示页面
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
		//已经选择了城市而且不是从weatherActivity跳转过来，才会直接跳转到weatheractivity
		if (prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示默认标题头
		setContentView(R.layout.choose_area);
		
		listView=(ListView) findViewById(R.id.list_view);
		titleView=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,dataList);
		
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince=provinceList.get(index);
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					selectedCity=cityList.get(index);
					queryCounties();
				}else if (currentLevel==LEVEL_COUNTY) {
					String countyCode=countyList.get(index).getCountyCode();
					Intent intent=new Intent(ChooseAreaActvity.this,WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
				
			}

			
		});
		
		queryProvinces();
	}


	/**
	 * 查询全国的所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryProvinces() {
		
		provinceList=coolWeatherDB.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else {
			queryFromServer(null,"province");
		}
	}
	
	/**
	 * 查询省内所有的城市，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */
	private void queryCities() {
		
		cityList=coolWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	
	/**
	 * 查询市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
	 */

	private void queryCounties() {
		
		countyList=coolWeatherDB.loadCounty(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county: countyList){
				dataList.add(county.getCountyName());
			}
			
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleView.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}

	
	/**
	 * 根据传入的代号和类型从服务器上查询省市县的数据
	 */
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result=false;
				if ("province".equals(type)) {
					result=Utility.handProvincesResponse(coolWeatherDB, response);
				}else if ("city".equals(type)) {
					result=Utility.handCitiesResponse(coolWeatherDB, 
											response, selectedProvince.getId());
				}else if ("county".equals(type)) {
					result=Utility.handCountiesResponse(coolWeatherDB, 
											response, selectedCity.getId());
				}
				
				if (result) {
					
					//通过runOnUiThread（）方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog();
							
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActvity.this, 
									"加载失败...", Toast.LENGTH_SHORT).show();
					}
				});
				
			}
		});
	}

	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		
		if (progressDialog==null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	
	/**
	 * 捕获Back键，根据当期的级别来判断，此时对应应该返回市列表，省列表，还是直接退出
	 */
	public void onBackPressed() {
		if (currentLevel==LEVEL_COUNTY) {
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else {
			if (isFromWeatherActivity) {
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
