package com.jtbdefense.atak.mandown.services;

import static java.lang.String.*;
import static java.util.Collections.singletonList;

import android.content.Intent;
import android.util.Log;

import com.atakmap.android.chat.ChatManagerMapComponent;
import com.atakmap.android.contact.Contact;
import com.atakmap.android.ipc.AtakBroadcast;

import java.util.List;

public class ChatService {

    private static final String TAG = "ManDownChatService";

    public static void sendTAKChatMessage(String message) {
        List<Contact> contactList = singletonList(ChatManagerMapComponent.getChatBroadcastContact());

        ChatManagerMapComponent
                .getInstance()
                .sendMessage(message, contactList);

        Log.d(TAG, format("Message %s has been sent", message));

        AtakBroadcast
                .getInstance()
                .sendBroadcast(new Intent("com.atakmap.android.chat.HISTORY_UPDATE"));
    }
}
