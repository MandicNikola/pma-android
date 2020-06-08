package com.example.pma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.pma.database.DatabaseManagerPoint;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.Route;
import com.mapbox.geojson.Feature;
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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class RouteDetail extends AppCompatActivity implements OnMapReadyCallback {

    private TextView headerText;
    private TextView caloriesText;
    private TextView distanceText;
    private TextView fromDateText;
    private TextView toDateText;

    private MapView mapView;
    private MapboxMap mapboxMap;

    private Route route;
    private List points = new ArrayList<com.example.pma.model.Point>();

    private DatabaseManagerRoute managerRoute;
    private DatabaseManagerPoint managerPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.getLongExtra("route", 0) != 0) {
            managerRoute = new DatabaseManagerRoute(this);
            managerRoute.open();
            managerPoint = new DatabaseManagerPoint(this);
            managerPoint.open();

            Long id = intent.getLongExtra("route", 0);
            route = managerRoute.getRoute(id);

            points = managerPoint.getRoutePoints(id);
            managerPoint.close();
            managerRoute.close();
        }

        // map init flow
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_route_detail);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        caloriesText = findViewById(R.id.calories_data);
        distanceText = findViewById(R.id.distance_data);
        fromDateText = findViewById(R.id.date_from);
        toDateText = findViewById(R.id.date_to);
        headerText = findViewById(R.id.route_header);

        if(route != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            caloriesText.setText(decimalFormat.format(route.getCalories()) + " cal");
            distanceText.setText(decimalFormat.format(route.getDistance()) + " m");
            toDateText.setText(route.getEnd_time());
            fromDateText.setText(route.getStart_time());
        }
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
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            if(points.size() > 1) {
                com.example.pma.model.Point point = (com.example.pma.model.Point) points.get(0);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(point.getLatitude(), point.getLongitude()))
                        .zoom(15)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }

            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            style.addSource(drawLines());
            style.addLayer(new LineLayer("linelayer", "line-source")
                    .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                            PropertyFactory.lineOpacity(.7f),
                            PropertyFactory.lineWidth(7f),
                            PropertyFactory.lineColor(Color.parseColor("#3bb2d0"))));

        });
    }

    // dummy data for drawing lines
    // need to adapt that better later
    // extract route coordinates
    public GeoJsonSource drawLines() {
        List route = new ArrayList<Point>();

        Collections.sort(this.points);

        for(Object p: this.points) {
            route.add(Point.fromLngLat(((com.example.pma.model.Point) p).getLongitude(), ((com.example.pma.model.Point) p).getLatitude()));
        }

        LineString lineString = LineString.fromLngLats(route);

        Feature feature = Feature.fromGeometry(lineString);

        return new GeoJsonSource("line-source", feature);
    }
}
