package com.example.pma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pma.database.DatabaseManagerGoal;
import com.example.pma.database.DatabaseManagerPoint;
import com.example.pma.database.DatabaseManagerRoute;
import com.example.pma.model.Goal;
import com.example.pma.model.GoalResponse;
import com.example.pma.services.GoalPlaceholder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

public class ActiveRoute extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;

    private static final String DOT_SOURCE_ID = "dot-source-id";

    /** `array` of route points */
    private List points = new ArrayList<Point>();
    // TODO: Use them for memorizing
    /** `array` list from locations, later use them for memorizing to DB */
    private List locations = new ArrayList<Location>();

    // distance calculated for route
    private Float distance = 0f;
    // calories calculated for route
    private Double calories = 0.0;

    // coordinates of map center
    private Double lat;
    private Double lng;

    private TextView distanceValueView;
    private TextView caloriesValueView;
    // TODO: Later when get user settings need to provide those values from DB
    private double height = 192;
    private double weight = 105;

    private boolean startTracking = false;
    //for  SQlite database
    private DatabaseManagerRoute dbManager;
    private DatabaseManagerPoint dbManagerPoint;
    private DatabaseManagerGoal dbManagerGoal;


    // GOOGLE API LOCATIONS USED FOR APP
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback;

    private Button finishButton;
    private static final String TAG = "ActiveRoute";

    // Notification variables
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    private NotificationManager mNotifyManager;

    private static final int NOTIFICATION_ID = 0;

    Retrofit retrofit;

    private GoalPlaceholder goalService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_active_route);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://pma-app-19.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        distanceValueView = (TextView)findViewById(R.id.distance_value);
        distanceValueView.setText("0 m");
        caloriesValueView = (TextView)findViewById(R.id.calories_value);
        caloriesValueView.setText("0 cal");

        dbManager = new DatabaseManagerRoute(this);
        dbManager.open();
        dbManagerPoint = new DatabaseManagerPoint(this);
        dbManagerPoint.open();
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        finishButton = findViewById(R.id.finishBtn);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if(location != null) {
                ActiveRoute.this.lat = location.getLatitude();
                ActiveRoute.this.lng = location.getLongitude();
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onLocationChanged(locationResult.getLastLocation());
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(getLocationRequest((long)10000, (long)2000), locationCallback, null);

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
        // TODO: Need to get userSettings because need for user weight and height

        // Initialization of notification channel
        createNotificationChannel();
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
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if(location != null) {
                        ActiveRoute.this.lat = location.getLatitude();
                        ActiveRoute.this.lng = location.getLongitude();
                    }
                });

                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        onLocationChanged(locationResult.getLastLocation());
                    }
                };

                fusedLocationProviderClient.requestLocationUpdates(getLocationRequest((long)10000, (long)2000), locationCallback, null);
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
            // used this to round numbers of this
            distanceValueView.setText(Math.round(this.distance*100)/100.0 + " m");
            caloriesValueView.setText(Math.round(this.calories*100)/100.0 + " cal");
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
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        if(startTracking){
            Location firstLocation = (Location) locations.get(0);
            Location lastLocation = (Location) locations.get(locations.size()-1);
            Date start_date = new Date(firstLocation.getTime());
            Date end_date = new Date(lastLocation.getTime());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate1 = simpleDateFormat.format(start_date);
            String strDate2 = simpleDateFormat.format(end_date);

            long id = -1;
            id=dbManager.insert(this.calories,this.distance,"m",(long)-1,strDate1,strDate2);
            for(int i = 0 ;i< locations.size();i++){
                Location location = (Location) locations.get(i);
                Date current_time = new Date(location.getTime());
                String current_time_string = simpleDateFormat.format(current_time);
                long idPoint = dbManagerPoint.insert((float)location.getLongitude(),(float)location.getLatitude(),id,current_time_string);
            }
            startTracking = false;
        }
        mapView.onDestroy();
    }

    public void onLocationChanged(Location location) {
        if(location != null) {
            this.lat = location.getLatitude();
            this.lng = location.getLongitude();

            Double lng = location.getLongitude();
            Double lat = location.getLatitude();

            if(startTracking) points.add(Point.fromLngLat(lng, lat));

            if(startTracking) calculateValues(location);

                if(mapboxMap != null) {
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
    }

    /**
     *
     * @param interval - interval used to get location updates
     * @param fastestInterval - fastest interval, used to prepare to get location.
     * @return locationRequest prepared for `fused` API
     */
    private LocationRequest getLocationRequest(Long interval, Long fastestInterval) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    // handle when route is finished
    // TODO: put route in DB
    public void onFinishClick(View view) throws ParseException {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        // TODO: Handle case when there's not locations
        Location firstLocation = (Location) locations.get(0);
        Location lastLocation = (Location) locations.get(locations.size()-1);
        Date start_date = new Date(firstLocation.getTime());
        Date end_date = new Date(lastLocation.getTime());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate1 = simpleDateFormat.format(start_date);
        String strDate2 = simpleDateFormat.format(end_date);

        // indicator for show notification
        boolean showNotification = false;

        long id = -1;
        id=dbManager.insert(this.calories,this.distance,"m",(long)-1,strDate1,strDate2);
        for(int i = 0 ;i< locations.size();i++){
            Location location = (Location) locations.get(i);
            Date current_time = new Date(location.getTime());
            String current_time_string = simpleDateFormat.format(current_time);
            long idPoint = dbManagerPoint.insert(location.getLongitude(), location.getLatitude(),id,current_time_string);
        }
        Date endDateRoute=new SimpleDateFormat("yyyy-MM-dd").parse(strDate1);
        updateGoal(endDateRoute);

        if(showNotification) {
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        }

        Intent intent = new Intent(this, RouteActivity.class);
        startActivity(intent);

    }
    public void updateGoal(Date endDateRoute){
        ArrayList<Goal> goals = new ArrayList<>();
        dbManagerGoal = new DatabaseManagerGoal(this);
        dbManagerGoal.open();

        goals = dbManagerGoal.getGoals();
        for(Goal goal:goals){
            if(goal.getDate().after(endDateRoute)){
                Log.d(TAG," dosao po ciljeve");

                if(goal.getNotified() == 0) {
                    Log.d(TAG," nije notified");
                SimpleDateFormat simpleDateFormatGoal = new SimpleDateFormat("yyyy-MM-dd");
                String goalDate = simpleDateFormatGoal.format(goal.getDate());

                // TODO: update boolean for showing notification, also goals that are already completed should not be considered
                if(goal.getGoalKey().equals("Distance")) {

                        double currentValueDistance = goal.getCurrentValue() + this.distance;
                        Log.d(TAG," u distance je");
                        if(currentValueDistance < goal.getGoalValue()) {
                            updateGoalBack(currentValueDistance,goal.getBackId(),0);

                            dbManagerGoal.update(goal.getId(), goal.getGoalKey(), goal.getGoalValue(), goalDate, currentValueDistance, goal.getNotified(), goal.getBackId());
                        }else{
                            updateGoalBack(currentValueDistance,goal.getBackId(),1);
                            dbManagerGoal.update(goal.getId(), goal.getGoalKey(), goal.getGoalValue(), goalDate, currentValueDistance, 1, goal.getBackId());

                        }
                }else{

                        double currentValueCalories = goal.getCurrentValue() + this.calories;

                        if (currentValueCalories < goal.getGoalValue()) {

                            Log.d(TAG, " calories cilj" + currentValueCalories);
                            updateGoalBack(currentValueCalories,goal.getBackId(),0);

                            dbManagerGoal.update(goal.getId(), goal.getGoalKey(), goal.getGoalValue(), goalDate, currentValueCalories, goal.getNotified(), goal.getBackId());
                        }else{
                            Log.d(TAG, " calories ispunjen cilj" + currentValueCalories);
                            updateGoalBack(currentValueCalories,goal.getBackId(),1);

                            dbManagerGoal.update(goal.getId(), goal.getGoalKey(), goal.getGoalValue(), goalDate, currentValueCalories, 1, goal.getBackId());

                        }

                    }
                }
            }
        }
        dbManagerGoal.close();

    }

    public void updateGoalBack(double currentValue, long id,int notifiedFlag){
        goalService = retrofit.create(GoalPlaceholder.class);
        GoalResponse goal = new GoalResponse(id,currentValue,notifiedFlag);

        Call<GoalResponse> callGoal = goalService.updateGoal(goal);
        callGoal.enqueue(new Callback<GoalResponse>() {
            @Override
            public void onResponse(Call<GoalResponse> call, Response<GoalResponse> response) {
                Log.d(TAG," kod je"+response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG," uspjesno  je"+response.body().getId());
                    if(response.code() == 200){
                        Log.d(TAG," vratio se posle update goals back"+response.code());
                    }
                }
            }
            @Override
            public void onFailure(Call<GoalResponse> call, Throwable t) {
                Log.d(TAG," neuspesno update goal");

            }
        });


    }
    public void onStartClick(View view) {

        view.setVisibility(View.GONE);
        finishButton.setVisibility(View.VISIBLE);
        initMapCenter();
        this.startTracking = true;
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Good job!")
                .setContentText("You have reached some goals.")
                .setSmallIcon(R.drawable.ic_accessibility_black_24dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(notificationPendingIntent);
        return notifyBuilder;
    }

}
