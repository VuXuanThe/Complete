package com.example.gias.User.news;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gias.FindStudent.AdapterShowStudent;
import com.example.gias.Helper;
import com.example.gias.Notifications.Data;
import com.example.gias.Object.Comment;
import com.example.gias.Object.Post;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentCommentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommentFragment extends Fragment {
    public final static String TAG = "CommentFragment";
    private FragmentCommentBinding binding;
    private String uidPost;
    private int numComment;
    private List<Comment> comments;

    public static CommentFragment newInstance(Bundle bundle) {
        CommentFragment fragment = new CommentFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        comments = new ArrayList<>();
        Bundle data = getArguments();
        if (data != null) {
            uidPost = data.getString("uidPost");
            binding.tvOK.setText(Helper.setUnderlineString("Có " + data.getString("numLike") + " đã ok"));
            numComment = Integer.parseInt(data.getString("numberComment"));
            addComment();
            getComment();
        }
        return binding.getRoot();
    }

    private void addComment(){
        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = binding.edtComment.getText().toString();

                if(comment.isEmpty()){
                    Toast.makeText(getContext(), "Bạn chưa nhập ý kiến!", Toast.LENGTH_SHORT).show();
                }
                else{
                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference(UserActivity.OBJECT);
                    mData.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name, linkAvatar;
                                    if(UserActivity.OBJECT.equals(Helper.TEACHER)){
                                        name = (String) snapshot.child("teacherName").getValue();
                                        linkAvatar = (String) snapshot.child("linkAvatarTeacher").getValue();
                                    }
                                    else {
                                        name = (String) snapshot.child("studentName").getValue();
                                        linkAvatar = (String) snapshot.child("linkAvatarStudent").getValue();
                                    }

                                    Comment comment1 = new Comment(linkAvatar, name, String.valueOf(System.currentTimeMillis()), comment);
                                    DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Posts").child(uidPost);
                                    mData.child("comments").push().setValue(comment1);
                                    numComment++;
                                    mData.child("numberComment").setValue(String.valueOf(numComment));
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}});
                }
                Helper.hideKeyBoard(getActivity());
                binding.edtComment.setText("");
            }
        });
    }

    private void getComment(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvListComment.setLayoutManager(layoutManager);

        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Posts").child(uidPost);
        mData.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    comments.add(dataSnapshot.getValue(Comment.class));
                }
                handlerSortComment();
                AdapterComment adapterComment = new AdapterComment(comments, getContext());
                binding.rvListComment.setAdapter(adapterComment);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void handlerSortComment(){
        Collections.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
    }
}