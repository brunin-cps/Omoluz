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
import com.android.onlymsg.activity.MainActivity;
import com.android.onlymsg.adapter.ContactAdapter;
import com.android.onlymsg.config.ConfigFirebase;
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ContatsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contact> contacts;
    private DatabaseReference dbReference;
    private ValueEventListener valueEventListenerContacts;

    @Override
    public void onStart() {
        super.onStart();
        dbReference.addValueEventListener(valueEventListenerContacts);
    }

    @Override
    public void onStop() {
        super.onStop();
        dbReference.removeEventListener(valueEventListenerContacts);
    }

    public ContatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contacts = new ArrayList<>();

        Preferences preferences = new Preferences(getActivity());

        //get all the contacts from logged user
        dbReference = ConfigFirebase.getFirebase();
        dbReference = dbReference.child("contacts").child(preferences.getUserId());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contats, container, false);

        listView = view.findViewById(R.id.lv_contacts);


        /** Default adapter
        adapter = new ArrayAdapter(
                                    getActivity(),
                                    R.layout.contact_list,
                                    contacts
        );*/

        adapter = new ContactAdapter(getActivity(),contacts);
        listView.setAdapter(adapter);

        //Listener to recover contacts
        valueEventListenerContacts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Clear data
                contacts.clear();

                //List contact data
                for (DataSnapshot dados: snapshot.getChildren()){
                    Contact contact = dados.getValue(Contact.class);
                    contacts.add(contact);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //Set on click on the contact list, so the user can start a chat
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                //recover data to be send
                Contact contact = contacts.get(position);


                //send data to chatActivity
                intent.putExtra("name",contact.getName());
                intent.putExtra("email",contact.getEmail());


                startActivity(intent);
            }
        });

        return view;
    }
}