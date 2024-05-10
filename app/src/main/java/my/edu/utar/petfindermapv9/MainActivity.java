package my.edu.utar.petfindermapv9;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private MapView mapView = null;
    private static final String TAG = "YourClassName";
    private double longitude =2.294351;
    private double latitude = 48.858844;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_main);
        launchSearchActivity();

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude))
                                .zoom(0) // Set the zoom level as needed
                                .build();

                        // Set the map's camera position to the default location
                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 50);
                    }
                });
            }
        });
        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirm placeName
                if (placeName != null) {
                    //back to fill info page to put into firebase database


                }
                else {
                    Toast.makeText(MainActivity.this, "Address cannot be empty.", Toast.LENGTH_LONG).show();

                }
                // Your code to handle the button click event goes here
                // For example, you can add code to perform some action when the button is clicked.
            }
        });





    }
    private void launchSearchActivity() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .build(this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            placeName = feature.text();
            if (placeName != null){
                mapMarker(placeName);
            }
            Toast.makeText(this, "Selected Place: " + placeName, Toast.LENGTH_LONG).show();
            latitude = data.getDoubleExtra("selected_place_latitude", 0.0);
            longitude = data.getDoubleExtra("selected_place_longitude", 0.0);
        }
    }
    private void mapMarker(String placeName){
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                                .accessToken("sk.eyJ1IjoibmV3YmllODciLCJhIjoiY2xtMW9nZXR3MGk4OTNlcDlneGQ2bzMzNiJ9.2IMEbL8sa7UvD8onYROLgA")
                                .query(placeName)
                                .build();
                        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
                            @Override
                            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                                List<CarmenFeature> results = response.body().features();
                                if (results.size() > 0) {
                                    Point firstResultPoint = results.get(0).center();
                                    String logMessage = firstResultPoint.toString();
                                    int startIndex = logMessage.indexOf("coordinates=[") + "coordinates=[".length();
                                    int endIndex = logMessage.indexOf("]", startIndex);
                                    String coordinatesStr = logMessage.substring(startIndex, endIndex);

                                    // Split the coordinates string by comma
                                    String[] parts = coordinatesStr.split(",");
                                    try {
                                        // Split the coordinates part by spaces to get longitude and latitude
                                        longitude = Double.parseDouble(parts[0].trim());
                                        latitude = Double.parseDouble(parts[1].trim());

                                        Log.d(TAG, "Longitude: " + longitude);
                                        Log.d(TAG, "Latitude: " + latitude);

                                        IconFactory iconFactory = IconFactory.getInstance(MainActivity.this);
                                        Icon icon = iconFactory.fromResource(R.drawable.marker);
                                        mapboxMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(latitude, longitude)));
                                        CameraPosition position = new CameraPosition.Builder()
                                                .target(new LatLng(latitude, longitude))
                                                .zoom(20)
                                                .build();
                                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 50);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error parsing coordinates: " + e.getMessage());
                                    }


                                }
                                else {

                                    // No result for your request were found.
                                    Log.d(TAG, "onResponse: No result found");

                                }
                            }

                            @Override
                            public void onFailure(Call<GeocodingResponse> call, Throwable t) {

                            }
                        });



                    }
                });

            }

        });
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
}