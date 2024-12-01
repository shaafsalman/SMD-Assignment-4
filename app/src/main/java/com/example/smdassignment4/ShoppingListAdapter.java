package com.example.smdassignment4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShoppingListAdapter extends FirebaseRecyclerAdapter<Item, ShoppingListAdapter.ItemViewHolder> {

    public ShoppingListAdapter(@NonNull FirebaseRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        // Bind item data to the views
        holder.tvItemName.setText(model.getItemName());
        holder.tvQuantity.setText(String.valueOf(model.getQuantity()));
        holder.tvPrice.setText(String.format("$%.2f", model.getPrice()));

        // Set up delete button
        holder.itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> {
            String itemId = getRef(position).getKey(); // Get the item's ID
            if (itemId != null) {
                // Correct the Firebase path to "Users/{userId}/ShoppingItems"
                DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) // Current user
                        .child("ShoppingItems") // Correct path for shopping items
                        .child(itemId); // Item ID to be deleted

                itemRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(holder.itemView.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvQuantity, tvPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
