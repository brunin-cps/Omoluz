package com.android.onlymsg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.onlymsg.R;
import com.android.onlymsg.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> contacts;
    private Context context;

    public ContactAdapter(@NonNull Context c, @NonNull ArrayList<Contact> objects) {
        super(c, 0, objects);
        this.contacts = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //check if the user has some contacts
        if (contacts != null){
            //start object to create custom contacts list
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //set view from Xml
            view = inflater.inflate(R.layout.contact_list,parent, false);
            TextView contactName = view.findViewById(R.id.tv_nome);
            TextView contactEmail = view.findViewById(R.id.tv_email);

            Contact contact = contacts.get(position);

            contactEmail.setText(contact.getEmail());
            contactName.setText(contact.getName());
        }

        return view;
    };
}
