package com.example.gias;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Helper {
    public static String TEACHER        = "Teacher";
    public static String TEACHER_TV     = "Gia Sư";
    public static String STUDENT        = "Student";
    public static String STUDENT_TV     = "Học Sinh";
    public static String MALE           = "Nam";
    public static String FEMALE         = "Nữ";
    public static String VIETNAM        = "+84";
    private Context context;

    //url image in App
    public static String IMG_REPAIR = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/change_information.png?alt=media&token=1dc6e475-a8a8-4ff2-b53c-260b1cf8bf53";
    public static String IMG_CONNECT = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/conect_user.png?alt=media&token=3eb79ede-4b33-4fb7-9390-881e576cc58b";
    public static String IMG_SECURITY = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/security.png?alt=media&token=1bd6e868-c252-4628-952b-15ce3a47e359";
    public static String IMG_INVITE = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/invite_friend.png?alt=media&token=4be05433-45de-4bc6-8d1e-34b5e80f8d55";
    public static String IMG_CHANGE_LANGUAGE = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/language.png?alt=media&token=ff93d1ad-3875-4140-b090-6b1eb053d0c6";
    public static String IMG_SIGNOUT = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/out.png?alt=media&token=e261fb27-c9a9-451b-be1e-bfbf85e5ba86";
    public static String IMG_POSITION_AD = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/position.png?alt=media&token=74398d78-9d72-4a5f-933d-9ae2be54de40";
    public static String IMG_FEEDBACK = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/feedback.png?alt=media&token=4ece54ae-6559-4243-a488-3500dbca35ee";
    public static String IMG_CONTACT_AD = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/contact.png?alt=media&token=dabd84e2-fd88-498f-a3c2-c3fe24e40340";
    public static String IMG_QA_AD = "https://firebasestorage.googleapis.com/v0/b/vt-education-e691f.appspot.com/o/qa.png?alt=media&token=1f01624d-3918-4a2d-8201-1d319789c3df";

    public static SpannableString setUnderlineString(String s){
        SpannableString content = new SpannableString(s);
        content.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return content;
    }

    public Helper(Context context) {
        this.context = context;
    }

    public static ArrayList <String> EXPERIENT(){
        ArrayList < String > arrayList = new ArrayList<String>();
        arrayList.add("Ít Hơn 1 Năm");
        arrayList.add("1 Năm");
        arrayList.add("2 Năm");
        arrayList.add("Nhiều Hơn 2 Năm");
        return arrayList;
    }

    public static ArrayList <String> AGE(){
        ArrayList <String> strings = new ArrayList<String>();
        strings.add("6");strings.add("7");strings.add("8");strings.add("9");strings.add("10");strings.add("11");strings.add("12");strings.add("13");
        strings.add("14");strings.add("15");strings.add("16");strings.add("17");strings.add("18");
        strings.add("19");
        strings.add("20");
        strings.add("21");
        strings.add("22");
        strings.add("23");
        strings.add("24");
        strings.add("25");
        strings.add("26");
        strings.add("27");
        strings.add("28");
        strings.add("29");
        strings.add("30");
        strings.add("31");
        strings.add("32");
        strings.add("33");
        strings.add("34");
        strings.add("35");
        strings.add("36");
        strings.add("37");
        strings.add("38");
        strings.add("39");
        strings.add("40");
        return strings;
    }

    public static ArrayList <String> TUITION(){
        ArrayList < String > arrayList = new ArrayList<String>();
        arrayList.add("50,000");
        arrayList.add("60,000");
        arrayList.add("70,000");
        arrayList.add("80,000");
        arrayList.add("90,000");
        arrayList.add("100,000");
        arrayList.add("110,000");
        arrayList.add("120,000");
        arrayList.add("130,000");
        arrayList.add("140,000");
        arrayList.add("150,000");
        arrayList.add("160,000");
        arrayList.add("170,000");
        arrayList.add("180,000");
        arrayList.add("190,000");
        arrayList.add("200,000");
        return arrayList;
    }

    public static ArrayList <String> STUDY(){
        ArrayList < String > arrayList = new ArrayList<String>();
        arrayList.add("Trung Bình");
        arrayList.add("Khá");
        arrayList.add("Giỏi");
        return arrayList;
    }

    public static ArrayList <String> VEHICLE(){
        ArrayList <String> strings = new ArrayList<>();
        strings.add("Xe Máy");
        strings.add("Xe Bus");
        strings.add("Xe Đạp");
        strings.add("Phương Tiện Khác");
        return strings;
    }

    public static String [] LISTFREETIME = {
            "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ Nhật"
    };

    public static void hideKeyBoard(Activity activity) {
        View view1 = activity.getCurrentFocus();
        if(view1 != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }

    public static String Adress(Context context, double longitude, double latitude){
        Geocoder geocoder;
        String address = null;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return address;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Bitmap createAvatarUser(Bitmap bitmap) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(dp(62), dp(76), Bitmap.Config.ARGB_8888);
            result.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(result);
            Drawable drawable = context.getResources().getDrawable(R.drawable.boder_user_marker);
            drawable.setBounds(0, 0, dp(62), dp(76));
            drawable.draw(canvas);

            Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            RectF bitmapRect = new RectF();
            canvas.save();
            if (bitmap != null) {
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                Matrix matrix = new Matrix();
                float scale = dp(52) / (float) bitmap.getWidth();
                matrix.postTranslate(dp(5), dp(5));
                matrix.postScale(scale, scale);
                roundPaint.setShader(shader);
                shader.setLocalMatrix(matrix);
                bitmapRect.set(dp(5), dp(5), dp(52 + 5), dp(52 + 5));
                canvas.drawRoundRect(bitmapRect, dp(26), dp(26), roundPaint);
            }
            canvas.restore();
            try {
                canvas.setBitmap(null);
            } catch (Exception e) {}
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(context.getResources().getDisplayMetrics().density * value);
    }

}
