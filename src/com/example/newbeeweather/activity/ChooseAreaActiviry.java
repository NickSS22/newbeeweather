package com.example.newbeeweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.newbeeweather.model.City;
import com.example.newbeeweather.model.Country;
import com.example.newbeeweather.model.NewbeeWeatherDB;
import com.example.newbeeweather.model.Province;
import com.example.newbeeweather.util.HttpCallbackListener;
import com.example.newbeeweather.util.HttpUtil;
import com.example.newbeeweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActiviry extends Activity {

	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private NewbeeWeatherDB newbeeWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	/*
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/*
	 * ���б�
	 */
	private List<City> cityList;
	/*
	 * ���б�
	 */
	private List<Country> countryList;

	/*
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/*
	 * ѡ�еĳ���
	 */
	private City selectedCity;

	/*
	 * ѡ�еļ���
	 */
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		newbeeWeatherDB = NewbeeWeatherDB.getInstance(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCountries();
				}
			}
		});
		// ����ʡ������
		queryProvinces();
	}

	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û����ȥ�������ϲ�ѯ
	 */
	private void queryProvinces() {
		provinceList = newbeeWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	/*
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities() {
		cityList = newbeeWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/*
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCountries() {
		countryList = newbeeWeatherDB.loadCountries(selectedCity.getId());
		if (countryList.size() > 0) {
			dataList.clear();
			for (Country country : countryList) {
				dataList.add(country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}
	}

	/*
	 * ���ߴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص�����
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		
		showProgressDilog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(newbeeWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(newbeeWeatherDB,
							response, selectedProvince.getId());
				} else if ("country".equals(type)) {
					result = Utility.handleCountriseResponse(newbeeWeatherDB,
							response, selectedCity.getId());
				}
				
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("response".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCountries();
							}
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActiviry.this, "����ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		
		});
	}

	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDilog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ��ء�����������");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б���ʡ�б�������ֱ���˳���
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}