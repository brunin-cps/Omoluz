package com.android.onlymsg.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.onlymsg.R;
import com.android.onlymsg.adapter.TabAdapter;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Base64Converter;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.helper.SlidingTabLayout;
import com.android.onlymsg.model.Contact;
import com.android.onlymsg.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {



    private Button signOut;
    private FirebaseAuth auth;
    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String idContact;
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListenerContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        auth = ConfigFirebase.getFirebaseAuth();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Omoluz");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.slt_tabs);
        slidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#FFFFFF"));
        slidingTabLayout.setDistributeEvenly(true);
        viewPager = findViewById(R.id.viewPager);

        //Configure Adapter

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.signout_item:
                logoutUser();
                return true;
            case R.id.friends_item:
                openRegisterContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void openRegisterContact(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //Config dialog
        alertDialog.setTitle("Novo Contato");
        alertDialog.setMessage("Email do usuario");
        alertDialog.setCancelable(false);

        EditText editTextEmail = new EditText(MainActivity.this);
        alertDialog.setView(editTextEmail);

        //Config buttons
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String emailContact = editTextEmail.getText().toString();

                //Check if input is empty
                if(emailContact.isEmpty()){
                    Toast.makeText(MainActivity.this,"Preencha o email",Toast.LENGTH_LONG).show();
                }else{

                    //Check if email is registered in Db
                    idContact = Base64Converter.CodeToBase64(emailContact);

                    //Get Firebase Instance
                    dbReference = ConfigFirebase.getFirebase();
                    dbReference = dbReference.child("users").child(idContact);

                    //Query to see if user exists
                    dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue() != null){

                                //Get the query data
                                User searchedUser = snapshot.getValue(User.class);


                                /**Below is one way to do it, but with that, you will need to keep making querys. To solve this problem
                                we will use the preferences and save the email and name on the user cellphone (we will save in user preferences)

                                auth.getCurrentUser().getEmail();
                                */


                                //Get id from logged user
                                Preferences preferences = new Preferences(MainActivity.this);
                                String idLoggedUser = preferences.getUserId();

                                dbReference = ConfigFirebase.getFirebase();

                                //Create a node contacts if doest exist
                                dbReference = dbReference.child("contacts")
                                                         .child(idLoggedUser)
                                                         .child(idContact);

                                Contact contact = new Contact();
                                contact.setEmail(searchedUser.getEmail());
                                contact.setIdUser(idContact);
                                contact.setName(searchedUser.getName());

                                //Adding values to contacts of the database
                                dbReference.setValue(contact);


                            }else{
                                Toast.makeText(MainActivity.this,"Email sem cadastro.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        dbReference.removeEventListener(valueEventListenerContacts);
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbReference = ConfigFirebase.getFirebase();
        valueEventListenerContacts = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dados: snapshot.getChildren()){
                    //String typeUser = dados.child("typeUser").getValue().toString();

                    User user = dados.getValue(User.class);

                    if (!user.getTypeUser()){
                        String idContact = Base64Converter.CodeToBase64(user.getEmail());

                        //Get id from logged user
                        Preferences preferences = new Preferences(MainActivity.this);
                        String idLoggedUser = preferences.getUserId();

                        dbReference = ConfigFirebase.getFirebase();
                        //Create a node contacts if doest exist
                        dbReference = dbReference.child("contacts")
                                .child(idLoggedUser)
                                .child(idContact);

                        Contact contact = new Contact();
                        contact.setEmail(user.getEmail());
                        contact.setIdUser(idContact);
                        contact.setName(user.getName());

                        //Adding values to contacts of the database
                        dbReference.setValue(contact);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbReference.child("users").addValueEventListener(valueEventListenerContacts);



    }

    private void logoutUser(){
        auth.signOut();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}