package com.example.gias.User.news;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterListPost extends RecyclerView.Adapter<AdapterListPost.Viewhorder>{
    List<Post> posts;
    Context context;
    boolean clickAvatarUser;
    ClickAvatarUserPost clickAvatarUserPost;
    ClickComment clickComment;

    public AdapterListPost(List<Post> posts, Context context, boolean clickAvatarUser) {
        this.posts = posts;
        this.context = context;
        this.clickAvatarUser = clickAvatarUser;
    }

    public void setClickAvatarUserPost(ClickAvatarUserPost clickAvatarUserPost){
        this.clickAvatarUserPost = clickAvatarUserPost;
    }

    public void setClickComment(ClickComment clickComment){
        this.clickComment = clickComment;
    }

    @NonNull
    @Override
    public AdapterListPost.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_post, parent, false);
        AdapterListPost.Viewhorder viewhorder = new AdapterListPost.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterListPost.Viewhorder holder, int position) {
        final Post post = posts.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvDescription.setText(post.getDescription());
        if(post.getObject().equals(Helper.TEACHER)) holder.tvObject.setText(Helper.TEACHER_TV);
        else holder.tvObject.setText(Helper.STUDENT_TV);

        if(context != null){
            Glide.with(context).load(post.getLinkAvatarPoster()).into(holder.imgAvatarUser);
        }

        if(post.getLinkImageOne() != null){
            Glide.with(context).load(post.getLinkImageOne()).into(holder.imgPost);
        }

        if(post.getLinkImageTwo() != null || post.getLinkImageThree() != null || post.getLinkImageFour() != null){
            holder.btnNextImage.setVisibility(View.VISIBLE);
        }

        if(post.getFeel() != null){
            if(post.getFeel().equals("Happy"))
                holder.tvFeeling.setText("Đang cảm thấy vui vẻ");
            if(post.getFeel().equals("Sad"))
                holder.tvFeeling.setText("Đang cảm thấy buồn");
            if(post.getFeel().equals("Question"))
                holder.tvFeeling.setText("Đang cảm thấy thắc mắc");
            if(post.getFeel().equals("Cute"))
                holder.tvFeeling.setText("Đang cảm thấy đáng yêu");
            if(post.getFeel().equals("Angry"))
                holder.tvFeeling.setText("Đang cảm thấy tức giận");
            holder.tvFeeling.setVisibility(View.VISIBLE);
        }

        //set Date post
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(post.getTimePost()));
        String time = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.tvTime.setText(time);

        //setUserNamePost
        holder.tvUserName.setText(post.getNamePoster());

        //check number ok
        if(!post.getNumberLike().equals("0")){
            holder.tvNumberLike.setText(post.getNumberLike() + " người đã ok");
            holder.tvNumberLike.setVisibility(View.VISIBLE);
        }

        //check ok
        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Posts");
        mData.child("post" + post.getNumberPhonePoster() + post.getTimePost()).child("UserOkPosts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        holder.btnOk.setBackground(context.getResources().getDrawable(R.drawable.ok));
                        holder.tvLike.setText("đã ok");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});

        holder.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.tvLike.getText().equals("đã ok")){
                    DatabaseReference mRemoveUserOkPost = FirebaseDatabase.getInstance().getReference("Posts");
                    mRemoveUserOkPost.child("post" + post.getNumberPhonePoster() + post.getTimePost()).child("UserOkPosts")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                    DatabaseReference mOk = FirebaseDatabase.getInstance().getReference("Posts");
                    mOk.child("post" + post.getNumberPhonePoster() + post.getTimePost()).child("numberLike").setValue(String.valueOf(Integer.parseInt(post.getNumberLike()) - 1));
                    holder.tvLike.setText("ok");
                    holder.btnOk.setBackground(context.getResources().getDrawable(R.drawable.defaut_ok));
                    holder.tvNumberLike.setText((Integer.parseInt(post.getNumberLike()) - 1) + " người đã ok");
                    post.setNumberLike(String.valueOf(Integer.parseInt(post.getNumberLike()) - 1));
                    if(String.valueOf(Integer.parseInt(post.getNumberLike())).equals("0"))
                        holder.tvNumberLike.setVisibility(View.GONE);
                }
                else{
                    DatabaseReference mRemoveUserOkPost = FirebaseDatabase.getInstance().getReference("Posts");
                    mRemoveUserOkPost.child("post" + post.getNumberPhonePoster() + post.getTimePost()).child("UserOkPosts")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Liked");

                    DatabaseReference mOk = FirebaseDatabase.getInstance().getReference("Posts");
                    mOk.child("post" + post.getNumberPhonePoster() + post.getTimePost()).child("numberLike").setValue(String.valueOf(Integer.parseInt(post.getNumberLike()) + 1));
                    holder.tvLike.setText("đã ok");
                    holder.btnOk.setBackground(context.getResources().getDrawable(R.drawable.ok));
                    holder.tvNumberLike.setText((Integer.parseInt(post.getNumberLike()) + 1) + " người đã ok");
                    post.setNumberLike(String.valueOf(Integer.parseInt(post.getNumberLike()) + 1));
                    holder.tvNumberLike.setVisibility(View.VISIBLE);
                }
            }
        });

        //clickAvatarImage
        if(clickAvatarUser){
            holder.imgAvatarUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference(post.getObject());
                    mData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Bundle data = new Bundle();
                            data.putString("OBJECT", post.getObject());
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                if(post.getObject().equals(Helper.TEACHER)){
                                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                    if(post.getNumberPhonePoster().equals(teacher.getPhoneNumber())){
                                        data.putSerializable(Helper.TEACHER, teacher);
                                        clickAvatarUserPost.onClick(data);
                                        break;
                                    }
                                }
                                else{
                                    Student student = dataSnapshot.getValue(Student.class);
                                    if(post.getNumberPhonePoster().equals(student.getPhoneNumber())){
                                        data.putSerializable(Helper.STUDENT, dataSnapshot.getValue(Student.class));
                                        clickAvatarUserPost.onClick(data);
                                        break;
                                    }

                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});
                }
            });
        }

        holder.btnIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("numberComment", post.getNumberComment());
                bundle.putString("uidPost", post.getUidPost());
                bundle.putString("numLike", post.getNumberLike());
                clickComment.onClick(bundle);
            }
        });

        holder.tvNumComment.setText("Có " + post.getNumberComment() + " ý kiến");
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvTime, tvUserName, tvTitle, tvDescription, tvFeeling, tvObject, tvLike, tvNumberLike, tvNumComment;

        ImageView imgAvatarUser, imgPost;

        ImageButton btnOk, btnIdea, btnShare;

        Button btnNextImage;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvFeeling = itemView.findViewById(R.id.tvFeeling);
            tvObject = itemView.findViewById(R.id.tvObject);
            imgAvatarUser = itemView.findViewById(R.id.imgAvatarUser);
            imgPost = itemView.findViewById(R.id.imgPost);
            btnOk = itemView.findViewById(R.id.btnOk);
            btnIdea = itemView.findViewById(R.id.btnIdea);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnNextImage = itemView.findViewById(R.id.btnNextImage);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvNumberLike = itemView.findViewById(R.id.tvNumberLike);
            tvNumComment = itemView.findViewById(R.id.tvNumberComment);
    }
    }
}
