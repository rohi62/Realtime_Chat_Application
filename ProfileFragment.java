package com.example.samefor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.samefor.LoginActivity;
import com.example.samefor.R;
import com.example.samefor.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseFirestore db;
    UserModel userModel;
    EditText usernamechange;
    Button save_button;
    String UserName;
    String personEmail;
    String personName;
    String userId;
    TextView emailfeatch, emailnamefeatch, usernamefeatch, logoutDialog;
    Dialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        emailfeatch = view.findViewById(R.id.emailfeatch);
        emailnamefeatch = view.findViewById(R.id.emailnamefeatch);
        usernamefeatch = view.findViewById(R.id.usernamefeatch);
        usernamechange = view.findViewById(R.id.usernamechange);
        save_button = view.findViewById(R.id.save_button);
        logoutDialog = view.findViewById(R.id.logoutDialog);

        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            userId = acct.getId();
            emailnamefeatch.setText(personName);
            emailfeatch.setText(personEmail);

            // Fetch user data from Firestore using userId
            db.collection("Data").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                usernamefeatch.setText(userModel.getUSERNAME());
                            } else {
                                usernamefeatch.setText("No UserName Found");
                            }
                        } else {
                            usernamefeatch.setText("No User Found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirestoreError", "Error fetching user data", e);
                        usernamefeatch.setText("Error Loading UserName");
                    });
        } else {
            Toast.makeText(getContext(), "Error: User not signed in", Toast.LENGTH_SHORT).show();
        }

        save_button.setOnClickListener(v -> {
            UserName = usernamechange.getText().toString().trim();

            if (UserName.isEmpty()) {
                usernamechange.setError("Username is empty");
            } else if (UserName.length() < 3) {
                usernamechange.setError("Username must be at least 3 characters long");
            } else {
                updateUsername();
            }
        });

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog);
        dialog.setCancelable(true);

        Button cancledilog = dialog.findViewById(R.id.cancledilog);
        Button logout = dialog.findViewById(R.id.logout);

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN);

        logout.setOnClickListener(v -> mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), task -> {
                    Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }));

        cancledilog.setOnClickListener(v -> dialog.dismiss());

        logoutDialog.setOnClickListener(v -> dialog.show());

        return view;
    }


    private void updateUsername() {
        userModel.setUSERNAME(UserName);
        db.collection("Data").document(userId)
                .set(userModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Username update failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
