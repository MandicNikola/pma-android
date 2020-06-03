package com.example.pma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class ActiveRoute extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private MapView mapView;
    private MapboxMap mapboxMap;

    private static final String DOT_SOURCE_ID = "dot-source-id";

    /** `array` of route points */
    private List points = new ArrayList<Point>();
    // TODO: Use them for memorizing
    /** `array` list from locations, later use them for memorizing to DB */
    private List locations = new ArrayList<Location>();

    private LocationManager locationManager;

    // distance calculated for route
    private Float distance = 0f;
    // calories calculated for route
    private Double calories = 0.0;

    // coordinates of map center
    private Double lat;
    private Double lng;

    // TODO: Later when get user settings need to provide those values from DB
    private double height = 192;
    private double weight = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_active_route);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        /* location manager init flow */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET,
                }, 10);
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 1, this);

        // TODO: Need to get userSettings because need for user weight and height
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        double initLat = lat != null ? lat : 45.2671;
        double initLng = lng != null ? lng : 19.83;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(initLat, initLng))
                .zoom(15)
                .build();
        mapboxMap.setCameraPosition(cameraPosition);
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            LineString lineString = LineString.fromLngLats(points);
            Feature feature = Feature.fromGeometry(lineString);
            // line init flow
            style.addSource(new GeoJsonSource("line-source", feature));

            style.addLayer(new LineLayer("linelayer", "line-source")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(7f),
                            PropertyFactory.lineColor(Color.parseColor("#3bb2d0"))));


            // marker init flow
            style.addImage(DOT_SOURCE_ID, BitmapFactory.decodeResource(getResources(), R.drawable.blue_marker));

            List<Feature> iconFeatureList = new ArrayList<>();
            iconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(initLng, initLat)));

            style.addSource(new GeoJsonSource("dot-source", FeatureCollection.fromFeatures(iconFeatureList)));

            style.addLayer(new SymbolLayer("dot-layer", "dot-source")
                .withProperties(
                        iconImage(DOT_SOURCE_ID),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true),
                        iconSize(0.5f)
                )
            );

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 1, this);
                lat = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                lng = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
                initMapCenter();
                break;
            default:
                break;
        }
    }

    private void initMapCenter() {
        if(mapboxMap != null) {
            double initLat = lat != null ? lat : 45.2671;
            double initLng = lng != null ? lng : 19.83;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(initLat, initLng))
                    .zoom(15)
                    .build();
            mapboxMap.setCameraPosition(cameraPosition);
        }
    }

    private void calculateValues(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();

        if(locations.size() >= 1) {
            // get last point
            Location previousLocation = (Location) locations.get(locations.size() - 1);
            float[] calculatedDistance = new float[2];

            Location.distanceBetween(lat, lng, previousLocation.getLatitude(), previousLocation.getLongitude(), calculatedDistance);
            this.distance += calculatedDistance[0];
            float timeDifference = ((location.getTime() - previousLocation.getTime()) / 1000);
            float speed = calculatedDistance[0] / timeDifference;
            this.calories += ((0.035 * weight) + (Math.pow(speed, 2) / height)*(0.029 * weight)) * (timeDifference / 60);

            String message = new StringBuilder()
                    .append("Calories: ")
                    .append(this.calories)
                    .append(" cal \n")
                    .append("Distance: ")
                    .append(this.distance)
                    .append(" m")
                    .toString();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        locations.add(location);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(this);
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {

        Double lng = location.getLongitude();
        Double lat = location.getLatitude();

        points.add(Point.fromLngLat(lng, lat));

        // TODO: ADD speed calculation
        calculateValues(location);
        // TODO: ADD calories calculation, need to extract settings from user
        if(mapboxMap != null) {
            // start route to track, now move to the center
            if(points.size() == 1) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(15)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }
            mapboxMap.getStyle( style -> {
                GeoJsonSource source = style.getSourceAs("line-source");
                LineString lineString = LineString.fromLngLats(points);
                Feature feature = Feature.fromGeometry(lineString);
                source.setGeoJson(feature);

                // update icon
                source = style.getSourceAs("dot-source");
                source.setGeoJson(Feature.fromGeometry(Point.fromLngLat(location.getLongitude(), location.getLatitude())));
            });
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    // handle when route is finished
    // TODO: put route in DB
    public void onFinishClick(View view) {
        locationManager.removeUpdates(this);
        Intent intent = new Intent(this, RouteActivity.class);
        startActivity(intent);
    }
}
