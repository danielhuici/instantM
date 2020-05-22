package info.androidhive.loginandregistration.chats;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.io.Serializable;
import java.util.Random;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.group.EditGroupActivity;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.scaledrone.MemberData;
import info.androidhive.loginandregistration.scaledrone.Message;


public class MessageActivity extends AppCompatActivity  implements RoomListener {

    private String channelID = "1NVeBVoez27uLnQ9";
    private String roomName = "observable-room"; // Nombre de la sala. Variable a cambiar
    private TextView chatName;
    private EditText messageText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra =  getIntent().getExtras();
        group = (Group) extra.getSerializable("group");
        System.out.println(group.getName() + "111111111111111111111111111111111111111111111111111111");
        setContentView(R.layout.activity_message);
        chatName = findViewById(R.id.chatName);
        chatName.setText(group.getName());
        messageText = findViewById(R.id.editText);

        MemberData data = new MemberData(getRandomName(), getRandomColor());

        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        chatName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, EditGroupActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable("group", group);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, MessageActivity.this);
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    // Successfully connected to Scaledrone room
    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    // Connecting to Scaledrone room failed
    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }


    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData data = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);
            boolean belongsToCurrentUser = receivedMessage.getClientID().equals(scaledrone.getClientID());
            final Message message = new Message(receivedMessage.getData().asText(), data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }





    private String getRandomName() {
        String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};
        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    public void sendMessage(View view) {
        String message = messageText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            messageText.getText().clear();
            Log.v("MESSAGE SENT: ", message);
        }
    }

}
