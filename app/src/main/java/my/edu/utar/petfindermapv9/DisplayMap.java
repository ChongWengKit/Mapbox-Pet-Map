package my.edu.utar.petfindermapv9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayMap extends AppCompatActivity {
    private MapView mapView;
    private String placeName;
    private double latitude;
    private double longitude;
    @Override
    //FOR DISPLAY PAGE
    protected void onCreate(Bundle savedInstanceState) {
        // extract from firebase , string
        placeName = "355 Buena Vista Avenue East";


        //

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                                .accessToken("sk.eyJ1IjoibmV3YmllODciLCJhIjoiY2x3MGhldTljMDB0ZDJqb2MyZjF2bjJ3bSJ9.dsJrHIdSOg8B5MvkeS_yPA")
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


                                        IconFactory iconFactory = IconFactory.getInstance(DisplayMap.this);
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
                                    }


                                }
                                else {

                                    // No result for your request were found.

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
}
