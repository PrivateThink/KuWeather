package com.kuweather.app.activity;


import com.kuweather.app.R;
import com.kuweather.app.util.HttpCallbackListener;
import com.kuweather.app.util.HttpUtil;
import com.kuweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity{

	private LinearLayout weatherInfoLayout;
	//用于显示城市名
	private TextView cityNameText;
	//用于显示发布时间
	private TextView publishText;
	//用于显示天气描述信息
	private TextView weatherdespText;
	//用于显示气温1
	private TextView temp1Text;
	//用于显示气温2
	private TextView temp2Text;
	//用于显示当前的日期
	private TextView currentDateText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//初始化各控件
		
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherdespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		
		//获取Intent带过来的县级代号
		String countyCode=getIntent().getStringExtra("county_code");
		
		if (!TextUtils.isEmpty(countyCode)) {
			//有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			//根据县级代号查询所对应的天气代号
			queryWeatherCode(countyCode);
		}else{
			//没有县级代号就直接显示本地天气
			showWeather();
		}
		
	}


	/**
	 * 根据县级代号查询所对应的天气代号
	 * @param countyCode
	 */

	private void queryWeatherCode(String countyCode) {
		
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFormServer(address,"countyCode");
	}
	

	/**
	 * 查询天气代号所对应的天气
	 */
	
	private void queryWeantherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFormServer(address, "weatherCode");
	}
	
	/**
	 * 从sharaedPreferences文件中读取存储的天气信息，并显示在界面上
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherdespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}
	/**
	 * 根据传入的地址和类型去查询天气的代号或者天气信息
	 * @param address
	 * @param type
	 */
	private void queryFormServer(final String address, final String type) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//从服务器返回的数据解析出天气代号
						String[] array=response.split("\\|");
						if (array!=null && array.length==2) {
							String weatherCode=array[1];
							queryWeantherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败...");
					}
				});
			}
		});
		
	}

}
