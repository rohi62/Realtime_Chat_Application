package com.example.samefor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.samefor.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SpleshActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splesh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = FirebaseFirestore.getInstance();

        new Handler().postDelayed(() -> {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(SpleshActivity.this);
            if (account != null) {
                String userId = account.getId();
                checkUserData(userId);
            } else {
                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);
    }

    private void checkUserData(String userId) {
        db.collection("Data").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UserModel userModel = document.toObject(UserModel.class);
                            if (userModel != null && userModel.getUSERNAME() != null && !userModel.getUSERNAME().isEmpty()) {
                                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                            } else {
                                startActivity(new Intent(SpleshActivity.this, UserNameAcivity.class));
                            }
                        } else {
                            startActivity(new Intent(SpleshActivity.this, UserNameAcivity.class));
                        }
                    } else {
                        startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                    finish();
                });


    }
}
