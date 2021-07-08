package com.android.onlymsg.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfigFirebase {

    private static DatabaseReference dbReference;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebase(){

        if (dbReference == null){
            dbReference = FirebaseDatabase.getInstance().getReference();
        }

        return dbReference;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
