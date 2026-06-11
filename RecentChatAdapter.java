package com.example.samefor.adpter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;




import com.example.samefor.ChatroomActivity;
import com.example.samefor.R;
import com.example.samefor.model.ChatroomModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {

    private List<ChatroomModel> chatrooms;
    private Context context;

    public RecentChatAdapter(List<ChatroomModel> chatrooms, Context context) {
        this.chatrooms = chatrooms;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatroomModel chatroom = chatrooms.get(position);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            String currentUserId = account.getId();

            int otherUserIndex = chatroom.getUserIds().get(0).equals(currentUserId) ? 1 : 0;

            holder.username.setText(chatroom.getUsernames().get(otherUserIndex));

            String lastMessage = chatroom.getLastMessage();
            String lastMessageSenderId = chatroom.getLastMessageSenderId();

            int wordCount = countWords(lastMessage);


            if (lastMessageSenderId.equals(currentUserId)) {
                if (wordCount > 15) {
                    holder.recentMessage.setText("Sent message");
                } else {
                    holder.recentMessage.setText("You: " + lastMessage);
                }
            } else {
                if (wordCount > 15) {
                    holder.recentMessage.setText("Received message");
                } else {
                    holder.recentMessage.setText(lastMessage);
                }
            }

            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("HH:mm");
            }
            LocalDateTime localDateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localDateTime = LocalDateTime.ofInstant(chatroom.getLastmessageTimestamp().toInstant(), ZoneId.systemDefault());
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.time.setText(localDateTime.format(formatter));
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatroomActivity.class);

                intent.putExtra("CURRENT_USER_NAME", chatroom.getUsernames().get(otherUserIndex == 0 ? 1 : 0));
                intent.putExtra("OTHER_USERNAME", chatroom.getUsernames().get(otherUserIndex));

                intent.putExtra("CURRENT_USER_ID", currentUserId);
                intent.putExtra("OTHER_USER_ID", chatroom.getUserIds().get(otherUserIndex));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

        } else {
            Toast.makeText(context, "User not logged in. Please log in.", Toast.LENGTH_SHORT).show();
        }
    }


    private int countWords(String message) {
        if (message == null || message.trim().isEmpty()) {
            return 0;
        }
        return message.trim().split("\\s+").length;
    }

    @Override
    public int getItemCount() {
        return chatrooms.size();
    }

    public void updateChatrooms(List<ChatroomModel> newChatrooms) {
        this.chatrooms = newChatrooms;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, recentMessage, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            recentMessage = itemView.findViewById(R.id.new_message);
            time = itemView.findViewById(R.id.time);
        }
    }
}
