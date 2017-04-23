package com.team3.fastcampus.record.InDiary;

/**
 * Created by yoonjoonghyun on 2017. 3. 25..
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.team3.fastcampus.record.R;

import java.util.ArrayList;
import java.util.List;

/**
 * InDiary의 리스트를 Map으로 보여주기 위한 Fragment
 */
public class InDiaryMapViewFragment extends Fragment implements OnMapReadyCallback  {

    private Context context;

    private List<LatLng> locations;

    private View view;

    private GoogleMap googleMap;
    private MapView mapView;

    public InDiaryMapViewFragment() {
        locations = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_in_diary_map_view, container, false);

        initView();

        initGoogleMap(savedInstanceState);

        return view;
    }

    private void initView() {
        mapView = (MapView) view.findViewById(R.id.map);
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        MapsInitializer.initialize(getActivity().getApplicationContext());

        mapView.getMapAsync(this);
    }
  
    public void init() {
        locations.clear();
        if (this.googleMap != null) {
            this.googleMap.clear();
        }
    }

    public void add(LatLng latLng) {
        locations.add(latLng);
        this.googleMap.addMarker(new MarkerOptions().position(latLng));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        updateRoute();
    }

    public void set(List<LatLng> locations) {
        this.locations.clear();
        if (this.googleMap != null) {
            this.googleMap.clear();
        }
        for (LatLng latLng : locations) {
            this.locations.add(latLng);
            if (this.googleMap != null) {
                this.googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        }
        if (this.googleMap != null) {
            updateRoute();
        }
    }

    public void pin(int position) {
        if (locations.size() > position) {
            LatLng latLng = locations.get(position);
            this.googleMap.addMarker(new MarkerOptions().position(latLng));
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    private void updateRoute() {
        LatLng[] latLngs = new LatLng[locations.size()];
        locations.toArray(latLngs);
        this.googleMap.addPolyline((new PolylineOptions())
                .add(latLngs)
                .width(6)
                .color(Color.RED)
                .visible(true));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (locations.size() > 0)
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(0), 15));
        for (int i = 0; i < locations.size(); i++) {
            this.googleMap.addMarker(new MarkerOptions().position(locations.get(i)));
        }
        updateRoute();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
