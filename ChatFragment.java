package com.example.samefor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.samefor.adpter.RecentChatAdapter;
import com.example.samefor.model.ChatroomModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatAdapter adapter;
    List<ChatroomModel> chatrooms;
    FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatrooms = new ArrayList<>();
        adapter = new RecentChatAdapter(chatrooms, getContext());
        recyclerView.setAdapter(adapter);


        firestore = FirebaseFirestore.getInstance();

        loadRecentChats();

        return view;
    }

    private void loadRecentChats() {
        String currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User ID is not available", Toast.LENGTH_SHORT).show();
            Log.e("ChatFragment", "User ID is not available");
            return;
        }

        Log.d("ChatFragment", "Fetching recent chats for user: " + currentUserId);

        CollectionReference chatroomsRef = firestore.collection("Chatrooms");
        chatroomsRef.whereArrayContains("userIds", currentUserId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("ChatFragment", "Listen failed.", e);
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        chatrooms.clear();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ChatroomModel chatroom = document.toObject(ChatroomModel.class);
                            if (chatroom != null) {
                                chatrooms.add(chatroom);
                            }
                        }


                        adapter.updateChatrooms(chatrooms);
                        Log.d("ChatFragment", "Recent chats updated.");
                    } else {
                        Log.d("ChatFragment", "No recent chats found for user: " + currentUserId);
                    }
                });
    }


    private String getCurrentUserId() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            return account.getId();
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                return auth.getCurrentUser().getUid();
            } else {

                Log.e("ChatFragment", "User is not logged in");
                return null;
            }
        }
    }
}
