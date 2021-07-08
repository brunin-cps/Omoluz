package com.android.onlymsg.helper;


import android.util.Base64;

public class Base64Converter {
    public static String CodeToBase64(String txt){
        return Base64.encodeToString(txt.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");

    }

    public static String UncodeFromBase64(String txt){
        return new String(Base64.decode(txt, Base64.DEFAULT));
    }

}
