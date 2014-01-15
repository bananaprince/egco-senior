package com.egco.storefinderproject.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class GoogleMapUtils {
	public static final String MODE_DRIVING = "driving";
	public static final String MODE_WALKING = "walking";

	public static final float ZOOM_VERY_HIGH = 16.0f;
	public static final float ZOOM_HIGH = 14.0f;
	public static final float ZOOM_MEDIUM = 12.0f;
	public static final float ZOOM_LOW = 10.0f;
	
	private static final String GMAP_API_SERVICE_URL = "http://maps.googleapis.com/maps/api/directions/json?";
	private static final String GMAP_API_SERVICE_PARAM_SOURCE = "origin=";
	private static final String GMAP_API_SERVICE_PARAM_DESTINATION = "destination=";
	private static final String GMAP_API_SERVICE_PARAM_SENSOR = "sensor=";
	private static final String GMAP_API_SERVICE_PARAM_UNITS = "units=";
	private static final String GMAP_API_SERVICE_PARAM_MODE = "mode=";

	private static final String GMAP_FIELD_ROUTES = "routes";
	private static final String GMAP_FIELD_LEGS = "legs";
	private static final String GMAP_FIELD_DISTANCE = "distance";
	private static final String GMAP_FIELD_DURATION = "duration";
	private static final String GMAP_FIELD_TEXT = "text";
	private static final String GMAP_FIELD_VALUE = "value";
	private static final String GMAP_FIELD_START_ADDR = "start_address";
	private static final String GMAP_FIELD_END_ADDR = "end_address";
	private static final String GMAP_FIELD_START_LOCATION = "start_location";
	private static final String GMAP_FIELD_END_LOCATION = "end_location";
	private static final String GMAP_FIELD_STEP = "steps";
	private static final String GMAP_FIELD_POLYLINE = "polyline";
	private static final String GMAP_FIELD_POINTS = "points";
	private static final String GMAP_FIELD_LAT = "lat";
	private static final String GMAP_FIELD_LNG = "lng";

	private GoogleMap gMap;
	private Location currentLocation;
	private Context mContext;

	private JSONObject serviceResult;

	public GoogleMapUtils(MapFragment gMapFragment, Context context) {
		gMap = gMapFragment.getMap();
		mContext = context;
		gMap.setMyLocationEnabled(true);
		MapFragment.newInstance(setMapManipulation());

		currentLocation = getCurrentLocation();

		moveCamera(getLatLngFromLocation(currentLocation), ZOOM_VERY_HIGH);

	}

	private void moveCamera(LatLng position, float zoom) {
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, zoom));
	}

	public LatLng getLatLngFromLocation(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	private GoogleMapOptions setMapManipulation() {
		GoogleMapOptions gMapOptions = new GoogleMapOptions();
		gMapOptions.tiltGesturesEnabled(true);
		gMapOptions.rotateGesturesEnabled(true);
		gMapOptions.zoomGesturesEnabled(true);
		gMapOptions.zoomControlsEnabled(true);
		gMapOptions.scrollGesturesEnabled(true);

		return gMapOptions;
	}

	public void callGoogleMapAPIService(String transportationMode,
			LatLng source, LatLng destination) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(GMAP_API_SERVICE_URL);
		sBuilder.append(GMAP_API_SERVICE_PARAM_SOURCE);
		sBuilder.append(source.latitude + "," + source.longitude);
		sBuilder.append("&");
		sBuilder.append(GMAP_API_SERVICE_PARAM_DESTINATION);
		sBuilder.append(destination.latitude + "," + destination.longitude);
		sBuilder.append("&");
		sBuilder.append(GMAP_API_SERVICE_PARAM_SENSOR);
		sBuilder.append("false");
		sBuilder.append("&");
		sBuilder.append(GMAP_API_SERVICE_PARAM_UNITS);
		sBuilder.append("metric");
		sBuilder.append("&");
		sBuilder.append(GMAP_API_SERVICE_PARAM_MODE);
		sBuilder.append(transportationMode);
		new callGoogleMapAPIServiceAsyncTask().execute(sBuilder.toString());
	}

	private class callGoogleMapAPIServiceAsyncTask extends
			AsyncTask<String, String, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject result = JSONFromURLUtils.getJSONFromURL(params[0]);
			serviceResult = result;
			Log.v("CAT", serviceResult.toString());
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			drawDirection();
		}

	}

	public Location getCurrentLocation() {
		Location curLocation;
		LocationManager locationManager = (LocationManager) mContext
				.getSystemService(mContext.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		curLocation = locationManager.getLastKnownLocation(provider);
		return curLocation;
	}

	public MarkerOptions getMarker(LatLng target) {
		MarkerOptions mapMarker = new MarkerOptions();
		mapMarker.position(target);
		mapMarker.icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
		return mapMarker;
	}

	public void drawMarker(MarkerOptions marker) {
		gMap.addMarker(marker);
	}

	private ArrayList<LatLng> decodePolyline(String encodedString) {
		ArrayList<LatLng> decodedPoly = new ArrayList<LatLng>();
		int index = 0;
		int len = encodedString.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b;
			int shift = 0;
			int result = 0;

			do {
				b = encodedString.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;

			do {
				b = encodedString.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			decodedPoly.add(position);
		}
		return decodedPoly;
	}

	public String getLegDistanceText() {
		String result = null;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			JSONObject distance = legs.getJSONObject(GMAP_FIELD_DISTANCE);
			result = distance.getString(GMAP_FIELD_TEXT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public long getLegDistanceValue() {
		long result = -1;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			JSONObject distance = legs.getJSONObject(GMAP_FIELD_DISTANCE);
			result = distance.getLong(GMAP_FIELD_VALUE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getLegDurationText() {
		String result = null;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			JSONObject duration = legs.getJSONObject(GMAP_FIELD_DURATION);
			result = duration.getString(GMAP_FIELD_TEXT);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public long getLegDurationValue() {
		long result = -1;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			JSONObject duration = legs.getJSONObject(GMAP_FIELD_DURATION);
			result = duration.getLong(GMAP_FIELD_VALUE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getSourceAddress() {
		String result = null;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			result = legs.getString(GMAP_FIELD_START_ADDR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getDestinationAddress() {
		String result = null;
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			result = legs.getString(GMAP_FIELD_END_ADDR);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<LatLng> getDirectionPoint() {
		ArrayList<LatLng> pointList = new ArrayList<LatLng>();
		try {
			JSONObject routes = serviceResult.getJSONArray(GMAP_FIELD_ROUTES)
					.getJSONObject(0);
			JSONObject legs = routes.getJSONArray(GMAP_FIELD_LEGS)
					.getJSONObject(0);
			JSONArray step = legs.getJSONArray(GMAP_FIELD_STEP);
			for (int i = 0; i < step.length(); i++) {
				JSONObject start = step.getJSONObject(i).getJSONObject(
						GMAP_FIELD_START_LOCATION);
				pointList.add(new LatLng(start.getDouble(GMAP_FIELD_LAT), start
						.getDouble(GMAP_FIELD_LNG)));

				JSONObject polyLine = step.getJSONObject(i).getJSONObject(
						GMAP_FIELD_POLYLINE);
				String pointString = polyLine.getString(GMAP_FIELD_POINTS);
				ArrayList<LatLng> polyLinePointList = decodePolyline(pointString);
				pointList.addAll(polyLinePointList);

				JSONObject end = step.getJSONObject(i).getJSONObject(
						GMAP_FIELD_END_LOCATION);
				pointList.add(new LatLng(end.getDouble(GMAP_FIELD_LAT), end
						.getDouble(GMAP_FIELD_LNG)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pointList;
	}

	public void drawDirection() {
		ArrayList<LatLng> pointList = getDirectionPoint();
		PolylineOptions directionLine = new PolylineOptions().width(3f).color(
				Color.MAGENTA);
		for (int i = 0; i < pointList.size(); i++) {
			directionLine.add(pointList.get(i));
		}
		gMap.addPolyline(directionLine);

	}

}
