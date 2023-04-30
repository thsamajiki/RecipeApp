package com.seoultech.recipeschoolproject.authentication;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.listener.Type;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.vo.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthentication {
    private static FirebaseAuthentication firebaseAuthentication;
    private OnCompleteListener<Void> onCompleteListener;

    private FirebaseAuthentication() {}

    public static FirebaseAuthentication getInstance() {
        if (firebaseAuthentication == null) {
            firebaseAuthentication = new FirebaseAuthentication();
        }

        return firebaseAuthentication;
    }

    public void setOnCompleteListener(OnCompleteListener<Void> onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public void signUpEmail(final Context context, final String email, final String pwd) {
        final Response<Void> response = new Response<>();
        response.setType(Type.AUTH);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        MyInfoUtil.getInstance().putEmail(context, email);
                        MyInfoUtil.getInstance().putPwd(context, pwd);
                        onCompleteListener.onComplete(true, response);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteListener.onComplete(false, response);
            }
        });
    }

    public void login(final Context context, final String email, final String pwd) {
        final Response<Void> response = new Response<>();
        response.setType(Type.AUTH);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        MyInfoUtil.getInstance().putEmail(context, email);
                        MyInfoUtil.getInstance().putPwd(context, pwd);
                        getUserInfo(context);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onCompleteListener.onComplete(false, response);
                    }
                });
    }

    public void getUserInfo(final Context context) {
        final Response<Void> response = new Response<>();
        response.setType(Type.AUTH);
        String myKey = MyInfoUtil.getInstance().getKey();
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("User")
                .document(myKey)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            MyInfoUtil.getInstance().putNickname(context, userData.getNickname());
                            MyInfoUtil.getInstance().putProfileImageUrl(context, userData.getProfileUrl());
                            onCompleteListener.onComplete(true, response);
                        } else {
                            onCompleteListener.onComplete(false, response);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onCompleteListener.onComplete(false, response);
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void autoLogin(Context context) {
        FirebaseUser firebaseUser = getCurrentUser();
        final Response<Void> response = new Response<>();
        response.setType(Type.AUTH);

        if(firebaseUser == null) {
            onCompleteListener.onComplete(false, response);
        } else {
            String email = MyInfoUtil.getInstance().getEmail(context);
            String pwd = MyInfoUtil.getInstance().getPwd(context);
            if (TextUtils.isEmpty(email)) {
                onCompleteListener.onComplete(false, response);
                return;
            }

            AuthCredential emailAuthCredential = EmailAuthProvider.getCredential(email, pwd);
            firebaseUser.reauthenticate(emailAuthCredential)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onCompleteListener.onComplete(true, response);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onCompleteListener.onComplete(false, response);
                        }
                    });
        }
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }
}