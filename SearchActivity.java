package com.example.samefor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samefor.adpter.RecyclerAdpter;
import com.example.samefor.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ImageButton back_arrow;
    RecyclerView recyclerView;
    RecyclerAdpter recyclerAdpter;
    List<UserModel> userList;
    List<UserModel> filteredList;
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchView = findViewById(R.id.searchview);
        recyclerView = findViewById(R.id.reacyclesearch);
        back_arrow = findViewById(R.id.back_arrow);

        userList = new ArrayList<>();
        filteredList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        recyclerAdpter = new RecyclerAdpter(filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdpter);

        fetchUsers();

        back_arrow.setOnClickListener(v -> onBackPressed());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });





    }

    private void fetchUsers() {
        db.collection("Data")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserModel user = document.toObject(UserModel.class);
                            userList.add(user);
                        }
                        filteredList.clear();
                        filteredList.addAll(userList);
                        recyclerAdpter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filter(String text) {
        ArrayList<UserModel> filtered = new ArrayList<>();
        for (UserModel userModel : userList) {
            if (userModel.getUSERNAME().toLowerCase().contains(text.toLowerCase()) || userModel.getEMAIL().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(userModel);
            }
        }
        recyclerAdpter.updateList(filtered);
    }


}
