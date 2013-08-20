package com.ihave.ilocala;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {
	private BMapManager iBMapManager = null;
	private MapView iMapView = null;
	private TextView lTextView = null;
	private String mData;
//	private TextView latlonTextView = null;
	private LocationClient mLocationClient = null;
	private MapController iMapController = null;
//	private BDLocation mLocation ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		iBMapManager = new BMapManager(getApplicationContext());
		iBMapManager.init("47429da4b6e37d0f6ddd11b8c80b7f20", null);
		setContentView(R.layout.activity_main);
		lTextView = (TextView)findViewById(R.id.locTextView);
		//latlonTextView = (TextView)findViewById(R.id.latlonTextView);
		mLocationClient = new LocationClient(getApplicationContext());
		iMapView = (MapView)findViewById(R.id.baiduMapView);
		iMapView.setBuiltInZoomControls(true);
		iMapController = iMapView.getController();
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");//返回的定位结果包含地址信息
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);//禁止启用缓存定位
		option.setPoiNumber(5);	//最多返回POI-Point of Interest个数	
		option.setPoiDistance(1000); //poi查询距离		
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	
	    mLocationClient.registerLocationListener(new BDLocationListener() {	
		    	@Override
				public void onReceivePoi(BDLocation poiLocation) {
					if (poiLocation == null){
						return ; 
					}
					StringBuffer sb = new StringBuffer(256);
					sb.append("Poi time : ");
					sb.append(poiLocation.getTime());
					sb.append("\nerror code : "); 
					sb.append(poiLocation.getLocType());
					sb.append("\nlatitude : ");
					sb.append(poiLocation.getLatitude());
					sb.append("\nlontitude : ");
					sb.append(poiLocation.getLongitude());
					sb.append("\nradius : ");
					sb.append(poiLocation.getRadius());
					if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){
						sb.append("\naddr : ");
						sb.append(poiLocation.getAddrStr());
					} 
					if(poiLocation.hasPoi()){
						sb.append("\nPoi:");
						sb.append(poiLocation.getPoi());
					}else{				
						sb.append("noPoi information");
					}
					logMsg(sb.toString());
					
				}
				
				@Override
				public void onReceiveLocation(BDLocation location) {
					if (location == null)
						return ;
					StringBuffer sb = new StringBuffer(256);
					sb.append("time : ");
					sb.append(location.getTime());
					sb.append("\nerror code : ");
					sb.append(location.getLocType());
					sb.append("\nlatitude : ");
					sb.append(location.getLatitude());
					sb.append("\nlontitude : ");
					sb.append(location.getLongitude());
					sb.append("\nradius : ");
					sb.append(location.getRadius());
					if (location.getLocType() == BDLocation.TypeGpsLocation){
						sb.append("\nspeed : ");
						sb.append(location.getSpeed());
						sb.append("\nsatellite : ");
						sb.append(location.getSatelliteNumber());
					} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//						sb.append("\n省：");
//						sb.append(location.getProvince());
//						sb.append("\n市：");
//						sb.append(location.getCity());
//						sb.append("\n区/县：");
//						sb.append(location.getDistrict());
						sb.append("\naddr : ");
						sb.append(location.getAddrStr());
					}
					sb.append("\nsdk version : ");
					sb.append(mLocationClient.getVersion());
					sb.append("\nisCellChangeFlag : ");
					sb.append(location.isCellChangeFlag());
					//新建方法来展现此位置的地图
					showMapView(location.getLatitude(),location.getLongitude());
					logMsg(sb.toString());
				}
		     });
	    
	
		if(mLocationClient != null && mLocationClient.isStarted()){
	    	mLocationClient.requestLocation();
	    	// myLocationClient.requestPoi();
	    }else{
	    	Log.d("LocSDK3", "locClient is null or not started");
	    }
	}
	
	public void showMapView(double latitude,double longitude){
		MyLocationOverlay mLocationOverlay = new MyLocationOverlay(iMapView);
		LocationData mLocationData = new LocationData();
		mLocationData.latitude = latitude;
		mLocationData.longitude = longitude;
		mLocationData.direction = 2.0f;
		mLocationOverlay.setData(mLocationData);
		iMapView.getOverlays().add(mLocationOverlay);
		iMapView.refresh();
		iMapController.animateTo(new GeoPoint((int)(mLocationData.latitude * 1E6), (int)(mLocationData.longitude * 1E6)));
		iMapController.setZoom(18);
	}
	
	
	
	
	@Override
	protected void onDestroy(){
		iMapView.destroy();
		if(iBMapManager != null){
			iBMapManager.destroy();
			iBMapManager = null;
		}
	   if(mLocationClient != null && mLocationClient.isStarted()){ 
	            mLocationClient.stop(); 
	            mLocationClient = null; 
	   } 
	   super.onDestroy();  
	}
	
	@Override
	protected void onPause(){
		iMapView.onPause();
		if(iBMapManager != null){
			iBMapManager.stop();
		}
		super.onPause();
	}
	
	@Override
	protected void onResume(){
		iMapView.onResume();
		if(iBMapManager != null){
			iBMapManager.start();
		}
		super.onResume();
	}
	

	private void logMsg(String string) {
		try { mData = string;
			if ( lTextView != null )
				lTextView.setText(mData);
		} catch (Exception e) {
			e.printStackTrace();
		}
   }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
