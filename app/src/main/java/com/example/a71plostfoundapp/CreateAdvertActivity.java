package com.example.a71plostfoundapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a71plostfoundapp.database.DatabaseHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private RadioGroup radioPostType;
    private RadioButton radioLost, radioFound;
    private EditText editTextName, editTextPhone, editTextDescription, editTextDate, editTextLocation;
    private Button buttonSave, buttonGetCurrentLocation, buttonShowOnMap;
    private double latitude;
    private double longitude;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        databaseHelper = new DatabaseHelper(this);

        radioPostType = findViewById(R.id.radioGroupPostType);
        radioLost = findViewById(R.id.radioLost);
        radioFound = findViewById(R.id.radioFound);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextDate = findViewById(R.id.editTextDate);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.buttonSave);
        buttonGetCurrentLocation = findViewById(R.id.buttonGetCurrentLocation);


        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDzMPr2qSJ9Ae13HMf_yQ_d0bscTvLszjM");
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the date picker dialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        // Set up the Autocomplete intent
        editTextLocation.setFocusable(false);
        editTextLocation.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(CreateAdvertActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        // GetCurrentLocation Button Click Listener
        buttonGetCurrentLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                getLocation();
            }
        });

        buttonSave.setOnClickListener(v -> {
            String postType = radioLost.isChecked() ? "Lost" : "Found";
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String description = editTextDescription.getText().toString();
            String date = editTextDate.getText().toString();
            String location = editTextLocation.getText().toString();

            // Check if all fields are filled
            if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(CreateAdvertActivity.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the item to the database
            databaseHelper.insertItem(postType, name, phone, description, date, location, latitude, longitude);

            // Clear input fields
            clearFields();

            // Notify the user
            Toast.makeText(CreateAdvertActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
        });



        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                    editTextDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("Location", "Location: " + location.toString());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Geocoder geocoder = new Geocoder(CreateAdvertActivity.this, Locale.getDefault());
                    try {
                        Log.d("Location", "Attempting to get address...");
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            String addressFragments = address.getMaxAddressLineIndex() >= 0 ? address.getAddressLine(0) : "";
                            Log.d("Location", "Address to be set: " + addressFragments);
                            runOnUiThread(() -> editTextLocation.setText(addressFragments));
                            Log.d("Location", "Address set to editTextLocation: " + editTextLocation.getText().toString());
                            updateMapLocation(); // Update map with new location
                        } else {
                            Toast.makeText(CreateAdvertActivity.this, "No address found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException ioException) {
                        Toast.makeText(CreateAdvertActivity.this, "Service not available", Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException illegalArgumentException) {
                        Toast.makeText(CreateAdvertActivity.this, "Invalid lat long used", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateAdvertActivity.this, "Location not detected", Toast.LENGTH_SHORT).show();
                    Log.d("Location", "Location not detected");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to clear input fields after saving an item
    private void clearFields() {
        editTextName.setText("");
        editTextPhone.setText("");
        editTextDescription.setText("");
        editTextDate.setText("");
        editTextLocation.setText("");
        radioPostType.clearCheck();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextLocation.setText(place.getAddress()); // Set the address to the editText
                if (place.getLatLng() != null) {
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    updateMapLocation();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(this, "Address selection canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    private void updateMapLocation() {
        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }
}
