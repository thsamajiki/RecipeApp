package com.seoultech.recipeschoolproject.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.seoultech.recipeschoolproject.listener.OnChatListChangeListener;
import com.seoultech.recipeschoolproject.listener.OnCompleteListener;
import com.seoultech.recipeschoolproject.listener.OnMessageListener;
import com.seoultech.recipeschoolproject.listener.Response;
import com.seoultech.recipeschoolproject.listener.Type;
import com.seoultech.recipeschoolproject.util.MyInfoUtil;
import com.seoultech.recipeschoolproject.vo.ChatData;
import com.seoultech.recipeschoolproject.vo.MessageData;
import com.seoultech.recipeschoolproject.vo.NoticeData;
import com.seoultech.recipeschoolproject.vo.RateData;
import com.seoultech.recipeschoolproject.vo.RecipeData;
import com.seoultech.recipeschoolproject.vo.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseData {

    private static FirebaseData firebaseData;

    private FirebaseData() {};

    public static FirebaseData getInstance() {
        if(firebaseData == null) {
            firebaseData = new FirebaseData();
        }

        return firebaseData;
    }

    public void uploadUserData(final Context context, final UserData userData, final OnCompleteListener<Void> onCompleteListener) {
        final Response<Void> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("User")
                .document(userData.getUserKey())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MyInfoUtil.getInstance().putNickname(context, userData.getNickname());
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

    public void uploadRecipeData(RecipeData recipeData, final OnCompleteListener<RecipeData> onCompleteListener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firestore.collection("RecipeData").document();
        String key = documentReference.getId();
        recipeData.setKey(key);
        final Response<RecipeData> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        response.setData(recipeData);
        documentReference.set(recipeData)
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

    public void modifyRecipeData(String recipeKey, HashMap<String, Object> editData, final OnCompleteListener<Void> onCompleteListener) {
        Response<Void> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("RecipeData")
                .document(recipeKey)
                .update(editData)
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

    public void deleteRecipeData(String recipeKey, HashMap<String, Object> editData, final OnCompleteListener<Void> onCompleteListener) {
        Response<Void> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("RecipeData")
                .document(recipeKey)
                .delete()
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

    public void downloadRecipeData(final OnCompleteListener<ArrayList<RecipeData>> onCompleteListener) {
        final Response<ArrayList<RecipeData>> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("RecipeData")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            return;
                        }
                        ArrayList<RecipeData> recipeDataArrayList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()) {
                            RecipeData recipeData = documentSnapshot.toObject(RecipeData.class);
                            recipeDataArrayList.add(recipeData);
                        }
                        response.setData(recipeDataArrayList);
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

    public void uploadRating(final RecipeData recipeData, final RateData rateData, final OnCompleteListener<RecipeData> onCompleteListener) {
        final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final Response<RecipeData> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        firestore.runTransaction(new Transaction.Function<RecipeData>() {
            @Nullable
            @Override
            public RecipeData apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference recipeRef = firestore.collection("RecipeData").document(recipeData.getKey());
                DocumentReference rateRef = recipeRef.collection("RateList").document(rateData.getUserKey());
                DocumentSnapshot rateSnapShot = transaction.get(rateRef);

                int originTotalCount = recipeData.getTotalRatingCount();
                float originRate = recipeData.getRate();
                float originSum = originTotalCount * originRate;
                int newTotalCount = originTotalCount + 1;
                if (rateSnapShot.exists()) {
                    RateData myOriginRateData = rateSnapShot.toObject(RateData.class);
                    float myOriginRate = myOriginRateData.getRate();
                    originSum = originSum - myOriginRate;
                    newTotalCount--;
                }
                float userRate = rateData.getRate();
                float newRate = (originSum + userRate) / newTotalCount;
                recipeData.setTotalRatingCount(newTotalCount);
                recipeData.setRate(newRate);
                transaction.set(rateRef, rateData);
                transaction.set(recipeRef, recipeData);

                return recipeData;
            }
        }).addOnSuccessListener(new OnSuccessListener<RecipeData>() {
            @Override
            public void onSuccess(RecipeData data) {
                response.setData(data);
                onCompleteListener.onComplete(true, response);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteListener.onComplete(false, response);
            }
        });
    }

    public void updateUserData(String userKey, HashMap<String, Object> editData, OnCompleteListener<Void> onCompleteListener) {
        Response<Void> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("User")
                .document(userKey)
                .update(editData)
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

    public ListenerRegistration getChatList(String userKey, OnChatListChangeListener onChatListChangeListener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Chat")
                .whereEqualTo("userList." + userKey, true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (queryDocumentSnapshots != null) {
                            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                                ChatData chatData = documentChange.getDocument().toObject(ChatData.class);
                                onChatListChangeListener.onChatListChange(documentChange.getType(), chatData);
                            }
                        }
                    }
                });
    }

    public void createChatRoom(final Context context,
                               final String otherUserKey,
                               final String message,
                               final OnCompleteListener<ChatData> onCompleteListener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Response<ChatData> response = new Response<>();
        response.setType(Type.FIRE_STORE);

        firestore.runTransaction(new Transaction.Function<ChatData>() {
            @Nullable
            @Override
            public ChatData apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                String myUserKey = MyInfoUtil.getInstance().getKey();
                String myProfileUrl = MyInfoUtil.getInstance().getProfileImageUrl(context);
                String myUserNickname = MyInfoUtil.getInstance().getNickname(context);
                DocumentReference userRef = firestore.collection("User").document(otherUserKey);
                UserData userData = transaction.get(userRef).toObject(UserData.class);
                if (userData == null) {
                    return null;
                }
                transaction.set(userRef, userData);
                HashMap<String, String> userProfiles = new HashMap<>();
                userProfiles.put(myUserKey, myProfileUrl);
                userProfiles.put(userData.getUserKey(), userData.getProfileUrl());
                HashMap<String ,String> userNicknames = new HashMap<>();
                userNicknames.put(myUserKey, myUserNickname);
                userNicknames.put(userData.getUserKey(), userData.getNickname());
                HashMap<String, Boolean> userList = new HashMap<>();
                userList.put(myUserKey, true);
                userList.put(userData.getUserKey(), true);
                MessageData lastMessage = new MessageData();
                lastMessage.setMessage(message);
                lastMessage.setUserKey(myUserKey);
                lastMessage.setTimestamp(Timestamp.now());
                DocumentReference chatRef = firestore.collection("Chat").document();
                ChatData chatData = new ChatData();
                chatData.setUserProfiles(userProfiles);
                chatData.setUserNicknames(userNicknames);
                chatData.setUserList(userList);
                chatData.setLastMessage(lastMessage);
                chatData.setKey(chatRef.getId());
                transaction.set(chatRef, chatData);
                DocumentReference messageRef = chatRef.collection("Messages").document();
                transaction.set(messageRef, lastMessage);

                return chatData;
            }
        }).addOnSuccessListener(new OnSuccessListener<ChatData>() {
            @Override
            public void onSuccess(ChatData chatData) {
                response.setData(chatData);
                onCompleteListener.onComplete(true, response);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onCompleteListener.onComplete(false, response);
            }
        });
    }

    public ListenerRegistration getMessageList(String chatDataKey, OnMessageListener onMessageListener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Chat")
                .document(chatDataKey)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            onMessageListener.onMessage(false, null);
                            return;
                        }
                        if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                            onMessageListener.onMessage(true, null);
                            return;
                        }
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            MessageData messageData = documentChange.getDocument().toObject(MessageData.class);
                            onMessageListener.onMessage(true, messageData);
                        }
                    }
                });

    }

    public void sendMessage(String message, ChatData chatData) {

        MessageData messageData = new MessageData();
        String myUserKey = MyInfoUtil.getInstance().getKey();
        messageData.setUserKey(myUserKey);
        messageData.setMessage(message);
        messageData.setTimestamp(Timestamp.now());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference chatRef = firestore.collection("Chat").document(chatData.getKey());
                DocumentReference messageRef = chatRef.collection("Messages").document();
                transaction.update(chatRef, "lastMessage", messageData);
                transaction.set(messageRef, messageData);
                return null;
            }
        });
    }

    public void checkExistChatData(String otherUserKey, OnCompleteListener<ChatData> onCompleteListener) {
        String myUserKey = MyInfoUtil.getInstance().getKey();
        List<String> userList = new ArrayList<>();
        userList.add(myUserKey);
        userList.add(otherUserKey);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Response<ChatData> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        firestore.collection("Chat")
                .whereEqualTo("userList." + otherUserKey, true)
                .whereEqualTo("userList." + myUserKey, true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
                            if (documentSnapshotList.size() > 0) {
                                ChatData chatData = documentSnapshotList.get(0).toObject(ChatData.class);
                                response.setData(chatData);
                            }

                        }
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

    public Task<QuerySnapshot> getNoticeList(final OnCompleteListener<List<NoticeData>> onCompleteListener) {
        final Response<List<NoticeData>> response = new Response<>();
        response.setType(Type.FIRE_STORE);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        return firestore.collection("Notice")
                .orderBy("postDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            return;
                        }
                        List<NoticeData> noticeDataArrayList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots.getDocuments()) {
                            NoticeData noticeData = documentSnapshot.toObject(NoticeData.class);
                            noticeDataArrayList.add(noticeData);
                        }
                        response.setData(noticeDataArrayList);
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