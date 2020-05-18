package com.mshlab.firebasestorageapi;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
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
    private AdvanceLoader advanceLoader;
    private boolean allowCancel;
    private String loadingMessage;
    private String cancelMeassge;

    private FirebaseStorageAPI(final Builder builder) {
        this.visibleAcitivty = builder.visibleAcitivty;
        this.cancelMeassge = builder.cancelMeassge;
        this.errorMessage = builder.errorMessage;
        this.uploadingMessage = builder.uploadingMessage;
        this.downloadMessage = builder.downloadMessage;
        this.allowCancel = builder.allowCancel;
        this.loadingMessage = builder.loadingMessage;
        this.advanceLoader = new AdvanceLoader(visibleAcitivty, loadingMessage);
    }


    public static class Builder {
        private Activity visibleAcitivty; //require
        private String cancelMeassge = "upload canceled";
        private String errorMessage = "error occurs while uploadingMessage the file";
        private String uploadingMessage = "uploading .. ";
        private boolean allowCancel = true;
        private String downloadMessage = "downloading";
        private String loadingMessage = "loading ..";

        public Builder setVisibleAcitivty(Activity visibleAcitivty) {
            this.visibleAcitivty = visibleAcitivty;
            return this;
        }

        public Builder setCancelMessage(String cancelMeassge) {
            this.cancelMeassge = cancelMeassge;
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

        public Builder setUploadingMessage(String uploadingMeesage) {
            this.uploadingMessage = uploadingMeesage;
            return this;
        }

        public Builder setDownloadingMessage(String downloadMessage) {
            this.downloadMessage = downloadMessage;
            return this;
        }

        public Builder setLoadingMessage(String loadingMessage) {
            this.loadingMessage = loadingMessage;
            return this;
        }


        public FirebaseStorageAPI build() {
            if (visibleAcitivty == null) {
                throw new IllegalStateException("build FirebaseStorageAPI require passing visibleAcitivty");
            } else {
                return new FirebaseStorageAPI(this);
            }
        }
    }


    public void deleteFile(final StorageReference mStorageRef, OnCompleteListener<Void> onCompleteListener) {
        advanceLoader.showSimple();
        mStorageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                advanceLoader.hide();
            }
        }).addOnCompleteListener(onCompleteListener).addOnFailureListener(getFailureListener());

    }

    public void downloadAsStream(final StorageReference mStorageRef, final OnCompleteListener<StreamDownloadTask.TaskSnapshot> onCompleteListener) {
        advanceLoader.showSimple();

        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(final StorageMetadata storageMetadata) {

                mStorageRef.getStream().addOnCompleteListener(onCompleteListener).addOnProgressListener(new OnProgressListener<StreamDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull StreamDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / storageMetadata.getSizeBytes();
                        String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(storageMetadata.getSizeBytes());
                        advanceLoader.updateProgress(progress, downloadMessage, msgSize);
                    }
                });
            }
        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                advanceLoader.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnFailureListener(getFailureListener());


    }

    public void downloadAsBytes(final StorageReference mStorageRef, final long maxSizeByte, final OnCompleteListener<byte[]> onCompleteListener) {
        advanceLoader.showSimple();
        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(final StorageMetadata storageMetadata) {

                mStorageRef.getBytes(maxSizeByte).addOnCompleteListener(onCompleteListener);
            }
        }).addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                advanceLoader.hide();
            }
        }).addOnFailureListener(getFailureListener());


    }


    //download
    public void downloadToLocalPath(final StorageReference mStorageRef, final File downloadFilePath, final OnCompleteListener<FileDownloadTask.TaskSnapshot> onCompleteListener) {
        advanceLoader.show();

        mStorageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(final StorageMetadata storageMetadata) {

                mStorageRef.getFile(downloadFilePath).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / storageMetadata.getSizeBytes();
                        String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(storageMetadata.getSizeBytes());
                        advanceLoader.updateProgress(progress, downloadMessage, msgSize);
                    }
                }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        advanceLoader.hide();
                    }
                }).addOnCompleteListener(onCompleteListener).addOnFailureListener(getFailureListener());
            }
        }).addOnFailureListener(getFailureListener());


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
            DialogInterface.OnClickListener onCancelClicked = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(visibleAcitivty, cancelMeassge, Toast.LENGTH_LONG).show();
                    if (!uploadTask.isComplete()) {
                        uploadTask.cancel();
                    } else {
                        //Upload is complete, but user wanted to cancel. Let's delete the file
                        uploadTask.getSnapshot().getMetadata().getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                task.getException().printStackTrace();
                            }
                        });
                        // storageRef.delete(); // will delete all your files
                    }
                }

            };

            advanceLoader.show(onCancelClicked);
        } else {
            advanceLoader.show();
        }


        final long finalFileSize = fileSize;
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                advanceLoader.hide();
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnCompleteListener(onCompleteListener);
                } else {
                    task.getException().printStackTrace();
                    uploadTask.addOnCompleteListener(onCompleteListener);
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / finalFileSize;
                String msgSize = Helper.formatSize(taskSnapshot.getBytesTransferred()) + " of " + Helper.formatSize(finalFileSize);

                advanceLoader.updateProgress(progress, uploadingMessage, msgSize);
            }
        }).addOnFailureListener(getFailureListener());


    }

    private OnFailureListener getFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                advanceLoader.hide();
                Toast.makeText(visibleAcitivty, errorMessage, Toast.LENGTH_LONG).show();

                e.printStackTrace();
                int errorCode = ((StorageException) e).getErrorCode();
                switch (errorCode) {
                    case StorageException.ERROR_OBJECT_NOT_FOUND:
                        Toast.makeText(visibleAcitivty, "file not exist", Toast.LENGTH_LONG).show();


                    default:
                        Toast.makeText(visibleAcitivty, errorMessage, Toast.LENGTH_LONG).show();


                }
            }
        };
    }

    ;
}



