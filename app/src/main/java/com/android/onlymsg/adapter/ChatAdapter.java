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
import com.android.onlymsg.model.Chat;
import com.android.onlymsg.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends ArrayAdapter<Chat> {

    private ArrayList<Chat> chats;
    private Context context;

    public ChatAdapter(@NonNull Context c, @NonNull ArrayList<Chat> objects) {
        super(c, 0, objects);
        this.chats = objects;
        this.context = c;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //check if the user has some contacts
        if (chats != null){
            //start object to create custom contacts list
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //set view from Xml
            view = inflater.inflate(R.layout.chat_list,parent, false);
            TextView chatName = view.findViewById(R.id.tv_nome_chat);
            TextView chatLastMessage = view.findViewById(R.id.tv_last_message);

            Chat chat = chats.get(position);

            chatName.setText(chat.getName());
            chatLastMessage.setText(chat.getMessage());
        }

        return view;
    };
}
