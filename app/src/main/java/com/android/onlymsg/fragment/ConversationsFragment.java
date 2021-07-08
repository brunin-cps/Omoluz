package com.android.onlymsg.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.onlymsg.R;
import com.android.onlymsg.activity.ChatActivity;
import com.android.onlymsg.adapter.ChatAdapter;
import com.android.onlymsg.adapter.ContactAdapter;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Base64Converter;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.Chat;
import com.android.onlymsg.model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Chat> adapter;
    private ArrayList<Chat> chats;
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListenerChats;


    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        dbReference.addValueEventListener(valueEventListenerChats);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbReference.removeEventListener(valueEventListenerChats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chats = new ArrayList<>();

        Preferences preferences = new Preferences(getActivity());

        //get all the chats from logged user
        dbReference = ConfigFirebase.getFirebase();
        dbReference = dbReference.child("chat").child(preferences.getUserId());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);


        listView = view.findViewById(R.id.lv_chat);


        /** Default adapter
         adapter = new ArrayAdapter(
         getActivity(),
         R.layout.contact_list,
         contacts
         );*/

        adapter = new ChatAdapter(getActivity(),chats);
        listView.setAdapter(adapter);

        //Listener to recover chat
        valueEventListenerChats = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Clear data
                chats.clear();

                //List chat data
                for (DataSnapshot dados: snapshot.getChildren()){
                    Chat chat = dados.getValue(Chat.class);
                    chats.add(chat);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Set on click on the chat list, so the user can restart a chat
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //recover data to be send
                Chat chat = chats.get(position);


                //send data to chatActivity
                intent.putExtra("name",chat.getName());
                String emailUser = Base64Converter.UncodeFromBase64(chat.getIdUser());
                intent.putExtra("email",emailUser);


                startActivity(intent);
            }
        });

        return view;
    }
}