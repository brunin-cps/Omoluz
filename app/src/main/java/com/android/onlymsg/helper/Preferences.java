package com.android.onlymsg.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferences  {

    private Context context;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "OnlyMsg.preferences";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_ID = "idLoggedUser";
    private final String CHAVE_NAME = "nameLoggedUser";

    /**
    private final String CHAVE_CEL = "cel";
    private final String CHAVE_TOKEN = "token";
    */

    public Preferences(Context atributeContext){

        context = atributeContext;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();

    }

    public void SaveData(String idUser,String nameUser){

        editor.putString(CHAVE_ID,idUser);
        editor.putString(CHAVE_NAME,nameUser);
        editor.commit();

        /**
         editor.putString(CHAVE_CEL,cel);
         editor.putString(CHAVE_TOKEN,token);
        */
    }


    public String getUserId(){
        return preferences.getString(CHAVE_ID,null);
    }
    public String getUserName(){
        return preferences.getString(CHAVE_NAME,null);
    }

    /**public HashMap<String,String> GetUserPreferences(){

        HashMap<String,String> userData = new HashMap<>();
        userData.put(CHAVE_NOME, preferences.getString(CHAVE_NOME,null));
        userData.put(CHAVE_CEL, preferences.getString(CHAVE_CEL,null));
        userData.put(CHAVE_TOKEN, preferences.getString(CHAVE_TOKEN,null));

        return  userData;
    }*/
}
