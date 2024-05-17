
package com.example.a71plostfoundapp;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a71plostfoundapp.database.DatabaseHelper;

import java.util.Calendar;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, descriptionEditText, dateEditText, locationEditText;
    private RadioGroup postTypeGroup;
    private Button saveButton;
    private Button dateButton; // Button to trigger date picker

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        // Initialize UI components
        dateButton = findViewById(R.id.dateButton);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        dateEditText = findViewById(R.id.dateEditText);
        locationEditText = findViewById(R.id.locationEditText);
        postTypeGroup = findViewById(R.id.postTypeGroup);
        saveButton = findViewById(R.id.saveButton);

        // Set up the date picker dialog
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAdvertActivity.this,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            // Format the date into "YYYY-MM-DD"
                            String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                            dateEditText.setText(formattedDate);
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Set up the save button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postType = ((RadioButton) findViewById(postTypeGroup.getCheckedRadioButtonId())).getText().toString();
                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String location = locationEditText.getText().toString();

                // Validate inputs (basic example)
                if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                    Toast.makeText(CreateAdvertActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                }

                // Insert data into SQLite database
                DatabaseHelper dbHelper = new DatabaseHelper(CreateAdvertActivity.this);
                dbHelper.insertItem(postType, name, phone, description, date, location);
                Toast.makeText(CreateAdvertActivity.this, "Item Saved Successfully", Toast.LENGTH_SHORT).show();

                // Finish activity and return to the main screen
                finish();
            }
        });
    }
}
