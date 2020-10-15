package com.example.gias.User.Manager;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gias.FindStudent.AdapterShowStudent;
import com.example.gias.FindStudent.ClickItemStudent;
import com.example.gias.Object.Notice;
import com.example.gias.Object.Student;
import com.example.gias.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterItemNotice extends RecyclerView.Adapter<AdapterItemNotice.Viewhorder> {
    Context context;
    List<Notice> notices;

    public AdapterItemNotice(Context context, List<Notice> notices) {
        this.context = context;
        this.notices = notices;
    }

    @NonNull
    @Override
    public AdapterItemNotice.Viewhorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notice, parent, false);
        AdapterItemNotice.Viewhorder viewhorder = new AdapterItemNotice.Viewhorder(view);
        return viewhorder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItemNotice.Viewhorder holder, int position) {
        Notice notice = notices.get(position);
        if(position % 2 == 0){
            holder.view_item.setBackgroundColor(context.getResources().getColor(R.color.colorBackground));
        }
        holder.tvTitle.setText(notice.getTitle());
        holder.tvMess.setText(notice.getMes());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(notice.getTime()));
        String time = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.tvTime.setText(time);

    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public class Viewhorder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMess, tvTime;
        LinearLayout view_item;

        public Viewhorder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMess = itemView.findViewById(R.id.tvMess);
            view_item = itemView.findViewById(R.id.view_item);
        }
    }
}

