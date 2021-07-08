package com.android.onlymsg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.onlymsg.R;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Base64Converter;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class RegisterUserActivity extends AppCompatActivity {

    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText password;
    private Button btRegister;
    private User user;

    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        name = findViewById(R.id.edit_register_nome);
        email= findViewById(R.id.edit_register_email);
        password = findViewById(R.id.edit_register_password);
        btRegister = findViewById(R.id.button_register);

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(     !(name.getText().toString().isEmpty())
                        && !(email.getText().toString().isEmpty())
                        && !(password.getText().toString().isEmpty())
                ){
                    user = new User();
                    user.setName(name.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setPassword(password.getText().toString());

                    RegisterUser();
                }else{
                    Toast.makeText(RegisterUserActivity.this, "Não deixe campo vazio", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void RegisterUser(){
        auth = ConfigFirebase.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(RegisterUserActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterUserActivity.this,"Sucesso ao criar a conta.",Toast.LENGTH_LONG).show();

                    /**
                    FirebaseUser createdUser = task.getResult().getUser();

                    Before the conversion to the Email, it was used the id From Auth Firebase
                    user.setId(createdUser.getUid());
                    down, we use the user email to create his id
                    */
                    String idUser = Base64Converter.CodeToBase64(user.getEmail());
                    user.setId(idUser);
                    user.save();



                    //Save data like email/name in the user preferences
                    Preferences preferences = new Preferences(RegisterUserActivity.this);
                    preferences.SaveData(idUser,user.getName());

                    openLoggedUser();

                    /**
                    auth.signOut();
                    finish();
                    */
                }else{
                    String errorException = "";
                    try {
                         throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        errorException = "Digite uma senha mais forte, contendo mais caracteres letras e numeros!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        errorException = "O email digitado é invalido, digite um email valido!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        errorException = "Email ja cadastrado!";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterUserActivity.this,"Erro: " + errorException,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void openLoggedUser(){
        Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}