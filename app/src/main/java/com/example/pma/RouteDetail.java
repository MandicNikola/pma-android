package com.example.pma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.pma.model.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RouteDetail extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView headerText;
    private TextView caloriesText;
    private TextView distanceText;
    private TextView fromDateText;
    private TextView toDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
