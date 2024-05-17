package com.example.a71plostfoundapp;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a71plostfoundapp.database.Item;

import java.util.List;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<Item> itemList;
    private Context context; // To use for starting a new activity

    public ItemsAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.nameTextView.setText(item.getName());
        holder.itemView.setOnClickListener(v -> {
            // Start ItemDetailActivity and pass the item ID
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("ITEM_ID", item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItemList(List<Item> newItemList) {
        this.itemList = newItemList; // Update the list with new data
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }
}
