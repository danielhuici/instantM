package info.androidhive.loginandregistration.scaledrone;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import info.androidhive.loginandregistration.utils.SQLiteHandler;
import info.androidhive.loginandregistration.utils.Tupla;

public class MessageCommunication extends Observable implements RoomListener {
    Scaledrone scaledrone;
    private String channelID = "1NVeBVoez27uLnQ9";
    private String roomName = ""; // Nombre de la sala. Variable a cambiar
    public static final String RECEIVED_MESSAGE = "RECEIVED_MESSAGE";
    public static final String RECOVER_MESSAGES = "RECOVER_MESSAGES";
    private EditText currentMessage;
    private SQLiteHandler db;
    private static final String URL_SAVE_MESSAGE = "http://34.69.44.48/instantm/guardar_mensaje.php";
    private static final String URL_RECOVER_MESSAGES = "http://34.69.44.48/instantm/recuperar_mensajes.php";
    private static final String URL_SAVE_PRIVATE_MESSAGE = "http://34.69.44.48/instantm/guardar_mensaje_privado.php";
    private static final String URL_RECOVER_PRIVATE_MESSAGES = "http://34.69.44.48/instantm/recuperar_mensajes_privados.php";

    public MessageCommunication(Activity context, final String roomName, final String groupId,
                                final String receiverId) {
        db = new SQLiteHandler(context);
        this.roomName = roomName;

        if (groupId.equals("-1")) {
            recoverPrivateMessagesDb();
        } else {
            recoverMessagesGroupDb(groupId);
        }

        scaledroneConnectionManager();
    }


    private void scaledroneConnectionManager() {
        MemberData data = new MemberData(getRandomColor(), getRandomName());
        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open to room: " + roomName);
                // Since the MainActivity itself already implement RoomListener we can pass it as a target
                scaledrone.subscribe(roomName, MessageCommunication.this);
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

            // TODO: Si falla el nombre del que se muestra, quizá sea aquí el problema
            setChanged();
            notifyObservers(new Tupla<>(RECEIVED_MESSAGE, message));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        return db.getCurrentUsername();
    }

    //TODO: Corregir
    public Scaledrone getScaledrone() {
        return scaledrone;
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while (sb.length() < 7) {
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    public void setCurrentMessage(EditText message) {
        currentMessage = message;
    }

    public void sendPrivateMessage(View view, String roomName, String idReceiver) {
        String message = currentMessage.getText().toString();
        System.out.println("Guarda mi mensaje!!" +  roomName + idReceiver);
        this.roomName = roomName;
        if (message.length() > 0) {
            savePrivateMessageDb(message, idReceiver);
            sendMessage(message);
        }
    }

    private void sendMessage(String message) {
        scaledrone.publish(roomName, message);
        currentMessage.getText().clear();
    }

    public void sendGroupMessage(View view, String roomName, String groupId) {
        String message = currentMessage.getText().toString();
        this.roomName = roomName;
        System.out.println("Mensaje enviado: " + message);
        if (message.length() > 0) {
            saveMessageGroupDb(message, groupId);
            sendMessage(message);
            currentMessage.getText().clear();
        }
    }

    public void saveMessageGroupDb(final String message, final String groupId) {
        SaveMessageGroupListener saveMessageListener = new SaveMessageGroupListener();
        System.out.println("SaveMessageDB: " + message + " " + db.getCurrentID() + " " + groupId);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_SAVE_MESSAGE, saveMessageListener, saveMessageListener) {

            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", String.valueOf(db.getCurrentID()));
                params.put("message", message);
                params.put("id_group", groupId);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class SaveMessageGroupListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) { }

        @Override
        public void onErrorResponse(VolleyError error) { }
    }

    public void savePrivateMessageDb(final String message, final String idReceiver) {
        SavePrivateMessageListener saveMessageListener = new SavePrivateMessageListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_SAVE_PRIVATE_MESSAGE, saveMessageListener, saveMessageListener) {

            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_user", String.valueOf(db.getCurrentID()));
                params.put("id_receiver", idReceiver);
                params.put("message", message);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class SavePrivateMessageListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) { }

        @Override
        public void onErrorResponse(VolleyError error) { }
    }

    public void recoverMessagesGroupDb(final String groupId) {
        RecoverGroupMessagesListener recoverGroupMessagesListener = new RecoverGroupMessagesListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_RECOVER_MESSAGES, recoverGroupMessagesListener, recoverGroupMessagesListener) {
            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_group", groupId);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class RecoverGroupMessagesListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {

                    List<Message> messages = Message.JSONToMessages(jObj.getJSONArray("messages"), db.getCurrentUsername());
                    setChanged();
                    notifyObservers(new Tupla<>(RECOVER_MESSAGES, messages));

                } else {
                    setChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
            }

    }
        @Override
        public void onErrorResponse(VolleyError error) { }
    }



    private void recoverPrivateMessagesDb() {
        RecoverPrivateMessagesListener recoverPrivateMessagesDb = new RecoverPrivateMessagesListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_RECOVER_PRIVATE_MESSAGES, recoverPrivateMessagesDb, recoverPrivateMessagesDb) {
            protected Map<String, String> getParams() {
                // Parámetros para la consulta POST <columna_db, variables>
                Map<String, String> params = new HashMap<String, String>();
                params.put("room_name", roomName.substring(11));
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class RecoverPrivateMessagesListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    System.out.println(jObj.getJSONArray("messages"));
                    List<Message> messages = Message.JSONToMessages(jObj.getJSONArray("messages"), db.getCurrentUsername());
                    setChanged();
                    notifyObservers(new Tupla<>(RECOVER_MESSAGES, messages));
                } else {
                    setChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
            }

        }
        @Override
        public void onErrorResponse(VolleyError error) { }
    }


}
