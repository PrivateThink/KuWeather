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
	//������ʾ������
	private TextView cityNameText;
	//������ʾ����ʱ��
	private TextView publishText;
	//������ʾ����������Ϣ
	private TextView weatherdespText;
	//������ʾ����1
	private TextView temp1Text;
	//������ʾ����2
	private TextView temp2Text;
	//������ʾ��ǰ������
	private TextView currentDateText;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		
		//��ʼ�����ؼ�
		
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherdespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		
		//��ȡIntent���������ؼ�����
		String countyCode=getIntent().getStringExtra("county_code");
		
		if (!TextUtils.isEmpty(countyCode)) {
			//���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			//�����ؼ����Ų�ѯ����Ӧ����������
			queryWeatherCode(countyCode);
		}else{
			//û���ؼ����ž�ֱ����ʾ��������
			showWeather();
		}
		
	}


	/**
	 * �����ؼ����Ų�ѯ����Ӧ����������
	 * @param countyCode
	 */

	private void queryWeatherCode(String countyCode) {
		
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFormServer(address,"countyCode");
	}
	

	/**
	 * ��ѯ������������Ӧ������
	 */
	
	private void queryWeantherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFormServer(address, "weatherCode");
	}
	
	/**
	 * ��sharaedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�ڽ�����
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherdespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDateText.setText(prefs.getString("current_date", ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}
	/**
	 * ���ݴ���ĵ�ַ������ȥ��ѯ�����Ĵ��Ż���������Ϣ
	 * @param address
	 * @param type
	 */
	private void queryFormServer(final String address, final String type) {
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//�ӷ��������ص����ݽ�������������
						String[] array=response.split("\\|");
						if (array!=null && array.length==2) {
							String weatherCode=array[1];
							queryWeantherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					//������������ص�������Ϣ
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
						publishText.setText("ͬ��ʧ��...");
					}
				});
			}
		});
		
	}

}
