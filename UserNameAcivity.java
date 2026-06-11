package com.example.samefor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.samefor.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserNameAcivity extends AppCompatActivity {

    EditText username;
    Button usernamebtn;
    ImageButton backbtn;
    FirebaseFirestore db;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String personName;
    String personEmail;
    String UserName;
    String userId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_name_acivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username);
        usernamebtn = findViewById(R.id.userbutton);
        backbtn = findViewById(R.id.backbtn);

        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            userId = acct.getId();
        } else {
            Intent intent = new Intent(UserNameAcivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        backbtn.setOnClickListener(v -> {
            Intent intent = new Intent(UserNameAcivity.this, LoginActivity.class);
            startActivity(intent);
        });

        usernamebtn.setOnClickListener(v -> {
            UserName = username.getText().toString().trim();

            if (UserName.isEmpty()) {
                username.setError("Username cannot be empty");
            } else if (UserName.length() < 3) {
                username.setError("Username must be at least 3 characters long");
            } else {
                saveUserDataToFirestore();
            }
        });


    }

    private void saveUserDataToFirestore() {
        UserModel user = new UserModel(personEmail, personName, UserName, userId);

        if (userId != null && !userId.isEmpty()) {
            db.collection("Data").document(userId)
                    .set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            nextActivity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Creating Account Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Invalid userId", Toast.LENGTH_SHORT).show();
        }
    }

    void nextActivity() {
        Intent intent = new Intent(UserNameAcivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
