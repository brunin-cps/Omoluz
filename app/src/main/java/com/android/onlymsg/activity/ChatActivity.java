package com.android.onlymsg.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.onlymsg.R;
import com.android.onlymsg.adapter.MessageAdapter;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Base64Converter;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.Chat;
import com.android.onlymsg.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMessage;
    private ImageButton btSend;
    private DatabaseReference dbReference;
    private ListView listView;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;
    private ValueEventListener valueEventListenerMessage;

    //user message receiver data
    private String nameReceiver;
    private String idReceiverUser;


    //user message sender data
    private String idSenderUser;
    private String nameSenderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editMessage = findViewById(R.id.edit_message);
        btSend = findViewById(R.id.bt_send);
        listView = findViewById(R.id.lv_chat);


        //get who is logged and his data
        Preferences preferences = new Preferences(ChatActivity.this);
        idSenderUser = preferences.getUserId();
        nameSenderUser = preferences.getUserName();

        //recover data from contacts
        Bundle extra = getIntent().getExtras();
        if(extra != null){
            nameReceiver = extra.getString("name");
            String emailReceiver =  extra.getString("email");
            idReceiverUser = Base64Converter.CodeToBase64(emailReceiver);
        }

        //Configuration of chat toolbar
        toolbar = findViewById(R.id.tb_chat);
        toolbar.setTitle(nameReceiver);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_left_24);
        setSupportActionBar(toolbar);

        //Create listview messages and adapter
        messages = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this,messages);

        /** Default adapter
        adapter = new ArrayAdapter(
            ChatActivity.this, android.R.layout.simple_list_item_1,messages
        );
         */


        listView.setAdapter(adapter);

        //Get messages from firebase to show on screen
        dbReference = ConfigFirebase.getFirebase()
                                .child("messages")
                                .child(idSenderUser)
                                .child(idReceiverUser);

        //Create message listener
        valueEventListenerMessage = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();

                //write every message from 2 users
                for (DataSnapshot dados:snapshot.getChildren()){
                    Message msg = dados.getValue(Message.class);
                    messages.add(msg);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbReference.addValueEventListener(valueEventListenerMessage);

        //Send Message
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Digite uma mensagem",Toast.LENGTH_LONG).show();
                }else{
                    Message msg = new Message();
                    msg.setIdUser(idSenderUser);
                    msg.setMessage(message);

                    //Save message to sender
                    Boolean returnMessageSender = saveMessage(idSenderUser,idReceiverUser,msg);

                    if(!returnMessageSender){
                       Toast.makeText(ChatActivity.this,"Problema ao enviar mensagem, tente novamente!",Toast.LENGTH_LONG).show();
                    }else{

                        //Save message to receiver
                        Boolean returnMessageReceiver = saveMessage(idReceiverUser,idSenderUser,msg);
                        if(!returnMessageReceiver){
                            Toast.makeText(ChatActivity.this,"Problema ao enviar mensagem ao destino, tente novamente!",Toast.LENGTH_LONG).show();
                        }

                    }

                    //Create the chat view
                    Chat chat = new Chat();
                    chat.setIdUser(idReceiverUser);
                    chat.setName(nameReceiver);
                    chat.setMessage(message);

                    //save chat to sender
                    Boolean returnSavedChatSender = salveChat(idSenderUser,idReceiverUser,chat);


                    if(!returnSavedChatSender){
                        Toast.makeText(ChatActivity.this,"Problema ao criar a conversa, tente novamente!",Toast.LENGTH_LONG).show();
                    }else{

                        chat = new Chat();
                        chat.setIdUser(idSenderUser);
                        chat.setName(nameSenderUser);
                        chat.setMessage(message);

                        //save chat to receiver
                        Boolean returnSavedChatReceiver = salveChat(idReceiverUser,idSenderUser,chat);
                        if(!returnSavedChatReceiver){
                            Toast.makeText(ChatActivity.this,"Problema ao salvar a conversa para o destinatario, tente novamente!",Toast.LENGTH_LONG).show();
                        }
                    }

                    //reset text field
                    editMessage.setText("");
                }
            }
        });

    }

    private boolean salveChat(String idSender, String idReceiver, Chat chat){
        try {
            dbReference = ConfigFirebase.getFirebase().child("chat");
            dbReference.child(idSender)
                    .child(idReceiver)
                    .setValue(chat);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveMessage(String idSender,String idReceiver,Message message){
        try {
            dbReference = ConfigFirebase.getFirebase().child("messages");
            dbReference.child(idSender)
                       .child(idReceiver)
                       .push()
                       .setValue(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbReference.removeEventListener(valueEventListenerMessage);
    }
}