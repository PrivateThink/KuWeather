package com.kuweather.app.util;

import android.text.TextUtils;

import com.kuweather.app.db.CoolWeatherDB;
import com.kuweather.app.model.City;
import com.kuweather.app.model.County;
import com.kuweather.app.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 */
	
	public synchronized static boolean handProvincesResponse(CoolWeatherDB
			coolWeatherDB,String response){
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces=response.split(",");
			if (allProvinces!=null && allProvinces.length>0) {
				for(String p:allProvinces){
					String [] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据保存到Province表
					coolWeatherDB.saveProvince(province);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 解析和处理服务器返回的市级数据
	 */
	
	public synchronized static boolean handCitiesResponse(CoolWeatherDB
			coolWeatherDB,String response,int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCities=response.split(",");
			if (allCities!=null && allCities.length>0) {
				for(String c:allCities){
					String [] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据保存到city表
					coolWeatherDB.saveCity(city);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 解析和处理服务器返回的县级数据
	 */
	
	public synchronized static boolean handCountiesResponse(CoolWeatherDB
			coolWeatherDB,String response,int cityId){
		if (!TextUtils.isEmpty(response)) {
			String[] allCountiess=response.split(",");
			if (allCountiess!=null && allCountiess.length>0) {
				for(String c:allCountiess){
					String [] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据保存到county表
					coolWeatherDB.saveCounty(county);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
}
