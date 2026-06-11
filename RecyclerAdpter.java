package com.example.samefor.adpter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.samefor.ChatroomActivity;
import com.example.samefor.R;
import com.example.samefor.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdpter extends RecyclerView.Adapter<RecyclerAdpter.UserViewHolder> {

    private List<UserModel> userList;
    private List<UserModel> filteredUserList;
    private Context context;
    private String currentUserId;

    public RecyclerAdpter(List<UserModel> userList, Context context) {
        this.context = context;
        this.userList = userList;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            currentUserId = account.getId();
            filteredUserList = filterOutCurrentUser(userList, currentUserId);
        } else {
            Toast.makeText(context, "User not logged in. Please log in.", Toast.LENGTH_SHORT).show();
            filteredUserList = new ArrayList<>(userList);
        }
    }

    private List<UserModel> filterOutCurrentUser(List<UserModel> originalList, String currentUserId) {
        List<UserModel> filteredList = new ArrayList<>();
        for (UserModel user : originalList) {
            if (!user.getUserId().equals(currentUserId)) {
                filteredList.add(user);
            }
        }
        return filteredList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = filteredUserList.get(position);
        holder.usernameTextView.setText(user.getUSERNAME());
        holder.emailTextView.setText(user.getEMAIL());

        fetchCurrentUserDetails(currentUserId, holder, user);
    }

    @Override
    public int getItemCount() {
        return filteredUserList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, emailTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.recyclervview);
            emailTextView = itemView.findViewById(R.id.email);
        }
    }

    public void updateList(List<UserModel> newList) {
        userList = newList;
        filteredUserList = filterOutCurrentUser(newList, currentUserId);
        notifyDataSetChanged();
    }

    private void fetchCurrentUserDetails(String currentUserId, UserViewHolder holder, UserModel otherUser) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Data")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        if (userModel != null) {
                            String currentUserName = userModel.getUSERNAME();


                            String otherUserFcmToken = otherUser.getFcmToken();
                            if (otherUserFcmToken == null || otherUserFcmToken.isEmpty()) {
                                Toast.makeText(context, "FCM token is missing for this user", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(context, ChatroomActivity.class);
                                intent.putExtra("CURRENT_USER_NAME", currentUserName);
                                intent.putExtra("OTHER_USERNAME", otherUser.getUSERNAME());
                                intent.putExtra("CURRENT_USER_ID", currentUserId);
                                intent.putExtra("OTHER_USER_ID", otherUser.getUserId());
                                intent.putExtra("OTHER_FCM_TOKEN", otherUserFcmToken);  // Pass FCM token
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });

                        } else {
                            Toast.makeText(context, "No UserName Found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "No User Found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreError", "Error fetching user data", e);
                    Toast.makeText(context, "Error Loading UserName", Toast.LENGTH_SHORT).show();
                });
    }

}
