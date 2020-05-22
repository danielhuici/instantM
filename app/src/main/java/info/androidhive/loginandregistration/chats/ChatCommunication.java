package info.androidhive.loginandregistration.chats;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.contact.Contact;
import info.androidhive.loginandregistration.group.Group;
import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.utils.Tupla;

class ChatCommunication extends Observable {
    static final String GET_CHATS_OK = "GET_PRIVATE_CHATS_OK";
    static final String GET_CHATS_ERROR = "GET_PRIVATE_CHATS_ERROR";
    private static final String URL_GET_CHATS = "http://34.69.44.48/instantm/obtener_chats.php";

    void getUserChats(final int userId) {
        GetUserChats getUserPrivateChats = new GetUserChats();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_CHATS, getUserPrivateChats, getUserPrivateChats){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }
    class GetUserChats implements Response.Listener<String>, Response.ErrorListener{
        List<Chat> chats;

        GetUserChats() {
            this.chats = new ArrayList<>();
        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                if (!error) { // No hay error
                    if(!jObj.isNull("private_chats")) {
                        chats.addAll(Contact.JSONToContacts(jObj.getJSONArray("private_chats")));
                    }
                    if(!jObj.isNull("group_chats")) {
                         chats.addAll(Group.JSONToGroups(jObj.getJSONArray("group_chats")));
                    }
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CHATS_OK, chats));
                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CHATS_ERROR,"ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                e.printStackTrace();
                notifyObservers(new Tupla<>(GET_CHATS_ERROR,"JSON ERROR"));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_CHATS_ERROR," RESPONSE ERROR"));
        }
    }
}
