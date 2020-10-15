package com.example.gias.User.news;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.profile.FragmentUser;
import com.example.gias.databinding.FragmentNewsBinding;

import java.util.List;

public class NewsFragment extends Fragment implements ILoadDataNews {
    public final static String TAG = "NewsFragment";
    private FragmentNewsBinding binding;
    private FragmentNewsPresenter presenter;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false);
        presenter = new FragmentNewsPresenter(getContext(), this);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    public void getFragment(Fragment fragment, String nameFragment) {
        try {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.containerUser, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFragmentFullScreen(Fragment fragment, String nameFragment) {
        try {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.fullScreenActivityUser, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getDataStudent(final Student student) {
        if(getContext() != null)
            Glide.with(getContext()).load(student.getLinkAvatarStudent()).into(binding.imgAvatarUser);
        binding.tvAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Helper.STUDENT, student);
                getFragment(AddPostFragment.newInstance(bundle), AddPostFragment.TAG);
            }
        });
    }

    @Override
    public void getDataTeacher(final Teacher teacher) {
        if(getContext() != null)
            Glide.with(getContext()).load(teacher.getLinkAvatarTeacher()).into(binding.imgAvatarUser);
        binding.tvAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Helper.TEACHER, teacher);
                getFragment(AddPostFragment.newInstance(bundle), AddPostFragment.TAG);
            }
        });
    }

    @Override
    public void getListNews(List<Post> posts) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvListPost.setLayoutManager(layoutManager);
        AdapterListPost adapterListPost = new AdapterListPost(posts, getContext(), true);
        binding.rvListPost.setAdapter(adapterListPost);
        adapterListPost.setClickAvatarUserPost(new ClickAvatarUserPost() {
            @Override
            public void onClick(Bundle bundle) {
                getFragment(FragmentUser.newInstance(bundle), FragmentUser.TAG);
            }
        });
        adapterListPost.setClickComment(new ClickComment() {
            @Override
            public void onClick(Bundle bundle) {
                getFragmentFullScreen(CommentFragment.newInstance(bundle), CommentFragment.TAG);
            }
        });
    }
}