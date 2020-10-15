package com.example.gias.FindTeacher;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gias.Helper;
import com.example.gias.Location.GPSLocation;
import com.example.gias.Location.MapFragment;
import com.example.gias.Main.Login.FragmentLogin;
import com.example.gias.Main.MainActivity;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.profile.FragmentUser;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentFindObjectBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static androidx.core.app.ActivityCompat.finishAffinity;

public class FragmentFindTeacher extends Fragment implements ILoadTeacher {
    public static final String TAG = "FragmentFindTeacher";
    private FragmentFindObjectBinding binding;
    private FindTeacherPresenter presenter;
    private String positionAddFragmentUser;
    private AndroidXMapFragment mapFragment;
    private Map map;

    public static FragmentFindTeacher newInstance(Bundle data) {
        FragmentFindTeacher fragment = new FragmentFindTeacher();
        fragment.setArguments(data);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_object, container, false);
        binding.btnNearTeacher.setBackground(getResources().getDrawable(R.drawable.header_background_profile));
        binding.btnListTeacher.setBackground(getResources().getDrawable(R.color.glass));
        //get activity to add fragment user
        Bundle data = getArguments();
        if(data != null) positionAddFragmentUser = data.getString("PositionAddFragmentUser");

        //Load teacher from firebase
        presenter = new FindTeacherPresenter(this, getContext());
        return binding.getRoot();
    }

    //in it List Teacher
    private void inItListTeacher(List<Teacher> teachers){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        binding.rvList.setLayoutManager(layoutManager);
        AdapterShowTeacher adapterShowTeacher = new AdapterShowTeacher(teachers, getContext());
        binding.rvList.setAdapter(adapterShowTeacher);
        clickItemTeacher(adapterShowTeacher);
    }
    private void clickItemTeacher(final AdapterShowTeacher adapterShowTeacher){
        adapterShowTeacher.setClickItemTeacher(new ClickItemTeacher() {
            @Override
            public void onClick(Teacher teacher) {
                Bundle args = new Bundle();
                args.putSerializable(Helper.TEACHER, teacher);
                args.putString("OBJECT", Helper.TEACHER);
                if(positionAddFragmentUser.equals(MainActivity.TAG))
                    getFragmentInMain(FragmentUser.newInstance(args), FragmentUser.TAG);
                else if(positionAddFragmentUser.equals(UserActivity.TAG))
                    getFragmentInUser(FragmentUser.newInstance(args), FragmentUser.TAG);
            }
        });
    }
    public void getFragmentInMain(Fragment fragment, String nameFragment) {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .add(R.id.container, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getFragment: " + e.getMessage());
        }
    }
    public void getFragmentInUser(Fragment fragment, String nameFragment) {
        try {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_fragment_right, R.anim.exit_fragment_right, R.anim.pop_enter_fragment, R.anim.pop_exit_fragment)
                    .add(R.id.containerUser, fragment)
                    .addToBackStack(nameFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getFragment: " + e.getMessage());
        }
    }

    //in it map teacher
    private void inItMapTeacher(final List<Teacher> teachers){
        String diskCacheRoot = getActivity().getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot);
        if(!success)
            Toast.makeText(getActivity().getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_SHORT).show();
        else{
            mapFragment = new AndroidXMapFragment();
            getFragmentManager().beginTransaction().add(R.id.mapfragment, mapFragment, "MAP_TAG").commit();
            mapFragment.init(new ApplicationContext(getContext()), new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(
                        final OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE){
                        map = mapFragment.getMap();
                        map.setZoomLevel(13);
                        map.setTilt((map.getMinTilt() + map.getMaxTilt())/2);
                        map.setProjectionMode(Map.Projection.MERCATOR);
                        Image marker_img_current_position = new Image();
                        try {
                            marker_img_current_position.setImageResource(R.drawable.ic_current_position);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        map.getPositionIndicator().setMarker(marker_img_current_position);
                        map.getPositionIndicator().setVisible(true);
                        GPSLocation location = new GPSLocation(getContext());
                        GeoCoordinate currentPosition =  new GeoCoordinate(location.getLatitude(), location.getLongitude());
                        map.setCenter(currentPosition, Map.Animation.NONE);
                        clickMarkerTeacher(teachers);
                        clickButtonCurrentPosition();
                        createMarker(teachers);
                    }
                    else{
                        System.out.println("ERROR: Cannot initialize Map Fragment");
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
    private void clickMarkerTeacher(final List < Teacher > teachers){
        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
            @Override
            public void onPanStart() {}
            @Override
            public void onPanEnd() {}
            @Override
            public void onMultiFingerManipulationStart() {}
            @Override
            public void onMultiFingerManipulationEnd() {}
            @Override
            public boolean onMapObjectsSelected(@NonNull List<ViewObject> list) {
                for (ViewObject viewObject : list) {
                    if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        MapObject mapObject = (MapObject) viewObject;
                        if (mapObject.getType() == MapObject.Type.MARKER) {
                            MapMarker marker = (MapMarker) mapObject;
                            if(marker != null){
                                setBottomSheetTeacher(teachers.get(Integer.valueOf(marker.getTitle())));
                            }
                            return false;
                        }
                    }
                }
                return false;
            }
            @Override
            public boolean onTapEvent(@NonNull PointF pointF) {return false; }
            @Override
            public boolean onDoubleTapEvent(@NonNull PointF pointF) {
                return false;
            }
            @Override
            public void onPinchLocked() {}
            @Override
            public boolean onPinchZoomEvent(float v, @NonNull PointF pointF) {
                return false;
            }
            @Override
            public void onRotateLocked() {}
            @Override
            public boolean onRotateEvent(float v) {
                return false;
            }
            @Override
            public boolean onTiltEvent(float v) {
                return false;
            }
            @Override
            public boolean onLongPressEvent(@NonNull PointF pointF) {
                return false;
            }
            @Override
            public void onLongPressRelease() { }
            @Override
            public boolean onTwoFingerTapEvent(@NonNull PointF pointF) {
                return false;
            }
        }, 0, false);
    }
    private void clickButtonCurrentPosition(){
        binding.btnCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSLocation location = new GPSLocation(getContext());
                map.setCenter(new GeoCoordinate(location.getLatitude(), location.getLongitude()), Map.Animation.BOW);
            }
        });
    }
    private void createMarker(List<Teacher> teachers){
        int i = 0;
        for (final Teacher teacher: teachers) {
            Image marker_img = new Image();
            marker_img.setBitmap(new Helper(getContext()).createAvatarUser(teacher.getAvatarTeacher()));
            MapMarker marker = new MapMarker(new GeoCoordinate(teacher.getLatitude(), teacher.getLongitude()), marker_img);
            marker.setDraggable(true);
            marker.setTitle(String.valueOf(i)); i++;
            map.addMapObject(marker);
        }
        binding.Load.setVisibility(View.GONE);
    }
    public void setBottomSheetTeacher(final Teacher teacher){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet_teacher,
                        (LinearLayout)getActivity().findViewById(R.id.bottomSheetContainerTeacher));
        ImageView avatar = bottomSheetView.findViewById(R.id.bsimgAcountTeacher);
        avatar.setImageBitmap(teacher.getAvatarTeacher());
        TextView teacherName = bottomSheetView.findViewById(R.id.bstvAcount_name);
        teacherName.setText(teacher.getTeacherName());
        TextView experient = bottomSheetView.findViewById(R.id.bstvExperient);
        experient.setText(teacher.getExperient());
        TextView subject = bottomSheetView.findViewById(R.id.bstvSubject);
        subject.setText(teacher.getSubject());
        TextView gender = bottomSheetView.findViewById(R.id.bstvGender);
        gender.setText(teacher.getGender());
        TextView age = bottomSheetView.findViewById(R.id.bstvAge);
        age.setText(String.valueOf(teacher.getAge()));
        TextView tvTitle = bottomSheetView.findViewById(R.id.tvTitle);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        TextView detail = bottomSheetView.findViewById(R.id.tvDetail);
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable(Helper.TEACHER, teacher);
                args.putString("OBJECT", Helper.TEACHER);
                if(positionAddFragmentUser.equals(MainActivity.TAG))
                    getFragmentInMain(FragmentUser.newInstance(args), FragmentUser.TAG);
                else if(positionAddFragmentUser.equals(UserActivity.TAG))
                    getFragmentInUser(FragmentUser.newInstance(args), FragmentUser.TAG);
                bottomSheetDialog.dismiss();
            }
        });

        TextView direction = bottomSheetView.findViewById(R.id.tvDirection);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(getContext(), "Bạn phải đăng nhập hoặc đăng kí để sử dụng tính năng này!", Toast.LENGTH_SHORT).show();
                    getFragmentInMain(FragmentLogin.newInstance(), FragmentLogin.TAG);
                    bottomSheetDialog.dismiss();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Helper.TEACHER, teacher);
                    getFragmentInUser(MapFragment.newInstance(bundle), MapFragment.TAG);
                    bottomSheetDialog.dismiss();
                }

            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    //control button
    private void clickControlButton(){
        binding.btnNearTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mapfragment.setVisibility(View.VISIBLE);
                binding.mapfragment.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                binding.rvList.setVisibility(View.INVISIBLE);
                binding.btnCurrentPosition.setVisibility(View.VISIBLE);
                binding.btnNearTeacher.setBackground(getResources().getDrawable(R.drawable.header_background_profile));
                binding.btnListTeacher.setBackground(getResources().getDrawable(R.color.glass));
            }
        });
        binding.btnListTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.mapfragment.setVisibility(View.INVISIBLE);
                binding.rvList.setVisibility(View.VISIBLE);
                binding.rvList.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                binding.btnCurrentPosition.setVisibility(View.GONE);
                binding.btnListTeacher.setBackground(getResources().getDrawable(R.drawable.header_background_profile));
                binding.btnNearTeacher.setBackground(getResources().getDrawable(R.color.glass));
            }
        });
    }

    @Override
    public void loadSuccess(List<Teacher> teachers) {
        inItMapTeacher(teachers);
        inItListTeacher(teachers);
        clickControlButton();
    }

    @Override
    public void loadFail(String mess) {

    }
}
