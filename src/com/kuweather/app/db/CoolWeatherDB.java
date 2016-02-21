package com.kuweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.kuweather.app.model.City;
import com.kuweather.app.model.County;
import com.kuweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	//���ݿ���
	private static final String DB_NAME="cool_weather";
	//���ݿ�汾
	private static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	//˽�л����췽��
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbhelper=new CoolWeatherOpenHelper(context, 
						DB_NAME, null, VERSION);
		db=dbhelper.getWritableDatabase();
	}
	
	//��ȡCoolWeatherDBʵ��
	
	public synchronized static CoolWeatherDB getInstance(Context context){
		if (coolWeatherDB==null) {
			coolWeatherDB=new CoolWeatherDB(context);
		}
		
		return coolWeatherDB;
	}
	
	//����ʡ�ݵ�����
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//�����ݿ��ж�ȡʡ������
	
	public List<Province> loadProvinces(){
		List<Province> list=new ArrayList<>();
		
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			
			do {
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
								.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
							    .getColumnIndex("province_code")));
				list.add(province);
				
			} while (cursor.moveToNext());
			
		}
	
		if (cursor!=null) {
			cursor.close();
		}
		
		return list;
	}
	
	
	//������е�����
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values=new ContentValues();
			values.put("City_name",city.getCityName());
			values.put("City_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	//�����ݿ��ж�ȡ��������
	
	public List<City> loadCity(int provinceID){
		List<City> list=new ArrayList<>();
		
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceID)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
								.getColumnIndex("City_name")));
				city.setCityCode(cursor.getString(cursor
							    .getColumnIndex("City_code")));
				city.setProvinceId(provinceID);
				list.add(city);
				
			} while (cursor.moveToNext());
			
		}
	
		if (cursor!=null) {
			cursor.close();
		}
		
		return list;
	}
	
	
	//����County������
		public void saveCounty(County county){
			if (county!=null) {
				ContentValues values=new ContentValues();
				values.put("County_name",county.getCountyName());
				values.put("County_code", county.getCountyCode());
				values.put("city_id",county.getCityId());
				db.insert("County", null, values);
			}
		}
		
		//�����ݿ��ж�ȡCounty����
		
		public List<County> loadCounty(int cityID){
			List<County> list=new ArrayList<>();
			
			Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cityID)}, null, null, null);
			
			if (cursor.moveToFirst()) {
				
				do {
					County county=new County();
					county.setId(cursor.getInt(cursor.getColumnIndex("id")));
					county.setCountyName(cursor.getString(cursor
									.getColumnIndex("County_name")));
					county.setCountyCode(cursor.getString(cursor
								    .getColumnIndex("County_code")));
					county.setCityId(cityID);
					list.add(county);
					
				} while (cursor.moveToNext());
				
			}
		
			if (cursor!=null) {
				cursor.close();
			}
			
			return list;
		}
}
