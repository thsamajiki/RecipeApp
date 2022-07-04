package com.seoultech.recipeschoolproject.storage;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.seoultech.recipeschoolproject.listener.OnFileUploadListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseStorageApi {
    private static FirebaseStorageApi instance;
    private OnFileUploadListener onFileUploadListener;
    public static final String DEFAULT_IMAGE_PATH = "images/";
    public static final String PROFILE_IMAGE_PATH = "profile/";

    private FirebaseStorageApi() {
    }

    public static FirebaseStorageApi getInstance() {
        if(instance == null) {
            instance = new FirebaseStorageApi();
        }
        return instance;
    }

    public void setOnFileUploadListener(OnFileUploadListener onFileUploadListener) {
        this.onFileUploadListener = onFileUploadListener;
    }

    public void uploadImage(String folderPath, String filePath) {
        Uri file = Uri.fromFile(new File(filePath));
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderPath + file.getLastPathSegment());;
        UploadTask uploadTask = storageRef.putFile(file);
        uploadTask
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        float percent = (float) taskSnapshot.getBytesTransferred() / (float) taskSnapshot.getTotalByteCount() * 100;
                        onFileUploadListener.onFileUploadProgress(percent);
                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        onFileUploadListener.onFileUploadComplete(true, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFileUploadListener.onFileUploadComplete(false, null);
                    }
                });
    }
}
