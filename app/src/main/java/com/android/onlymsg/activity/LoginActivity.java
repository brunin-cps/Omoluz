package com.android.onlymsg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.onlymsg.R;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Base64Converter;
import com.android.onlymsg.helper.Permission;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.User;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

public class LoginActivity extends AppCompatActivity {

//    private TextInputEditText username;
//    private TextInputEditText cellphone;
//    private TextInputEditText ddd;
//    private TextInputEditText postalCode;
//    private Button register;
//    private String[] necessaryPermissions = new String[]{
//            Manifest.permission.SEND_SMS
//    };

    private TextInputEditText email;
    private TextInputEditText password;
    private Button login;
    private User user;
    private FirebaseAuth auth;
    private String idLoogedUser;

    private ValueEventListener valueEventListener;
    private DatabaseReference dbReference;

    //private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        isLoginDone();

        email = findViewById(R.id.edit_login_email);
        password = findViewById(R.id.edit_login_password);
        login = findViewById(R.id.button_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !(email.getText().toString().isEmpty())
                        && !(password.getText().toString().isEmpty()) ){
                    user = new User();
                    user.setEmail(email.getText().toString());
                    user.setPassword(password.getText().toString());

                    validateLogin();
                } else {
                    Toast.makeText(LoginActivity.this, "Não deixe campo vazio", Toast.LENGTH_LONG).show();
                }
            }
        });

        //dbReference = ConfigFirebase.getFirebase();
        //dbReference.child("pontos").setValue("800");
    }

    public void openUserRegisterPage(View view){
        Intent intent = new Intent(LoginActivity.this,RegisterUserActivity.class);
        startActivity(intent);
    }

    private void isLoginDone(){
        auth = ConfigFirebase.getFirebaseAuth();

        if(auth.getCurrentUser() != null){
            openMainScreen();
        }
    }

    private void validateLogin(){
        auth = ConfigFirebase.getFirebaseAuth();
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    idLoogedUser = Base64Converter.CodeToBase64(user.getEmail());

                    dbReference = ConfigFirebase.getFirebase().child("users").child(idLoogedUser);

                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            User userInfo = snapshot.getValue(User.class);

                            Preferences preferences = new Preferences(LoginActivity.this);
                            preferences.SaveData(idLoogedUser,userInfo.getName());

                            Toast.makeText(LoginActivity.this,"Sucesso ao Logar.",Toast.LENGTH_LONG).show();
                            openMainScreen();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                    dbReference.addListenerForSingleValueEvent(valueEventListener);

                }else{
                    String errorException = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        errorException = "O email digitado ou senha estao incorretos, digite novamente!";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,"Erro: " + errorException,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void openMainScreen(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Not used anymore, only did this to test how can I send a SMS in java
//    private void createLoginInputMasks(){
//
//        //Define the format
//        SimpleMaskFormatter smfCel = new SimpleMaskFormatter("NNNNN-NNNN");
//        SimpleMaskFormatter smfDdd = new SimpleMaskFormatter("NN");
//        SimpleMaskFormatter smfCp = new SimpleMaskFormatter("+NN");
//
//        //Add mask to variable
//        MaskTextWatcher maskCel = new MaskTextWatcher(cellphone,smfCel);
//        MaskTextWatcher maskDdd = new MaskTextWatcher(ddd,smfDdd);
//        MaskTextWatcher maskCp = new MaskTextWatcher(postalCode,smfCp);
//
//        cellphone.addTextChangedListener(maskCel);
//        ddd.addTextChangedListener(maskDdd);
//        postalCode.addTextChangedListener(maskCp);
//
//    }
//
//    private void OnRegister(){
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String name = username.getText().toString();
//                String cel  = postalCode.getText().toString() + ddd.getText().toString() +  cellphone.getText().toString();
//
//                String formatCel = cel;
//                formatCel = formatCel.replace("+","");
//                formatCel = formatCel.replace("-","");
//
//                //Gerando Token
//                Random rand = new Random();
//                int num = rand.nextInt(8999) + 1000;
//                String token = String.valueOf(num);
//                String message = "Codigo de confirmacao OnlyMsg: " + token;
//
//                //Log.i("ex",token);
//                Preferences preferences = new Preferences(LoginActivity.this);
//                preferences.SaveUserPreferences(name,formatCel,token);
//
//
//                HashMap<String,String> user = preferences.GetUserPreferences();
//
//                //Log.i("name",user.get("name"));
//                formatCel = "5554";
//                boolean sendValidationToken = sendSMS("+" + formatCel,message);
//
////                if( sendValidationToken ){
////                    Intent intent = new Intent(LoginActivity.this,ValidatorActivity.class);
////                    startActivity(intent);
////                    finish();
////                }else{
////                    Toast.makeText(LoginActivity.this,"Problema ao enviar o SMS, tente novamente!",Toast.LENGTH_LONG).show();
////                }
//
//            }
//        });
//    }
//
//    private boolean sendSMS(String cel,String message){
//        try{
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(cel,null,message,null,null);
//            return  true;
//
//        }catch (Exception e){
//
//            e.printStackTrace();
//            return  false;
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
//
//        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        for ( int result : grantResults){
//            if(result == PackageManager.PERMISSION_DENIED){
//                alertPermissionDenied();
//            }
//        }
//
//    }
//
//    private void alertPermissionDenied(){
//
//        AlertDialog.Builder buider = new AlertDialog.Builder(this);
//        buider.setTitle("Permissoes Negadas");
//        buider.setMessage("Se nao aceitar nao vai usar vacilao.");
//
//        buider.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//
//        AlertDialog dialog = buider.create();
//        dialog.show();
//    }

}
