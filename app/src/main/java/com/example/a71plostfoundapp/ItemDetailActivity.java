package com.example.a71plostfoundapp;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a71plostfoundapp.database.DatabaseHelper;
import com.example.a71plostfoundapp.database.Item;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.text.format.DateUtils;
public class ItemDetailActivity extends AppCompatActivity {

    private TextView nameTextView, locationTextView, dateTextView;
    private Button removeButton;
    private int itemId; // ID of the item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Initialize UI components
        nameTextView = findViewById(R.id.nameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        dateTextView = findViewById(R.id.dateTextView);
        removeButton = findViewById(R.id.removeButton);

        // Get the item ID from the Intent
        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        loadItemDetails(itemId);

        // Set up the remove button
        removeButton.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.deleteItem(itemId);
            Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity and return to the previous screen
        });
    }



    private void loadItemDetails(int itemId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Item item = dbHelper.getItem(itemId);
        if (item != null) {
            nameTextView.setText(item.getName());
            locationTextView.setText(item.getLocation());

            // Format the date as "X days ago"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = sdf.parse(item.getDate());
                long timeInMillis = date.getTime();
                CharSequence daysAgo = DateUtils.getRelativeTimeSpanString(timeInMillis, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS);
                dateTextView.setText(daysAgo);
            } catch (ParseException e) {
                e.printStackTrace();
                dateTextView.setText("Date parsing error");
            }
        } else {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

}
