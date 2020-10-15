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
import com.example.gias.User.Chats.Interface.ClickItemChat;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
import com.example.gias.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterListChat extends RecyclerView.Adapter<AdapterListChat.Viewhorder> {
    List<UserChat> userChats;
    ClickItemChat clickItemChat;
    Context context;
    DatabaseReference mData;

    public AdapterListChat(List<UserChat> userChats, Context context) {
        this.userChats = userChats;
        this.context = context;
        mData = FirebaseDatabase.getInstance().getReference();
    }

    public void setClickItemUserChat(ClickItemChat clickItemChat){
        this.clickItemChat = clickItemChat;
    }

    @NonNull
    @Override
    public Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user_chat, parent, false);
        AdapterListChat.Viewhorder viewhorder = new AdapterListChat.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewhorder holder, int position) {
        final UserChat userChat = userChats.get(position);
        holder.tvUserName.setText(userChat.getUserName());
        holder.tvMessage.setText(userChat.getMes());
        mData.child(userChat.getObject()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(userChat.getObject().equals(Helper.STUDENT)){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if(student.getPhoneNumber().equals(userChat.getPhoneNumber())){
                            if(student.getStatus().equals("online"))
                                holder.online.setVisibility(View.VISIBLE);
                            else
                                holder.online.setVisibility(View.GONE);
                        }

                    }
                }
                else {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if(teacher.getPhoneNumber().equals(userChat.getPhoneNumber())){
                            if(teacher.getStatus().equals("online"))
                                holder.online.setVisibility(View.VISIBLE);
                            else
                                holder.online.setVisibility(View.GONE);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Glide.with(context).load(userChat.getLinkAvatar()).into(holder.imgAvatarUser);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemChat.onClick(userChat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userChats.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvMessage;
        ImageView imgAvatarUser, online;
        View view_item;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMessage = itemView.findViewById(R.id.message);
            imgAvatarUser = itemView.findViewById(R.id.imgAvatarUser);
            online = itemView.findViewById(R.id.online);
            view_item = itemView.findViewById(R.id.view_item);
        }
    }
}
