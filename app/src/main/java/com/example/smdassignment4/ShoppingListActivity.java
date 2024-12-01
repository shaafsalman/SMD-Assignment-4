package com.example.smdassignment4;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity {

    FloatingActionButton fabAddItem;
    RecyclerView recyclerView;
    ShoppingListAdapter adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        fabAddItem = findViewById(R.id.fabAddItem);
        recyclerView = findViewById(R.id.recyclerView);

        // Get the current user's unique ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize the Firebase database reference for the current user's shopping items
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("ShoppingItems");

        // Query to fetch shopping items from Firebase for the current user
        Query query = databaseReference;

        // Setup FirebaseRecyclerOptions and adapter
        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query, Item.class)
                        .build();
        adapter = new ShoppingListAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Setup FloatingActionButton to add new items
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the custom view for the dialog
                View dialogView = LayoutInflater.from(ShoppingListActivity.this)
                        .inflate(R.layout.insert_item_dialog, null);

                AlertDialog.Builder addItemDialog = new AlertDialog.Builder(ShoppingListActivity.this)
                        .setView(dialogView);

                EditText etItemName = dialogView.findViewById(R.id.etItemName);
                EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
                EditText etPrice = dialogView.findViewById(R.id.etPrice);

                addItemDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String itemName = etItemName.getText().toString().trim();
                        String quantityStr = etQuantity.getText().toString().trim();
                        String priceStr = etPrice.getText().toString().trim();

                        if (itemName.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
                            Toast.makeText(ShoppingListActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int quantity = Integer.parseInt(quantityStr);
                        double price = Double.parseDouble(priceStr);

                        // Create a new item object
                        Item newItem = new Item(itemName, quantity, price);

                        // Push the new item to the current user's Firebase node
                        String itemId = databaseReference.push().getKey();
                        if (itemId != null) {
                            HashMap<String, Object> itemMap = new HashMap<>();
                            itemMap.put("itemName", itemName);
                            itemMap.put("quantity", quantity);
                            itemMap.put("price", price);

                            databaseReference.child(itemId).setValue(itemMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ShoppingListActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(ShoppingListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

                addItemDialog.setNegativeButton("Cancel", null);
                addItemDialog.show();
            }
        });

        FloatingActionButton fabLogout = findViewById(R.id.logoutFab);
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log out the user
                FirebaseAuth.getInstance().signOut();

                // Show a message to the user
                Toast.makeText(ShoppingListActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

                // Redirect to the login activity (assuming you have a LoginActivity)
                startActivity(new Intent(ShoppingListActivity.this, LoginActivity.class));
                finish(); // Close the current activity
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening to Firebase changes
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop listening to Firebase changes
        adapter.stopListening();
    }
}
