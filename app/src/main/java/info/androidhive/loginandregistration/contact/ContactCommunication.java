package info.androidhive.loginandregistration.contact;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import info.androidhive.loginandregistration.scaledrone.AppController;
import info.androidhive.loginandregistration.utils.Tupla;

public class ContactCommunication extends Observable {


    protected static final String CREATE_CONTACT_OK = "CREATE_CONTACT_OK";
    public static final String GET_USER_CONTACTS_ERROR = "GET_USER_CONTACTS_ERROR";
    public static final String GET_USER_CONTACTS_OK = "GET_USER_CONTACTS_OK";
    protected static final String CREATE_CONTACT_ERROR = "CREATE_CONTACT_ERROR";

    public static final String DELETE_CONTACT_OK = "DELETE_CONTACT_OK";
    public static final String DELETE_CONTACT_ERROR = "DELETE_CONTACT_ERROR";

    protected static final String GET_CONTACTS_OK = "GET_CONTACTS_OK";
    protected static final String GET_CONTACTS_ERROR = "GET_CONTACTS_ERROR";

    public static final String GET_ROOMNAME_OK = "GET_ROOMNAME_OK";
    public static final String GET_ROOMNAME_ERROR = "GET_ROOMNAME_ERROR";

    private static final String URL_ADD_CONTACT = "http://34.69.44.48/instantm/anadir_contacto.php";
    private static final String URL_GET_CONTACTS = "http://34.69.44.48/instantm/obtener_contactos.php";
    private static final String URL_GET_USER_CONTACTS = "http://34.69.44.48/instantm/obtener_contactos_usuario.php";
    private static final String URL_DELETE_CONTACT = "http://34.69.44.48/instantm/eliminar_contacto.php";
    private static final String URL_ROOM_CONTACT = "http://34.69.44.48/instantm/obtener_sala.php";


    void createContact(final int userId, final int contactId) {
        CreateContactListener listener = new CreateContactListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_ADD_CONTACT, listener,listener){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("contact_id", String.valueOf(contactId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    void getContacts(final int userId) {
        GetContactListener getContactListener = new GetContactListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {
                // Parámetros para la solicitud POST <columna_db, variable>
                Map<String, String> params = new HashMap<>();
                params.put("user_id",String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    public void getContactsFromUser(final int userId) {
        GetUserContactsListener getContactListener = new GetUserContactsListener();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_GET_USER_CONTACTS, getContactListener,getContactListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }




    class GetContactListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                if (!error) { // No hay error
                    List<Contact> contacts = Contact.JSONToContacts(jObj.getJSONArray("contacts"));
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CONTACTS_OK,contacts));
                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_CONTACTS_OK,"ERROR"));
                }
            } catch (JSONException e) {
                setChanged();
                notifyObservers(new Tupla<>(GET_CONTACTS_ERROR,"ERROR"));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_CONTACTS_ERROR,"ERROR"));
        }
    }



    class CreateContactListener implements Response.Listener<String>, Response.ErrorListener{

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");
                if (!error) {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_CONTACT_OK, null));
                } else {
                    setChanged();
                    notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(CREATE_CONTACT_ERROR,"ERROR"));
        }

    }

    class GetUserContactsListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);

                boolean error;

                error = jObj.getBoolean("error");

                if (!error) { // No hay error
                    setChanged();
                    if (!jObj.isNull("contacts"))
                            notifyObservers(new Tupla<>(GET_USER_CONTACTS_OK, Contact.JSONToContacts(jObj.getJSONArray("contacts"))));

                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "JSON ERROR"));
            }
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_USER_CONTACTS_ERROR, "RESPONSE ERROR"));
        }
    }
    public void deleteContact(final String username, final Contact contact) {
        DeleteContactsListener deleteContactListener = new DeleteContactsListener();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_DELETE_CONTACT, deleteContactListener, deleteContactListener){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("contact_name", contact.getName());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }
    class DeleteContactsListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    setChanged();
                    List<Contact> contacts = Contact.JSONToContacts(jObj.getJSONArray("contacts"));
                    setChanged();
                    notifyObservers(new Tupla<>(DELETE_CONTACT_OK, null));

                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(DELETE_CONTACT_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(DELETE_CONTACT_ERROR, "JSON ERROR"));
            }
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(DELETE_CONTACT_ERROR, "RESPONSE ERROR"));
        }
    }

    public void getContactRoom(final int userId, final int myId) {
        GetContactRoomListener getContactRoomListener = new GetContactRoomListener();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URL_ROOM_CONTACT, getContactRoomListener,getContactRoomListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user1", String.valueOf(userId));
                params.put("user2", String.valueOf(myId));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "");
    }

    class GetContactRoomListener implements Response.Listener<String>, Response.ErrorListener{
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // JSON error node?
                if (!error) { // No hay error
                    String roomName = jObj.getJSONObject("data").getString("room_name");
                    setChanged();
                    notifyObservers(new Tupla<>(GET_ROOMNAME_OK, roomName));

                } else { // Error
                    setChanged();
                    notifyObservers(new Tupla<>(GET_ROOMNAME_ERROR, "ERROR"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setChanged();
                notifyObservers(new Tupla<>(GET_ROOMNAME_ERROR, "JSON ERROR"));
            }
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            setChanged();
            notifyObservers(new Tupla<>(GET_ROOMNAME_ERROR, "RESPONSE ERROR"));
        }
    }
}
