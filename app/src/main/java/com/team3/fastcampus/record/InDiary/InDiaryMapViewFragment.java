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

    private int position;

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

    public void set(List<LatLng> locations) {
        this.locations.clear();
        if (this.googleMap != null) {
            this.googleMap.clear();
        }
        for (LatLng latLng : locations) {
            this.locations.add(latLng);
            if (latLng != null) {
                if (this.googleMap != null) {
                    this.googleMap.addMarker(new MarkerOptions().position(latLng));
                }
            }
        }
        if (this.googleMap != null) {
            updateRoute();
        }
    }

    public void pin(int position) {
        if (locations.size() > position) {
            this.position = position;
            if (googleMap != null) {
                LatLng latLng = locations.get(position);
                if (latLng != null) {
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        }
    }

    private void updateRoute() {
        int size = 0;
        for (LatLng latLng : locations) {
            if (latLng != null) {
                size++;
            }
        }
        LatLng[] latLngs = new LatLng[size];
        int count = 0;
        for (LatLng latLng : locations) {
            if (latLng != null) {
                latLngs[count++] = latLng;
            }
        }
        this.googleMap.addPolyline((new PolylineOptions())
                .add(latLngs)
                .width(6)
                .color(Color.RED)
                .visible(true));
        if (locations.size() > position + 1) {
            LatLng latLng = locations.get((position % 2 == 0) ? position + 1 : position);
            if (latLng != null)
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        for (LatLng latLng : locations) {
            if (latLng != null) {
                this.googleMap.addMarker(new MarkerOptions().position(latLng));
            }
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
