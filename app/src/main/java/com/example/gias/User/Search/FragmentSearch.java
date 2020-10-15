package com.example.gias.User.Search;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gias.FindStudent.ClickItemStudent;
import com.example.gias.FindStudent.FragmentFindStudent;
import com.example.gias.FindTeacher.ClickItemTeacher;
import com.example.gias.FindTeacher.FragmentFindTeacher;
import com.example.gias.Helper;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.profile.FragmentUser;
import com.example.gias.databinding.FragmentSearchBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.List;

public class FragmentSearch extends Fragment implements ILoadNewStudentAndTeacher {
    public static final String TAG = "FragmentSearch";
    private FragmentSearchBinding binding;
    private FragmentSearchPresenter presenter;
    private AdapterViewPagerNewStudent adapterViewPagerNewStudent;
    private AdapterViewPagerNewTeachers adapterViewPagerNewTeachers;
    private CompositePageTransformer transformer;
    private Handler slideHandler = new Handler();

    public static FragmentSearch newInstance(Bundle bundle){
        FragmentSearch fragment = new FragmentSearch();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
      inItView();
      transformer = new CompositePageTransformer();
      transformer.addTransformer(new MarginPageTransformer(8));
      transformer.addTransformer(new ViewPager2.PageTransformer() {
          @Override
          public void transformPage(@NonNull View page, float position) {
              float v = 1 - Math.abs(position);
              page.setScaleY(0.8f + v * 0.2f);
          }
      });
      presenter = new FragmentSearchPresenter(getContext(), this);
      return binding.getRoot();
    }

    private void inItView(){
        MobileAds.initialize(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        final Bundle bundle = getArguments();
        binding.cardviewFindTeacherMapAndList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(FragmentFindTeacher.newInstance(bundle), FragmentFindTeacher.TAG);
            }
        });
        binding.cardviewFindStudentMapAndList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragment(FragmentFindStudent.newInstance(bundle), FragmentFindStudent.TAG);
            }
        });
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

    private Runnable slideRunnableStudent = new Runnable() {
        @Override
        public void run() {
            binding.newStudent.setCurrentItem(binding.newStudent.getCurrentItem() + 1);
        }
    };

    private Runnable slideRunnableTeacher = new Runnable() {
        @Override
        public void run() {
            binding.newTeacher.setCurrentItem(binding.newTeacher.getCurrentItem() + 1);
        }
    };

    @Override
    public void loadNewStudents(List<Student> newStudents) {
        adapterViewPagerNewStudent = new AdapterViewPagerNewStudent(newStudents, getContext(), binding.newStudent);

        binding.newStudent.setClipToPadding(false);
        binding.newStudent.setClipChildren(false);
        binding.newStudent.setOffscreenPageLimit(3);
        binding.newStudent.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.newStudent.setAdapter(adapterViewPagerNewStudent);
        binding.newStudent.setPageTransformer(transformer);
        binding.newStudent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                slideHandler.removeCallbacks(slideRunnableStudent);
                slideHandler.postDelayed(slideRunnableStudent, 3000);
            }
        });
        adapterViewPagerNewStudent.setClickItemStudent(new ClickItemStudent() {
            @Override
            public void onClick(Student student) {
                Bundle args = new Bundle();
                args.putSerializable(Helper.STUDENT, student);
                args.putString("OBJECT", Helper.STUDENT);
                getFragment(FragmentUser.newInstance(args), FragmentUser.TAG);
            }
        });
    }

    @Override
    public void loadNewTeachers(List<Teacher> newTeachers) {
        adapterViewPagerNewTeachers = new AdapterViewPagerNewTeachers(newTeachers, getContext(), binding.newStudent);

        binding.newTeacher.setClipToPadding(false);
        binding.newTeacher.setClipChildren(false);
        binding.newTeacher.setOffscreenPageLimit(3);
        binding.newTeacher.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        binding.newTeacher.setAdapter(adapterViewPagerNewTeachers);
        binding.newTeacher.setPageTransformer(transformer);
        binding.newTeacher.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                slideHandler.removeCallbacks(slideRunnableTeacher);
                slideHandler.postDelayed(slideRunnableTeacher, 3000);
            }
        });
        adapterViewPagerNewTeachers.setClickItemTeacher(new ClickItemTeacher() {
            @Override
            public void onClick(Teacher teacher) {
                Bundle args = new Bundle();
                args.putSerializable(Helper.TEACHER, teacher);
                args.putString("OBJECT", Helper.TEACHER);
                getFragment(FragmentUser.newInstance(args), FragmentUser.TAG);
            }
        });
    }
}
