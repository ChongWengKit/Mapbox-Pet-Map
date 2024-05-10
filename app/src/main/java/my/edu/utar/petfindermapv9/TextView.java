package my.edu.utar.petfindermapv9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;

public class TextView extends AppCompatActivity {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        findViewById(R.id.searchButton).setOnClickListener(view -> {

            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        });

    }

}