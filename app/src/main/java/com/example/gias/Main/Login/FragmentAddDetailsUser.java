package com.example.gias.Main.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.gias.Helper;
import com.example.gias.Location.GPSLocation;
import com.example.gias.Main.MainActivity;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentAddDetailsUserBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static androidx.core.app.ActivityCompat.finishAffinity;

public class FragmentAddDetailsUser extends Fragment {
    public static String TAG = "FragmentAddDetailsUser";
    private int REQUEST_CODE_GET_IMAGE = 1;
    private FragmentAddDetailsUserBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mData;
    private StorageReference mStore;
    private AndroidXMapFragment mapFragment;
    private Map map;
    private GPSLocation currentLocation;
    private boolean checkChoseAvatar = false;
    private double longitude, latitude;
    private Bitmap avatar;
    private String linkAvatarUser, fullName, age = "0";
    boolean male, female, teacher, student, company;

    //Details Teacher
    String email, subjectTeacher, freeTimeTeacher, vehicle = "", experient = "", tuitionTeacher = "", processWork = "";
    private boolean[] checkFreeTimeTeacher;
    ArrayList <Integer> mItemsTeacher = new ArrayList<>();

    //Details Student
    String subjectStudent, freeTimeStudent, study = "", tuitionStudent = "";
    private boolean[] checkFreeTimeStudent;
    ArrayList <Integer> mItemsStudent = new ArrayList<>();

    public static FragmentAddDetailsUser newInstance(){
        Bundle args = new Bundle();
        FragmentAddDetailsUser fragment = new FragmentAddDetailsUser();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_details_user, container, false);
        inIt();
        inItView();
        confirm();
        signOut();
        return binding.getRoot();
    }

    private void inIt(){
        currentLocation     = new GPSLocation(getContext());
        mAuth               = FirebaseAuth.getInstance();
        mData               = FirebaseDatabase.getInstance().getReference();
        mStore              = FirebaseStorage.getInstance().getReference();
        longitude           = currentLocation.getLongitude();
        latitude            = currentLocation.getLatitude();

        //check freetime
        checkFreeTimeTeacher = new boolean[Helper.LISTFREETIME.length];
        checkFreeTimeStudent = new boolean[Helper.LISTFREETIME.length];
    }

    private void inItView(){
        ApplicationContext context = new ApplicationContext(getContext());
        initMapFragment(context);
        choseAvatarAcount();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        phoneNumber = "0" + phoneNumber.substring(3);
        binding.tvNumberPhone.setText(phoneNumber);
        binding.edtAdress.setText(Helper.Adress(getContext(), currentLocation.getLongitude(), currentLocation.getLatitude()));
        Search();
        hideMap();
        inItValuesSpinner();
        chooseObject();
    }

    private void confirm(){
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.hideKeyBoard(getActivity());
                checkValues();
            }
        });
    }

    private void signOut(){
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.logout))
                        .setMessage(getResources().getString(R.string.Mess_logout))
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            }
                        }).create().show();
            }
        });
    }

    private void chooseObject(){
        binding.rbtnTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.detailsTeacher.setVisibility(View.VISIBLE);
                    binding.detailsTeacher.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    binding.detailsStudent.setVisibility(View.GONE);
                }
            }
        });
        binding.rbtnStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    binding.detailsStudent.setVisibility(View.VISIBLE);
                    binding.detailsStudent .startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                    binding.detailsTeacher.setVisibility(View.GONE);
                }
            }
        });
    }

    private void checkValues(){
        //check values user
        fullName    = binding.edtName.getText().toString();
        male        = binding.rbtnMale.isChecked();
        female      = binding.rbtnFemale.isChecked();
        teacher     = binding.rbtnTeacher.isChecked();
        student     = binding.rbtnStudent.isChecked();
        if(binding.spAge.isClickable())
            age = Helper.AGE().get(binding.spAge.getSelectedItemPosition());
        boolean ok  = binding.cbRules.isChecked();
        if(fullName.isEmpty())
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_Name), Toast.LENGTH_SHORT).show();
        else if(longitude == 0 || latitude == 0)
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_Adress), Toast.LENGTH_SHORT).show();
        else if(male == false && female == false)
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Gender), Toast.LENGTH_SHORT).show();
        else if(teacher == false && student == false && company == false)
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Object), Toast.LENGTH_SHORT).show();
        else if(ok == false)
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Rules), Toast.LENGTH_SHORT).show();
        else if(checkChoseAvatar == false)
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Avatar), Toast.LENGTH_SHORT).show();
        else if(age == "0")
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Age), Toast.LENGTH_SHORT).show();

        //check values teacher if user is teacher
        else if(teacher){
            email           = binding.edtEmail.getText().toString().trim();
            subjectTeacher  = binding.edtSubjectTeacher.getText().toString();
            freeTimeTeacher = binding.tvFreeTimeTeacher.getText().toString();
            if(binding.spExperient.isClickable()) experient = Helper.EXPERIENT().get(binding.spExperient.getSelectedItemPosition());
            if(binding.spTuitionTeacher.isClickable()) tuitionTeacher = Helper.TUITION().get(binding.spTuitionTeacher.getSelectedItemPosition());
            if(binding.spVehicle.isClickable()) vehicle = Helper.VEHICLE().get(binding.spVehicle.getSelectedItemPosition());
            processWork = binding.edtProcessWork.getText().toString();

            if(email.isEmpty())
                binding.edtEmail.setError(getContext().getResources().getString(R.string.Error_Dont_Enter_Email));
            else if(Helper.isValidEmail(email) == false)
                binding.edtEmail.setError(getContext().getResources().getString(R.string.Error_Email));
            else if(subjectTeacher.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_Subject), Toast.LENGTH_SHORT).show();
            else if(freeTimeTeacher.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_FreeTime), Toast.LENGTH_SHORT).show();
            else if(experient.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Chose_Experient), Toast.LENGTH_SHORT).show();
            else if(tuitionTeacher.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Chose_Tuition), Toast.LENGTH_SHORT).show();
            else if(vehicle.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Chose_Tuition), Toast.LENGTH_SHORT).show();
            else if(processWork.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_ProcessWork), Toast.LENGTH_SHORT).show();
            else {
                binding.Load.setVisibility(View.VISIBLE);
                upLoadAvatar();
            }
        }

        //check values student if user is student
        else if(student){
            subjectStudent  = binding.edtSubjectStudent.getText().toString();
            freeTimeStudent = binding.tvFreeTimeStudent.getText().toString();
            if(binding.spStudy.isClickable()) study = Helper.STUDY().get(binding.spStudy.getSelectedItemPosition());
            if(binding.spTuitionStudent.isClickable()) tuitionStudent = Helper.TUITION().get(binding.spTuitionStudent.getSelectedItemPosition());

            if(subjectStudent.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_Subject), Toast.LENGTH_SHORT).show();
            else if(freeTimeStudent.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Enter_FreeTime), Toast.LENGTH_SHORT).show();
            else if(study.isEmpty())
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error_Dont_Chose_Study), Toast.LENGTH_SHORT).show();
            else{
                binding.Load.setVisibility(View.VISIBLE);
                upLoadAvatar();
            }
        }

        //check value company if user is company
        else{
            binding.Load.setVisibility(View.VISIBLE);
            upLoadAvatar();
        }
    }

    private void upLoadAvatar(){
        final StorageReference mStoreAvatar = mStore.child(mAuth.getCurrentUser().getUid() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mStoreAvatar.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> download = mStoreAvatar.getDownloadUrl();
                download.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        linkAvatarUser = uri.toString();
                        if(teacher)upLoadDataUserTeacher();
                        else upLoadDataUserStudent();
                    }
                });
            }
        });
    }

    private void upLoadDataUserStudent(){
        String gender;
        if(male) gender = Helper.MALE;
        else gender = Helper.FEMALE;
        Student studentUser = new Student(linkAvatarUser, mAuth.getCurrentUser().getPhoneNumber(),
                fullName, longitude, latitude, gender, study, age, Helper.STUDENT, subjectStudent,
                freeTimeStudent, tuitionStudent, null, "offline");
        mData.child(Helper.STUDENT).child(mAuth.getCurrentUser().getUid()).setValue(studentUser);
        addForderNewStudent(studentUser);
        binding.Load.setVisibility(View.GONE);
        Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra("OBJECT", Helper.STUDENT);
        startActivity(intent);
        getActivity().finish();
    }

    private void addForderNewStudent(final Student studentUser) {
        mData = FirebaseDatabase.getInstance().getReference("NewStudents");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List < Student > newStudents = new ArrayList<>();
                newStudents.add(studentUser);
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Integer.parseInt(dataSnapshot.getKey()) < 10)
                            newStudents.add(dataSnapshot.getValue(Student.class));
                        else break;
                    }
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NewStudents");
                for(int i = 0; i < newStudents.size(); i++){
                    reference.child(String.valueOf(i)).setValue(newStudents.get(i));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void upLoadDataUserTeacher(){
        String gender;
        if(male) gender = Helper.MALE;
        else gender = Helper.FEMALE;
        Teacher teacherUser = new Teacher(linkAvatarUser, mAuth.getCurrentUser().getPhoneNumber(),
                fullName, longitude, latitude, email, gender, Helper.TEACHER, experient, vehicle, age, subjectTeacher,
                freeTimeTeacher, tuitionTeacher, processWork, null, "offline");
        mData.child(Helper.TEACHER).child(mAuth.getCurrentUser().getUid()).setValue(teacherUser);
        addForderNewTeacher(teacherUser);
        binding.Load.setVisibility(View.GONE);
        Intent intent = new Intent(getContext(), UserActivity.class);
        intent.putExtra("OBJECT", Helper.TEACHER);
        startActivity(intent);
        getActivity().finish();
    }

    private void addForderNewTeacher(final Teacher teacherUser) {
        mData = FirebaseDatabase.getInstance().getReference("NewTeachers");
        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List < Teacher > newTeachers = new ArrayList<>();
                newTeachers.add(teacherUser);
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if(Integer.parseInt(dataSnapshot.getKey()) < 10)
                            newTeachers.add(dataSnapshot.getValue(Teacher.class));
                        else break;
                    }
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("NewTeachers");
                for(int i = 0; i < newTeachers.size(); i++){
                    reference.child(String.valueOf(i)).setValue(newTeachers.get(i));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
    }

    private void inItValuesSpinner(){
        //Create values chose Exeperient of teacher
        ArrayAdapter arrayAdapterExperient = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Helper.EXPERIENT());
        arrayAdapterExperient.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spExperient.setAdapter(arrayAdapterExperient);

        //Create values chose Age of User
        ArrayAdapter arrayAdapterAge = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Helper.AGE());
        arrayAdapterAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spAge.setAdapter(arrayAdapterAge);

        //Create values chose Vehicle of Teacher
        ArrayAdapter arrayAdapterVehicle = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Helper.VEHICLE());
        arrayAdapterVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spVehicle.setAdapter(arrayAdapterVehicle);

        //Create values chose tuition of teacher and student
        ArrayAdapter arrayAdapterTuition = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Helper.TUITION());
        arrayAdapterTuition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spTuitionTeacher.setAdapter(arrayAdapterTuition);
        binding.spTuitionStudent.setAdapter(arrayAdapterTuition);

        //Create values chose Study of Student
        ArrayAdapter arrayAdapterStudy = new ArrayAdapter<>(getContext(),  android.R.layout.simple_spinner_item, Helper.STUDY());
        arrayAdapterStudy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spStudy.setAdapter(arrayAdapterStudy);

        //Create AlertDialog of values Freetime to Teacher
        binding.btnCalendarTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn Lịch Rảnh Của Bạn");
                builder.setMultiChoiceItems(Helper.LISTFREETIME, checkFreeTimeTeacher, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked){
                            if(!mItemsTeacher.contains(position)){
                                mItemsTeacher.add(position);
                            }
                            else{
                                mItemsTeacher.remove(position);
                            }
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for(int i = 0; i < mItemsTeacher.size(); i++){
                            item += Helper.LISTFREETIME[mItemsTeacher.get(i)];
                            if(i != mItemsTeacher.size() - 1){
                                item += ", ";
                            }
                        }
                        binding.tvFreeTimeTeacher.setText(item);
                    }
                });
                builder.setNegativeButton("Ẩn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Xóa Tất Cả", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkFreeTimeTeacher.length; i++){
                            checkFreeTimeTeacher[i] = false;
                            mItemsTeacher.clear();
                            binding.tvFreeTimeTeacher.setText("");
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //Create AlertDialog of values Freetime to Student
        binding.btnCalendarStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn Lịch Rảnh Của Bạn");
                builder.setMultiChoiceItems(Helper.LISTFREETIME, checkFreeTimeStudent, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if(isChecked){
                            if(!mItemsStudent.contains(position)){
                                mItemsStudent.add(position);
                            }
                            else{
                                mItemsStudent.remove(position);
                            }
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String item = "";
                        for(int i = 0; i < mItemsStudent.size(); i++){
                            item += Helper.LISTFREETIME[mItemsStudent.get(i)];
                            if(i != mItemsStudent.size() - 1){
                                item += ", ";
                            }
                        }
                        binding.tvFreeTimeStudent.setText(item);
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Delete all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i = 0; i < checkFreeTimeStudent.length; i++){
                            checkFreeTimeStudent[i] = false;
                            mItemsStudent.clear();
                            binding.tvFreeTimeStudent.setText("");
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void choseAvatarAcount(){
        binding.btnChoseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GET_IMAGE);
            }
        });
    }

    private void Search(){
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSearch.setText(getContext().getResources().getString(R.string.Search));
                longitude = 0; latitude = 0;
                binding.containerMap.setVisibility(View.VISIBLE);
                binding.containerMap.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                binding.tvNoticeChangerAdress.setVisibility(View.VISIBLE);
                binding.tvNoticeChangerAdress.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
                Helper.hideKeyBoard(getActivity());
                checkAdress();
                binding.edtAdress.setText("");
            }
        });
    }

    private void hideMap(){
        binding.btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSearch.setText(getContext().getResources().getString(R.string.Dont_This_Adress));
                binding.tvNoticeChangerAdress.setVisibility(View.GONE);
                binding.containerMap.setVisibility(View.GONE);
            }
        });
    }

    private void checkAdress(){
        String newAdress = binding.edtAdress.getText().toString();
        if(newAdress.isEmpty())
            binding.edtAdress.setError(getContext().getResources().getString(R.string.Error_Dont_Enter_Adress));
        else{
            LatLng lng= currentLocation.getLocationFromAddress(getContext(), newAdress);
            if(lng == null)
                binding.edtAdress.setError(getContext().getResources().getString(R.string.Error_Adress));
            else{
                map.removeAllMapObjects();
                MapMarker marker = new MapMarker(new GeoCoordinate(lng.latitude, lng.longitude));
                marker.setDraggable(true);
                map.addMapObject(marker);
                map.setCenter(new GeoCoordinate(lng.latitude, lng.longitude), Map.Animation.NONE);
                clickMaker();
            }
        }
    }

    private void clickMaker(){
        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
            @Override
            public void onPanStart() {}
            @Override
            public void onPanEnd() {}
            @Override
            public void onMultiFingerManipulationStart() {}
            @Override
            public void onMultiFingerManipulationEnd() { }
            @Override
            public boolean onMapObjectsSelected(@NonNull List<ViewObject> list) {
                for (ViewObject viewObject : list) {
                    if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        MapObject mapObject = (MapObject) viewObject;
                        if (mapObject.getType() == MapObject.Type.MARKER) {
                            MapMarker marker = (MapMarker) mapObject;
                            if(marker != null){
                                binding.edtAdress.setText(Helper.Adress(getContext(), marker.getCoordinate().getLongitude(), marker.getCoordinate().getLatitude()));
                                longitude = marker.getCoordinate().getLongitude();
                                latitude = marker.getCoordinate().getLatitude();
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
            public boolean onDoubleTapEvent(@NonNull PointF pointF) {return false; }
            @Override
            public void onPinchLocked() {}
            @Override
            public boolean onPinchZoomEvent(float v, @NonNull PointF pointF) {return false; }
            @Override
            public void onRotateLocked() {}
            @Override
            public boolean onRotateEvent(float v) { return false; }
            @Override
            public boolean onTiltEvent(float v) { return false; }
            @Override
            public boolean onLongPressEvent(@NonNull PointF pointF) { return false; }
            @Override
            public void onLongPressRelease() { }
            @Override
            public boolean onTwoFingerTapEvent(@NonNull PointF pointF) { return false; }
        }, 0, false);
    }

    private void initMapFragment(ApplicationContext context) {
        String diskCacheRoot = getActivity().getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot);
        if(!success)
            Toast.makeText(getActivity().getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_SHORT).show();
        else{
            mapFragment = new AndroidXMapFragment();
            getFragmentManager().beginTransaction().add(R.id.mapfragment, mapFragment, "MAP_TAG").commit();
            mapFragment.init(context, new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(
                        final OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE){
                        map = mapFragment.getMap();
                        map.setZoomLevel(17);
                        map.setTilt((map.getMinTilt() + map.getMaxTilt())/2);
                        map.setProjectionMode(Map.Projection.MERCATOR);
                        GeoCoordinate currentPosition =  new GeoCoordinate(currentLocation.getLatitude(), currentLocation.getLongitude());
                        map.setCenter(currentPosition, Map.Animation.BOW);
                        binding.btnCurrentPosition.setVisibility(View.VISIBLE);
                        clickButtonCurrentPosition();
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

    private void clickButtonCurrentPosition(){
        binding.btnCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSLocation location = new GPSLocation(getContext());
                map.setCenter(new GeoCoordinate(location.getLatitude(), location.getLongitude()), Map.Animation.BOW);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_GET_IMAGE && resultCode == RESULT_OK && data != null){
            checkChoseAvatar = true;
            binding.imgAvatarUser.setImageURI(null);
            binding.imgAvatarUser.setImageURI(data.getData());
            binding.imgAvatarUser.setDrawingCacheEnabled(true);
            binding.imgAvatarUser.buildDrawingCache();
            avatar = Helper.getResizedBitmap(((BitmapDrawable) binding.imgAvatarUser.getDrawable()).getBitmap(), 1000);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
