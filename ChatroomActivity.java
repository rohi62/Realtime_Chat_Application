package com.example.samefor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samefor.adpter.ChatAdpter;
import com.example.samefor.model.ChatMessageModel;
import com.example.samefor.model.ChatroomModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ChatroomActivity extends AppCompatActivity {

    TextView sender_username;
    EditText write_messages;
    ImageButton sent_btn;
    FirebaseFirestore firestore;
    String chatroomId;
    ChatAdpter adpter;
    RecyclerView recyclerView;
    List<ChatMessageModel> chatMessages;
    ImageButton back_arrow;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestore = FirebaseFirestore.getInstance();

        sender_username = findViewById(R.id.sender_username);
        write_messages = findViewById(R.id.write_messages);
        sent_btn = findViewById(R.id.sent_btn);
        recyclerView = findViewById(R.id.messages);
        back_arrow = findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(v -> onBackPressed());

        chatMessages = new ArrayList<>();
        adpter = new ChatAdpter(chatMessages, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adpter);

        String username = getIntent().getStringExtra("CURRENT_USER_NAME");
        String otherUsername = getIntent().getStringExtra("OTHER_USERNAME");
        sender_username.setText(otherUsername);

        String currentUserId = getIntent().getStringExtra("CURRENT_USER_ID");
        String otherUserId = getIntent().getStringExtra("OTHER_USER_ID");

        if (currentUserId != null && otherUserId != null) {
            List<String> userIds = Arrays.asList(currentUserId, otherUserId);
            List<String> usernames = Arrays.asList(username, otherUsername);
            createOrRetrieveChatroom(userIds, usernames);
        } else {
            Toast.makeText(this, "User IDs are missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        sent_btn.setOnClickListener(v -> {
            String message = write_messages.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToUser(message, currentUserId);
            }
        });

    }

    private void createOrRetrieveChatroom(List<String> userIds, List<String> usernames) {
        firestore.collection("Chatrooms")
                .whereArrayContains("userIds", userIds.get(0))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean chatroomExists = false;

                        for (DocumentSnapshot document : task.getResult()) {
                            ChatroomModel chatroom = document.toObject(ChatroomModel.class);
                            if (chatroom != null && chatroom.getUserIds().containsAll(userIds)) {
                                chatroomId = chatroom.getChatroomId();
                                chatroomExists = true;
                                break;
                            }
                        }

                        if (!chatroomExists) {
                            chatroomId = UUID.randomUUID().toString();
                            ChatroomModel newChatroom = new ChatroomModel(
                                    chatroomId,
                                    userIds,
                                    usernames,
                                    Timestamp.now(),
                                    userIds.get(0),
                                    ""

                            );

                            firestore.collection("Chatrooms").document(chatroomId)
                                    .set(newChatroom)
                                    .addOnSuccessListener(aVoid -> Log.d("ChatroomActivity", "Chatroom created successfully"))
                                    .addOnFailureListener(e -> Log.e("ChatroomActivity", "Error creating chatroom", e));
                        }

                        loadChatMessages();
                    } else {
                        Log.e("ChatroomActivity", "Error retrieving chatrooms", task.getException());
                    }
                });
    }

    private void loadChatMessages() {
        if (chatroomId == null) return;

        CollectionReference messagesRef = firestore.collection("Chatrooms").document(chatroomId).collection("Messages");

        messagesRef.orderBy("timestampl").addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("ChatroomActivity", "Error fetching messages", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                chatMessages.clear();
                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    ChatMessageModel message = doc.toObject(ChatMessageModel.class);
                    chatMessages.add(message);
                }

                adpter.updateList(chatMessages);
                recyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }

    private void sendMessageToUser(String message, String currentUserId) {
        if (chatroomId == null) {
            Toast.makeText(this, "Chatroom ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, currentUserId, Timestamp.now());
        CollectionReference messagesRef = firestore.collection("Chatrooms").document(chatroomId).collection("Messages");

        messagesRef.add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        write_messages.setText("");  // Clear the input field
                        Log.d("ChatroomActivity", "Message sent successfully");

                        updateChatroomLastMessage(message, currentUserId, Timestamp.now());
                    } else {
                        Toast.makeText(ChatroomActivity.this, "Error sending message", Toast.LENGTH_SHORT).show();
                        Log.e("ChatroomActivity", "Error sending message", task.getException());
                    }
                });
    }

    private void updateChatroomLastMessage(String lastMessage, String senderId, Timestamp timestamp) {
        DocumentReference chatroomRef = firestore.collection("Chatrooms").document(chatroomId);

        chatroomRef.update(
                        "lastMessage", lastMessage,
                        "lastMessageSenderId", senderId,
                        "lastmessageTimestamp", timestamp
                ).addOnSuccessListener(aVoid -> Log.d("ChatroomActivity", "Chatroom updated with last message"))
                .addOnFailureListener(e -> Log.e("ChatroomActivity", "Error updating chatroom", e));
    }
}
