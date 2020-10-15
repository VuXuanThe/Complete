package com.example.gias.User.news;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gias.Object.Comment;
import com.example.gias.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.Viewhorder> {
    List<Comment> comments;
    Context context;

    public AdapterComment(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterComment.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        AdapterComment.Viewhorder viewhorder = new AdapterComment.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComment.Viewhorder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvName.setText(comment.getName());
        holder.tvComment.setText(comment.getComment());


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(comment.getTime()));
        String time = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.tvTime.setText(time);

        Glide.with(context).load(comment.getUidLinkAvatar()).into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvName, tvComment, tvTime;
        ImageView imgAvatar;
        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
