package com.example.pma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pma.model.Route;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

public class RouteDetail extends AppCompatActivity implements OnMapReadyCallback {

    private TextView headerText;
    private TextView caloriesText;
    private TextView distanceText;
    private TextView fromDateText;
    private TextView toDateText;

    private MapView mapView;
    private MapboxMap mapboxMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



        Intent intent = getIntent();
        if(intent.getParcelableExtra("route") != null) {
            Route route = intent.getParcelableExtra("route");
            caloriesText.setText(route.getCalories() + " cal");
            distanceText.setText(route.getDistance() + " " + route.getUnit());
            headerText.setText("Route #" + route.getId() + " Details");
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

        route.add(Point.fromLngLat( 19.850329868495464,
                45.25833272840828));
        route.add(Point.fromLngLat( 19.853881699964404,
                45.258505563251674));
        route.add(Point.fromLngLat( 19.853894859552383,
                45.258485530503094));
        route.add(Point.fromLngLat( 19.853800479322672,
                45.258491984568536));
//        route.add(Point.fromLngLat( -122.65631,
//                45.52104));
//        route.add(Point.fromLngLat( -122.6578,
//                45.51935));
//        route.add(Point.fromLngLat( -122.65867,
//                45.51848));

        LineString lineString = LineString.fromLngLats(route);

        Feature feature = Feature.fromGeometry(lineString);

        return new GeoJsonSource("line-source", feature);
    }
}
