package com.example.samefor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton searchButton;
    FirebaseFirestore db;


    ChatFragment chatFrangment;
    ProfileFragment profileFrangment;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseApp.initializeApp(this);

        chatFrangment =new  ChatFragment();
        profileFrangment= new ProfileFragment();
        bottomNavigationView =findViewById(R.id.bottom_navigation);
        searchButton=findViewById(R.id.main_search_btn);

        db=FirebaseFirestore.getInstance();

        searchButton.setOnClickListener(v -> {

            Intent intent=new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);

        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.menu_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,chatFrangment).commit();
                    // Toast.makeText(getApplicationContext(),"chat fragment",Toast.LENGTH_SHORT).show();

                }
                if (item.getItemId()==R.id.menu_profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFrangment).commit();
                    // Toast.makeText(getApplicationContext(),"profile fragment",Toast.LENGTH_SHORT).show();

                }
                return true;
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {

            userId = acct.getId();
        }else {
            Toast.makeText(getApplicationContext(),"genrating userId failed ",Toast.LENGTH_SHORT).show();
        }

        bottomNavigationView.setSelectedItemId(R.id.menu_chat);


    }


}