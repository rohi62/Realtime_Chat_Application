package com.example.samefor.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import com.example.samefor.R;
import com.example.samefor.model.ChatMessageModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.List;

public class ChatAdpter extends RecyclerView.Adapter<ChatAdpter.ChatModelViewHolder> {

    private List<ChatMessageModel> userList;
    private Context context;

    public ChatAdpter(List<ChatMessageModel> userList, Context context) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_recycler_view, parent, false);
        return new ChatModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {
        ChatMessageModel model = userList.get(position);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        if (account != null && model.getSender().equals(account.getId())) {

            holder.left_chat_layout.setVisibility(View.GONE);
            holder.right_chat_layout.setVisibility(View.VISIBLE);
            holder.right_text.setText(model.getMessgae());
        } else {
            // Show the message on the left side for other users
            holder.right_chat_layout.setVisibility(View.GONE);
            holder.left_chat_layout.setVisibility(View.VISIBLE);
            holder.left_text.setText(model.getMessgae());
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout left_chat_layout, right_chat_layout;
        TextView left_text, right_text;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            left_chat_layout = itemView.findViewById(R.id.left_chat_layout);
            right_chat_layout = itemView.findViewById(R.id.right_chat_layout);
            left_text = itemView.findViewById(R.id.left_text);
            right_text = itemView.findViewById(R.id.right_text);
        }
    }

    public void updateList(List<ChatMessageModel> newList) {
        userList = newList;
        notifyDataSetChanged();
    }
}
