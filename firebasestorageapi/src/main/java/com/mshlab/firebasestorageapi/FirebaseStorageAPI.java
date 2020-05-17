package com.mshlab.firebasestorageapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FirebaseStorageAPI {

    private static final int INPUT_STREAM = 1;
    private static final int FILE = 2;
    private static final int ARRAY_BYTES = 3;
    private static final String TAG = "FirebaseStorageAPI";
    private final String downloadMessage;
    private Activity visibleAcitivty; //require
    private String completeMessage;
    private String errorMessage;
    private String uploadingMessage;
    private ProgressDialog uploadDialog;
    private boolean allowCancel;

    private FirebaseStorageAPI(final Builder builder) {
        this.visibleAcitivty = builder.visibleAcitivty;
        this.completeMessage = builder.completeMessage;
        this.errorMessage = builder.errorMessage;
        this.uploadingMessage = builder.uploadingMeesage;
        this.downloadMessage = builder.downloadMessage;
        this.uploadDialog = new ProgressDialog(builder.visibleAcitivty);
        this.allowCancel = builder.allowCancel;
    }

    public static class Builder {
        private Activity visibleAcitivty; //require
        private String completeMessage = "upload completed successfully ";
        private String errorMessage = "error occurs while uploadingMessage the file";
        private String uploadingMeesage = "uploading .. ";
        private boolean allowCancel = true;
        private String downloadMessage = "downloading";

        public Builder setVisibleAcitivty(Activity visibleAcitivty) {
            this.visibleAcitivty = visibleAcitivty;
            return this;
        }

        public Builder setCompleteMessage(String completeMessage) {
            this.completeMessage = completeMessage;
            return this;
        }

        public Builder allowCancel(boolean allowCancel) {
            this.allowCancel = allowCancel;
            return this;
        }

        public Builder setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder setuploadingMessage(String uploadingMeesage) {
            this.uploadingMeesage = uploadingMeesage;
            return this;
        }

        public Builder setDownloadingMessage(String downloadMessage) {
            this.downloadMessage = downloadMessage;
            return this;
        }


        public FirebaseStorageAPI build() {
            return new FirebaseStorageAPI(this);
        }
    }



    public void downloadAsStream(final StorageReference mStorageRef, final OnCompleteListener<StreamDownloadTask.TaskSnapshot> onCompleteListener) {
        showProgress(downloadMessage);

        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(final StorageMetadata storageMetadata) {

                mStorageRef.getStream().addOnCompleteListener(onCompleteListener).addOnProgressListener(new OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull StreamDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / storageMetadata.getSizeBytes();
                        String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(storageMetadata.getSizeBytes());
                        updateProgress(progress, downloadMessage, msgSize);
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                hideProgress();
            }
        });


    }


    //download
    public void downloadToLocalPath(final StorageReference mStorageRef, final File downloadFilePath, final OnCompleteListener<FileDownloadTask.TaskSnapshot> onCompleteListener) {
        showProgress(downloadMessage);

        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(final StorageMetadata storageMetadata) {

                mStorageRef.getFile(downloadFilePath).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / storageMetadata.getSizeBytes();
                        String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(storageMetadata.getSizeBytes());
                        updateProgress(progress, downloadMessage, msgSize);
                    }
                }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        hideProgress();
                    }
                }).addOnCompleteListener(onCompleteListener);
            }
        });


    }


    public void upload(final InputStream dataToUpload, StorageReference mStorageRef, OnCompleteListener onSuccessListener) {
        upload(dataToUpload, mStorageRef, onSuccessListener, INPUT_STREAM);
    }

    public void upload(final byte[] dataToUpload, StorageReference mStorageRef, OnCompleteListener onSuccessListener) {
        upload(dataToUpload, mStorageRef, onSuccessListener, ARRAY_BYTES);
    }

    public void upload(final Uri dataToUpload, StorageReference mStorageRef, OnCompleteListener onSuccessListener) {
        upload(dataToUpload, mStorageRef, onSuccessListener, FILE);
    }

    private void upload(@NonNull Object objectData, final StorageReference storageReference, @NonNull final OnCompleteListener onCompleteListener, int dataType) {
        long fileSize = 0;
        final UploadTask uploadTask;

        switch (dataType) {
            case INPUT_STREAM:
                uploadTask = storageReference.putStream((InputStream) objectData);
                try {
                    fileSize = ((InputStream) objectData).available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case FILE:
                uploadTask = storageReference.putFile((Uri) objectData);
                fileSize = Helper.uriFileSize((Uri) objectData, visibleAcitivty);
                break;
            case ARRAY_BYTES:
                uploadTask = storageReference.putBytes((byte[]) objectData);
                fileSize = ((byte[]) objectData).length;
                break;

            default:
                throw new IllegalStateException("Unexpected objectData value: " + dataType);
        }

        if (allowCancel) {
            addCancelButtonProgress(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!uploadTask.isComplete()) {
                        //Upload is not complete yet, let's cancel
                        uploadTask.cancel();
                    } else if (uploadTask.isSuccessful()) {
                        //Upload is complete, but user wanted to cancel. Let's delete the file
                        uploadTask.getSnapshot().getMetadata().getReference().delete();
                        // storageRef.delete(); // will delete all your files
                    }
                }
            });
        }

        showProgress(uploadingMessage);


        final long finalFileSize = fileSize;
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                hideProgress();
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnCompleteListener(onCompleteListener);
                } else {
                    uploadTask.addOnCompleteListener(onCompleteListener);
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / finalFileSize;
                String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(finalFileSize);

                updateProgress(progress, uploadingMessage, msgSize);
            }
        });


    }


    private void addCancelButtonProgress(DialogInterface.OnClickListener onClickListener) {
        uploadDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", onClickListener);
    }

    public void updateProgress(double val, String title, String msg) {
        uploadDialog.setTitle(title);
        uploadDialog.setMessage(msg);
        uploadDialog.setProgress((int) val);
    }

    public void showProgress(String str) {
        try {
            uploadDialog.setCancelable(false);
            uploadDialog.setTitle("Please wait ...");
            uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            uploadDialog.setMax(100); // Progress Dialog Max Value
            uploadDialog.setMessage(str);
            if (uploadDialog.isShowing())
                uploadDialog.dismiss();
            uploadDialog.show();
        } catch (Exception e) {

        }
    }

    public void hideProgress() {
        try {
            if (uploadDialog.isShowing())
                uploadDialog.dismiss();
        } catch (Exception e) {

        }
    }
}
