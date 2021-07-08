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
import com.android.onlymsg.helper.Preferences;
import com.android.onlymsg.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private ArrayList<Message> messages;

    public MessageAdapter(@NonNull Context c, @NonNull ArrayList<Message> objects) {
        super(c, 0, objects);
        this.context = c;
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //Check if there is messages
        if (messages != null){

            //get whos is the sender
            Preferences preferences = new Preferences(context);
            String senderUser = preferences.getUserId();

            //Start Object to create message layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Recover message
            Message msg = messages.get(position);

            if (senderUser.equals(msg.getIdUser())){
                //Mount View from xml
                view = inflater.inflate(R.layout.item_message_right,parent,false);
            }else{
                view = inflater.inflate(R.layout.item_message_left,parent,false);
            }

            //Recover xml to show
            TextView txtMsg = view.findViewById(R.id.tv_message);
            txtMsg.setText(msg.getMessage());

        }

        return view;
    }
}
