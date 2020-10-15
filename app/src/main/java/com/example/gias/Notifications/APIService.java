package com.example.gias.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAPMDYNLQ:APA91bHte7jG0-8-NUdWTVLqhiaDps5HjxvShliyIr8ar_1v2cKxZflU5amxB6Oxs7jBfNLpN3e4jnjlaL9eNWqWgrrS7WQc4RISN1fWdydOT5A8OUgbgaIV2Oc77Aew4EcBfZ9BZGzP"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
