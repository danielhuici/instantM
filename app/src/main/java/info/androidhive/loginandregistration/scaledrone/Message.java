package info.androidhive.loginandregistration.scaledrone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.loginandregistration.group.Group;
/**
 * Define las propiedades de un mensaje.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class Message {
    private String text; // message body
    private MemberData user; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

    Message(String text, MemberData user, boolean belongsToCurrentUser) {
        this.text = text;
        this.user = user;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public MemberData getMemberData() {
        return user;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public static List<Message> JSONToMessages(JSONArray messagesListJSON, String currentUser) throws JSONException {
        List<Message> vMessages = new ArrayList<>();
        for (int i = 0; i< messagesListJSON.length(); i++) {
            vMessages.add(JSONToMessages(messagesListJSON.getJSONObject(i),currentUser));
        }
        return vMessages;
    }

    /**
     * Convierte un objeto json en un objeto del tipo Message.
     * @param messageListJSON json que contiene la información de un mensaje.
     * @param currentUser  id del usuario (que ejecuta la aplicación)
     * @return Objeto Message con la información del mensaje.
     * @throws JSONException
     */
    private static Message JSONToMessages(JSONObject messageListJSON, String currentUser) throws JSONException {
        JSONObject data = messageListJSON.getJSONObject("data");
        String message = data.getString("message");
        String username = data.getString("name");

        MemberData memberData = new MemberData(username);
        boolean belongsToCurrentUser = false;
        if(username.equals(currentUser)) {
            belongsToCurrentUser = true;
        }

        Message m = new Message(message, memberData, belongsToCurrentUser);
        return m;
    }

}