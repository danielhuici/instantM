package info.androidhive.loginandregistration.chats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.contact.ContactCommunication;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.scaledrone.Message;
import info.androidhive.loginandregistration.scaledrone.MessageAdapter;
import info.androidhive.loginandregistration.scaledrone.MessageCommunication;
import info.androidhive.loginandregistration.utils.Tupla;


public class MessageActivity extends AppCompatActivity implements Observer {
    private String channelID = "1NVeBVoez27uLnQ9";
    private String roomName = "observable-"; // Nombre de la sala. Variable a cambiar
    private String groupId;
    private String receiverId;
    private EditText messageText;
    private MessageCommunication messageCommunication;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent messageIntent = getIntent();

        groupId = messageIntent.getStringExtra("groupId");
        System.out.println("GroupID: " + groupId);
        if(groupId.equals("-1")) {
            System.out.println("ENTRO!");
            roomName += messageIntent.getStringExtra("roomName");
            receiverId = messageIntent.getStringExtra("receiverId");
            messageCommunication = new MessageCommunication(this, roomName, "-1", receiverId);

        } else {
            roomName += messageIntent.getStringExtra("groupName");
            messageCommunication = new MessageCommunication(this, roomName, groupId, "-1");
        }

        messageText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        messageCommunication.addObserver(this);
    }

    private void addMessageBubble(Message message) {
        messageAdapter.add(message);
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    @Override
    public void update(Observable observable, Object o) {
        final Tupla<String, Object> tupla = (Tupla<String, Object>) o;

        switch (tupla.a){
            case MessageCommunication.RECEIVED_MESSAGE:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addMessageBubble((Message) tupla.b);
                    }
                });
                break;

            case MessageCommunication.RECOVER_MESSAGES:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showOldMessages((List<Message>) tupla.b);
                    }
                });
                break;

            case ContactCommunication.GET_ROOMNAME_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showOldMessages((List<Message>) tupla.b);
                    }
                });
                break;
        }
    }

    private void showOldMessages(List<Message> messageList) {
        for (Message m : messageList) {
            addMessageBubble(m);
        }
    }

    public void sendMessage(View view) {
        messageText = (EditText) findViewById(R.id.editText);
        messageCommunication.setCurrentMessage(messageText);
        if(groupId.equals("-1")) {
            messageCommunication.sendPrivateMessage(view, roomName, receiverId);
        } else {
            messageCommunication.sendGroupMessage(view, roomName, groupId);
        }

    }
}
