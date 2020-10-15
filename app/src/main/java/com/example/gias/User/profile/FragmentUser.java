package com.example.gias.User.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gias.Helper;
import com.example.gias.Main.Login.FragmentLogin;
import com.example.gias.Main.MainActivity;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.Object.UserChat;
import com.example.gias.R;
import com.example.gias.User.Chats.MesengerActivity;
import com.example.gias.User.UserActivity;
import com.example.gias.User.news.AdapterListPost;
import com.example.gias.User.news.AddPostFragment;
import com.example.gias.databinding.FragmentUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import java.io.File;
import java.util.List;

import static androidx.core.app.ActivityCompat.finishAffinity;

public class FragmentUser extends Fragment implements IProfileUser {
    public static String TAG = "FragmentUser";
    private AndroidXMapFragment mapFragment;
    private Map map;
    private FragmentUserBinding binding;
    private UserPresenter presenter;
    private int showInformation = 0;
    private int showPosition = 0;

    private UserChat userChatSender;

    public static FragmentUser newInstance(Bundle bundle){
        FragmentUser fragment = new FragmentUser();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        presenter = new UserPresenter(getContext(), this);
        Bundle data = getArguments();
        if(data != null){
            String object = data.getString("OBJECT");
            if(object.equals(Helper.TEACHER)) {
                Teacher teacher = (Teacher) data.getSerializable("Teacher");
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(teacher.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        binding.btnMore.setVisibility(View.VISIBLE);
                        binding.btnContact.setVisibility(View.GONE);
                        binding.viewAddPost.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.btnMore.setVisibility(View.GONE);
                        binding.btnContact.setVisibility(View.VISIBLE);
                        binding.viewAddPost.setVisibility(View.GONE);
                    }
                    presenter.getPost(teacher.getPhoneNumber());
                }
                else {
                    binding.btnMore.setVisibility(View.GONE);
                    binding.btnContact.setVisibility(View.VISIBLE);
                    binding.viewAddPost.setVisibility(View.GONE);
                }
                setViewTeacher(teacher);
                contact(teacher, null);
            }
            else {
                Student student = (Student) data.getSerializable("Student");
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    if(student.getPhoneNumber().equals(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        binding.btnMore.setVisibility(View.VISIBLE);
                        binding.btnContact.setVisibility(View.GONE);
                        binding.viewAddPost.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.btnMore.setVisibility(View.GONE);
                        binding.btnContact.setVisibility(View.VISIBLE);
                        binding.viewAddPost.setVisibility(View.GONE);
                    }
                    presenter.getPost(student.getPhoneNumber());
                }
                else {
                    binding.btnMore.setVisibility(View.GONE);
                    binding.btnContact.setVisibility(View.VISIBLE);
                    binding.viewAddPost.setVisibility(View.GONE);
                }
                setViewStudent(student);
                contact(null, student);
            }
        }
        else{
            binding.btnMore.setVisibility(View.VISIBLE);
            presenter.getData(UserActivity.OBJECT);
            presenter.getPost(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        signoutAndCorrection();
        return binding.getRoot();
    }

    private void setImageApp(){
        Glide.with(getContext()).load(Helper.IMG_REPAIR).into(binding.imgRepair);
        Glide.with(getContext()).load(Helper.IMG_CONNECT).into(binding.imgConnectFriend);
        Glide.with(getContext()).load(Helper.IMG_SECURITY).into(binding.imgSecurity);
        Glide.with(getContext()).load(Helper.IMG_INVITE).into(binding.imgInvite);
        Glide.with(getContext()).load(Helper.IMG_CHANGE_LANGUAGE).into(binding.imgChangeLanguage);
        Glide.with(getContext()).load(Helper.IMG_SIGNOUT).into(binding.imgSignout);
        Glide.with(getContext()).load(Helper.IMG_POSITION_AD).into(binding.imgPositionAd);
        Glide.with(getContext()).load(Helper.IMG_FEEDBACK).into(binding.imgFeedback);
        Glide.with(getContext()).load(Helper.IMG_CONTACT_AD).into(binding.imgContactAd);
        Glide.with(getContext()).load(Helper.IMG_QA_AD).into(binding.imgQA);
    }

    private void setViewTeacher(final Teacher teacher){
        if(teacher.getStatus().equals("online")) binding.online.setVisibility(View.VISIBLE);
        else binding.online.setVisibility(View.GONE);
        binding.tvUserName.setText(teacher.getTeacherName());
        if(getContext() != null) {
            binding.tvAdress.setText(Helper.Adress(getContext(), teacher.getLongitude(), teacher.getLatitude()));
            Glide.with(getContext()).asBitmap().load(teacher.getLinkAvatarTeacher()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    binding.imageProfile.setImageBitmap(resource);
                    binding.imgAvatarUser.setImageBitmap(resource);
                    inItMapPosition(teacher.getLongitude(), teacher.getLatitude(), resource);
                    binding.Load.setVisibility(View.GONE);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
        binding.tvPhoneNumber.setText(teacher.getPhoneNumber().substring(0, 9) + "***");
        binding.tvGender.setText(teacher.getGender());
        binding.tvAge.setText(teacher.getAge());
        binding.tvObject.setText(Helper.TEACHER_TV);
        binding.tvEmail.setText(teacher.getEmail());
        binding.tvSubjectTeacher.setText(teacher.getSubject());
        binding.tvFreeTimeTeacher.setText(teacher.getFreeTime());
        binding.tvExperient.setText(teacher.getExperient());
        binding.tvTuitionTeacher.setText(teacher.getTuition());
        binding.tvVehicle.setText(teacher.getVehicle());
        binding.tvProcessWork.setText(teacher.getProcessWork());
        binding.tvInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showInformation == 0){
                    binding.detailsTeacher.setVisibility(View.VISIBLE);
                    binding.detailsTeacher.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    binding.tvInformation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gone, 0);
                    showInformation = 1;
                }
                else {
                    binding.detailsTeacher.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.detailsTeacher.setVisibility(View.GONE);
                            binding.tvInformation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_display, 0);
                        }
                    }, 210);

                    showInformation = 0;
                }

            }
        });
        binding.tvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPosition == 0){
                    binding.containerMap.setVisibility(View.VISIBLE);
                    binding.tvPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gone, 0);
                    binding.containerMap.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    showPosition = 1;
                }
                else{
                    binding.containerMap.setVisibility(View.GONE);
                    binding.tvPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_display, 0);
                    showPosition = 0;
                }
            }
        });
        binding.tvAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Helper.TEACHER, teacher);
                getFragment(AddPostFragment.newInstance(bundle), AddPostFragment.TAG);
            }
        });
    }

    private void setViewStudent(final Student student){
        if(student.getStatus().equals("online")) binding.online.setVisibility(View.VISIBLE);
        else binding.online.setVisibility(View.GONE);
        binding.tvUserName.setText(student.getStudentName());
        if(getContext() != null) {
            binding.tvAdress.setText(Helper.Adress(getContext(), student.getLongitude(), student.getLatitude()));
            Glide.with(getContext()).asBitmap().load(student.getLinkAvatarStudent()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    binding.imageProfile.setImageBitmap(resource);
                    binding.imgAvatarUser.setImageBitmap(resource);
                    inItMapPosition(student.getLongitude(), student.getLatitude(), resource);
                    binding.Load.setVisibility(View.GONE);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {}
            });
        }
        binding.tvPhoneNumber.setText(student.getPhoneNumber().substring(0, 9) + "***");
        binding.tvGender.setText(student.getGender());
        binding.tvAge.setText(student.getAge());
        binding.tvObject.setText(Helper.STUDENT_TV);
        binding.tvSubjectStudent.setText(student.getSubject());
        binding.tvFreeTimeStudent.setText(student.getFreeTime());
        binding.tvTuitionStudent.setText(student.getTuition());
        binding.tvStudy.setText(student.getStudy());
        binding.tvInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showInformation == 0){
                    binding.detailsStudent.setVisibility(View.VISIBLE);
                    binding.tvInformation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gone, 0);
                    binding.detailsStudent.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    showInformation = 1;
                }
                else{
                    binding.detailsStudent.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.detailsStudent.setVisibility(View.GONE);
                            binding.tvInformation.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_display, 0);
                        }
                    }, 210);
                    showInformation = 0;
                }

            }
        });
        binding.tvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPosition == 0){
                    binding.containerMap.setVisibility(View.VISIBLE);
                    binding.tvPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_gone, 0);
                    binding.containerMap.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    showPosition = 1;
                }
                else{
                    binding.containerMap.setVisibility(View.GONE);
                    binding.tvPosition.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_display, 0);
                    showPosition = 0;
                }
            }
        });
        binding.tvAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Helper.STUDENT, student);
                getFragment(AddPostFragment.newInstance(bundle), AddPostFragment.TAG);
            }
        });
    }

    private void inItMapPosition(final double longitude, final double latitude, final Bitmap bitmap) {
        String diskCacheRoot = getActivity().getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot);
        if(!success)
            Toast.makeText(getActivity().getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_SHORT).show();
        else{
            mapFragment = new AndroidXMapFragment();
            getFragmentManager().beginTransaction()
                    .replace(R.id.mapUser, mapFragment, "MAP_TAG")
                    .commit();
            mapFragment.init(new ApplicationContext(getContext()), new OnEngineInitListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEngineInitializationCompleted(
                        final OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE){
                        map = mapFragment.getMap();
                        map.setZoomLevel(16);
                        map.setTilt((map.getMinTilt() + map.getMaxTilt())/2);
                        Image image = new Image();
                        image.setBitmap(new Helper(getContext()).createAvatarUser(bitmap));
                        MapMarker markerUser = new MapMarker(new GeoCoordinate(latitude, longitude), image);
                        markerUser.setDraggable(true);
                        map.addMapObject(markerUser);
                        map.setCenter(new GeoCoordinate(latitude, longitude), Map.Animation.NONE);
                    }
                    else{
                        new AlertDialog.Builder(getActivity()).setMessage(
                                "Error : " + error.name() + "\n\n" + error.getDetails())
                                .setTitle(R.string.engine_init_error)
                                .setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                finishAffinity(getActivity());
                                            }
                                        }).create().show();
                    }
                }
            });
        }
    }

    private void signoutAndCorrection(){
        setImageApp();
        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.log_out);
                builder.setPositiveButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
                builder.setMessage(getResources().getString(R.string.Mes_logout));
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutMainMenu.setVisibility(View.VISIBLE);
                binding.layoutMainMenu.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
            }
        });

        binding.btnHideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.layoutMainMenu.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
                binding.layoutMainMenu.setVisibility(View.GONE);
            }
        });
    }

    private void contact(final Teacher teacher, final Student student){
        binding.btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getResources().getString(R.string.Dialog_Title));
                builder.setMessage(getResources().getString(R.string.Dialog_Mesage));
                builder.setNegativeButton(getResources().getString(R.string.Dialog_Title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(FirebaseAuth.getInstance().getCurrentUser() == null){
                            Toast.makeText(getContext(), "Bạn phải đăng kí hoặc đăng nhập tài khoản để liên hệ!", Toast.LENGTH_SHORT).show();
                            getFragmentInMain(FragmentLogin.newInstance(), FragmentLogin.TAG);
                        }
                        else{
                            UserChat userChatReceiver;
                            if(teacher != null)
                                userChatReceiver = new UserChat(teacher.getPhoneNumber(), teacher.getTeacherName(),
                                        teacher.getLinkAvatarTeacher(), null, teacher.getObject(), null);
                            else
                                userChatReceiver = new UserChat(student.getPhoneNumber(), student.getStudentName(),
                                        student.getLinkAvatarStudent(), null, student.getObject(), null);
                            Intent intent = new Intent(getContext(), MesengerActivity.class);
                            intent.putExtra("UserChatReceiver", userChatReceiver);
                            intent.putExtra("UserChatSender", userChatSender);
                            startActivity(intent);
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void getFragmentInMain(Fragment fragment, String nameFragment) {
        try {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .replace(R.id.container, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void getDataTeacher(final Teacher teacher) {
        setViewTeacher(teacher);
    }

    @Override
    public void getDataStudent(final Student student) {
        setViewStudent(student);
    }

    @Override
    public void getPostsUser(List<Post> posts) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvListPost.setLayoutManager(layoutManager);
        AdapterListPost adapterListPost = new AdapterListPost(posts, getContext(), false);
        binding.rvListPost.setAdapter(adapterListPost);
    }

    @Override
    public void getUserChatSender(UserChat userChat) {
        userChatSender = userChat;
    }
}