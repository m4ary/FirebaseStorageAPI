package com.mshlab.FirebaseStorageAPI_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mshlab.firebasestorageapi.FirebaseStorageAPI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseStorageAPI.showToast(this,"Hello World");
    }
}
