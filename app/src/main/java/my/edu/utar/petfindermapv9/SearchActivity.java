package my.edu.utar.petfindermapv9;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_search);

            PlaceAutocompleteFragment autocompleteFragment =
                    (PlaceAutocompleteFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.BLACK);
        // Handle autocomplete selections and return results to the calling activity
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(CarmenFeature carmenFeature) {
                // Extract relevant information from the selected CarmenFeature
                String placeName = carmenFeature.text();
                double latitude = carmenFeature.center().latitude();
                double longitude = carmenFeature.center().longitude();

                // Create an Intent to send the selected place data back to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_place_name", placeName);
                resultIntent.putExtra("selected_place_latitude", latitude);
                resultIntent.putExtra("selected_place_longitude", longitude);
                setResult(MainActivity.RESULT_OK, resultIntent);

                // Finish the search activity
                finish();
            }

            @Override
            public void onCancel() {
                // Handle cancellation if needed
            }
        });
        }
    }
