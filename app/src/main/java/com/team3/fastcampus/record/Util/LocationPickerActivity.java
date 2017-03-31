package com.team3.fastcampus.record.Util;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.team3.fastcampus.record.R;
import com.team3.fastcampus.record.Util.Permission.PermissionController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 지도에서 선택한 위치 또는 검색한 장소의 위치를 선택하여 Return해준다.
 */
public class LocationPickerActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "LocationPickerActivity";

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;

    private PlaceAutoCompleteAdapter placeAutocompleteAdapter;

    private Marker pointMarker;

    private AutoCompleteTextView ed_search;
    private ImageView iv_search_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        initGoogle();

        initView();

        initListener();

        initAdapter();

        initGoogleMap();
    }

    private void initGoogle() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(googleConnectionCallbacks)
                .addOnConnectionFailedListener(googleOnConnectionFailedListener)
                .build();
    }

    private void initView() {
        ed_search = (AutoCompleteTextView) findViewById(R.id.ed_search);
        iv_search_cancel = (ImageView) findViewById(R.id.iv_search_cancel);
        iv_search_cancel.setVisibility(View.GONE);
    }

    private void initListener() {
        ed_search.addTextChangedListener(search_textWatcher);
        ed_search.setOnItemClickListener(this);
        iv_search_cancel.setOnClickListener(this);
    }

    private void initAdapter() {
        placeAutocompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,  new LatLngBounds(
                new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362)), null);
        ed_search.setAdapter(placeAutocompleteAdapter);
    }

    private void initGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Change view's location of My Location Button
        View mapView = mapFragment.getView();
        if (mapView != null && mapView.findViewById(1) != null) {
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_cancel:
                cancelSerching();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hideKeybord();

        final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
        final String placeId = item.getPlaceId();
        final CharSequence primaryText = item.getPrimaryText(null);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                Toast.LENGTH_SHORT).show();
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Logger.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            Logger.d(TAG, "place name : " + place.getName());
            Logger.d(TAG, "place id : " + place.getId());
            Logger.d(TAG, "place address : " + place.getAddress());

            LatLng position = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

            places.release();
        }
    };

    private GoogleMap.OnMapLongClickListener googleMapOnMapLongClickListener = (position) ->
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

    private GoogleMap.OnMyLocationButtonClickListener googleMapOnMyLocationButtonClickListener = () -> {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsSettingDialog();
            return true;
        }
        return false;
    };

    private GoogleMap.OnCameraIdleListener googleMapOnCameraIdleListener = () -> {
        LatLng position = googleMap.getCameraPosition().target;
        placeAutocompleteAdapter.setBounds(new LatLngBounds(new LatLng(position.latitude - 0.1, position.longitude - 0.1), new LatLng(position.latitude + 1, position.longitude + 1)));
        pointMarker.setPosition(position);
    };

    private GoogleApiClient.ConnectionCallbacks googleConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Logger.i(TAG, "GoogleApiClient.ConnectionCallbacks : onConnected");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Logger.i(TAG, "GoogleApiClient.ConnectionCallbacks : onConnectionSuspended");
        }
    };

    private GoogleApiClient.OnConnectionFailedListener googleOnConnectionFailedListener = connectionResult ->
            Logger.i(TAG, "GoogleApiClient.OnConnectionFailedListener");

    private TextWatcher search_textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 0) {
                iv_search_cancel.setVisibility(View.VISIBLE);
            } else {
                iv_search_cancel.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnCameraIdleListener(googleMapOnCameraIdleListener);
        this.googleMap.setOnMapLongClickListener(googleMapOnMapLongClickListener);
        this.googleMap.setOnMyLocationButtonClickListener(googleMapOnMyLocationButtonClickListener);

        LatLng seoul = new LatLng(37.566535, 126.97796);
        pointMarker = this.googleMap.addMarker(new MarkerOptions().position(seoul));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));

        new PermissionController(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}).check(new PermissionController.PermissionCallback() {
            @Override
            public void success() {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    String provider = locationManager.getBestProvider(new Criteria(), true);
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                } else {
                    showGpsSettingDialog();
                }
                googleMap.setMyLocationEnabled(true);
            }

            @Override
            public void error() {
                Toast.makeText(LocationPickerActivity.this, "권한을 허용 하지 않으면 현재 위치 정보를 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGpsSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("GPS 가 꺼져있습니다.")
                .setMessage("GPS 가 켜져있어야 위치 정보를 가져올 수 있습니다.\n설정창으로 가시겠습니까?")
                .setPositiveButton("확인", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.cancel())
                .show();
    }

    public Address getAddressByLatLng(double lat, double lng) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> list = geocoder.getFromLocation(lat, lng, 1);

        if (list == null) {
            Logger.e(TAG, "Error to get address by location");
            return null;
        }

        if (list.size() > 0) {
            Logger.d(TAG, "getAddressByLatLng : "
                    + list.get(0).getCountryName() + " "
                    + list.get(0).getPostalCode() + " "
                    + list.get(0).getLocality() + " "
                    + list.get(0).getThoroughfare() + " "
                    + list.get(0).getFeatureName());
            return list.get(0);
        }
        return null;
    }

    private void cancelSerching() {
        ed_search.setText("");
        hideKeybord();
    }

    private void hideKeybord() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_search.getWindowToken(), 0);
    }

    private class PlaceAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

        private final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

        private ArrayList<AutocompletePrediction> mResultList;

        private LatLngBounds mBounds;
        private AutocompleteFilter mPlaceFilter;

        public PlaceAutoCompleteAdapter(Context context, GoogleApiClient googleApiClient, LatLngBounds bounds, AutocompleteFilter filter) {
            super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
            mGoogleApiClient = googleApiClient;
            mBounds = bounds;
            mPlaceFilter = filter;
        }

        public void setBounds(LatLngBounds bounds) {
            mBounds = bounds;
        }

        @Override
        public int getCount() {
            return mResultList.size();
        }

        @Nullable
        @Override
        public AutocompletePrediction getItem(int position) {
            return mResultList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = super.getView(position, convertView, parent);

            AutocompletePrediction item = getItem(position);

            TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
            TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
            textView1.setText(item.getPrimaryText(STYLE_BOLD));
            textView2.setText(item.getSecondaryText(STYLE_BOLD));

            return row;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();

                    ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                    if (constraint != null) {
                        filterData = getAutocomplete(constraint);
                    }

                    results.values = filterData;
                    if (filterData != null) {
                        results.count = filterData.size();
                    } else {
                        results.count = 0;
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    if (results != null && results.count > 0) {
                        mResultList = (ArrayList<AutocompletePrediction>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {
                    if (resultValue instanceof AutocompletePrediction) {
                        return ((AutocompletePrediction) resultValue).getFullText(null);
                    } else {
                        return super.convertResultToString(resultValue);
                    }
                }
            };
        }

        private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
            if (mGoogleApiClient.isConnected()) {

                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi
                                .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                        mBounds, mPlaceFilter);

                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    autocompletePredictions.release();
                    return null;
                }

                return DataBufferUtils.freezeAndClose(autocompletePredictions);
            }
            return null;
        }
    }

}
