package com.example.gias.User.news;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gias.Helper;
import com.example.gias.Object.Post;
import com.example.gias.Object.Student;
import com.example.gias.Object.Teacher;
import com.example.gias.R;
import com.example.gias.User.UserActivity;
import com.example.gias.databinding.FragmentAddPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment {
    public static String TAG = "AddPostFragment";
    private FragmentAddPostBinding binding;
    private DatabaseReference mData;
    final int REQUEST_EXTERNAL_STORAGE = 100;
    private String title;
    private String description;
    private String feel;
    private String timePost;
    private Bitmap imgOne, imgTwo, imgThree, imgFour;

    private String linkAvatar, nameUser;

    public static AddPostFragment newInstance(Bundle bundle) {
        AddPostFragment fragment = new AddPostFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_post, container, false);
        Bundle bundle = getArguments();

        if(bundle != null){
            if(bundle.getSerializable(Helper.STUDENT) != null){
                Student student = (Student) bundle.getSerializable(Helper.STUDENT);
                setViewStudent(student);
            }
            else {
                Teacher teacher = (Teacher) bundle.getSerializable(Helper.TEACHER);
                setViewTeacher(teacher);
            }
        }

        choseImage();

        deleteImage();

        chooseFeeling();

        checkValueForUpload();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return binding.getRoot();
    }

    private void setViewStudent(Student student){
        binding.tvUserName.setText(student.getStudentName());
        if(getContext() != null)
            Glide.with(getContext()).load(student.getLinkAvatarStudent()).into(binding.imgAvatarUser);
    }

    private void choseImage(){
        binding.imgGallerly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
            }
        });

        binding.viewAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
            }
        });
    }

    private void deleteImage(){
        binding.btnCancleImgOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgOne.setImageURI(null);
                imgOne = null;
                binding.viewImgOne.setVisibility(View.GONE);
            }
        });
        binding.btnCancleImgTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgTwo.setImageURI(null);
                imgTwo = null;
                binding.viewImgTwo.setVisibility(View.GONE);
            }
        });
        binding.btnCancleImgThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgThree.setImageURI(null);
                imgThree = null;
                binding.viewImgThree.setVisibility(View.GONE);
            }
        });
        binding.btnCancleImgFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.imgFour.setImageURI(null);
                imgFour = null;
                binding.viewImgFour.setVisibility(View.GONE);
            }
        });
    }

    private void chooseFeeling(){
        binding.imgFeel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomSheetFeeling();
            }
        });
    }

    private void setBottomSheetFeeling(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_bottom_sheet_feeling,
                        (LinearLayout)getActivity().findViewById(R.id.bottomSheetFeeling));

        ImageButton happy           = bottomSheetView.findViewById(R.id.btnHappy);
        ImageButton cute            = bottomSheetView.findViewById(R.id.btnCute);
        ImageButton sad             = bottomSheetView.findViewById(R.id.btnSad);
        ImageButton angry           = bottomSheetView.findViewById(R.id.btnAngry);
        ImageButton question        = bottomSheetView.findViewById(R.id.btnQuestion);
        final TextView feeling      = bottomSheetView.findViewById(R.id.tvFeeling);
        final Button btnNotChoose   = bottomSheetView.findViewById(R.id.btnNotChoose);
        TextView tvTitle            = bottomSheetView.findViewById(R.id.tvTitle);

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeling.setText("Bạn đang cảm thấy vui vẻ!");
                binding.tvFeeling.setText("Đang cảm thấy vui vẻ!");
                binding.tvFeeling.setVisibility(View.VISIBLE);
                feeling.setVisibility(View.VISIBLE);
                btnNotChoose.setVisibility(View.VISIBLE);
                feel = "Happy";
            }
        });
        cute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeling.setText("Bạn đang cảm thấy đáng yêu!");
                binding.tvFeeling.setText("Đang cảm thấy đáng yêu!");
                binding.tvFeeling.setVisibility(View.VISIBLE);
                feeling.setVisibility(View.VISIBLE);
                btnNotChoose.setVisibility(View.VISIBLE);
                feel = "Cute";
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeling.setText("Bạn đang cảm thấy buồn!");
                binding.tvFeeling.setText("Đang cảm thấy buồn!");
                binding.tvFeeling.setVisibility(View.VISIBLE);
                feeling.setVisibility(View.VISIBLE);
                btnNotChoose.setVisibility(View.VISIBLE);
                feel = "Sad";
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeling.setText("Bạn đang cảm thấy tức giận!");
                binding.tvFeeling.setText("Đang cảm thấy tức giận!");
                binding.tvFeeling.setVisibility(View.VISIBLE);
                feeling.setVisibility(View.VISIBLE);
                btnNotChoose.setVisibility(View.VISIBLE);
                feel = "Angry";
            }
        });
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feeling.setText("Bạn đang cảm thấy thắc mắc!");
                binding.tvFeeling.setText("Đang cảm thấy thắc mắc!");
                binding.tvFeeling.setVisibility(View.VISIBLE);
                feeling.setVisibility(View.VISIBLE);
                btnNotChoose.setVisibility(View.VISIBLE);
                feel = "Question";
            }
        });

        btnNotChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvFeeling.setText("");
                binding.tvFeeling.setVisibility(View.GONE);
                feeling.setText("");
                feeling.setVisibility(View.GONE);
                btnNotChoose.setVisibility(View.GONE);
                feel = null;
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setViewTeacher(Teacher teacher){
        binding.tvUserName.setText(teacher.getTeacherName());
        if(getContext() != null)
            Glide.with(getContext()).load(teacher.getLinkAvatarTeacher()).into(binding.imgAvatarUser);
    }

    private void checkValueForUpload(){
        binding.tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.edtTitle.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Bạn chưa nhập chủ đề của bài chia sẻ!", Toast.LENGTH_SHORT).show();
                    binding.edtTitle.setError("Bạn chưa nhập chủ đề của bài chia sẻ!");
                }
                else if(binding.edtDescription.getText().toString().isEmpty()){
                        Toast.makeText(getContext(), "Bạn chưa nhập nội dung của bài chia sẻ!", Toast.LENGTH_SHORT).show();
                        binding.edtDescription.setError("Bạn chưa nhập nội dung của bài chia sẻ!");
                }
                else {
                    binding.Load.setVisibility(View.VISIBLE);
                    title = binding.edtTitle.getText().toString();
                    description = binding.edtDescription.getText().toString();


                    mData = FirebaseDatabase.getInstance().getReference(UserActivity.OBJECT);
                    mData.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(UserActivity.OBJECT.equals(Helper.STUDENT)) {
                                linkAvatar = "linkAvatarStudent";
                                nameUser = "studentName";
                            }
                            else {
                                linkAvatar = "linkAvatarTeacher";
                                nameUser = "teacherName";
                            }
                            String linkAvaterPoster = (String) snapshot.child(linkAvatar).getValue();
                            String username = (String) snapshot.child(nameUser).getValue();
                            timePost = String.valueOf(System.currentTimeMillis());
                            String uidPost = "post" + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + timePost;
                            Post post = new Post(uidPost, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                                    title, description, feel, null, null, null, null,
                                    timePost, "0", "0", "0", linkAvaterPoster, UserActivity.OBJECT, username);
                            mData = FirebaseDatabase.getInstance().getReference("Posts");
                            mData.child(uidPost).setValue(post);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});

                    if(imgOne != null || imgTwo != null || imgThree != null || imgFour != null){
                        if(imgOne != null){
                            upLoadImagePost(imgOne, "linkImageOne");
                        }
                        if(imgTwo != null){
                            upLoadImagePost(imgTwo, "linkImageTwo");
                        }
                        if(imgThree != null){
                            upLoadImagePost(imgThree, "linkImageThree");
                        }
                        if(imgFour != null){
                            upLoadImagePost(imgFour, "linkImageFour");
                        }
                    }
                    else{
                        binding.Load.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                    }
                }
            }
        });
    }

    private void upLoadImagePost(Bitmap imgUpload, final String name){
        final StorageReference reference = FirebaseStorage.getInstance().getReference("post" + FirebaseAuth.getInstance().getCurrentUser().getUid() + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgUpload.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.Error), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> download = reference.getDownloadUrl();
                download.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Posts");
                        mData.child("post" + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + timePost)
                                .child(name).setValue(uri.toString());
                        if(name.equals("linkImageOne"))
                            imgOne = null;
                        if (name.equals("linkImageTwo"))
                            imgTwo = null;
                        if(name.equals("linkImageThree"))
                            imgThree = null;
                        if(name.equals("linkImageFour"))
                            imgFour = null;

                        if(imgOne == null && imgTwo == null && imgThree == null && imgFour == null){
                            binding.Load.setVisibility(View.GONE);
                            if(getContext() != null ) Toast.makeText(getContext(), "Thành công!", Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK && data != null){
            binding.imgOne.setImageURI(null);
            binding.imgTwo.setImageURI(null);
            binding.imgThree.setImageURI(null);
            binding.imgFour.setImageURI(null);
            binding.viewImgOne.setVisibility(View.GONE);
            binding.viewImgTwo.setVisibility(View.GONE);
            binding.viewImgThree.setVisibility(View.GONE);
            binding.viewImgFour.setVisibility(View.GONE);
            imgOne = null;
            imgTwo = null;
            imgThree = null;
            imgFour = null;
            ClipData clipData = data.getClipData();
            if(clipData != null){
                if(clipData.getItemCount() > 4)
                    Toast.makeText(getContext(), "Bạn chỉ được chọn tối đa 4 ảnh hoặc video", Toast.LENGTH_SHORT).show();
                else{
                    int count = clipData.getItemCount();
                    if(count == 1) {
                        binding.imgOne.setImageURI(clipData.getItemAt(0).getUri());
                        binding.viewImgOne.setVisibility(View.VISIBLE);
                        binding.imgOne.setDrawingCacheEnabled(true);
                        binding.imgOne.buildDrawingCache();
                        imgOne = Helper.getResizedBitmap(((BitmapDrawable) binding.imgOne.getDrawable()).getBitmap(),1000);
                    }
                    if(count == 2){
                        binding.imgOne.setImageURI(clipData.getItemAt(0).getUri());
                        binding.imgTwo.setImageURI(clipData.getItemAt(1).getUri());
                        binding.viewImgOne.setVisibility(View.VISIBLE);
                        binding.viewImgTwo.setVisibility(View.VISIBLE);
                        binding.imgOne.setDrawingCacheEnabled(true);
                        binding.imgOne.buildDrawingCache();
                        imgOne = Helper.getResizedBitmap(((BitmapDrawable) binding.imgOne.getDrawable()).getBitmap(),1000);
                        binding.imgTwo.setDrawingCacheEnabled(true);
                        binding.imgTwo.buildDrawingCache();
                        imgTwo = Helper.getResizedBitmap(((BitmapDrawable) binding.imgTwo.getDrawable()).getBitmap(),1000);
                    }
                    if (count == 3){
                        binding.imgOne.setImageURI(clipData.getItemAt(0).getUri());
                        binding.imgTwo.setImageURI(clipData.getItemAt(1).getUri());
                        binding.imgThree.setImageURI(clipData.getItemAt(2).getUri());
                        binding.viewImgOne.setVisibility(View.VISIBLE);
                        binding.viewImgTwo.setVisibility(View.VISIBLE);
                        binding.viewImgThree.setVisibility(View.VISIBLE);

                        binding.imgOne.setDrawingCacheEnabled(true);
                        binding.imgOne.buildDrawingCache();
                        imgOne = Helper.getResizedBitmap(((BitmapDrawable) binding.imgOne.getDrawable()).getBitmap(),1000);

                        binding.imgTwo.setDrawingCacheEnabled(true);
                        binding.imgTwo.buildDrawingCache();
                        imgTwo = Helper.getResizedBitmap(((BitmapDrawable) binding.imgTwo.getDrawable()).getBitmap(),1000);

                        binding.imgThree.setDrawingCacheEnabled(true);
                        binding.imgThree.buildDrawingCache();
                        imgThree = Helper.getResizedBitmap(((BitmapDrawable) binding.imgThree.getDrawable()).getBitmap(),1000);
                    }
                    if(count == 4){
                        binding.imgOne.setImageURI(clipData.getItemAt(0).getUri());
                        binding.imgTwo.setImageURI(clipData.getItemAt(1).getUri());
                        binding.imgThree.setImageURI(clipData.getItemAt(2).getUri());
                        binding.imgFour.setImageURI(clipData.getItemAt(3).getUri());
                        binding.viewImgOne.setVisibility(View.VISIBLE);
                        binding.viewImgTwo.setVisibility(View.VISIBLE);
                        binding.viewImgThree.setVisibility(View.VISIBLE);
                        binding.viewImgFour.setVisibility(View.VISIBLE);

                        binding.imgOne.setDrawingCacheEnabled(true);
                        binding.imgOne.buildDrawingCache();
                        imgOne = Helper.getResizedBitmap(((BitmapDrawable) binding.imgOne.getDrawable()).getBitmap(),1000);

                        binding.imgTwo.setDrawingCacheEnabled(true);
                        binding.imgTwo.buildDrawingCache();
                        imgTwo = Helper.getResizedBitmap(((BitmapDrawable) binding.imgTwo.getDrawable()).getBitmap(),1000);

                        binding.imgThree.setDrawingCacheEnabled(true);
                        binding.imgThree.buildDrawingCache();
                        imgThree = Helper.getResizedBitmap(((BitmapDrawable) binding.imgThree.getDrawable()).getBitmap(),1000);

                        binding.imgFour.setDrawingCacheEnabled(true);
                        binding.imgFour.buildDrawingCache();
                        imgFour = Helper.getResizedBitmap(((BitmapDrawable) binding.imgFour.getDrawable()).getBitmap(),1000);

                    }
                }

            }
        }
    }
}