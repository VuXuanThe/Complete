package com.example.gias.User.Chats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gias.Object.Chat;
import com.example.gias.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> mChats;
    private String imageurl;
    FirebaseUser mUser;

    public MessageAdapter(Context context, List<Chat> mChats, String imageurl) {
        this.context = context;
        this.mChats = mChats;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position);
        holder.showMessage.setText(chat.getMessage());
        Glide.with(context).load(imageurl).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView showMessage;
        public ImageView profileImage;

        public ViewHolder(View itemView){
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            profileImage = itemView.findViewById(R.id.profileImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(mUser.getPhoneNumber())){
            return MSG_TYPE_RIGHT;
        }
        else return MSG_TYPE_LEFT;
    }
}

