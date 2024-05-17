package com.example.a71plostfoundapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a71plostfoundapp.database.DatabaseHelper;
import com.example.a71plostfoundapp.database.Item;

import java.util.List;

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView itemsRecyclerView;
    private ItemsAdapter itemsAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        itemsRecyclerView = findViewById(R.id.itemsRecyclerView);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItemList();
    }

    private void refreshItemList() {
        List<Item> itemList = dbHelper.getAllItems(); // Retrieves the latest list of items
        if (itemsAdapter == null) {
            itemsAdapter = new ItemsAdapter(itemList, this);
            itemsRecyclerView.setAdapter(itemsAdapter);
        } else {
            itemsAdapter.updateItemList(itemList); // Always update the list and refresh the adapter
            itemsAdapter.notifyDataSetChanged(); // Make sure to notify the adapter
        }
    }
}
