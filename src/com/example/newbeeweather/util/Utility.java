package com.example.newbeeweather.util;

import android.R.integer;
import android.text.TextUtils;

import com.example.newbeeweather.model.City;
import com.example.newbeeweather.model.Country;
import com.example.newbeeweather.model.NewbeeWeatherDB;
import com.example.newbeeweather.model.Province;

public class Utility {

	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(NewbeeWeatherDB newbeeWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//简介吸出来的数据存储到Province表
					newbeeWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(NewbeeWeatherDB newbeeWeatherDB,
			String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allcitise = response.split(",");
			if (allcitise != null && allcitise.length > 0) {
				for (String c : allcitise ) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存在City表
					newbeeWeatherDB.saveCity(city);
				}
				return true;
			}
		}
				return false;
		
	}
	
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountriseResponse(NewbeeWeatherDB newbeeWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountrise = response.split(",");
			if( allCountrise != null && allCountrise.length > 0){
				for(String c : allCountrise){
					String[] array = c.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					//将解析出来的数据存储到Country表
					newbeeWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
		
	}
}
