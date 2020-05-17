package com.mshlab.FirebaseStorageAPI_sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mshlab.firebasestorageapi.FirebaseStorageAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    File localFile;
    private InputStream inputStream;
    FirebaseStorageAPI firebaseStorageAPI;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        firebaseStorageAPI = new FirebaseStorageAPI.Builder()
                .setVisibleAcitivty(this)
                .allowCancel(false).build();

        try {
            inputStream = this.getAssets().open("pic.png");
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void downloadButton(View view) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot().child("sample.png");
        firebaseStorageAPI.downloadToLocalPath(mStorageRef, localFile, new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if (task.isComplete()) {
                    Log.e(TAG, "Stream Size: " + localFile.length());
                    statusTextView.setText("file downloaded successfully\n Size: " + localFile.length() + " Byte");

                }
            }
        });
    }


    public void uploadButton(View view) {
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().getRoot().child("sample.png");
        firebaseStorageAPI.upload(inputStream, mStorageRef, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            statusTextView.setText("file uploaded successfully\n File URL: " + uri.toString());


                        }
                    });

                }
            }
        });
    }
}
